package com.ge.apm.view.component;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class FilterSiteOrgTreeComponentController {

    public static final String FILTER_ORG_CHANGE = "FILTER_ORG_CHANGE";
    public static final String SELECTED_SITE_SESSION_KEY = "SELECTED_SITE_IN_COMPONENT";

    private UaaService uaaService = WebUtil.getBean(UaaService.class);

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private TreeNode selectedNode;

    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private String orgNameInputFilter;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    protected void init(){
        initOrgTree();
    }

    private void initOrgTree() {
        List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());
        orgTree = buildTree(siteList);
        expandedByOrgType(orgTree, 2);

        initializeCurrentSite();
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

    public void onSelectTreeNode(NodeSelectEvent event) {

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        saveSiteInSession(selectedOrg);

        notifyController();
    }

    private void saveSiteInSession(OrgInfo selectedOrg) {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        request.getSession().setAttribute(SELECTED_SITE_SESSION_KEY, selectedOrg);
    }

    private void initializeCurrentSite() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        selectedOrg = (OrgInfo)request.getSession().getAttribute(SELECTED_SITE_SESSION_KEY);
    }

    public void registerController(ServerEventInterface controller) {
        controllerList.add(controller);
    }

    public void notifyController() {
        for (ServerEventInterface controller: controllerList) {
            controller.onServerEvent(FILTER_ORG_CHANGE, selectedOrg);
        }

    }

    public String getSelectedOrgName() {
        return null == selectedOrg ? WebUtil.getMessage("AllHospitals") : selectedOrg.getName();
    }

    public void filterTreeNodeByName() {
        uaaService.filterTreeNode(orgTree, orgNameInputFilter, 3, 3);
    }

}