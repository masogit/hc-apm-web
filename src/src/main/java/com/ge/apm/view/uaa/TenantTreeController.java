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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.api.Request.BeginListener;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 503035627
 */
@ManagedBean
@ViewScoped
public class TenantTreeController {

	UaaService uaaService = WebUtil.getBean(UaaService.class);
	private OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

	private TreeNode tenantOrgTree;
	private TreeNode selectedTenantOrgNode;

	@PostConstruct
	protected void init() {

	}

	public OrgInfo getOrgInfoByUID(String uid) {
		return orgDao.getByUid(uid);
	}

	public TreeNode createTenantOrgTree() {
		TreeNode rootNode = new DefaultTreeNode("org", null, null);
		List<OrgInfo> orgList = orgDao.findSimpleAllAsList().stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
		buildTree(orgList, rootNode);
//		expandedByOrgType(rootNode);
		OrgInfo rootOrg = new OrgInfo();
		rootOrg.setOrgType(0);
		rootOrg.setOrgLevel(0);
		rootOrg.setId(-1);
		rootOrg.setName(WebUtil.getMessage("AllTenants"));
		rootNode.getChildren().add(0, new DefaultTreeNode("org", rootOrg, null));
		return rootNode;
	}

	private void expandedByOrgType(TreeNode tree) {
		int count=tree.getChildCount();
		if(count>0){
			tree.setExpanded(true);
		}
		for (TreeNode child : tree.getChildren()) {
			expandedByOrgType(child);
		}
	}

	private void buildTree(List<OrgInfo> orgList, TreeNode rootNode) {
		int beginLevel = 0;
		int lastLevel = 4;
		List<OrgInfo> listAddedOrgInfo = new ArrayList<OrgInfo>();
		Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
		while (beginLevel < lastLevel) {
			buildTree(orgList, treeMap,rootNode, listAddedOrgInfo, beginLevel);
			++beginLevel;
		}
		TreeNodeUtil.sortTreeNodeByName(rootNode);
	}

	private void buildTree(List<OrgInfo> orgList,Map<String, TreeNode> treeMap, TreeNode rootNode, List<OrgInfo> listAddedOrgInfo, int beginLevel) {
		for (OrgInfo org : orgList) {
			if (!listAddedOrgInfo.contains(org)) {
				String parentUid = org.getParentUID();
				Integer orgLevel = org.getOrgLevel();
				if (StringUtils.isEmpty(parentUid) || orgLevel == 0) {
					rootNode.getChildren().add(treeMap.get(org.getUid()));
					listAddedOrgInfo.add(org);
				} else if (orgLevel == beginLevel) {
					if (treeMap.containsKey(parentUid)) {
						treeMap.get(parentUid).getChildren().add(treeMap.get(org.getUid()));
						listAddedOrgInfo.add(org);
					}
				}
			}
		}
	}

	public TreeNode buildTenantTree(Integer type) {
		if (null == tenantOrgTree) {
			tenantOrgTree = createTenantOrgTree();
		}
		return tenantOrgTree;
	}

	public List<OrgInfo> getClinicalDeptListBySite(String siteUID) {
		return uaaService.getClinicalDeptListBySiteUID(siteUID);
	}

	public TreeNode getSelectedTenantOrgNode() {
		return selectedTenantOrgNode;
	}

	public void setSelectedTenantOrgNode(TreeNode selectedTenantOrgNode) {
		this.selectedTenantOrgNode = selectedTenantOrgNode;
	}

}
