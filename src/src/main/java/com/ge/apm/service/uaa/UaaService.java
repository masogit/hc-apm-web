package com.ge.apm.service.uaa;

import java.util.*;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.ultima.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ge.apm.view.sysutil.UserContextService;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
        if(userContextService.hasRole("HospitalHead"))
            url = getDefautHomePage("HospitalHead");
        else if(userContextService.hasRole("AssetHead"))
            url = getDefautHomePage("AssetHead");
        else if(userContextService.hasRole("DeptHead"))
            url = getDefautHomePage("DeptHead");
        else if(userContextService.hasRole("AssetStaff"))
            url = getDefautHomePage("AssetStaff");
        else if(userContextService.hasRole("NursingStaff"))
            url = getDefautHomePage("NursingStaff");
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

        return sysRoleList;
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
            if (role==null){
                logger.warn("Assign role to user: Role not found, roleName="+newRoleName);
            }
            else {
                UserRole userRole = new UserRole();
                userRole.setUserId(userAccount.getId());
                userRole.setRoleId(role.getId());
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
    private  Map<String, TreeNode> treeMapG = new HashMap<>();
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

    public TreeNode getFilterOrgTreeByYuanQu(String siteUID,String keywords){
        treeNodeMap = new  LinkedHashMap<>();
        TreeNode filteredRoot  = new DefaultTreeNode("root", null, null);
        //xTreeMap
        Iterator<Map.Entry<String, TreeNode>> iterator = xtreeMap.entrySet().iterator();
        for(Map.Entry<String, TreeNode> nodeItem: xtreeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            //租户必须作为根节点先加入
            //if(org.getOrgType()==0){
            if(org.getUid().equals(rootUID)){
                TreeNode node1 = new DefaultTreeNode("org", org, null);
                node.setRowKey("org_"+org.getId());
                treeNodeMap.put(org.getUid(),node1);
            }
            buildTreeByKeyYuanqu(node,keywords);
        }
        //构建过虑好的树
        for(Map.Entry<String, TreeNode> nodeItem: treeNodeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            if(org.getParentUID()!=null){
                TreeNode parentNode = treeNodeMap.get(org.getParentUID());
                if(parentNode!=null){
                    parentNode.getChildren().add(node);
                }
            }
        }
        if(treeNodeMap.get(siteUID)!=null) {
            filteredRoot.getChildren().add(treeNodeMap.get(siteUID));
        }
        return filteredRoot;
    }

public void buildTreeByKey(TreeNode treeNode,String keyWords){
    //如果当前节点是最后一级即科室 则将该科室包括其上的父节点们添加到treeMapG
    if(treeNode.getChildren().isEmpty()){
        OrgInfo orgInfo =(OrgInfo)treeNode.getData();
        if(orgInfo.getName().contains(keyWords)){
            //将其到orgtype=0的所有父节点装入treeNodeMap
            treeNodeMap.put(orgInfo.getUid(),treeNode);
            //一旦找到 设备科 就将其 往上的父节点都加到树枝
            buidUpFromNode(treeNodeMap,treeNode);
        }
    }
     for (TreeNode child : treeNode.getChildren()) {
         buildTreeByKey(child,keyWords);
     }
}

    public void buildTreeByKeyYuanqu(TreeNode treeNode,String keyWords){
       // if(treeNode.getParent()!=null) {
            OrgInfo orgInfo = (OrgInfo) treeNode.getData();

            if (orgInfo.getName().contains(keyWords) || (orgInfo.getNamePyCode() != null && orgInfo.getNamePyCode().contains(keyWords))) {
                treeNodeMap.put(orgInfo.getUid(), treeNode);
                //如果找到 xx机构 就将其包括其父节点到到根节点都放到 treeNodeMap中
                buidUpFromNodeYuanQu(treeNodeMap, treeNode);

                buidUpFromNodeYuanQuDown(treeNodeMap, treeNode);

                if(orgInfo.getOrgType()==4){
                 buidUpFromNode(treeNodeMap, treeNode);
                }
            }

    }
    //就将该treeNode包括其父节点到到根节点都放到 treeNodeMap中
    public void buidUpFromNodeYuanQu(Map<String,TreeNode> treeNodeMap, TreeNode treeNode){
        if(treeNode==null){
            return;
        }
        OrgInfo orgInfo = (OrgInfo) treeNode.getData();
       if( orgInfo != null && orgInfo.getOrgType()>0){
        /*if(orgInfo.getName().contains("曙光东院")){
            System.out.println();
        }*/
            TreeNode treeNode1 = new DefaultTreeNode("org", orgInfo, null);
                   treeNodeMap.put(orgInfo.getUid(), treeNode1);

           buidUpFromNodeYuanQu(treeNodeMap, treeNode.getParent());
       }

    }
    public void buidUpFromNodeYuanQuDown(Map<String,TreeNode> treeNodeMap, TreeNode treeNode){
        ///
         if( treeNode.getChildren().size()!=0){
             for( TreeNode trn:treeNode.getChildren()){
                 OrgInfo orgInfo= (OrgInfo) trn.getData();
                 treeNodeMap.put(orgInfo.getUid(), trn);
                 buidUpFromNodeYuanQuDown(treeNodeMap,trn);
             }
         }
    }

    //一旦找到 设备科 就将其包括之上的父节点都加到treeMapG for building the filltered tree.
    public void buidUpFromNode(Map<String,TreeNode> treeNodeMap, TreeNode treeNode){
        if(treeNode != null){
            OrgInfo orgInfo =(OrgInfo)treeNode.getData();
            if(orgInfo.getParentUID()!=null){
                TreeNode node = new DefaultTreeNode("org", orgInfo, null);
                node.setRowKey("org_"+orgInfo.getId());
                treeNodeMap.put(orgInfo.getUid(),node);
                TreeNode parentNode = (TreeNode)treeMapG.get(orgInfo.getParentUID());
                buidUpFromNode(treeNodeMap,parentNode);
            }
        }

    }
    public Map<String,TreeNode> treeNodeMap = new  LinkedHashMap<>();
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
            treeMapG.put(org.getUid(),node);
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
        xtreeMap = treeMap;
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
            treeMapG.put(org.getUid(),node);
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
        xtreeMap = treeMap;
        if(!root.getChildren().isEmpty()){
            OrgInfo temp = (OrgInfo)root.getChildren().get(0).getData();
            rootUID = temp.getUid();
        }
        return root;
    }

    public  Map<String, TreeNode> xtreeMap;
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
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));
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
        List<String> regulatorUids = this.getRegulatorUidsBySiteUid(siteUid);
        if(regulatorUids != null && regulatorUids.size() > 0) {
            deptList = deptList.stream()
                                .filter(item -> !regulatorUids.contains(item.getUid()))
                                .collect(Collectors.toList());
        }
        return deptList;
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

}
