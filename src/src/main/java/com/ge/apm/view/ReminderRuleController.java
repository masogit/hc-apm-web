package com.ge.apm.view;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.*;
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
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper=false)
public class ReminderRuleController extends GenericCRUDUUIDController<ReminderRule> {

    private static final long serialVersionUID = 6868159463011514707L;

    private ReminderRuleRepository dao;
    private SysRoleRepository sysRoleRepository;
    private UserAccountMapper userAccountMapper;

    private UserAccount loginUser;

    @Override
    protected ReminderRuleRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(ReminderRuleRepository.class);
        sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
        loginUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected Page<ReminderRule> loadData(PageRequest pageRequest) {
        searchFilters = ObjectUtil.isEmpty(searchFilters) ? ListUtil.toList() : searchFilters;
        Page<ReminderRule> page = dao.findBySearchFilter(searchFilters, pageRequest);
        for (ReminderRule reminderRule : page.getContent()) {
            reminderRule.setRuleParam(JSON.parseObject(reminderRule.getParameters(), RuleParam.class));
            reminderRule.setWechatParam(JSON.parseObject(reminderRule.getParameters(), WechatParam.class));
        }
        return page;
    }

    @Override
    public void save(){
        if ("Create".equals(crudActionName)) {
            selected.setState(0);
            selected.setTenantUid(loginUser.getTenantUID());
            selected.setInstitutionUid(loginUser.getInstitutionUID());
            selected.setHospitalUid(loginUser.getHospitalUID());
            selected.setSiteUid(loginUser.getSiteUID());
        }
        JSONObject parameters = JSON.parseObject(JSON.toJSONString(selected.getRuleParam()));
        parameters.putAll(JSON.parseObject(JSON.toJSONString(selected.getWechatParam())));
        selected.setParameters(JSON.toJSONString(parameters));
        dao.save(selected);
    }

    public void setToUsing(String id, boolean using) {
        selected = dao.findById(id);
        selected.setId(id);
        selected.setState(using ? 1 : 0);
        dao.save(selected);
        selected = null;
    }

    public String getEventPage(String ruleId) {
        String operation = "reminderEventList.xhtml?ruleId=" + ruleId;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void onRuleEvent(String ruleId) {
        String url = "actionName=View&ruleId=" + ruleId;
        url = "reminderEventList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public List<SysRole> getRoleList() {
        return sysRoleRepository.find();
    }

    public List<UserAccount> getUserList() {
        return userAccountMapper.getBySiteUid(selected.getSiteUid());
    }


}
