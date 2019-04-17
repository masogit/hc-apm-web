package com.ge.apm.service.uaa;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.InspectionChecklistRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.InspectionChecklist;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.SysRoleVO;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

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
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private CustomRoleRepository customRoleRepository;
    

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
        if(userContextService.hasRole("NursingStaff"))
            url = getDefautHomePage("NursingStaff");
        else if(userContextService.hasRole("HospitalHead"))
            url = getDefautHomePage("HospitalHead");
        else if(userContextService.hasRole("AssetHead"))
            url = getDefautHomePage("AssetHead");
        else if(userContextService.hasRole("DeptHead"))
            url = getDefautHomePage("DeptHead");
        else if(userContextService.hasRole("AssetStaff"))
            url = getDefautHomePage("AssetStaff");
        else
            url = "/home.xhtml";
        
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

    public List<SysRoleVO> getSysRoleVO(String tenantUID){
        loadSysRoles();
        TenantInfo ti = tenantInfoDao.findByuid(tenantUID);
        List<SysRoleVO> sysRoleList = new ArrayList<>();
        for (SysRole tempSysRole : sysRoles.values()){
            SysRoleVO srVO = new SysRoleVO();
            srVO.setId(tempSysRole.getId());
            srVO.setName(tempSysRole.getName());
            srVO.setRoleDesc(tempSysRole.getRoleDesc());
            srVO.setRoleType(0);
            //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
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
    public void setUserRoles(UserAccount userAccount, List<String> newRoleNames){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);

        //first remove all existing user roles
        UserRoleRepository userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        TenantInfoRepository tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        userRoleDao.deleteByUserAccount(userAccount.getId());
        
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
        for(String newRoleName: newRoleNames){
            SysRole role = sysRoleDao.getByRoleDesc(newRoleName);
            if (role == null){
                CustomRole customRole = customRoleRepository.findByTenantUIDAndName(userAccount.getTenantUID(),newRoleName);
                if(customRole == null){
                    logger.warn("Assign role to user: Role not found, roleName="+newRoleName);
                    continue;
                }else{
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userAccount.getId());
                    userRole.setRoleId(customRole.getId());
                    userRole.setRoleType(1);
                    userRoleDao.save(userRole);
                }
            }
            else {
                UserRole userRole = new UserRole();
                userRole.setUserId(userAccount.getId());
                userRole.setRoleId(role.getId());
                userRole.setRoleType(0);
                userRoleDao.save(userRole);
            }
        }
    }

    @Transactional
    public void activeTemporaryUser(Integer userId){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        userDao.activeSingleUser(userId);
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
    
    //get tree for user to access
    public TreeNode getOrgAccessTreeByUser(UserAccount user){
        OrgInfo rootOrg = orgInfoDao.getByUid(user.getSiteUID());
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
        List<OrgInfo> orgList = null;
        orgList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(siteUID);
        TreeNode root  = new DefaultTreeNode("root", null, null);
        
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

        if(!root.getChildren().isEmpty()){
            OrgInfo temp = (OrgInfo)root.getChildren().get(0).getData();
            rootUID = temp.getUid();
        }
        return root;
    }

    public TreeNode getOrgTreeBySiteWithoutClinical(String siteUID){
        List<OrgInfo> orgList = null;
        orgList = orgInfoDao.getOrgInfosByParentId(siteUID);
        TreeNode root  = new DefaultTreeNode("root", null, null);

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
        if(!root.getChildren().isEmpty()){
            OrgInfo temp = (OrgInfo)root.getChildren().get(0).getData();
            rootUID = temp.getUid();
        }
        return root;
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
        OrgInfo ownnedOrg = orgInfoDao.getByUid(user.getOrgInfoUID());
        if (ownnedOrg.getOrgType() == 4) {
            //if chosen org is clinical dept, then only select the current clinical dept instead of the whole site.
            UserOrgInfo userOrg = new UserOrgInfo();
            userOrg.setUserUid(user.getUid());
            userOrg.setOrgUid(ownnedOrg.getUid());
            userOrg.setOrgType(ownnedOrg.getOrgType());
            userOrgDao.save(userOrg);
        } else {
            OrgInfo org = orgInfoDao.getByUid(user.getSiteUID());
            if (org != null) {
                UserOrgInfo userOrg = new UserOrgInfo();
                userOrg.setUserUid(user.getUid());
                userOrg.setOrgUid(org.getUid());
                userOrg.setOrgType(org.getOrgType());
                UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
                userOrgDao.save(userOrg);
            }
        }
        
        List<WorkflowConfig> workflowConfigs = wfcfigDao.findByDeptForSharedAsset(ownnedOrg.getUid());

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
        }

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

    //用户可以显示的医院列表(不包含科室)
    public List<OrgInfo> getUserAccessibleSiteList(String userUid) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(item -> item.getUid()).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
       orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
         orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, new ArrayList(orgUidSet2), "1*2+"));
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4)); //非科室
        return orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    //用户可以显示的医院列表(包含科室)
    public List<OrgInfo> getUserAccessibleSiteListWithClinicalDept(String userUid) {
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(item -> item.getUid()).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());

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

    public List<OrgInfo> getAllAccessibleClinicalDeptList() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> sitesList = this.getUserAccessibleSiteList(user.getUid());
        Set<String> sites = sitesList.stream().map(item -> item.getUid()).collect(Collectors.toSet());

        List<OrgInfo> dropdownList = getClinicalDeptListByAnySiteLevel(sites);

        return dropdownList;
    }

    public List<OrgInfo> getClinicalDeptListByAnySiteLevel(String siteUID) {
        List<SearchFilter> orgFilters = new ArrayList<>();
        orgFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, siteUID, "1*2+"));
        orgFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, siteUID, "1*2+"));
        orgFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, siteUID, "1*2+"));
        orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUID, "1*2+"));
        orgFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgInfoDao.findBySearchFilter(orgFilters);
    }

    public List<OrgInfo> getClinicalDeptListByAnySiteLevel(Set<String> sites) {
        List<String> siteList = new ArrayList<>(sites);

        List<SearchFilter> orgFilters = new ArrayList<>();
        orgFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, siteList, "1*2+"));
        orgFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, siteList, "1*2+"));
        orgFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, siteList, "1*2+"));
        orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteList, "1*2+"));
        orgFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgInfoDao.findBySearchFilter(orgFilters);
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
        List<OrgInfo> orgList = orgInfoDao.findBySearchFilter(orgInfoFilters);
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
    
    public TreeNode getOrgTreeByParentIdWithSelectedChildren(String parentOrgId) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getOrgInfosByParentId(parentOrgId);
        TreeNode rootNode = buildOrgTreeByParentIdWithSelectedChildren(orgInfoFullList,orgInfoFullList);

        return rootNode;
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

    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelected(String parentOrgId, String userUid) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        TreeNode rootNode = buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoSelectableList);

        return rootNode;
    }
    public TreeNode getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(String parentOrgId, String userUid) {

        List<OrgInfo> orgInfoFullList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(parentOrgId);
//        List<OrgInfo> orgInfoSelectableList = getUserAccessibleSiteListWithClinicalDept(userUid);

        TreeNode rootNode = buildOrgTreeByParentIdWithClinicalDeptsSelected(orgInfoFullList, orgInfoFullList);

        return rootNode;
    }

    public TreeNode buildOrgTreeByParentIdWithClinicalDeptsSelected(List<OrgInfo> orgInfoFullList, List<OrgInfo> orgInfoSelectableList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoFullList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_"+org.getId());
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
            }
        }

        return rootNode;
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
            if(!org.getName().contains(filterKeyword) && !org.getNamePyCode().contains(filterKeyword)) return;
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
}
