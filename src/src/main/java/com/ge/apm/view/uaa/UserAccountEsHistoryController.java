package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.UserAccountHistoryRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAccountHistory;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmEsIndex;
import com.ge.healthcare.cn.common.utils.bean.HistoryInfo;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class UserAccountEsHistoryController extends GenericCRUDController<UserAccountHistory> {

    private static final long serialVersionUID = -1;

    private UserAccount currentUser;
    private UserAccountService userAccountService;
    private ElasticService elasticService;
    private UserAccountHistoryRepository dao = null;
    @Getter
    @Setter
    private Map<String,Object> filterObject;
    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAccountHistoryRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        userAccountService = WebUtil.getBean(UserAccountService.class);
        elasticService=WebUtil.getBean(ElasticService.class);
        filterObject=new HashMap<>();
    }

    @Override
    protected UserAccountHistoryRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<UserAccountHistory> loadData(PageRequest pageRequest) {
        setSearchFilter();
        String orderBy = null;
        if (pageRequest.getSort() != null) {
            orderBy = pageRequest.getSort().getOrderFor("createdDate").getDirection().name();
        }
        Page<Object> pgData = elasticService.searchHistory(searchFilters, UserContextService.getAccessToken(),
                pageRequest.getPageNumber(), pageRequest.getPageSize(), ApmEsIndex.USER_HISTORY.getValue(), null, orderBy);
        List<UserAccountHistory> list = ListUtil.newArrayList();
        for (Object obj : pgData.getContent()) {
            UserAccountHistory userAccountHistory = JSON.parseObject(obj.toString(), UserAccountHistory.class);
            if (StringUtils.isNotEmpty(userAccountHistory.getOperationType())) {
                HistoryInfo.TypeAction typeAction = HistoryInfo.TypeAction.valueOf(userAccountHistory.getOperationType());
                userAccountHistory.setOperationType(typeAction.getValue());
            }
            list.add(userAccountHistory);
        }
        return new PageImpl<>(list, pageRequest, pgData.getTotalElements());
    }

    @Override
    public void setSearchFilter() {
        // Preserve existing filters set by onFilter event
        List<SearchFilter> existingFilters = this.searchFilters;
        this.searchFilters = new ArrayList<>(1);
        String siteUid = currentUser.getSiteUID();
        List<String> loginNameList = userAccountService.getLoginNameBySiteUid(siteUid);
        searchFilters.add(new SearchFilter("createdBy", SearchFilter.Operator.IN, loginNameList));
        // Re-add existing filters from onFilter event
        if (existingFilters != null) {
            for (SearchFilter filter : existingFilters) {
                if (!"createdBy".equals(filter.fieldName)) {
                    searchFilters.add(filter);
                }
            }
        }
    }

    @Override
    public List<UserAccountHistory> getItemList() {
        return dao.find();
    }

    public List<SelectItem> getHistoryActionList() {
        return Arrays.stream(UserAccount.HistoryAction.values())
                .map(action -> new SelectItem(action.name(), action.getValue()))
                .collect(Collectors.toList());
    }

}
