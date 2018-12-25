package com.ge.apm.view;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.ContractInfoService;
import com.ge.apm.service.common.SearchService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.dao.SearchFilter.Operator;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class SearchController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
	public static final String STR_TENANTUID = "tenantUID";
	public static final String STR_INSTITUTIONUID = "institutionUID";
	public static final String STR_HOSPITALUID = "hospitalUID";
	public static final String STR_SITEUID = "siteUID";
	public static final String STR_DEPTUID = "clinicalDeptUID";
	public static final String STR_ASSETTYPE = "assetType";
	public static final String STR_ASSETNAME = "name";
	public static final String STR_ASSETNAME_PY = "namePyCode";
	public static final String STR_ASSETGROUP = "assetGroup";
	public static final String STR_FUNCTIONTYPE = "functionType";
	public static final String STR_QRCODE = "qrCode";
	public static final String STR_DEPTNUM = "departNum";
	public static final String STR_FINANCENUM = "financingNum";
	protected UserAccount currentUser;
	protected I18nMessageRepository i18nDao;
	SearchService searchService;
	OrgInfo orgSite;
	OrgInfo selectedOrgDepart;
	OrgInfo selectedOrg;
	AssetInfo selectedAssetInfo;
	TreeNode orgTree;
	TreeNode selectedNode;
	List<OrgInfo> listOrgSite;
	List<OrgInfo> listOrgDept;
	String assetName;
	String assetType;
	String functionType;
	String assetGroup;
	String threeQrCode;
	ServerEventInterface callingController;
	String updateViewIDs;

	// 1:contractedit,2:pcc,3:nursesite
	protected int type = 0;
	boolean blnAccessScope = false;
	boolean blnOrgTree = false;
	boolean showDialog = false;
	boolean blnSite = false;
	boolean blnDept = false;
	boolean blnAssetName = false;
	boolean blnAssetType = false;
	boolean blnAssetGroup = false;
	boolean blnFunctionType = false;
	boolean blnThreeQrCode = false;
	boolean blnPickList = false;
	boolean blnAssetSelect = false;
	boolean blnChangeEvent = false;
	boolean blnFilterStatus = true;
	boolean blnFilterValied = true;
	boolean blnFilterDelete = true;
	List<SearchFilter> listSearchFilter;
	DualListModel<AssetInfo> availableAssets;
	List<AssetInfo> listUnSelectedAssetInfo;
	protected List<AssetInfo> listSelectedAssetInfo;
	UaaService uaaService;
	AssetInstallationService aiService;
	ContractInfoService contractInfoService;
	AutoCompleteService autoCompleteService;
	String otherSiteUID;

	@PostConstruct
	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		searchService = WebUtil.getBean(SearchService.class);
		uaaService = WebUtil.getBean(UaaService.class);
		contractInfoService = WebUtil.getBean(ContractInfoService.class);
		aiService = WebUtil.getBean(AssetInstallationService.class);
		autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
		listSearchFilter = new ArrayList<SearchFilter>();

		updateData();
	}

	private void resetStatus() {
		orgSite = null;
		selectedOrgDepart = null;
		selectedOrg = null;
		selectedAssetInfo = null;
		if (listUnSelectedAssetInfo == null) {
			listUnSelectedAssetInfo = new ArrayList<AssetInfo>();
		} else {
			listUnSelectedAssetInfo.clear();
		}
		if (listSelectedAssetInfo == null) {
			listSelectedAssetInfo = new ArrayList<AssetInfo>();
		} else {
			listSelectedAssetInfo.clear();
		}
		if (availableAssets == null) {
			availableAssets = new DualListModel<>(listUnSelectedAssetInfo, listSelectedAssetInfo);
		}
		assetName = "";
		assetType = "";
		assetGroup = "";
		functionType = "";
		threeQrCode = "";

		blnAccessScope = false;
		blnOrgTree = false;
		blnSite = false;
		blnDept = false;
		blnAssetName = false;
		blnAssetType = false;
		blnAssetGroup = false;
		blnFunctionType = false;
		blnThreeQrCode = false;
		blnPickList = false;
		blnAssetSelect = false;
		blnChangeEvent = false;
	}

	public void updateData() {
		resetStatus();
		fetchOrgTree();
		if (type == 1) {
			blnPickList = true;
			blnAssetGroup = true;
			blnThreeQrCode = true;
			// fetchSiteOrgList();
			fetchDeptOrgList(selectedOrg);
//			fetchOriginPickListAssetInfo();
		} else if (type == 2) {
			blnAssetSelect = true;
			// fetchSiteOrgList();
			fetchDeptOrgList(selectedOrg);
			fetchOriginSelectAssetInfo();
		} else if (type == 3) {
			blnChangeEvent = true;
			blnAccessScope = true;
			// fetchSiteOrgList();
			fetchDeptOrgList(selectedOrg);
//			fetchOriginSelectAssetInfo();
		} else if (type == 4) {
			blnPickList = true;
			blnAssetName = true;
			blnAssetGroup = true;
			blnFunctionType = true;
			blnChangeEvent = true;
			blnAccessScope = true;
			// fetchSiteOrgList();
			fetchDeptOrgList(selectedOrg);
//			fetchOriginPickListAssetInfo();
		}
	}

	private void fetchOrgTree() {
		String siteUid = currentUser.getSiteUID();
		String uid = currentUser.getUid();
		if (type == 1) {
			if (StringUtils.isNotEmpty(otherSiteUID)) {
				siteUid = otherSiteUID;
			}
		}
		TreeNode subTree = null;
		if (blnAccessScope) {
			subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(siteUid, uid);
		} else {
			subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(siteUid);
		}
		orgTree = new DefaultTreeNode("org", null, null);
		orgTree.getChildren().add(subTree);
		selectedOrg = aiService.getOrgInfoByUid(siteUid);
	}

	public void fetchSiteOrgList() {
		if (searchService.isSiteUser(currentUser)) {
			listOrgSite = new ArrayList<OrgInfo>();
			OrgInfo tempOrg = searchService.getOrgInfoByUid(currentUser.getSiteUID());
			if (tempOrg != null) {
				listOrgSite.add(tempOrg);
			}
		} else {
			listOrgSite = searchService.getSiteListByHospitalUid(currentUser.getHospitalUID());
		}
	}

	public void fetchDeptOrgList(OrgInfo orgInfo) {
		if (blnAccessScope) {
			listOrgDept = searchService.fetchAccessOrgInfo(orgInfo.getSiteUID(), currentUser.getUid());
		} else {
			listOrgDept = searchService.getDeptListBySiteUid(orgInfo.getUid());
		}
	}

	private void onChange() {
		if (!generateListFilter()) {
			return;
		}
		if (blnPickList) {
			fetchOriginPickListAssetInfo();
		} else if (blnAssetSelect) {
			fetchOriginSelectAssetInfo();
		}
		if (blnChangeEvent) {
			if (callingController != null) {
				if (type == 2) {
					callingController.onServerEvent("change", selectedAssetInfo);
				} else if (type == 3) {
					if (selectedOrgDepart != null) {
						callingController.onServerEvent("change", selectedOrgDepart);
					} else {
						callingController.onServerEvent("change", selectedOrg);
					}
				} else if (type == 4) {
					callingController.onServerEvent("change", listSelectedAssetInfo);
				}
			}
		}
	}

	public void onSelectOrgFromTree(NodeSelectEvent event) {
		TreeNode node = event.getTreeNode();
		selectedOrg = (OrgInfo) node.getData();
		if (null != selectedOrg) {
			fetchDeptOrgList(selectedOrg);
			onChange();
		}
	}

	public void onOrgSiteChange() {
		fetchDeptOrgList(orgSite);
		onChange();
	}

	public void onOrgDeptChange() {
		onChange();
	}

	public void onAssetNameChange() {
		onChange();
	}

	public void onAssetTypeChange() {
		onChange();
	}

	public void onAssetGroupChange() {
		onChange();
	}

	public void onFunctionTypeChange() {
		onChange();
	}

	public void onThreeQrCodeChange() {
		onChange();
	}

	public void onAssetsTransfer(TransferEvent event) {
		List<AssetInfo> collection = (List<AssetInfo>) event.getItems();
		if (collection != null && collection.size() > 0) {
			if (event.isAdd()) {
				listSelectedAssetInfo.addAll(collection);
			} else {
				listSelectedAssetInfo.removeAll(collection);
			}
		}
	}

	private void fetchOriginPickListAssetInfo() {
		if (listSearchFilter.size() > 0) {
			listUnSelectedAssetInfo = searchService.getListAssetInfo(listSearchFilter);
			if (listUnSelectedAssetInfo == null) {
				listUnSelectedAssetInfo = new ArrayList<AssetInfo>();
			}
			listSelectedAssetInfo = new ArrayList<AssetInfo>();
			availableAssets = new DualListModel<>(listUnSelectedAssetInfo, listSelectedAssetInfo);
		}
	}

	private void fetchOriginSelectAssetInfo() {
		if (listSearchFilter.size() > 0) {
			listUnSelectedAssetInfo = searchService.getListAssetInfo(listSearchFilter);
			if (listUnSelectedAssetInfo == null) {
				listUnSelectedAssetInfo = new ArrayList<AssetInfo>();
			}
		}
	}

	public void prepareDialogCallback(ServerEventInterface callingController, int type, Object obj, String updateViewIDs) {
		this.type = type;
		this.callingController = callingController;
		this.updateViewIDs = updateViewIDs;
		if (type == 1) {
			this.otherSiteUID = (String) obj;
		}
		showDialog = true;
		updateData();
	}

	public void confirm() {
		showDialog = false;
		if (callingController != null) {
			if (type == 1) {
				callingController.onServerEvent("assetListSelected", listSelectedAssetInfo);
			}
		}
	}

	public List<AssetInfo> completeSelectedAssetInfo(String query) {
		return autoCompleteService.completeAssetInfoList(query, listUnSelectedAssetInfo);
	}

	private boolean generateListFilter() {
		listSearchFilter = new ArrayList<SearchFilter>();
		if (selectedOrg != null) {
			int orgTypeTemp = selectedOrg.getOrgType();
			if (orgTypeTemp == 0) {
				listSearchFilter.add(new SearchFilter(STR_TENANTUID, Operator.EQ, selectedOrg.getTenantUID(), "1*"));
			} else if (orgTypeTemp == 1) {
				listSearchFilter.add(new SearchFilter(STR_INSTITUTIONUID, Operator.EQ, selectedOrg.getInstitutionUID(), "1*"));
			} else if (orgTypeTemp == 2) {
				listSearchFilter.add(new SearchFilter(STR_HOSPITALUID, Operator.EQ, selectedOrg.getHospitalUID(), "1*"));
			} else if (orgTypeTemp == 3) {
				listSearchFilter.add(new SearchFilter(STR_SITEUID, Operator.EQ, selectedOrg.getSiteUID(), "1*"));
			}
			if (selectedOrgDepart != null) {
				listSearchFilter.add(new SearchFilter(STR_DEPTUID, Operator.EQ, selectedOrgDepart.getUid(), "1*"));
			}
			if (blnFilterStatus) {
				listSearchFilter.add(new SearchFilter("status", SearchFilter.Operator.NE, "4", "1*"));
			}
			if (blnFilterValied) {
				listSearchFilter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
			}
			if (blnFilterDelete) {
				listSearchFilter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
			}
			if (StringUtils.isNotEmpty(assetName)) {
				listSearchFilter.add(new SearchFilter(STR_ASSETNAME, Operator.LIKE, assetName, "1*3+"));
				listSearchFilter.add(new SearchFilter(STR_ASSETNAME_PY, Operator.LIKE, assetName, "1*3+"));
			}
			if (StringUtils.isNotEmpty(assetGroup)) {
				listSearchFilter.add(new SearchFilter(STR_ASSETGROUP, Operator.EQ, assetGroup, "1*"));
			}
			if (StringUtils.isNotEmpty(functionType)) {
				listSearchFilter.add(new SearchFilter(STR_FUNCTIONTYPE, Operator.LIKE, functionType, "1*"));
			}
			if (StringUtils.isNotEmpty(threeQrCode)) {
				listSearchFilter.add(new SearchFilter(STR_QRCODE, Operator.LIKE, threeQrCode, "1*2+"));
				listSearchFilter.add(new SearchFilter(STR_DEPTNUM, Operator.LIKE, threeQrCode, "1*2+"));
				listSearchFilter.add(new SearchFilter(STR_FINANCENUM, Operator.LIKE, threeQrCode, "1*2+"));
			}
		}
		if (listSearchFilter.size() > 1) {
			return true;
		}
		return false;
	}

	public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public OrgInfo getOrgSite() {
		return orgSite;
	}

	public void setOrgSite(OrgInfo orgSite) {
		this.orgSite = orgSite;
	}

	public OrgInfo getSelectedOrgDepart() {
		return selectedOrgDepart;
	}

	public void setSelectedOrgDepart(OrgInfo selectedOrgDepart) {
		this.selectedOrgDepart = selectedOrgDepart;
	}

	public List<OrgInfo> getListOrgSite() {
		return listOrgSite;
	}

	public void setListOrgSite(List<OrgInfo> listOrgSite) {
		this.listOrgSite = listOrgSite;
	}

	public List<OrgInfo> getListOrgDept() {
		return listOrgDept;
	}

	public void setListOrgDept(List<OrgInfo> listOrgDept) {
		this.listOrgDept = listOrgDept;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getThreeQrCode() {
		return threeQrCode;
	}

	public void setThreeQrCode(String threeQrCode) {
		this.threeQrCode = threeQrCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public I18nMessageRepository getI18nDao() {
		return i18nDao;
	}

	public void setI18nDao(I18nMessageRepository i18nDao) {
		this.i18nDao = i18nDao;
	}

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public ServerEventInterface getCallingController() {
		return callingController;
	}

	public void setCallingController(ServerEventInterface callingController) {
		this.callingController = callingController;
	}

	public String getUpdateViewIDs() {
		return updateViewIDs;
	}

	public void setUpdateViewIDs(String updateViewIDs) {
		this.updateViewIDs = updateViewIDs;
	}

	public boolean isBlnSite() {
		return blnSite;
	}

	public void setBlnSite(boolean blnSite) {
		this.blnSite = blnSite;
	}

	public boolean isBlnDept() {
		return blnDept;
	}

	public void setBlnDept(boolean blnDept) {
		this.blnDept = blnDept;
	}

	public boolean isBlnAssetType() {
		return blnAssetType;
	}

	public void setBlnAssetType(boolean blnAssetType) {
		this.blnAssetType = blnAssetType;
	}

	public boolean isBlnThreeQrCode() {
		return blnThreeQrCode;
	}

	public void setBlnThreeQrCode(boolean blnThreeQrCode) {
		this.blnThreeQrCode = blnThreeQrCode;
	}

	public boolean isBlnAccessScope() {
		return blnAccessScope;
	}

	public void setBlnAccessScope(boolean blnAccessScope) {
		this.blnAccessScope = blnAccessScope;
	}

	public boolean isShowDialog() {
		return showDialog;
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

	public List<SearchFilter> getListSearchFilter() {
		return listSearchFilter;
	}

	public void setListSearchFilter(List<SearchFilter> listSearchFilter) {
		this.listSearchFilter = listSearchFilter;
	}

	public boolean isBlnAssetGroup() {
		return blnAssetGroup;
	}

	public void setBlnAssetGroup(boolean blnAssetGroup) {
		this.blnAssetGroup = blnAssetGroup;
	}

	public boolean isBlnFunctionType() {
		return blnFunctionType;
	}

	public void setBlnFunctionType(boolean blnFunctionType) {
		this.blnFunctionType = blnFunctionType;
	}

	public String getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(String assetGroup) {
		this.assetGroup = assetGroup;
	}

	public DualListModel<AssetInfo> getAvailableAssets() {
		return availableAssets;
	}

	public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
		this.availableAssets = availableAssets;
	}

	public List<AssetInfo> getListUnSelectedAssetInfo() {
		return listUnSelectedAssetInfo;
	}

	public void setListUnSelectedAssetInfo(List<AssetInfo> listUnSelectedAssetInfo) {
		this.listUnSelectedAssetInfo = listUnSelectedAssetInfo;
	}

	public List<AssetInfo> getListSelectedAssetInfo() {
		return listSelectedAssetInfo;
	}

	public void setListSelectedAssetInfo(List<AssetInfo> listSelectedAssetInfo) {
		this.listSelectedAssetInfo = listSelectedAssetInfo;
	}

	public OrgInfo getSelectedOrg() {
		return selectedOrg;
	}

	public void setSelectedOrg(OrgInfo selectedOrg) {
		this.selectedOrg = selectedOrg;
	}

	public TreeNode getOrgTree() {
		if (orgTree == null) {
			fetchOrgTree();
		}
		return orgTree;
	}

	public void setOrgTree(TreeNode orgTree) {
		this.orgTree = orgTree;
	}

	public boolean isBlnOrgTree() {
		return blnOrgTree;
	}

	public void setBlnOrgTree(boolean blnOrgTree) {
		this.blnOrgTree = blnOrgTree;
	}

	public UaaService getUaaService() {
		return uaaService;
	}

	public void setUaaService(UaaService uaaService) {
		this.uaaService = uaaService;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public boolean isBlnPickList() {
		return blnPickList;
	}

	public void setBlnPickList(boolean blnPickList) {
		this.blnPickList = blnPickList;
	}

	public AssetInfo getSelectedAssetInfo() {
		return selectedAssetInfo;
	}

	public void setSelectedAssetInfo(AssetInfo selectedAssetInfo) {
		this.selectedAssetInfo = selectedAssetInfo;
	}

	public boolean isBlnAssetSelect() {
		return blnAssetSelect;
	}

	public void setBlnAssetSelect(boolean blnAssetSelect) {
		this.blnAssetSelect = blnAssetSelect;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public boolean isBlnAssetName() {
		return blnAssetName;
	}

	public void setBlnAssetName(boolean blnAssetName) {
		this.blnAssetName = blnAssetName;
	}

	public boolean isBlnChangeEvent() {
		return blnChangeEvent;
	}

	public void setBlnChangeEvent(boolean blnChangeEvent) {
		this.blnChangeEvent = blnChangeEvent;
	}

	public String getOtherSiteUID() {
		return otherSiteUID;
	}

	public void setOtherSiteUID(String otherSiteUID) {
		this.otherSiteUID = otherSiteUID;
	}
}
