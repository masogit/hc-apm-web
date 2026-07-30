package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
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

@ManagedBean
@ViewScoped
public class AcquisitionPlanAuditListController extends GenericCRUDUUIDController<AcquisitionPlan> {

    @Getter @Setter
    private int tabIndex= 0;
    @Getter @Setter
    private Integer queryIndex = 0;

    private AcquisitionPlanRepository dao;
    private UserAccount loginUser;
    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        String query = WebUtil.getRequestParameter("query");
        if(StrUtil.isNotEmpty(query)){
            queryIndex = Integer.parseInt(query);
        }

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
                this.updateSearchFilter(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, statusList, "1*"));
            }
        }else if(queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("drugCenterUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("workGroupUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("bureauPartyUid", SearchFilter.Operator.EQ, loginUser.getUid(), "1*2+"));
        }
        return dao.findBySearchFilter(searchFilters, pageRequest);
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


    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
