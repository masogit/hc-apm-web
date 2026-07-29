package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.HospitalAreaClassification;
import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
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
import java.util.Set;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanListController extends GenericCRUDUUIDController<AcquisitionPlan> {

    private PlanFilingPeriodRepository planFilingPeriodRepository;
    private AcquisitionPlanRepository dao;
    private UserAccount loginUser;
    private UaaService uaaService;
    private AcquisitionPlanService acquisitionPlanService;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        planFilingPeriodRepository = WebUtil.getBean(PlanFilingPeriodRepository.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
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
