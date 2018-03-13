package com.ge.apm.service.utils;

import com.ge.apm.domain.OrgInfo;
import org.primefaces.model.TreeNode;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public interface TreeNodeUtil {

    public OrgInfoComparator orgInfoComparator = new OrgInfoComparator();

    static void expandTreeNode(TreeNode node) {

        if (node.isLeaf() || node.isExpanded()) {
            return;
        }
        node.setExpanded(true);
        node.getChildren().stream().forEach((TreeNode childNode) -> expandTreeNode(childNode));

    }

    static TreeNode findNodeFromTree(TreeNode tree, Object data) {
        if (null!=tree.getData() && tree.getData().equals(data)) {
            return tree;
        }
        for (TreeNode child : tree.getChildren()) {
            TreeNode res = findNodeFromTree(child, data);
            if(res != null){
                return res;
            }
        }
        return null;
    }
    
    static void expandParentsNodes(TreeNode node){
        if(node.getParent()!=null){
            node.setExpanded(true);
            expandParentsNodes(node.getParent());
        }
    }
    

    static void sortTreeNodeByName(TreeNode node) {

        if (node.isLeaf()) {
            return;
        }

        List<TreeNode> nodeList = new ArrayList<TreeNode>();
        nodeList.addAll(node.getChildren());



        nodeList.sort( (TreeNode o1, TreeNode o2) -> {
                OrgInfo org1 = (OrgInfo) o1.getData();
                OrgInfo org2 = (OrgInfo) o2.getData();

                return orgInfoComparator.compare(org1, org2);

            }
        );

        node.getChildren().clear();
        node.getChildren().addAll(nodeList);

        nodeList.stream().forEach((TreeNode childNode) -> sortTreeNodeByName(childNode));

    }

}
