package com.ge.apm.service.uaa;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.AssetTypeGroupMapper;
import com.ge.apm.dao.mapper.UserAssetTypeGroupMapper;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.dto.AssetTypeGroupDto;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetTypeGroupService {

    @Autowired
    AssetTypeGroupRepository assetTypeGroupDao;

    @Autowired
    AssetTypeGroupMapper assetTypeGroupMapper;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    UserAssetTypeGroupMapper userAuthGroupMapper;

    public TreeNode buildAssetTypeGroupInTree() {

        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();

        List<AssetTypeGroup> resultList = assetTypeGroupDao.listAssetTypeGroupInHierarchy(currentUserAccount.getTenantUID());

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup assetTypeGroup: resultList) {
            TreeNode node = new DefaultTreeNode("assetTypeGroup", assetTypeGroup, null);
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

        sortAssetTypeGroupNaturally(root);

        return root;

    }

    public TreeNode buildUserAccessibleAssetTypeGroupInTree() {
        String userUid = UserContextService.getCurrentUserAccount().getUid();

        TreeNode root  = new DefaultTreeNode("root", null, null);

        List<AssetTypeGroup> resultList = assetTypeGroupDao.listAssetTypeGroupInHierarchyByUser(userUid);

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup assetTypeGroup: resultList) {
            TreeNode node = new DefaultTreeNode("assetTypeGroup", assetTypeGroup, null);
            node.setRowKey(assetTypeGroup.getUid());
            treeNodeMap.put(assetTypeGroup.getUid(), node);
        }

        //build hierarchy
        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            AssetTypeGroup currentData = (AssetTypeGroup) currentNode.getData();

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

        sortAssetTypeGroupNaturally(root);

        return root;

    }

    public TreeNode buildUserAccessibleAssetTypeGroupInTreeWithAncestorIncluded() {
        String userUid = UserContextService.getCurrentUserAccount().getUid();

        TreeNode root  = new DefaultTreeNode("root", null, null);

        List<AssetTypeGroup> resultList = assetTypeGroupMapper.listAssetTypeGroupInHierarchyByUserWithAncestorIncluded(userUid);

        Map<String, TreeNode> treeNodeMap = new HashMap<>();

        for (AssetTypeGroup assetTypeGroup: resultList) {
            TreeNode node = new DefaultTreeNode("assetTypeGroup", assetTypeGroup, null);
            node.setRowKey(assetTypeGroup.getUid());
            node.setSelectable(assetTypeGroup.isAccessible());
            treeNodeMap.put(assetTypeGroup.getUid(), node);
        }

        //build hierarchy
        for (Map.Entry<String, TreeNode> entry: treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            AssetTypeGroup currentData = (AssetTypeGroup) currentNode.getData();

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

        sortAssetTypeGroupNaturally(root);

        return root;

    }

    public void sortAssetTypeGroupNaturally(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    AssetTypeGroup sag1 = (AssetTypeGroup) o1.getData();
            AssetTypeGroup sag2 = (AssetTypeGroup) o2.getData();
                    return sag1.getEncoded().compareTo(sag2.getEncoded());
                }
        );

        for (TreeNode node: root.getChildren()) {
            sortAssetTypeGroupNaturally(node);
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
        List<AssetTypeGroup> resultList = assetTypeGroupDao.findByParentUidIn(new ArrayList<String>(){{add(parentUid);}});
        if(resultList !=null && !resultList.isEmpty()) {
            for (AssetTypeGroup assetTypeGroup : resultList) {
                collectChildClinicalGroups(assetTypeGroup.getUid(), clinicalDeptSet);
            }
        }
    }
    public Set<AssetTypeGroup> collectChildAssetTypeGroupInExstingList(List<AssetTypeGroup> assetTypeGroupsMatchingByName, List<AssetTypeGroup> assetTypeGroupList) {
        Set<AssetTypeGroup> assetTypeGroupSet = new HashSet<>();
        for (AssetTypeGroup assetTypeGroup: assetTypeGroupsMatchingByName) {
            collectChildAssetTypeGroupInExstingList(assetTypeGroup, assetTypeGroupList, assetTypeGroupSet);
        }
        return assetTypeGroupSet;
    }

    public void collectChildAssetTypeGroupInExstingList(AssetTypeGroup parentAssetTypeGroup, List<AssetTypeGroup> assetTypeGroupList, Set<AssetTypeGroup> assetTypeGroupSet) {
        assetTypeGroupSet.add(parentAssetTypeGroup);
        for (AssetTypeGroup assetTypeGroup : assetTypeGroupList) {
            if (!parentAssetTypeGroup.getUid().equals(assetTypeGroup.getParentUid())) continue;
            collectChildAssetTypeGroupInExstingList(assetTypeGroup, assetTypeGroupList, assetTypeGroupSet);
        }
    }

    public Set<AssetTypeGroupDto> collectChildAssetTypeGroupInExstingList1(List<AssetTypeGroupDto> assetTypeGroupsMatchingByName, List<AssetTypeGroupDto> assetTypeGroupList) {
        Set<AssetTypeGroupDto> assetTypeGroupSet = new HashSet<>();
        for (AssetTypeGroupDto assetTypeGroup: assetTypeGroupsMatchingByName) {
            collectChildAssetTypeGroupInExstingList1(assetTypeGroup, assetTypeGroupList, assetTypeGroupSet);
        }
        return assetTypeGroupSet;
    }

    public void collectChildAssetTypeGroupInExstingList1(AssetTypeGroupDto parentAssetTypeGroup, List<AssetTypeGroupDto> assetTypeGroupList, Set<AssetTypeGroupDto> assetTypeGroupSet) {
        assetTypeGroupSet.add(parentAssetTypeGroup);
        for (AssetTypeGroupDto assetTypeGroup : assetTypeGroupList) {
            if (!parentAssetTypeGroup.getUid().equals(assetTypeGroup.getParentUid())) continue;
            collectChildAssetTypeGroupInExstingList1(assetTypeGroup, assetTypeGroupList, assetTypeGroupSet);
        }
    }
}
