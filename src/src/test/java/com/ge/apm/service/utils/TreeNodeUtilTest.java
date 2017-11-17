package com.ge.apm.service.utils;

import org.junit.Test;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreeNodeUtilTest {

    @Test
    public void testSortTreeNodeByName() {
        TreeNode root = new DefaultTreeNode("root");

        TreeNode nodeA = new DefaultTreeNode("A");
        TreeNode nodeA1 = new DefaultTreeNode("A1");
        TreeNode nodeA2 = new DefaultTreeNode("A2");
        TreeNode nodeA3 = new DefaultTreeNode("A3");

        TreeNode nodeB = new DefaultTreeNode("B");
        TreeNode nodeB1 = new DefaultTreeNode("B1");
        TreeNode nodeB2 = new DefaultTreeNode("B2");

        TreeNode nodeC = new DefaultTreeNode("C");

        root.getChildren().add(nodeB);
        root.getChildren().add(nodeA);
        root.getChildren().add(nodeC);

        nodeA.getChildren().add(nodeA2);
        nodeA.getChildren().add(nodeA1);
        nodeA.getChildren().add(nodeA3);

        nodeB.getChildren().add(nodeB2);
        nodeB.getChildren().add(nodeB1);

        TreeNodeUtil.sortTreeNodeByName(root);

        List<TreeNode> nodeList = root.getChildren();
        TreeNode testNodeA = nodeList.get(0);
        TreeNode testNodeB = nodeList.get(1);
        TreeNode testNodeC = nodeList.get(2);

        assertEquals("A", testNodeA.getData());
        assertEquals("B", testNodeB.getData());
        assertEquals("C", testNodeC.getData());

        assertEquals("A1", testNodeA.getChildren().get(0).getData());
        assertEquals("A2", testNodeA.getChildren().get(1).getData());
        assertEquals("A3", testNodeA.getChildren().get(2).getData());
    }

}