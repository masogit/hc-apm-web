package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTypeGroupMapper;
import com.ge.apm.dao.mapper.UserOrgInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetTypeGroupDto;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.domain.dto.UserOrgInfoDto;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.ge.apm.service.constant.ApprovalConstant.APPROVAL_SEPARATOR;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalConfigController extends GenericCRUDUUIDController<MultiLevelApprovalConfig> {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalConfigController.class);

    private MultiLevelApprovalConfigRepository configDao;
    private UserAccountRepository userAccountDao;
    private UserRoleRepository userRoleDao;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgInfoDao;
    private SysRoleRepository roleDao;
    private MultiLevelApprovalRepository multiLevelApprovalRepository;

    private UserOrgInfoMapper userOrgInfoMapper;

    private AssetTypeGroupMapper assetTypeGroupMapper;

    private ConfigCacheService configCacheService;

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

    @Getter
    @Setter
    private String approvalUserReplaceUid;

    @Getter
    @Setter
    private UserAccount approvalUserFirst;

    @Getter
    @Setter
    private UserAccount approvalUserReplace;

    private I18nMessageService i18nMessageService;

    @Getter
    @Setter
    private List<I18nMessage> partsTypes=new ArrayList<>();

    @Getter
    @Setter
    private List<SysRoleVO> roleListVO;

    private SysRoleRepository sysRoleRepository;
    private CustomRoleRepository customRoleRepository;

    @Getter
    @Setter
    private String filterBusinessType  = null;

    @Getter
    @Setter
    private boolean selectAssetGroupParts;
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
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        buildApproveUserList();
        partsTypes = i18nMessageService.getByMsgType("partsType");
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
        if("assetRepair".equals(selected.getBusinessType())){
            setSelectAssetPartsCheck();
        }
        selectAssetClinicalDeptChange();
    }

    public void preCreate() {
        if(CollectionUtils.isNotEmpty(deptInfoList)){
            deptInfoList.clear();
        }
        operateType = ApprovalConstant.OPERATE_TYPE_CREATE;
        configEnable = true;
        selectAssetGroup=true;
        selectClinicalDept=true;
        selectAssetGroupParts=true;
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
            selected.setSelectAssetPartsTypes(partsTypes.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList()));
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
            if("assetRepair".equals(selected.getBusinessType())){
                selected.setSelectAssetPartsTypes((List<String>) map.get("selectAssetPartsTypes"));
            }
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
                    if(CollectionUtils.isNotEmpty(userUids)){
                        List<UserAccount> users = new ArrayList();
                        userUids.stream().forEach(useruid -> {
                            users.add(userAccountDao.getByUid(useruid));
                        });
                        node.setApproveUsers(users);
                    }
                }
                node.setNodeId(i + 1);
            }
            nodesList.addAll(approvalNodes);
        }
    }

    //保存审批节点
    public void saveNode() {
//        approvalNode.setMultiLevelApprovalUsers(approvalNode.getApproveUsers());
        if(StringUtils.isEmpty(approvalNode.getApprovalStrategy()) && CollectionUtils.isEmpty(approvalNode.getApproveUsers()) &&( CollectionUtils.isEmpty(approvalNode.getSelectedRoles())  ||  approvalNode.getRoleApproveCondition().getCondition()==null )){
            WebUtil.addErrorMessage("请选择审核人");
            return;
        }
        RoleApproveCondition roleApproveCondition = approvalNode.getRoleApproveCondition();
        if(CollectionUtils.isNotEmpty(approvalNode.getSelectedRoles())){
            roleApproveCondition.setApproveRole(StringUtils.join(approvalNode.getSelectedRoles(), ","));
        }

        approvalNode.setRoleApproveCondition(roleApproveCondition);

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
        node.setApprovalStrategy(approvalNode.getApprovalStrategy());
        node.setUsedOperatingRoom(approvalNode.getUsedOperatingRoom());
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
        if(CollectionUtils.isEmpty(selected.getSelectAssetClinicalDeptUids())  || CollectionUtils.isEmpty(selected.getSelectAssetGroups())  || (isEnableAssetType()  && CollectionUtils.isEmpty(selected.getSelectAssetTypes()))     || ( "assetRepair".equals(selected.getBusinessType())  && CollectionUtils.isEmpty(selected.getSelectAssetPartsTypes()))               ){
            WebUtil.addWarnMessage("当前配置下配置信息多选条件不能为空");
            return;
        }
        if("assetRepair".equals(selected.getBusinessType()) &&  partsTypes.size()!=selected.getSelectAssetPartsTypes().size() && selected.getSelectAssetPartsTypes().size()>1 ){
            WebUtil.addWarnMessage("只能选择一个或者全选备件类型");
            return ;
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
            Integer minAmount = Optional.ofNullable((Integer) map.get("minAmount")).orElse(0);
            Integer maxAmount = Optional.ofNullable((Integer) map.get("maxAmount")).orElse(0);
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
                if(StringUtils.isEmpty(item.getApprovalStrategy()) && CollectionUtils.isEmpty(item.getApproveUsers())  &&( StringUtils.isBlank(item.getRoleApproveCondition().getApproveRole())  ||  item.getRoleApproveCondition().getCondition()==null )){
                    nodeApprovalUserIsNull.set(true);
                    WebUtil.addWarnMessage("当前配置下审批节点中审核人为空,不允许保存！");
                    return;
                }
                if(CollectionUtils.isNotEmpty(item.getApproveUsers())){
                    item.setUserUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
                }else{
                    item.setUserUids(null);
                }
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
        if(checkNode(collect)){
            WebUtil.addWarnMessage("当前配置下审批节点上级审批不可唯一,不允许保存！");
            return;
        }
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
        map.put("selectAssetPartsTypes", selected.getSelectAssetPartsTypes());
        String configration = JSON.toJSONString(map);
        selected.setConfigration(configration);
        super.save();
    }
    private boolean checkNode(List<ApprovalNode> nodes){
        boolean flag = Boolean.FALSE;
        if(CollectionUtils.isNotEmpty(nodes)){
            int size = nodes.size();
            ApprovalNode approvalNode = null;
            if(size == 1){
                approvalNode = nodes.get(0);
            }else{
                approvalNode = nodes.get(size - 1);
            }
            String approvalStrategy = approvalNode.getApprovalStrategy();
            if(StringUtils.isNotBlank(approvalStrategy)){
                flag = Boolean.TRUE;
            }
        }
        return flag;
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
        String approvalMsg = "已存在同级别审批流";
        /*if (selected.getBusinessType().equals(ApprovalConstant.APPROVAL_ASSET_DEPARTMENT_CONVERT)) {
            if (multiLevelApprovalConfigs.size() >= 2) {
                WebUtil.addWarnMessage(approvalMsg);
                return true;
            }
        }*/
        if(!multiLevelApprovalConfigs.isEmpty() && multiLevelApprovalConfigs != null){
            for (MultiLevelApprovalConfig multiLevelApprovalConfig :multiLevelApprovalConfigs){
                String configration = multiLevelApprovalConfig.getConfigration();
                if (minAmountNew != null && maxAmountNew != null && null != configration) {
                    if(!multiLevelApprovalConfig.getId().equals(selected.getId()) || !enable){
                        Map map = JSON.parseObject(configration, Map.class);
                        Integer minAmount = Optional.ofNullable(((Integer) map.get("minAmount"))).orElse(0);
                        Integer maxAmount = Optional.ofNullable(((Integer) map.get("maxAmount"))).orElse(0);
                        List<String>   selectAssetTypesOld=(List<String>) map.get("selectAssetTypes");
                        List<String>    selectAssetGroupsOld=(List<String>) map.get("selectAssetGroups");
                        List<String>   selectAssetClinicalDeptUidsOld=(List<String>) map.get("selectAssetClinicalDeptUids");
                        List<String>   selectAssetPartsTypesOld=(List<String>) map.get("selectAssetPartsTypes");

                        List<String> selectAssetGroups = selected.getSelectAssetGroups();
                        List<String> selectAssetTypes = selected.getSelectAssetTypes();
                        List<String> selectAssetClinicalDeptUids = selected.getSelectAssetClinicalDeptUids();
                        List<String> selectAssetPartsTypes = selected.getSelectAssetPartsTypes();
                        if( (minAmountNew==0 && maxAmountNew==0 ) || (minAmount==0 && maxAmount==0)){
                            if (conditionsCheck(multiLevelApprovalConfig, approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids,selectAssetPartsTypesOld,selectAssetPartsTypes)) {
                                return true;
                            }
                        }
                        if( minAmountNew >= minAmount && minAmountNew < maxAmount) {
                            if (conditionsCheck(multiLevelApprovalConfig, approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids,selectAssetPartsTypesOld,selectAssetPartsTypes))
                                return true;
                        }
                        if(( maxAmountNew > minAmount && maxAmountNew <= maxAmount)){
                            if (conditionsCheck(multiLevelApprovalConfig, approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids,selectAssetPartsTypesOld,selectAssetPartsTypes))
                                return true;
                        }
                        if( minAmount != 0 && minAmountNew <= minAmount && maxAmountNew >= maxAmount  && maxAmount != 0){
                            if (conditionsCheck(multiLevelApprovalConfig, approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids,selectAssetPartsTypesOld,selectAssetPartsTypes))
                            return true;
                        }
                        if(  (minAmountNew <= minAmount ||  minAmountNew >= minAmount ) && maxAmountNew == maxAmount  ){
                            if (conditionsCheck(multiLevelApprovalConfig, approvalMsg, selectAssetTypesOld, selectAssetGroupsOld, selectAssetClinicalDeptUidsOld, selectAssetGroups, selectAssetTypes, selectAssetClinicalDeptUids,selectAssetPartsTypesOld,selectAssetPartsTypes))
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

    private boolean conditionsCheck(MultiLevelApprovalConfig multiLevelApprovalConfig, String approvalMsg, List<String> selectAssetTypesOld, List<String> selectAssetGroupsOld, List<String> selectAssetClinicalDeptUidsOld, List<String> selectAssetGroups, List<String> selectAssetTypes, List<String> selectAssetClinicalDeptUids, List<String> selectAssetPartTypesOld, List<String> selectAssetPartTypes) {
        if((CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld) )||( CollectionUtils.isEmpty(selectAssetGroups)  && CollectionUtils.isEmpty(selectAssetGroups) && CollectionUtils.isEmpty(selectAssetClinicalDeptUids))){
            return false;
        }
        if((CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld) ) &&  ( CollectionUtils.isEmpty(selectAssetGroups)  && CollectionUtils.isEmpty(selectAssetGroups) && CollectionUtils.isEmpty(selectAssetClinicalDeptUids))){
            WebUtil.addWarnMessage(approvalMsg);
            return true;
        }

        if((CollectionUtils.containsAny(selectAssetTypesOld, selectAssetTypes) || (CollectionUtils.isEmpty(selectAssetTypesOld)  &&   CollectionUtils.isEmpty(selectAssetTypes)   )  )
                && ( CollectionUtils.containsAny(selectAssetGroupsOld, selectAssetGroups)  || (CollectionUtils.isEmpty(selectAssetGroupsOld)  &&   CollectionUtils.isEmpty(selectAssetGroups)  ))
                &&  (CollectionUtils.containsAny(selectAssetClinicalDeptUidsOld, selectAssetClinicalDeptUids) || (CollectionUtils.isEmpty(selectAssetClinicalDeptUidsOld)  &&   CollectionUtils.isEmpty(selectAssetClinicalDeptUids)  )  )){
            if("assetRepair".equals(selected.getBusinessType())){
                if((CollectionUtils.containsAny(selectAssetPartTypesOld, selectAssetPartTypes) || (CollectionUtils.isEmpty(selectAssetPartTypesOld)  &&   CollectionUtils.isEmpty(selectAssetPartTypes)  )  )   ){
                    WebUtil.addWarnMessage(approvalMsg);
                    return true;
                }
            /*} else if (ApprovalConstant.APPROVAL_ASSET_DEPARTMENT_CONVERT.equals(selected.getBusinessType())) {
                String approvalStrategy;
                if ("Create".equals(super.crudActionName)) {
                    List<ApprovalNode> collect = nodesList.stream().filter(item -> !Objects.equals(item.getId(), SimplePartsConstant.PARTS_LOCAL_ORG_ID)).collect(Collectors.toList());
                    approvalStrategy = JSON.toJSONString(collect);
                } else {
                    approvalStrategy = selected.getApprovalItem();
                }
                if ((approvalStrategy.contains("approvalStrategy") && multiLevelApprovalConfig.getApprovalItem().contains("approvalStrategy")) || (!approvalStrategy.contains("approvalStrategy") && !multiLevelApprovalConfig.getApprovalItem().contains("approvalStrategy"))) {
                    WebUtil.addWarnMessage(approvalMsg);
                    return true;
                } else {
                    return false;
                }*/
            } else {
                WebUtil.addWarnMessage(approvalMsg);
                return true;
            }
        }
        if("assetRepair".equals(selected.getBusinessType())){
            if(partsTypes.size()==selectAssetPartTypes.size()){
                return false;
            }
            if(partsTypes.size()!=selectAssetPartTypes.size() && selectAssetPartTypes.size()>1){
                WebUtil.addWarnMessage("只能选择一个或者全选备件类型");
                return true;
            }
            if(selectAssetPartTypesOld.contains(selectAssetPartTypes.get(0))){
                WebUtil.addWarnMessage("选择的备件类型已经存在其他审批流中");
                return true;
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
                Integer minAmount = Optional.ofNullable((Integer) map.get("minAmount")).orElse(0);
                Integer maxAmount = Optional.ofNullable((Integer) map.get("maxAmount")).orElse(0);
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
    public List<String> getNodeApprovalUser(ApprovalNode approvalNode) {
        List<String> approvalUsers = new ArrayList<>();
        if (approvalNode == null || approvalNode.getNodeId() == null) {
            return approvalUsers; // 如果审批节点为空或没有节点ID，直接返回空列表
        }
        ApprovalNode matchedNode = nodesList.stream()
                .filter(ite -> Objects.equals(ite.getNodeId(), approvalNode.getNodeId()))
                .findFirst()
                .orElse(null);
        if (matchedNode == null) {
            return approvalUsers;
        }
        addApproveUsers(approvalUsers, matchedNode);
        addRoleByApprovalStrategy(approvalUsers, matchedNode);
        addRoleByRoleApproveCondition(approvalUsers, matchedNode);
        return approvalUsers;
    }

    private void addApproveUsers(List<String> approvalUsers, ApprovalNode node) {
        if (CollectionUtils.isNotEmpty(node.getApproveUsers())) {
            approvalUsers.addAll(node.getApproveUsers().stream()
                    .map(UserAccount::getName)
                    .collect(Collectors.toList()));
        }
    }

    private void addRoleByApprovalStrategy(List<String> approvalUsers, ApprovalNode node) {
        if (node.getApprovalStrategy() != null) {
            approvalUsers.add(ApmSystemRole.DeptHead.getRoleName());
        }
    }

    private void addRoleByRoleApproveCondition(List<String> approvalUsers, ApprovalNode node) {
        RoleApproveCondition roleApproveCondition = node.getRoleApproveCondition();
        if (roleApproveCondition != null && roleApproveCondition.getApproveRole() != null && roleApproveCondition.getCondition() != null) {
            try {
//                ApmSystemRole role = ApmSystemRole.valueOf(roleApproveCondition.getApproveRole());
//                approvalUsers.add(role.getRoleName());
                List<String> selectedRoles = new ArrayList<>(Arrays.asList(roleApproveCondition.getApproveRole().split(",")));
                List<String> collect = Arrays.stream(ApmSystemRole.values()).map(ApmSystemRole::name).collect(Collectors.toList());
                List<String> sysRoles = collect.stream().filter(selectedRoles::contains).collect(Collectors.toList());
                List<String> customRoles = selectedRoles.stream().filter(item -> !sysRoles.contains(item)).collect(Collectors.toList());

                if(CollectionUtils.isNotEmpty(sysRoles)){
                    List<SysRole> sysRoleList = sysRoleRepository.getByNameIn(sysRoles);
                    if(CollectionUtils.isNotEmpty(sysRoleList)){
                        approvalUsers.addAll(sysRoleList.stream().map(SysRole::getRoleDesc).collect(Collectors.toList()));
                    }
                }

                if(CollectionUtils.isNotEmpty(customRoles)){
                    UserAccount userAccount = UserContextService.getCurrentUserAccount();
                    List<CustomRole> customRoleList = customRoleRepository.findByTenantUIDAndNameIn(userAccount.getTenantUID(),customRoles);
                    if(CollectionUtils.isNotEmpty(customRoleList)){
                        approvalUsers.addAll(customRoleList.stream().map(CustomRole::getName).collect(Collectors.toList()));
                    }
                }
            } catch (Exception e) {
                logger.error("审核角色不存在 ApproveRole:{}",roleApproveCondition.getApproveRole());
            }
        }
    }

    public void onAddNode() {
        approvalNode = new ApprovalNode();
        approvalNode.setRoleApproveCondition(new RoleApproveCondition());
//        roleApproveCondition = new RoleApproveCondition();
        selectedUserOrg = null;
        approvalRoleAssetHead = Boolean.FALSE;

        getSystemRoleList();
        approvalNode.setSelectedRoles(new ArrayList<>());
    }

    public void onEditNode(int nodeId) {
        for (ApprovalNode node : nodesList) {
            if (null != node.getNodeId() && nodeId == node.getNodeId()) {
                approvalNode = node;
                setApprovalRoleAssetHead(StringUtils.isNotEmpty(approvalNode.getApprovalStrategy()));
                if(node.getRoleApproveCondition()==null){
                    approvalNode.setRoleApproveCondition(new RoleApproveCondition());
                    approvalNode.setSelectedRoles(new ArrayList<>());
                }else{
                    RoleApproveCondition roleApproveCondition1 = node.getRoleApproveCondition();
                    String approveRole = roleApproveCondition1.getApproveRole();
                    if(StringUtils.isNotBlank(approveRole)){
                        List<String> selectedRoles = new ArrayList<>(Arrays.asList(approveRole.split(",")));
                        approvalNode.setSelectedRoles(selectedRoles);
                    }
                }
            }
        }
        approvalUserFirst=null;
        approvalUserReplace=null;
        getSystemRoleList();
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
        if(CollectionUtils.isEmpty(partsTypes) && "assetRepair".equals(selected.getBusinessType()) ){
            partsTypes = i18nMessageService.getByMsgType("partsType");
        }
        if(!"assetRepair".equals(selected.getBusinessType())){
            selected.setSelectAssetPartsTypes(new ArrayList<>());
        }
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
            selected.setSelectAssetPartsTypes((List<String>) map.get("selectAssetPartsTypes"));
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
        OrgInfo org = configCacheService.findOrgByUid(user.getOrgInfoUID());
//        OrgInfo org = orgInfoDao.getByUid(user.getOrgInfoUID());
        if (org == null) {
            return user.getName();

        }
        if(4 == org.getOrgType()){
            return user.getName() + "-" + org.getParentOrg().getName() + "-" + org.getName();
        }
        return user.getName() + "-" + org.getName();
    }

    public String getOrgFullName(String orgUid) {
        OrgInfo org = configCacheService.findOrgByUid(orgUid);
//        OrgInfo org = orgInfoDao.findByUid(orgUid);
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

        int roleId = roleDao.getIdByName(ApprovalConstant.MULTILEVEL_AUDITOR);
        List<String> selectAssetClinicalDeptUids = selected.getSelectAssetClinicalDeptUids();
        List<String> uids = approveUserList.stream().map(item -> item.getUid()).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(selectAssetClinicalDeptUids) && CollectionUtils.isNotEmpty(approveUserList)){

            ArrayList<String> orgUids = new ArrayList<>();

            OrgInfo org = configCacheService.findOrgByUid(selected.getHierOrgUid());

            orgUids.add(org.getSiteUID());
            orgUids.add(org.getHospitalUID());
            orgUids.add(org.getInstitutionUID());
            orgUids.add(org.getTenantUID());
            orgUids.addAll(selectAssetClinicalDeptUids);



            long s = System.currentTimeMillis();
//            List<UserOrgInfoDto> userOrgInfos=  userOrgInfoMapper.findAccessUidByUserUids(uids);
            approveUserList= userAccountDao.findByUserOrgAndRoleId(orgUids,roleId);
            long e = System.currentTimeMillis();
            logger.info("selectAssetClinicalDeptChange 查询消耗时间:{}",e-s);
//            Map<String, List<UserOrgInfoDto>> userOrgListMap = userOrgInfos.stream().collect(Collectors.groupingBy(UserOrgInfoDto::getUserUid));
//            List<String> validUser=new ArrayList<>();
//            userOrgListMap.forEach((k, list) -> {
//                List<String> collect = list.stream().map(UserOrgInfoDto::getOrgInfoUid).collect(Collectors.toList());
//                if(collect.containsAll(selectAssetClinicalDeptUids)){
//                    validUser.add(k);
//                }
//            });
//            if(CollectionUtils.isNotEmpty(validUser)){
//                approveUserList=approveUserList.stream().filter(item->validUser.contains(item.getUid())).collect(Collectors.toList());
//            }else{
//                approveUserList=new ArrayList<>();
//            }
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
    public void selectAssetParts(){
        if(selectAssetGroupParts && CollectionUtils.isNotEmpty(partsTypes)){
            selected.setSelectAssetPartsTypes(partsTypes.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList()));
        }else{
            selected.setSelectAssetPartsTypes(new ArrayList<>());
        }
    }
    public void setSelectAssetPartsCheck(){
        selectAssetGroupParts= Optional.ofNullable(selected.getSelectAssetPartsTypes()).map(selectAssetParts->partsTypes.size() ==selectAssetParts.size()).orElse(false);
    }

    public void selectClinicalDeptCheck(){
        selectClinicalDept = Optional.ofNullable(selected.getSelectAssetClinicalDeptUids())
                .map(assetClinicalDeptUids -> deptInfoList.size() == assetClinicalDeptUids.size())
                .orElse(false);
    }
    public void setSelectAssetGroupCheck(){
        selectAssetGroup= Optional.ofNullable(selected.getSelectAssetGroups()).map(selectAssetGroups->assetGroup.size() ==selectAssetGroups.size()).orElse(false);
    }

    public boolean getApprovalRoleAssetHead() {
        return approvalRoleAssetHead;
    }

    public List<SelectItemDto> getApproveCondition() {
        List<SelectItemDto> resultList = ListUtil.newArrayList();
        switch (this.selected.getBusinessType()) {
            case ApprovalConstant.APPROVAL_ASSET_REPAIR :
                resultList.add(new SelectItemDto("工单设备所属科室", "1"));
                break;
            default:
                break;

        }
        return resultList;
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

    public void approvalUserUpdate(){
        logger.info("approvalUserUpdate select:{},approvalNode:{},approvalUserReplace:{}",JSON.toJSONString(selected),JSON.toJSONString(approvalNode),approvalUserReplace.getUid());
        if( approvalUserReplace==null){
            WebUtil.addWarnMessage("请选择替换的审批人员");
            return;
        }
        Boolean approveUsersExist = Optional.ofNullable(approvalNode.getApproveUsers()).map(nodes -> nodes.stream().anyMatch(item -> item.getUid().equals(approvalUserReplace.getUid()))).orElse(false);
        if(approveUsersExist){
            WebUtil.addWarnMessage("替换的审批人员已在审批人员中");
            return;
        }
        if(approvalUserFirst!=null && approvalUserFirst.getUid().equals(approvalUserReplace.getUid()) ){
            WebUtil.addWarnMessage("选中的审批人不能相同");
            return;
        }
        List<UserAccount> approvalUsers =new ArrayList<>();
        if(approvalUserFirst!=null){
            approvalUsers=Optional.ofNullable(approvalNode.getApproveUsers()).map(nodes->nodes.stream().filter(item -> !item.getUid().equals(approvalUserFirst.getUid())).collect(Collectors.toList())).orElse(new ArrayList<>());
        }else{
            approvalUsers = approvalNode.getApproveUsers() != null ? approvalNode.getApproveUsers() : new ArrayList<>();
        }
        approvalUsers.add(approvalUserReplace);
        approvalNode.setApproveUsers(approvalUsers);
        //更新配置
        List<ApprovalNode> collect = nodesList.stream().filter(item -> item.getId() != SimplePartsConstant.PARTS_LOCAL_ORG_ID).collect(Collectors.toList());
        collect.stream().forEach(item -> {
            if (item.getUsingRole()) {
                item.setRoleUids(item.getApproveRoles().stream().map(a -> a.getId()).collect(Collectors.toList()));
                item.setUserUids(null);
            } else {
                if(CollectionUtils.isNotEmpty(item.getApproveUsers())){
                    item.setUserUids(item.getApproveUsers().stream().map(a -> a.getUid()).collect(Collectors.toList()));
                }
                item.setRoleUids(null);
            }
        });
        String nodes = JSON.toJSONString(collect);
        selected.setApprovalItem(nodes);
        configDao.save(selected);
        //更新审批节点

        List<String> approvalUids=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(approvalNode.getApproveUsers())){
            approvalUids= approvalNode.getApproveUsers().stream().filter(item -> org.apache.commons.lang.StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
        }
        Object[] objects = approvalUids.toArray();
        String str = org.apache.commons.lang.StringUtils.join(objects, ",");

        if (StringUtils.isNotBlank(str)) {
            str = APPROVAL_SEPARATOR + str + APPROVAL_SEPARATOR;
        }

        multiLevelApprovalRepository.updateApprovalUser(approvalNode.getNodeId(),str,selected.getId());

        WebUtil.addSuccessMessage("多级审批更换审批人员成功");
        approvalUserReplace=null;
        approvalUserFirst=null;
    }


    private void  getSystemRoleList(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        roleListVO = uaaService.getSysRoleVO(userAccount.getTenantUID());

        if (uaaService.isOpenNewMenuRoles(userAccount.getTenantUID())) {
            Set<String> tenantSysRoles = uaaService.getSysRoleListByTenantUid(userAccount.getTenantUID());
            Set<String> tenantCustomRoles = uaaService.getTenantCustomRoles(userAccount.getTenantUID()).stream().map(CustomRole::getName).collect(Collectors.toSet());
            roleListVO = roleListVO.stream().filter(role -> tenantSysRoles.contains(role.getName()) || tenantCustomRoles.contains(role.getName())).collect(Collectors.toList());
        }
    }
}
