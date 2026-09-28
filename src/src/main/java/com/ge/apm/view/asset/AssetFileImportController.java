/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.excel.EasyExcel;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetImport;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoLocalCacheService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbResourcesUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetFileImportController {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportController.class);

    private UploadedFile file;

    private List<Map<String, Object>> importOrgMap;
    private List<Map<String, Object>> importAssetMap;

    private AssetFileImportStreamingService streamingService;
    private AssetImportDataCheckService assetImportDataCheckService;
    private AssetFileImportUseCacheService importUseCacheService;

    private OrgInfoLocalCacheService orgInfoLocalCacheService;

    private CustomConfigService ccService;

    private UaaService uaaService;

    private OrgInfoRepository orgDao;
    private TenantInfoRepository tenantInfoDao;

    private Boolean isImported = false;
    private Boolean hasExportData = false;

    private Boolean canbeImported = false;
    private Boolean unCheckedNurseSite = false;
    private Boolean toDeleteCheckedNurseSite = false;
    private List<AssetInfo> unCheckNurseSiteMaintainList = new ArrayList<>();
    private Boolean autoGenerateQrcode = true;
    private Boolean isGeneratePmOrder;

    private StreamedContent exportFile;

    private StreamedContent assetTemplateFile;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private boolean displayErrorsOnly;

    private int activeTabIndex;

//    private Map<String, OrgInfo> orgNamesUriCache;

    private boolean crossSite;

    private Progress uploadProgress;

    private String treeNodeNameFilter;

    private List<AssetImport> assetImportList;
    private Boolean importProgressRender = false;


    @PostConstruct
    protected void init() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (!userContextService.accessContainsFunctionMenu("ASSET_BATCH_IMPORT_DATA")) {
            WebUtil.redirectTo("/accessdeniedzh.xhtml");
            return;
        }
        streamingService = WebUtil.getBean(AssetFileImportStreamingService.class);
        orgInfoLocalCacheService = WebUtil.getBean(OrgInfoLocalCacheService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        importUseCacheService = WebUtil.getBean(AssetFileImportUseCacheService.class);
        assetImportDataCheckService = WebUtil.getBean(AssetImportDataCheckService.class);

        initOrgTree();

    }

    public void uploadDataFile(FileUploadEvent event) {

        if (selectedOrg == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("PleaseSelectASiteFirst"));
            return;
        }

        this.file = event.getFile();

        crossSite = false;
        this.setImportProgressRender(true);
        uploadAssetFile(selectedOrg, false);
    }

    public void uploadCrossSiteDataFile(FileUploadEvent event) {
        logger.info("start to upload file");

        this.file = event.getFile();

        TenantInfo tenantInfo = tenantInfoDao.findByuid(UserContextService.getCurrentUserAccount().getTenantUID());
        if (tenantInfo.getInsulate() != null && tenantInfo.getInsulate().booleanValue()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("disableUploadInSiteSeparationMode"));
            return;
        }

        crossSite = true;

        logger.info("start to locate tenant org");
        //need tenant org to get workflow
        OrgInfo tenantOrg = orgDao.findFirstByTenantUIDAndOrgType(UserContextService.getCurrentUserAccount().getTenantUID(), 0);

        uploadAssetFile(tenantOrg, true);

    }

    public void uploadAssetFile(OrgInfo siteOrg, boolean crossSite) {

        assetImportDataCheckService.setSelectedOrgToImport(siteOrg, crossSite);
        streamingService.setSelectedOrgToImport(siteOrg, crossSite);
        try (
                ExcelDocumentStreamingReader orgDoc = new ExcelDocumentStreamingReader(file.getInputstream(), 1, AssetFileImportService.EXCEL_ORG_HEADER_ROW, null, null)) {
            /*logger.info("check upload asset file version");

            if(!streamingService.checkDocVersion(assetDoc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--"+WebUtil.getMessage("TemplateVersionIncorrect"));
                return;
            }*/
            logger.info("fetch org info in list");
            importOrgMap = streamingService.getOrgInfoMapFromTemplate(orgDoc);
            logger.info("fetch org cache for quick lookup");
            Map<String, OrgInfo> orgNamesUriCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(UserContextService.getCurrentUserAccount().getTenantUID());
            displayAssetsNew = new ArrayList<>();
            uploadProgress = new Progress();
            LocalDateTime startTime = LocalDateTime.now();
            EasyExcel.read(file.getInputstream(), AssetImport.class, new AssetImportHandel(displayAssetsNew, crossSite, assetImportDataCheckService, importOrgMap, unCheckNurseSiteMaintainList, selectedOrg, orgDao, uploadProgress))
                    .registerConverter(new AssetImportDoubleConverter())
                    .registerConverter(new AssetImportIntegerConverter())
                    .registerConverter(new AssetImportDateConvert())
                    .registerConverter(new AssetImportBooleanConvert())
                    .registerConverter(new AssetImportStringConvert())
                    .autoCloseStream(true).sheet(0).doRead();
            logger.info("found asset record count: {}", displayAssetsNew.size());
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info("easyExcel read  file: {} seconds", duration.getSeconds());

          /*  importAssetMap = streamingService.getAssetMapFromTemplate(assetDoc, orgNamesUriCache);

            logger.info("found asset record count: {}", importAssetMap.size());
*/
            if (displayAssetsNew.size() > 15000) {
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            logger.info("start to check data");
            LocalDateTime checkStartime = LocalDateTime.now();
            assetImportDataCheckService.orgCheck(importOrgMap);
            Map<String, Boolean> checkresult = assetImportDataCheckService.checkData(displayAssetsNew, importOrgMap, unCheckNurseSiteMaintainList, uploadProgress);
            LocalDateTime checkEndTime = LocalDateTime.now();
            Duration checkDuration = Duration.between(checkStartime, checkEndTime);
            logger.info("check import data : {} seconds", checkDuration.getSeconds());
            canbeImported = checkresult.get("canBeImport");
            unCheckedNurseSite = checkresult.get("unCheckedNurseSite");
            filterErrorRecords();
            logger.info("filter error records end");

        } catch (IOException e) {
            WebUtil.addErrorMessage("File can not be read!", null);
            logger.error(e.getMessage(), e);

        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" + e.getMessage(), null);

            logger.error(e.getMessage(), e);
        }
    }

    public void toBedelete() {
        toDeleteCheckedNurseSite = true;
    }

    public void checkIfRequireBeforeImportData() {
        if (CollectionUtils.isNotEmpty(unCheckNurseSiteMaintainList) && !toDeleteCheckedNurseSite) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("unCheckedNurseSite", unCheckedNurseSite);
            requestContext.addCallbackParam("toDeleteCheckedNurseSite", toDeleteCheckedNurseSite);
        } else {
            importData();
        }
    }

    public void importData() {
        try {
            if (!crossSite) {
                //if cross site import, org list excel sheet is useless, no need to import org
                streamingService.importOrgData(importOrgMap);
                logger.info(" import org data successful ");
            }

            Map<String, OrgInfo> orgNamesUriCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(UserContextService.getCurrentUserAccount().getTenantUID());

            //AssetFileImportCacheService assetFileImportCacheService = WebUtil.getBean(AssetFileImportCacheService.class);
            //AssetFileImportCache assetFileImportCache = new AssetFileImportCache(importAssetMap, selectedOrg, assetFileImportCacheService);
            LocalDateTime saveStartTime = LocalDateTime.now();
            List<AssetInfo> saveList = null;
            if (displayAssetsNew.size() >= 100) {
                saveList = importAssetDataAsync(displayAssetsNew, autoGenerateQrcode, orgNamesUriCache);
            } else {
                saveList = streamingService.importAssetDataNew(displayAssetsNew, autoGenerateQrcode, orgNamesUriCache);
            }
            LocalDateTime saveEndTime = LocalDateTime.now();
            Duration saveDuration = Duration.between(saveStartTime, saveEndTime);
            logger.info("save data : {} seconds", saveDuration.getSeconds());
            for (AssetInfo assetInfo : saveList) {
                streamingService.saveAssetHistory(assetInfo);
            }
            if (toDeleteCheckedNurseSite) {
                streamingService.deleteNursiteMaintain(unCheckNurseSiteMaintainList);
                toDeleteCheckedNurseSite = false;
            }
            deleteNurseSiteMaintainConfigItem(saveList);
            //importU seCacheService.importAssetData(crossSite, selectedOrg, importAssetMap, autoGenerateQrcode, orgNamesUriCache, assetFileImportCache);
            isImported = true;

            filterErrorRecords();

            logger.info("... import asset data successful ...");
//            downloadImportFailureData();
        } catch (Throwable e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);

            isImported = true; //disable import button
            logger.error(e.getMessage(), e);
        }
    }
    public void deleteNurseSiteMaintainConfigItem(List<AssetInfo> assetInfos) {
        if(toDeleteCheckedNurseSite) return;
        List<AssetInfo> scrappedEquipment = assetInfos.stream().filter(item -> Integer.valueOf(4).equals(item.getStatus())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(scrappedEquipment)) return;
        streamingService.deleteNurseSiteMaintainConfigItem(scrappedEquipment);
    }
    public void exportAssetExcelTemplate() throws IOException {

        CustomConfigObject customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        //field name using english
        List<String> exportFields = ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        //field name using chinese
//        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
//                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);
        List<I18nMessage> tcm = FieldValueMessageController.getFieldValueList("spare01");

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(false);



        List<String> assetSheetHeads = template.getAssetSheetHead();
        if(CollectionUtils.isNotEmpty(assetSheetHeads)){
            List<String> finalExportFields = exportFields;
            exportFields= assetSheetHeads.stream().filter(finalExportFields::contains).collect(Collectors.toList());
            if(!exportFields.contains("UUID")){
                exportFields.add("UUID");
            }
        }


        template.exportSampleAsset(orgList, exportFields);
        template.exportSampleAsset(orgList, exportFields, tcm);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (userContextService.isLocale()) {
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template.xlsx");
        } else {
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template_en.xlsx");
        }
        logger.info(" export asset excel template successful. ");
    }

    public void exportCrossSiteAssetExcelTemplate() throws IOException {
        CustomConfigObject customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        //field name using english
        List<String> exportFields = ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        insertCrossSiteColumns(exportFields);

        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(true);

        List<String> assetSheetHeads = template.getAssetSheetHead();
        if(CollectionUtils.isNotEmpty(assetSheetHeads)){
            List<String> finalExportFields = exportFields;
            exportFields= assetSheetHeads.stream().filter(finalExportFields::contains).collect(Collectors.toList());
            if(!exportFields.contains("UUID")){
                exportFields.add("UUID");
            }
        }


        template.exportSampleAsset(orgList, exportFields);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (userContextService.isLocale()) {
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template.xlsx");
        } else {
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template_en.xlsx");
        }
        logger.info(" export cross site asset excel template successful. ");
    }

    private void insertCrossSiteColumns(List<String> exportFields) {
        List<String> crossSiteColumns = new ArrayList<>();
        crossSiteColumns.add(WebUtil.getMessage("Institution"));
        crossSiteColumns.add(WebUtil.getMessage("Hospital"));

        String siteColumn = WebUtil.getMessage("hospitalId");
        int siteIndex = exportFields.indexOf(siteColumn);
        exportFields.addAll(siteIndex, crossSiteColumns);

    }

    public void cancel() {
        this.file = null;

    }

    public Integer getAssetErrorCounts() {
        if (null != importAssetMap && !importAssetMap.isEmpty()) {
            return Integer.parseInt(String.valueOf(importAssetMap.stream().filter(item -> item.get("status").equals(AssetFileImportService.ImportStatus.CheckFail)).count()));
        } else {
            return 0;
        }
    }

    public List<String> getAssetErrorList() {
        if (null == importAssetMap) {
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
        if (getIsSelect46Account()) {
            return;
        }
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

    }

    public void switchAssetCheckStatus(AjaxBehaviorEvent event) {

        filterErrorRecords();
    }

    public void filterErrorRecords() {

        if (!displayErrorsOnly) return;

        if (CollectionUtils.isEmpty(displayAssetsNew)) {
            displayErrorAssets = new ArrayList<>();
            logger.info("filter error records is empty. ");
            return;
        }
        List<AssetImport> result = new ArrayList<>();
        for (AssetImport assetImport : displayAssetsNew) {

            if (AssetFileImportService.ImportStatus.CheckFail.equals(assetImport.getImportStatus()) || AssetFileImportService.ImportStatus.Failure.equals(assetImport.getImportStatus())) {
                result.add(assetImport);
            }

        }
        displayErrorAssets = result;
        if (importOrgMap == null) return;
        List<Map<String, Object>> resultMap = new ArrayList<>();
        logger.info("filter org map start,size:{}", CollectionUtils.isNotEmpty(importOrgMap) ? importOrgMap.size() : 0);
        for (Map<String, Object> map : importOrgMap) {
            try {
                AssetFileImportService.ImportStatus importStatus = (AssetFileImportService.ImportStatus) map.get("status");
                logger.info("importStatus:{}", importStatus);
                if (importStatus == null) continue;
                if (importStatus == AssetFileImportService.ImportStatus.CheckFail || importStatus == AssetFileImportService.ImportStatus.Failure) {
                    resultMap.add(map);
                }
            }catch (Exception e){
                logger.info("convert failed:{}", e.getMessage());
            }
        }
        logger.info("filter error org records successful. ");
        displayOrgs = resultMap;
    }

    List<Map<String, Object>> displayAssets;
    List<AssetImport> displayErrorAssets;

    List<AssetImport> displayAssetsNew;

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

    public void onTabChange(TabChangeEvent event) {
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

    public Boolean getUnCheckedNurseSite() {
        return unCheckedNurseSite;
    }

    public Boolean getToDeleteCheckedNurseSite() {
        return toDeleteCheckedNurseSite;
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
        return (displayAssetsNew == null || displayAssetsNew.isEmpty());
    }

    public String getSelectedOrgName() {
        if (selectedOrg == null) {
            return "";
        } else {
            return selectedOrg.getName();
        }
    }

    public StreamedContent getAssetTemplateFile() {
        return assetTemplateFile;
    }

    public void setAssetTemplateFile(StreamedContent assetTemplateFile) {
        this.assetTemplateFile = assetTemplateFile;
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setActiveTabIndex(int activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    public boolean getIsSelect46Account() {
        if (Objects.equals(UserContextService.getCurrentUserAccount().getSiteId(), 46) && UserContextService.getCurrentUserAccount().getTenantUID().equals(UserContextService.getCurrentUserAccount().getSiteUID())) {
            return true;
        }
        return false;
    }

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    public List<AssetImport> getAssetImportList() {
        return assetImportList;
    }

    public void setAssetImportList(List<AssetImport> assetImportList) {
        this.assetImportList = assetImportList;
    }

    private TreeNode buildTree(List<OrgInfo> orgList, int siteId) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("org", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            rootNode.setSelectable(true);
            rootNode.setRowKey("org_" + org.getId());
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        return rootNode;
    }

    public void filterTreeNode() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgList = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid(), treeNodeNameFilter, loginUser.getSiteId());
        buildTree(orgList, loginUser.getSiteId());
    }

    public List<AssetImport> getDisplayAssetsNew() {
        logger.info("asset import result display error:{}", isDisplayErrorsOnly());
        if (isDisplayErrorsOnly()) {
            return displayErrorAssets;
        }
        return displayAssetsNew;
    }

    public void setDisplayAssetsNew(List<AssetImport> displayAssetsNew) {
        this.displayAssetsNew = displayAssetsNew;
    }

    public Progress getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(Progress uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public Boolean getImportProgressRender() {
        return importProgressRender;
    }

    public void setImportProgressRender(Boolean importProgressRender) {
        this.importProgressRender = importProgressRender;
    }

    public void start() {
        this.importProgressRender = true;
    }

    public List<AssetInfo> importAssetDataAsync(List<AssetImport> assetData, Boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache) {
        int mid = assetData.size() / 2;
        UserAccount currentAccount = UserContextService.getCurrentUserAccount();
        DbResourcesUtil resourcesUtil = WebUtil.getBean(DbResourcesUtil.class);
        CompletableFuture<List<AssetInfo>> t = CompletableFuture.supplyAsync(() -> streamingService.importAssetDataAsync(assetData.subList(0, mid), autoGenerateQrcode, orgNamesUriCache, currentAccount, resourcesUtil))
                .thenCombine(CompletableFuture.supplyAsync(() -> streamingService.importAssetDataAsync(assetData.subList(mid, assetData.size()), autoGenerateQrcode, orgNamesUriCache, currentAccount, resourcesUtil)), (a, b) -> {
                    List<AssetInfo> result = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(a)) {
                        result.addAll(a);
                    }
                    if (CollectionUtils.isNotEmpty(b)) {
                        result.addAll(b);
                    }
                    return result;
                });
        return t.join();
    }
}
