package com.ge.apm.view.component;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class UserSiteOrgTreeComponentController {

    public static final String ORG_CHANGE = "ORG_CHANGE";

    private UaaService uaaService;

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private TreeNode selectedNode;

    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private String treeNodeNameFilter;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    protected void init(){
        uaaService = WebUtil.getBean(UaaService.class);

        initOrgTree();
    }

    private void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root",null,null);
        TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
        if(!StringUtils.isEmpty(treeNode)){
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        notifyController();

    }

    public void registerController(ServerEventInterface controller) {
        controllerList.add(controller);
    }

    public void notifyController() {
        for (ServerEventInterface controller: controllerList) {
            controller.onServerEvent(ORG_CHANGE, selectedOrg);
        }

    }

}
