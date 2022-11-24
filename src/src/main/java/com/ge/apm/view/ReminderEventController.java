package com.ge.apm.view;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.ReminderEventRepository;
import com.ge.apm.dao.ReminderRuleRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.ReminderEvent;
import com.ge.apm.domain.ReminderRule;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.reminder.WechatParam;
import com.ge.apm.domain.reminder.rule.RuleParam;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper=false)
public class ReminderEventController extends GenericCRUDUUIDController<ReminderEvent> {

    private static final long serialVersionUID = 6868159463011514707L;

    private ReminderEventRepository dao;
    private UserAccountMapper userAccountMapper;

    @Override
    protected ReminderEventRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(ReminderEventRepository.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String ruleId = (String) UrlEncryptController.getValueFromMap(encodeStr, "ruleId");
        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("ruleId", SearchFilter.Operator.EQ, ruleId, "1*"));
    }

    @Override
    protected Page<ReminderEvent> loadData(PageRequest pageRequest) {
        Page<ReminderEvent> page = dao.findBySearchFilter(searchFilters, pageRequest);
        List<ReminderEvent> content = page.getContent();
        for (ReminderEvent reminderEvent : content) {
            WechatParam wechatParam = JSON.parseObject(reminderEvent.getParameters(), WechatParam.class);
            UserAccount user = userAccountMapper.getUserByWechatId(wechatParam.getOpenId());
            reminderEvent.setName(user.getName());
        }
        return page;
    }

}
