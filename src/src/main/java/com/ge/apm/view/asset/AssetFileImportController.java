/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetFileExportService;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.AssetFileImportStreamingService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetFileImportController {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportController.class);

    private UploadedFile file;

    private List<Map<String, Object>> importOrgMap;
    private List<Map<String, Object>> importAssetMap;

    private AssetFileExportService exportService;

    private AssetFileImportStreamingService streamingService;

    private UaaService uaaService;

    private OrgInfoRepository orgDao;

    private Boolean isImported = false;
    private Boolean hasExportData = false;
    
    private Boolean canbeImported = false;
    private Boolean autoGenerateQrcode = true;
    private Boolean isGeneratePmOrder;

    private StreamedContent exportFile;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private boolean displayErrorsOnly;

    @PostConstruct
    protected void init() {
        exportService = WebUtil.getBean(AssetFileExportService.class);
        streamingService = WebUtil.getBean(AssetFileImportStreamingService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);

        initOrgTree();

    }

    public void uploadDataFile(FileUploadEvent event) {

        if (selectedOrg == null) {
            WebUtil.addErrorMessage("请先选择所属院区！");
            return;
        }

        this.file = event.getFile();


        try (ExcelDocumentStreamingReader assetDoc = new ExcelDocumentStreamingReader(file.getInputstream(), 0, AssetFileImportService.EXCEL_ASSET_HEADER_ROW, 2, 3);
             ExcelDocumentStreamingReader orgDoc = new ExcelDocumentStreamingReader(file.getInputstream(), 1, AssetFileImportService.EXCEL_ORG_HEADER_ROW, null, null)
        ) {

            if(!streamingService.checkDocVersion(assetDoc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--模版版本不正确");
                return;
            }

            importAssetMap = streamingService.getAssetMapFromTemplate(assetDoc);

            if(importAssetMap.size() > 20000){
                WebUtil.addErrorMessage("导入Excel中资产行数最多20000条，请分批导入！");
                return;
            }

            importOrgMap = streamingService.getOrgInfoMapFromTemplate(orgDoc);

            
            canbeImported = streamingService.checkData(importOrgMap, importAssetMap);

            filterErrorRecords();

        } catch (IOException e) {
            WebUtil.addErrorMessage("File can not be read!", null);
            logger.error(e.getMessage(),e);

        } catch (Throwable e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage(), null);
            logger.error(e.getMessage(),e);
        }
    }

    public void importData() {
        try {
            streamingService.importData(importOrgMap, importAssetMap, autoGenerateQrcode,isGeneratePmOrder);
            isImported = true;

            filterErrorRecords();

//            downloadImportFailureData();
        } catch (Throwable e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);

            isImported = true; //disable import button
        }
    }

    public void downloadImportFailureData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            hasExportData = exportService.exportFailData(doc, importOrgMap, importAssetMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "failedData.xlsx");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void cancel() {
        this.file = null;

    }
    
    public Integer getAssetErrorCounts(){
        if(null!=importAssetMap && !importAssetMap.isEmpty()){
           return Integer.parseInt(String.valueOf(importAssetMap.stream().filter(item -> item.get("status").equals(AssetFileImportService.ImportStatus.CheckFail)).count()));
        }else{
            return 0;
        }
    }
    
    public List<String> getAssetErrorList(){
        if(null == importAssetMap) {
            return null;
        }
        List<String> errors = importAssetMap.stream().filter(item -> item.get("status").equals(AssetFileImportService.ImportStatus.CheckFail))
                .map(item -> item.get("errmsg").toString()).collect(Collectors.toList());
        return errors;
    }
    
//    public Integer getOrgErrorCounts(){
//        if(null!=importOrgMap &&!importOrgMap.isEmpty()){
//           return Integer.parseInt(String.valueOf(importOrgMap.entrySet().stream().filter(item -> item.getValue().get("status").equals(AssetFileImportService.ImportStatus.CheckFail)).count()));
//        }else{
//            return 0;
//        }
//    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);

    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();

        if (orgInfo == null || orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = orgInfo;
        }

        if (selectedOrg != null) {
//            aiService.setSelectedOrgToImport(selectedOrg);
            streamingService.setSelectedOrgToImport(selectedOrg);
        }

    }

    public void switchAssetCheckStatus(AjaxBehaviorEvent event) {

        filterErrorRecords();
    }

    public void filterErrorRecords() {

        if (!displayErrorsOnly) return;

        if (importAssetMap == null) return;

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importAssetMap) {

            AssetFileImportService.ImportStatus importStatus = (AssetFileImportService.ImportStatus) map.get("status");
            if (importStatus == null) continue;

            if (importStatus == AssetFileImportService.ImportStatus.CheckFail || importStatus == AssetFileImportService.ImportStatus.Failure) {
                result.add(map);
            }

        }

        displayAssets = result;

        if (importOrgMap == null) return;

        List<Map<String, Object>> resultMap = new ArrayList<>();

        for (Map<String, Object> map: importOrgMap) {

            AssetFileImportService.ImportStatus importStatus = (AssetFileImportService.ImportStatus) map.get("status");
            if (importStatus == null) continue;

            if (importStatus == AssetFileImportService.ImportStatus.CheckFail || importStatus == AssetFileImportService.ImportStatus.Failure) {
                resultMap.add(map);
            }

        }

        displayOrgs = resultMap;
    }

    List<Map<String, Object>> displayAssets;

    public List<Map<String, Object>> getDisplayAssets() {

        if (displayErrorsOnly) {
            return displayAssets;
        } else {
            return importAssetMap;
        }

    }

    List<Map<String, Object>> displayOrgs;

    public List<Map<String, Object>> getDisplayOrgs() {

        if (displayErrorsOnly) {
            return displayOrgs;
        } else {
            return importOrgMap;
        }

    }

    public List<Map<String, Object>> getImportOrgMap() {
        return importOrgMap;
    }

    public List<Map<String, Object>> getImportAssetMap() {
        return importAssetMap;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public Boolean getHasExportData() {
        return hasExportData;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public Boolean getCanbeImported() {
        return canbeImported;
    }

    public Boolean getAutoGenerateQrcode() {
        return autoGenerateQrcode;
    }

    public void setAutoGenerateQrcode(Boolean autoGenerateQrcode) {
        this.autoGenerateQrcode = autoGenerateQrcode;
    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public boolean isDisplayErrorsOnly() {
        return displayErrorsOnly;
    }

    public void setDisplayErrorsOnly(boolean displayErrorsOnly) {
        this.displayErrorsOnly = displayErrorsOnly;
    }

    public boolean isEmpty() {
        return (importAssetMap == null || importAssetMap.isEmpty());
    }

    public String getSelectedOrgName() {
        if (selectedOrg == null) {
            return "";
        } else {
            return selectedOrg.getName();
        }
    }
}
