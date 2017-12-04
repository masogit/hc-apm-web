package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.GbCodeRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.GbCode;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.GbCodeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SelectGbController extends JpaCRUDController<GbCode> {

    GbCodeRepository dao = null;

    private GbCodeService gbCodeService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(GbCodeRepository.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
    }

    @Override
    protected GbCodeRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<GbCode> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (!this.hasFilterOnField("siteUID")) {
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }
        return super.loadData(pageRequest);
    }

    private TreeNode orgGbCodeTree;

    public TreeNode getGbTree() {
        return orgGbCodeTree;
    }

    public void onSelectRow() {
        selectedGbCode = this.selected;
    }

    protected TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        if (node.getType().equals("gb")) {
            addGbToGbNode(node);
            node.setExpanded(true);
            return;
        }
        node.setExpanded(true);

        Object nodeData = node.getData();
        if (nodeData instanceof GbCode) {
            selectedGbCode = (GbCode) nodeData;
        } else {
            selectedGbCode = null;
        }
    }

    private GbCode selectedGbCode = null;

    public void confirmSelection() {
        if (this.selectedGbCode == null) {
            WebUtil.addErrorMessageKey("noGbSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        showDialog = false;
        if (callingController != null) {
            callingController.onServerEvent("onGbSelected", this.selectedGbCode);
        }
    }

    private String updateViewIDs;

    public String getUpdateViewIDs() {
        return updateViewIDs;
    }

    private ServerEventInterface callingController;


    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs) {
        showDialog = true;

        this.selected = null;
        this.selectedNode = null;
        this.selectedGbCode = null;

        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

        UaaService uaaService = WebUtil.getBean(UaaService.class);

        orgGbCodeTree =  gbCodeService.getGbCodeTree();
        if (orgGbCodeTree.getParent() != null) {
            orgGbCodeTree = orgGbCodeTree.getParent();
        }
    }

    public TreeNode getOrgGbCodeTree() {
        return orgGbCodeTree;
    }

    public void setOrgGbCodeTree(TreeNode orgGbCodeTree) {
        this.orgGbCodeTree = orgGbCodeTree;
    }

    private void addGbToGbNode(TreeNode gbNode) {
        List<TreeNode> children = gbNode.getChildren();
        GbCode gbNodeData = (GbCode)gbNode.getData();

        List<GbCode> subGbList = dao.getSubGb(gbNodeData.getLevel() + 1);
        subGbList.forEach(item->{
TreeNode x1Node= new DefaultTreeNode("gb",item,gbNode);
        });



    }

    public void onOrgExpand(NodeExpandEvent event) {
        addGbToGbNode(event.getTreeNode());
    }

    public void cancelDialog() {
        showDialog = false;
    }

    private boolean showDialog = false;

    public boolean isShowDialog() {
        return showDialog;
    }

    private TreeNode[] selectedNodes;

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }
}
