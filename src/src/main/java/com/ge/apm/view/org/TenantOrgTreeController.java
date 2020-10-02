/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.org;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class TenantOrgTreeController {
    private static final Logger logger = LoggerFactory.getLogger(TenantOrgTreeController.class);

    private OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    private TreeNode tenantOrgTreePage;

    private boolean includeClinicalDeptInTree = false;


    @PostConstruct
    protected void init() {

    }

    public void showClinicalDept() {
        includeClinicalDeptInTree = true;
    }

    public TreeNode getTenantOrgTree() {
        if (null == tenantOrgTreePage) {
            tenantOrgTreePage = createTenantOrgTree();
        }
        return tenantOrgTreePage;
    }

    public TreeNode createTenantOrgTree() {
        String tenantUid = UserContextService.getCurrentUserAccount().getTenantUID();

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        rootNode.getChildren().add(new DefaultTreeNode("org", rootOrg, null));

        List<OrgInfo> orgInfoList;
        if (includeClinicalDeptInTree) {
            orgInfoList = orgDao.getByTenantUID(tenantUid);
        } else {
            orgInfoList = orgDao.getSitesByTenantUID(tenantUid);
        }

        TreeNode siteTree = buildTree(orgInfoList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }

    private void expandedByOrgType(TreeNode tree, Integer type) {
        if (tree.getData() != null) {
            OrgInfo node = (OrgInfo) tree.getData();
            if (node.getOrgType() > type) {
                return;
            }
        }
        tree.setExpanded(true);
        for (TreeNode child : tree.getChildren()) {
            expandedByOrgType(child, type);
        }

    }

    private TreeNode buildTree(List<OrgInfo> orgList) {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        return rootNode;
    }

}
