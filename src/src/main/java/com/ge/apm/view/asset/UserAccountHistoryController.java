package com.ge.apm.view.asset;

import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.bean.HistoryInfo;
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
                HistoryInfo.TypeAction typeAction = HistoryInfo.TypeAction.valueOf(c.getOperationType());
                c.setOperationType(typeAction.getValue());
            }
        });
        return pgData;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*2+"));
    }

    @Override
    public List<UserAccountHistory> getItemList() {
        return dao.find();
    }

}
