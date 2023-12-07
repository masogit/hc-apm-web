package com.ge.apm.view.asset;

import com.ge.apm.dao.LoginRecordRepository;
import com.ge.apm.domain.LoginRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class LoginRecordController extends GenericCRUDController<LoginRecord> {

    private static final long serialVersionUID = -1;

    private UserAccount currentUser;
    private UserAccountService userAccountService;
    private LoginRecordRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(LoginRecordRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        userAccountService = WebUtil.getBean(UserAccountService.class);
    }

    @Override
    protected LoginRecordRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<LoginRecord> loadData(PageRequest pageRequest) {
        setSearchFilter();
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        String siteUid = currentUser.getSiteUID();
        if (!this.hasFilterOnField("loginName")) {
            List<String> loginNameList = userAccountService.getLoginNameBySiteUid(siteUid);
            searchFilters.add(new SearchFilter("loginName", SearchFilter.Operator.IN, loginNameList));
        }
    }

    @Override
    public List<LoginRecord> getItemList() {
        return dao.find();
    }


}
