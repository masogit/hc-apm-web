package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalConfigController extends GenericCRUDUUIDController<MultiLevelApprovalConfig> {

    private MultiLevelApprovalConfigRepository configDao;
    private UserAccountRepository userAccountDao;
    private UserRoleRepository userRoleDao;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgInfoDao;
    private SysRoleRepository roleDao;
    @Setter@Getter
    private ApprovalNode approvalNode;
    @Setter
    private List<ApprovalNode> nodesList;
    private List<UserAccount> approveUserList;
    @Getter
    private String operateType;
    @Getter
    private boolean configEnable = true;
    @Getter
    private TreeNode orgTree;
    @Getter
    private OrgInfo selectedUserOrg;

    @Override
    protected void init() {
        configDao = WebUtil.getBean(MultiLevelApprovalConfigRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        roleDao = WebUtil.getBean(SysRoleRepository.class);
        orgTree= WebUtil.getBean(UaaService.class).buildOrgTree(UserContextService.getCurrentUserAccount().getTenantUID(), "", -1, -1);
        buildApproveUserList();
    }

    //获取拥有审核权限的用户
    public void buildApproveUserList() {
        int roleId = roleDao.getIdByName(ApprovalConstant.MULTILEVEL_AUDITOR);
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<String> userIds = userRoleDao.findUserUidByRoleId(userAccount.getTenantUID(),roleId);
        if (CollectionUtils.isNotEmpty(userIds)) {
            approveUserList = userAccountDao.getUserList(userIds);
        } else {
            approveUserList = new ArrayList<>();
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
        approvalNode.setApproveUsers(null);
    }

    public List<I18nMessage> getBusinessTypeList(){
        return i18nDao.findByMsgTypeOrderById(ApprovalConstant.BUSINESS_TYPE);
    }

    public List<UserAccount> getUsers4Approval(){
        if(selectedUserOrg != null){
            return approveUserList.stream().filter(user -> user.getOrgInfoUID().equals(selectedUserOrg.getUid())).collect(Collectors.toList());
        }
        return this.approveUserList;
    }

    public void ifSelectbyOrg(){
        selectedUserOrg = null;
    }

    @Override
    protected Page<MultiLevelApprovalConfig> loadData(PageRequest pageRequest) {
        setSearchFilters();
        Page<MultiLevelApprovalConfig> bySearchFilter = configDao.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;
    }

    public void setSearchFilters() {
        if(null == searchFilters){
            searchFilters = new ArrayList<>();
        }
        List<String> uids = orgInfoDao.getOrgInfosByParentId(UserContextService.getCurrentUserAccount().getSiteUID()).stream().map(org -> org.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("hierOrgUid", SearchFilter.Operator.IN, uids));
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo selectedOrg = (OrgInfo) node.getData();
        selected.setHierOrgUid(selectedOrg.getUid());
        selected.setTenantUID(selectedOrg.getTenantUID());
    }

    public void preEdit() {
        operateType = ApprovalConstant.OPERATE_TYPE_EDIT;
        setConfigEnable();
        switchJsontoVo();
    }

    public void preCreate() {
        operateType = ApprovalConstant.OPERATE_TYPE_CREATE;
        configEnable = true;
        try {
            super.prepareCreate();
            //默认启用
            selected.setEnable(true);
            nodesList.clear();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void switchJsontoVo() {
        nodesList.clear();
        String configration = selected.getConfigration();
        if (null != configration) {
            Map map = JSON.parseObject(configration, Map.class);
            selected.setMinAmount((Integer) map.get("minAmount"));
            selected.setMaxAmount((Integer) map.get("maxAmount"));
        }
        String approvalItem = selected.getApprovalItem();
        if (null != approvalItem) {
            List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>(){
            });
            for (int i = 0; i < approvalNodes.size(); i++) {
                ApprovalNode node = approvalNodes.get(i);
                if (node.getUsingRole()) {
                    List<Integer> roleUids = node.getRoleUids();
                    List<SysRole> roles = new ArrayList();
                    roleUids.stream().forEach(ruid -> {
                        roles.add(roleDao.getById(Integer.valueOf(ruid)));
                    });
                    node.setApproveRoles(roles);
                } else {
                    List<UserAccount> users = new ArrayList();

                    List<String> userUids=node.getUserUids();
                    if(node.isClinicalDepartmentDirector()){
                        userUids= node.getMultiLevelApprovalUids();
                    }
                    userUids.stream().forEach(useruid -> {
                        users.add(userAccountDao.getByUid(useruid));
                    });
                    node.setApproveUsers(users);
                }
                node.setNodeId(i + 1);
            }
            nodesList.addAll(approvalNodes);
        }
    }

    //保存审批节点
    public void saveNode() {
        approvalNode.setMultiLevelApprovalUsers(approvalNode.getApproveUsers());

        if (null != approvalNode.getNodeId()) {

            if(approvalNode.isClinicalDepartmentDirector()){
                List<UserAccount> deptHeadList = buildRoleApproveUserList("DeptHead");
                if(CollectionUtils.isNotEmpty(deptHeadList)  && CollectionUtils.isNotEmpty(approvalNode.getApproveUsers())){
                    approvalNode.setClinicalDepartmentDirectorUsers(deptHeadList);
                }
            }
            return;
        }
        ApprovalNode node = new ApprovalNode();
        node.setId(UUID.randomUUID().toString().replace("-", ""));
        node.setNodeId(nodesList.size());
        node.setNodeName(approvalNode.getNodeName());
        node.setUsingRole(approvalNode.getUsingRole());
        if (approvalNode.getUsingRole()) {
            node.setApproveRoles(approvalNode.getApproveRoles());
        } else {
            node.setApproveUsers(approvalNode.getApproveUsers());
        }
        if(approvalNode.isClinicalDepartmentDirector()){
            List<UserAccount> deptHeadList = buildRoleApproveUserList("DeptHead");
            if(CollectionUtils.isNotEmpty(deptHeadList)  && CollectionUtils.isNotEmpty(node.getApproveUsers())){
//                node.getApproveUsers().addAll(deptHeadList);
                node.setClinicalDepartmentDirectorUsers(deptHeadList);
            }
        }
        nodesList.add(node);
    }

    //获取角色用户审核人员
    public List<UserAccount> buildRoleApproveUserList(String roleName) {
        List<UserAccount> userList = new ArrayList<>();
        int roleId = roleDao.getIdByName(roleName);
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<String> userIds = userRoleDao.findUserUidByRoleId(userAccount.getTenantUID(),roleId);
        if (CollectionUtils.isNotEmpty(userIds)) {
             userList = userAccountDao.getUserList(userIds);
        }
        return userList;
    }

    //保存配置
    public void save() {
        //判断是否已经存在审批流
        Integer minAmountNew = selected.getMinAmount() != null ?selected.getMinAmount() : 0;
        Integer maxAmountNew = selected.getMaxAmount() != null ?selected.getMaxAmount() : 0;
        String configrationOld = selected.getConfigration();
        boolean enable=true;
        if(selected.getId() != null){
            MultiLevelApprovalConfig multiLevelApprovalConfig = configDao.findById(selected.getId());
            enable = multiLevelApprovalConfig.getEnable();
        }
        if (null != configrationOld) {
            Map map = JSON.parseObject(configrationOld, Map.class);
            Integer minAmount = (Integer) map.get("minAmount");
            Integer maxAmount = (Integer) map.get("maxAmount");
            if (minAmountNew != minAmount || maxAmountNew != maxAmount || !enable){
                if(this.check(minAmountNew, maxAmountNew ,enable)) return;
            }
        }else {
            if(this.check(minAmountNew, maxAmountNew,enable)) return;
        }


        List<ApprovalNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
        collect.stream().forEach(item -> {
            if (item.getUsingRole()) {
                item.setRoleUids(item.getApproveRoles().stream().map(a -> a.getId()).collect(Collectors.toList()));
                item.setUserUids(null);
            } else {
                item.setUserUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
                item.setMultiLevelApprovalUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));

                if(item.isClinicalDepartmentDirector()){
                    List<String> clinicalDepartmentDirectorUids = item.getClinicalDepartmentDirectorUsers().stream().map(a -> a.getUid()).collect(Collectors.toList());
                    item.getUserUids().addAll(clinicalDepartmentDirectorUids);
                    item.setUserUids(item.getUserUids().stream().distinct().collect(Collectors.toList()));
                }
                item.setRoleUids(null);
            }
        });
        String nodes = JSON.toJSONString(collect);
        selected.setApprovalItem(nodes);

        Map<String, Object> map = new HashMap();
        map.put("minAmount", selected.getMinAmount());
        map.put("maxAmount", selected.getMaxAmount());
        String configration = JSON.toJSONString(map);
        selected.setConfigration(configration);
        super.save();
    }

    public Boolean check(Integer minAmountNew,Integer maxAmountNew,boolean enable){
        List<MultiLevelApprovalConfig> multiLevelApprovalConfigs = configDao.findByHierOrgUidAndBusinessTypeAndEnable(selected.getHierOrgUid(),selected.getBusinessType(),true);
        if(!multiLevelApprovalConfigs.isEmpty() && multiLevelApprovalConfigs != null){
            String approvalMsg ="已存在同级别审批流";
            for (MultiLevelApprovalConfig multiLevelApprovalConfig :multiLevelApprovalConfigs){
                String configration = multiLevelApprovalConfig.getConfigration();
                if (minAmountNew != null && maxAmountNew != null && null != configration) {
                    if(!multiLevelApprovalConfig.getId().equals(selected.getId()) || !enable){
                        Map map = JSON.parseObject(configration, Map.class);
                        Integer minAmount = ((Integer) map.get("minAmount"));
                        Integer maxAmount = ((Integer) map.get("maxAmount"));
                        if( minAmountNew > minAmount && minAmountNew < maxAmount){
                            WebUtil.addWarnMessage(approvalMsg);
                            return true;
                        }
                        if( maxAmountNew > minAmount && maxAmountNew <= maxAmount){
                            WebUtil.addWarnMessage(approvalMsg);
                            return true;
                        }
                        if( minAmount != 0 && minAmountNew <= minAmount && maxAmountNew >= maxAmount){
                            WebUtil.addWarnMessage(approvalMsg);
                            return true;
                        }
                    }

                }else {
                    WebUtil.addWarnMessage(approvalMsg);
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean enable(Boolean enable){
        if(enable != null && enable){
            //判断是否已经存在审批流
            Integer minAmountNew = selected.getMinAmount() != null ?selected.getMinAmount() : 0;
            Integer maxAmountNew = selected.getMaxAmount() != null ?selected.getMaxAmount() : 0;
            String configrationOld = selected.getConfigration();
            if (null != configrationOld) {
                Map map = JSON.parseObject(configrationOld, Map.class);
                Integer minAmount = ((Integer) map.get("minAmount"));
                Integer maxAmount = ((Integer) map.get("maxAmount"));
                if(this.check(minAmount, maxAmount,true)) return true;
            }else {
                if(this.check(minAmountNew, maxAmountNew,true)) return true;
            }
        }
        return false;
    }
    //初始化审批节点
    public List<ApprovalNode> getNodesList() {
        ApprovalNode node = new ApprovalNode();
        node.setId(SimplePartsConstant.PARTS_LOCAL_ORG_ID);
        if (null == nodesList) {
            nodesList = new ArrayList<>();
        } else {
            List<ApprovalNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
            nodesList = collect;
        }
        nodesList.add(node);
        return nodesList;
    }

    public void onAddNode() {
        approvalNode = new ApprovalNode();
        selectedUserOrg = null;
    }

    public void onEditNode(int nodeId) {
        for (ApprovalNode node : nodesList) {
            if (null != node.getNodeId() && nodeId == node.getNodeId()) {
                approvalNode = node;
            }
        }
    }

    public void delNode(int nodeId) {
        List<ApprovalNode> collect = nodesList.stream().filter(node -> !SimplePartsConstant.PARTS_LOCAL_ORG_ID.equals(node.getId())).filter(node -> nodeId != node.getNodeId()).collect(Collectors.toList());
        collect.stream().forEach(node -> {
            if (node.getNodeId() > nodeId) {
                node.setNodeId(node.getNodeId() - 1);
            }
        });
        nodesList = collect;
    }

    public void onSelectBusiType() {
        setConfigEnable();
    }

    //配置信息是否展示
    public void setConfigEnable(){
        List<String> list = Arrays.asList(ApprovalConstant.APPROVAL_ASSET_REPAIR, ApprovalConstant.APPROVAL_ASSET_MAINTAIN, ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT,ApprovalConstant.APPROVAL_ASSET_SCRAP);//设备维修/设备保养/预算管理
        configEnable = list.contains(selected.getBusinessType());
    }

    public void setToUsing(String uid) {
        selected = configDao.findById(uid);
        if(this.enable(true)) return;
        selected.setEnable(true);
        super.save();
    }

    public void setNotUsing(String uid) {
        selected = configDao.findById(uid);
        selected.setEnable(false);
        super.save();
    }

    public String getNameWithOrgName(UserAccount user) {
        OrgInfo org = orgInfoDao.getByUid(user.getOrgInfoUID());
        if(4 == org.getOrgType()){
            return user.getName() + "-" + org.getParentOrg().getName() + "-" + org.getName();
        }
        return user.getName() + "-" + org.getName();
    }

    public String getOrgFullName(String orgUid) {
        OrgInfo org = orgInfoDao.findByUid(orgUid);
        if (null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name = org.getName() + "/" + name;
        }
        return name;
    }

    public String switchName(String msgKey) {
        if (null != msgKey) {
            return i18nDao.findValueZHByKey(msgKey);
        }
        return null;
    }

    @Override
    protected GenericRepositoryUUID<MultiLevelApprovalConfig> getDAO() {
        return configDao;
    }
}
