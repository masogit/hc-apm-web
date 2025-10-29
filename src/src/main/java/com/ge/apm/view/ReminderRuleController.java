package com.ge.apm.view;

import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper = false)
public class ReminderRuleController extends GenericCRUDUUIDController<ReminderRule> {

    private static final long serialVersionUID = 6868159463011514707L;

    private PageUtil<ReminderRule> pageUtil = new PageUtil<>();
    private ReminderRuleRepository dao;
    private SysRoleRepository sysRoleRepository;
    private UserAccountMapper userAccountMapper;
    private UaaService uaaService;
    private OrgInfoService orgInfoService;
    private ReminderRuleOrgRepository ruleOrgRepository;

    private UserAccount loginUser;
    private List<SysRole> roles;
    private List<CustomRole> customRoles;
    private boolean isApm;
    private List<ReminderRule> apmRuleList;
    private String retrieval;
    private boolean propagateSelectionUp = true;
    private TreeNode accessOrgTree;
    private TreeNode[] selectedAccessOrgNodes;
    private Integer state = -1;
    private String type;

    @Override
    protected ReminderRuleRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        isApm = request.getRequestURI().endsWith("apmReminderRuleList.xhtml");
        dao = WebUtil.getBean(ReminderRuleRepository.class);
        sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);
        userAccountMapper = WebUtil.getBean(UserAccountMapper.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoService = WebUtil.getBean(OrgInfoService.class);
        ruleOrgRepository = WebUtil.getBean(ReminderRuleOrgRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        roles = sysRoleRepository.find();
        customRoles = uaaService.getTenantCustomRoles(loginUser.getTenantUID());
    }

    @Override
    protected Page<ReminderRule> loadData(PageRequest pageRequest) {
        searchFilters = ObjectUtil.isEmpty(searchFilters) ? ListUtil.toList() : searchFilters;
        List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        List<ReminderRule> resultList = dao.findBySearchFilter(searchFilters);

        for (ReminderRule reminderRule : resultList) {
            reminderRule.setRuleParam(JSON.parseObject(reminderRule.getParameters(), RuleParam.class));
            reminderRule.setWechatParam(JSON.parseObject(reminderRule.getParameters(), WechatParam.class));
        }
        return pageUtil.pageNum(resultList, pageRequest);
    }

    public void save(String actionName) {
        WechatParam wechatParam = selected.getWechatParam();
        if ("setting".equalsIgnoreCase(actionName) && (ObjectUtil.isEmpty(wechatParam.getFirst()) || ObjectUtil.isEmpty(wechatParam.getKeyword1()))) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessageKey("微信模板不能为空！");
            return;
        }
        selected.setState(state);
        if ("Create".equals(crudActionName)) {
            selected.setState(-1);
            selected.setTenantUid(loginUser.getTenantUID());
            selected.setInstitutionUid(loginUser.getInstitutionUID());
            selected.setHospitalUid(loginUser.getHospitalUID());
            selected.setSiteUid(loginUser.getSiteUID());
        } else if ("Edit".equals(crudActionName) && "base".equals(actionName)) {
            selected.setState(-1);
        }
        if (!verifyEmpty(actionName)) return;
        JSONObject parameters = JSON.parseObject(JSON.toJSONString(selected.getRuleParam()));

        wechatParam.setTemplateId(ReminderType.getWechatTemplateId(selected.getType()));
        parameters.putAll(JSON.parseObject(JSON.toJSONString(wechatParam)));
        selected.setParameters(JSON.toJSONString(parameters));
        dao.save(selected);
    }

    private boolean verifyEmpty(String actionName) {
        boolean flag = true;
        RuleParam ruleParam = selected.getRuleParam();
        if ("setting".equals(actionName) && ruleParam.getPushType().equals(2)) {
            Integer pushCycle = ruleParam.getPushCycle();
            Integer pushNum = ruleParam.getPushNum();
            if (pushCycle == null || pushCycle < 1) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessageKey("pushCycle 不能小于0");
                flag = false;
            }
            if (pushNum == null || pushNum < 1) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessageKey("pushNum 不能小于0");
                flag = false;
            }
        }
        return flag;
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

    public void prepareUserScope() {
        // 已选中的组织
        List<OrgInfo> orgList = uaaService.getOrgListByReminderRule(selected.getId());
        retrieval = null;
        propagateSelectionUp = true;
        accessOrgTree = uaaService.getOrgTreeBySiteWithoutClinical(selected.getTenantUid());
        setSelectedOrgTree(accessOrgTree, orgList);
        TreeNodeUtil.expandTreeNode(accessOrgTree);
    }

    private void setSelectedOrgTree(TreeNode node, List<OrgInfo> accessiableOrgList) {
        OrgInfo org = (OrgInfo) node.getData();
        if (accessiableOrgList.contains(org) || (node.getParent() != null && node.getParent().isSelected())) {
            node.setSelected(true);
        }
        for (TreeNode item : node.getChildren()) {
            setSelectedOrgTree(item, accessiableOrgList);
        }
    }

    public void getFilteredNodes() {
        if (StringUtils.isEmpty(retrieval)) {
            propagateSelectionUp = true;
        } else {
            if (!accessOrgTree.getChildren().get(0).isSelected()) {
                propagateSelectionUp = false;
            }
        }
        uaaService.filterTreeNode(accessOrgTree, retrieval, -1, -1);
    }

    public void onSelectAccessTreeNode(NodeSelectEvent event) {
        setTreeNodes(event.getTreeNode(), true);
    }

    public void onUnselectAccessTreeNode(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    public void saveOrgs() {
        Set<OrgInfo> selectedOrgList = new HashSet<>();
        readSelectedOrgTree(accessOrgTree, selectedOrgList);
        addMissingChildClinical(accessOrgTree, selectedOrgList);
        ruleOrgRepository.deleteByReminderRuleUid(selected.getId());
        List<ReminderRuleOrg> ruleOrgList = ListUtil.newArrayList();
        for (OrgInfo selectedOrg : selectedOrgList) {
            ReminderRuleOrg ruleOrg = new ReminderRuleOrg();
            ruleOrg.setOrgType(selectedOrg.getOrgType());
            ruleOrg.setOrgUid(selectedOrg.getUid());
            ruleOrg.setRuleUid(selected.getId());
            ruleOrgList.add(ruleOrg);
        }
        ruleOrgRepository.save(ruleOrgList);
    }

    private void readSelectedOrgTree(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (node.getParent() == null || !node.getParent().isSelected()) {
                selectedTreeNodeList.add((OrgInfo) node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedOrgTree(item, selectedTreeNodeList);
            }
        }
    }

    private void addMissingChildClinical(TreeNode node, Set<OrgInfo> selectedTreeNodeList) {
        OrgInfo data = (OrgInfo) node.getData();
        if (data == null) {
            //root
            for (TreeNode item : node.getChildren()) {
                addMissingChildClinical(item, selectedTreeNodeList);
            }
        } else if (data.getOrgType() < 4) {
            if (!node.isSelected()) {
                for (TreeNode item : node.getChildren()) {
                    addMissingChildClinical(item, selectedTreeNodeList);
                }
            }
        } else if (data.getOrgType() == 5 || data.getOrgType() == 4) {
            //if site is selected, will not traverse here
            if (node.isSelected()) {
                selectedTreeNodeList.add(data);
            }
            //大科室下面可能还有大科室
            for (TreeNode item : node.getChildren()) {
                addMissingChildClinical(item, selectedTreeNodeList);
            }
        }
    }

    public void initWechatTemplate() {
        switch (selected.getType()) {
            case "AssetLifecycle":
                selected.setWechatParam(JSON.parseObject(ReminderRule.ASSET_LIFECYCLE_MSG, WechatParam.class));
                break;
            case "AssetWarrantyDate":
                selected.setWechatParam(JSON.parseObject(ReminderRule.ASSET_WARRANTY_DATE_MSG, WechatParam.class));
                break;
            case "ContractOverdue":
                selected.setWechatParam(JSON.parseObject(ReminderRule.CONTRACT_OVERDUE_MSG, WechatParam.class));
                break;
            case "ContractAck":
                selected.setWechatParam(JSON.parseObject(ReminderRule.CONTRACT_ACK_MSG, WechatParam.class));
                break;
            case "NurseSiteMaintainItem":
                selected.setWechatParam(JSON.parseObject(ReminderRule.NURSE_SITE_MSG, WechatParam.class));
                break;
            case "UnattendedOverdue":
                selected.setWechatParam(JSON.parseObject(ReminderRule.UNATTENDED_OVERDUE_MSG, WechatParam.class));
                break;
            case "SupplierReview":
                selected.setWechatParam(JSON.parseObject(ReminderRule.SUPPLIER_REVIEW_MSG, WechatParam.class));
                break;
            case "UnattendedApproval":
                selected.setWechatParam(JSON.parseObject(ReminderRule.UNATTENDED_APPROVAL_MSG, WechatParam.class));
                break;
            case "AdjustmentCenterReturn":
                selected.setWechatParam(JSON.parseObject(ReminderRule.ADJUSTMENT_CENTER_RETURN_MSG, WechatParam.class));
                break;
            default:
                break;
        }
    }

    public void prepareSitting() {
        crudActionName = "Edit";
        if (selected.getState() >= 0) {
            state = selected.getState();
        } else {
            state = 1;
        }
        type = selected.getType();
    }

    public List<SelectItem> getDateUnitList() {
        List<SelectItem> resultList = ListUtil.newArrayList();
        resultList.add(new SelectItem("0" , "天"));
        if ("UnattendedApproval".equals(type)) {
            resultList.add(new SelectItem("1" , "小时"));
            resultList.add(new SelectItem("2" , "分钟"));
        }
        return resultList;
    }

    public String getPushSettingComment() {
        String messageType = this.selected.getRuleParam().getMessageType();
        Integer pushType = this.selected.getRuleParam().getPushType();
        String comment = "";
        if (pushType == 1) {
            if ("overdue".equals(messageType)) {
                comment = "(业务发生后按周期提醒1次)";
            } else {
                comment = "(业务发生前按周期提醒1次)";
            }
        } else if (pushType == 2) {
            if ("overdue".equals(messageType)) {
                comment = "(业务发生后按周期、次数进行提醒)";
            } else {
                comment = "(业务发生前按周期、次数进行提醒)";
            }
        }
        return comment;
    }

    public List<OrgInfo> getHospitalList(String orgLevel) {
        return orgInfoService.getHospitalList(orgLevel);
    }

    public String getOrgNameById(Integer orgId) {
        return orgInfoService.getOrgNameById(orgId);
    }

    public String getInsNameByUId(String uid) {
        return orgInfoService.getInsNameByUId(uid);
    }

    public String getHospitalNameByUId(String uid) {
        return orgInfoService.getHospitalNameByUId(uid);
    }

    public String getSiteNameByUId(String uid) {
        return orgInfoService.getSiteNameByUId(uid);
    }

    public String getTenantName(String tenantUID) {
        return orgInfoService.getTenantName(tenantUID);
    }

    public List<SysRole> getRoleList() {
        return sysRoleRepository.find();
    }

    public List<UserAccount> getUserList() {
        return userAccountMapper.getBySiteUid(selected.getSiteUid());
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Boolean getApm() {
        return isApm;
    }

    public void setApm(Boolean apm) {
        isApm = apm;
    }

    public List<ReminderRule> getApmRuleList() {
        return apmRuleList;
    }

    public void setApmRuleList(List<ReminderRule> apmRuleList) {
        this.apmRuleList = apmRuleList;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
