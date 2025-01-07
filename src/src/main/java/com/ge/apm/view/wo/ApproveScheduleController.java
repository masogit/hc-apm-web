package com.ge.apm.view.wo;

import com.ge.apm.dao.ApproveScheduleRepository;
import com.ge.apm.dao.AuditGroupConfigurationDetailRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ApproveSchedule;
import com.ge.apm.domain.AuditGroupConfigurationDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class ApproveScheduleController extends GenericCRUDUUIDController<ApproveSchedule> {

    private ApproveScheduleRepository dao;
    private AuditGroupConfigurationDetailRepository auditGroupConfigurationDetailRepository;
    private UserAccountRepository userAccountRepository;
    private V2_WorkOrderService workOrderService;
    private UserAccount userAccount;

    @Getter @Setter
    private V2_WorkOrder workOrder;

    @Setter
    @Getter
    private List<AuditGroupConfigurationDetail>auditGroupConfigurationDetails;
    private List<String> uids;
    @Override
    protected void init() {
        dao = WebUtil.getBean(ApproveScheduleRepository.class);
        auditGroupConfigurationDetailRepository = WebUtil.getBean(AuditGroupConfigurationDetailRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        workOrderService = WebUtil.getBean(V2_WorkOrderService.class);
        userAccount = UserContextService.getCurrentUserAccount();
        uids = Arrays.asList(userAccount.getTenantUID(),userAccount.getInstitutionUID(),userAccount.getHospitalUID(),userAccount.getSiteUID());
    }

    @Override
    protected GenericRepositoryUUID<ApproveSchedule> getDAO() {
        return dao;
    }

    @Override
    protected Page<ApproveSchedule> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        selected=null;
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN,uids,"1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN,uids,"1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN,uids,"1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN,uids,"1*2+"));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }
    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }

        ApproveSchedule approveSchedule = (ApproveSchedule) event.getObject();
        selected=approveSchedule;
        auditGroupConfigurationDetails = auditGroupConfigurationDetailRepository.listAllByBusinessUid(approveSchedule.getBusinessUid());
        if(auditGroupConfigurationDetails!=null && !auditGroupConfigurationDetails.isEmpty()){
            for ( AuditGroupConfigurationDetail auditGroupConfigurationDetail :auditGroupConfigurationDetails) {
                auditGroupConfigurationDetail.setSrNum(approveSchedule.getSrNum());
            }
        }
        List<V2_WorkOrder> workOrders = workOrderService.findBySrNum(selected.getTenantUID(), selected.getSrNum());
        workOrder = workOrders.stream().findFirst().get();
    }
    public String getUserName(String userUid){
        UserAccount userAccount = userAccountRepository.getByUid(userUid);
        if (userAccount!=null){
            return userAccount.getName();
        }
        return null;
    }

}