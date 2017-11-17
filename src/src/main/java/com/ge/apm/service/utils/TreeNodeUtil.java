package com.ge.apm.service.utils;

import com.ge.apm.domain.OrgInfo;
import com.github.stuxuhai.jpinyin.PinyinHelper;
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

        nodeList.sort( (TreeNode o1, TreeNode o2) -> {
                OrgInfo org1 = (OrgInfo) o1.getData();
                OrgInfo org2 = (OrgInfo) o2.getData();
                try {
                    String name1 = PinyinHelper.getShortPinyin(org1.getName());
                    String name2 = PinyinHelper.getShortPinyin(org2.getName());
                    return name1.compareTo(name2);
                }catch(Exception e) {
                    return 0;
                }


            }
        );

        for (TreeNode childNode: nodeList) {
            childNode.setParent(node);
        }

        nodeList.stream().forEach((TreeNode childNode) -> sortTreeNodeByName(childNode));

    }

}
