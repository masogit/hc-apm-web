package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.ReminderEventRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.ReminderEvent;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.reminder.WechatParam;
import com.ge.apm.view.sysutil.UrlEncryptController;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

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

    private String ruleId;

    @Override
    protected ReminderEventRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(ReminderEventRepository.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        ruleId = (String) UrlEncryptController.getValueFromMap(encodeStr, "ruleId");
    }

    @Override
    protected Page<ReminderEvent> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("ruleId", SearchFilter.Operator.EQ, ruleId, "1*"));
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
