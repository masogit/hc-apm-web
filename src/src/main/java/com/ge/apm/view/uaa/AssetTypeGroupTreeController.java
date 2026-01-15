package com.ge.apm.view.uaa;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetTypeGroupTreeController {
    @Getter @Setter
    private String treeNodeNameFilter;
    @Getter @Setter
    private boolean propagateSelectionUp = true;
    @Getter @Setter
    private TreeNode assetTypeGroupTree;
    @Getter @Setter
    private List<AssetTypeGroup> assetTypeGroups;
    @Getter @Setter
    private Map<String, AssetTypeGroup> assetTypeGroupMap;

    public TreeNode getAssetTypeGroupTreeByTenant() {
        return assetTypeGroupTree != null ? assetTypeGroupTree : getAssetTypeGroupTree(WebUtil.getBean(AssetTypeGroupRepository.class).listAssetTypeGroupInHierarchy(UserContextService.getCurrentUserAccount().getTenantUID()));
    }

    public TreeNode getAssetTypeGroupTreeByUser() {
        return assetTypeGroupTree != null ? assetTypeGroupTree : getAssetTypeGroupTree(UserContextService.getCurrentUserAccount().getAssetTypeGroupList());
    }

    private TreeNode getAssetTypeGroupTree(List<AssetTypeGroup> assetTypeGroups) {
        assetTypeGroupMap = assetTypeGroups.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, t -> t));
        Map<String, TreeNode> treeNodeMap = new HashMap<>();
        for (AssetTypeGroup assetTypeGroup: assetTypeGroups) {
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
        assetTypeGroupTree = root;
        return assetTypeGroupTree;
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

    public void filterTreeNodeByName() {
        if (StringUtils.isEmpty(treeNodeNameFilter)) {
            propagateSelectionUp = true;
        } else {
            if (!assetTypeGroupTree.getChildren().get(0).isSelected()) {
                propagateSelectionUp = false;
            }
        }
        filterTreeNode(assetTypeGroupTree, treeNodeNameFilter, -1, -1);
    }

    public void filterTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel) {
        // filter tree node by setting node type to "hidden" for invisible nodes and  "org" for visible nodes
        if (filterKeyword == null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);

        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doFilterTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item -> {
            item.setType("assetTypeGroup");
            if (!isKeywordEmpty) item.setExpanded(true); // Auto expand tree nodes
        });
    }

    private void doFilterTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel){
        node.getChildren().forEach(item->{
            doFilterTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);

            item.setType("hidden");
            AssetTypeGroup org = (AssetTypeGroup)item.getData();
            if(!org.getName().contains(filterKeyword)) return;
            nodeList.add(item);
            TreeNode pNode = node;
            while(pNode!=null){
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }

    public void onSelectAssetTypeTreeNode(NodeSelectEvent event) {
        setAssetTypeTreeNodes(event.getTreeNode(), true);
    }

    public void onUnSelectAssetTypeTreeNode(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }

    private void setAssetTypeTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    public List<String> getSelectedAssetTypeList() {
        List<String> selectedAssetTypeList = ListUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(assetTypeGroupTree)) {
            for (TreeNode treeNode : assetTypeGroupTree.getChildren()) {
                if (treeNode.isSelected()) {
                    selectedAssetTypeList.add(((AssetTypeGroup) treeNode.getData()).getEncoded());
                }
            }
        }
        return selectedAssetTypeList;
    }

    public String convertAssetTypeGroupEncodedToName(String encoded) {
        if (null == encoded) {
            return null;
        }
        AssetTypeGroup assetTypeGroup = assetTypeGroupMap.get(encoded);
        if (null == assetTypeGroup) {
            return null;
        }
        return assetTypeGroup.getDisplayName();
    }
}
