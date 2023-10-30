
package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.budget.BudgetApplicationExportService;
import com.ge.apm.service.budget.BudgetPersistService;
import com.ge.apm.service.common.SysRoleConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Yangfan, Liu
 */
@ViewScoped
@ManagedBean(name = "budgetController")
public class BudgetApplicationBapController extends GenericCRUDUUIDController<BudgetApplication> {
    private static Logger logger = LoggerFactory.getLogger(BudgetApplicationBapController.class);

    private Page<BudgetApplication> emptyPage = new PageImpl<>(Collections.emptyList());
    private UserAccount account = UserContextService.getCurrentUserAccount();
    private Integer tabIndex = 0;
    private Integer tabIndexList = 0;


    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";
    public final static String VIEW_ACTION = "View";

    private BlobFileService blobService;
    /**
     * 编辑数据主键
     */
    private String editId;

    private BudgetApplicationAsset curSelectApplicationBudgetAsset = new BudgetApplicationAsset();

    private BudgetAssetInfo curSelectBudgetAssetInfo;

    private List<V2_BlobObject> attachList;

    // 弹窗列表
    private List<BudgetAssetInfo> optionalBudgetAssets = new ArrayList<>();

    private List<SearchFilter> assetSearchFilters;

    private BudgetApplicationRepository dao;
    private BudgetAssetInfoRepository budgetAssetDao;
    private BlobObjectRepository blobObjectRepository;

    private PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;

    private UserContextService userContextService;
    private BudgetPersistService persistService;
    private BudgetApplicationExportService budgetApplicationExportService;

    private BudgetSearchForm searchForm = new BudgetSearchForm();
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();

    private BudgetAssetInfo selectedAsset;

    private OrgInfo org;
    private CustomConfigObject customConfig;
    private TreeNode orgTree;
    private TreeNode orgTreeEdit;
    private OrgInfo selectedDept;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BudgetApplicationRepository.class);
        budgetAssetDao = WebUtil.getBean(BudgetAssetInfoRepository.class);
        purchaseBudgetConfigRepository = WebUtil.getBean(PurchaseBudgetConfigRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        budgetApplicationExportService = WebUtil.getBean(BudgetApplicationExportService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        persistService = WebUtil.getBean(BudgetPersistService.class);
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        CustomConfigService ccService = WebUtil.getBean(CustomConfigService.class);
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 0);
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(treeNode);
        orgTreeEdit = initOrgTree(treeNodeEdit);
        String action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        this.editId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "editId");
        attachList = new ArrayList<>();
        String tabIndexStr = (String) WebUtil.getRequestParameter("tabIndex");
        if(StringUtils.isNotBlank(tabIndexStr)){
            this.tabIndex = Integer.parseInt(tabIndexStr);
        }
        String tabIndexList = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "tabIndexList");
        if(StringUtils.isNotBlank(tabIndexList)){
            this.tabIndexList = Integer.parseInt(tabIndexList);
        }
        try {
            curSelectApplicationBudgetAsset = new BudgetApplicationAsset();
            if (BudgetApplicationBapController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
            } else if (BudgetApplicationBapController.EDIT_ACTION.equals(action)) {
                this.prepareEdit();
            } else if (BudgetApplicationBapController.VIEW_ACTION.equals(action)){
                this.prepareView();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected GenericRepositoryUUID<BudgetApplication> getDAO() {
        return dao;
    }

    public List<BudgetAssetInfo> autoCompleteAssetName(String query) {
        optionalBudgetAssets.clear();
        List<BudgetAssetInfo> assetInfoList = budgetAssetDao.findByTenantUIDAndIsDeleted(selectedDept.getTenantUID(),0);
        assetInfoList = assetInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(assetInfoList)?assetInfoList:new ArrayList<BudgetAssetInfo>();
    }

    public void onBudgetAssetInfoChange(){
        this.curSelectApplicationBudgetAsset.setApplicationAssetName(this.curSelectBudgetAssetInfo.getName());
        this.curSelectApplicationBudgetAsset.setApplicationAssetCategory(this.curSelectBudgetAssetInfo.getApplicationAssetCategory());
        this.curSelectApplicationBudgetAsset.setRecommendedBrand(this.curSelectBudgetAssetInfo.getAssetBrand());
        this.curSelectApplicationBudgetAsset.setModel(this.curSelectBudgetAssetInfo.getAssetModel());
        this.curSelectApplicationBudgetAsset.setStandards(this.curSelectBudgetAssetInfo.getStandards());
    }

    public boolean curModelDisables(){
        return "View".equals(crudActionName)?true:false;
    }

    public boolean curModelCreate(){
        return "Create".equals(crudActionName)?true:false;
    }

    public void onTabChange(TabChangeEvent event) {
        selected = null;
        switch (event.getTab().getId()) {
            case "s1":
                tabIndex = 1;
                break;
            case "s2":
                tabIndex = 2;
                break;
            case "s3":
                tabIndex = 3;
                break;
            case "s4":
                tabIndex = 4;
                break;
            default:
                tabIndex = 0;
                break;
        }
    }



    protected Page<BudgetApplication> loadData(PageRequest pageRequest) {
        if (userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)
                || userContextService.hasRole(SysRoleConstant.ROLE_DEPT_HEAD)
                ||userContextService.hasRole(SysRoleConstant.ROLE_CLINICAL_STAFF)) {
            this.searchFilters = this.getSearchFilter(this.tabIndex);
        } else {
            return emptyPage;
        }
        List<BudgetApplication> dataList = persistService.loadDataList(this.searchFilters);
        return new PageImpl(dataList,pageRequest,dataList.size());
    }

    public void exportExcel(String orderType){
        this.searchFilters = this.getSearchFilter(this.tabIndex);
        List<BudgetApplication> dataList = persistService.loadDataList(this.searchFilters);
        List<Map<String, Object>> budgetDataList = persistService.createBudgeApplicationData(dataList,orderType);
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/budget/application/excel/APM_Budget_BAP_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            persistService.exportData(doc, budgetDataList,orderType);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Budget_BAP.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(BudgetApplicationBapController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<SearchFilter> getSearchFilter(Integer tabIndex) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("orderType", SearchFilter.Operator.EQ, BudgetApplication.OrderType.BAP.toString()));
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        removeFilterOnField("applicationStatus");removeFilterOnField("applicationStep");
        if (tabIndex == 0) {//待办
            searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1,4)));
        } else if(tabIndex == 1){//待审核
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "PendingApproval"));
        } else if(tabIndex == 2){//待完成
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "PendingComplete"));
        } else if(tabIndex == 3){//完成
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "Complete"));
        } else if(tabIndex == 4){//全部
            searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1, 2, 3,4,5,6)));
        }
        if (userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)
                || userContextService.hasRole(SysRoleConstant.ROLE_DEPT_HEAD)) {
            // 临床科室主任, 医学工程处主任
            queryScope(org);
        } else if (userContextService.hasRole(SysRoleConstant.ROLE_CLINICAL_STAFF)) {
            // 临床科室人员
            searchFilters.add(new SearchFilter("applicantId", SearchFilter.Operator.EQ, account.getId()));
        }
        return searchFilters;
    }

    /**
     * 跳转新增模式
     *
     * @return
     */
    public String intoCreateModel(){
        List<PurchaseBudgetConfig> purchaseBudgetConfigList = purchaseBudgetConfigRepository.queryEffiveConfig(account.getTenantUID(),account.getSiteUID(),new Date());
        String url = new String();
        if(CollectionUtils.isNotEmpty(purchaseBudgetConfigList)){
            url = UrlEncryptController.encodeUrlParam("Edit?action=" + BudgetApplicationBapController.CREATE_ACTION+"&tabIndexList="+tabIndex);
        }
        WebUtil.addSuccessMessage("年度采购入口未开启，请检查后重试！");
        return url;
    }
    /**
     * 跳转编辑模式
     *
     * @return
     */
    public String intoEditModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action=" + BudgetApplicationBapController.EDIT_ACTION + "&editId=" + this.selected.getId()+"&tabIndexList="+tabIndex);
    }
    /**
     * 跳转查看模式
     *
     * @return
     */
    public String intoViewModel() {
        return UrlEncryptController.encodeUrlParam("Edit?action=" + BudgetApplicationBapController.VIEW_ACTION + "&editId=" + this.selected.getId()+"&tabIndexList="+tabIndex);
    }
    public void doubleClick(SelectEvent event) {
        String str = "action=" + BudgetApplicationBapController.VIEW_ACTION + "&editId=" + this.selected.getId()+"&tabIndexList="+tabIndex;
        String url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void saveApplication() {
        persistService.handleCommonField(selected,1,BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString(),
                account,crudActionName,this.curSelectBudgetAssetInfo);
        boolean isOk = persistService.saveApplication(selected,this.attachList);
        this.curSelectApplicationBudgetAsset = selected.getBudgetAssets().iterator().next();
        actionMessage(isOk);
    }

    public String applicationDataFlow(Integer applicationStatus,String applicationStep) {
        if(BudgetApplication.APPLICATIONSTEP.PendingApproval.toString().equals(applicationStep)){
            //无可用的审批流
            boolean resultMultiLevelApproval = persistService.checkMultiLevelApproval(selected);
            if(!resultMultiLevelApproval){
                WebUtil.addSuccessMessage("无可用的审批流,请检查后重试！");
                return "";
            }
            persistService.handleCommonField(selected,applicationStatus,applicationStep,
                    account,crudActionName,this.curSelectBudgetAssetInfo);
            persistService.submitApplication(selected,this.attachList);
        }else{
            persistService.handleCommonField(selected,applicationStatus,applicationStep,
                    account,crudActionName,this.curSelectBudgetAssetInfo);
            persistService.saveApplication(selected,this.attachList);
        }
        return this.backList();
    }

    public String backList() {
        return "List.xhtml?faces-redirect=true&tabIndex="+tabIndexList;
    }


    @Override
    public void delete() {
        BudgetApplication application = dao.findById(selected.getId());
        boolean isOk = persistService.deleteApplication(application);
        selected = null;
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetApplication") + WebUtil.getMessage("Blank")
                + "删除" + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
    }

    public void saveAsset() {
        // 保存时设置科室·
        selectedAsset.setDept(selectedDept);
        selectedAsset.setDeptId(selectedDept.getId());
        selectedAsset.setTenantUID(selectedDept.getTenantUID());
        selectedAsset.setInstitutionUID(selectedDept.getInstitutionUID());
        selectedAsset.setHospitalUID(selectedDept.getHospitalUID());
        selectedAsset.setSiteUID(selectedDept.getSiteUID());
        boolean isOk = persistService.saveAsset(selectedAsset);
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetAsset") + WebUtil.getMessage("Blank")
                + WebUtil.getMessage("Create") + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
        this.optionalBudgetAssets = budgetAssetDao.findBySearchFilter(assetSearchFilters,new Sort(Sort.Direction.DESC, "createdDate"));
    }

    private void actionMessage(boolean isOk) {
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetApplication") + WebUtil.getMessage("Blank")
                + WebUtil.getMessage(crudActionName) + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
    }

    public void search() {
        selected = null;
        clearSearchFilter();
        addSearchFilter();
    }

    public void resetSearch() {
        selected = null;
        clearSearchFilter();
        searchForm = new BudgetSearchForm();
        addSearchFilter();
    }

    private void clearSearchFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
    }

    private void addSearchFilter() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        if (null != searchForm.getDept()) {
            OrgInfo orgInfo = searchForm.getDept();
            queryScope(orgInfo);
        }
        if (null != searchForm.getDateStart()) {
            updateSearchFilter(new SearchFilter("applicationDate", SearchFilter.Operator.GTE,  formatter.format(searchForm.getDateStart())));
        }
        if (null != searchForm.getDateEnd()) {
            this.searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.LT, formatter.format(new DateTime(searchForm.getDateEnd()).plusDays(1).toDate())));
        }
        if (null != searchForm.getApplicantYear()) {
            this.searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.EQ, formatter.format(searchForm.getApplicantYear())));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicationNum())) {
            updateSearchFilter(new SearchFilter("applicationNum", SearchFilter.Operator.LIKE, searchForm.getApplicationNum()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicantName())) {
            updateSearchFilter(new SearchFilter("applicantName", SearchFilter.Operator.EQ, searchForm.getApplicantName()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetName())) {
            updateSearchFilter(new SearchFilter("applicationAssetName", SearchFilter.Operator.LIKE, searchForm.getAssetName()));
        }
    }
    private void queryScope(OrgInfo org){
        if(org == null) return;
        switch (org.getOrgType()) {
            case 4:
                // 科室
                searchFilters.add(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, org.getId()));
                break;
            case 3:
                // 院区
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, org.getSiteUID()));
                break;
            case 2:
                // 医院
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, org.getHospitalUID()));
                break;
            case 1:
                // 集团
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, org.getInstitutionUID()));
                break;
            default:
                // 租户
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, org.getTenantUID()));
        }
    }
    public void calcAmount(BudgetApplicationAsset budgetApplicationAsset) {
        if(budgetApplicationAsset.getApplicationPrice()!= null && budgetApplicationAsset.getApplicationQuantity()!= null){
            budgetApplicationAsset.setApplicationAmount(budgetApplicationAsset.getApplicationPrice()
                    .multiply(new BigDecimal(budgetApplicationAsset.getApplicationQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            this.selected.setTotalAmount(budgetApplicationAsset.getApplicationAmount());
        }
    }

    public void prepareCreateAsset() {
        selectedAsset = new BudgetAssetInfo();
        selectedAsset.setIsDeleted(0);
        selectedAsset.setCreatedBy(account.getLoginName());
        selectedAsset.setCreatedDate(new Date());
        selectedAsset.setLastModifiedBy(account.getLoginName());
        selectedAsset.setLastModifiedDate(new Date());
        searchAsset();
    }

    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    /**
     * 查询 区域部门选择
     * @param event
     */
    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        searchForm.setDept(orgInfo);
    }

    /**
     * 编辑区域部门选择
     * @param event
     */
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selectedDept = orgInfo;
        this.selected.setApplicationDeptName(selectedDept.getName());
        this.selected.setApplicationDept(selectedDept);
    }



    public void clearSelected() {
        if (StringUtils.isEmpty(selected.getId())) {
            selected = null;
        }
    }

    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedDept = null;
        selected = new BudgetApplication();
        persistService.initCreateObject(selected,this.curSelectApplicationBudgetAsset,account,BudgetApplication.OrderType.BAP.toString());
        this.selectedDept = selected.getApplicationDept();
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        buildCurDetail(this.editId);
    }

    @Override
    public void prepareView() {
        crudActionName = "View";
        buildCurDetail(this.editId);
    }

    public void buildCurDetail(String id){
        this.selected = this.dao.findById(id);
        this.curSelectApplicationBudgetAsset = this.selected.getBudgetAssets().iterator().next();
        this.selectedDept = this.selected.getApplicationDept();
        this.selected.setApplicationDeptName(this.selectedDept.getName());
        if(this.curSelectApplicationBudgetAsset!=null ){
            this.attachList = blobObjectRepository.findByBoUuid(this.curSelectApplicationBudgetAsset.getId());
        }
    }

    public boolean budgetApprovalsDisplay(){
        return (CollectionUtils.isNotEmpty(this.selected.getBudgetApprovals())&&this.selected.getBudgetApprovals().size()>0)?true:false;
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject ) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_BUDGET);
                //blobObject.setBoUuid(selected.getBudgetAssets().iterator().next().getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void searchAsset() {
        clearAssetSearchFilter();
        addAssetSearchFilter();
        optionalBudgetAssets.clear();
        this.optionalBudgetAssets = budgetAssetDao.findBySearchFilter(assetSearchFilters,new Sort(Sort.Direction.DESC, "createdDate"));
    }

    public void resetAssetSearch() {
        assetSearchForm = new BudgetAssetSearchForm();
        searchAsset();
    }

    private void clearAssetSearchFilter() {
        if (assetSearchFilters == null) {
            assetSearchFilters = new ArrayList<>();
        } else {
            assetSearchFilters.clear();
        }
    }
    private void updateAssetSearchFilter(SearchFilter searchFilter) {
        removeAssetFilterOnField(searchFilter.fieldName);
        assetSearchFilters.add(searchFilter);
    }

    private void removeAssetFilterOnField(String fieldName) {
        assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> fieldName.equals(item.fieldName)).collect(Collectors.toList()));
    }

    private void addAssetSearchFilter() {
        if (null != assetSearchForm.getApplicationAssetCategory()) {
            updateAssetSearchFilter(new SearchFilter("applicationAssetCategory", SearchFilter.Operator.LIKE, assetSearchForm.getApplicationAssetCategory()));
        }
        if (null != assetSearchForm.getStandards()) {
            updateAssetSearchFilter(new SearchFilter("standards", SearchFilter.Operator.LIKE, assetSearchForm.getStandards()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetBrand())) {
            updateAssetSearchFilter(new SearchFilter("assetBrand", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetModel())) {
            updateAssetSearchFilter(new SearchFilter("assetModel", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetName())) {
            updateAssetSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        // 科室
//        updateAssetSearchFilter(new SearchFilter("deptId", SearchFilter.Operator.EQ, selectedDept.getId()));
        // 现在只要是同一租户下的设备都能看到
        updateAssetSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedDept.getTenantUID()));
        updateAssetSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
    }

    @Getter
    @Setter
    private StreamedContent exportFile;


    public void exportAll()  {
        try {
            this.selected = this.dao.findById(selected.getId());
            this.curSelectApplicationBudgetAsset = this.selected.getBudgetAssets().iterator().next();
            this.selectedDept = this.selected.getApplicationDept();
            this.selected.setApplicationDeptName(this.selectedDept.getName());
            if(this.curSelectApplicationBudgetAsset!=null ){
                this.attachList = blobObjectRepository.findByBoUuid(this.curSelectApplicationBudgetAsset.getId());
            }
            exportFile = budgetApplicationExportService.exportZip(curSelectApplicationBudgetAsset, attachList);

        }catch (IOException ex) {
            java.util.logging.Logger.getLogger(BudgetApplicationBapController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void prepareDelete() {
        crudActionName = "Delete";
    }


    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public BudgetSearchForm getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(BudgetSearchForm searchForm) {
        this.searchForm = searchForm;
    }

    public BudgetAssetSearchForm getAssetSearchForm() {
        return assetSearchForm;
    }

    public void setAssetSearchForm(BudgetAssetSearchForm assetSearchForm) {
        this.assetSearchForm = assetSearchForm;
    }

    public BudgetAssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(BudgetAssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getOrgTreeEdit() {
        return orgTreeEdit;
    }

    public void setOrgTreeEdit(TreeNode orgTreeEdit) {
        this.orgTreeEdit = orgTreeEdit;
    }

    public OrgInfo getSelectedDept() {
        return selectedDept;
    }

    public void setSelectedDept(OrgInfo selectedDept) {
        this.selectedDept = selectedDept;
    }

    public String getEditId() {
        return editId;
    }

    public void setEditId(String editId) {
        this.editId = editId;
    }

    public BudgetApplicationAsset getCurSelectApplicationBudgetAsset() {
        return curSelectApplicationBudgetAsset;
    }

    public void setCurSelectApplicationBudgetAsset(BudgetApplicationAsset curSelectApplicationBudgetAsset) {
        this.curSelectApplicationBudgetAsset = curSelectApplicationBudgetAsset;
    }

    public List<V2_BlobObject> getAttachList() {
        if(this.curSelectApplicationBudgetAsset!=null && StringUtils.isNotBlank(this.curSelectApplicationBudgetAsset.getId())){
            attachList = blobObjectRepository.findByBoUuid(this.curSelectApplicationBudgetAsset.getId());
        }
        return attachList;
    }

    public void setAttachList(List<V2_BlobObject> attachList) {
        this.attachList = attachList;
    }

    public BudgetAssetInfo getCurSelectBudgetAssetInfo() {
        return curSelectBudgetAssetInfo;
    }

    public void setCurSelectBudgetAssetInfo(BudgetAssetInfo curSelectBudgetAssetInfo) {
        this.curSelectBudgetAssetInfo = curSelectBudgetAssetInfo;
    }

    public List<BudgetAssetInfo> getOptionalBudgetAssets() {
        return optionalBudgetAssets;
    }

    public void setOptionalBudgetAssets(List<BudgetAssetInfo> optionalBudgetAssets) {
        this.optionalBudgetAssets = optionalBudgetAssets;
    }
}
