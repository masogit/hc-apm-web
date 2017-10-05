package com.ge.apm.service.orginfo;

import com.ge.apm.domain.OrgInfo;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgInfoService {
    private Logger logger = LoggerFactory.getLogger(getClass());


    public List<OrgInfo> flattenOrgTree(List<OrgInfo> orgInfoList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = null;

        for(OrgInfo org: orgInfoList){
            TreeNode node = treeMap.get(org.getUid());

            OrgInfo current = org;
            while(current.getParentUID()!=null){
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo)parentNode.getData();
            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null){
                    rootNode = treeMap.get(current.getUid());
                }
            }
        }

        List<OrgInfo> resultList = new ArrayList<>();

        traverseWithOrgInfo(rootNode, resultList);
        return resultList;
    }

    private void traverseWithOrgInfo(TreeNode node, List<OrgInfo> itemList) {

        OrgInfo orgInfo = (OrgInfo) node.getData();

        itemList.add(orgInfo);

        if (!node.isLeaf()) {
            for (TreeNode nodeItem : node.getChildren()) {
                traverseWithOrgInfo(nodeItem, itemList);
            }
        }

    }
}
