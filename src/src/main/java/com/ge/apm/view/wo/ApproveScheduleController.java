package com.ge.apm.view.wo;

import com.ge.apm.dao.ApproveScheduleRepository;
import com.ge.apm.dao.AuditGroupConfigurationDetailRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ApproveSchedule;
import com.ge.apm.domain.AuditGroupConfigurationDetail;
import com.ge.apm.domain.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ApproveScheduleController extends GenericCRUDUUIDController<ApproveSchedule> {

    private ApproveScheduleRepository dao;
    private AuditGroupConfigurationDetailRepository auditGroupConfigurationDetailRepository;
    private UserAccountRepository userAccountRepository;

    @Setter
    @Getter
    private List<AuditGroupConfigurationDetail>auditGroupConfigurationDetails;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ApproveScheduleRepository.class);
        auditGroupConfigurationDetailRepository = WebUtil.getBean(AuditGroupConfigurationDetailRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);

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
    }
    public String getUserName(String userUid){
        UserAccount userAccount = userAccountRepository.getByUid(userUid);
        if (userAccount!=null){
            return userAccount.getName();
        }
        return null;
    }

}