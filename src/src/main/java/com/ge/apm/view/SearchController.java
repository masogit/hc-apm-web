package com.ge.apm.view;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.SearchService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.dao.SearchFilter.Operator;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class SearchController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
	public static final String STR_SITEUID="siteUID";
	public static final String STR_DEPTUID="clinicalDeptUID";
	public static final String STR_ASSETTYPE="assetType";
	public static final String STR_ASSETGROUP="assetGroup";
	public static final String STR_QRCODE="qrCode";
	public static final String STR_DEPTNUM="departNum";
	public static final String STR_FINANCENUM="financingNum";
	UserAccount currentUser;
	I18nMessageRepository i18nDao;
	SearchService searchService;
	OrgInfo orgSite;
	OrgInfo orgDepart;
	List<OrgInfo> listOrgSite;
	List<OrgInfo> listOrgDept;
	String assetType;
	String assetGroup;
	String threeQrCode;
	ServerEventInterface callingController;
	String updateViewIDs;
	int type=0;
	boolean blnSite=false;
	boolean blnDept=false;
	boolean blnAssetType=false;
	boolean blnThreeQrCode=false;
	boolean blnAccessScope=false;
	boolean showDialog=false;
	boolean blnAssetGroup=false;
	boolean blnFunctionType=false;
	List<SearchFilter> listSearchFilter;
	DualListModel<AssetInfo> availableAssets;
	List<AssetInfo> listUnSelectedAssetInfo;
	List<AssetInfo> listSelectedAssetInfo;

	@PostConstruct
	private void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		searchService=WebUtil.getBean(SearchService.class);
		listSearchFilter=new ArrayList<SearchFilter>();
		orgSite=null;
		orgDepart=null;
		assetType="";
		assetGroup="";
		threeQrCode="";
		if(type==1){
			blnSite=true;
			blnDept=true;
			blnAssetGroup=true;
			blnThreeQrCode=true;
			blnAccessScope=false;
			fetchSiteOrgList();
			fetchListOriginAssetInfo();
		}
	}
	public void fetchSiteOrgList(){
		if(blnAccessScope){
			
		}
		else{
			if(searchService.isSiteUser(currentUser)){
				listOrgSite=new ArrayList<OrgInfo>();
				OrgInfo tempOrg=searchService.getOrgInfoByUid(currentUser.getSiteUID());
				if(tempOrg!=null){
					listOrgSite.add(tempOrg);
				}
			}
			else{
				listOrgSite=searchService.getSiteListByHospitalUid(currentUser.getHospitalUID());
			}
		}
	}
	public void fetchDeptOrgList(){
		if(blnAccessScope){
			
		}
		else{
			listOrgDept=searchService.getDeptListBySiteUid(orgSite.getUid());
		}
	}
	private void onChange(){
		generateListFilter();
		if(type==1){
			fetchListOriginAssetInfo();
		}
	}
	public void onOrgSiteChange(){
		fetchDeptOrgList();
		onChange();
	}
	public void onOrgDeptChange(){
		onChange();
	}
	public void onAssetTypeChange(){
		onChange();
	}
	public void onAssetGroupChange(){
		onChange();
		
	}
	public void onThreeQrCodeChange(){
		onChange();
	}
	public void onAssetsTransfer(TransferEvent event) {
		List<AssetInfo> collection=(List<AssetInfo>) event.getItems();
		if(collection!=null && collection.size()>0){
			if (event.isAdd()) {
				listSelectedAssetInfo.addAll(collection);
			} else {
				listSelectedAssetInfo.removeAll(collection);
			}
		}
	}
	private void fetchListOriginAssetInfo(){
		if(listSearchFilter.size()>0){
			listUnSelectedAssetInfo=searchService.getListAssetInfo(listSearchFilter);
			if(listUnSelectedAssetInfo==null){
				listUnSelectedAssetInfo=new ArrayList<AssetInfo>();
			}
			listSelectedAssetInfo=new ArrayList<AssetInfo>();
			availableAssets=new DualListModel<>(listUnSelectedAssetInfo, listSelectedAssetInfo);
		}
		else{
			listUnSelectedAssetInfo=new ArrayList<AssetInfo>();
			listSelectedAssetInfo=new ArrayList<AssetInfo>();
			availableAssets=new DualListModel<>(listUnSelectedAssetInfo, listSelectedAssetInfo);
		}
	}
	public void prepareDialogCallback(ServerEventInterface callingController,int type, String updateViewIDs) {
		this.type=type;
        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;
        showDialog=true;
        init();
    }
	public void confirm() {
        showDialog = false;
        if (callingController != null) {
            callingController.onServerEvent("assetListSelected", listSelectedAssetInfo);
        }
    }
	private void generateListFilter(){
		listSearchFilter=new ArrayList<SearchFilter>();
		if(type==1){
			if(orgSite!=null){
				listSearchFilter.add(new SearchFilter(STR_SITEUID,Operator.EQ,orgSite.getUid(),"1*"));
			}
			if(orgDepart!=null){
				listSearchFilter.add(new SearchFilter(STR_DEPTUID,Operator.EQ,orgDepart.getUid(),"1*"));
			}
			if(StringUtils.isNotEmpty(assetGroup)){
				listSearchFilter.add(new SearchFilter(STR_ASSETGROUP,Operator.EQ,assetGroup,"1*"));
			}
			if(StringUtils.isNotEmpty(threeQrCode)){
				listSearchFilter.add(new SearchFilter(STR_QRCODE,Operator.LIKE,threeQrCode,"1*2+"));
				listSearchFilter.add(new SearchFilter(STR_DEPTNUM,Operator.LIKE,threeQrCode,"1*2+"));
				listSearchFilter.add(new SearchFilter(STR_FINANCENUM,Operator.LIKE,threeQrCode,"1*2+"));
			}
			blnThreeQrCode=true;
		}
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

	public OrgInfo getOrgDepart() {
		return orgDepart;
	}

	public void setOrgDepart(OrgInfo orgDepart) {
		this.orgDepart = orgDepart;
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

}
