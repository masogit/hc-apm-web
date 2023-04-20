package com.ge.apm.view.budget;

import com.ge.apm.dao.BudgetApplicationRepository;
import com.ge.apm.dao.BudgetAssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.SysRoleConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Yangfan, Liu
 */
@ViewScoped
@ManagedBean(name = "budgetController")
public class BudgetApplicationController extends GenericCRUDUUIDController<BudgetApplication> {
    private static Logger logger = LoggerFactory.getLogger(BudgetApplicationController.class);

    private Page<BudgetApplication> emptyPage = new PageImpl<>(Collections.emptyList());
    private UserAccount account = UserContextService.getCurrentUserAccount();

    private Integer tabIndex = 0;

    // 可选择的设备列表
    private List<BudgetApplicationAsset> optionalBudgetAssets = new ArrayList<>();
    // 已选择的设备列表
    private List<BudgetApplicationAsset> selectedBudgetAssets = new ArrayList<>();
    // 已删除的设备列表
    private List<BudgetApplicationAsset> deletedBudgetAssets = new ArrayList<>();

    // 预算筛选
    private List<I18nMessage> budgetFilterAssetGroupSecondaryList = new ArrayList<>();
    // 预算设备筛选
    private List<I18nMessage> assetFilterAssetGroupSecondaryList = new ArrayList<>();
    // 新增预算设备
    private List<I18nMessage> assetCreateAssetGroupSecondaryList = new ArrayList<>();

    private List<SearchFilter> assetSearchFilters;

    private BudgetApplicationRepository dao;
    private BudgetAssetInfoRepository budgetAssetDao;
    private I18nMessageRepository i18nDao;

    private UserContextService userContextService;
    private BudgetPersistService persistService;

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
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

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
    }

    @Override
    protected GenericRepositoryUUID<BudgetApplication> getDAO() {
        return dao;
    }

    @Override
    protected Page<BudgetApplication> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if (tabIndex == 0) {
            updateSearchFilter(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1, 2, 3, 4)));
        } else {
            updateSearchFilter(new SearchFilter("applicationStatus", SearchFilter.Operator.EQ, tabIndex));
        }
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        if (userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)
                || userContextService.hasRole(SysRoleConstant.ROLE_DEPT_HEAD)) {
            // 临床科室主任, 医学工程处主任
            queryScope(org);
        } else if (userContextService.hasRole(SysRoleConstant.ROLE_CLINICAL_STAFF)) {
            // 临床科室人员
            updateSearchFilter(new SearchFilter("applicantId", SearchFilter.Operator.EQ, account.getId()));
        } else {
            return emptyPage;
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void save() {
        // 保存时设置科室
        selected.setApplicationDept(selectedDept);
        selected.setApplicationDeptId(selectedDept.getId());

        selected.setTenantUID(selectedDept.getTenantUID());
        selected.setInstitutionUID(selectedDept.getInstitutionUID());
        selected.setHospitalUID(selectedDept.getHospitalUID());
        selected.setSiteUID(selectedDept.getSiteUID());

        if (selected.getId() != null) {
            BudgetApplication application = dao.findById(selected.getId());
            if (!BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING.equals(application.getApplicationStatus())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("BudgetAlreadyInProgress"));
                return;
            }
        }
        boolean isOk = persistService.saveApplication(selected, deletedBudgetAssets);
        actionMessage(isOk);
    }

    @Override
    public void delete() {
        BudgetApplication application = dao.findById(selected.getId());
        if (!BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING.equals(application.getApplicationStatus())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("BudgetAlreadyInProgress"));
            return;
        }
        boolean isOk = persistService.deleteApplication(selected);
        selected = null;
        actionMessage(isOk);
    }

    public void saveAsset() {
        // 保存时设置科室
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
    }

    private void actionMessage(boolean isOk) {
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetApplication") + WebUtil.getMessage("Blank")
                + WebUtil.getMessage(crudActionName) + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
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
        if (null != searchForm.getDept()) {
            OrgInfo orgInfo = searchForm.getDept();
            queryScope(orgInfo);
        }
        if (null != searchForm.getDateStart()) {
            updateSearchFilter(new SearchFilter("applicationDate", SearchFilter.Operator.GTE, searchForm.getDateStart()));
        }
        if (null != searchForm.getDateEnd()) {
            this.searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.LT, new DateTime(searchForm.getDateEnd()).plusDays(1).toDate()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicationNum())) {
            updateSearchFilter(new SearchFilter("applicationNum", SearchFilter.Operator.LIKE, searchForm.getApplicationNum()));
        }
        if (null != searchForm.getAssetGroup()) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetGroup", SearchFilter.Operator.EQ, searchForm.getAssetGroup()));
        }
        if (null != searchForm.getAssetGroupSecondary()) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetGroupSecondary", SearchFilter.Operator.EQ, searchForm.getAssetGroupSecondary()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicantName())) {
            updateSearchFilter(new SearchFilter("applicant.name", SearchFilter.Operator.EQ, searchForm.getApplicantName()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetName())) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetName", SearchFilter.Operator.LIKE, searchForm.getAssetName()));
        }
    }
    private void queryScope(OrgInfo org){
        if(org == null) return;
        switch (org.getOrgType()) {
            case 4:
                // 科室
                updateSearchFilter(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, org.getId()));
                break;
            case 3:
                // 院区
                updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, org.getSiteUID()));
                break;
            case 2:
                // 医院
                updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, org.getHospitalUID()));
                break;
            case 1:
                // 集团
                updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, org.getInstitutionUID()));
                break;
            default:
                // 租户
                updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, org.getTenantUID()));
        }
    }
    public void searchAsset() {
        clearAssetSearchFilter();
        addAssetSearchFilter();
        optionalBudgetAssets.clear();
        List<BudgetAssetInfo> assetInfoList = budgetAssetDao.findBySearchFilter(assetSearchFilters);
        fillOptionalAssets(assetInfoList);
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

    private void addAssetSearchFilter() {
        if (null != assetSearchForm.getAssetGroup()) {
            updateAssetSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetSearchForm.getAssetGroup()));
        }
        if (null != assetSearchForm.getAssetGroupSecondary()) {
            updateAssetSearchFilter(new SearchFilter("assetGroupSecondary", SearchFilter.Operator.EQ, assetSearchForm.getAssetGroupSecondary()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetBrand())) {
            updateAssetSearchFilter(new SearchFilter("assetBrand", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetModel())) {
            updateAssetSearchFilter(new SearchFilter("assetModel", SearchFilter.Operator.EQ, assetSearchForm.getAssetModel()));
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

    private void fillOptionalAssets(List<BudgetAssetInfo> assetInfoList) {
        Date date = new Date();
        assetInfoList.forEach(asset -> {
            BudgetApplicationAsset budgetAsset = new BudgetApplicationAsset();
            budgetAsset.setAsset(asset);
            budgetAsset.setApplicationAssetId(asset.getId());
            budgetAsset.setApplicationPrice(asset.getPrice());

            budgetAsset.setNew(true);
            budgetAsset.setTenantUID(account.getTenantUID());
            budgetAsset.setInstitutionUID(account.getInstitutionUID());
            budgetAsset.setHospitalUID(account.getHospitalUID());
            budgetAsset.setSiteUID(account.getSiteUID());
            budgetAsset.setCreatedBy(account.getLoginName());
            budgetAsset.setCreatedDate(date);
            budgetAsset.setLastModifiedBy(account.getLoginName());
            budgetAsset.setLastModifiedDate(date);

            optionalBudgetAssets.add(budgetAsset);
        });
    }

    private String generateApplicationNum() {
        LocalDateTime date = LocalDateTime.now(ZoneId.of("GMT+8"));
        return BudgetConstant.BUDGET_APPLICATION_NUM_PREFIX.concat(date.format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS")));
    }

    public void calcAmount(BudgetApplicationAsset budgetAsset) {
        budgetAsset.setApplicationAmount(budgetAsset.getApplicationPrice()
                .multiply(new BigDecimal(budgetAsset.getApplicationQuantity()))
                .setScale(2, RoundingMode.HALF_UP));

        calcTotalAmount();
    }

    private void calcTotalAmount() {
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        selected.getBudgetAssets().forEach(a -> totalAmount.set(totalAmount.get().add(a.getApplicationAmount())));
        // 更新总金额
        selected.setTotalAmount(totalAmount.get());
    }

    public void preAddBudgetAsset() {
        resetAssetSearch();
    }

    public void addBudgetAsset() {
        selectedBudgetAssets.addAll(optionalBudgetAssets.stream().filter(a -> a.getApplicationQuantity() != null && a.getApplicationQuantity() > 0
                && a.getApplicationPrice() != null && a.getApplicationPrice().compareTo(BigDecimal.ZERO) > 0)
                .peek(a -> {
                    a.setId(UUID.randomUUID().toString());
                    a.setApplicationAmount(a.getApplicationPrice().multiply(new BigDecimal(a.getApplicationQuantity())).setScale(2, RoundingMode.HALF_UP));
                }).collect(Collectors.toList()));
        selected.getBudgetAssets().addAll(selectedBudgetAssets);

        calcTotalAmount();
    }

    public void delBudgetAsset(BudgetApplicationAsset budgetAsset) {
        deletedBudgetAssets.add(budgetAsset);
        selected.getBudgetAssets().remove(budgetAsset);

        calcTotalAmount();
    }

    public void prepareCreateAsset() {
        Date date = new Date();
        selectedAsset = new BudgetAssetInfo();

        selectedAsset.setIsDeleted(0);
        selectedAsset.setCreatedBy(account.getLoginName());
        selectedAsset.setCreatedDate(date);
        selectedAsset.setLastModifiedBy(account.getLoginName());
        selectedAsset.setLastModifiedDate(date);
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

    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        searchForm.setDept(orgInfo);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (selected.getApplicationDeptId() != null && !orgInfo.getId().equals(selected.getApplicationDeptId())) {
            selected.getBudgetAssets().clear();
        }
        selectedDept = orgInfo;
    }

    public boolean show(String fieldKey) {
        if (customConfig.getAssetsEditpageBasicFields().contains(fieldKey)) {
            return true;
        }
        if (customConfig.getAssetsEditpageCatalogFields().contains(fieldKey)) {
            return true;
        }
        if (customConfig.getAssetsEditpageCodesFields().contains(fieldKey)) {
            return true;
        }
        if (customConfig.getAssetsEditpageDateFields().contains(fieldKey)) {
            return true;
        }
        if (customConfig.getAssetsEditpageFinicialFields().contains(fieldKey)) {
            return true;
        }
        return customConfig.getAssetsEditpageStatusFields().contains(fieldKey);
    }

    public void onBudgetFilterAssetGroupChange(AjaxBehaviorEvent event) {
        searchForm.setAssetGroupSecondary(null);
        budgetFilterAssetGroupSecondaryList.clear();
        fillBudgetFilterAssetGroupSecondaryList();
    }

    private void fillBudgetFilterAssetGroupSecondaryList() {
        if (searchForm.getAssetGroup() == null) {
            budgetFilterAssetGroupSecondaryList = Collections.emptyList();
        } else {
            budgetFilterAssetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_"
                    + searchForm.getAssetGroup(), UserContextService.getCurrentUserAccount().getSiteId());
        }
    }

    public void onAssetFilterAssetGroupChange(AjaxBehaviorEvent event) {
        assetSearchForm.setAssetGroupSecondary(null);
        assetFilterAssetGroupSecondaryList.clear();
        fillAssetFilterAssetGroupSecondaryList();
    }

    private void fillAssetFilterAssetGroupSecondaryList() {
        if (assetSearchForm.getAssetGroup() == null) {
            assetFilterAssetGroupSecondaryList = Collections.emptyList();
        } else {
            assetFilterAssetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_"
                    + assetSearchForm.getAssetGroup(), UserContextService.getCurrentUserAccount().getSiteId());
        }
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
        selectedBudgetAssets.clear();
        selectedDept = null;
        Date date = new Date();
        selected = new BudgetApplication();
        selected.setApplicationNum(generateApplicationNum());
        selected.setApplicant(account);
        selected.setApplicantId(account.getId());
        selected.setApplicationDate(new Date());
        selected.setApplicationStatus(BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING);
        selected.setApprovalProgress(0);
        selected.setBudgetAssets(new HashSet<>());
        selected.setBudgetApprovals(new HashSet<>());

        selected.setIsDeleted(0);
        selected.setCreatedBy(account.getLoginName());
        selected.setCreatedDate(date);
        selected.setLastModifiedBy(account.getLoginName());
        selected.setLastModifiedDate(date);
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        selectedBudgetAssets.clear();
        selectedDept = selected.getApplicationDept();
    }

    @Override
    public void prepareView() {
        crudActionName = "View";
    }

    @Override
    public void prepareDelete() {
        crudActionName = "Delete";
    }

    private void updateAssetSearchFilter(SearchFilter searchFilter) {
        removeAssetFilterOnField(searchFilter.fieldName);
        assetSearchFilters.add(searchFilter);
    }

    private void removeAssetFilterOnField(String fieldName) {
        assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> fieldName.equals(item.fieldName)).collect(Collectors.toList()));
    }

    /*
     * getter and setter
     */
    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public List<BudgetApplicationAsset> getOptionalBudgetAssets() {
        return optionalBudgetAssets;
    }

    public void setOptionalBudgetAssets(List<BudgetApplicationAsset> optionalBudgetAssets) {
        this.optionalBudgetAssets = optionalBudgetAssets;
    }

    public List<I18nMessage> getBudgetFilterAssetGroupSecondaryList() {
        return budgetFilterAssetGroupSecondaryList;
    }

    public void setBudgetFilterAssetGroupSecondaryList(List<I18nMessage> budgetFilterAssetGroupSecondaryList) {
        this.budgetFilterAssetGroupSecondaryList = budgetFilterAssetGroupSecondaryList;
    }

    public List<I18nMessage> getAssetFilterAssetGroupSecondaryList() {
        return assetFilterAssetGroupSecondaryList;
    }

    public void setAssetFilterAssetGroupSecondaryList(List<I18nMessage> assetFilterAssetGroupSecondaryList) {
        this.assetFilterAssetGroupSecondaryList = assetFilterAssetGroupSecondaryList;
    }

    public List<I18nMessage> getAssetCreateAssetGroupSecondaryList() {
        return assetCreateAssetGroupSecondaryList;
    }

    public void setAssetCreateAssetGroupSecondaryList(List<I18nMessage> assetCreateAssetGroupSecondaryList) {
        this.assetCreateAssetGroupSecondaryList = assetCreateAssetGroupSecondaryList;
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
}