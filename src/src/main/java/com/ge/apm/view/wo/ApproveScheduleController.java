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

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }
    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }
    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }

        ApproveSchedule approveSchedule = (ApproveSchedule) event.getObject();
        selected=approveSchedule;
        auditGroupConfigurationDetails = auditGroupConfigurationDetailRepository.listAllByBusinessUid(approveSchedule.getBusinessUid());
    }
    public String getUserName(String userUid){
        UserAccount userAccount = userAccountRepository.getByUid(userUid);
        if (userAccount!=null){
            return userAccount.getName();
        }
        return null;
    }

    public String getAuditNodeStatus( Integer auditNodeStatus){
        switch (auditNodeStatus){
            case 0:return "未审核";
            case 1:return "审核通过";
            case 2:return "审核不通过";
            default:return null;
        }
    }
    public String getApproveStatusName( Integer approveStatus){
        switch (approveStatus){
            case 1:return "未审核";
            case 2:return "审核通过";
            case 3:return "审核不通过";
            case 4:return "审核进行中";
            default:return null;
        }
    }


}