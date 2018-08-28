package com.ge.apm.service.orginfo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgInfoTreeService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public TreeNode buildHierachyClinicalDept(String siteUid, List<OrgInfo> orgInfoList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = new DefaultTreeNode("root", null, null);
        rootNode.setSelectable(false);

        for(OrgInfo org: orgInfoList){
            TreeNode currentNode = treeMap.get(org.getUid());

            if (org.getParentUID().equals(siteUid)) {

                rootNode.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeMap.get(org.getParentUID());
                parentNode.getChildren().add(currentNode);
            }

        }

        return rootNode;
    }

}
