package com.ge.apm.service.uaa;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.mapper.OrgInfoMapper;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.PermissionGroupMapper;
import com.ge.apm.dao.mapper.PermissionGroupUserMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212547631
 */
@Component
public class UaaService {
    private static final Logger logger = Logger.getLogger(UaaService.class);
    public static final String ROOT = "default";
    public static final String ORG = "org";
    public static final String ASSET = "asset";
    public static final String TEMPORARY_ACCOUNT = "";
    private static Map<String, SysRole> sysRoles;
    public static final String TEMP_STAFF = "仅报修权限账号";

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    UserOrgInfoRepository userOrgDao;

    @Autowired
    WorkflowConfigRepository wfcfigDao;

    @Autowired
    private ConfigCacheService configService;

    @Autowired
    private CustomRoleRepository customRoleRepository;

    @Autowired
    private ZkbIbCrossUserStatusRepository zkbIbCrossUserStatusRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserAccountHistoryService userAccountHistoryService;

    @Autowired
    private OrgInfoMapper orgInfoMapper;

    @Autowired
    private PermissionGroupMapper permissionGroupMapper;
    @Autowired
    private PermissionGroupUserMapper permissionGroupUserMapper;
    @Autowired
    private PermissionGroupUserRepository permissionGroupUserRepository;



    private void loadSysRoles(){
        if(sysRoles!=null) return;

        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
        List<SysRole> sysRoleList = sysRoleDao.find();

        sysRoles = new HashMap<String, SysRole>();
        for(SysRole sysRole: sysRoleList){
            sysRoles.put(sysRole.getName(), sysRole);
        }
    }

    public String getUserDefaultHomePage(UserAccount user){
        String url;
        UserContextService userContextService = (UserContextService) WebUtil.getBean(UserContextService.class);
        if(userContextService.isZkbAccount())
        {
            url="/portal/zkbTenantHome.xhtml";
            return url;
        }
        if (userContextService.isActive().equals("1"))
            url = "/home.xhtml";
        else if(userContextService.hasRole("NursingStaff"))
            url = getDefautHomePage("NursingStaff");
        else if(userContextService.hasRole("HospitalHead"))
            url = getDefautHomePage("HospitalHead");
        else if(userContextService.hasRole("AssetHead"))
            url = getDefautHomePage("AssetHead");
        else if(userContextService.hasRole("DeptHead"))
            url = getDefautHomePage("DeptHead");
        else if(userContextService.hasRole("AssetStaff"))
            url = getDefautHomePage("AssetStaff");
        else if(userContextService.hasRole("ClinicalStaff"))
            url = getDefautHomePage("ClinicalStaff");
        else
//            url = "/home.xhtml";
            url = "/portal/homeNurse1.xhtml";

        return url;
    }

    private String getDefautHomePage(String roleName){
        loadSysRoles();

        SysRole sysRole = sysRoles.get(roleName);
        if(sysRole!=null)
            return sysRole.getHomePage();
        else
            return "/home.xhtml";
    }

    public List<SysRole> getSysRoles(){
        loadSysRoles();

        List<SysRole> roleList = new ArrayList<SysRole>();
        roleList.addAll(sysRoles.values());

        return roleList;
    }

    public List<String> getSysRoleNames(){
        loadSysRoles();

        List<String> roleList = new ArrayList<String>();

        for(Map.Entry<String, SysRole> item: sysRoles.entrySet()){
            roleList.add(item.getValue().getRoleDesc());
        }

        return roleList;
    }

    //（disableList中是需要从roleList中移除的roleNamme）
    public List<String> getSysRoleNamesByDisableList(List<String> disableRoleList){
        loadSysRoles();

        List<String> roleList = new ArrayList<String>();

        Map<String, SysRole> tempRole = new HashedMap(sysRoles);

        for (String roleName: disableRoleList) {
            if(tempRole.containsKey(roleName)){
                tempRole.remove(roleName);
            }
        }

        for(Map.Entry<String, SysRole> item: tempRole.entrySet()){
            roleList.add(item.getValue().getRoleDesc());
        }

        return roleList;
    }


    public boolean isLocale(){
        if(LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")){
            return true;
        } else {
            return false;
        }
    }

    public List<SysRoleVO> getSysRoleVO(String tenantUID){
        loadSysRoles();
        TenantInfo ti = configService.getTenantByUid(tenantUID);
        List<SysRoleVO> sysRoleList = new ArrayList<>();
        for (SysRole tempSysRole : sysRoles.values()){
            SysRoleVO srVO = new SysRoleVO();
            srVO.setId(tempSysRole.getId());
            srVO.setName(tempSysRole.getName());
            srVO.setRoleDesc(tempSysRole.getRoleDesc());
            srVO.setRoleDescEn(tempSysRole.getRoleDescEn());
            /*if(isLocale()){
                srVO.setRoleDesc(tempSysRole.getRoleDesc());
            }else{
                srVO.setRoleDesc(tempSysRole.getRoleDescEn());
            }*/
            srVO.setRoleType(0);
            //判断登录用户是否是LocalITAdmin(本地后台运营管理)
            if(UserContextService.isOnlyLocalITAdmin()){
                if("ITAdmin".equals(tempSysRole.getName())){
                    //srVO.setDisable(Boolean.TRUE);
                    continue;
                }
            }
            //判断租户是否院区隔离
            if(ti.getInsulate() == null || !ti.getInsulate()){
                if("LocalITAdmin".equals(tempSysRole.getName())){
                    //srVO.setDisable(Boolean.TRUE);
                    continue;
                }
            }
            //判断是否是智康宝用户，如果不是就移除掉智康宝的角色
            if(srVO.getName().equals("zkbDealerSales") || srVO.getName().equals("zkbDealerFE")){
                if(null != ti.getType() && ti.getType()!=3){
                    continue;
                }
            }
            //如果是GE租户下放开产品超管和PM超管角色 别的租户看不到
            if(!"GE".equals(ti.getName())){
                if(srVO.getName().equals("ReviewSystemAdministrator")||srVO.getName().equals("PMAdministrator")){
                    continue;
                }
            }
            if(!UserContextService.checkRole(ApmSystemRole.ITAdmin.name())){
                srVO.setDisable(true);
            }
            sysRoleList.add(srVO);
        }
        List<CustomRole> customRoles = getTenantCustomRoles(ti.getUid());
        if(CollectionUtils.isNotEmpty(customRoles)){
            for (CustomRole customRole: customRoles) {
                SysRoleVO srVO = new SysRoleVO();
                srVO.setId(customRole.getId());
                srVO.setName(customRole.getName());
                srVO.setRoleDesc(customRole.getName());
                srVO.setRoleType(1);
                sysRoleList.add(srVO);
            }
        }
        return sysRoleList;
    }

    public List<CustomRole> getTenantCustomRoles(String tenantUID){
        return customRoleRepository.findByTenantUID(tenantUID);
    }

    public List<UserRole> getSystemRoles(Integer userId){
        UserRoleRepository userAccountRepository = WebUtil.getBean(UserRoleRepository.class);
        return userAccountRepository.findByUserIdAndRoleType(userId,0);
    }

    public List<String> getAuthorities(Integer userId){
        SysRoleRepository sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);
        List<SysRole> sysRoles = sysRoleRepository.getSysRoles(userId);
        List<String> authorities = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(sysRoles)){
            for (SysRole role : sysRoles) {
                if(StringUtils.isNotEmpty(role.getAuthorities())){
                    authorities.addAll(Arrays.asList(role.getAuthorities().split(",")));
                }
            }
        }
        List<CustomRole> customRoles = customRoleRepository.findCustomRolesByUserId(userId);
        if(CollectionUtils.isNotEmpty(customRoles)){
            for (CustomRole customRole :customRoles) {
                if(StringUtils.isNotEmpty(customRole.getAuthorities())){
                    authorities.addAll(Arrays.asList(customRole.getAuthorities().split(",")));
                }
            }
        }
        return authorities;
    }

    public Boolean hasAuthority(Integer userId,String authority){
        return getAuthorities(userId).contains(authority);
    }

    @Transactional
    public UserAccount loadUserWithUserRoles(int userId){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getById(userId);

        try{
            Hibernate.initialize(user.getUserRoleList());
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        return user;
    }

    @Transactional
    public void setUserRoles(boolean isCreated, UserAccount userAccount, List<String> newRoleNames){
        //first remove all existing user roles
        UserRoleRepository userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        TenantInfoRepository tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        //then set user roles
        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
        if((!newRoleNames.contains(TEMP_STAFF)) && (userAccount.getUserRoleNames().contains(TEMP_STAFF))){
            TenantInfo tenant = tenantInfoRepository.findByuid(userAccount.getTenantUID());
            if(tenant.getIsEnabled() == null || (!tenant.getIsEnabled())){
                deactiveTemporaryUser(userAccount.getId());
            }
        }
        if(newRoleNames.contains(TEMP_STAFF)){
            activeTemporaryUser(userAccount.getId());

        }
        if(newRoleNames.isEmpty()){
            newRoleNames.add("临床科室人员");
        }
        List<UserRole> userRoles = ListUtil.newArrayList();
        for(String newRoleName: newRoleNames){
            SysRole role = sysRoleDao.getByRoleDesc(newRoleName);
            if (role == null){
                CustomRole customRole = customRoleRepository.findByTenantUIDAndName(userAccount.getTenantUID(),newRoleName);
                if(customRole == null){
                    logger.warn("Assign role to user: Role not found, roleName="+newRoleName);
                }else{
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userAccount.getId());
                    userRole.setRoleId(customRole.getId());
                    userRole.setRoleType(1);
                    userRoles.add(userRole);
                }
            }
            else {
                UserRole userRole = new UserRole();
                userRole.setUserId(userAccount.getId());
                userRole.setRoleId(role.getId());
                userRole.setRoleType(0);
                userRole.setRole(role);
                userRoles.add(userRole);
            }
        }
        if (isCreated) {
            userAccountHistoryService.updateNewUser(userAccount.getUid(), userAccount.getLoginName());
        } else {
            userAccount.setUserRoleList(userRoles);
            userAccountHistoryService.save(UserAccount.HistoryAction.UPDATE_ROLE.name(), userAccount);
        }
        if(userAccount.getId()!=null){
            userRoleDao.deleteByUserAccount(userAccount.getId());
        }
        if(isCreated && !UserContextService.checkRole(ApmSystemRole.ITAdmin.name())){
            return ;
        }
        userRoleDao.save(userRoles);
    }

    @Transactional
    public void setUserPermissionGroup(UserAccount userAccount, List<String> permissionGroupUidList){
        permissionGroupUserRepository.deleteByUserUid(userAccount.getUid());
        List<PermissionGroupUser> saveList = ListUtil.toList();
        for (String permissionGroupUid : permissionGroupUidList) {
            PermissionGroupUser permissionGroupUser = new PermissionGroupUser();
            permissionGroupUser.setPermissionGroupUid(permissionGroupUid);
            permissionGroupUser.setUserUid(userAccount.getUid());
            saveList.add(permissionGroupUser);
        }
        permissionGroupUserRepository.save(saveList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addZkbIbCrossUserStatus(UserAccount userAccount, List<String> newRoleNames){
        List<UserRole> old = userRoleRepository.findByUserId(userAccount.getId());
        String isOldRole = "0";
        for(UserRole r: old){
            if(r.getRoleId().equals(3)){
                isOldRole = "1";
                break;
            }
        }
        String isNewRole = "0";
        for(String s :newRoleNames){
            if("工程师".equals(s)){
                isNewRole = "1";
                break;
            }
        }

        if(!isOldRole.equals(isNewRole)){
            zkbIbCrossUserStatusRepository.deleteByLoginName(userAccount.getLoginName());
            if("0".equals(isNewRole)){
                ZkbIbCrossUserStatus zkbIbCrossUserStatus = new ZkbIbCrossUserStatus();
                zkbIbCrossUserStatus.setLoginName(userAccount.getLoginName());
                zkbIbCrossUserStatus.setStatus("D");
                zkbIbCrossUserStatusRepository.save(zkbIbCrossUserStatus);
            }else{
                ZkbIbCrossUserStatus zkbIbCrossUserStatus = new ZkbIbCrossUserStatus();
                zkbIbCrossUserStatus.setLoginName(userAccount.getLoginName());
                zkbIbCrossUserStatus.setStatus("N");
                zkbIbCrossUserStatusRepository.save(zkbIbCrossUserStatus);
            }
        }

        List<UserRole> userRoles = userRoleRepository.findByUserIdAndRoleType(userAccount.getId(),5);
        if(CollectionUtils.isNotEmpty(userRoles) && newRoleNames.contains("后台运营管理")){
            List<ZkbFePartOrderUserConfig> list = null;
            ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
            list = zkbFePartOrderUserConfigRepository.findByLoginName(userAccount.getLoginName());
            if(CollectionUtils.isNotEmpty(list)){
                if(!list.get(0).getConfigType().equals("Supplier")){
                    WebUtil.addErrorMessage("此用户在FE USER CONFIG中已有角色："+WebUtil.getMessage(list.get(0).getConfigType()));
                }
                return;
            }
            ZkbFePartOrderUserConfig zkbFePartOrderUserConfig = new ZkbFePartOrderUserConfig();
            zkbFePartOrderUserConfig.setLoginName(userAccount.getLoginName());
            zkbFePartOrderUserConfig.setName(userAccount.getName());
            zkbFePartOrderUserConfig.setEmail(userAccount.getEmail());
            zkbFePartOrderUserConfig.setPhone(userAccount.getTelephone());
//            zkbFePartOrderUserConfig.setSso(selectedTemp.getSso());
            zkbFePartOrderUserConfig.setConfigType("Supplier");
            zkbFePartOrderUserConfig.setRemark("本用户由《用户管理》菜单页面中同步创建");
            zkbFePartOrderUserConfig.setTenantUid(userAccount.getTenantUID());
            zkbFePartOrderUserConfigRepository.save(zkbFePartOrderUserConfig);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteZkbIbCrossUserStatus(UserAccount userAccount,String oldLoginName){
        List<UserRole> roles = userRoleRepository.findByUserId(userAccount.getId());
        boolean isExist = false;
        for(UserRole r: roles){
            if(r.getRoleId().equals(3)){
                isExist = true;
                break;
            }
        }
        if(isExist){
            zkbIbCrossUserStatusRepository.deleteByLoginName(oldLoginName);
            ZkbIbCrossUserStatus zkbIbCrossUserStatus = new ZkbIbCrossUserStatus();
            zkbIbCrossUserStatus.setLoginName(oldLoginName);
            zkbIbCrossUserStatus.setStatus("D");
            zkbIbCrossUserStatusRepository.save(zkbIbCrossUserStatus);
        }
    }

    @Transactional
    public void activeTemporaryUser(Integer userId){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        userDao.activeSingleUser(userId);
    }

    public List<UserAccount> findByNameLike(String name) {
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        return userDao.findByNameContains(name);
    }

    public void deactiveTemporaryUser(Integer userId){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        userDao.deactiveTemporaryUser(userId);
    }

    public List<OrgInfo> getHospitalListBySiteId(int siteId){
        return orgInfoDao.getHospitalBySiteId(siteId);
    }

    public List<OrgInfo> getOrgListByHospitalId(int hospitalId){
        return orgInfoDao.getByHospitalId(hospitalId);
    }

    public List<OrgInfo> getFullOrgListBySiteId(int siteId) {
        return orgInfoDao.getFullOrgListBySiteId(siteId);

    }

    public DefaultTreeNode getFullOrgTreeBySiteId(DefaultTreeNode rootNode, int siteId, Integer selectedOrgId){
        List<OrgInfo> fullOrgList = orgInfoDao.getFullOrgListBySiteId(siteId);

        DefaultTreeNode root = new DefaultTreeNode("Root", null);
        root.getChildren().add(rootNode);

        buildOrgTree(fullOrgList, rootNode, selectedOrgId);
        return root;
    }

    public DefaultTreeNode getFullOrgTreeBySiteId(int siteId, Integer selectedOrgId){
        List<OrgInfo> fullOrgList = orgInfoDao.getFullOrgListBySiteId(siteId);

        DefaultTreeNode root = new DefaultTreeNode("Root", null);
        buildOrgTree(fullOrgList, root, selectedOrgId);
        return root;
    }

    private void buildOrgTree(List<OrgInfo> orgList, DefaultTreeNode root, Integer selectedOrgId){
        //first create all nodes
        TreeNode selectedNode = null;
        Map<Integer, TreeNode> treeMap = new HashMap<>();
        for(OrgInfo org: orgList){
            TreeNode node;
            node = new DefaultTreeNode("org", org, root);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getId(), node);
            if(org.getId().equals(selectedOrgId))
                selectedNode = node;
        }

        //then update node's parent by org's parentOrg
        for(Map.Entry<Integer, TreeNode> nodeItem: treeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            if(org.getParentOrg()!=null){
                TreeNode parentNode = treeMap.get(org.getParentOrg().getId());
                if(parentNode!=null){
                    parentNode.getChildren().add(node);
                }
            }
        }

        if(selectedNode!=null){
            TreeNode node = selectedNode.getParent();
            while(node!=null) {
                node.setExpanded(true);
                node = node.getParent();
            }
        }

        //store selectedNode into root.data property
        if(selectedNode!=null) {
            selectedNode.setSelected(true);

            root.setData(selectedNode);
        }
    }

    public DefaultTreeNode getOrgTree(int hospitalId, boolean showHospitalAsRoot, Integer selectedOrgId, boolean ...type){
        List<OrgInfo> orgList = null;
        if (type == null || type.length == 0) {
            orgList = orgInfoDao.getByHospitalId(hospitalId);
        } else {
            orgList = orgInfoDao.getFullOrgListBySiteId(hospitalId);
        }

        DefaultTreeNode root = new DefaultTreeNode("Root", null);
        DefaultTreeNode treeRoot = root;

        if(showHospitalAsRoot){
            OrgInfo hospitalInfo = orgInfoDao.findById(hospitalId);
            if(hospitalInfo!=null){
                DefaultTreeNode node = new DefaultTreeNode("org", hospitalInfo, root);
                node.setRowKey("org_"+hospitalInfo.getId());
                root = node;
            }
        }

        buildOrgTree(orgList, root, selectedOrgId);

        return treeRoot;
    }

    //origin user accessible org list
    public List<OrgInfo> getOrgListByUser(String userUid){
        return orgInfoDao.getByUser(userUid);
    }

    public List<OrgInfo> getOrgListByReminderRule(String ruleUid){
        return orgInfoDao.getByReminderRule(ruleUid);
    }

    public List<OrgInfo> getOrgListByPermissionGroup(List<String> permissionGroupUidList){
        return orgInfoDao.getOrgListByPermissionGroup(permissionGroupUidList);
    }

    public boolean isIbSiteAdmin(UserAccount userAccount) {
        if (userAccount.getSiteId() == 46) {
            List<OrgInfo> orgListByUser = getOrgListByUser(userAccount.getUid());
            // available to see the all institutions in ib site
            boolean allSite = orgListByUser.size() == 1 && StringUtils.equals(orgListByUser.get(0).getUid(), orgListByUser.get(0).getTenantUID());
            return allSite || UserContextService.isOnlyLocalITAdmin();
        }
        return false;
    }

    //get tree for user to access
    public TreeNode getOrgAccessTreeByUser(UserAccount user){
        TreeNode root = new DefaultTreeNode("root", null, null);
        List<OrgInfo> userScopeList = getOrgListByUser(user.getUid());
        for (OrgInfo item : userScopeList) {
            if (item.getOrgType() == 4) {
                new DefaultTreeNode("org", item, root);
            } else {
                root.getChildren().add(getOrgTreeBySite(item.getSiteUID()).getChildren().get(0));
            }
        }
        return root;
    }

    //full of the tree for this org
    public TreeNode getOrgTreeBySite(String siteUID){
        List<OrgInfo> orgList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(siteUID);
        return getOrgTreeNodes(siteUID, orgList);
    }
    public TreeNode getOrgTreeBySite46(String siteUID,String retrieval){
        List<OrgInfo> orgList = orgInfoDao.getOrgTreeBySiteAndparm(siteUID,retrieval);
        return getOrgTreeNodes(siteUID, orgList);
    }
    public TreeNode getOrgTreeNodes(String siteUID, List<OrgInfo> orgList) {
        return getOrgTreeNodes(siteUID,  orgList,null);
    }
    public TreeNode getOrgTreeNodes(String siteUID, List<OrgInfo> orgList,List<String> orgUid) {
        TreeNode root = new DefaultTreeNode("root", null, null);

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        for (OrgInfo org : orgList) {
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_" + org.getId());
            if(CollectionUtils.isNotEmpty(orgUid)){
                node.setSelectable( orgUid.contains(org.getUid()));
            }
            treeMap.put(org.getUid(), node);
        }
        for (Map.Entry<String, TreeNode> nodeItem : treeMap.entrySet()) {
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo) node.getData();
            if (org.getParentUID() != null) {
                TreeNode parentNode = treeMap.get(org.getParentUID());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }

        if (treeMap.get(siteUID) != null) {
            root.getChildren().add(treeMap.get(siteUID));
        }

        if (!root.getChildren().isEmpty()) {
            OrgInfo temp = (OrgInfo) root.getChildren().get(0).getData();
            rootUID = temp.getUid();
        }
        return root;
    }

    public TreeNode getOrgTreeNodesByAssetType(String siteUID, List<OrgInfo> orgList,List<String> orgUid) {
        TreeNode root = new DefaultTreeNode("root", null, null);

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        for (OrgInfo org : orgList) {
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_" + org.getId());
            if(CollectionUtils.isNotEmpty(orgUid)){
                node.setSelectable( orgUid.contains(org.getUid()));
            }
            treeMap.put(org.getUid() + "#" + org.getAssetType(), node);
        }
        for (Map.Entry<String, TreeNode> nodeItem : treeMap.entrySet()) {
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo) node.getData();
            if (org.getParentUID() != null) {
                TreeNode parentNode = null;
                for (String key : treeMap.keySet()) {
                    String str = key.split("#")[0];
                    if (str.equals(org.getParentUID())) {
                        parentNode = treeMap.get(key);
                    }
                }
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }

        for (String key : treeMap.keySet()) {
            String str = key.split("#")[0];
            if (str.equals(siteUID)) {
                root.getChildren().add(treeMap.get(key));
            }
        }

        if (!root.getChildren().isEmpty()) {
            OrgInfo temp = (OrgInfo) root.getChildren().get(0).getData();
            rootUID = temp.getUid();
        }
        return root;
    }

    public TreeNode getOrgTreeBySiteWithoutClinical(String siteUID){
        List<OrgInfo> orgList =  orgInfoDao.getOrgInfosByParentId(siteUID);
        return getOrgTreeNodes(siteUID, orgList);
    }

    public String rootUID;

    public TreeNode getOrgTreeBySiteExceptClinicalDept(String siteUID){
        List<OrgInfo> orgList = null;
        orgList = orgInfoDao.getOrgInfosByParentId(siteUID);
        TreeNode root  = new DefaultTreeNode("root", null, null);;

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        for(Map.Entry<String, TreeNode> nodeItem: treeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            if(org.getParentUID()!=null){
                TreeNode parentNode = treeMap.get(org.getParentUID());
                if(parentNode!=null){
                    parentNode.getChildren().add(node);
                }
            }
        }

        if(treeMap.get(siteUID)!=null){
            root.getChildren().add(treeMap.get(siteUID));
        }

        return root;
    }

    @Transactional
    public void clearUserScope(String userUid){
        UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        List<UserOrgInfo> orgList = userOrgDao.findByUserUid(userUid);
        userOrgDao.delete(orgList);
    }

    @Transactional
    public void clearUserOrgAfterOrgUpdate(String userUid){
        userOrgDao.deleteByUserUid(userUid);
    }

    @Transactional
    public void setDefaultScope(UserAccount user) {
        userOrgDao.deleteByUserUid(user.getUid());
        OrgInfo ownnedOrg = orgInfoDao.getByUid(user.getOrgInfoUID());
        if (ownnedOrg.getOrgType() == 4) {
            //if chosen org is clinical dept, then only select the current clinical dept instead of the whole site.
            List<OrgInfo> hospitalChildrenByUids = orgInfoDao.getHospitalChildrenByUids(Arrays.asList(ownnedOrg.getUid()));
            List<UserOrgInfo> collect = hospitalChildrenByUids.stream().map(item -> {
                UserOrgInfo userOrg = new UserOrgInfo();
                userOrg.setUserUid(user.getUid());
                userOrg.setOrgUid(item.getUid());
                userOrg.setOrgType(item.getOrgType());
                return userOrg;
            }).collect(Collectors.toList());
            userOrgDao.save(collect);
        } else {
            OrgInfo org = orgInfoDao.getByUid(user.getSiteUID());
            if (org != null) {
                UserOrgInfo userOrg = new UserOrgInfo();
                userOrg.setUserUid(user.getUid());
                userOrg.setOrgUid(org.getUid());
                userOrg.setOrgType(org.getOrgType());
                userOrgDao.save(userOrg);
            }
        }

/*        List<WorkflowConfig> workflowConfigs = wfcfigDao.findByDeptForSharedAsset(ownnedOrg.getUid());

        UserOrgInfo foundUserOrg = null;

        if (ownnedOrg.getOrgType() == 4 && workflowConfigs.size() == 0) {
            //if chosen org is clinical dept, then only select the current clinical dept instead of the whole site.

            foundUserOrg = userOrgDao.findFirstByUserUidAndOrgUid(user.getUid(), ownnedOrg.getUid());
            if (foundUserOrg == null) {
                UserOrgInfo userOrg = new UserOrgInfo();
                userOrg.setUserUid(user.getUid());
                userOrg.setOrgUid(ownnedOrg.getUid());
                userOrg.setOrgType(ownnedOrg.getOrgType());
                userOrgDao.save(userOrg);
            }

        } else {
            OrgInfo org = orgInfoDao.getByUid(user.getSiteUID());
            if (org != null) {
                foundUserOrg = userOrgDao.findFirstByUserUidAndOrgUid(user.getUid(), org.getUid());
                if (foundUserOrg == null) {
                    UserOrgInfo userOrg = new UserOrgInfo();
                    userOrg.setUserUid(user.getUid());
                    userOrg.setOrgUid(org.getUid());
                    userOrg.setOrgType(org.getOrgType());
                    userOrgDao.save(userOrg);
                }
            }
        }*/

    }

    public DefaultTreeNode getOrgTree(int hospitalId){
        return getOrgTree(hospitalId, false, null);
    }

    private void addAssetToOrgNode(TreeNode node, Map<Integer, List<AssetInfo>> assetMap){
        if("org".equals(node.getType())){
            OrgInfo org = (OrgInfo)node.getData();
            List<AssetInfo> assets = assetMap.get(org.getId());

            if(assets!=null) {
                for(AssetInfo item: assets){
                    TreeNode assetNode = new DefaultTreeNode("asset", item, node);
                    assetNode.setRowKey("asset_"+item.getId());
                }
            }
        }

        for(TreeNode aNode: node.getChildren()){
            addAssetToOrgNode(aNode, assetMap);
        }
    }

    public TreeNode getOrgAssetTree(int hospitalId, boolean ...type){
        AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assetList = null;
        if (type == null || type.length == 0) {
            assetList = assetInfoDao.getByHospitalId(hospitalId);
        } else {
            assetList = assetInfoDao.getBySiteId(hospitalId);//实际是siteId
        }

        Map<Integer, List<AssetInfo>> assetMap = new HashMap<>();
        for(AssetInfo asset: assetList){
            List<AssetInfo> assets = assetMap.get(asset.getClinicalDeptId());

            if(assets==null){
                assets = new ArrayList<>();
                assetMap.put(asset.getClinicalDeptId(), assets);
            }

            assets.add(asset);
        }

        TreeNode root = getOrgTree(hospitalId, false, null, type);
        addAssetToOrgNode(root, assetMap);
        cleanTree(root,ASSET);
        return root;
    }

    private TreeNode cleanTree(TreeNode node, String type) {
        Iterator<TreeNode> iterator = node.getChildren().iterator();
        Set<TreeNode> remomveset = new HashSet<TreeNode>();
        while (iterator.hasNext()) {
            TreeNode subNode = iterator.next();
            if (subNode.getType().equals(type)) {
                return node;
            }
            subNode = cleanTree(subNode, type);
            if (subNode.isLeaf()) {
                remomveset.add(subNode);
            }
        }
        node.getChildren().removeAll(remomveset);
        return node;
    }

    public void removeInvalidNode(TreeNode node,String type){
        if(node == null || !node.getType().equals(type)){
            logger.error("current node is null or not org node !");
            return;
        }
        if(node.isLeaf()){
            node.getParent().getChildren().remove(node);
            return;
        }
        List<TreeNode> invalidNodeList = new ArrayList<TreeNode>();
        for(TreeNode childNode : node.getChildren()){
            if(childNode.isLeaf() && childNode.getType().equals(type)){
                invalidNodeList.add(childNode);
            }
            if(childNode.getType().equals(node.getType()) && childNode.getChildCount() > 0){
                removeInvalidNode(childNode,type);
            }
        }
        if(!CollectionUtils.isEmpty(invalidNodeList)){
            node.getChildren().removeAll(invalidNodeList);
        }
    }

    private void addChecklistToAssetNode(TreeNode node, Map<Integer, List<InspectionChecklist>> checklistMap){
        if("asset".equals(node.getType())){
            AssetInfo asset = (AssetInfo)node.getData();
            List<InspectionChecklist> checkList = checklistMap.get(asset.getId());

            if(checkList!=null) {
                for(InspectionChecklist item: checkList){
                    TreeNode checklistNode = new DefaultTreeNode("checklist", item, node);
                    checklistNode.setRowKey("checklist_"+item.getId());
                }
            }
        }

        for(TreeNode aNode: node.getChildren()){
            addChecklistToAssetNode(aNode, checklistMap);
        }
    }

    public TreeNode getOrgAssetChecklistTree(int hospitalId, int checklistType){
        InspectionChecklistRepository checklistDao = WebUtil.getBean(InspectionChecklistRepository.class);
        List<InspectionChecklist> checkList = checklistDao.getByHospitalIdAndChecklistType(hospitalId, checklistType);

        Map<Integer, List<InspectionChecklist>> checklistMap = new HashMap<>();
        for(InspectionChecklist item: checkList){
            List<InspectionChecklist> checklist = checklistMap.get(item.getAssetId());

            if(checklist==null){
                checklist = new ArrayList<>();
                checklistMap.put(item.getAssetId(), checklist);
            }

            checklist.add(item);
        }

        TreeNode root = getOrgAssetTree(hospitalId);
        addChecklistToAssetNode(root, checklistMap);
        return root;
    }

    public List<UserAccount> getUserList(String siteUid, String name){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getBySiteUIDAndNameLike(siteUid, name);
    }

    public List<UserAccount> getUserList(int hospitalId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getByHospitalId(hospitalId);
    }

    public List<UserAccount> getUsersWithAssetStaffRole(int hospitalId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getUsersWithAssetStaffRole(hospitalId);
    }

    public List<UserAccount> getUsersWithAssetHeadOrStaffRole(int hospitalId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getUsersWithAssetHeadOrStaffRole(hospitalId);
    }

    public UserAccount getUserById(int userId){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.findById(userId);
    }

    public UserAccount getUserByUid(String userUid){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.findByUid(userUid);
    }

    public List<UserAccount> getUsersByDeptUid(String deptUid){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getByOrgInfoUID(deptUid);
    }

    public List<UserAccount> getUsersByDeptUidAndRole(String deptUid, String roleName){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getUsersByDeptUidAndRole(deptUid, roleName);
    }

    public List<UserAccount> getUsersBySiteUidAndRole(String siteUid, String roleName){
        UserAccountRepository userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        return userAccountDao.getUsersBySiteUidAndRole(siteUid, roleName);
    }

    //用户可以显示的医院列表
    public List<OrgInfo> getUserAccessibleSiteList(String userUid, String name, Integer siteId) {
        List<String> institutions = orgInfoDao.findByInstitutionByNameLike(name, siteId); //TODO: fetch by user accessible site list
        //for accessible sites`
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, institutions));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4)); //非科室
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    //用户可以显示的医院列表(包含科室)
    public List<OrgInfo> getUserAccessibleSiteListWithClinicalDept(String userUid, String name, Integer siteId) {
        List<String> institutions = orgInfoDao.findByInstitutionByNameLike(name, siteId); //TODO: fetch by user accessible site list
        //for accessible sites`
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, institutions));
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    public List<OrgInfo> getUserAccessibleSiteListWithCondition(String name, Integer siteId) {
        List<String> institutions = orgInfoDao.findByInstitutionByNameOrShortName(name, siteId); //TODO: fetch by user accessible site list
        //for accessible sites`
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, institutions));
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    //用户可以显示的医院列表(不包含科室)
    public List<OrgInfo> getUserAccessibleSiteList(String userUid) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(OrgInfo::getUid).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(OrgInfo::getSiteUID).collect(Collectors.toSet());

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, new ArrayList(orgUidSet2), "1*2+"));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4)); //非科室
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    //用户可以显示的医院列表(不包含科室)
    /*public List<OrgInfo> getUserAccessibleSiteListByFilter(String userUid,String treeNodeNameFilter,String parentOrgId) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(OrgInfo::getUid).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(OrgInfo::getSiteUID).collect(Collectors.toSet());

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, new ArrayList(orgUidSet2), "1*2+"));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4)); //非科室
        orgInfoFilters.add(new SearchFilter("name",SearchFilter.Operator.LIKE,treeNodeNameFilter));
        Pageable pageable = new PageRequest(0,500);

        return orgInfoDao.findBySearchFilter(orgInfoFilters,pageable).getContent();
    }*/

    //用户可以显示的医院列表(包含科室)
    public List<OrgInfo> getUserAccessibleSiteListWithClinicalDept(String userUid) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        for (OrgInfo orgInfo : accessList){
            if(orgInfo.getTenantUID().equals(orgInfo.getSiteUID())&&orgInfo.getSiteId()==46){
                return new ArrayList<OrgInfo>();
            }
        }
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(OrgInfo::getUid).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(OrgInfo::getSiteUID).collect(Collectors.toSet());

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, new ArrayList(orgUidSet2), "1*2+"));
        List<OrgInfo> accessList2 = orgInfoDao.findBySearchFilter(orgInfoFilters);

        for (OrgInfo orgInfo: accessList2) {
            if (!accessList.contains(orgInfo)) {
                accessList.add(orgInfo);
            }
        }
        return accessList;
    }

    //用户对指定医院访问的科室列表
    public List<OrgInfo> getOrgDeptList(String siteUid,String userUid) {
        if(logger.isDebugEnabled()) {
            logger.debug("getOrgDeptList siteUID: " + siteUid + ", userUid: " + userUid);
        }
        List<OrgInfo> deptList;
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        Set<String> orgUidSet = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());
        if (orgUidSet.contains(siteUid)) {
            deptList = accessList.stream().filter(item -> item.getSiteUID().equals(siteUid) && item.getOrgType() == 4).collect(Collectors.toList());
        } else {
            deptList = getClinicalDeptListBySiteUID(siteUid);
        }
        if(logger.isDebugEnabled()) {
            logger.debug("deptList: " + deptList.stream().map(item -> item.getUid()).collect(Collectors.joining(",")));
        }
        List<String> allocationDepts = this.getRegulatorUidsBySiteUid(siteUid);
        if(allocationDepts != null && allocationDepts.size() > 0) {
            deptList = deptList.stream()
                    .filter(item -> !allocationDepts.contains(item.getUid()))
                    .collect(Collectors.toList());
        }
        return deptList;
    }
    public List<String>  getAllAccessibleClinicalDeptStringList(){
        List<OrgInfo> orgInfos = this.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        if (!orgInfos.isEmpty()){
            for ( OrgInfo orgInfo:orgInfos) {
                list.add(orgInfo.getName());
            }
        }
        return list;
    }
    public List<OrgInfo> getAllAccessibleClinicalDeptList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        return this.getUserAccessibleSiteListWithClinicalDept(user.getUid());
    }

    public List<OrgInfo> getClinicalDeptListBySiteUID(String siteUID) {
        List<SearchFilter> orgFilters = new ArrayList<>();
        orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUID));
        orgFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgInfoDao.findBySearchFilter(orgFilters);
    }

    public TreeNode getOrgFullTreeByTenant(String tenantUid) {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid));
        orgInfoFilters.add(new SearchFilter("parentUID", SearchFilter.Operator.IsNull, tenantUid));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));

        Sort sort = new Sort(Sort.Direction.ASC, "parentUID","orgIndex", "name");
        List<OrgInfo> orgList = orgInfoDao.findBySearchFilter(orgInfoFilters, sort);
        TreeNode root = new DefaultTreeNode("root", null, null);
        for(OrgInfo item : orgList){
            TreeNode siteNode = getOrgTreeBySite(item.getUid());
            root.getChildren().addAll(siteNode.getChildren());
        }
        return root;
    }

    public TreeNode getOrgFullTreeByTenantWithoutClinical(String tenantUid) {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid));
        orgInfoFilters.add(new SearchFilter("parentUID", SearchFilter.Operator.IsNull, tenantUid));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));
        List<OrgInfo> orgList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        TreeNode root = new DefaultTreeNode("root", null, null);
        for(OrgInfo item : orgList){
            TreeNode siteNode = getOrgTreeBySiteWithoutClinical(item.getUid());
            root.getChildren().addAll(siteNode.getChildren());
        }
        return root;
    }

    public TreeNode getOrgFullTreeByTenantFilter(String tenantUid,String keyWord) {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid));
        orgInfoFilters.add(new SearchFilter("parentUID", SearchFilter.Operator.IsNull, tenantUid));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));
        List<OrgInfo> orgList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        TreeNode root = new DefaultTreeNode("root", null, null);
        for(OrgInfo item : orgList){
            TreeNode siteNode = getOrgTreeBySite(item.getUid());
            root.getChildren().addAll(siteNode.getChildren());
        }
        return root;
    }




    public TreeNode getOrgTreebyParentId(String parentOrgId, String userUid) {

        TreeNode treeNode = getOrgTreeBySiteExceptClinicalDept(parentOrgId);
        List<OrgInfo> orgInfoList = getUserAccessibleSiteList(userUid);

        setSelectableOrgTree(treeNode, orgInfoList);

        return treeNode;
    }

    private void setSelectableOrgTree(TreeNode node, List<OrgInfo> accessiableOrgList) {
        OrgInfo org = (OrgInfo) node.getData();
        node.setSelectable(false);
        if (accessiableOrgList.contains(org)) {
            node.setSelectable(true);
        }
        for (TreeNode item : node.getChildren()) {
            setSelectableOrgTree(item, accessiableOrgList);
        }
    }



    public TreeNode getOrgTreeByParentIdWithSelectedChildren(String parentOrgId, String userUid) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getOrgInfosByParentId(parentOrgId);
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteList(userUid);

        TreeNode rootNode = buildOrgTreeByParentIdWithSelectedChildren(orgInfoFullList, orgInfoSelectableList);

        return rootNode;
    }

    public List<OrgInfo> getOrgTreeByParentIdWithSelectedChildren(String parentOrgId, String userUid,String treeNodeNameFilter,int siteId) {
        //List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListByFilter(userUid,treeNodeNameFilter,parentOrgId);
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListByFilterAll(userUid,treeNodeNameFilter,parentOrgId);
        return orgInfoSelectableList;
    }

    public List<OrgInfo> getUserAccessibleSiteListByFilterAll(String userUid,String treeNodeNameFilter,String parentOrgId) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        boolean flag = false;
        String tenantUid = null;
        for (OrgInfo orgInfo : accessList){
            if(orgInfo.getTenantUID().equals(orgInfo.getSiteUID())&&orgInfo.getSiteId()==46){
                tenantUid = orgInfo.getTenantUID();
                flag = true;
            }
        }
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        if(flag){
            List<String> institutions = orgInfoDao.findByInstitutionByNameLike(treeNodeNameFilter, 46);
            orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, institutions));
            orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));
            List<OrgInfo> list = orgInfoDao.findBySearchFilter(orgInfoFilters);
            if(list!=null&&list.size()>0){
                OrgInfo tenant = orgInfoDao.findByUid(tenantUid);
                list.add(tenant);
            }
            return list;
        }else{
            List<String> institutions = new ArrayList<String>();
            for (OrgInfo orgInfo : accessList){
                institutions.add(orgInfo.getInstitutionUID());
            }
            orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, institutions));
            orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4)); //非科室
            if(!StringUtils.isEmpty(treeNodeNameFilter)){
                orgInfoFilters.add(new SearchFilter("name",SearchFilter.Operator.LIKE,treeNodeNameFilter,"1*2+"));
                orgInfoFilters.add(new SearchFilter("orgType",SearchFilter.Operator.EQ,1,"1*2+"));
            }
            List<OrgInfo> listAll = orgInfoDao.findBySearchFilter(orgInfoFilters);
            //for accessible sites
            Set<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(OrgInfo::getUid).collect(Collectors.toSet());
            //for sites of accessible departments
            Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(OrgInfo::getSiteUID).collect(Collectors.toSet());

            List<OrgInfo> list = new ArrayList<>();
            if(orgUidSet1!=null&&orgUidSet1.size()>0){
                for (OrgInfo tmp:listAll) {
                    for (String uid:orgUidSet1) {
                        if(uid.equals(tmp.getInstitutionUID())||uid.equals(tmp.getHospitalUID())||uid.equals(tmp.getSiteUID())||uid.equals(tmp.getTenantUID())){
                            if(!list.contains(tmp))list.add(tmp);
                        }
                    }
                }
            }
            if(orgUidSet2!=null&&orgUidSet2.size()>0) {
                for (OrgInfo tmp : listAll) {
                    for (String uid : orgUidSet2) {
                        if (uid.equals(tmp.getUid())) {
                            if(!list.contains(tmp))list.add(tmp);
                        }
                    }
                }
            }

            return list;
        }
    }

    public TreeNode getOrgTreeByParentIdWithSelectedChildren(String parentOrgId) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getOrgInfosByParentId(parentOrgId);
        TreeNode rootNode = buildOrgTreeByParentIdWithSelectedChildren(orgInfoFullList,orgInfoFullList);

        return rootNode;
    }

    public List<String> getOrgInfoUidsByparentUid(String uid) {
        return orgInfoDao.getOrgInfoUidsByparentUid(uid);
    }

    public TreeNode buildOrgTreeByParentIdWithSelectedChildren(List<OrgInfo> orgInfoFullList, List<OrgInfo> orgInfoSelectableList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoFullList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(false);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = null;

        for(OrgInfo org: orgInfoSelectableList){
            TreeNode node = treeMap.get(org.getUid());

            if (node == null) continue;
            node.setSelectable(true);

            OrgInfo current = org;
            while(current.getParentUID()!=null){
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo)parentNode.getData();

            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null){
                    rootNode = treeMap.get(current.getUid());
                }
            }

        }

        return rootNode;
    }

    /*public TreeNode buildOrgTreeByParentIdWithSelectedChildren(List<OrgInfo> orgInfoFullList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        TreeNode rootNode = null;
        for(OrgInfo org: orgInfoFullList) {
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(true);
            node.setRowKey("org_" + org.getId());
            treeMap.put(org.getUid(), node);
        }

        for(OrgInfo org: orgInfoFullList) {
            OrgInfo current = org;
            while (current.getParentUID() != null) {
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo) parentNode.getData();

            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null) {
                    rootNode = treeMap.get(current.getUid());
                }
            }
        }

        return rootNode;
    }*/

    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelected(String parentOrgId, String userUid) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        TreeNode rootNode = buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoSelectableList);

        return rootNode;
    }

    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelected(String parentOrgId, String userUid, Integer selectableLevel) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoSelectableList, selectableLevel);
    }

    public TreeNode getOrgTreeByParentIdWithClinicalDeptSelected(String parentOrgId, Integer selectableLevel) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);

        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, selectableLevel);
    }

    public TreeNode getOrgTreeByParentIdWithClinicalDeptSelected(String parentOrgId, Integer selectableLevel, String condition) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
        List<OrgInfo> orgInfoFullList2 = orgInfoFullList.stream().filter(t -> (t.getOrgType() == 4 && t.getName().toLowerCase().contains(condition.toLowerCase())) || t.getOrgType() != 4).collect(Collectors.toList());
        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList2, selectableLevel);
    }
    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelected(String parentOrgId, String userUid, Integer selectableLevel, String condition) {
        return getOrgTreeByParentIdWithClinicalDeptsSelected(parentOrgId,userUid,selectableLevel,condition,false);
    }

    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelected(String parentOrgId, String userUid, Integer selectableLevel, String condition,boolean expandAll) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
        List<OrgInfo> orgInfoFullList2 = orgInfoFullList.stream().filter(t -> (t.getOrgType() == 4 && t.getName().toLowerCase().contains(condition.toLowerCase())) || t.getOrgType() != 4).collect(Collectors.toList());
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList2, orgInfoSelectableList, selectableLevel,expandAll);
    }

    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(String parentOrgId, String userUid) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
//        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        TreeNode rootNode = buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoFullList);

        return rootNode;
    }

    public TreeNode buildOrgTreeByParentIdWithClinicalDeptsSelected(List<OrgInfo> orgInfoFullList, List<OrgInfo> orgInfoSelectableList) {
        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoSelectableList, 4);
    }

    public TreeNode buildOrgTreeByParentIdWithClinicalDeptsSelected(List<OrgInfo> orgInfoFullList, List<OrgInfo> orgInfoSelectableList, Integer selectableLevel){
        return buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoSelectableList, selectableLevel, false);
    }

    public TreeNode buildOrgTreeByParentIdWithClinicalDeptsSelected(List<OrgInfo> orgInfoFullList, List<OrgInfo> orgInfoSelectableList, Integer selectableLevel,boolean expandAll) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoFullList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(selectable(org, selectableLevel));
            node.setRowKey("org_"+org.getId());
            node.setExpanded(expandAll);
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = null;

        for(OrgInfo org: orgInfoSelectableList){
            TreeNode node = treeMap.get(org.getUid());

            if (node == null) continue;

            OrgInfo current = org;
            while(current.getParentUID()!=null){
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo)parentNode.getData();
            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null){
                    rootNode = treeMap.get(current.getUid());
                }
            }else {
                OrgInfo rootOrg = (OrgInfo)rootNode.getData();

            }
        }

        return rootNode;
    }

    public TreeNode buildOrgTreeByParentIdWithClinicalDeptsSelected(List<OrgInfo> orgInfoFullList, Integer selectableLevel) {
        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoFullList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(selectable(org, selectableLevel));
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = null;

        for(OrgInfo org: orgInfoFullList){
            TreeNode node = treeMap.get(org.getUid());

            if (node == null) continue;

            OrgInfo current = org;
            while(current.getParentUID()!=null){
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo)parentNode.getData();
            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null){
                    rootNode = treeMap.get(current.getUid());
                }
            }else {
                OrgInfo rootOrg = (OrgInfo)rootNode.getData();

            }
        }

        return rootNode;
    }


    /**
     * level is 4: dept
     * level is 3: dept, site
     * level is 2: dept, site, hospital
     * level is 1: dept, site, hospital, institution
     * level is 0: dept, site, hospital, institution, tenant
     */
    private boolean selectable(OrgInfo org, Integer selectableLevel) {
        if (selectableLevel == null) {
            return true;
        }
        return selectableLevel <= org.getOrgType();
    }

    public boolean isTemporaryAccount(String role) {
        Optional<SysRole> optionalSysRole = sysRoles.values().stream().filter(sysRole -> sysRole.getRoleDesc().equals(role) && sysRole.getId() == 9).findFirst();

        if (optionalSysRole.isPresent()) {
            return true;
        } else {
            return false;
        }

    }

    public String getTemporaryAccountIfAlreadyChecked(List<String> roles) {

        for (String role: roles) {
            if (isTemporaryAccount(role)) {
                return role;
            }
        }

        return null;
    }

    // 根据院区UID(siteUID)获取工作流中配置的调剂中心UID列表
    public List<String> getRegulatorUidsBySiteUid(String siteUID) {
        List<WorkflowConfig> workflows = wfcfigDao.findBySiteUID(siteUID);
        List<String> regulatorUids = workflows.stream()
                .map(workflow -> workflow.getDeptForSharedAsset())
                .distinct()
                .collect(Collectors.toList());
        if(logger.isDebugEnabled()) {
            logger.debug("getRegulatorUidsBySiteUid siteUID: " + siteUID + ", regulatorUids: " + regulatorUids.stream().collect(Collectors.joining(",")));
        }
        return regulatorUids;

    }

    //where or
    public Map<Integer,List<String>> findAccessUidByUserUid(String userUid) {
        List<UserOrgInfo> listUserOrgInfo = userOrgDao.findOrgUidListByUserUid(userUid);
        Map<Integer,List<String>> map=new HashMap<Integer,List<String>>();
        if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
            for (int i = 0; i < listUserOrgInfo.size(); ++i) {
                UserOrgInfo temp=listUserOrgInfo.get(i);
                Integer type=temp.getOrgType();
                if(map.containsKey(type)){
                    map.get(type).add(temp.getOrgUid());
                }
                else{
                    List<String> tempSet=new ArrayList<String>();
                    tempSet.add(temp.getOrgUid());
                    map.put(type, tempSet);
                }
            }
        }
        return map;
    }

    //where or
    public Map<Integer,List<String>> findAccessUidByUserUid(String userUid,String siteUid) {
        List<UserOrgInfo> listUserOrgInfo = userOrgDao.findOrgUidListByUserUid(userUid);
        Map<Integer,List<String>> map=new HashMap<Integer,List<String>>();
        List<String> orgUids=new ArrayList<String>();
        List<OrgInfo> listOrgInfo=null;
        if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
            for (int i = 0; i < listUserOrgInfo.size(); ++i) {
                UserOrgInfo temp=listUserOrgInfo.get(i);
                if(!orgUids.contains(temp.getOrgUid())){
                    orgUids.add(temp.getOrgUid());
                }
            }
        }
        if(orgUids.size()>0){
            listOrgInfo=orgInfoDao.findByUidAndSiteUid(orgUids,siteUid);
        }
        if (listOrgInfo != null && listOrgInfo.size() > 0) {
            for (int i = 0; i < listOrgInfo.size(); ++i) {
                OrgInfo temp=listOrgInfo.get(i);
                Integer type=temp.getOrgType();
                if(map.containsKey(type)){
                    map.get(type).add(temp.getUid());
                }
                else{
                    List<String> tempSet=new ArrayList<String>();
                    tempSet.add(temp.getUid());
                    map.put(type, tempSet);
                }
            }
        }
        return map;
    }

    //where and
    public List<String> findAccessClinicalUidByUserUid(String userUid){
        Map<Integer,List<String>> map=findAccessUidByUserUid(userUid);
        if(map.size()>0){
            List<String> tempSet=null;
            List<String> clinicalSet=null;
            tempSet=map.get(4);
            boolean hasClinical=false;
            if(tempSet!=null){
                hasClinical=true;
                clinicalSet=tempSet;
            }
            if(!hasClinical){
                clinicalSet=new ArrayList<String>();
                map.put(4, clinicalSet);
            }
            tempSet=map.get(3);
            if(tempSet!=null){
                List<String> re=orgInfoDao.findClinicalUidBySiteUid(tempSet);
                if(re!=null){
                    clinicalSet.addAll(re);
                }
            }
            tempSet=map.get(2);
            if(tempSet!=null){
                List<String> re=orgInfoDao.findClinicalUidByHospitalUid(tempSet);
                if(re!=null){
                    clinicalSet.addAll(re);
                }
            }
            tempSet=map.get(1);
            if(tempSet!=null){
                List<String> re=orgInfoDao.findClinicalUidByInstitutionUid(tempSet);
                if(re!=null){
                    clinicalSet.addAll(re);
                }
            }
            tempSet=map.get(0);
            if(tempSet!=null){
                List<String> re=orgInfoDao.findClinicalUidByTenantUid(tempSet);
                if(re!=null){
                    clinicalSet.addAll(re);
                }
            }
        }
        return map.get(4);
    }

    //where and
    public List<OrgInfo> findAccessClinicalUidByUserUid(String userUid,String siteUid){
        List<String> setAccessClinical=findAccessClinicalUidByUserUid(userUid);
        List<OrgInfo> re=null;
        if(setAccessClinical!=null && setAccessClinical.size()>0){
            re=orgInfoDao.findByUidAndSiteUid(setAccessClinical, siteUid);
        }
        return re;
    }

    public TreeNode buildOrgTree(String tenantUid, String filterKeyword, int maxOrgType, int maxOrgLevel){
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setShortName(WebUtil.getMessage("AllHospitals"));
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().addAll(getOrgFullTreeByTenant(tenantUid).getChildren());

        filterTreeNode(orgTree, filterKeyword, maxOrgType, maxOrgLevel);

        return orgTree;
    }

    public void expendedTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if(filterKeyword==null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doExpendedTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item->{
            item.setType("org");
            if(!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }

    private void doExpendedTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doExpendedTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);

            item.setType("hidden");
            OrgInfo org = (OrgInfo)item.getData();
            //if(!org.getName().contains(filterKeyword) && !org.getNamePyCode().contains(filterKeyword)) return;
            if(maxOrgType >=0 && org.getOrgType()>maxOrgType) return;
            if(maxOrgLevel >=0 && org.getOrgLevel()>maxOrgLevel) return;

            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }

    public void filterTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if(filterKeyword==null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doFilterTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item->{
            item.setType("org");
            if(!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }

    private void doFilterTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doFilterTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);

            item.setType("hidden");
            OrgInfo org = (OrgInfo)item.getData();
            if(!org.getName().contains(filterKeyword) && (org.getNameEn() == null?true:!org.getNameEn().contains(filterKeyword)) && (org.getNamePyCode()==null?true:!org.getNamePyCode().contains(filterKeyword))) return;
            if(maxOrgType >=0 && org.getOrgType()>maxOrgType) return;
            if(maxOrgLevel >=0 && org.getOrgLevel()>maxOrgLevel) return;

            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }

    public List<OrgInfo> getUserAccessibleSiteListByUid(String uid) {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, uid));
        Sort sort = new Sort(Sort.Direction.ASC, "orgIndex");
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    //获取该用户下的所有组织树
    public TreeNode buildEnableOrgTree (String userUid,String orgUid,OrgInfo orgInfo){
        List<OrgInfo> enableList = new ArrayList<>();
        if (null != orgInfo && 4 == orgInfo.getOrgType()) {
            //enableList = orgInfoDao.getByUser(userUid);
            enableList = collectChildOrgs(orgInfoDao.getByUid(orgUid), enableList);
        }else{
            enableList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(orgUid);
        }
        TreeNode orgTree = buildEnableTree(enableList);
        return orgTree;
    }

    public List<OrgInfo> collectChildOrgs(OrgInfo info, List<OrgInfo> clinicalOrgSet) {
        if (4 == info.getOrgType().intValue()) {
            //System.out.println("keshi----->" +info.getName());
            clinicalOrgSet.add(info);
        }
        List<OrgInfo> resultList = orgInfoDao.findByParentUID(info.getUid());
        if(resultList !=null&& !resultList.isEmpty()) {
            for (OrgInfo orgInfo : resultList) {
                collectChildOrgs(orgInfo, clinicalOrgSet);
            }
        }
        return clinicalOrgSet;
    }

    private TreeNode buildEnableTree(List<OrgInfo> orgList) {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        return rootNode;
    }

    /**
     * 创建智康宝GE FE USER CONFIG “Supplier”角色
     * @param userAccount
     * @param newRoleNames
     */
    @Transactional(rollbackFor = Exception.class)
    public void addZkbFeSupplierAdminUser(UserAccount userAccount, List<String> newRoleNames){
        List<UserRole> userRoles = userRoleRepository.findByUserIdAndRoleType(userAccount.getId(),5);
        if(CollectionUtils.isNotEmpty(userRoles) || newRoleNames.contains("后台运营管理")){
            List<ZkbFePartOrderUserConfig> list = null;
            ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
            list = zkbFePartOrderUserConfigRepository.findByTenantUidAndConfigType(userAccount.getTenantUID(), ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString());
            if(list != null && list.size() > 0){
                return;//如果当前租户已经创建过Supplier角色，则直接退出。
            }
            list = zkbFePartOrderUserConfigRepository.findByLoginName(userAccount.getLoginName());
            if(CollectionUtils.isNotEmpty(list)){
                if(!list.get(0).getConfigType().equals("Supplier")){
                    WebUtil.addErrorMessage("此用户在智康宝GE FE USER CONFIG中已有角色："+WebUtil.getMessage(list.get(0).getConfigType()));
                }
                return;
            }
            ZkbFePartOrderUserConfig zkbFePartOrderUserConfig = new ZkbFePartOrderUserConfig();
            zkbFePartOrderUserConfig.setLoginName(userAccount.getLoginName());
            zkbFePartOrderUserConfig.setName(userAccount.getName());
            zkbFePartOrderUserConfig.setEmail(userAccount.getEmail());
            zkbFePartOrderUserConfig.setPhone(userAccount.getTelephone());
            zkbFePartOrderUserConfig.setConfigType("Supplier");
            zkbFePartOrderUserConfig.setRemark("本用户由《用户管理》菜单页面中同步创建");
            zkbFePartOrderUserConfig.setTenantUid(userAccount.getTenantUID());
            zkbFePartOrderUserConfigRepository.save(zkbFePartOrderUserConfig);
        }
    }

    public List<PermissionGroup> getPermissionGroups(String tenantUid) {
        return permissionGroupMapper.getBySiteUid(tenantUid);
    }

    public List<String> getPermissionGroupsByUserUid(String uid) {
        return permissionGroupUserMapper.getPermissionGroupsByUserUid(uid);
    }

    public List<String> getFeatureListByTenantUid(String tenantUid) {
        TenantInfo tenantInfo = configService.getTenantByUid(tenantUid);
        if (tenantInfo != null && StringUtils.isNotEmpty(tenantInfo.getFeatureList())) {
            return (List<String>) JSONObject.parse(tenantInfo.getFeatureList());
        }
        return new ArrayList<>();
    }

    public Set<String> getFunctionListByTenantUid(String tenantUid) {
        Set<String> functionList = new HashSet<>();
        List<String> featureList = getFeatureListByTenantUid(tenantUid);
        if (CollectionUtils.isNotEmpty(featureList)) {
            List<ApmFeature> apmFeatures = Arrays.asList(ApmFeature.values()).stream().filter(feature -> featureList.contains(feature.name())).collect(Collectors.toList());
            apmFeatures.forEach(feature -> functionList.addAll(feature.getFunctionList().stream().map(f -> f.name()).collect(Collectors.toSet())));
        }
        return functionList;
    }

    public Set<String> getSysRoleListByTenantUid(String tenantUid) {
        Set<String> functionList = getFunctionListByTenantUid(tenantUid);
        Set<ApmFunction> apmFunctions = Arrays.asList(ApmFunction.values()).stream()
                .filter(function -> functionList.contains(function.name())).collect(Collectors.toSet());
        Set<String> roles = new HashSet<>();
        apmFunctions.forEach(f -> roles.addAll(f.getRoleList().stream().map(r -> r.name()).collect(Collectors.toSet())));
        return roles;
    }

    public boolean isOpenNewMenuRoles(String tenantUid) {
        TenantInfo tenantInfo = configService.getTenantByUid(tenantUid);
        return tenantInfo.getEnablesNewMenu() && StringUtils.isNotEmpty(tenantInfo.getFeatureList());
    }

    /**
     * 根据 ApmFunction 获取当前拥有该权限的所有用户
     * 两条路径：
     *   1. SysRole路径：ApmFunction.roleList → ApmSystemRole → SysRole → UserRole(roleType=0) → UserAccount
     *   2. CustomRole路径：CustomRole.functionList 包含该 ApmFunction → UserRole(roleType=1) → UserAccount
     *
     * @param apmFunction 目标 ApmFunction 枚举值
     * @param tenantUid   租户UID（用于限定 CustomRole 范围），传 null 则查全量
     * @return 拥有该 ApmFunction 的有效用户列表（去重）
     */
    public List<UserAccount> getUsersByApmFunction(ApmFunction apmFunction, String tenantUid) {
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);

        Set<Integer> userIds = new HashSet<>();

        // ── 路径 1：通过系统角色查找 ──
        // ApmFunction.roleList 包含的是 ApmSystemRole 枚举，通过 name() 匹配 SysRole.name
        List<String> sysRoleNames = apmFunction.getRoleList().stream()
                .map(ApmSystemRole::name)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(sysRoleNames)) {
            List<SysRole> sysRoles = sysRoleDao.getByNameIn(sysRoleNames);
            if (CollectionUtils.isNotEmpty(sysRoles)) {
                List<Integer> sysRoleIds = sysRoles.stream()
                        .map(SysRole::getId)
                        .collect(Collectors.toList());
                List<UserAccount> sysRoleUsers = userDao.findUsersBySysRoleIds(sysRoleIds);
                sysRoleUsers.forEach(u -> userIds.add(u.getId()));
            }
        }

        // ── 路径 2：通过自定义角色查找（CustomRole.functionList 是 JSON 数组，直接存 ApmFunction 名称）──
        List<CustomRole> customRoles;
        if (StringUtils.isNotEmpty(tenantUid)) {
            customRoles = customRoleRepository.findByTenantUID(tenantUid);
        } else {
            customRoles = (List<CustomRole>) customRoleRepository.findAll();
        }

        List<Integer> matchedCustomRoleIds = customRoles.stream()
                .filter(cr -> StringUtils.isNotEmpty(cr.getFunctionList())
                        && cr.getFunctionList().contains(apmFunction.name()))
                .map(CustomRole::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(matchedCustomRoleIds)) {
            List<UserAccount> customRoleUsers = userDao.findUsersByCustomRoleIds(matchedCustomRoleIds);
            customRoleUsers.forEach(u -> userIds.add(u.getId()));
        }

        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        return userDao.getByIds(userIds);
    }

    public List<OrgInfo> filterNoneAsset(List<String> orgUidList) {
        orgUidList = ObjectUtil.isEmpty(orgUidList) ? ListUtil.newArrayList("-1") : orgUidList;
        return orgInfoMapper.filterNoneAsset(orgUidList);
    }


    /**
     * 根据父节点UID,从现有的TreeNode树结构中查找直接子节点(仅下一级)
     * @param orgTree 已有的组织树
     * @param parentUid 父节点UID
     * @return 直接子节点的OrgInfo列表
     */
    public List<OrgInfo> getDirectChildrenFromTree(TreeNode orgTree, String parentUid) {
        List<OrgInfo> directChildren = new ArrayList<>();

        // 找到父节点
        TreeNode parentNode = TreeNodeUtil.findNodeFromTree(orgTree,
                orgInfoDao.findByUid(parentUid));

        if (parentNode == null) {
            logger.warn("Parent node not found for uid: " + parentUid);
            return directChildren;
        }

        // 只获取直接子节点
        for (TreeNode child : parentNode.getChildren()) {
            OrgInfo orgInfo = (OrgInfo) child.getData();
            if (orgInfo != null) {
                directChildren.add(orgInfo);
            }
        }

        return directChildren;
    }

    public List<OrgInfo> getBySiteUidDeptList(String siteUid){


       return orgInfoDao.getInstitutionUIDAndOrgType(siteUid);
    }


}
