package com.ge.apm.view.budget;

import com.ge.apm.dao.PurchaseBudgetConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.budget.PurchaseBudgetConfigService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2023/10/12
 * @description :
 */
@ViewScoped
@ManagedBean(name = "purchaseBudgetConfigController")
public class PurchaseBudgetConfigController extends GenericCRUDUUIDController<PurchaseBudgetConfig> {


    private PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;
    private UserAccountRepository userAccountRepository;
    private PurchaseBudgetConfigService purchaseBudgetConfigService;

    private String actionName;
    private UserAccount currentUser;
    @Getter@Setter
    private PurchaseBudgetConfig searchForm = new PurchaseBudgetConfig();


    @Override
    protected void init() {
        purchaseBudgetConfigRepository = WebUtil.getBean(PurchaseBudgetConfigRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        purchaseBudgetConfigService = WebUtil.getBean(PurchaseBudgetConfigService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<PurchaseBudgetConfig> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        if(StringUtils.isNotEmpty(searchForm.getYear())){
            updateSearchFilter(new SearchFilter("year", SearchFilter.Operator.LIKE, searchForm.getYear()));
        }
        return purchaseBudgetConfigRepository.findBySearchFilter(this.searchFilters, pageRequest);
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
        searchForm = new PurchaseBudgetConfig();
    }

    @Override
    protected GenericRepositoryUUID<PurchaseBudgetConfig> getDAO() {
        return purchaseBudgetConfigRepository;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.actionName = "Create";
        selected = new PurchaseBudgetConfig();
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
    public void save() {
        if(selected.getStartDate().compareTo(selected.getEndDate()) > 0){
            WebUtil.addErrorMessage("终止申报日期不能小于开启日期！");
            return;
        }
        if("Edit".equals(this.actionName)){
            List<PurchaseBudgetConfig> olds = purchaseBudgetConfigRepository.findByYearAndTenantUIDAndIdIsNot(selected.getYear(), selected.getId(), currentUser.getTenantUID());
            if(olds != null && olds.size() > 0){
                WebUtil.addErrorMessage("年度申购开启记录已存在，不能重复创建！");
                return;
            }
            purchaseBudgetConfigRepository.save(selected);
        }else {
            selected.setTenantUID(currentUser.getTenantUID());
            selected.setInstitutionUID(currentUser.getInstitutionUID());
            selected.setHospitalUID(currentUser.getHospitalUID());
            selected.setSiteUID(currentUser.getSiteUID());
            List<PurchaseBudgetConfig> olds = purchaseBudgetConfigRepository.findByYearAndTenantUID(selected.getYear(), currentUser.getTenantUID());
            if(olds != null && olds.size() > 0){
                WebUtil.addErrorMessage("年度申购开启记录已存在，不能重复创建！");
                return;
            }
            selected = purchaseBudgetConfigRepository.save(selected);
        }
        //purchaseBudgetConfigService.sendWxMsg(selected);
        selected = null;
    }


    public void cancel(){
        selected = null;
    }

    public String getUserName(String loginName) {
        if (null == loginName) {
            return null;
        }

        UserAccount userAccount = userAccountRepository.getByLoginName(loginName);
        if (null == userAccount) {
            return null;
        }

        return userAccount.getName();
    }
}
