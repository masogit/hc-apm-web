package com.ge.apm.view.asset;

import javax.faces.bean.ManagedBean;

import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultTreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
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
    private String oneSearchStr;
    private ZkbContractAssetRepository zkbContractAssetRepository;
    private ZkbContractRepository zkbContractRepository;
    private UserContextService userContextService;

    private UserAccount user;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        zkbContractRepository= WebUtil.getBean(ZkbContractRepository.class);
        zkbContractAssetRepository= WebUtil.getBean(ZkbContractAssetRepository.class);
        this.filterBySite = false;
        this.filterByHospital = false;
        userContextService = WebUtil.getBean(UserContextService.class);
        user = UserContextService.getCurrentUserAccount();
    }


    public Boolean getConfirm(){
        if(selected==null)
            return true;
        else if(selected.getIsPm()==null){
            return false;
        }else if(selected.getIsPm()==true) {
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
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        if (!this.hasFilterOnField("siteUID")) {

            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }
        if (user.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
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
    
    public void onSelectTreeNodePMAssets(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        if (node.getType().equals("org")) {
            addAssetToOrgNode(node);
            node.setExpanded(true);
            node.getChildren().forEach(item ->{
                if(item.getData() instanceof AssetInfo){
                    if(((AssetInfo)item.getData()).getIsPm()!=null && !((AssetInfo)item.getData()).getIsPm()){
                        item.setSelectable(false);
                    }
                };
            
            });
            
            return;
        }
        node.setExpanded(true);

        Object nodeData = node.getData();
        if (nodeData instanceof AssetInfo) {
            selectedAsset = (AssetInfo) nodeData;
            selected =(AssetInfo) nodeData;
        } else {
            selectedAsset = null;
            selected = null;
        }
    }

    private AssetInfo selectedAsset = null;

    public void confirmSelection(String maintainFlag) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if(userContextService.isZkbUser()){
            //getzkb_contract
            List<ZkbContractAsset>  contractAssetList = zkbContractAssetRepository.findByAssetId(String.valueOf(selectedAsset.getUid()));
            if(CollectionUtils.isEmpty(contractAssetList)){
                if("t".equals(maintainFlag)){
                    WebUtil.addErrorMessageKey("对于智康保设备，设备有合同才可以创建保养工单！");
                }else{
                    WebUtil.addErrorMessageKey("对于智康保设备，设备有合同才可以创建报修工单！");
                }
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
            Date now = new Date();
            List<ZkbContract>  contractList =new ArrayList<>();
            for(ZkbContractAsset zkbContractAsset : contractAssetList) {
                String contractId = zkbContractAsset.getContractId();
                ZkbContract zkbContract = zkbContractRepository.findById(contractId);
                contractList.add(zkbContract);
            }
            Collections.sort(contractList, new Comparator<ZkbContract>() {
                public int compare(ZkbContract arg0, ZkbContract arg1) {
                    Date hits0 = arg0.getEndDate();
                    Date hits1 = arg1.getEndDate();
                    if (hits1.compareTo(hits0) > 0) {
                        return 1;
                    } else if (hits1.compareTo(hits0) == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            ZkbContract validZkbContract = contractList.get(0);
            if(validZkbContract !=null &&(now.getTime() >= getSomeDay(validZkbContract.getEndDate(),"+").getTime() || now.getTime() <= getSomeDay(validZkbContract.getStartDate(),"-").getTime())){
                if("t".equals(maintainFlag)){
                    WebUtil.addErrorMessageKey("对于智康保设备，合同有效期内的设备才可以创建保养工单！");
                }else{
                    WebUtil.addErrorMessageKey("对于智康保设备，合同有效期内的设备才可以创建报修工单！");
                }
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
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

    public Date getSomeDay(Date date,String operator) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day1 = c.get(Calendar.DATE);
        if("+".equals(operator)){
            //c.set(Calendar.DATE, day1 + 1);
            c.set(Calendar.HOUR, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
        }else {
            //c.set(Calendar.DATE, day1 - 1);
            c.set(Calendar.HOUR, 00);
            c.set(Calendar.MINUTE, 00);
            c.set(Calendar.SECOND, 00);
        }
        String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        System.out.println(dayAfter);
        return c.getTime();
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
        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
            return;
        }
        if (orgNode.getChildren().stream().filter(item -> item.getType().equals("asset")).count() == 0) {

            OrgInfo orgInfo = (OrgInfo) orgNode.getData();
            ArrayList<SearchFilter> orgFilters = new ArrayList<SearchFilter>();
            orgFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            orgFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            if (Objects.equals(orgInfo.getOrgType(), 4)) {
                orgFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
            } else {
                orgFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
                orgFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IsNull, null));
            }

            if (user.isEnableAssetTypeGroup()) {
                orgFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
            }
            List<AssetInfo> assetList = dao.findBySearchFilter(orgFilters);

            assetList.forEach(item -> {
                TreeNode assetNode = new DefaultTreeNode("asset", item, orgNode);
//             assetNode.setRowKey(item.getUid());
            });
        }
    }
    
    @Override
    public void setSearchFilter() {
    	// TODO Auto-generated method stub
    	super.setSearchFilter();
    	if (StringUtils.isNotEmpty(oneSearchStr)) {
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            if(userContextService.isCountyArea()){
                this.updateSearchFilter(new SearchFilter("systemId", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            }
        }
    }

    public void setResetSearchFilter() {
        // TODO Auto-generated method stub
        super.setSearchFilter();
        if(searchFilters.size()>0){
            searchFilters.clear();
        }
        this.setOneSearchStr("");
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


	public String getOneSearchStr() {
		return oneSearchStr;
	}


	public void setOneSearchStr(String oneSearchStr) {
		this.oneSearchStr = oneSearchStr;
	}
    
}
