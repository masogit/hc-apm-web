package com.ge.apm.service.utils;

import com.ge.apm.domain.OrgInfo;
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
                OrgInfo name1 = (OrgInfo) o1.getData();
                OrgInfo name2 = (OrgInfo) o2.getData();
                return name1.getName().compareTo(name2.getName());
            }
        );

        for (TreeNode childNode: nodeList) {
            childNode.setParent(node);
        }

        nodeList.stream().forEach((TreeNode childNode) -> sortTreeNodeByName(childNode));

    }

}
