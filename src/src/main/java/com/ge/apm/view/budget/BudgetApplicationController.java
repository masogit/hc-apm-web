package com.ge.apm.view.budget;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BudgetApplicationRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private UserAccount account = UserContextService.getCurrentUserAccount();

    private Integer tabIndex = 0;

    // 可选择的设备列表
    private List<BudgetApplicationAsset> optionalBudgetAssets = new ArrayList<>();
    // 已选择的设备列表
    private List<BudgetApplicationAsset> selectedBudgetAssets = new ArrayList<>();

    private List<I18nMessage> asset2ncClassifyList = new ArrayList<>();

    private List<SearchFilter> assetSearchFilters;

    private BudgetApplicationRepository dao;
    private AssetInfoRepository assetDao;
    private I18nMessageRepository i18nDao;

    private BudgetPersistService persistService;

    private BudgetSearchForm searchForm = new BudgetSearchForm();
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();

    @Override
    protected void init() {
        dao = WebUtil.getBean(BudgetApplicationRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

        persistService = WebUtil.getBean(BudgetPersistService.class);
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
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, account.getTenantUID()));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void save() {
        if (selected.getId() != null) {
            BudgetApplication application = dao.findById(selected.getId());
            if (!BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING.equals(application.getApplicationStatus())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("BudgetAlreadyInProgress"));
                return;
            }
        }
        boolean isOk = persistService.save(selected);
        actionMessage(isOk);
    }

    @Override
    public void delete() {
        BudgetApplication application = dao.findById(selected.getId());
        if (!BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING.equals(application.getApplicationStatus())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("BudgetAlreadyInProgress"));
            return;
        }
        boolean isOk = persistService.delete(selected);
        selected = null;
        actionMessage(isOk);
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
        clearSearchFilter();
        addSearchFilter();
    }

    public void resetSearch() {
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
        if (null != searchForm.getDeptId()) {
            updateSearchFilter(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, searchForm.getDeptId()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetType())) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetType", SearchFilter.Operator.LIKE, searchForm.getAssetType()));
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
        if (null != searchForm.getApplicantId()) {
            updateSearchFilter(new SearchFilter("applicantId", SearchFilter.Operator.EQ, searchForm.getApplicantId()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetName())) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetName", SearchFilter.Operator.LIKE, searchForm.getAssetName()));
        }
        if (null != searchForm.getAsset2ndClassify()) {
            updateSearchFilter(new SearchFilter("budgetAssets.assetGroupSecondary", SearchFilter.Operator.EQ, searchForm.getAsset2ndClassify()));
        }
    }

    public void searchAsset() {
        clearAssetSearchFilter();
        addAssetSearchFilter();
        optionalBudgetAssets.clear();
        convertAssetToBudgetAsset(assetDao.findBySearchFilter(assetSearchFilters, new Sort(Sort.Direction.ASC, "id")));
    }

    public void resetAssetSearch() {
        clearAssetSearchFilter();
        assetSearchForm = new BudgetAssetSearchForm();
        addAssetSearchFilter();
    }

    private void clearAssetSearchFilter() {
        if (assetSearchFilters == null) {
            assetSearchFilters = new ArrayList<>();
        } else {
            assetSearchFilters.clear();
        }
    }

    private void addAssetSearchFilter() {
        if (StringUtils.isNotBlank(assetSearchForm.getAssetType())) {
            updateAssetSearchFilter(new SearchFilter("assetType", SearchFilter.Operator.LIKE, assetSearchForm.getAssetType()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetBrand())) {
            updateAssetSearchFilter(new SearchFilter("brand.name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getFunctionType())) {
            updateAssetSearchFilter(new SearchFilter("functionType", SearchFilter.Operator.EQ, assetSearchForm.getFunctionType()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetName())) {
            updateAssetSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if (null != assetSearchForm.getAsset2ndClassify()) {
            updateAssetSearchFilter(new SearchFilter("assetGroupSecondary", SearchFilter.Operator.EQ, assetSearchForm.getAsset2ndClassify()));
        }
    }

    private void convertAssetToBudgetAsset(List<AssetInfo> assetInfoList) {
        Date date = new Date();
        assetInfoList.forEach(asset -> {
            BudgetApplicationAsset budgetAsset = new BudgetApplicationAsset();
            budgetAsset.setAsset(asset);
            budgetAsset.setApplicationAssetId(asset.getId());

            budgetAsset.setNew(true);
            budgetAsset.setIsDeleted(0);
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
        optionalBudgetAssets.clear();
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
        selected.getBudgetAssets().remove(budgetAsset);

        calcTotalAmount();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedBudgetAssets.clear();
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
        selected.setTenantUID(account.getTenantUID());
        selected.setInstitutionUID(account.getInstitutionUID());
        selected.setHospitalUID(account.getHospitalUID());
        selected.setSiteUID(account.getSiteUID());
        selected.setCreatedBy(account.getLoginName());
        selected.setCreatedDate(date);
        selected.setLastModifiedBy(account.getLoginName());
        selected.setLastModifiedDate(date);
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        selectedBudgetAssets.clear();
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
        removeFilterOnField(searchFilter.fieldName);
        assetSearchFilters.add(searchFilter);
    }

    public List<Integer> budgetDeptList() {
        List<Integer> list = new ArrayList<>();
        return list;
    }

    public void onAssetTypeChange(AjaxBehaviorEvent event) {
        searchForm.setAsset2ndClassify(null);
        asset2ncClassifyList.clear();
        fillAsset2ndClassifyList();
    }

    private void fillAsset2ndClassifyList() {
        if (searchForm.getAssetType() == null) {
            asset2ncClassifyList = Collections.emptyList();
        } else {
            asset2ncClassifyList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_"
                    + searchForm.getAssetType(), UserContextService.getCurrentUserAccount().getSiteId());
        }
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

    public List<I18nMessage> getAsset2ncClassifyList() {
        return asset2ncClassifyList;
    }

    public void setAsset2ncClassifyList(List<I18nMessage> asset2ncClassifyList) {
        this.asset2ncClassifyList = asset2ncClassifyList;
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
}