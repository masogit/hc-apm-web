package com.ge.apm.view.budget;

import com.ge.apm.dao.BudgetApplicationAssetRepository;
import com.ge.apm.dao.BudgetAssetInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.budget.BudgetPersistService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Yangfan, Liu
 */
@ViewScoped
@ManagedBean(name = "budgetAssetController")
public class BudgetAssetInfoController extends GenericCRUDUUIDController<BudgetAssetInfo> {
    private static Logger logger = LoggerFactory.getLogger(BudgetAssetInfoController.class);

    private UserAccount account = UserContextService.getCurrentUserAccount();

    private List<I18nMessage> budgetAssetGroupSecondaryList = new ArrayList<>();

    private BudgetAssetInfoRepository dao;
    private BudgetApplicationAssetRepository budgetAssetDao;
    private I18nMessageRepository i18nDao;

    private BudgetPersistService persistService;

    private BudgetAssetSearchForm searchForm = new BudgetAssetSearchForm();

    private OrgInfo org;
    private CustomConfigObject customConfig;
    private TreeNode orgTree;
    private TreeNode orgTreeEdit;
    private OrgInfo selectedDept;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BudgetAssetInfoRepository.class);
        budgetAssetDao = WebUtil.getBean(BudgetApplicationAssetRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

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
    protected GenericRepositoryUUID<BudgetAssetInfo> getDAO() {
        return dao;
    }

    @Override
    protected Page<BudgetAssetInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        queryScope(org);
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }
    private void queryScope(OrgInfo org){
        if(org == null) return;
        /*switch (org.getOrgType()) {
            case 4:
                // 科室
                updateSearchFilter(new SearchFilter("deptId", SearchFilter.Operator.EQ, org.getId()));
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
        }*/
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, org.getTenantUID()));

    }
    @Override
    public void save() {
        // 保存时设置科室
        //selected.setDept(selectedDept);
        //selected.setDeptId(selectedDept.getId());
        selected.setTenantUID(account.getTenantUID());
        selected.setInstitutionUID(account.getTenantUID());
        selected.setHospitalUID(account.getTenantUID());
        selected.setSiteUID(account.getTenantUID());
        boolean isOk = persistService.saveAsset(selected);
        actionMessage(isOk);
    }

    @Override
    public void delete() {
        /*int usingAssetsCount = budgetAssetDao.countByApplicationAssetId(selected.getId());
        if (usingAssetsCount > 0) {
            WebUtil.addErrorMessage(WebUtil.getMessage("BudgetAssetAlreadyInUsing"));
            return;
        }*/
        boolean isOk = persistService.deleteAsset(selected);
        selected = null;
        actionMessage(isOk);
    }

    private void actionMessage(boolean isOk) {
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetAsset") + WebUtil.getMessage("Blank")
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
        searchForm = new BudgetAssetSearchForm();
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
        if (null != searchForm.getAssetGroup()) {
            updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, searchForm.getAssetGroup()));
        }
        if (null != searchForm.getAssetGroupSecondary()) {
            updateSearchFilter(new SearchFilter("assetGroupSecondary", SearchFilter.Operator.EQ, searchForm.getAssetGroupSecondary()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetBrand())) {
            updateSearchFilter(new SearchFilter("assetBrand", SearchFilter.Operator.LIKE, searchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetModel())) {
            updateSearchFilter(new SearchFilter("assetModel", SearchFilter.Operator.EQ, searchForm.getAssetModel()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetName())) {
            updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, searchForm.getAssetName()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicationAssetCategory())) {
            updateSearchFilter(new SearchFilter("applicationAssetCategory", SearchFilter.Operator.LIKE, searchForm.getApplicationAssetCategory()));
        }
        if (StringUtils.isNotBlank(searchForm.getStandards())) {
            updateSearchFilter(new SearchFilter("standards", SearchFilter.Operator.LIKE, searchForm.getStandards()));
        }
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

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedDept = (OrgInfo) node.getData();
    }

    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        searchForm.setDept(orgInfo);
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

    public void onBudgetAssetGroupChange(AjaxBehaviorEvent event) {
        searchForm.setAssetGroupSecondary(null);
        budgetAssetGroupSecondaryList.clear();
        fillBudgetAssetGroupSecondaryList();
    }

    private void fillBudgetAssetGroupSecondaryList() {
        if (searchForm.getAssetGroup() == null) {
            budgetAssetGroupSecondaryList = Collections.emptyList();
        } else {
            budgetAssetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_"
                    + searchForm.getAssetGroup(), UserContextService.getCurrentUserAccount().getSiteId());
        }
    }

    public void clearSelected() {
        if (StringUtils.isEmpty(selected.getId())) {
            selected = null;
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedDept = null;
        Date date = new Date();
        selected = new BudgetAssetInfo();

        selected.setIsDeleted(0);
        selected.setCreatedBy(account.getLoginName());
        selected.setCreatedDate(date);
        selected.setLastModifiedBy(account.getLoginName());
        selected.setLastModifiedDate(date);
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        selectedDept = selected.getDept();
    }

    /*
     * getter and setter
     */
    public List<I18nMessage> getBudgetAssetGroupSecondaryList() {
        return budgetAssetGroupSecondaryList;
    }

    public void setBudgetAssetGroupSecondaryList(List<I18nMessage> budgetAssetGroupSecondaryList) {
        this.budgetAssetGroupSecondaryList = budgetAssetGroupSecondaryList;
    }

    public BudgetAssetSearchForm getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(BudgetAssetSearchForm searchForm) {
        this.searchForm = searchForm;
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