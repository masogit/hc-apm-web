package com.ge.apm.view.asset;

import javax.faces.bean.ManagedBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class SelectAssetController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    private UaaService uuaService;
    private AssetInstallationService aiService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
    }


    public Boolean getConfirm(){
        if(selected==null)
            return true;
        else if(selected.getPm()==null){
            return false;
        }else if(selected.getPm()==true) {
            return false;
        }else{
            return true;
        }
    }
    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (!this.hasFilterOnField("siteUID")) {

            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }
        return super.loadData(pageRequest);
    }



    private TreeNode orgAssetTree;

    public TreeNode getOrgAssetTree() {
        return orgAssetTree;
    }

    public boolean x1;
    public void onSelectRow() {
        selectedAsset = this.selected;
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
        if (node.getType().equals("org")) {
            addAssetToOrgNode(node);
            node.setExpanded(true);
            return;
        }
        node.setExpanded(true);

        Object nodeData = node.getData();
        if (nodeData instanceof AssetInfo) {
            selectedAsset = (AssetInfo) nodeData;
        } else {
            selectedAsset = null;
        }
    }

    private AssetInfo selectedAsset = null;

    public void confirmSelection() {
        if (this.selectedAsset == null) {
            WebUtil.addErrorMessageKey("noAssetSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        showDialog = false;
        if (callingController != null) {
            callingController.onServerEvent("onAssetSelected", this.selectedAsset);
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
        this.selectedAsset = null;

        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

        UaaService uaaService = WebUtil.getBean(UaaService.class);

        orgAssetTree = uaaService.getOrgAccessTreeByUser(UserContextService.getCurrentUserAccount());
        if (orgAssetTree.getParent() != null) {
            orgAssetTree = orgAssetTree.getParent();
        }
    }

    private void addAssetToOrgNode(TreeNode orgNode) {
        if (orgNode.getChildren().stream().filter(item -> item.getType().equals("asset")).count() == 0) {

            OrgInfo orgInfo = (OrgInfo) orgNode.getData();
            ArrayList<SearchFilter> orgFilters = new ArrayList<SearchFilter>();
            orgFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            orgFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            if (orgInfo.getOrgType() == 4) {
                orgFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
            } else {
                orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
                orgFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IsNull, null));
            }
            List<AssetInfo> assetList = dao.findBySearchFilter(orgFilters);

            assetList.forEach(item -> {
                TreeNode assetNode = new DefaultTreeNode("asset", item, orgNode);
//             assetNode.setRowKey(item.getUid());
            });
        }
    }
    
     public List<UserAccount> getOwnerList() {
        return aiService.getWorkerList();
    }

    public void onOrgExpand(NodeExpandEvent event) {
        addAssetToOrgNode(event.getTreeNode());
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
