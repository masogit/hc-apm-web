/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.uaa;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.asset.QrCodeLibController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
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
public class OrgTreeController {
    private static final Logger logger = LoggerFactory.getLogger(OrgTreeController.class);
    UaaService uaaService = WebUtil.getBean(UaaService.class);
    private OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);


    private TreeNode siteOrgTree;
    private TreeNode selectedOrgNode;

    private TreeNode tenantOrgTreePage;
    private TreeNode selectedTenantOrgNode;

    private TreeNode selectedDeptOrgNode;
    private TreeNode deptOrgTree;

    private String siteUID;

    // TreeNodeName Filter
    private String treeNodeNameFilter;
    private String retrieval;
    private String tenantUid="";
    int siteId;

    public int getTenantId() {
        return siteId;
    }

    public void setTenantId(int siteId) {
        this.siteId = siteId;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(String retrieval) {
        this.retrieval = retrieval;
    }

    public TreeNode getTenantOrgTreePage() {
        return tenantOrgTreePage;
    }

    public void setTenantOrgTreePage(TreeNode tenantOrgTreePage) {
        this.tenantOrgTreePage = tenantOrgTreePage;
    }

    @PostConstruct
    protected void init() {
        tenantUid = UserContextService.getCurrentUserAccount().getTenantUID();//获取用户siteId用于区分用户是否是46号租户
        siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
        siteId = UserContextService.getCurrentUserAccount().getSiteId();
    }

    public OrgInfo getOrgInfoByUID(String uid) {
        return orgDao.getByUid(uid);
    }

    public TreeNode createTenantOrgTree() {

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        rootNode.getChildren().add(new DefaultTreeNode("org", rootOrg, null));
        List<OrgInfo> siteList = orgDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID())
                                        .stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());

        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }
    public TreeNode createTenantOrgTree46() {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        rootNode.getChildren().add(new DefaultTreeNode("org", rootOrg, null));
        List<OrgInfo> siteList = orgDao.getOrgTreeBySiteAndparm(siteUID,retrieval==null?"":retrieval).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
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



    public TreeNode getSiteClinicalDeptOrgTree() {
        if (null == siteOrgTree) {
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            if (uaaService.isIbSiteAdmin(currentUserAccount)) {
                return null;
            }
            siteOrgTree = createSiteListWithClinicalDeptOrgTree();
        }
        return siteOrgTree;
    }

    public TreeNode createSiteListWithClinicalDeptOrgTree() {
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid());
        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 3);
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

    public void filterTreeNodeByName() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        if (uaaService.isIbSiteAdmin(currentUserAccount)) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid(), treeNodeNameFilter, currentUserAccount.getSiteId());
            TreeNode siteTree = buildTree(siteList);
            expandedByOrgType(siteTree, 2);
            siteOrgTree = siteTree;
        } else {
            uaaService.filterTreeNode(siteOrgTree, treeNodeNameFilter, 3, 3);
        }
    }

    public void filterTreeNodeByNameType4() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        if (uaaService.isIbSiteAdmin(currentUserAccount)) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid(), treeNodeNameFilter, currentUserAccount.getSiteId());
            TreeNode siteTree = buildTree(siteList);
            expandedByOrgType(siteTree, 2);
            siteOrgTree = siteTree;
        } else {
            uaaService.filterTreeNode(siteOrgTree, treeNodeNameFilter, 4, 4);
        }
    }

    public TreeNode getSiteOrgTree() {
        if (null == siteOrgTree) {
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            if (uaaService.isIbSiteAdmin(currentUserAccount)) {
                return null;
            }
            siteOrgTree = createSiteOrgTree();
        }
        return siteOrgTree;
    }

    public TreeNode getTenantOrgTree() {
        if (null == tenantOrgTreePage) {
            if(judgeTheUser()){
                logger.info("===============================================TrgTreeController.getTenantOrgTree 优化逻辑");
                tenantOrgTreePage = createTenantOrgTree46();
            }else{
                logger.info("===============================================TrgTreeController.getTenantOrgTree 原始逻辑");
                tenantOrgTreePage = createTenantOrgTree();
            }
        }
        return tenantOrgTreePage;
    }

    public void filterNode(){
        tenantOrgTreePage = createTenantOrgTree46();
    }

    public void filterNodeByName(){
        if(judgeTheUser()){
            logger.info("===============================================TrgTreeController.getTenantOrgTree 优化逻辑");
            tenantOrgTreePage = createTenantOrgTree46();
        }else{
            logger.info("===============================================TrgTreeController.getTenantOrgTree 原始逻辑");
            tenantOrgTreePage = createTenantOrgTree();
        }
        if (!StringUtils.isEmpty(treeNodeNameFilter)) {
            uaaService.filterTreeNode(tenantOrgTreePage, treeNodeNameFilter, 3, 3);
        }
    }
    //判断租户是否是46号租户 是返回true 否返回false
    public boolean judgeTheUser(){
        logger.info("=================================OrgTreeController.judgeTheUser parameter siteUID={},tenantUID={},siteId={}",siteUID,tenantUid,siteId);
        if(siteUID.equals(tenantUid) || 46==siteId){
            return true;
        }else{
            return false;
        }
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
            expandedByOrgType(deptOrgTree, 4);
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

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

}
