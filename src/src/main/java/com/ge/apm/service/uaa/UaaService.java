package com.ge.apm.service.uaa;

import java.util.*;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.InspectionChecklistRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InspectionChecklist;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;
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
    private static Map<String, SysRole> sysRoles;
    
    @Autowired
    OrgInfoRepository orgInfoDao;

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
        userRoleDao.deleteByUserAccount(userAccount);
        
        //then set user roles
        SysRoleRepository sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
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


    //gaolei2
    public TreeNode getFilterOrgTreeBySite2(String siteUID,String keywords){
        TreeNode filteredRoot  = new DefaultTreeNode("root", null, null);
        Iterator<Map.Entry<String, TreeNode>> iterator = xtreeMap.entrySet().iterator();
        //遍历xtreeMap为结构的机构树,将保留输入的查找如"设备科".
        for(Map.Entry<String, TreeNode> nodeItem: xtreeMap.entrySet()){
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo)node.getData();
            //租户必须作为根节点先加入
            if(org.getOrgType()==0){
                TreeNode node1 = new DefaultTreeNode("org", org, null);
                node.setRowKey("org_"+org.getId());
                treeNodeMap.put(org.getUid(),node1);
                x2(node);
            }
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


public void x2(TreeNode treeNode){
    //如果当前节点是最后一级即科室 则将该科室包括其上的父节点们添加到treeMapG
    if(treeNode.getChildren().isEmpty()){
        OrgInfo orgInfo =(OrgInfo)treeNode.getData();
        if(orgInfo.getName().contains("设备科")){
            //将其到orgtype=0的所有父节点装入treeNodeMap
            treeNodeMap.put(orgInfo.getUid(),treeNode);
            //一旦找到 设备科 就将其 往上的父节点都加到树立
            buidUpFromNode(treeNodeMap,treeNode);
        }
    }
     for (TreeNode child : treeNode.getChildren()) {
          x2(child);
     }

}

    public TreeNode x2_backup(TreeNode treeNode){
        //如果当前节点是最后一级即科室 则将该科室包括其上的父节点们添加到treeMapG
        if(treeNode.getChildren().isEmpty()){
            OrgInfo orgInfo =(OrgInfo)treeNode.getData();
            if(orgInfo.getName().contains("设备科")){
                //将其到orgtype=0的所有父节点装入treeNodeMap
                treeNodeMap.put(orgInfo.getUid(),treeNode);
                //一旦找到 设备科 就将其 往上的父节点都加到树立
                buidUpFromNode(treeNodeMap,treeNode);
            }
            //return null;
        }
        for (TreeNode child : treeNode.getChildren()) {
            TreeNode res = x2_backup(child);
        }
        return null;
    }
//一旦找到 设备科 就将其包括之上的父节点都加到treeMapG for building the filltered tree.
public void buidUpFromNode(Map<String,TreeNode> treeNodeMap, TreeNode treeNode){
      OrgInfo orgInfo =(OrgInfo)treeNode.getData();
      if(orgInfo.getParentUID()!=null){
          TreeNode node = new DefaultTreeNode("org", orgInfo, null);
          node.setRowKey("org_"+orgInfo.getId());
          treeNodeMap.put(orgInfo.getUid(),node);
         TreeNode parentNode = (TreeNode)treeMapG.get(orgInfo.getParentUID());
          buidUpFromNode(treeNodeMap,parentNode);
      }

}

    public void removeElement(List<TreeNode> trList){
        Iterator<TreeNode> it = trList.iterator();
        while(it.hasNext()){

            TreeNode treeNode = (TreeNode)it.next();
            OrgInfo orgInfo =(OrgInfo)treeNode.getData();
            if(!orgInfo.getName().contains("设备科")){
                it.remove();
            }
        }
    }

public Map<String,TreeNode> treeNodeMap = new  LinkedHashMap<>();


    //full of the tree for this org
    public TreeNode getOrgTreeBySite(String siteUID){
        List<OrgInfo> orgList = null;
        orgList = orgInfoDao.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(siteUID);
        TreeNode root  = new DefaultTreeNode("root", null, null);;
        
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
        return root;
    }
public  Map<String, TreeNode> xtreeMap;
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
    public void setDefaultScope(UserAccount user) {
        OrgInfo ownnedOrg = orgInfoDao.getByUid(user.getOrgInfoUID());
        if (ownnedOrg.getOrgType() == 4) {
            //if chosen org is clinical dept, then only select the current clinical dept instead of the whole site.
            UserOrgInfo userOrg = new UserOrgInfo();
            userOrg.setUserUid(user.getUid());
            userOrg.setOrgUid(ownnedOrg.getUid());
            userOrg.setOrgType(ownnedOrg.getOrgType());
            UserOrgInfoRepository userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
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
        List<OrgInfo> accessList = getOrgListByUser(userUid);
        Set<String> orgUidSet = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        if (orgUidSet.contains(siteUid)) {
            return accessList.stream().filter(item -> item.getSiteUID().equals(siteUid) && item.getOrgType() == 4).collect(Collectors.toList());
        } else {
            orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));
            orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
            return orgInfoDao.findBySearchFilter(orgInfoFilters);
        }
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

}
