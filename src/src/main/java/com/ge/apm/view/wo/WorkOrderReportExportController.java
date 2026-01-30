package com.ge.apm.view.wo;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.V2WorkOrderReportDto;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.*;
import com.ge.apm.service.wo.doctemplate.WorkOrderDocxRenderService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2017/7/26.
 */
@ManagedBean
@ViewScoped
public class WorkOrderReportExportController implements Serializable {

    private static final long serialVersionUID = -2591740986699150398L;

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderReportExportController.class);

    private WorkOrderExportService workOrderExportService;
    private WorkOrderPublishRenderService workOrderPublishRenderService;
    private WorkOrderExportTemplateService workOrderExportTemplateService;
    private AssetInfoService assetInfoService;
    private UserCommonService ucService;
    private StreamedContent exportFile;

    private StreamedContent exportPdf;

    private StreamedContent exportDocx;

    private UserAccount currentUser;
    private OrgInfoRepository orgDao;
    private TenantInfoRepository tenantInfoRepository;


    private List<OrgInfo> institutionList;
    private List<OrgInfo> hospitalList;
    private List<OrgInfo> siteList;

    private String currentInstitutionUID;
    private String currentHospitalUID;
    private String currentSiteUID;

    private Boolean isStatusOpen;
    private Boolean isStatusClose;
    private Boolean isStatusCancel;

    private Date startTime;
    private Date endTime;
    private Date closedStartTime;
    private Date closedEndTime;

    private List<Map<String, Object>> workOrderList;

    private UaaService uaaService;
    @Setter@Getter
    private Boolean needTwoSubStatus=false;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;
    
    private OrgInfo filterDeptOrg;
    private List<OrgInfo> clinicalDeptList;
    private UserAccount filterCurrentOwner;
    private List<UserAccount> currentOwnerList;

    @Getter
    private Progress progress;

    private List<Map<String, Object>> selectedItems;

    private Boolean isInsulate;
    private String filterParts;
    private String filterTotalPrice;
    @Getter @Setter
    private String currentStepId;
    @Getter @Setter
    private Map<String,String> filterMap;
    private WorkflowService workflowService;
    private UserContextService userContextService;
    @Getter @Setter
    private String srNumStr;

    private WorkOrderDocxRenderService renderService;
    
    private SystemTemplateService systemTemplateService;
    @PostConstruct
    protected void init() {
        userContextService = WebUtil.getBean(UserContextService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        workOrderExportService = WebUtil.getBean(WorkOrderExportService.class);
        workOrderPublishRenderService = WebUtil.getBean(WorkOrderPublishRenderService.class);
        workOrderExportTemplateService = WebUtil.getBean(WorkOrderExportTemplateService.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        renderService=WebUtil.getBean(WorkOrderDocxRenderService.class);
        systemTemplateService=WebUtil.getBean(SystemTemplateService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        currentInstitutionUID = currentUser.getInstitutionUID();
        currentHospitalUID = currentUser.getHospitalUID();
        currentSiteUID = currentUser.getSiteUID();

        institutionList = orgDao.findByTenantUIDAndOrgType(currentUser.getTenantUID(), 1);
        hospitalList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(currentUser.getInstitutionUID(), 2);
        siteList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(currentUser.getHospitalUID(), 3);

        isStatusOpen = Boolean.TRUE;
        isStatusClose = Boolean.TRUE;
        isStatusCancel = Boolean.TRUE;

        isInsulate = tenantInfoRepository.findByuid(currentUser.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;

        filterMap = new HashMap<>();
        initOrgTree();
        loadClinicalDept();
        currentOwnerList = ucService.getWorkerList(null,"");
        currentOwnerList.addAll(ucService.getExternalWorkerList(currentUser.getTenantUID()).stream().filter(item-> !currentOwnerList.contains(item)).collect(Collectors.toList()));
        currentAssetTypeGroupList = currentUser.getAssetTypeGroupList();
        enableAssetTypeGroup = currentUser.isEnableAssetTypeGroup();
        if (enableAssetTypeGroup) {
            assetTypeGroupEncodedSet = currentAssetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        }
        needTwoSubStatus = isConfigNeedTwoSubStatusByUser(userContextService.getLoginUser());
    }
    
    public void onClinicalDeptChange(){
        
    }
    public void onCurrentOwnerChange(){
        
    }
    
    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.EMPTY_LIST;
        }else{
            clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getSiteUID());
            if(null != filterDeptOrg && !filterDeptOrg.getSiteUID().equals(selectedOrg.getSiteUID()) ){
                filterDeptOrg = null;
            }
        }
    }

    private void exportWoData(List<Map<String, Object>> workOrderdata) {
        SystemTemplateEnum systemTemplateEnum=SystemTemplateEnum.WORK_ORDER_REPORT_EXCEL;
//        String fileName = "APM_WorkOrder_Template.xlsx";
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if(!userContextService.isLocale()){
//             fileName = "APM_WorkOrder_Template_en.xlsx";-
            systemTemplateEnum=SystemTemplateEnum.WORK_ORDER_REPORT_EXCEL_EN;
        }

        workOrderdata = workOrderExportService.supplementWorkOrder(workOrderdata);

        try {
//            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExport/" + fileName);
            UserAccount user = UserContextService.getCurrentUserAccount();
            byte[] byteArray = systemTemplateService.getTemplateFileByte(systemTemplateEnum, user);


            ExcelDocument doc = new ExcelDocument(new ByteArrayInputStream(byteArray), ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = workOrderExportService.exportData(doc, workOrderdata, workOrderExportService.createPartsData(workOrderdata));
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "WorkOrderData.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(WorkOrderReportExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportWo() {
        if(selectedItems != null && selectedItems.size() > 0){
            exportWoData(selectedItems);
        }else if(workOrderList != null && workOrderList.size() > 0){
            exportWoData(workOrderList);
        }
    }

    public void exportAllWo() {
        exportWoData(workOrderList);
    }

    public void exportAllWoByAssetInfoId(Integer assetInfoId) {
        V2WorkOrderReportDto v2WorkOrderReportDto = new V2WorkOrderReportDto();
        v2WorkOrderReportDto.setAssetInfoId(assetInfoId);
        exportWoData(workOrderExportService.createPreviewWorkOrderData(v2WorkOrderReportDto));
    }

    public void exportSelectedWo() {
        exportWoData(selectedItems);
    }

    public void exportAllWoPdf() {
        export2Pdf(workOrderList);
    }

    public void exportSelectedWoPdf() {
        export2Pdf(selectedItems);
    }

    private void export2Pdf(List<Map<String, Object>> data) {
        WorkOrderPublishRenderService.RenderResult renderResult = workOrderPublishRenderService.renderPdf(data);
        exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(renderResult.result)), renderResult.contentType, renderResult.fileName);
    }

    public void exportWoDocx() {
        if(selectedItems != null && selectedItems.size() > 0){
            export2Docx(selectedItems);
        }else if(workOrderList != null && workOrderList.size() > 0){
            export2Docx(workOrderList);
        }
    }
    public void exportAllWoDocx() {
        export2Docx(workOrderList);
    }

    public void exportAllWoDocxByAssetInfoId(Integer assetInfoId) {
        V2WorkOrderReportDto v2WorkOrderReportDto = new V2WorkOrderReportDto();
        v2WorkOrderReportDto.setAssetInfoId(assetInfoId);
        export2Docx(workOrderExportService.createPreviewWorkOrderData(v2WorkOrderReportDto));
    }

    public void exportSelectedWoDocx() {
        export2Docx(selectedItems);
    }

    private void export2Docx(List<Map<String, Object>> data) {
        exportDocx = null;
        if (data.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NotSelectTheRecordsThatNeedToBeExported"));
            return;
        }
        data = workOrderExportService.supplementWorkOrder(data);
        try {
            progress=new Progress();
//            byte[] templateBytes = workOrderExportTemplateService.getActiveTemplateBytes(isInsulate, currentSiteUID, currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID());
            //WorkOrderPublishRenderService.RenderResult renderResult = workOrderPublishRenderService.render(data, templateBytes);
            byte[] templateBytes =  systemTemplateService.getTemplateFileByte(SystemTemplateEnum.WORK_ORDER_REPORT, currentUser);

//            data.sort((a, b) -> {
//                String noA = (String) a.getOrDefault(WebUtil.getMessage("srNum_xlsx"), "");
//                String noB = (String) b.getOrDefault(WebUtil.getMessage("srNum_xlsx"), "");
//                return noA.compareTo(noB);
//            });
            WorkOrderExportStream workOrderExportStream=new WorkOrderExportStream(data,templateBytes,renderService,progress);
            exportDocx = new DefaultStreamedContent(workOrderExportStream, workOrderExportStream.getContentType(), workOrderExportStream.getFileName());
        } catch (IOException e) {
            logger.error("Fail to export work orders", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("ExportFailed"));
        }
    }

    public void exportComplete(){
        WebUtil.addSuccessMessage("导出完成");
    }

    public void reflashData() {
        previewData(null);
    }

    public void previewData(ActionEvent actionEvent) {

        this.selectedItems = null;
        if(actionEvent!=null) filterMap.clear();

        if (null == startTime && null == endTime && null == closedStartTime && null == closedEndTime) {
            showMsgBox(WebUtil.getMessage("ApplyTimeErrTip"));
            return;
        } else {
            if ((null != startTime && null == endTime)) {
                showMsgBox(WebUtil.getMessage("LastestApplyTimeErrTip"));
                return;
            }
            if ((null == startTime && null != endTime)) {
                showMsgBox(WebUtil.getMessage("EarliestApplyTimeErrTip"));
                return;
            }
            if (null != closedStartTime && null == closedEndTime) {
                showMsgBox(WebUtil.getMessage("LastestCloseTimeErrTip"));
                return;
            }
            if (null == closedStartTime && null != closedEndTime) {
                showMsgBox(WebUtil.getMessage("EarliestCloseTimeErrTip"));
                return;
            }

            ZoneId zone = ZoneId.systemDefault();
            if(null != startTime && null != endTime){
                LocalDate startDate = LocalDateTime.ofInstant(startTime.toInstant(), zone).toLocalDate();
                Date tempEndDate = Date.from(startDate.plus(1, ChronoUnit.YEARS).atStartOfDay().atZone(zone).toInstant());
                if(endTime.after(tempEndDate)){
                    WebUtil.addErrorMessage(WebUtil.getMessage("ApplyTimeTooLongErrTip"));
                    return;
                }
            }
            if(null != closedStartTime && null != closedEndTime){
                LocalDate closedStartDate = LocalDateTime.ofInstant(closedStartTime.toInstant(), zone).toLocalDate();
                Date tempClosedEndDate = Date.from(closedStartDate.plus(1, ChronoUnit.YEARS).atStartOfDay().atZone(zone).toInstant());
                if(closedEndTime.after(tempClosedEndDate)){
                    WebUtil.addErrorMessage(WebUtil.getMessage("CloseTimeTooLongErrTip"));
                    return;
                }
            }

            V2WorkOrderReportDto v2WorkOrderReportDto = new V2WorkOrderReportDto();
            v2WorkOrderReportDto.setStartTime(startTime);
            v2WorkOrderReportDto.setEndTime(endTime);
            v2WorkOrderReportDto.setClosedStartTime(closedStartTime);
            v2WorkOrderReportDto.setClosedEndTime(closedEndTime);
            v2WorkOrderReportDto.setFilterParts(filterParts);
            v2WorkOrderReportDto.setFilterTotalPrice(filterTotalPrice);
            v2WorkOrderReportDto.setCurrentStepId(currentStepId);
            v2WorkOrderReportDto.setSelectedOrg(selectedOrg);
            v2WorkOrderReportDto.setClinicalDept(filterDeptOrg);
            v2WorkOrderReportDto.setCurrentOwner(filterCurrentOwner);
            v2WorkOrderReportDto.setStatusList(getStatusList());
            v2WorkOrderReportDto.setClinicalDeptList(clinicalDeptList);
            v2WorkOrderReportDto.setFilterMap(filterMap);
            v2WorkOrderReportDto.setAssetTypeGroupEncodedSet(assetTypeGroupEncodedSet);
            workOrderList = workOrderExportService.createPreviewWorkOrderData(v2WorkOrderReportDto);
        }
    }

    public void previewDataBySrNum(ActionEvent actionEvent) {
        this.selectedItems = null;
        if(actionEvent!=null) filterMap.clear();
        V2WorkOrderReportDto v2WorkOrderReportDto = new V2WorkOrderReportDto();
        v2WorkOrderReportDto.setSrNum(srNumStr);
        v2WorkOrderReportDto.setTenantUid(currentUser.getTenantUID());
        v2WorkOrderReportDto.setSiteUid(currentUser.getSiteUID());
        workOrderList = workOrderExportService.createPreviewWorkOrderData(v2WorkOrderReportDto);
    }

    private void showMsgBox(String msg) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private List<Integer> getStatusList() {
        List<Integer> statusList = new ArrayList<>();
        if (isStatusOpen) {
            statusList.add(1);
        }
        if (isStatusClose) {
            statusList.add(2);
        }
        if (isStatusCancel) {
            statusList.add(3);
        }

        if (statusList.isEmpty()) {
            //当工单状态没有选择时
            statusList.add(-999);
        }

        return statusList;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }
        loadClinicalDept();
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
    
    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public List<UserAccount> completeOwnerList(String query) {
        
        if (currentOwnerList == null) {
            return Collections.emptyList();
        }
        
        List<UserAccount> res = this.currentOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    private AssetTypeGroupRepository assetTypeGroupRepository;
    private AssetTypeGroupService assetTypeGroupService;
    private Set<String> assetTypeGroupEncodedSet;
    private List<AssetTypeGroup> currentAssetTypeGroupList;
    @Getter @Setter
    private String filterAssetTypeName;
    @Getter @Setter
    private boolean enableAssetTypeGroup;

    public List<String> completeAssetTypeGroupList(String query) {
        if (currentAssetTypeGroupList == null) {
            return Collections.emptyList();
        }
        List<AssetTypeGroup> res = this.currentAssetTypeGroupList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());
        return res.stream().map(AssetTypeGroup::getName).distinct().collect(Collectors.toList());
    }

    public void onAssetTypeGroupChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterAssetTypeName = (String) ((SelectEvent) event).getObject();
        }
        if (filterAssetTypeName != null) {
            List<AssetTypeGroup> assetTypeGroupsMatchingByName = this.currentAssetTypeGroupList.stream().filter(item -> item.getName().equals(filterAssetTypeName)).collect(Collectors.toList());
            Set<AssetTypeGroup> matchedList = assetTypeGroupService.collectChildAssetTypeGroupInExstingList(assetTypeGroupsMatchingByName, currentAssetTypeGroupList);
            assetTypeGroupEncodedSet = matchedList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        } else {
            if (enableAssetTypeGroup) {
                assetTypeGroupEncodedSet = currentAssetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
            } else {
                assetTypeGroupEncodedSet = Collections.emptySet();
            }
        }
    }

    public void filterSiteByHospital() {
        siteList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(this.getCurrentHospitalUID(), 3);
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public String openNewWindowView(Map<String, Object> item) {
        return "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=workOrderDetail&amp;woId=" + item.get("woId")
                + "&amp;srId=" + item.get("srId")
                + "&amp;assetUid=" + item.get("assetUid")
                + "&amp;fileSearchUrl=" + workOrderExportService.getFileSearchUrl();
    }

    public String getFileSearchUrl() {
        return workOrderExportService.getFileSearchUrl();
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<OrgInfo> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<OrgInfo> institutionList) {
        this.institutionList = institutionList;
    }

    public List<OrgInfo> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<OrgInfo> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<OrgInfo> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<OrgInfo> siteList) {
        this.siteList = siteList;
    }

    public String getCurrentInstitutionUID() {
        return currentInstitutionUID;
    }

    public void setCurrentInstitutionUID(String currentInstitutionUID) {
        this.currentInstitutionUID = currentInstitutionUID;
    }

    public String getCurrentHospitalUID() {
        return currentHospitalUID;
    }

    public void setCurrentHospitalUID(String currentHospitalUID) {
        this.currentHospitalUID = currentHospitalUID;
    }

    public String getCurrentSiteUID() {
        return currentSiteUID;
    }

    public void setCurrentSiteUID(String currentSiteUID) {
        this.currentSiteUID = currentSiteUID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getClosedStartTime() {
        return closedStartTime;
    }

    public void setClosedStartTime(Date closedStartTime) {
        this.closedStartTime = closedStartTime;
    }

    public Date getClosedEndTime() {
        return closedEndTime;
    }

    public void setClosedEndTime(Date closedEndTime) {
        this.closedEndTime = closedEndTime;
    }

    public Boolean getStatusOpen() {
        return isStatusOpen;
    }

    public void setStatusOpen(Boolean statusOpen) {
        isStatusOpen = statusOpen;
    }

    public Boolean getStatusClose() {
        return isStatusClose;
    }

    public void setStatusClose(Boolean statusClose) {
        isStatusClose = statusClose;
    }

    public Boolean getStatusCancel() {
        return isStatusCancel;
    }

    public void setStatusCancel(Boolean statusCancel) {
        isStatusCancel = statusCancel;
    }

    public List<Map<String, Object>> getWorkOrderList() {
        return workOrderList;
    }

    public void setWorkOrderList(List<Map<String, Object>> workOrderList) {
        this.workOrderList = workOrderList;
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


    public List<Map<String, Object>> getSelectedItems() {
        return selectedItems;
    }


    public void setSelectedItems(List<Map<String, Object>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public StreamedContent getExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(StreamedContent exportPdf) {
        this.exportPdf = exportPdf;
    }

    public StreamedContent getExportDocx() {
        return exportDocx;
    }

    public void setExportDocx(StreamedContent exportDocx) {
        this.exportDocx = exportDocx;
    }

    public OrgInfo getFilterDeptOrg() {
        return filterDeptOrg;
    }

    public void setFilterDeptOrg(OrgInfo filterDeptOrg) {
        this.filterDeptOrg = filterDeptOrg;
    }

    public UserAccount getFilterCurrentOwner() {
        return filterCurrentOwner;
    }

    public void setFilterCurrentOwner(UserAccount filterCurrentOwner) {
        this.filterCurrentOwner = filterCurrentOwner;
    }

    public String getFilterParts() {
        return filterParts;
    }

    public void setFilterParts(String filterParts) {
        this.filterParts = filterParts;
    }

    public String getFilterTotalPrice() {
        return filterTotalPrice;
    }

    public void setFilterTotalPrice(String filterTotalPrice) {
        this.filterTotalPrice = filterTotalPrice;
    }

    public Boolean isConfigNeedTwoSubStatusByUser(UserAccount userAccount) {
        WorkflowConfig woc = workflowService.getWorkflowByOrg(userAccount.getSiteUID());
        return woc != null&&woc.getEnableSendRepair()!=null&&woc.getEnableSendRepair();
    }

    public List<I18nMessage> getWoSteps(String stepIds){
        List<String> stepIdsList = Arrays.asList(stepIds.split(","));
        return FieldValueMessageController.doGetFieldValueList("woSteps", -1).stream()
                .filter(item -> stepIdsList.contains(item.getMsgKey())).collect(Collectors.toList());
    }
}
