package com.ge.apm.view.systemEventAudit;

import com.ge.apm.dao.SystemEventAuditRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.SystemEventAudit;
import com.ge.apm.domain.UserAccount;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class SystemEventAuditTableController extends GenericCRUDUUIDController<SystemEventAudit> {

    private SystemEventAuditRepository systemEventAuditRepository;
    private UserAccountRepository userAccountRepository;

    @Override
    protected void init() {
        super.init();
        systemEventAuditRepository = WebUtil.getBean(SystemEventAuditRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
    }

    @Override
    protected Page<SystemEventAudit> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("boType", SearchFilter.Operator.EQ, AuditBoTypeEnum.TABLE_DOWNLOAD.getBoType()));

        return systemEventAuditRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    public String getUserName(String uid) {
        UserAccount userAccount = userAccountRepository.findByUid(uid);
        if(userAccount == null){
            return "";
        }
        return userAccount.getName();
    }

    @Override
    protected GenericRepositoryUUID<SystemEventAudit> getDAO() {
        return systemEventAuditRepository;
    }
}
