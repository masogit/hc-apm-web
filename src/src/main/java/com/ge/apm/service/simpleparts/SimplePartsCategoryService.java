package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.simpleparts.SimplePartsCategoryRepository;
import com.ge.apm.domain.simpleparts.SimplePartsCategory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimplePartsCategoryService {

    @Autowired
    private SimplePartsCategoryRepository partsDao;

    @Transactional(readOnly = true)
    public TreeNode buildPartsCategoryInTree(String tenantUid) {
        List<SimplePartsCategory> resultList = partsDao.listPartsCategoryInHierarchy(tenantUid);
        Map<String, TreeNode> treeNodeMap = new HashMap<>(8);
        for (SimplePartsCategory partsCategory : resultList) {
            TreeNode node = new DefaultTreeNode("partsCategory", partsCategory, null);
            node.setRowKey(partsCategory.getId());
            treeNodeMap.put(partsCategory.getId(), node);
        }
        TreeNode root = new DefaultTreeNode("root", null, null);
        for (Map.Entry<String, TreeNode> entry : treeNodeMap.entrySet()) {
            TreeNode currentNode = entry.getValue();
            SimplePartsCategory currentData = (SimplePartsCategory) currentNode.getData();
            if (null == currentData.getParentUid()) {
                root.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeNodeMap.get(currentData.getParentUid());
                if (null != parentNode) {
                    parentNode.getChildren().add(currentNode);
                }
            }
        }
        sortPartsCategoryByEncoded(root);
        return root;
    }

    public void sortPartsCategoryByEncoded(TreeNode root) {
        root.getChildren().sort((TreeNode o1, TreeNode o2) -> {
                    SimplePartsCategory sag1 = (SimplePartsCategory) o1.getData();
                    SimplePartsCategory sag2 = (SimplePartsCategory) o2.getData();
                    return sag1.getEncoded().compareTo(sag2.getEncoded());
                }
        );
        for (TreeNode node : root.getChildren()) {
            sortPartsCategoryByEncoded(node);
        }
    }

    public void filterTreeNode(TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel) {
        if (filterKeyword == null) filterKeyword = "";
        boolean isKeywordEmpty = "".equals(filterKeyword);
        ArrayList<TreeNode> nodeList = new ArrayList<>();
        doFilterTreeNode(nodeList, node, filterKeyword, maxOrgType, maxOrgLevel);
        nodeList.forEach(item -> {
            item.setType("partsCategory");
            if (!isKeywordEmpty) item.setExpanded(true);
        });
    }

    private void doFilterTreeNode(ArrayList<TreeNode> nodeList, TreeNode node, String filterKeyword, int maxOrgType, int maxOrgLevel) {
        node.getChildren().forEach(item -> {
            doFilterTreeNode(nodeList, item, filterKeyword, maxOrgType, maxOrgLevel);
            item.setType("hidden");
            SimplePartsCategory parts = (SimplePartsCategory) item.getData();
            if (!parts.getName().contains(filterKeyword)) return;
            nodeList.add(item);
            TreeNode pNode = node;
            while (pNode != null) {
                nodeList.add(pNode);
                pNode = pNode.getParent();
            }
        });
    }




}
