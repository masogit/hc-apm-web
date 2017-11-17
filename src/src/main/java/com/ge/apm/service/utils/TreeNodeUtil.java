package com.ge.apm.service.utils;

import org.primefaces.model.TreeNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface TreeNodeUtil {

    static void expandTreeNode(TreeNode node) {

        if (node.isLeaf() || node.isExpanded()) {
            return;
        }
        node.setExpanded(true);
        node.getChildren().stream().forEach((TreeNode childNode) -> expandTreeNode(childNode));

    }

    static void sortTreeNodeByName(TreeNode node) {

        if (node.isLeaf()) {
            return;
        }

        List<TreeNode> nodeList = node.getChildren();

        for (TreeNode childNode: nodeList) {
            childNode.setParent(null);
        }

        Collections.sort(nodeList, (TreeNode o1, TreeNode o2) -> {
                String name1 = (String) o1.getData();
                String name2 = (String) o2.getData();
                return name1.compareTo(name2);
            }
        );

        for (TreeNode childNode: nodeList) {
            childNode.setParent(node);
        }

        nodeList.stream().forEach((TreeNode childNode) -> sortTreeNodeByName(childNode));

    }

}
