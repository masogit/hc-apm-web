package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by lsg on 20/7/2018.
 */

@ManagedBean
@ViewScoped
public class AssetSubscriberController  extends JpaCRUDController<MessageSubscriber> implements Serializable, ServerEventInterface {
	private AssetInfo selectedAsset;
	private DualListModel<AssetInfo> availableAssets;
	private List<AssetInfo> sourceAssets;
	private List<AssetInfo> targetAssets;
	//private QueryAsset queryAsset;
	private AssetInfo queryAsset1;
	private RelRuleAssetRepository relRuleAssetRepository;
	private Map<Integer, Boolean> selectedMap;
	private UserAccount currentUser;
	private UserAccountRepository userAccountRepository;
	private List<UserAccount> tenantUserList;
	private AssetInfoRepository assetInfoRepository;
	private OrgInfo selectedOrg;
	private AssetTagService assetTagService;

	UserContextService userContextService;
	MessageSubscriberRepository dao;
	private Boolean level0;
	private Boolean level1;
	private Boolean level2;
	//消息设置变量组
	private Boolean critical;
	private Boolean important;
	private Boolean normal;
	private Boolean overMsg;//超时
	private Boolean contractDueHit;//质保提醒
	private Boolean matainRepHit;//维保提醒
	private Integer loginUserOrgType;

	private Boolean isInsulate;
	private OrgInfoRepository orgInfoDao;
	private Boolean isSelectedCurrentPage = false;

	private int selectType=1;
	private Boolean isReceiveTimeoutMsg;
	private List<MessageSubscriber> delMsgList;
	private UaaService uaaService;
	private TreeNode orgTree;
	private TreeNode selectedNode;
	private List<OrgInfo> clinicalDeptList;

	@Override
	protected void init() {
		this.selectedMap = new HashMap();
		assetTagService = WebUtil.getBean(AssetTagService.class);
		userContextService  = (UserContextService) WebUtil.getBean(UserContextService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		dao= WebUtil.getBean(MessageSubscriberRepository.class);
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
		targetAssets = new ArrayList<>();
		userCommonService = WebUtil.getBean(UserCommonService.class);
		tenantUserList =userAccountRepository.getByTenantUID(currentUser.getTenantUID());
		tenantUserList = tenantUserList.stream().distinct().collect(Collectors.toList());
		//queryAsset = new QueryAsset();
		//queryAsset.setTenantUID(currentUser.getTenantUID());

		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoDao.findByUid(currentUser.getSiteUID()).getOrgType();
		clinicalDeptList = new ArrayList<OrgInfo>();


		queryAsset1 = new AssetInfo();
		queryAsset1.setTenantUID(currentUser.getTenantUID());
		if(currentUser.getOrgLevel() != 0) {
			if (currentUser.getOrgLevel() == 1) {
				queryAsset1.setInstitutionUID(currentUser.getInstitutionUID());
			} else if (currentUser.getOrgLevel() == 2) {
				queryAsset1.setHospitalUID(currentUser.getHospitalUID());
			} else {
				queryAsset1.setSiteUID(currentUser.getSiteUID());
			}
		}
		initAssets();
        initOrgTree();
	}

	@Override
	public void onBeforeNewObject(MessageSubscriber object) {
		object.setSiteId(currentUser.getSiteId());
		object.setHospitalId(currentUser.getHospitalId());
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());

		selectedOrg = null;
		clinicalDeptList = null;


		this.getQueryAssetsList();


	}

	public void getQueryAssetsList() {
		if(selectedOrg != null && selectedOrg.getOrgLevel() != null){
			sourceAssets = assetTagService.getAssetList(queryAsset1);

			sourceAssets = new ArrayList(sourceAssets);
			sourceAssets.removeAll(targetAssets);
		}else{
			sourceAssets = new ArrayList<AssetInfo>();
		}
		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
	}
	public void initOrgTree() {
		uaaService = WebUtil.getBean(UaaService.class);
		UserAccount loginUser = UserContextService.getCurrentUserAccount();

		TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

		OrgInfo rootOrg = new OrgInfo();
		rootOrg.setId(-1);
		rootOrg.setName(WebUtil.getMessage("AllHospitals"));
		orgTree = new DefaultTreeNode("root",null,null);
		TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
		rootNode.getChildren().add(treeNode);
		//rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(loginUser.getTenantUID()).getChildren());
		rootNode.setExpanded(true);

	}

	public String getSelectedAssetName(){
		AssetInfo assetInfo=assetInfoRepository.getById(selected.getAssetId());
		return assetInfo.getName();
	}
	public void onSelectTreeNode(NodeSelectEvent event) {
		queryAsset1.setInstitutionUID(null);
		queryAsset1.setHospitalUID(null);
		queryAsset1.setSiteUID(null);

		TreeNode node = event.getTreeNode();
		OrgInfo orgInfo = (OrgInfo) node.getData();
		if (orgInfo.getId() == -1) {
			selectedOrg = null;
		} else {
			selectedOrg = (OrgInfo) node.getData();
		}

		if(selectedOrg != null && selectedOrg.getOrgLevel() != null){
			if(selectedOrg.getOrgLevel() == 1){
				queryAsset1.setInstitutionUID(selectedOrg.getInstitutionUID());
				clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getInstitutionUID(), 4);
			}else if(selectedOrg.getOrgLevel() == 2){
				queryAsset1.setHospitalUID(selectedOrg.getHospitalUID());
				clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getHospitalUID(), 4);
			}else if(selectedOrg.getOrgLevel() == 3){
				queryAsset1.setSiteUID(selectedOrg.getSiteUID());
				clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getSiteUID(), 4);
			}
		}

		this.getQueryAssetsList();

	}


	public Integer getTrueOnMap(Map<Integer,Boolean> hm){
     //  int count =0;
       for(Map.Entry<Integer,Boolean> entry: hm.entrySet()){
       	if(entry.getValue()){
       		return entry.getKey();
		}
	   }
		return null;
	}


	public boolean multiSelect(){
		if(selectedMap.isEmpty()){
			return true;
		}
		List<Integer> collect = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item->item.getKey()).collect(Collectors.toList());
		if(collect.isEmpty()){
			return true;
		}
		Integer msgId =collect.get(0);
		selected=dao.findById(Integer.valueOf(msgId));
		if(selected!=null)
			assetName =selected.getAssetInfo().getName();
		//如果多于1个被选中
   	if(selectedMap.entrySet().stream().filter(item->item.getValue()).collect(Collectors.toList()).size()==1){
   		return false;
   		} else
   		return true;
	}
	public List<AssetInfo> assetList =null;
	public void changeSubId(){
		//assetList = assetInfoRepository.getUserListByAsset(selected.getSubscribeUserId());
		orgTree=createSiteOrgTree(userAccountRepository.getById(selected.getSubscribeUserId()).getUid());
		System.out.println(selectedOrg);
		selectedOrg=null;

		//addFilter();
	}

	public void onItemSelected() {
		isSelectedCurrentPage = !delMsgList.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
	}
	public void initAssets() {
		sourceAssets = new ArrayList<>();
		targetAssets.forEach((item) -> {
			if (sourceAssets.contains(item)) {
				sourceAssets.remove(item);
			}
		});
		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
	}
	public void onSelectAllOfPage() {
		for(MessageSubscriber msgsbu:delMsgList){
			selectedMap.put(msgsbu.getId(), this.isSelectedCurrentPage);
		}
		isSelectedCurrentPage = !delMsgList.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
	}



	public void delete(){
		delMsgList.forEach(item-> System.out.println(item.getId()));
		delMsgList=delMsgList.stream().filter(item->selectedMap.containsKey(item.getId())).filter(item->
				selectedMap.get(item.getId())).collect(Collectors.toList());

		if(delMsgList.isEmpty()){
			super.delete();
		}else {
			dao.delete(delMsgList);
		}
		selectedMap=new HashMap<Integer,Boolean>();
	}
	@Override
	protected MessageSubscriberRepository getDAO() {
		return dao;
	}
	private String assetName;

	public void onAssetsTransfer(TransferEvent event) {
		for (Object item : event.getItems()) {
			if (event.isAdd()) {
				targetAssets.add((AssetInfo) item);
			} else {
				targetAssets.remove((AssetInfo) item);
			}
		}
	}

	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		super.prepareCreate();
		this.selected.setSubscribeUserId(currentUser.getId());
		targetAssets = new ArrayList<AssetInfo>();
		sourceAssets =new ArrayList<AssetInfo>();
		availableAssets =new DualListModel<AssetInfo>();
		level0=true;
		level1=true;
		level2=true;
		isReceiveTimeoutMsg=true;
		assetList = assetInfoRepository.getUserListByAsset(selected.getSubscribeUserId());
	}
	@Override
	protected Page<MessageSubscriber> loadData(PageRequest pageRequest) {
		Page<MessageSubscriber> result;
		if(this.searchFilters ==null){
			searchFilters =  new ArrayList<>();
		}else{
			this.removeFilterOnField("subscribeUserId");
			this.removeFilterOnField("subscriberUser.tenantUID");
			this.removeFilterOnField("siteId");
		}
		//IT管理员可以看到所有关注标签的用户,
		if(!userContextService.hasRole("ITAdmin")) {
			searchFilters.add(new SearchFilter("subscribeUserId", SearchFilter.Operator.EQ, currentUser.getId()));
		}else{
			searchFilters.add(new SearchFilter("subscriberUser.tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
		}

		if(searchFilters.size() <= 0){
			result=dao.findAll(pageRequest);
			delMsgList = result.getContent();
			return result;
		}else{
			result= dao.findBySearchFilter(searchFilters, pageRequest);
			delMsgList = result.getContent();
			return result;
		}
	}
	//如果是新增，只有后台itadmin进来 可以修改。 如果是修改 全灰色
	public Boolean subscribe(){
		if(this.getCRUDActionName().equals("修改")){
			return true; //不可修改
		}else if(userContextService.hasRole("ITAdmin")){
			return false;
		}else
		{
			return true;
		}
	}
	public void onSelect(){
		selectedSetting();
	}
	public boolean isChecked() {
		Map<Integer, Boolean> map = this.selectedMap;
		for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
			if (entry.getValue()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		AssetInfo asset = (AssetInfo) eventObject;
		if (asset == null) {
			return;
		}
		assetName = asset.getName();
		this.selectedAsset = asset;
		this.selected.setAssetInfo(asset);
	}

	public void saveAssetSub(){
        MessageSubscriber msgsub= null;
        List<MessageSubscriber> msgsubList = new ArrayList<MessageSubscriber>();
		if(this.getCRUDActionName().equals("新增")) {
			if(targetAssets.isEmpty()){
				WebUtil.addErrorMessage("设备没有选取,请务必选择设备");
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				msgsubList = new ArrayList<MessageSubscriber>();
				return;
			}
			/* 如果加上合同等消息推送 替换成这个if
			if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false &&
					selected.getContractDueHit()==false && selected.getMeterHit()==false && selected.getIsReceiveTimeoutMsg()==false){*/
				if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false &&selected.getIsReceiveTimeoutMsg()==false){
				WebUtil.addErrorMessage("消息设置不可为空");
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				msgsubList = new ArrayList<MessageSubscriber>();
				return;
			}

			if(selected.getIsReceiveTimeoutMsg()){
				if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false){
					WebUtil.addErrorMessage("务必选择紧急程度");
					RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
					msgsubList = new ArrayList<MessageSubscriber>();
					return;
				}
			}

			for(AssetInfo assetInfo:targetAssets ){
				msgsub= new MessageSubscriber();
				msgsub.setAssetId(assetInfo.getId());
				msgsub.setSubscribeUserId(selected.getSubscribeUserId());
				msgsub.setLevel0(selected.getLevel0());
				msgsub.setLevel1(selected.getLevel1());
				msgsub.setLevel2(selected.getLevel2());
				//如果紧急程度任选了一个 说明维修事件被选择了
				if(selected.getLevel0() || selected.getLevel1() ||selected.getLevel2())
					msgsub.setRepairHit(true);
				else
					msgsub.setRepairHit(false);
				msgsub.setContractDueHit(selected.getContractDueHit());
				msgsub.setMainRepHit(selected.getMainRepHit());
				msgsub.setMeterHit(selected.getMeterHit());
				msgsub.setIsReceiveTimeoutMsg(selected.getIsReceiveTimeoutMsg());
				msgsubList.add(msgsub);
			}


			List<Integer> idList=msgsubList.stream().map(item->item.getAssetId()).collect(Collectors.toList());
			List<MessageSubscriber> msglist = dao.getMsgListByAssetIdAndUserId(idList,selected.getSubscribeUserId());
			if(msglist.size()>0){
				WebUtil.addErrorMessage("不能重复创建相同设备的消息订阅");
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				return;
			}
			dao.save(msgsubList);
			WebUtil.addSuccessMessage("成功创建设备消息订阅");

			assetName=null;
		}else{
			if(selected.getIsReceiveTimeoutMsg()){
				if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false){
					WebUtil.addErrorMessage("务必选择紧急程度");
					RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
					return;
				}
			}
			/* 如果加上合同等消息推送 替换成这个if
			* if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false &&
					selected.getContractDueHit()==false && selected.getMeterHit()==false)*/
			if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false){
				WebUtil.addErrorMessage("消息设置不可为空");
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				return;
			}
			if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false)
				selected.setRepairHit(false);
			else
				selected.setRepairHit(true);
			super.save();
		}
	}
	public void selectedSetting(){
		List<Integer> collect = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item->item.getKey()).collect(Collectors.toList());
		if(!collect.isEmpty()){
			Integer msgId =collect.get(0);
			selected=dao.findById(Integer.valueOf(msgId));
			assetName =selected.getAssetInfo().getName();
		}
	}
	public void cancel(){}


	@Override
	public void prepareEdit() {
		super.prepareEdit();
		selectedSetting();

		if(selected.getLevel0()!=null){
			level0=selected.getLevel0();
		}else{
			level0=false;
		}
		if(selected.getLevel1()!=null){
			level1=selected.getLevel1();
		}else{
			level1=false;
		}
		if(selected.getLevel2()!=null){
			level2=selected.getLevel2();
		}else{
			level2=false;
		}
//		if(queryAsset!=null)
//			queryAsset.setName(selected.getAssetInfo().getName());


	}

	public boolean requiredCheck(){
		/*必须要选择紧急程度*/
		if(!level0 && !level1 && !level2){
			WebUtil.addErrorMessage("务必选择紧急程度");
			RequestContext.getCurrentInstance().addCallbackParam("priority", true);
			return false;
		}
		if (targetAssets.isEmpty() && this.getCRUDActionName().equals("新增")) {
			WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
			RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
			return false;
		}
		return true;
	}
	/*public void addFilter() {
        orgTree=createSiteOrgTree();
		sourceAssets = getAssetList();
		targetAssets.forEach((item) -> {
			if (sourceAssets.contains(item)) {
				sourceAssets.remove(item);
			}
		});
		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
	}*/

	public TreeNode createSiteOrgTree(String userUid) {

		List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(userUid);
		TreeNode siteTree = buildTree(siteList);
		expandedByOrgType(siteTree, 2);
		return siteTree;
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
/**/
	/*public List<AssetInfo> getAssetList() {
		List<AssetInfo> astLIst =assetList;
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			astLIst=	astLIst.stream().filter(item->item.getFunctionType()!=null &&item.getFunctionType().equals(queryAsset.getFunctionType())).collect(Collectors.toList());
		}
		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			astLIst=	astLIst.stream().filter(item->item.getName()!=null && item.getName().equals(queryAsset.getName())).collect(Collectors.toList());
		}

		if(null != queryAsset.getManufactureId()){
			astLIst=	astLIst.stream().filter(item->item.getManufactureId()!=null && item.getManufactureId().equals(queryAsset.getManufactureId())).collect(Collectors.toList());
		}
		if (null != queryAsset.getAssetGroup()) {
			astLIst=	astLIst.stream().filter(item->item.getAssetGroup()!=null && item.getAssetGroup().equals(queryAsset.getAssetGroup())).collect(Collectors.toList());
		}
		if (null != queryAsset.getFunctionType()) {
			astLIst=	astLIst.stream().filter(item->item.getFunctionType()!=null &&item.getFunctionType().equals(queryAsset.getFunctionType())).collect(Collectors.toList());

		}
		if(!astLIst.isEmpty()){
			return astLIst;
		}

		return new ArrayList<>();
	}*/

	private UserCommonService userCommonService;

	public List<SearchFilter> getAssetFilter() {
		List<SearchFilter> filter = userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType);
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
		return filter;
	}
	public List<UserAccount> getTenantUserList() {
		return tenantUserList;
	}
	public void setTenantUserList(List<UserAccount> tenantUserList) {
		this.tenantUserList = tenantUserList;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public AssetInfo getSelectedAsset() {
		return selectedAsset;
	}

	public void setSelectedAsset(AssetInfo selectedAsset) {
		this.selectedAsset = selectedAsset;
	}



	public int getSelectType() {
		return selectType;
	}

	public void setSelectType(int selectType) {
		this.selectType = selectType;
	}

	public Boolean getIsReceiveTimeoutMsg() {
		return isReceiveTimeoutMsg;
	}

	public void setIsReceiveTimeoutMsg(Boolean receiveTimeoutMsg) {
		isReceiveTimeoutMsg = receiveTimeoutMsg;
	}





	public DualListModel<AssetInfo> getAvailableAssets() {
		return availableAssets;
	}

	public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
		this.availableAssets = availableAssets;
	}

	public List<AssetInfo> getSourceAssets() {
		return sourceAssets;
	}

	public void setSourceAssets(List<AssetInfo> sourceAssets) {
		this.sourceAssets = sourceAssets;
	}

	public List<AssetInfo> getTargetAssets() {
		return targetAssets;
	}

	public void setTargetAssets(List<AssetInfo> targetAssets) {
		this.targetAssets = targetAssets;
	}



	public RelRuleAssetRepository getRelRuleAssetRepository() {
		return relRuleAssetRepository;
	}

	public void setRelRuleAssetRepository(RelRuleAssetRepository relRuleAssetRepository) {
		this.relRuleAssetRepository = relRuleAssetRepository;
	}

	public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public UserAccountRepository getUserAccountRepository() {
		return userAccountRepository;
	}

	public void setUserAccountRepository(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	public AssetInfoRepository getAssetInfoRepository() {
		return assetInfoRepository;
	}

	public void setAssetInfoRepository(AssetInfoRepository assetInfoRepository) {
		this.assetInfoRepository = assetInfoRepository;
	}

	public UserContextService getUserContextService() {
		return userContextService;
	}

	public void setUserContextService(UserContextService userContextService) {
		this.userContextService = userContextService;
	}

	public MessageSubscriberRepository getDao() {
		return dao;
	}

	public void setDao(MessageSubscriberRepository dao) {
		this.dao = dao;
	}

	public Integer getLoginUserOrgType() {
		return loginUserOrgType;
	}

	public void setLoginUserOrgType(Integer loginUserOrgType) {
		this.loginUserOrgType = loginUserOrgType;
	}

	public Boolean getInsulate() {
		return isInsulate;
	}

	public void setInsulate(Boolean insulate) {
		isInsulate = insulate;
	}

	public OrgInfoRepository getOrgInfoRepository() {
		return orgInfoDao;
	}

	public void setOrgInfoRepository(OrgInfoRepository orgInfoDao) {
		this.orgInfoDao = orgInfoDao;
	}
	public UserCommonService getUserCommonService() {
		return userCommonService;
	}

	public void setUserCommonService(UserCommonService userCommonService) {
		this.userCommonService = userCommonService;
	}

	public Boolean getLevel0() {
		return level0;
	}

	public void setLevel0(Boolean level0) {
		this.level0 = level0;
	}

	public Boolean getLevel1() {
		return level1;
	}

	public void setLevel1(Boolean level1) {
		this.level1 = level1;
	}

	public Boolean getLevel2() {
		return level2;
	}

	public void setLevel2(Boolean level2) {
		this.level2 = level2;
	}

	public Boolean getCritical() {
		return critical;
	}

	public void setCritical(Boolean critical) {
		this.critical = critical;
	}

	public Boolean getImportant() {
		return important;
	}

	public void setImportant(Boolean important) {
		this.important = important;
	}

	public Boolean getNormal() {
		return normal;
	}

	public void setNormal(Boolean normal) {
		this.normal = normal;
	}

	public Boolean getOverMsg() {
		return overMsg;
	}

	public void setOverMsg(Boolean overMsg) {
		this.overMsg = overMsg;
	}

	public Boolean getMatainRepHit() {
		return matainRepHit;
	}

	public void setMatainRepHit(Boolean matainRepHit) {
		this.matainRepHit = matainRepHit;
	}

	public Boolean getReceiveTimeoutMsg() {
		return isReceiveTimeoutMsg;
	}

	public void setReceiveTimeoutMsg(Boolean receiveTimeoutMsg) {
		isReceiveTimeoutMsg = receiveTimeoutMsg;
	}

	public Boolean getIsSelectedCurrentPage() {
		return isSelectedCurrentPage;
	}

	public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
		isSelectedCurrentPage = selectedCurrentPage;
	}

	public Map<Integer, Boolean> getSelectedMap() {
		return selectedMap;
	}

	public void setSelectedMap(Map<Integer, Boolean> selectedMap) {
		this.selectedMap = selectedMap;
	}

	public List<MessageSubscriber> getDelMsgList() {
		return delMsgList;
	}

	public void setDelMsgList(List<MessageSubscriber> delMsgList) {
		this.delMsgList = delMsgList;
	}

	public AssetInfo getQueryAsset1() {
		return queryAsset1;
	}

	public void setQueryAsset1(AssetInfo queryAsset1) {
		this.queryAsset1 = queryAsset1;
	}

	public OrgInfo getSelectedOrg() {
		return selectedOrg;
	}

	public void setSelectedOrg(OrgInfo selectedOrg) {
		this.selectedOrg = selectedOrg;
	}

	public AssetTagService getAssetTagService() {
		return assetTagService;
	}

	public void setAssetTagService(AssetTagService assetTagService) {
		this.assetTagService = assetTagService;
	}

	public OrgInfoRepository getOrgInfoDao() {
		return orgInfoDao;
	}

	public void setOrgInfoDao(OrgInfoRepository orgInfoDao) {
		this.orgInfoDao = orgInfoDao;
	}

	public Boolean getSelectedCurrentPage() {
		return isSelectedCurrentPage;
	}

	public void setSelectedCurrentPage(Boolean selectedCurrentPage) {
		isSelectedCurrentPage = selectedCurrentPage;
	}

	public UaaService getUaaService() {
		return uaaService;
	}

	public void setUaaService(UaaService uaaService) {
		this.uaaService = uaaService;
	}

	public TreeNode getOrgTree() {
		return orgTree;
	}

	public void setOrgTree(TreeNode orgTree) {
		this.orgTree = orgTree;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<OrgInfo> getClinicalDeptList() {
		return clinicalDeptList;
	}

	public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
		this.clinicalDeptList = clinicalDeptList;
	}

	public void setAssetList(List<AssetInfo> assetList) {
		this.assetList = assetList;
	}
}
/*
* public List<AssetInfo> getAssetList_backup() {
		List<SearchFilter> assetFilters = new ArrayList<SearchFilter>();
		List<String> uids = null;
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
		}
		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
		}

		if(null != queryAsset.getManufactureId()){
			assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId()));
		}
		if (null != queryAsset.getAssetGroup()) {
			assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
		}
		if (null != queryAsset.getClinicalDeptId()) {
			assetFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
		}
		if (StringUtils.isNotEmpty(queryAsset.getBrand())) {
			assetFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, queryAsset.getBrand()));
		}
		if (!assetFilters.isEmpty()) {
			if (queryAsset.getRuleStatus() != null) {
				uids = new ArrayList<>();
				if (queryAsset.getRuleStatus() == 1) {// 已配置
					uids = relRuleAssetRepository.findAssetUidsByTenantUID(currentUser.getTenantUID());
					if (CollectionUtils.isNotEmpty(uids)) {
						assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
					}
				} else if (queryAsset.getRuleStatus() == 2) {// 未配置
					uids = relRuleAssetRepository.findAssetUidsByTenantUID(currentUser.getTenantUID());
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, uids));
				}
			}
			List<SearchFilter> userFilter = getAssetFilter();
			assetFilters.addAll(userFilter);
			return assetInfoRepository.findBySearchFilter(assetFilters);
		}
		return new ArrayList<>();
	}
* */