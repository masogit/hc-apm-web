package com.ge.apm.view.asset;

import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
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
        Page<UserAccountHistory> pgData = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<UserAccountHistory> content = pgData.getContent();
        content.forEach(c -> {
            if (StringUtils.isNotEmpty(c.getOperationType())) {
                UserAccount.HistoryAction historyAction = UserAccount.HistoryAction.valueOf(c.getOperationType());
                c.setOperationType(historyAction.getValue());
            }
        });
        return pgData;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        String siteUid = currentUser.getSiteUID();
        List<String> loginNameList = userAccountService.getLoginNameBySiteUid(siteUid);
        searchFilters.add(new SearchFilter("createdBy", SearchFilter.Operator.IN, loginNameList));
    }

    @Override
    public List<UserAccountHistory> getItemList() {
        return dao.find();
    }

}
