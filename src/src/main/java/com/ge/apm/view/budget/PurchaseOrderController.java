
package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.budget.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.budget.BudgetApplicationExportService;
import com.ge.apm.service.budget.PurchaseOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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
    public final static String VIEW_ACTION = "View";

    private BlobFileService blobService;
    /**
     * 编辑数据主键
     */
    private String editId;
    //当前设备列表
    private List<BudgetApplicationAsset> curApplicationBudgetAssets = new ArrayList<BudgetApplicationAsset>();
    private BudgetApplicationAsset dataListSelectApplicationAssetBudget ;

    // 弹窗列表
    private List<BudgetApplicationAsset> optionalBudgetApplications = new ArrayList<BudgetApplicationAsset>();
    // 已选择的设备列表
    private List<BudgetApplicationAsset> selectApplicationBudgetAssets = new ArrayList<BudgetApplicationAsset>();

    private List<V2_BlobObject> attachList;

    private List<SearchFilter> assetSearchFilters;

    private PurchaseOrderInfoRepository dao;

    private BudgetApplicationAssetRepository budgetApplicationAssetRepository;

    private BudgetApplicationRepository budgetApplicationRepository;
    private BudgetAssetInfoRepository budgetAssetDao;
    private BlobObjectRepository blobObjectRepository;

    private PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;

    private UserContextService userContextService;
    private PurchaseOrderService purchaseOrderService;
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
        dao = WebUtil.getBean(PurchaseOrderInfoRepository.class);
        budgetApplicationAssetRepository = WebUtil.getBean(BudgetApplicationAssetRepository.class);
        budgetApplicationRepository = WebUtil.getBean(BudgetApplicationRepository.class);
        budgetAssetDao = WebUtil.getBean(BudgetAssetInfoRepository.class);
        purchaseBudgetConfigRepository = WebUtil.getBean(PurchaseBudgetConfigRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        budgetApplicationExportService = WebUtil.getBean(BudgetApplicationExportService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        purchaseOrderService = WebUtil.getBean(PurchaseOrderService.class);
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        CustomConfigService ccService = WebUtil.getBean(CustomConfigService.class);
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        //todo 可以控制检索数据范围
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(account.getHospitalUID(), null);
        orgTree = initOrgTree(treeNode);
        String action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        this.editId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "editId");
        attachList = new ArrayList<>();
        try {
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

    public List<V2_BlobObject> getAttachList() {
        return attachList;
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
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
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

    public void doubleClick(SelectEvent event) {
        String str = "action=" + PurchaseOrderController.EDIT_ACTION + "&editId=" + this.selected.getId();
        String url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String savePurchase() {
        purchaseOrderService.handleCommonField(selected,account);
        purchaseOrderService.savePurchase(selected,curApplicationBudgetAssets,this.attachList);
        return purchaseOrderService.backList();
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
            totalAmount.set(totalAmount.get().add(curApplicationBudget.getApplicationAmount()));
            applicationQuantitySumResult += curApplicationBudget.getApplicationQuantity();

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
        assetSearchForm.setDept(orgInfo);
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

    @Override
    public void prepareView() {
        crudActionName = "View";
        buildCurDetail(this.editId);
    }

    public void buildCurDetail(String id){
        this.selected = this.dao.findById(id);
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ,this.selected.getId()));
        this.curApplicationBudgetAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets);
        this.attachList = blobObjectRepository.findByBoUuid(this.selected.getId());

    }



    public void preAddBudgetApplication() {
        resetAssetSearch();
    }

    public void delBudgetApplication() {
        //删除 明细信息 第一步先删除当前明细
        this.curApplicationBudgetAssets = this.curApplicationBudgetAssets.stream().filter(cur ->
                !cur.getId().equals(dataListSelectApplicationAssetBudget.getId())).collect(Collectors.toList());
        //第二步处理状态
        if(dataListSelectApplicationAssetBudget.getBudgetApplication() !=null &&
                StringUtils.isNotBlank(dataListSelectApplicationAssetBudget.getBudgetApplication().getProcureStatus()) &&
                StringUtils.isNotBlank(dataListSelectApplicationAssetBudget.getBudgetApplication().getPurchaseId())){
            purchaseOrderService.setPurchaseInfoNull(dataListSelectApplicationAssetBudget.getBudgetApplication());
            budgetApplicationRepository.save(dataListSelectApplicationAssetBudget.getBudgetApplication());
        }
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
        updateAssetSearchFilter(new SearchFilter("budgetApplication.procureStatus",
                SearchFilter.Operator.IsNull,null));//采购状态未采购的数据
        updateAssetSearchFilter(new SearchFilter("budgetApplication.applicationStatus", SearchFilter.Operator.IN, Arrays.asList(6)));//状态已完成
        updateAssetSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));//未删除

    }

    @Getter
    @Setter
    private StreamedContent exportFile;


    public void exportAll()  {
        try {
            exportFile = purchaseOrderService.exportPurchaseZip(selected);
        }catch (IOException ex) {
            java.util.logging.Logger.getLogger(PurchaseOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void setAttachList(List<V2_BlobObject> attachList) {
        this.attachList = attachList;
    }


    public List<BudgetApplicationAsset> getOptionalBudgetApplications() {
        return optionalBudgetApplications;
    }

    public void setOptionalBudgetApplications(List<BudgetApplicationAsset> optionalBudgetApplications) {
        this.optionalBudgetApplications = optionalBudgetApplications;
    }

    public List<BudgetApplicationAsset> getCurApplicationBudgetAssets() {
        return curApplicationBudgetAssets;
    }

    public void setCurApplicationBudgetAssets(List<BudgetApplicationAsset> curApplicationBudgetAssets) {
        this.curApplicationBudgetAssets = curApplicationBudgetAssets;
    }

    public BudgetApplicationAsset getDataListSelectApplicationAssetBudget() {
        return dataListSelectApplicationAssetBudget;
    }

    public void setDataListSelectApplicationAssetBudget(BudgetApplicationAsset dataListSelectApplicationAssetBudget) {
        this.dataListSelectApplicationAssetBudget = dataListSelectApplicationAssetBudget;
    }

    public List<BudgetApplicationAsset> getSelectApplicationBudgetAssets() {
        return selectApplicationBudgetAssets;
    }

    public void setSelectApplicationBudgetAssets(List<BudgetApplicationAsset> selectApplicationBudgetAssets) {
        this.selectApplicationBudgetAssets = selectApplicationBudgetAssets;
    }
}
