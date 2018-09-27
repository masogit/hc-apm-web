package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.ContractImportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.*;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2018/8/20.
 */
@ManagedBean
@ViewScoped
public class ContractImportController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContractImportController.class);

    private UploadedFile file;

    private List<Map<String, Object>> importContractMap;

    private UaaService uaaService;

    private ContractImportService contractImportService;

    private StreamedContent exportFile;

    private String contractType;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    List<Map<String, Object>> errorContractList;

    @PostConstruct
    protected void init() {
        contractImportService = WebUtil.getBean(ContractImportService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        initOrgTree();

    }

    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            //检查导入模板版本
            /*if(!contractImportService.checkDocVersion(doc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--模版版本不正确");
                return;
            }*/

            int maxRowNum = ContractImportService.EXCEL_CONTRACT_HEADER_ROW + 5000;
            if(doc.exceedMaxRowNum(ContractImportService.SHEET_SERVICE_CONTRACT, maxRowNum)){
                WebUtil.addErrorMessage("导入Excel中数据行数最多5000条，请分批导入！");
                return;
            }

            importContractMap = contractImportService.getContractMapFromTemplate(doc, contractType, selectedOrg);

            contractImportService.importData(importContractMap);

            errorContractList = contractImportService.filterErrorRecords(importContractMap);

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    public void downloadImportFailureData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/contract/contractImport/APM_Contract_Service_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean hasFailData = contractImportService.exportFailData(doc, errorContractList);
            if(hasFailData != null && hasFailData){
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "contractFailedData.xlsx");
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);

    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Map<String, Object>> getImportContractMap() {
        return importContractMap;
    }

    public void setImportContractMap(List<Map<String, Object>> importContractMap) {
        this.importContractMap = importContractMap;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
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

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<Map<String, Object>> getErrorContractList() {
        return errorContractList;
    }

    public void setErrorContractList(List<Map<String, Object>> errorContractList) {
        this.errorContractList = errorContractList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}
