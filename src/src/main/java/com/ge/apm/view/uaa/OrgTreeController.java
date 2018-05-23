/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class OrgTreeController {

    UaaService uaaService = WebUtil.getBean(UaaService.class);
    private OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
    
    
    private TreeNode siteOrgTree;
    private TreeNode selectedOrgNode;
    
    private TreeNode tenantOrgTree;
    private TreeNode selectedTenantOrgNode;

    private TreeNode selectedDeptOrgNode;
    private TreeNode deptOrgTree;

    @PostConstruct
    protected void init() {

    }

    public OrgInfo getOrgInfoByUID(String uid){
        return orgDao.getByUid(uid);
    }
    
    public TreeNode createTenantOrgTree() {
        
         OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        rootNode.getChildren().add(new DefaultTreeNode("org", rootOrg, null));
        List<OrgInfo> siteList = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID())
                .stream().filter(item -> item.getOrgType()<4).collect(Collectors.toList());
        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }
    
    public TreeNode createSiteOrgTree() {
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());
        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 2);
        return siteTree;
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

    public TreeNode getSiteOrgTree() {
        if (null == siteOrgTree) {
            siteOrgTree = createSiteOrgTree();
        }
        return siteOrgTree;
    }
    
    public TreeNode getTenantOrgTree() {
        if (null == tenantOrgTree) {
            tenantOrgTree = createTenantOrgTree();
        }
        return tenantOrgTree;
    }

    public List<OrgInfo> getClinicalDeptListBySite(String siteUID) {
        return uaaService.getClinicalDeptListBySiteUID(siteUID);
    }

    public TreeNode getDeptOrgTree(String siteUid) {
        if (null == deptOrgTree || !siteUid.equals(deptOrgTree.getRowKey())) {
            List<OrgInfo> deptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid());
            deptOrgTree = buildTree(deptList.stream().distinct().filter(item -> Strings.isNullOrEmpty(siteUid) || item.getTenantUID().equals(siteUid)
                    || item.getInstitutionUID().equals(siteUid) || item.getHospitalUID().equals(siteUid) || item.getSiteUID().equals(siteUid)).collect(Collectors.toList()));
            deptOrgTree.setRowKey(siteUid);
            selectedDeptOrgNode = null;
            expandedByOrgType(deptOrgTree,4);
        }
        return deptOrgTree;
    }

    public TreeNode getSelectedOrgNode() {
        return selectedOrgNode;
    }

    public void setSelectedOrgNode(TreeNode selectedOrgNode) {
        this.selectedOrgNode = selectedOrgNode;
    }

    public TreeNode getSelectedDeptOrgNode() {
        return selectedDeptOrgNode;
    }

    public void setSelectedDeptOrgNode(TreeNode selectedDeptOrgNode) {
        this.selectedDeptOrgNode = selectedDeptOrgNode;
    }

    public TreeNode getSelectedTenantOrgNode() {
        return selectedTenantOrgNode;
    }

    public void setSelectedTenantOrgNode(TreeNode selectedTenantOrgNode) {
        this.selectedTenantOrgNode = selectedTenantOrgNode;
    }

}
