package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.AcquisitionPlanMapper;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.acquisition.form.AcquisitionPlanSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.*;
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
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanAuditListController extends GenericCRUDUUIDController<AcquisitionPlan> {

    @Getter @Setter
    private int tabIndex= 0;
    @Getter @Setter
    private Integer queryIndex = 0;
    private UaaService uaaService;
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nMessageRepository;
    private AcquisitionPlanRepository dao;
    AcquisitionPlanMapper acquisitionPlanMapper;
    private AcquisitionPlanService acquisitionPlanService;
    private UserAccount loginUser;
    @Getter@Setter
    private StreamedContent excelFile;
    @Getter @Setter
    private String filterApplicationDate = null;
    @Getter @Setter
    private OrgInfo selectedDept;
    @Getter @Setter
    private String treeNodeNameFilter;
    @Getter @Setter
    private TreeNode orgTreeEdit;
    @Getter @Setter
    private OrgInfo org;
    @Getter @Setter
    private List<OrgInfo> hospitalList;
    @Getter @Setter
    private String selectedHospital;
    @Getter @Setter
    private AcquisitionPlanSearchForm searchForm;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        acquisitionPlanMapper = WebUtil.getBean(AcquisitionPlanMapper.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);

        loginUser = UserContextService.getCurrentUserAccount();
        String query = WebUtil.getRequestParameter("query");
        if(StrUtil.isNotEmpty(query)){
            queryIndex = Integer.parseInt(query);
        }
        searchForm = new AcquisitionPlanSearchForm();
        loadHospitalList();
        getDeptOrgTree();
    }

    @Override
    protected Page<AcquisitionPlan> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        setSearchFilter();

        List<SearchFilter> searchFiltersQuery = new ArrayList<>();
        searchFiltersQuery.addAll(searchFilters);
        if(queryIndex == 1){
            searchFiltersQuery.removeAll(searchFilters.stream().filter(item -> "drugCenterUid".equals(item.fieldName)).collect(Collectors.toList()));
            searchFiltersQuery.removeAll(searchFilters.stream().filter(item -> "workGroupUid".equals(item.fieldName)).collect(Collectors.toList()));
            searchFiltersQuery.removeAll(searchFilters.stream().filter(item -> "bureauPartyUid".equals(item.fieldName)).collect(Collectors.toList()));
            searchFiltersQuery.add(new SearchFilter("userIds", SearchFilter.Operator.EQ, loginUser.getUid(), "1*"));
        }

        List<AcquisitionPlan> list = acquisitionPlanMapper.queryAcquisitionPlanListForPage(searchFiltersQuery, (int) pageRequest.getOffset(), pageRequest.getPageSize());
        String c = acquisitionPlanMapper.queryAcquisitionPlanListForPageCount(searchFiltersQuery);
        int count = 0;
        if(StrUtil.isNotEmpty(c)){
            count = Integer.parseInt(c);
        }

        return new PageImpl<>(list, pageRequest, count);
    }

    public void setSearchFilter() {
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));
        if(tabIndex == 0){
            String currentYear = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
            this.updateSearchFilter(new SearchFilter("applicationYear", SearchFilter.Operator.EQ, currentYear, "1*"));
        } else if (tabIndex == 1) {
            String currentYear = String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
            this.updateSearchFilter(new SearchFilter("applicationYear", SearchFilter.Operator.NE, currentYear, "1*"));
        } else {
            this.removeFilterOnField("applicationYear");
        }

        if(queryIndex == 0){
            /*this.updateSearchFilter(new SearchFilter("pendAuditUid", SearchFilter.Operator.LIKE, loginUser.getUid(), "1*"));*/
            Set<String> userFunctions = UserContextService.getCurrentUserFunction();
            List<String> statusList = new ArrayList<>();
            if (userFunctions.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER.name())) {
                statusList.add(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name());
            }
            if (userFunctions.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP.name())) {
                statusList.add(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name());
            }
            if (userFunctions.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_BUREAU_PARTY.name())) {
                statusList.add(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name());
            }
            if (!statusList.isEmpty()) {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "applicationStatus".equals(item.fieldName)).collect(Collectors.toList()));
                searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, statusList, "1*"));
            }
        }else if(queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("drugCenterUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("workGroupUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("bureauPartyUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
        }


        searchFilters.removeAll(searchFilters.stream().filter(item -> "applicationDeptUid".equals(item.fieldName)).collect(Collectors.toList()));
        if(selectedDept != null){
            updateSearchFilter(new SearchFilter("applicationDeptUid", SearchFilter.Operator.EQ, selectedDept.getUid(), "1*"));
        }
    }

    private void addSearchFilter() {
        if(searchForm != null){
            if(StrUtil.isNotEmpty(searchForm.getApplicationHospitalName())){
                updateSearchFilter(new SearchFilter("applicationHospitalName", SearchFilter.Operator.LIKE, searchForm.getApplicationHospitalName(), "1*"));
            }
            if(StrUtil.isNotEmpty(searchForm.getAssetName())){
                updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE, searchForm.getAssetName(), "1*"));
            }
            if(StrUtil.isNotEmpty(searchForm.getApplicationType())){
                updateSearchFilter(new SearchFilter("applicationStatus", SearchFilter.Operator.EQ, searchForm.getApplicationType(), "1*"));
            }
            if(searchForm.getBudgetTotalAmountBegin() != null){
                searchFilters.add(new SearchFilter("budgetTotalAmount", SearchFilter.Operator.GTE, searchForm.getBudgetTotalAmountBegin(), "1*"));
            }
            if(searchForm.getBudgetTotalAmountEnd() != null){
                searchFilters.add(new SearchFilter("budgetTotalAmount", SearchFilter.Operator.LTE, searchForm.getBudgetTotalAmountEnd(), "1*"));
            }
        }
    }

    public void search() {
        searchFilters.clear();
        addSearchFilter();
    }

    public void resetSearch() {
        selected = null;
        clearSearchFilter();
        searchForm = new AcquisitionPlanSearchForm();
        addSearchFilter();
    }

    private void clearSearchFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
    }

        //tab切换
    public void onTabChange(TabChangeEvent event) {
        switch (event.getTab().getId()) {
            case "current":
                tabIndex = 0;
                break;
            case "history":
                tabIndex = 1;
                break;
            default:
                tabIndex = 0;
                break;
        }
        selected = null;
    }

    public String auditUrl(String id){
        return UrlEncryptController.encodeUrlParam("AuditDetail?action=Edit&id="+ id+"&action=Audit");
    }

    public void viewUrl(String id){
        String url = "action=View&id="
                + selected.getId();
        url = "AuditDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void onSelectOrder(String selectedId) throws IOException {

    }

    public void loadHospitalList(){
        hospitalList = orgInfoRepository.getByHospitalByTenant(loginUser.getTenantUID());

    }

    public void hospitalListChange(){
        treeSearchCondition();
    }


    public void treeSearchCondition(){
        TreeNode treeNodeEdit = null;
        String parentUid = loginUser.getTenantUID();
        if(selectedHospital != null){
            parentUid = selectedHospital;
        }
        if(StrUtil.isEmpty(treeNodeNameFilter)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptSelected(parentUid,  4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptSelected(parentUid, 4, treeNodeNameFilter);
        }
        orgTreeEdit = initOrgTree(treeNodeEdit);
    }

    public void clearDeptOrg(){
        selectedDept = null;
        treeNodeNameFilter = "";
    }


    public void getDeptOrgTree() {
        String parentUid = loginUser.getTenantUID();
        if(selectedHospital != null){
            parentUid = selectedHospital;
        }
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptSelected(parentUid,  4);
        orgTreeEdit =  initOrgTree(treeNodeEdit);
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

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedDept = (OrgInfo) node.getData();
    }

    public void onRowToggle(ToggleEvent event) {
        AcquisitionPlan acquisitionPlan = (AcquisitionPlan) event.getData();
        // 判断当前是展开还是折叠
        if (event.getVisibility() == Visibility.VISIBLE) {
            // 展开时：去数据库查询详情，并赋值给当前对象
            List<SearchFilter> ns = new ArrayList<>();
            ns.addAll(searchFilters);
            ns.add(new SearchFilter("applicationHospitalUid", SearchFilter.Operator.EQ, acquisitionPlan.getApplicationHospitalUid()));
            ns.add(new SearchFilter("applicationYear", SearchFilter.Operator.EQ, acquisitionPlan.getApplicationYear()));
            Sort sort = new Sort(Sort.Direction.DESC, "applicationNo");
            List<AcquisitionPlan> acquisitionPlans = dao.findBySearchFilter(searchFilters, sort);
            acquisitionPlan.setList(acquisitionPlans);
        } else {
            // 折叠时：可选操作，清空数据以释放内存
            //car.setDetails(null);
        }
    }

    public void exportExcel() throws IOException {
        Sort sort = new Sort(Sort.Direction.DESC, "applicationNo");
        List<AcquisitionPlan> list = dao.findBySearchFilter(searchFilters, sort);

        InputStream inputStream = acquisitionPlanService.exportExcel(list);
        excelFile = new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", "Acquisition_Export.xlsx");

    }

    public List<I18nMessage> getI18nMessages() {
        List<I18nMessage> i18nMessages = i18nMessageRepository.findI18nByType("AcquisitionApplicationStatus");
        Map<String, I18nMessage> i18Map = new HashMap<>();
        for (I18nMessage i18nMessage : i18nMessages) {
            i18Map.put(i18nMessage.getMsgKey(), i18nMessage);
        }
        List<I18nMessage> result = new ArrayList<>();
        List<String> statusList = new ArrayList<>();
        statusList.add("TempSave");
        statusList.add("DrugCenterPending");
        statusList.add("WorkGroupPending");
        statusList.add("BureauPartyPending");
        statusList.add("Approved");
        statusList.add("Rejected");
        statusList.add("Cancel");
        for(String key : statusList){
            I18nMessage i18nMessage = i18Map.get(key);
            result.add(i18nMessage);
        }
        return result;
    }
    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
