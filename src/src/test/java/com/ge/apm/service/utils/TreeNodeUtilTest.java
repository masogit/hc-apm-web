package com.ge.apm.service.utils;

import com.ge.apm.domain.OrgInfo;
import org.junit.Test;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreeNodeUtilTest {

    @Test
    public void testSortTreeNodeByName() {
        TreeNode root = new DefaultTreeNode("root");

        TreeNode nodeA = new DefaultTreeNode(new OrgInfo("A"));
        TreeNode nodeA1 = new DefaultTreeNode(new OrgInfo("A1"));
        TreeNode nodeA2 = new DefaultTreeNode(new OrgInfo("A2"));
        TreeNode nodeA3 = new DefaultTreeNode(new OrgInfo("A3"));

        TreeNode nodeB = new DefaultTreeNode(new OrgInfo("B"));
        TreeNode nodeB1 = new DefaultTreeNode(new OrgInfo("B1"));
        TreeNode nodeB2 = new DefaultTreeNode(new OrgInfo("B2"));

        TreeNode nodeC = new DefaultTreeNode(new OrgInfo("C"));

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

        assertEquals("A", ((OrgInfo)testNodeA.getData()).getName());
        assertEquals("B", ((OrgInfo)testNodeB.getData()).getName());
        assertEquals("C", ((OrgInfo)testNodeC.getData()).getName());

        assertEquals("A1", ((OrgInfo)testNodeA.getChildren().get(0).getData()).getName());
        assertEquals("A2", ((OrgInfo)testNodeA.getChildren().get(1).getData()).getName());
        assertEquals("A3", ((OrgInfo)testNodeA.getChildren().get(2).getData()).getName());
    }

}