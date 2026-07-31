package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class PlanFilingPeriodController extends GenericCRUDUUIDController<PlanFilingPeriod> {


    @Getter
    @Setter
    private PlanFilingPeriod searchForm = new PlanFilingPeriod();
    private String actionName;
    private PlanFilingPeriodRepository dao;
    private AcquisitionPlanRepository acquisitionPlanRepository;
    private UserAccountRepository userAccountRepository;
    private UserAccount currentUser;
    @Getter @Setter
    private Boolean canEdit = false;
    UserContextService userContextService;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(PlanFilingPeriodRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        userContextService = WebUtil.getBean(UserContextService.class);
        acquisitionPlanRepository = WebUtil.getBean(AcquisitionPlanRepository.class);

        if(userContextService.accessContainsFunctionMenu("ACQUISITION_PLAN_FILING_PERIOD_EDIT")){
            canEdit = true;
        }
    }

    @Override
    protected Page<PlanFilingPeriod> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        if(StringUtils.isNotEmpty(searchForm.getYear())){
            updateSearchFilter(new SearchFilter("year", SearchFilter.Operator.LIKE, searchForm.getYear()));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void search(){
        selected = null;
        clearSearchFilter();
    }

    private void clearSearchFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
    }

    public void resetSearch(){
        selected = null;
        clearSearchFilter();
        searchForm = new PlanFilingPeriod();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.actionName = "Create";
        selected = new PlanFilingPeriod();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        selected.setYear(String.valueOf(year));
        selected.setCreatedBy(this.currentUser.getLoginName());
        selected.setCreatedDate(new Date());
        selected.setOperaterName(currentUser.getName());
    }

    @Override
    public void prepareEdit() {
        this.actionName = "Edit";
        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());
        super.prepareEdit();
    }


    @Override
    public void save()  {
        if(selected.getStartDate().compareTo(selected.getEndDate()) > 0){
            WebUtil.addErrorMessage("终止申报日期不能小于开启日期！");
            return;
        }
        if("Edit".equals(this.actionName)){
            dao.save(selected);
        }else {
            selected.setTenantUID(currentUser.getTenantUID());
            List<PlanFilingPeriod> olds = dao.findByYearAndTenantUID(selected.getYear(), currentUser.getTenantUID());
            if(CollectionUtils.isNotEmpty(olds)){
                WebUtil.addErrorMessage("医疗设备购置计划申报期已存在，不能重复创建！");
                return;
            }
            dao.save(selected);
        }

        selected = null;
    }

    public void delete() {
        List<AcquisitionPlan> list = acquisitionPlanRepository.findByApplicationYear(selected.getYear());
        if(CollectionUtils.isNotEmpty(list)){
            WebUtil.addSuccessMessage("该申报期存在购置计划申请，不能删除！");
        }else{
            dao.delete(selected);
            WebUtil.addSuccessMessage("删除成功！");
        }
    }

    public void cancel(){
        selected = null;
    }

    @Override
    protected GenericRepositoryUUID<PlanFilingPeriod> getDAO() {
        return dao;
    }
}
