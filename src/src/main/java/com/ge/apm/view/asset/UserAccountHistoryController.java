package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.bean.HistoryInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private UserAccountMapper userAccountMapper;
    private UserAccountHistoryRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAccountHistoryRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        userAccountService = WebUtil.getBean(UserAccountService.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
    }

    @Override
    protected UserAccountHistoryRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccountHistory> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        Wrapper<UserAccountHistory> wrapper = new EntityWrapper<>();
        wrapper.eq("ua.tenant_uid", currentUser.getTenantUID());
        wrapper.andNew();
        wrapper.eq("ua.institution_uid", currentUser.getSiteUID());
        wrapper.or().eq("ua.hospital_uid", currentUser.getSiteUID());
        wrapper.or().eq("ua.site_uid", currentUser.getSiteUID());
        List<UserAccountHistory> historyList = userAccountMapper.selectHistoryList(pagination, wrapper);
        historyList.forEach(c -> {
            if (StringUtils.isNotEmpty(c.getOperationType())) {
                HistoryInfo.TypeAction typeAction = HistoryInfo.TypeAction.valueOf(c.getOperationType());
                c.setOperationType(typeAction.getValue());
            }
        });
        return new PageImpl<>(historyList, pageRequest, pagination.getTotal());
    }

    @Override
    public List<UserAccountHistory> getItemList() {
        return dao.find();
    }

}
