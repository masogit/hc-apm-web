package com.ge.apm.service.utils;

import org.primefaces.model.TreeNode;

public interface TreeNodeUtil {
    static void expandTreeNode(TreeNode node) {

        if (node.isLeaf() || node.isExpanded()) {
            return;
        }
        node.setExpanded(true);
        node.getChildren().stream().forEach((TreeNode childNode) -> expandTreeNode(childNode));

    }
}
