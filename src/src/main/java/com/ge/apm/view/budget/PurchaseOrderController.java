
package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.budget.AccessRecordsService;
import com.ge.apm.service.budget.PurchaseOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AccessRecordsFeature;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author lyj
 */
@ViewScoped
@ManagedBean(name = "purchaseOrderController")
public class PurchaseOrderController extends GenericCRUDUUIDController<PurchaseOrderInfo> {
    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);
    private Page<PurchaseOrderInfo> emptyPage = new PageImpl<>(Collections.emptyList());
    private UserAccount account = UserContextService.getCurrentUserAccount();
    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";
    private BlobFileService blobService;
    private List<SearchFilter> assetSearchFilters;
    private PurchaseOrderInfoRepository dao;
    private BudgetApplicationAssetRepository budgetApplicationAssetRepository;
    private BudgetApplicationRepository budgetApplicationRepository;
    private BlobObjectRepository blobObjectRepository;
    private VenderInfoRepository venderInfoRepository;
    private PurchaseVenderRepository purchaseVenderRepository;
    private OrgInfoRepository orgDao;
    private BudgetPurchaseTemplateManageRepository budgetPurchaseTemplateManageRepository;
    private PurchaseTemplateRepository purchaseTemplateRepository;
    private PurchaseOrderService purchaseOrderService;
    private UserContextService userContextService;
    @Getter  @Setter
    private String budgetAssetId;
    @Getter @Setter
    private BudgetSearchForm searchForm = new BudgetSearchForm();
    @Getter @Setter
    private BudgetApplicationAsset confirmBudget;
    @Getter @Setter
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();
    @Getter @Setter
    private BudgetAssetInfo selectedAsset;
    @Getter @Setter
    private OrgInfo org;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private TreeNode orgTreeEdit;
    @Getter @Setter
    private OrgInfo selectedDept;
    @Getter @Setter
    private List<BudgetPurchaseTemplateManage> budgetPurchaseTemplateManageList;
    @Getter @Setter
    private List<String> templateIds = new ArrayList<>();
  /*  @Getter @Setter
    private List<String> venderInfoStr = new ArrayList<>();*/
    @Getter  @Setter
    private VenderInfo venderInfoConfirm;
    @Getter  @Setter
    private VenderInfo companyInfoConfirm;
    @Getter  @Setter
    private List<VenderInfo> venderInfos = new ArrayList<>();
    @Getter  @Setter
    private List<VenderInfo> companyInfos = new ArrayList<>();
    @Getter @Setter
    private String editId;
    //当前设备列表
    @Getter @Setter
    private List<BudgetApplicationAsset> curApplicationBudgetAssets = new ArrayList<BudgetApplicationAsset>();
    @Getter @Setter
    private BudgetApplicationAsset dataListSelectApplicationAssetBudget ;
    // 弹窗列表
    @Getter @Setter
    private List<BudgetApplicationAsset> optionalBudgetApplications = new ArrayList<BudgetApplicationAsset>();
    // 已选择的设备列表
    @Getter @Setter
    private List<BudgetApplicationAsset> selectApplicationBudgetAssets = new ArrayList<BudgetApplicationAsset>();
    @Getter @Setter
    private StreamedContent exportFile;
    @Getter @Setter
    private List<V2_BlobObject> attachList;
    @Getter @Setter
    private List<VenderInfo> venderInfoList;
    @Getter @Setter
    private VenderInfo venderInfo;
    @Getter  @Setter
    private Integer tabIndex = 0;

    @Override
    protected void init() {
        AccessRecordsService accessRecordsService = WebUtil.getBean(AccessRecordsService.class);
        accessRecordsService.accessRecords(AccessRecordsFeature.BUDGET.getFeatureCode(), "/budget/purchase/List.xhtml", UserContextService.getCurrentUserAccount());
        venderInfoRepository = WebUtil.getBean(VenderInfoRepository.class);;
        purchaseVenderRepository = WebUtil.getBean(PurchaseVenderRepository.class);;
        dao = WebUtil.getBean(PurchaseOrderInfoRepository.class);
        budgetApplicationAssetRepository = WebUtil.getBean(BudgetApplicationAssetRepository.class);
        budgetApplicationRepository = WebUtil.getBean(BudgetApplicationRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        purchaseTemplateRepository = WebUtil.getBean(PurchaseTemplateRepository.class);
        purchaseOrderService = WebUtil.getBean(PurchaseOrderService.class);
        budgetPurchaseTemplateManageRepository = WebUtil.getBean(BudgetPurchaseTemplateManageRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        CustomConfigService ccService = WebUtil.getBean(CustomConfigService.class);
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        //todo 可以控制检索数据范围
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(account.getHospitalUID(), null);
        orgTree = initOrgTree(treeNode);
        String action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        this.editId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "editId");
        attachList = new ArrayList<>();
        try {
            budgetPurchaseTemplateManageList = budgetPurchaseTemplateManageRepository.findByTenantUID(account.getTenantUID());
            curApplicationBudgetAssets = new ArrayList<BudgetApplicationAsset>();
            if (PurchaseOrderController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
            } else if (PurchaseOrderController.EDIT_ACTION.equals(action)) {
                this.prepareEdit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected GenericRepositoryUUID<PurchaseOrderInfo> getDAO() {
        return dao;
    }

    public String backList() {
        return "List.xhtml?faces-redirect=true";
    }

    public void addBudgetAsset() {
        if (purchaseOrderService.checkDistinct(curApplicationBudgetAssets,selectApplicationBudgetAssets)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("同一个采购单所采购的设备名称需相同！"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        curApplicationBudgetAssets.addAll(selectApplicationBudgetAssets);
        calcTotalAmount(curApplicationBudgetAssets);
    }

    @Override
    protected Page<PurchaseOrderInfo> loadData(PageRequest pageRequest) {
        this.searchFilters = this.getSearchFilter(this.tabIndex);
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public List<SearchFilter> getSearchFilter(Integer tabIndex) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, account.getTenantUID()));
        if (tabIndex == 0) {//待招标弯沉
            updateSearchFilter(new SearchFilter("orderStep", SearchFilter.Operator.EQ, "ToConfirmedInfoTendered"));
            updateSearchFilter(new SearchFilter("stepStatus", SearchFilter.Operator.EQ, "ConfirmedInfoMeet"));
        } else if(tabIndex == 1){//招标完成
            updateSearchFilter(new SearchFilter("orderStep",  SearchFilter.Operator.EQ, "Complete"));
            updateSearchFilter(new SearchFilter("stepStatus",SearchFilter.Operator.EQ, "Complete"));
        }  else if(tabIndex == 5){//全部
            updateSearchFilter(new SearchFilter("orderStep",  SearchFilter.Operator.IN, Arrays.asList("Complete","ToConfirmedInfoTendered")));
        }
        queryByUserRole(org,searchFilters);
        return searchFilters;
    }

    private void queryByUserRole(OrgInfo org,List<SearchFilter> searchFiltersArg){
        if(org == null) return;
       // int switchArg = userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)?org.getOrgLevel():org.getOrgType();
        switch (org.getOrgLevel()) {
            case 3:
                // 院区
                searchFiltersArg.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, org.getSiteUID()));
                break;
            case 2:
                // 医院
                searchFiltersArg.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, org.getHospitalUID()));
                break;
            case 1:
                // 集团
                searchFiltersArg.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, org.getInstitutionUID()));
                break;
            default:
                // 租户
                searchFiltersArg.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, org.getTenantUID()));
        }
    }
    public void onTabChange(TabChangeEvent event) {
        selected = null;
        switch (event.getTab().getId()) {
            case "s1":
                tabIndex = 1;
                break;
            case "s5":
                tabIndex = 5;
                removeFilterOnField("orderStep");
                removeFilterOnField("stepStatus");
                break;
            default:
                tabIndex = 0;
                break;
        }
    }


    private void addSearchFilter() {
        if (null != searchForm.getDateStart()) {
            updateSearchFilter(new SearchFilter("createdDate", SearchFilter.Operator.GTE, searchForm.getDateStart()));
        }
        if (null != searchForm.getDateEnd()) {
            this.searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, new DateTime(searchForm.getDateEnd()).plusDays(1).toDate()));
        }
        if (StringUtils.isNotBlank(searchForm.getPurchaseNum())) {
            updateSearchFilter(new SearchFilter("purchaseNum", SearchFilter.Operator.LIKE, searchForm.getPurchaseNum()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicantName())) {
            updateSearchFilter(new SearchFilter("operator.name", SearchFilter.Operator.LIKE, searchForm.getApplicantName()));
        }
    }

    /**
     * 根据参数跳转
     *
     * @return
     */
    public String intoActionModel(String action){
        if(PurchaseOrderController.CREATE_ACTION.equals(action)){
            return UrlEncryptController.encodeUrlParam("Edit?action=" + PurchaseOrderController.CREATE_ACTION);
        }else if(PurchaseOrderController.EDIT_ACTION.equals(action)){
            return UrlEncryptController.encodeUrlParam("Edit?action=" + PurchaseOrderController.EDIT_ACTION + "&editId=" + this.selected.getId());
        }else {
            return null;
        }
    }

    public void updateStatus(String purchaseId , String status){
        PurchaseOrderInfo purchaseOrderInfo = dao.findById(purchaseId);
        purchaseOrderInfo.setStatus(status);
        purchaseOrderInfo.setLastModifiedDate(new Date());
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        PurchaseOrderInfo purchaseOrder = dao.save(purchaseOrderInfo);
        if(this.selected!=null && this.selected.getId().equals(purchaseOrder.getId())){
            this.selected = purchaseOrder;
        }
    }

    public void doubleClick(SelectEvent event) {
        String str = "action=" + PurchaseOrderController.EDIT_ACTION + "&editId=" + this.selected.getId();
        String url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String savePurchase() {
        purchaseOrderService.savePurchase(selected,curApplicationBudgetAssets,this.templateIds,account);
        return purchaseOrderService.backList();
    }

    public void submitPurchase() {
        //无可用的审批流
        boolean resultMultiLevelApproval = purchaseOrderService.checkMultiLevelApproval(selected);
        if(!resultMultiLevelApproval){
            WebUtil.addSuccessMessage("失败:无可用的确认流程,请检查后重试！");
            return;
        }
        selected.setOrderStep("ToConfirmedInfo");
        purchaseOrderService.submitPurchaseApplication(selected);
        this.selected = null;
        WebUtil.addSuccessMessage("采购单提交成功");
    }


    @Override
    public void delete() {
        PurchaseOrderInfo purchaseOrderInfo = dao.findById(selected.getId());
        boolean isOk = purchaseOrderService.deleteApplication(purchaseOrderInfo);
        selected = null;
        WebUtil.addSuccessMessage("采购单" + WebUtil.getMessage("Blank")
                + "删除" + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
    }

    public void search() {
        clearSearchFilterInitSearchFilter(this.selected);
    }

    public void resetSearch() {
        searchForm = new BudgetSearchForm();
        clearSearchFilterInitSearchFilter(this.selected);
    }

    private void clearSearchFilterInitSearchFilter(PurchaseOrderInfo selectPurchaseOrderInfo) {
        selectPurchaseOrderInfo = null;
        this.searchFilters = purchaseOrderService.initSearchFilter(this.searchFilters);
        addSearchFilter();
    }




    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject ) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_BUDGET_PURCHASE);
            if (Optional.ofNullable(selected.getId()).isPresent()) { // 新增
                blobObject.setBoUuid(selected.getId());
            }
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    private void calcTotalAmount(List<BudgetApplicationAsset> curApplicationBudgetAssets) {
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        int applicationQuantitySumResult = 0;
        for(BudgetApplicationAsset curApplicationBudget:curApplicationBudgetAssets){
            if(curApplicationBudget.getConfirmAmount()==null && curApplicationBudget.getConfirmQuantity()==null){
                totalAmount.set(totalAmount.get().add(curApplicationBudget.getOfflineApplicationAmount()==null?
                        new BigDecimal(0):curApplicationBudget.getOfflineApplicationAmount()));
                applicationQuantitySumResult += curApplicationBudget.getOfflineApplicationQuantity()==null?
                        0:curApplicationBudget.getOfflineApplicationQuantity();
            }else{
                totalAmount.set(totalAmount.get().add(curApplicationBudget.getConfirmAmount()==null?
                        new BigDecimal(0):curApplicationBudget.getConfirmAmount()));
                applicationQuantitySumResult += curApplicationBudget.getConfirmQuantity()==null?
                        0:curApplicationBudget.getConfirmQuantity();
            }
        }
        selected.setTotalPrice(totalAmount.get());
        selected.setPurchaseQuantity(applicationQuantitySumResult);
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
            node.setSelectable(Objects.equals(org.getOrgType(), 4));
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
        assetSearchForm.setDept(orgInfo);
    }
    public boolean curModelDisables(){
        return "View".equals(crudActionName)?true:false;
    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedDept = null;
        selected = new PurchaseOrderInfo();
        purchaseOrderService.initCreateObject(selected,account);
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        buildCurDetail(this.editId);
    }

    public void buildCurDetail(String id){
        this.selected = this.dao.findById(id);
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ,this.selected.getId()));
        this.curApplicationBudgetAssets = this.queryBudgetApplicationAsset();
        List<PurchaseTemplate> purchaseTemplateList = purchaseTemplateRepository.findByPurchaseId(id);
        if(CollectionUtils.isNotEmpty(purchaseTemplateList)){
            this.templateIds = purchaseTemplateList.stream().map(PurchaseTemplate::getTemplateId).collect(Collectors.toList());
        }
        //投标商拼接数据
        if(StringUtils.isNotBlank(this.selected.getVenderId())){
            this.venderInfoConfirm = venderInfoRepository.findById(this.selected.getVenderId());
        }
        if(StringUtils.isNotBlank(this.selected.getCompanyId())){
            this.companyInfoConfirm = venderInfoRepository.findById(this.selected.getCompanyId());
        }
        List<VenderInfo> venderInfoListRadioList = venderInfoRepository.queryByPurchaseId(this.selected.getId());

        if(CollectionUtils.isNotEmpty(venderInfoListRadioList)){
            this.venderInfos = venderInfoListRadioList.stream().filter(vi -> VenderInfo.BidType.Vender.toString().equals(vi.getBidType())).collect(Collectors.toList());
            this.companyInfos = venderInfoListRadioList.stream().filter(vi -> VenderInfo.BidType.Company.toString().equals(vi.getBidType())).collect(Collectors.toList());
        }
        /*List<PurchaseVender> purchaseVenderList = purchaseVenderRepository.findByPurchaseId(id);
        if(CollectionUtils.isNotEmpty(purchaseVenderList)){
            for(PurchaseVender purchaseVender :purchaseVenderList){
                StringBuilder stringBuilder = new StringBuilder();
                VenderInfo venderInfo = venderInfoRepository.findById(purchaseVender.getVenderId());
                if(venderInfo == null){
                    continue;
                }
                stringBuilder.append(VenderInfo.BidType.Company.toString().equals(venderInfo.getBidType())?venderInfo.getCompanyName()+" : ":venderInfo.getVenderName()+" : ");
                stringBuilder.append("[");
                List<V2_BlobObject> v2_blobObjectList = blobService.findBlobObjectsByBoUid(purchaseVender.getId());
                if(CollectionUtils.isNotEmpty(v2_blobObjectList)){
                    for(V2_BlobObject v2_blobObject :v2_blobObjectList){
                        stringBuilder.append(" "+v2_blobObject.getObjectName()+",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                stringBuilder.append("]");
                venderInfoStr.add(stringBuilder.toString());
            }
        }*/
    }

    public void preAddBudgetApplication() {
        resetAssetSearch();
    }

    public void delBudgetApplication() {
        //删除 明细信息 第一步先删除当前明细
        this.curApplicationBudgetAssets = this.curApplicationBudgetAssets.stream().filter(cur ->
                !cur.getId().equals(dataListSelectApplicationAssetBudget.getId())).collect(Collectors.toList());
        //第二步处理状态
        purchaseOrderService.delBudgetApplication(curApplicationBudgetAssets,dataListSelectApplicationAssetBudget,
                selected,account,templateIds);
        dataListSelectApplicationAssetBudget = null;
        calcTotalAmount(curApplicationBudgetAssets);
    }

    public void searchBudgetApplication() {
        this.dataListSelectApplicationAssetBudget = null;
        this.assetSearchFilters = purchaseOrderService.initSearchFilter(this.assetSearchFilters);
        addAssetSearchFilter();
        optionalBudgetApplications.clear();
        selectApplicationBudgetAssets.clear();
        this.optionalBudgetApplications = budgetApplicationAssetRepository.findBySearchFilter(assetSearchFilters,new Sort(Sort.Direction.DESC, "createdDate"));
    }

    public void resetAssetSearch() {
        assetSearchForm = new BudgetAssetSearchForm();
        searchBudgetApplication();
    }

    private void updateAssetSearchFilter(SearchFilter searchFilter) {
        removeAssetFilterOnField(searchFilter.fieldName);
        assetSearchFilters.add(searchFilter);
    }

    private void removeAssetFilterOnField(String fieldName) {
        assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> fieldName.equals(item.fieldName)).collect(Collectors.toList()));
    }

    private void addAssetSearchFilter() {
        if (null != assetSearchForm.getDept()) {
            updateAssetSearchFilter(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, assetSearchForm.getDept().getId()));
        }
        if (null != assetSearchForm.getApplicationNum()) {
            updateAssetSearchFilter(new SearchFilter("budgetApplication.applicationNum", SearchFilter.Operator.LIKE, assetSearchForm.getApplicationNum()));
        }
        if (null != assetSearchForm.getApplicationAssetCategory()) {
            updateAssetSearchFilter(new SearchFilter("applicationAssetCategory", SearchFilter.Operator.LIKE, assetSearchForm.getApplicationAssetCategory()));
        }
        if (null != assetSearchForm.getStandards()) {
            updateAssetSearchFilter(new SearchFilter("standards", SearchFilter.Operator.LIKE, assetSearchForm.getStandards()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetBrand())) {
            updateAssetSearchFilter(new SearchFilter("recommendedBrand", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetModel())) {
            updateAssetSearchFilter(new SearchFilter("model", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetName())) {
            updateAssetSearchFilter(new SearchFilter("applicationAssetName", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if (CollectionUtils.isNotEmpty(this.curApplicationBudgetAssets)) {//每次选择去掉之前的
            updateAssetSearchFilter(new SearchFilter("id", SearchFilter.Operator.NotIN, this.curApplicationBudgetAssets.stream().map(BudgetApplicationAsset::getId).collect(Collectors.toList())));
        }
        queryByUserRole(org,assetSearchFilters);
        updateAssetSearchFilter(new SearchFilter("budgetApplication.procureStatus",
                SearchFilter.Operator.IsNull,null));//采购状态未采购的数据
        updateAssetSearchFilter(new SearchFilter("budgetApplication.applicationStatus", SearchFilter.Operator.IN, Arrays.asList(6)));//状态已完成
        updateAssetSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));//未删除

    }

    public void exportAll()  {
        try {
            exportFile = purchaseOrderService.exportPurchaseZip(selected);
        }catch (IOException ex) {
            java.util.logging.Logger.getLogger(PurchaseOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPurchaseUrl() {
        return selected==null?"":purchaseOrderService.getBaseURL().replace("gateway","")+"reports/purchaseInfo?purchaseNum="+selected.getPurchaseNum()+
                "&assetName="+ (StringUtils.isNotBlank(selected.getApplicationAssetName())?selected.getApplicationAssetName():"");
    }

    public int approvaLevelPurchase(){
        Set<MultiLevelApproval> multiLevelApprovalSet = this.selected.getPurchaseApprovals();
        return CollectionUtils.isEmpty(multiLevelApprovalSet)?0:multiLevelApprovalSet.size();
    }

    public boolean editButtonDisplay(){
        boolean render = true;
        if("Enabled".equals(this.selected.getStatus()) || StringUtils.isNotBlank(this.selected.getOrderStep())){
            render = false;
        }
        return render;
    }
    public void prepareConfirm(String budgetAssetId) {
        this.confirmBudget = new BudgetApplicationAsset();
        this.budgetAssetId = budgetAssetId;
        BudgetApplicationAsset budgetApplicationAsset = budgetApplicationAssetRepository.findById(budgetAssetId);
        if(budgetApplicationAsset!=null){
            this.confirmBudget.setConfirmQuantity(budgetApplicationAsset.getConfirmQuantity());
            this.confirmBudget.setConfirmPrice(budgetApplicationAsset.getConfirmPrice());
        }
    }

    public void saveConfirmInfoBudgetApp() {
        purchaseOrderService.saveConfirmInfoBudgetApp(this.budgetAssetId,this.confirmBudget,this.selected);
        WebUtil.addSuccessMessage("操作成功！");
    }

    public List<BudgetApplicationAsset> queryBudgetApplicationAsset() {
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ, this.selected.getId()));
        this.curApplicationBudgetAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets,new Sort(Sort.Direction.DESC, "createdDate"));
        if (CollectionUtils.isNotEmpty(this.curApplicationBudgetAssets)) {
            this.curApplicationBudgetAssets.stream().filter(baa -> baa.getConfirmApplicationDeptId() != null).forEach(baa -> {
                OrgInfo confirmOrg = orgDao.findById(baa.getConfirmApplicationDeptId());
                baa.setConfirmApplicationDeptName(confirmOrg.getName());
            });
        }
        return this.curApplicationBudgetAssets;
    }

}
