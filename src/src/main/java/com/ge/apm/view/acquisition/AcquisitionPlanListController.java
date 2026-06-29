package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.budget.BudgetApplicationBapController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanListController extends GenericCRUDUUIDController<AcquisitionPlan> {

    private PlanFilingPeriodRepository planFilingPeriodRepository;
    private AcquisitionPlanRepository dao;
    private UserAccount loginUser;
    private UaaService uaaService;



    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        planFilingPeriodRepository = WebUtil.getBean(PlanFilingPeriodRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        loginUser = UserContextService.getCurrentUserAccount();
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
           /* List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));*/
            updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,  UserContextService.getCurrentUserAccount().getHospitalUID(), "1*2+"));
        }
        /*List<String> status = new ArrayList<>();
        status.add(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
        status.add(AcquisitionPlan.ApplicationStatusEnum.Rejected.name());
        // 方式二：静态方法精确判断
        if (UserContextService.getCurrentUserFunction().contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER.name())) {
            // 有权限
            status.add(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name());
        } else if (UserContextService.getCurrentUserFunction().contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP.name())) {
            status.add(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name());
        }else if (UserContextService.getCurrentUserFunction().contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_BUREAU_PARTY.name())){
            status.add(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name());
        }
        if(CollectionUtils.isNotEmpty(status)){
            updateSearchFilter(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, status));
        }*/
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


    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
