package com.ge.apm.view.pm;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

	// 维修事件（紧急程度）列表页筛选：多选 1/2/3/4（不改动实体类）
	@Getter
	@Setter
	private List<String> repairEventFilter = new ArrayList<>();

	@Getter
	@Setter
	private List<AssetInfo> selectAssetInfos;


	@Getter
	@Setter
	private Boolean allSelected1 = Boolean.FALSE;
	@Getter
	@Setter
	private Boolean allSelected2 = Boolean.FALSE;
	@Getter
	@Setter
	private Map<String, Boolean> selectedAssetUidTable1;
	@Getter
	@Setter
	private Map<String, Boolean> selectedAssetUidTable2;
	@Getter
	@Setter
	private Set<AssetInfo> selectedAssetsed1;
	@Getter
	@Setter
	private Set<AssetInfo> selectedAssetsed2;
	@Getter
	@Setter
	private Set<AssetInfo> selectAsset;
	@Getter @Setter
	private List<Integer> subscribeUserIds = new ArrayList<>();
	@Getter @Setter
	private String selectUserName;
	@Getter @Setter
	private MessageSubscriber batch = new MessageSubscriber();


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
//		targetAssets = new ArrayList<>();
		userCommonService = WebUtil.getBean(UserCommonService.class);
		tenantUserList =userAccountRepository.getByTenantUID(currentUser.getTenantUID());
		tenantUserList = tenantUserList.stream().distinct().collect(toList());
		tenantUserList = tenantUserList.parallelStream().sorted(Comparator.comparing(UserAccount::getName)).collect((toList()));
		tenantUserList.sort(new PinYinComparatorByName());
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
		selectedAssetUidTable1=new HashMap<>();
		selectedAssetUidTable2=new HashMap<>();
		selectedAssetsed1=new HashSet<>();
		selectedAssetsed2=new HashSet<>();
		selectAsset=new HashSet<>();
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
			List<AssetInfo> queryList = assetTagService.getAssetList(queryAsset1);
			selectAssetInfos=new ArrayList<>(queryList);
			if(CollectionUtils.isNotEmpty(selectAsset)){
				selectAssetInfos.removeAll(new ArrayList<>(selectAsset));

			}
//			sourceAssets = new ArrayList(sourceAssets);
//			sourceAssets.removeAll(selectAsset);
		}else{
//			sourceAssets = new ArrayList<AssetInfo>();
			selectAssetInfos=new ArrayList<>();
		}
//		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
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

	public List<UserAccount> getUserByOrgTree() {
		if (null != selectedOrg) {
			return uaaService.getUsersByDeptUid(selectedOrg.getUid());
		}
		return new ArrayList<>();
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
		selectUserName = null;
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
		List<Integer> collect = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item->item.getKey()).collect(toList());
		if(collect.isEmpty()){
			return true;
		}
		Integer msgId =collect.get(0);
		selected=dao.findById(Integer.valueOf(msgId));
		if(selected!=null)
			assetName =selected.getAssetInfo().getName();
		//如果多于1个被选中
   	if(selectedMap.entrySet().stream().filter(item->item.getValue()).collect(toList()).size()==1){
   		return false;
   		} else
   		return true;
	}
	public List<AssetInfo> assetList =null;
	public void changeSubId(){
		UserAccount userAccount =  userAccountRepository.getById(selected.getSubscribeUserId());
		selectUserName = userAccount.getName();
		String userOrg = userAccount.getUid();
		if (CollectionUtils.isNotEmpty(subscribeUserIds) && subscribeUserIds.size() > 1) {
			List<UserAccount> userAccountList = userAccountRepository.getByIds(subscribeUserIds.stream().collect(Collectors.toSet()));
			selectUserName = userAccountList.stream().map(UserAccount::getName).collect(Collectors.joining(","));
			// 多选用户的情况下 取最高层级用户
			Optional<UserAccount> highest =
					userAccountList.stream()
							.min(Comparator.comparing(
									UserAccount::getOrgLevel,
									Comparator.nullsLast(Integer::compareTo)
							));
			if (highest.isPresent()) {
				userOrg = highest.get().getUid();
			}
		}
		orgTree = createSiteOrgTree(userOrg);
//		System.out.println(selectedOrg);
//		selectedOrg=null;
	}

	public void onItemSelected() {
		isSelectedCurrentPage = !delMsgList.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
	}
	public void initAssets() {
//		sourceAssets = new ArrayList<>();
//		targetAssets.forEach((item) -> {
//			if (sourceAssets.contains(item)) {
//				sourceAssets.remove(item);
//			}
//		});
//		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
		selectAssetInfos=new ArrayList<>();
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
				selectedMap.get(item.getId())).collect(toList());

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
//		targetAssets = new ArrayList<AssetInfo>();
//		sourceAssets =new ArrayList<AssetInfo>();
//		availableAssets =new DualListModel<AssetInfo>();
		selectAssetInfos=new ArrayList<>();
		level0=true;
		level1=true;
		level2=true;
		isReceiveTimeoutMsg=true;
		assetList = assetInfoRepository.getUserListByAsset(selected.getSubscribeUserId());
		selectUserName = null;
		subscribeUserIds = new ArrayList<>();
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

		boolean hasRepairEventFilter = (repairEventFilter != null && !repairEventFilter.isEmpty());

		if(hasRepairEventFilter) {
			repairEventFilter.forEach(v -> {
				switch (v) {
					case "1":
						searchFilters.add(new SearchFilter("level0", SearchFilter.Operator.EQ, true, "1*5+"));
						break;
					case "2":
						searchFilters.add(new SearchFilter("level1", SearchFilter.Operator.EQ, true, "1*5+"));
						break;
					case "3":
						searchFilters.add(new SearchFilter("level2", SearchFilter.Operator.EQ, true, "1*5+"));
						break;
					case "4":
						searchFilters.add(new SearchFilter("isReceiveTimeoutMsg", SearchFilter.Operator.EQ, true, "1*5+"));
						break;
				}
			});
		} else {
			this.removeFilterOnField("level0");
			this.removeFilterOnField("level1");
			this.removeFilterOnField("level2");
			this.removeFilterOnField("isReceiveTimeoutMsg");
		}

		if (searchFilters.size() == 0) {
			result = dao.findAll(pageRequest);
        } else {
			result = dao.findBySearchFilter(searchFilters, pageRequest);
        }
        delMsgList = result.getContent();
        return result;
    }
	//如果是新增，只有后台itadmin进来 可以修改。 如果是修改 全灰色
	public Boolean subscribe(){
		if(this.getCRUDActionName().equals(WebUtil.getMessage("Edit"))){
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
		if(this.getCRUDActionName().equals(WebUtil.getMessage("Create"))) {
			if(selectAsset.isEmpty()){
				WebUtil.addErrorMessage(WebUtil.getMessage("EquipmentMustBeSelectedErrMsg"));
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				return;
			}
            List<Boolean> messages = ListUtil.toList(selected.getLevel0(), selected.getLevel1(), selected.getLevel2(), selected.getIsReceiveTimeoutMsg(),
                    selected.getMeterHit(), selected.getExpirationReminderEvent());
            if (!messages.contains(Boolean.TRUE)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("MessageSettingsCannotBeEmpty"));
                RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				return;
			}

			if(selected.getIsReceiveTimeoutMsg()){
				if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false){
					WebUtil.addErrorMessage(WebUtil.getMessage("BeSureToSelectPriority"));
					RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
					return;
				}
			}
			// Create：支持多关注人；同人同设备重复 -> 关注项取合集（OR 合并）
			List<Integer> userIds;
			if (subscribeUserIds != null && !subscribeUserIds.isEmpty()) {
				userIds = subscribeUserIds;
			} else {
				// 兼容：如果页面还是走单选
				userIds = Collections.singletonList(selected.getSubscribeUserId());
			}
			if(CollectionUtils.isEmpty(subscribeUserIds) && selected.getSubscribeUserId() == 0){
				WebUtil.addErrorMessage(WebUtil.getMessage("please select users!"));
				return;
			}
			List<AssetInfo> assets = new ArrayList<>(selectAsset);
			List<Integer> assetIdList = assets.stream().map(AssetInfo::getId).collect(toList());

			List<MessageSubscriber> toCreate = new ArrayList<>();
			List<MessageSubscriber> toUpdate = new ArrayList<>();

			for (Integer userId : userIds) {
				// 查出该 userId 在这些 assetId 上已存在的订阅
				List<MessageSubscriber> existingList = dao.getMsgListByAssetIdAndUserId(assetIdList, userId);
				Map<Integer, MessageSubscriber> existingByAssetId = existingList.stream()
						.collect(Collectors.toMap(MessageSubscriber::getAssetId, x -> x, (a, b) -> a));

				for (AssetInfo assetInfo : assets) {
					MessageSubscriber exist = existingByAssetId.get(assetInfo.getId());
					if (exist != null) {
						// ③ 同一人同一设备：关注项取合集（OR 合并）
						exist.setLevel0(orBool(exist.getLevel0(), selected.getLevel0()));
						exist.setLevel1(orBool(exist.getLevel1(), selected.getLevel1()));
						exist.setLevel2(orBool(exist.getLevel2(), selected.getLevel2()));
						exist.setIsReceiveTimeoutMsg(exist.getIsReceiveTimeoutMsg() || selected.getIsReceiveTimeoutMsg());
						exist.setContractDueHit(orBool(exist.getContractDueHit(), selected.getContractDueHit()));
						exist.setMainRepHit(orBool(exist.getMainRepHit(), selected.getMainRepHit()));
						exist.setMeterHit(orBool(exist.getMeterHit(), selected.getMeterHit()));
						exist.setExpirationReminderEvent(orBool(exist.getExpirationReminderEvent(), selected.getExpirationReminderEvent()));
						// repairHit：只要 level0/1/2 有一个为 true 就算选择了维修事件
						boolean mergedRepair = b(exist.getLevel0()) || b(exist.getLevel1()) || b(exist.getLevel2());
						exist.setRepairHit(mergedRepair);
						exist.setLastModifiedBy(currentUser.getName());
						exist.setLastModifiedDate(new Date());
						toUpdate.add(exist);
					} else {
						MessageSubscriber messageSubscriber = new MessageSubscriber();
						messageSubscriber.setAssetId(assetInfo.getId());
						messageSubscriber.setSubscribeUserId(userId);
						messageSubscriber.setLevel0(selected.getLevel0());
						messageSubscriber.setLevel1(selected.getLevel1());
						messageSubscriber.setLevel2(selected.getLevel2());
						messageSubscriber.setIsReceiveTimeoutMsg(selected.getIsReceiveTimeoutMsg());
						boolean repair = b(selected.getLevel0()) || b(selected.getLevel1()) || b(selected.getLevel2());
						messageSubscriber.setRepairHit(repair);
						messageSubscriber.setContractDueHit(selected.getContractDueHit());
						messageSubscriber.setMainRepHit(selected.getMainRepHit());
						messageSubscriber.setMeterHit(selected.getMeterHit());
						messageSubscriber.setExpirationReminderEvent(selected.getExpirationReminderEvent());
						messageSubscriber.setTenantUID(assetInfo.getTenantUID());
						messageSubscriber.setHospitalUID(assetInfo.getHospitalUID());
						messageSubscriber.setInstitutionUID(assetInfo.getInstitutionUID());
						messageSubscriber.setSiteUID(assetInfo.getSiteUID());
						messageSubscriber.setCreatedBy(currentUser.getName());
						messageSubscriber.setCreatedDate(new Date());
						messageSubscriber.setLastModifiedBy(currentUser.getName());
						messageSubscriber.setLastModifiedDate(new Date());
						toCreate.add(messageSubscriber);
					}
				}
			}
			// 统一落库（Spring Data 的 save(Iterable) 通常支持；你原来 dao.save(list) 也是这么用的）
			if (!toUpdate.isEmpty()) {
				dao.save(toUpdate);
			}
			if (!toCreate.isEmpty()) {
				dao.save(toCreate);
			}

			WebUtil.addSuccessMessage(WebUtil.getMessage("EquipmentSubscriptionSuccessTip"));
			assetName = null;
			subscribeUserIds = new ArrayList<>();
		} else {
			if(selected.getIsReceiveTimeoutMsg()){
				if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false){
					WebUtil.addErrorMessage(WebUtil.getMessage("BeSureToSelectPriority"));
					RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
					return;
				}
			}
			if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false &&selected.getMeterHit()==false && selected.getExpirationReminderEvent()==false){
				WebUtil.addErrorMessage(WebUtil.getMessage("MessageSettingsCannotBeEmpty"));
				RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
				return;
			}
			if(selected.getLevel0()==false &&selected.getLevel1()==false && selected.getLevel2()==false)
				selected.setRepairHit(false);
			else
				selected.setRepairHit(true);
			selected.setLastModifiedBy(currentUser.getName());
			selected.setLastModifiedDate(new Date());
			super.save();
		}
		clearAll();
	}
	public void selectedSetting(){
		List<Integer> collect = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item->item.getKey()).collect(toList());
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

	}

	public boolean requiredCheck(){
		/*必须要选择紧急程度*/
		if(!level0 && !level1 && !level2){
			WebUtil.addErrorMessage(WebUtil.getMessage("BeSureToSelectPriority"));
			RequestContext.getCurrentInstance().addCallbackParam("priority", true);
			return false;
		}
		if (targetAssets.isEmpty() && this.getCRUDActionName().equals(WebUtil.getMessage("Create"))) {
			WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
			RequestContext.getCurrentInstance().addCallbackParam("addAsset", true);
			return false;
		}
		return true;
	}

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

	private UserCommonService userCommonService;

	public List<SearchFilter> getAssetFilter() {
		List<SearchFilter> filter = userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType);
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
		return filter;
	}

	public void addCheckedDevice(){
		for (AssetInfo assetInfo : selectedAssetsed1) {
			boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
			if(!b){
				selectAsset.add(assetInfo);
			}
		}
		allSelected1 = Boolean.FALSE;
		selectedAssetsed1.clear();
		selectedAssetUidTable1.clear();
	}


	public void deleteCheckedDevice(){
		for (AssetInfo assetInfo : selectedAssetsed2) {
			boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
			if(b){
				selectAsset.remove(assetInfo);
			}
		}
		allSelected2 = Boolean.FALSE;
		selectedAssetsed2.clear();
		selectedAssetUidTable2.clear();
	}
	public void clearAll(){
		selectAsset.clear();
		allSelected1 = Boolean.FALSE;
		selectedAssetsed1.clear();
		selectedAssetUidTable1.clear();
		allSelected2 = Boolean.FALSE;
		selectedAssetsed2.clear();
		selectedAssetUidTable2.clear();

	}

	public String repairEventLabelCsv(MessageSubscriber item) {
		if(item == null) {
			return "";
		}
		List<String> labels = new ArrayList<>();
		if(Boolean.TRUE.equals(item.getLevel0())) labels.add(casePriorityLabel("1"));
		if(Boolean.TRUE.equals(item.getLevel1())) labels.add(casePriorityLabel("2"));
		if(Boolean.TRUE.equals(item.getLevel2())) labels.add(casePriorityLabel("3"));
		if(item.getIsReceiveTimeoutMsg()) labels.add(casePriorityLabel("4"));
		return String.join(",", labels);
	}

	/**
	 * 列表页过滤/排序辅助：返回紧急程度 value 的 CSV（例如："1,2,4"）
	 */
	public String repairEventLevelCsv(MessageSubscriber item) {
		if(item == null) {
			return "";
		}
		List<String> levels = new ArrayList<>();
		if(Boolean.TRUE.equals(item.getLevel0())) levels.add("1");
		if(Boolean.TRUE.equals(item.getLevel1())) levels.add("2");
		if(Boolean.TRUE.equals(item.getLevel2())) levels.add("3");
		if(item.getIsReceiveTimeoutMsg()) levels.add("4");
		return String.join(",", levels);
	}

	private String casePriorityLabel(String code) {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			if(fc == null) {
				return code;
			}
			String expr = "#{fieldMsg.fieldValue('casePriority','" + code + "')}";
			Object v = fc.getApplication().evaluateExpressionGet(fc, expr, Object.class);
			return v == null ? code : String.valueOf(v);
		} catch (Exception e) {
			return code;
		}
	}

	public void onSubscribeUsersChange() {
		if (subscribeUserIds != null && !subscribeUserIds.isEmpty()) {
			this.selected.setSubscribeUserId(subscribeUserIds.get(0));
			changeSubId();
		} else {
			selectUserName = null;
			this.selected.setSubscribeUserId(0);
		}
	}

	private boolean b(Boolean v) {
		return Boolean.TRUE.equals(v);
	}
	private Boolean orBool(Boolean a, Boolean b) {
		return (Boolean.TRUE.equals(a) || Boolean.TRUE.equals(b));
	}

	public void applyBatchUpdate() {
		if (selectedMap == null || selectedMap.isEmpty()) {
			WebUtil.addErrorMessage("请勾选记录.");
			return;
		}
		List<MessageSubscriber> subscribers = dao.findByIds(selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toSet()));
		if(CollectionUtils.isNotEmpty(subscribers)) {
			subscribers.forEach(v -> {
				v.setLevel0(batch.getLevel0());
				v.setLevel1(batch.getLevel1());
				v.setLevel2(batch.getLevel2());
				v.setIsReceiveTimeoutMsg(batch.getIsReceiveTimeoutMsg());
			});
		}
		dao.save(subscribers);
		WebUtil.addSuccessMessage("批量修改成功.");
	}

	public boolean checkBatchEdit() {
		if (selectedMap.isEmpty()) {
			return true;
		}
		Set<Integer> collect = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toSet());
		if (collect.isEmpty() || collect.size() == 1) {
			return true;
		}
		AtomicBoolean disable = new AtomicBoolean(false);
		List<MessageSubscriber> list = dao.findByIds(collect);
		MessageSubscriber first = list.get(0);
		list.forEach(v -> {
			if (!Objects.equals(first.getLevel0(), v.getLevel0())
					|| !Objects.equals(first.getLevel1(), v.getLevel1())
					|| !Objects.equals(first.getLevel2(), v.getLevel2())
					|| !Objects.equals(first.getIsReceiveTimeoutMsg(), v.getIsReceiveTimeoutMsg())) {
				disable.set(true);
			}
		});
		return disable.get();
	}

	public void addAssetsOfCurrentPage1(AjaxBehaviorEvent event) {
		if(allSelected1) {
			selectAssetInfos.stream().forEach(t -> {
				selectedAssetUidTable1.put(t.getUid(), Boolean.TRUE);
				selectedAssetsed1.add(t);
			});
		} else {
			selectAssetInfos.stream().forEach(t -> {
				selectedAssetUidTable1.put(t.getUid(), Boolean.FALSE);
				selectedAssetsed1.remove(t);
			});
		}
	}
	public void addAssetsOfCurrentPage2(AjaxBehaviorEvent event) {
		if(allSelected2) {
			selectAsset.stream().forEach(t -> {
				selectedAssetUidTable2.put(t.getUid(), Boolean.TRUE);
				selectedAssetsed2.add(t);
			});
		} else {
			selectAsset.stream().forEach(t -> {
				selectedAssetUidTable2.put(t.getUid(), Boolean.FALSE);
				selectedAssetsed2.remove(t);
			});
		}
	}


	public void updateSelctedAssetList1(AjaxBehaviorEvent event) {
		SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
		AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
		if(box.isSelected()) {
			selectedAssetsed1.add(asset);
		} else {
			selectedAssetsed1.remove(asset);
		}
		allSelected1=selectAssetInfos.stream().allMatch(item-> Optional.ofNullable(selectedAssetUidTable1.get(item.getUid())).orElse(Boolean.FALSE));
	}
	public void updateSelctedAssetList2(AjaxBehaviorEvent event) {
		SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
		AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
		if(box.isSelected()) {
			selectedAssetsed2.add(asset);
		} else {
			selectedAssetsed2.remove(asset);
		}
		allSelected2=selectAssetInfos.stream().allMatch(item->Optional.ofNullable(selectedAssetUidTable2.get(item.getUid())).orElse(Boolean.FALSE));
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