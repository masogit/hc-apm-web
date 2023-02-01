package com.ge.apm.view.his;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisOrgComponentController {

    public static final String ORG_CHANGE = "ORG_CHANGE";

    private OrgInfoRepository orgInfoDao;
    private UaaService uaaService;

    @Getter@Setter
    private OrgInfo selectedOrg;

    @Getter@Setter
    private String treeNodeNameFilter;

    @Getter@Setter
    private TreeNode selectedNode;

    @Getter@Setter
    private TreeNode orgTree;

    @Getter@Setter
    private List<OrgInfo> orgInfoList;

    private List<ServerEventInterface> controllerList = new ArrayList<>();

    @PostConstruct
    public void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);

        selectedOrg = orgInfoDao.getByUid(UserContextService.getCurrentUserAccount().getOrgInfoUID());

        rebuildOrgList();
    }

    public void rebuildOrgList() {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        orgInfoFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.IsNotNull,null));
        orgInfoList = orgInfoDao.findBySearchFilter(orgInfoFilters);
    }

    public void onSelectOutSideTreeNode(SelectEvent event) {
        selectedOrg = (OrgInfo)event.getObject();

        notifyController();
    }

    public void rebuildOrgTree(String tenantUid) {
        orgTree = uaaService.buildOrgTree(tenantUid, "", -1, -1);
    }

    public void filterTreeNode() {
        uaaService.filterTreeNode(orgTree, treeNodeNameFilter, -1, -1);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();

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


