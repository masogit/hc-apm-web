package com.ge.apm.view.wo;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.WorkOrderDetailPojo;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.WorkOrderDetailExportService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.map.HashedMap;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
 * Created by 212605082 on 2019/3/18.
 */
@ManagedBean
@ViewScoped
public class WorkOrderDetailExportController implements Serializable {

    private static final long serialVersionUID = -2591740986699150398L;

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderDetailExportController.class);

    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private UserCommonService ucService;
    private WorkOrderDetailExportService workOrderDetailExportService;

    private List<Map<String, Object>> workOrderDetailPojoList;
    private List<Map<String, Object>> selectedItems;
    private StreamedContent exportFile;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private List<OrgInfo> clinicalDeptList;
    private UserAccount currentUser;
    private List<UserAccount> currentOwnerList;

    private OrgInfo filterDeptOrg;
    private Date startTime;
    private Date endTime;
    private String filterStatementStatus;
    private UserAccount filterCurrentOwner;
    private Double filterMinPrice;
    private Double filterMaxPrice;
    private String searchStr;

    @PostConstruct
    protected void init() {

        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        workOrderDetailExportService = WebUtil.getBean(WorkOrderDetailExportService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        initOrgTree();
        loadClinicalDept();
        currentOwnerList = ucService.getWorkerList(null,"");
        currentOwnerList.addAll(ucService.getExternalWorkerList(currentUser.getTenantUID()).stream().filter(item-> !currentOwnerList.contains(item)).collect(Collectors.toList()));

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

    public void previewData(){
        Map<String, Object> filterMap = new HashedMap();

        ZoneId zone = ZoneId.systemDefault();
        LocalDate startDate = LocalDateTime.ofInstant(startTime.toInstant(), zone).toLocalDate();
        Date tempEndDate = Date.from(startDate.plus(1, ChronoUnit.YEARS).atStartOfDay().atZone(zone).toInstant());
        if(endTime.after(tempEndDate)){
            WebUtil.addErrorMessage("保修时间段最大不能超过一年！");
            return;
        }

        this.selectedItems = null;

        if(selectedOrg != null){
            filterMap.put("selectedOrg", selectedOrg);
        }
        if(filterDeptOrg != null){
            filterMap.put("filterDeptOrg", filterDeptOrg);
        }
        if(clinicalDeptList != null && !clinicalDeptList.isEmpty()){
            filterMap.put("clinicalDeptList", clinicalDeptList);
        }
        if(filterCurrentOwner != null){
            filterMap.put("filterCurrentOwner", filterCurrentOwner);
        }
        if(startTime != null){
            filterMap.put("startTime", startTime);
        }
        if(endTime != null){
            filterMap.put("endTime", endTime);
        }
        if(filterStatementStatus != null){
            filterMap.put("filterStatementStatus", filterStatementStatus);
        }
        if(filterMinPrice != null && filterMinPrice != 0D){
            filterMap.put("filterMinPrice", filterMinPrice);
        }
        if(filterMaxPrice != null && filterMaxPrice != 0D){
            filterMap.put("filterMaxPrice", filterMaxPrice);
        }
        if(searchStr != null && searchStr.trim() != ""){
            filterMap.put("searchStr", searchStr);
        }

        workOrderDetailPojoList = workOrderDetailExportService.createWorkOrderDetailData(filterMap);

    }

    private void exportWorkOrderDetailData(List<Map<String, Object>> workOrderDetailData) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        String fileName = "APM_WorkOrderDetail_Template.xlsx";
        if(!userContextService.isLocale()){
            fileName = "APM_WorkOrderDetail_Template_en.xlsx";
        }
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExport/" + fileName);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = workOrderDetailExportService.exportOrderDetailData(doc, workOrderDetailData);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "WorkOrderDetailData.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(WorkOrderReportExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportAllWo(){
        exportWorkOrderDetailData(workOrderDetailPojoList);
    }

    public void exportSelectedWo(){
        exportWorkOrderDetailData(selectedItems);
    }

    public void onSelectWorkOrderDetail(){

    }

    public void updateStatement(String actionType){
        Boolean flag = Boolean.FALSE;
        if(selectedItems.isEmpty()){
            WebUtil.addErrorMessage("请选择要操作的数据");
            return;
        }
        if("close".equals(actionType)){
            flag = workOrderDetailExportService.closeStatement(selectedItems);
        }else if("open".equals(actionType)){
            flag = workOrderDetailExportService.openStatement(selectedItems);
        }

        if(flag){
            WebUtil.addErrorMessage("变更完成");
            selectedItems = new ArrayList<>();
            previewData();
        }
    }

    public void onClinicalDeptChange(){

    }
    public void onCurrentOwnerChange(){

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

    public Double getFilterMinPrice() {
        return filterMinPrice;
    }

    public void setFilterMinPrice(Double filterMinPrice) {
        this.filterMinPrice = filterMinPrice;
    }

    public Double getFilterMaxPrice() {
        return filterMaxPrice;
    }

    public void setFilterMaxPrice(Double filterMaxPrice) {
        this.filterMaxPrice = filterMaxPrice;
    }

    public String getFilterStatementStatus() {
        return filterStatementStatus;
    }

    public void setFilterStatementStatus(String filterStatementStatus) {
        this.filterStatementStatus = filterStatementStatus;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public List<Map<String, Object>> getWorkOrderDetailPojoList() {
        return workOrderDetailPojoList;
    }

    public void setWorkOrderDetailPojoList(List<Map<String, Object>> workOrderDetailPojoList) {
        this.workOrderDetailPojoList = workOrderDetailPojoList;
    }

    public List<Map<String, Object>> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Map<String, Object>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}
