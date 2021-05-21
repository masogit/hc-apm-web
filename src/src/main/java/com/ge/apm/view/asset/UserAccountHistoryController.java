package com.ge.apm.view.asset;

import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
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
public class UserAccountHistoryController extends GenericCRUDController<UserAccountHistory> {

    private static final long serialVersionUID = -1;

    private UserAccount currentUser;
    private UserAccountService userAccountService;
    private UserAccountHistoryRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAccountHistoryRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        userAccountService = WebUtil.getBean(UserAccountService.class);
    }

    @Override
    protected UserAccountHistoryRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccountHistory> loadData(PageRequest pageRequest) {
        setSearchFilter();
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        Integer siteId = currentUser.getSiteId();
        List<String> loginNameList = userAccountService.getLoginNameBySiteId(siteId);

        //查询和当前用户siteId相同的用户的登陆记录
        if (!this.hasFilterOnField("loginName")) {
            searchFilters.add(new SearchFilter("loginName", SearchFilter.Operator.IN, loginNameList));
        }
    }

    @Override
    public List<UserAccountHistory> getItemList() {
        return dao.find();
    }

}
