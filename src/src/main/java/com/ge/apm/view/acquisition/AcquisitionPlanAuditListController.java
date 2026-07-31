package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private AcquisitionPlanRepository dao;
    private UserAccount loginUser;

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

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);

        loginUser = UserContextService.getCurrentUserAccount();
        String query = WebUtil.getRequestParameter("query");
        if(StrUtil.isNotEmpty(query)){
            queryIndex = Integer.parseInt(query);
        }
        loadHospitalList();
        getDeptOrgTree();
    }

    @Override
    protected Page<AcquisitionPlan> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
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
                searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, statusList, "1*"));
            }
        }else if(queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("drugCenterUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("workGroupUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("bureauPartyUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
        }

        setSearchFilter();
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public void setSearchFilter() {
        searchFilters.removeAll(searchFilters.stream().filter(item -> "applicationDeptUid".equals(item.fieldName)).collect(Collectors.toList()));
        if(selectedDept != null){
            updateSearchFilter(new SearchFilter("applicationDeptUid", SearchFilter.Operator.EQ, selectedDept.getUid(), "1*"));
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



    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
