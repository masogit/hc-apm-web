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
    private OrgInfoRepository orgDao=WebUtil.getBean(OrgInfoRepository.class);
    private TreeNode siteOrgTree;

    @PostConstruct
    protected void init() {

    }

    public TreeNode createSiteOrgTree() {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        rootNode.getChildren().add(new DefaultTreeNode("org", rootOrg, null));
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());

        Map<String, TreeNode> treeMap = siteList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));

        for (OrgInfo org : siteList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        return rootNode;
    }

    public TreeNode getSiteOrgTree() {
        if (null == siteOrgTree) {
            siteOrgTree = createSiteOrgTree();
        }
        return siteOrgTree;
    }

    public List<OrgInfo> getClinicalDeptListBySite(String siteUID) {
        List<SearchFilter> orgFilters = new ArrayList<>();
        orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUID));
        orgFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
        return orgDao.findBySearchFilter(orgFilters);
    }

}
