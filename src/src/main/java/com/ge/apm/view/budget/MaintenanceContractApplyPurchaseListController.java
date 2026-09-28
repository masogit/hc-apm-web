package com.ge.apm.view.budget;

import com.ge.apm.dao.MaintenanceContractApplyPurchaseRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.MaintenanceContractApplyPurchase;
import com.ge.apm.service.budget.BudgetApplicationExportService;
import com.ge.apm.service.budget.MaintenanceContractApplyPurchaseService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
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
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

@ViewScoped
@ManagedBean(name = "mcApplyPurchaseListController")
public class MaintenanceContractApplyPurchaseListController extends GenericCRUDUUIDController<MaintenanceContractApplyPurchase> {

    private static Logger logger = LoggerFactory.getLogger(MaintenanceContractApplyPurchaseListController.class);
    private MaintenanceContractApplyPurchaseRepository dao;
    private MaintenanceContractApplyPurchaseService mcApplyPurchaseService;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    @Getter
    @Setter
    private BudgetSearchForm searchForm = new BudgetSearchForm();
    @Getter  @Setter
    private Integer tabIndex = 0;
    @Getter  @Setter
    private TreeNode orgTree;
    @Getter  @Setter
    private OrgInfo org;
    @Getter @Setter
    private String searchDeptName;
    @Getter @Setter
    private StreamedContent exportFile;
    UserAccount userAccount;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(MaintenanceContractApplyPurchaseRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        mcApplyPurchaseService = WebUtil.getBean(MaintenanceContractApplyPurchaseService.class, "mcApplyPurchaseService");
        uaaService = WebUtil.getBean(UaaService.class);
        userAccount = UserContextService.getCurrentUserAccount();
        org = orgDao.findById(userAccount.getOrgInfoId());
        TreeNode treeNode =  uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(userAccount.getOrgInfoUID(), userAccount.getUid());
        orgTree = initOrgTree(treeNode);
        String tabIndexStr = (String) WebUtil.getRequestParameter("tabIndex");
        if(StringUtils.isNotBlank(tabIndexStr)){
            this.tabIndex = Integer.parseInt(tabIndexStr);
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

    public void doubleClick(){
        String str = "action=" + MaintenanceContractApplyPurchaseService.EDIT_ACTION + "&selectedId=" + this.selected.getId()+"&tabIndex="+tabIndex;
        String url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        searchForm.setDept(orgInfo);
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
            case "s5":
                tabIndex = 5;
                break;
            default:
                tabIndex = 0;
                break;
        }
    }

    public String pageForward(String action) throws ParseException {
        String url = "";
        if(MaintenanceContractApplyPurchaseService.CREATE_ACTION.equals(action)){
            url = UrlEncryptController.encodeUrlParam("Edit?action=" + MaintenanceContractApplyPurchaseService.CREATE_ACTION+"&tabIndex="+tabIndex);
        }else if(MaintenanceContractApplyPurchaseService.EDIT_ACTION.equals(action)){
            return UrlEncryptController.encodeUrlParam("Edit?action=" + MaintenanceContractApplyPurchaseService.EDIT_ACTION
                    + "&selectedId=" + this.selected.getId()+"&tabIndex="+tabIndex);
        }

        return url;
    }

    @Override
    public void delete() {
        mcApplyPurchaseService.deleteApply(selected.getId());
        selected = null;
    }

    @Override
    protected Page<MaintenanceContractApplyPurchase> loadData(PageRequest pageRequest) {
        getSearchFilter();
        addSearchFilterForSiteUid();
        return super.loadData(pageRequest);
    }

    public void getSearchFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        removeFilterOnField("applicationStatus");
        removeFilterOnField("applicationStep");
        if (tabIndex == 0) {//待办
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, MaintenanceContractApplyPurchase.ApplicationStepEnum.SubmitPending.name()));
        } else if(tabIndex == 1){//待审核
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, MaintenanceContractApplyPurchase.ApplicationStepEnum.ApprovalPending.name()));
        } else if(tabIndex == 2){//待过会论证
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, MaintenanceContractApplyPurchase.ApplicationStepEnum.MeetingReasoningPending.name()));
        } else if(tabIndex == 3){//采购信息确认
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, MaintenanceContractApplyPurchase.ApplicationStepEnum.PurchaseConfirmationPending.name()));
        } else if(tabIndex == 4){//完成
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, MaintenanceContractApplyPurchase.ApplicationStepEnum.Complete.name()));
        }else if(tabIndex == 5){//全部
            //searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1, 2, 3,4,5,6)));
        }
    }

    public void search(){
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

    public void addSearchFilter(){
        if(StrUtil.isNotEmpty(searchForm.getApplicationNum())){
            searchFilters.add(new SearchFilter("applicationNum", SearchFilter.Operator.LIKE, searchForm.getApplicationNum()));
        }
        if(searchForm.getDept() != null){
            searchFilters.add(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, searchForm.getDept().getId()));
        }
        if(searchForm.getDateStart() != null){
            searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.GTE, searchForm.getDateStart()));
        }
        if(searchForm.getDateEnd() != null){
            searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.LTE, searchForm.getDateEnd()));
        }
        if(StrUtil.isNotEmpty(searchForm.getApplicantName())){
            searchFilters.add(new SearchFilter("applicantName", SearchFilter.Operator.LIKE, searchForm.getApplicantName()));
        }
    }

    public void addSearchFilterForSiteUid(){
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(userAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("orgUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    public void exportAll()  {
        try {
            exportFile = mcApplyPurchaseService.exportZip(selected.getId());
        }catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    @Override
    protected GenericRepositoryUUID<MaintenanceContractApplyPurchase> getDAO() {
        return dao;
    }
}
