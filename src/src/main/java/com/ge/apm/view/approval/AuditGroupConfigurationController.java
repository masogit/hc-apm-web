package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author 503189829
 * @create 2021/12/1
 * @since 1.0.0
 */

@ManagedBean
@ViewScoped
public class AuditGroupConfigurationController extends GenericCRUDUUIDController<AuditGroupConfiguration> {

    private AuditGroupConfigurationRepository auditGroupConfigurationRepository;
    private OrgInfoRepository orgInfoDao;
    private SysRoleRepository roleDao;
    private UserAccountRepository userAccountDao;
    private UserRoleRepository userRoleDao;
    private I18nMessageRepository i18nDao;

    @Setter
    @Getter
    private AuditGroupConfigurationNode auditGroupConfigurationNode;
    @Setter
    private List<AuditGroupConfigurationNode> nodesList;
    private List<UserAccount> approveUserList;
    @Getter
    private String operateType;
    @Getter
    private OrgInfo selectedUserOrg;

    @Override
    protected GenericRepositoryUUID<AuditGroupConfiguration> getDAO() {
        return auditGroupConfigurationRepository;
    }

    @Override
    protected void init() {
        auditGroupConfigurationRepository = WebUtil.getBean(AuditGroupConfigurationRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        roleDao = WebUtil.getBean(SysRoleRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        buildApproveUserList();
    }
    //获取拥有审核权限的用户
    public void buildApproveUserList() {
        approveUserList = new ArrayList<>();
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        String siteUID = userAccount.getSiteUID();
        approveUserList= userAccountDao.findBySiteUID(siteUID);
    }
    @Override
    protected Page<AuditGroupConfiguration> loadData(PageRequest pageRequest) {
        Page<AuditGroupConfiguration> bySearchFilter = auditGroupConfigurationRepository.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;
    }
    public void preCreate() throws IllegalAccessException, InstantiationException {
        operateType = ApprovalConstant.OPERATE_TYPE_CREATE;
        nodesList.clear();
        super.prepareCreate();
    }
    //保存分组节点
    public void saveNode() {
        if (null != auditGroupConfigurationNode.getNodeId()) {
            return;
        }
        AuditGroupConfigurationNode node = new AuditGroupConfigurationNode();
        node.setId(UUID.randomUUID().toString().replace("-", ""));
        node.setNodeId(nodesList.size());
        node.setApproveUsers(auditGroupConfigurationNode.getApproveUsers());
        nodesList.add(node);
    }
    public void preEdit() {
        operateType = ApprovalConstant.OPERATE_TYPE_EDIT;
        nodesList.clear();
        String groupMemberItem = selected.getGroupMemberItem();
        if (null != groupMemberItem) {
            List<AuditGroupConfigurationNode> auditGroupConfigurationNodes = JSON.parseObject(groupMemberItem, new TypeReference<List<AuditGroupConfigurationNode>>(){
            });
            for (int i = 0; i < auditGroupConfigurationNodes.size(); i++) {
                AuditGroupConfigurationNode node = auditGroupConfigurationNodes.get(i);
                    List<String> userUids = node.getUserUids();
                    List<UserAccount> users = new ArrayList();
                    userUids.stream().forEach(useruid -> {
                        users.add(userAccountDao.getByUid(useruid));
                    });
                    node.setApproveUsers(users);
                node.setNodeId(i + 1);
            }
            nodesList.addAll(auditGroupConfigurationNodes);
        }
    }
    public void delNode(int nodeId) {
        List<AuditGroupConfigurationNode> collect = nodesList.stream().filter(node -> !SimplePartsConstant.PARTS_LOCAL_ORG_ID.equals(node.getId())).filter(node -> nodeId != node.getNodeId()).collect(Collectors.toList());
        collect.stream().forEach(node -> {
            if (node.getNodeId() > nodeId) {
                node.setNodeId(node.getNodeId() - 1);
            }
        });
        nodesList = collect;
    }
    public List<UserAccount> getUsersAudit(){
        if(selectedUserOrg != null){
            return approveUserList.stream().filter(user -> user.getOrgInfoUID().equals(selectedUserOrg.getUid())).collect(Collectors.toList());
        }
        return this.approveUserList;
    }
    public String getNameWithOrgName(UserAccount user) {
        if(user!=null){
            OrgInfo org = orgInfoDao.getByUid(user.getOrgInfoUID());
            if(4 == org.getOrgType()){
                return user.getName() + "-" + org.getParentOrg().getName() + "-" + org.getName();
            }
            return user.getName() + "-" + org.getName();
        }
        return null;
    }
    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
    }
    public void onAddNode() {
        auditGroupConfigurationNode = new AuditGroupConfigurationNode();
        selectedUserOrg = null;
    }

    public List<AuditGroupConfigurationNode> getNodesList() {
        AuditGroupConfigurationNode node = new AuditGroupConfigurationNode();
        node.setId(SimplePartsConstant.PARTS_LOCAL_ORG_ID);
        if (null == nodesList) {
            nodesList = new ArrayList<>();
        } else {
            List<AuditGroupConfigurationNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
            nodesList = collect;
        }
        nodesList.add(node);
        return nodesList;
    }
    public List<I18nMessage> getGroupNameList(){
        return i18nDao.findByMsgTypeOrderById("auditGroupName");
    }
    //保存配置
    public void save() {
        List<AuditGroupConfigurationNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
        collect.stream().forEach(item -> {
            item.setUserUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
        });
        String nodes = JSON.toJSONString(collect);
        selected.setGroupMemberItem(nodes);
        super.save();
    }
    public String getName(List<UserAccount> approveUsers){
        if (approveUsers!=null && !approveUsers.isEmpty()){
            for (UserAccount userAccount:approveUsers) {
                return userAccount.getName();
            }
        }
        return null;
    }
    public String getGroupMember(String groupMemberItem){
        String groupMember=null;
        if (!StringUtils.isEmpty(groupMemberItem)){
            List<AuditGroupConfigurationNode> userUids = JSON.parseObject(groupMemberItem, new TypeReference<List<AuditGroupConfigurationNode>>() {});
            if (userUids!=null && !userUids.isEmpty()){
                for (int i = 0; i < userUids.size(); i++) {
                    UserAccount account = userAccountDao.getByUid(userUids.get(i).getUserUids().get(0));
                    if(i==0){
                        groupMember=account.getName();
                    }else{
                        groupMember=groupMember+"/"+account.getName();
                    }


                }

            }
        }
        return groupMember;
    }
}
