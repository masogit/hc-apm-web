package com.ge.apm.service.uaa;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.AssetTypeGroupMapper;
import com.ge.apm.dao.mapper.UserAssetTypeGroupMapper;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.SysAuthGroup;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetTypeGroupService {

    @Autowired
    AssetTypeGroupRepository sysAuthGroupDao;

    @Autowired
    AssetTypeGroupMapper sysAuthGroupMapper;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    UserAssetTypeGroupMapper userAuthGroupMapper;



    public TreeNode buildSysAuthGroupInTree() {

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        List<AssetTypeGroup> resultList = sysAuthGroupDao.listAssetTypeGroupInHierarchy(currentUserAccount.getTenantUID());

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup assetTypeGroup: resultList) {
            TreeNode node = new DefaultTreeNode("sysAuthGroup", assetTypeGroup, null);
            node.setRowKey(assetTypeGroup.getUid());
            treeNodeMap.put(assetTypeGroup.getUid(), node);
        }

        TreeNode root  = new DefaultTreeNode("root", null, null);

        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            AssetTypeGroup currentData = (AssetTypeGroup) currentNode.getData();

            if (null == currentData.getParentUid()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUid());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                }
            }

        }

        sortSysAuthGroupNaturally(root);

        return root;

    }

    public TreeNode buildUserAccessibleSysAuthGroupInTree() {
        String userUid = UserContextService.getCurrentUserAccount().getUid();

        TreeNode root  = new DefaultTreeNode("root", null, null);

        List<AssetTypeGroup> resultList = sysAuthGroupDao.listAssetTypeGroupInHierarchyByUser(userUid);

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup sysAuthGroup: resultList) {
            TreeNode node = new DefaultTreeNode("sysAuthGroup", sysAuthGroup, null);
            node.setRowKey(sysAuthGroup.getUid());
            treeNodeMap.put(sysAuthGroup.getUid(), node);
        }

        //build hierarchy
        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            SysAuthGroup currentData = (SysAuthGroup) currentNode.getData();

            if (null == currentData.getParentUid()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUid());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                } else {
                    //if parent node not selected, make itself as root node
                    root.getChildren().add(currentNode);
                }
            }

        }

        sortSysAuthGroupNaturally(root);

        return root;

    }

    public TreeNode buildUserAccessibleSysAuthGroupInTreeWithAncestorIncluded() {
        String userUid = UserContextService.getCurrentUserAccount().getUid();

        TreeNode root  = new DefaultTreeNode("root", null, null);

        List<SysAuthGroup> resultList = sysAuthGroupMapper.listSysAuthGroupInHierarchyByUserWithAncestorIncluded(userUid);

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (SysAuthGroup sysAuthGroup: resultList) {
            TreeNode node = new DefaultTreeNode("sysAuthGroup", sysAuthGroup, null);
            node.setRowKey(sysAuthGroup.getUid());
            node.setSelectable(sysAuthGroup.isAccessible());
            treeNodeMap.put(sysAuthGroup.getUid(), node);
        }

        //build hierarchy
        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            SysAuthGroup currentData = (SysAuthGroup) currentNode.getData();

            if (null == currentData.getParentUid()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUid());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                } else {
                    //if parent node not selected, make itself as root node
                    root.getChildren().add(currentNode);
                }
            }

        }

        sortSysAuthGroupNaturally(root);

        return root;

    }

    public void sortSysAuthGroupNaturally(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    AssetTypeGroup sag1 = (AssetTypeGroup) o1.getData();
            AssetTypeGroup sag2 = (AssetTypeGroup) o2.getData();
                    return sag1.getEncoded().compareTo(sag2.getEncoded());
                }
        );

        for (TreeNode node: root.getChildren()) {
            sortSysAuthGroupNaturally(node);
        }

    }

    public Set<String> collectChildAuthGroups(List<String> parentUid) {
        Set<String> orgUidSet = new HashSet<>();
        for (String strUid: parentUid) {
            collectChildClinicalGroups(strUid, orgUidSet);
        }
        return orgUidSet;
    }

    public void collectChildClinicalGroups(String parentUid,Set<String> clinicalDeptSet) {
        clinicalDeptSet.add(parentUid);
        List<AssetTypeGroup> resultList = sysAuthGroupDao.findByParentUidIn(new ArrayList<String>(){{add(parentUid);}});
        if(resultList !=null && !resultList.isEmpty()) {
            for (AssetTypeGroup sysAuthGroup : resultList) {
                collectChildClinicalGroups(sysAuthGroup.getUid(), clinicalDeptSet);
            }
        }
    }
    public Set<SysAuthGroup> collectChildSysAuthGroupInExstingList(List<SysAuthGroup> sysAuthGroupsMatchingByName, List<SysAuthGroup> sysAuthGroupList) {
        Set<SysAuthGroup> sysAuthGroupSet = new HashSet<>();
        for (SysAuthGroup sysAuthGroup: sysAuthGroupsMatchingByName) {
            collectChildSysAuthGroupInExstingList(sysAuthGroup, sysAuthGroupList, sysAuthGroupSet);
        }
        return sysAuthGroupSet;
    }

    public void collectChildSysAuthGroupInExstingList(SysAuthGroup parentSysAuthGroup, List<SysAuthGroup> sysAuthGroupList, Set<SysAuthGroup> sysAuthGroupSet) {
        sysAuthGroupSet.add(parentSysAuthGroup);
        for (SysAuthGroup sysAuthGroup : sysAuthGroupList) {
            if (!parentSysAuthGroup.getUid().equals(sysAuthGroup.getParentUid())) continue;
            collectChildSysAuthGroupInExstingList(sysAuthGroup, sysAuthGroupList, sysAuthGroupSet);
        }
    }
}
