package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.HospitalAreaClassification;
import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.budget.BudgetApplicationBapController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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
import javax.faces.event.AjaxBehaviorEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanListController extends GenericCRUDUUIDController<AcquisitionPlan> {

    private PlanFilingPeriodRepository planFilingPeriodRepository;
    private AcquisitionPlanRepository dao;
    private UserAccount loginUser;
    private UaaService uaaService;
    private AcquisitionPlanService acquisitionPlanService;

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
    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        planFilingPeriodRepository = WebUtil.getBean(PlanFilingPeriodRepository.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        loginUser = UserContextService.getCurrentUserAccount();
        getDeptOrgTree();
    }

    @Override
    protected Page<AcquisitionPlan> loadData(PageRequest pageRequest) {
        selected = null;
        this.setSearchFilter();
        Page<AcquisitionPlan> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        return pageResult;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));

        if (!this.hasFilterOnField("hospitalUID") ) {
            HospitalAreaClassification hospitalAreaClassification = acquisitionPlanService.getHospitalAreaClassification(loginUser);
            if(hospitalAreaClassification != null){
                List<String> hospitalUidList = acquisitionPlanService.getHospitals(hospitalAreaClassification, UserContextService.getCurrentUserAccount().getHospitalUID());
                if(CollectionUtils.isNotEmpty(hospitalUidList)){
                    updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, hospitalUidList, "1*"));
                }else{
                    updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,  UserContextService.getCurrentUserAccount().getHospitalUID(), "1*"));
                }
            }else {
                List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,  UserContextService.getCurrentUserAccount().getHospitalUID(), "1*2+"));
                updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ,  UserContextService.getCurrentUserAccount().getHospitalUID(), "1*2+"));
            }
        }
        searchFilters.removeAll(searchFilters.stream().filter(item -> "applicationDeptUid".equals(item.fieldName)).collect(Collectors.toList()));
        if(selectedDept != null){
            updateSearchFilter(new SearchFilter("applicationDeptUid", SearchFilter.Operator.EQ, selectedDept.getUid(), "1*"));
        }
    }

    public void treeSearchCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(treeNodeNameFilter)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4, treeNodeNameFilter);
        }
        orgTreeEdit = initOrgTree(treeNodeEdit);
    }

    public void clearDeptOrg(){
        selectedDept = null;
        treeNodeNameFilter = "";
    }


    public void getDeptOrgTree() {
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(loginUser.getOrgInfoUID(), loginUser.getUid(), 4);
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


    public String createUrl() throws ParseException {
        List<PlanFilingPeriod> planFilingPeriods = planFilingPeriodRepository.queryEffectConfig(loginUser.getTenantUID(), new Date());
        if(CollectionUtils.isEmpty(planFilingPeriods)){
            WebUtil.addErrorMessage("医疗设备购置计划申报期未开启，请检查后重试！");
            return "";
        }
        return UrlEncryptController.encodeUrlParam("Edit?action=Create");
    }

    public String editUrl() throws ParseException {
        return UrlEncryptController.encodeUrlParam("Edit?action=Edit&id="+selected.getId());
    }

    public void delete(){
        if(selected.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.TempSave.toString())){
            dao.delete(selected);
            selected = null;
            WebUtil.addSuccessMessage("删除成功！");
        }else{
            WebUtil.addErrorMessage("该购置计划已提交不能删除！");
        }
    }


    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
