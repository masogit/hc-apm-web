package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTypeGroupMapper;
import com.ge.apm.dao.mapper.UserOrgInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetTypeGroupDto;
import com.ge.apm.domain.dto.UserOrgInfoDto;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
    private MultiLevelApprovalRepository multiLevelApprovalRepository;

    private UserOrgInfoMapper userOrgInfoMapper;

    private AssetTypeGroupMapper assetTypeGroupMapper;

    @Setter@Getter
    private ApprovalNode approvalNode;
    private RoleApproveCondition roleApproveCondition=new RoleApproveCondition();
    @Setter
    private List<ApprovalNode> nodesList;
    private List<UserAccount> approveUserList;
    private List<UserAccount> approveUserListInit;
    @Getter
    private String operateType;
    @Getter
    private boolean configEnable = true;
    @Getter
    private TreeNode orgTree;
    @Getter
    private OrgInfo selectedUserOrg;

    private UaaService uaaService;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private UserOrgInfoRepository userOrgInfoRepository;
    private boolean selectClinicalDept;
    private boolean selectAssetGroup;
    private boolean approvalRoleAssetHead;
    @Override
    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        configDao = WebUtil.getBean(MultiLevelApprovalConfigRepository.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        roleDao = WebUtil.getBean(SysRoleRepository.class);
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        orgTree= WebUtil.getBean(UaaService.class).buildOrgTree(UserContextService.getCurrentUserAccount().getTenantUID(), "", -1, -1);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        userOrgInfoMapper = WebUtil.getBean(UserOrgInfoMapper.class);
        assetTypeGroupMapper = WebUtil.getBean(AssetTypeGroupMapper.class);
        userOrgInfoRepository = WebUtil.getBean(UserOrgInfoRepository.class);
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
        approveUserListInit=approveUserList;
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
        selectAssetClinicalDeptChange();
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
        getClinicalDepList(selectedOrg.getUid());
        if(CollectionUtils.isNotEmpty(deptInfoList)){
            selected.setSelectAssetClinicalDeptUids(deptInfoList.stream().map(OrgInfo::getUid).collect(Collectors.toList()));
            selectClinicalDept=true;
        }
    }

    public void preEdit() {
        operateType = ApprovalConstant.OPERATE_TYPE_EDIT;
        setConfigEnable();
        switchJsontoVo();
        getClinicalDepList(selected.getHierOrgUid());
        loadAssetTypeGroup();
        selectClinicalDeptCheck();
        setSelectAssetGroupCheck();
    }

    public void preCreate() {
        if(CollectionUtils.isNotEmpty(deptInfoList)){
            deptInfoList.clear();
        }
        operateType = ApprovalConstant.OPERATE_TYPE_CREATE;
        configEnable = true;
        selectAssetGroup=true;
        selectClinicalDept=true;
        try {
            super.prepareCreate();
            //默认启用
            selected.setEnable(true);
            nodesList.clear();

            List<AssetTypeGroup> resultList = assetTypeGroupRepository.listAssetTypeGroupInHierarchy(UserContextService.getCurrentUserAccount().getTenantUID());
            if(CollectionUtils.isNotEmpty(resultList)){
                selected.setSelectAssetTypes(resultList.stream().map(AssetTypeGroup::getUid).collect(Collectors.toList()));
            }
            loadAssetTypeGroup();
            List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup");
            selected.setSelectAssetGroups(assetGroup.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList()));
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
            selected.setSelectAssetTypes((List<String>) map.get("selectAssetTypes"));
            selected.setSelectAssetGroups((List<String>) map.get("selectAssetGroups"));
            selected.setSelectAssetClinicalDeptUids((List<String>) map.get("selectAssetClinicalDeptUids"));
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
                    List<String> userUids = node.getUserUids();
                    List<UserAccount> users = new ArrayList();
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
//        approvalNode.setMultiLevelApprovalUsers(approvalNode.getApproveUsers());
        if(CollectionUtils.isEmpty(approvalNode.getApproveUsers()) &&( StringUtils.isBlank(approvalNode.getRoleApproveCondition().getApproveRole())  ||  approvalNode.getRoleApproveCondition().getCondition()==null )){
            WebUtil.addErrorMessage("请选择审核人");
            return;
        }

        if (null != approvalNode.getNodeId()) {
//            if(approvalNode.isClinicalDepartmentDirector()){
//                List<UserAccount> deptHeadList = buildRoleApproveUserList("DeptHead");
//                if(CollectionUtils.isNotEmpty(deptHeadList)  && CollectionUtils.isNotEmpty(approvalNode.getApproveUsers())){
//                    approvalNode.setClinicalDepartmentDirectorUsers(deptHeadList);
//                }
//            }
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
        node.setRoleApproveCondition(approvalNode.getRoleApproveCondition());
//        if(approvalNode.isClinicalDepartmentDirector()){
//            List<UserAccount> deptHeadList = buildRoleApproveUserList("DeptHead");
//            if(CollectionUtils.isNotEmpty(deptHeadList)  && CollectionUtils.isNotEmpty(node.getApproveUsers())){
////                node.getApproveUsers().addAll(deptHeadList);
//                node.setClinicalDepartmentDirectorUsers(deptHeadList);
//            }
//        }
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
        userAssetTypeUpdate();
        if(CollectionUtils.isEmpty(selected.getSelectAssetClinicalDeptUids())  || CollectionUtils.isEmpty(selected.getSelectAssetGroups())  || (isEnableAssetType()  && CollectionUtils.isEmpty(selected.getSelectAssetTypes())  )  ){
            WebUtil.addWarnMessage("当前配置下配置信息多选条件不能为空");
            return;
        }
        //判断是否已经存在审批流
        Integer minAmountNew = selected.getMinAmount() != null ?selected.getMinAmount() : 0;
        Integer maxAmountNew = selected.getMaxAmount() != null ?selected.getMaxAmount() : 0;
        String configrationOld = selected.getConfigration();
        boolean enable=true;
        if(selected.getId() != null){
            MultiLevelApprovalConfig multiLevelApprovalConfig = configDao.findById(selected.getId());
            enable = multiLevelApprovalConfig.getEnable();
            if(checkExistMultiLevelApproval()){
                WebUtil.addWarnMessage("当前配置下有进行中的多级审批单，不允许编辑&删除！");
                return;
            }
        }
//        userAssetTypeUpdate();
        if (null != configrationOld) {
            Map map = JSON.parseObject(configrationOld, Map.class);
            Integer minAmount = (Integer) map.get("minAmount");
            Integer maxAmount = (Integer) map.get("maxAmount");
//            if (minAmountNew != minAmount || maxAmountNew != maxAmount || !enable){
                if(this.check(minAmountNew, maxAmountNew ,enable)) return;
//            }
        }else {
            if(this.check(minAmountNew, maxAmountNew,enable)) return;
        }


        List<ApprovalNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
        AtomicBoolean nodeApprovalUserIsNull= new AtomicBoolean(false);
        collect.stream().forEach(item -> {
            if (item.getUsingRole()) {
                item.setRoleUids(item.getApproveRoles().stream().map(a -> a.getId()).collect(Collectors.toList()));
                item.setUserUids(null);
            } else {
                if(CollectionUtils.isEmpty(item.getApproveUsers())  &&( StringUtils.isBlank(item.getRoleApproveCondition().getApproveRole())  ||  item.getRoleApproveCondition().getCondition()==null )){
                    nodeApprovalUserIsNull.set(true);
                    WebUtil.addWarnMessage("当前配置下审批节点中审核人为空,不允许保存！");
                    return;
                }
                item.setUserUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
//                item.setMultiLevelApprovalUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
//
//                if(item.isClinicalDepartmentDirector()){
//                    List<String> clinicalDepartmentDirectorUids = item.getClinicalDepartmentDirectorUsers().stream().map(a -> a.getUid()).collect(Collectors.toList());
//                    item.getUserUids().addAll(clinicalDepartmentDirectorUids);
//                    item.setUserUids(item.getUserUids().stream().distinct().collect(Collectors.toList()));
//                }
                item.setRoleUids(null);
            }
        });
        if(nodeApprovalUserIsNull.get()){
            return;
        }
        String nodes = JSON.toJSONString(collect);
        selected.setApprovalItem(nodes);

        Map<String, Object> map = new HashMap();
        map.put("minAmount", selected.getMinAmount());
        map.put("maxAmount", selected.getMaxAmount());
        map.put("selectAssetClinicalDeptUids", selected.getSelectAssetClinicalDeptUids());
        map.put("selectAssetGroups", selected.getSelectAssetGroups());
        map.put("selectAssetTypes", selected.getSelectAssetTypes());
        String configration = JSON.toJSONString(map);
        selected.setConfigration(configration);
        super.save();
    }


    private void userAssetTypeUpdate() {
        Set<AssetTypeGroup> selectedSysAuthGroups = gatherUserAuthGroup();
        selected.setSelectAssetTypes(selectedSysAuthGroups.stream().map(AssetTypeGroup::getUid).collect(Collectors.toList()));

    }

    public Set<AssetTypeGroup> gatherUserAuthGroup() {
        Set<AssetTypeGroup> selectedSysAuthGroups = new HashSet<>();
        readSelectedNodesInAssetTypeGroupTree(assetTypeGroupTree, selectedSysAuthGroups);

        return selectedSysAuthGroups;
    }

    private void readSelectedNodesInAssetTypeGroupTree(TreeNode node, Set<AssetTypeGroup> selectedTreeNodeList) {
        if (node.isSelected()) {
            if (node.getParent() != null && node.getParent().isSelected()) {
                return;
            } else {
                selectedTreeNodeList.add((AssetTypeGroup)node.getData());
            }
        } else {
            for (TreeNode item : node.getChildren()) {
                readSelectedNodesInAssetTypeGroupTree(item, selectedTreeNodeList);
            }
        }
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
                        List<String>   selectAssetTypesOld=(List<String>) map.get("selectAssetTypes");
                        List<String>    selectAssetGroupsOld=(List<String>) map.get("selectAssetGroups");
                        List<String>   selectAssetClinicalDeptUidsOld=(List<String>) map.get("selectAssetClinicalDeptUids");

                        List<String> selectAssetGroups = selected.getSelectAssetGroups();
                        List<String> selectAssetTypes = selected.getSelectAssetTypes();
                        List<String> selectAssetClinicalDeptUids = selected.getSelectAssetClinicalDeptUids();
//                        if(CollectionUtils.isEmpty(selectAssetGroups) || CollectionUtils.isEmpty(selectAssetTypes) ||CollectionUtils.isEmpty(selectAssetClinicalDeptUids) ){
//                            String configrationNew = selected.getConfigration();
//                            if (null != configrationNew) {
//                                Map mapNew = JSON.parseObject(configrationNew, Map.class);
//                                selectAssetTypes=(List<String>) mapNew.get("selectAssetTypes");
//                                selectAssetGroups=(List<String>) mapNew.get("selectAssetGroups");
//                                selectAssetClinicalDeptUids=(List<String>) mapNew.get("selectAssetClinicalDeptUids");
//                            }
//                        }
                        if( (minAmountNew==0 && maxAmountNew==0 ) || (minAmount==0 && maxAmount==0)   ){
                            if (conditionsCheck(approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids))
                                return true;
                        }
                        if( minAmountNew >= minAmount && minAmountNew < maxAmount) {
                            if (conditionsCheck(approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids))
                                return true;
                        }
                        if(( maxAmountNew > minAmount && maxAmountNew <= maxAmount)){
                            if (conditionsCheck(approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids))
                                return true;
//                            WebUtil.addWarnMessage(approvalMsg);
//                            return true;
                        }
                        if( minAmount != 0 && minAmountNew <= minAmount && maxAmountNew >= maxAmount  && maxAmount != 0){
                            if (conditionsCheck(approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids))
//                            WebUtil.addWarnMessage(approvalMsg);
                            return true;
                        }
                        if(  (minAmountNew <= minAmount ||  minAmountNew >= minAmount ) && maxAmountNew == maxAmount  ){
                            if (conditionsCheck(approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids))
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

    private boolean conditionsCheck(String approvalMsg, List<String> selectAssetTypesOld, List<String> selectAssetGroupsOld, List<String> selectAssetClinicalDeptUidsOld, List<String> selectAssetGroups, List<String> selectAssetTypes, List<String> selectAssetClinicalDeptUids) {
        if((CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld) )||( CollectionUtils.isEmpty(selectAssetGroups)  && CollectionUtils.isEmpty(selectAssetGroups) && CollectionUtils.isEmpty(selectAssetClinicalDeptUids))){
            return false;
        }
        if((CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld) ) &&  ( CollectionUtils.isEmpty(selectAssetGroups)  && CollectionUtils.isEmpty(selectAssetGroups) && CollectionUtils.isEmpty(selectAssetClinicalDeptUids))){
            WebUtil.addWarnMessage(approvalMsg);
            return true;
        }
//        if(selectAssetTypesOld.contains(selectAssetTypes)  &&  selectAssetGroupsOld.contains(selectAssetGroups)  && selectAssetClinicalDeptUidsOld.contains(selectAssetClinicalDeptUids)){
//            WebUtil.addWarnMessage(approvalMsg);
//            return true;
//        }

        if((CollectionUtils.containsAny(selectAssetTypesOld, selectAssetTypes) || (CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetTypes)   )  )
                && ( CollectionUtils.containsAny(selectAssetGroupsOld, selectAssetGroups)  || (CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetGroups)  ))
                &&  (CollectionUtils.containsAny(selectAssetClinicalDeptUidsOld, selectAssetClinicalDeptUids) || (CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUids)  )  )){
            WebUtil.addWarnMessage(approvalMsg);
            return true;
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
        approvalNode.setRoleApproveCondition(new RoleApproveCondition());
//        roleApproveCondition = new RoleApproveCondition();
        selectedUserOrg = null;
    }

    public void onEditNode(int nodeId) {
        for (ApprovalNode node : nodesList) {
            if (null != node.getNodeId() && nodeId == node.getNodeId()) {
                approvalNode = node;
                setApprovalRoleAssetHead(StringUtils.isNotEmpty(approvalNode.getApprovalStrategy()));
                if(node.getRoleApproveCondition()==null){
                    approvalNode.setRoleApproveCondition(new RoleApproveCondition());
                }
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
        List<String> list = Arrays.asList(ApprovalConstant.APPROVAL_ASSET_REPAIR, ApprovalConstant.APPROVAL_ASSET_MAINTAIN,ApprovalConstant.APPROVAL_ASSET_SCRAP);//设备维修/设备保养
        configEnable = list.contains(selected.getBusinessType());
    }

    //审核人条件是否展示
    public boolean getApprovalConditionEnable(){
        List<String> list = Arrays.asList(ApprovalConstant.APPROVAL_ASSET_REPAIR) ; //设备维修
        if(selected!=null && selected.getBusinessType()!=null){
            return list.contains(selected.getBusinessType());
        }
        return false;
    }

    public void setToUsing(String uid) {
        selected = configDao.findById(uid);
        String configrationOld = selected.getConfigration();
        if (null != configrationOld) {
            Map map = JSON.parseObject(configrationOld, Map.class);
            selected.setSelectAssetTypes((List<String>) map.get("selectAssetTypes"));
            selected.setSelectAssetGroups((List<String>) map.get("selectAssetGroups"));
            selected.setSelectAssetClinicalDeptUids((List<String>) map.get("selectAssetClinicalDeptUids"));
        }
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




    public RoleApproveCondition getRoleApproveCondition(){
        return roleApproveCondition;
    }

    public void setRoleApproveCondition(RoleApproveCondition roleApproveCondition){
        this.roleApproveCondition= roleApproveCondition;
    }

//    @Override
//    public  void delete(MultiLevelApprovalConfig multiLevelApprovalConfig){
//       int count= multiLevelApprovalRepository.getIncompleteApprovalConfig(multiLevelApprovalConfig.getId());
//       if(count>0){
//           WebUtil.addWarnMessage("当前配置下有进行中的多级审批单，不允许编辑&删除！");
//       }
//
//    }


    @Override
    public  void delete( ){
       if(checkExistMultiLevelApproval()){
           WebUtil.addWarnMessage("当前配置下有进行中的多级审批单，不允许编辑&删除！");
           return;
       }
       super.delete();
    }


    public boolean checkExistMultiLevelApproval(){
        int count= multiLevelApprovalRepository.getIncompleteApprovalConfig(selected.getId());
        return count>0;
    }
    List<OrgInfo>  deptInfoList ;

    public List<AssetTypeGroup> getAssetTypeList(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<AssetTypeGroup> topAuthGroupAndTenantUid = assetTypeGroupRepository.getTopAuthGroupAndTenantUid(userAccount.getTenantUID());
        return topAuthGroupAndTenantUid;
    }

    public List<OrgInfo> getClinicalDepList(String uid){
        if(StringUtils.isBlank(uid)){
            uid=selectedUserOrg.getSiteUID();
        }
        deptInfoList = uaaService.getClinicalDeptListBySiteUID(uid);
        return deptInfoList;
    }


    public boolean isEnableAssetType(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        return userAccount.isEnableAssetTypeGroup();
    }


    public List<OrgInfo> getDeptInfoList() {
        return deptInfoList;
    }

    public void setDeptInfoList(List<OrgInfo> deptInfoList) {
        this.deptInfoList = deptInfoList;
    }
    public void selectAssetTypeChange(){
        userAssetTypeUpdate();

        selectAssetClinicalDeptChange();

    }

    public void selectAssetClinicalDeptChange(){
//        buildApproveUserList();
        approveUserList=approveUserListInit;
        if(selected==null){
            return;
        }
        List<String> selectAssetClinicalDeptUids = selected.getSelectAssetClinicalDeptUids();
        List<String> uids = approveUserList.stream().map(item -> item.getUid()).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(selectAssetClinicalDeptUids) && CollectionUtils.isNotEmpty(approveUserList)){
            List<UserOrgInfoDto> userOrgInfos=  userOrgInfoMapper.findAccessUidByUserUids(uids);
            Map<String, List<UserOrgInfoDto>> userOrgListMap = userOrgInfos.stream().collect(Collectors.groupingBy(UserOrgInfoDto::getUserUid));
            List<String> validUser=new ArrayList<>();
            userOrgListMap.forEach((k, list) -> {
                List<String> collect = list.stream().map(UserOrgInfoDto::getOrgInfoUid).collect(Collectors.toList());
                if(collect.containsAll(selectAssetClinicalDeptUids)){
                    validUser.add(k);
                }
            });
            if(CollectionUtils.isNotEmpty(validUser)){
                approveUserList=approveUserList.stream().filter(item->validUser.contains(item.getUid())).collect(Collectors.toList());
            }else{
                approveUserList=new ArrayList<>();
            }
        }

        List<String> selectAssetTypes = selected.getSelectAssetTypes();
        if(CollectionUtils.isNotEmpty(selectAssetTypes) && CollectionUtils.isNotEmpty(approveUserList) && isEnableAssetType()) {
            Set<AssetTypeGroup> selectedSysAuthGroups = new HashSet<>();
            getSelectAssetType(assetTypeGroupTree,selectedSysAuthGroups);
            List<String> assetTypeUids = selectedSysAuthGroups.stream().map(AssetTypeGroup::getUid).collect(Collectors.toList());
            List<AssetTypeGroupDto> assetTypeByUids = assetTypeGroupMapper.getAssetTypeByUserUids(uids);
            Map<String, List<AssetTypeGroupDto>> assetTypeListMap = assetTypeByUids.stream().collect(Collectors.groupingBy(AssetTypeGroupDto::getUserUid));
            List<String> validUser=new ArrayList<>();
            assetTypeListMap.forEach((k, list) -> {
                List<String> collect = list.stream().map(AssetTypeGroupDto::getUid).collect(Collectors.toList());
                if(collect.containsAll(assetTypeUids)){
                    validUser.add(k);
                }
            });
            if(CollectionUtils.isNotEmpty(validUser)){
                approveUserList=approveUserList.stream().filter(item->validUser.contains(item.getUid())).collect(Collectors.toList());
            }else{
                approveUserList=new ArrayList<>();
            }
        }

        //筛选条件变更校验审批节点中审核人是否存在权限移除
        List<String> approveUserListUids = approveUserList.stream().map(UserAccount::getUid).collect(Collectors.toList());

        nodesList.stream().forEach(item->{
//            List<String> approvalUids = item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(item.getApproveUsers())){
                List<String> invalidUserUid=new ArrayList<>();
                for (UserAccount userAccount : item.getApproveUsers()) {
                    if(!approveUserListUids.contains(userAccount.getUid())){
                        invalidUserUid.add(userAccount.getUid());
                    }
                }
//                approvalUids.removeAll(invalidUserUid);
                List<UserAccount> validUser = item.getApproveUsers().stream().filter(user -> !invalidUserUid.contains(user.getUid())).collect(Collectors.toList());
                item.setApproveUsers(validUser);
            }
        });
    }
    private TreeNode assetTypeGroupTree;
    private TreeNode[] selectedAssetTypeGroupNodes;

    private AssetTypeGroupService assetTypeGroupService;



    public TreeNode getAssetTypeGroupTree() {
        return assetTypeGroupTree;
    }

    private void loadAssetTypeGroup() {
        assetTypeGroupTree = assetTypeGroupService.buildAssetTypeGroupInTree();

        List<String> userAssetGroups = selected.getSelectAssetTypes();
        if(CollectionUtils.isNotEmpty(userAssetGroups)){
            setSelectedSysAuthGroupTree(assetTypeGroupTree, userAssetGroups);
        }
    }

    private void setSelectedSysAuthGroupTree(TreeNode node, List<String> userAuthGroupUids) {
        if (node.getData() instanceof AssetTypeGroup) {
            AssetTypeGroup assetTypeGroup = (AssetTypeGroup) node.getData();
            if (userAuthGroupUids.contains(assetTypeGroup.getUid())) {
                node.setSelected(true);
            } else if (node.getParent() != null && node.getParent().isSelected()) {
                node.setSelected(true);
            }
        }

        for (TreeNode item : node.getChildren()) {
            setSelectedSysAuthGroupTree(item, userAuthGroupUids);
        }
    }

    private void getSelectAssetType(TreeNode node, Set<AssetTypeGroup> selectedSysAuthGroups) {
            if (node.getChildren()==null) {
                selectedSysAuthGroups.add((AssetTypeGroup)node.getData());
            }else{
                for (TreeNode item : node.getChildren()) {
                    readSelectedNodesInAssetTypeGroupTree(item, selectedSysAuthGroups);
                }
            }


    }

    public void onSelectAssetTypeGroupTreeNode(NodeSelectEvent event) {
        setTreeNodes(event.getTreeNode(), true);
        selectAssetTypeChange();
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    public void onUnselectAssetTypeGroupTreeNode(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
        selectAssetTypeChange();
    }

    public TreeNode[] getSelectedAssetTypeGroupNodes() {
        return selectedAssetTypeGroupNodes;
    }

    public void setSelectedAssetTypeGroupNodes(TreeNode[] selectedAssetTypeGroupNodes) {
        this.selectedAssetTypeGroupNodes = selectedAssetTypeGroupNodes;
    }

    public boolean getSelectClinicalDept() {
        return selectClinicalDept;
    }

    public void setSelectClinicalDept(boolean selectClinicalDept) {
        this.selectClinicalDept = selectClinicalDept;
    }

    public boolean getSelectAssetGroup() {
        return selectAssetGroup;
    }

    public void setSelectAssetGroup(boolean selectAssetGroup) {
        this.selectAssetGroup = selectAssetGroup;
    }

    public void selectClinicalDeptUids(){
        if(selectClinicalDept && CollectionUtils.isNotEmpty(deptInfoList)){
            selected.setSelectAssetClinicalDeptUids(deptInfoList.stream().map(OrgInfo::getUid).collect(Collectors.toList()));
        }else{
           selected.setSelectAssetClinicalDeptUids(new ArrayList<>());
        }
    }
    List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup");

    public void selectAssetGroupUids(){
        if(selectAssetGroup && CollectionUtils.isNotEmpty(assetGroup)){
            selected.setSelectAssetGroups(assetGroup.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList()));
        }else{
            selected.setSelectAssetGroups(new ArrayList<>());
        }
    }
    public void selectClinicalDeptCheck(){
        selectClinicalDept = deptInfoList.size() == selected.getSelectAssetClinicalDeptUids().size();
    }
    public void setSelectAssetGroupCheck(){
        selectAssetGroup = assetGroup.size() == selected.getSelectAssetGroups().size();
    }

    public boolean getApprovalRoleAssetHead() {
        return approvalRoleAssetHead;
    }

    public void setApprovalRoleAssetHead(boolean approvalRoleAssetHead) {
        this.approvalRoleAssetHead = approvalRoleAssetHead;
    }

    public void changeApprovalStrategy(){
        if(this.approvalRoleAssetHead){
            // Approval by the superior  上级科室主任审批
            this.approvalNode.setApprovalStrategy("ApprovalSuperior");
        }else{
            this.approvalNode.setApprovalStrategy("");
        }
    }
}
