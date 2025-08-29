package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.NurseSiteMaintainConfigMapper;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.CustomRoleAuthConstant;
import com.ge.apm.domain.MaintainDataPair;
import com.ge.apm.domain.NurseSiteMaintainCheckItem;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigCheckItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAuth;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class MaintainConfigController extends GenericCRUDUUIDController<NurseSiteMaintainConfig> implements Serializable, ServerEventInterface{
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainConfigController.class);
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private NurseSiteMaintainConfigRepository dao;
    private NurseSiteMaintainConfigMapper configMapper;
    private UserAccountRepository userAccountRepository;
    private List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig;
    private List<MaintainDataPair> listStrNurseSiteMaintainConfig;
    private List<NurseSiteMaintainConfig> listSelected;
    //config
  	private NurseSiteMaintainConfigCheckItem objPairCheckItem;
  	private NurseSiteMaintainConfigCheckItem objPairCheckItemEdit=new NurseSiteMaintainConfigCheckItem();
  	private List<AssetInfo> listCheckAssets;
  	private List<OrgInfo> listOrgInfo;
  	private List<String> listCheckDate;
  	private List<String> listWeekDay;
  	private List<String> listMonthDay;
  	private List<NurseSiteMaintainCheckItem> listWholeCheckItems;
  	private List<NurseSiteMaintainCheckItem> listWholeCheckItemsAll;
  	private List<NurseSiteMaintainCheckItem> listSelectedCheckItems;
  	private List<NurseSiteMaintainConfigCheckItem> listPairCheckItems;
  	private TableOption tableOption;
  	//input
  	private String assetInfoName;
  	private Integer assetGroup;
  	private String functionType;
  	private OrgInfo selectedOrgInfo;
  	private OrgInfo selectedParentOrgInfo;
  	
  	//dropdownlist
  	private Integer perDayCount;
  	private List<MaintainDataPair> checkListHours;
  	private List<MaintainDataPair> checkListWeekDays;
  	private List<MaintainDataPair> checkListMonthDays;
  	private List<AssetInfo> checkListAssets;
  	private List<OrgInfo> checkListOrgInfo;
  	private List<OrgInfo> checkListParentOrgInfo;
  	private List<String> setOrgUid;
  	private List<String> listOrgUid;
  	private DualListModel<AssetInfo> availableAssets;
  	private boolean isSaveDisable=true;
  	private List<Integer> listAssetGroup;
  	private boolean isCreate=false;
  	private SearchController searchController;
  	private AssetSelectController assetSelectController;
  	private UserAuth customRoleAuth;
	private boolean internalCreate=false;
	private boolean createOption=false;
	//选定的院区
	@Setter
	@Getter
	private OrgInfo selectSite;

	private NurseSiteMaintainCheckItemRepository nurseSiteMaintainConfigItemRepository;
	private boolean disabled;

	private NurseSiteMaintainConfigCheckItem editCheckItem;

	private Set<String> groupNames;

	private SelectCheckItemController selectCheckItemController;
    @Override
    protected void init() {
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        searchController = WebUtil.getBean(SearchController.class);
        searchController.setCallingController(this);
        assetSelectController = WebUtil.getBean(AssetSelectController.class);
		assetSelectController.setCallingController(this);
		selectCheckItemController = WebUtil.getBean(SelectCheckItemController.class);
		selectCheckItemController.setCallingController(this);
        currentUser = UserContextService.getCurrentUserAccount();
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		configMapper = WebUtil.getBean(NurseSiteMaintainConfigMapper.class);
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		nurseSiteMaintainConfigItemRepository = WebUtil.getBean(NurseSiteMaintainCheckItemRepository.class);
        nurseSiteMaintainService.init();
        dao=nurseSiteMaintainService.getNurseSiteMaintainConfigRepository();
        setOrgUid = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
        if(setOrgUid!=null){
        	listOrgUid=new ArrayList<String>();
        	listOrgUid.addAll(setOrgUid);
        }
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_MAINTAIN_CONFIG);
        
        //test
    }

    @Override
    protected GenericRepositoryUUID<NurseSiteMaintainConfig> getDAO(){
        return dao;
    }

    @Override
    protected Page<NurseSiteMaintainConfig> loadData(PageRequest pageRequest) {
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return new PageImpl<>(ListUtil.empty());
		}
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
		Wrapper<NurseSiteMaintainConfig> wrapper = new EntityWrapper<>();
		Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
		Sort sort = pageRequest.getSort();
		if (ObjectUtil.isNotEmpty(sort)) {
			for (Sort.Order order : sort) {
				Sort.Direction direction = order.getDirection();
				String property = ZipUtil.camelToUnderline(order.getProperty());
				wrapper.orderBy("nsmc." + property , "ASC".equals(direction.name()));
			}
		}
		if (currentUser.isEnableAssetTypeGroup()) {
			wrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
		}
		if(selectSite!=null) {
			wrapper.andNew().eq("nsmc.site_uid", selectSite.getUid());
			wrapper.or().eq("nsmc.hospital_uid", selectSite.getUid());
			wrapper.or().eq("nsmc.institution_uid", selectSite.getUid());
			wrapper.or().eq("nsmc.tenant_uid", selectSite.getUid());
			wrapper.andNew();
		}
//		wrapper.in("nsmc.clinical_dept_uid", listOrgUid);
		wrapper.eq("uo.user_uid", currentUser.getUid());
		wrapper.eq("nsmc.tenant_uid", currentUser.getTenantUID());
		WrapperSearchFilterUtils.convert(searchFilters, wrapper, "nsmc");
		wrapper.groupBy("nsmc.id");
		List<NurseSiteMaintainConfig> list;
		if (currentUser.isEnableAssetTypeGroup()) {
			list = configMapper.findJoinAssetList(pagination, wrapper);
		} else {
			list = configMapper.findList(pagination, wrapper);
		}
		List<String> createBy = list.stream().map(NurseSiteMaintainConfig::getCreateByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
		List<String> modifyBy = list.stream().map(NurseSiteMaintainConfig::getModifyByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
		modifyBy.addAll(createBy);
		if (ObjectUtil.isNotEmpty(modifyBy)) {
			List<UserAccount> userAccounts = userAccountRepository.getByUids(new HashSet<>(modifyBy));
			Map<String, UserAccount> userAccountMap = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, t -> t));
			for (NurseSiteMaintainConfig maintainConfig : list) {
				maintainConfig.setCreateBy(userAccountMap.get(maintainConfig.getCreateByUid()));
				maintainConfig.setModifyBy(userAccountMap.get(maintainConfig.getModifyByUid()));
			}
		}
		return new PageImpl<>(list, pageRequest, pagination.getTotal());
    }

    public List<NurseSiteMaintainConfig> getConfigList(String clinicalDeptUid) {
    	if(clinicalDeptUid==null) return new ArrayList<>();
		Page<NurseSiteMaintainConfig> result = loadData(new PageRequest(0,1000));
		return result.getContent().stream().filter(
				nurseSiteMaintainConfig -> nurseSiteMaintainConfig.getClinicalDeptUID().equals(clinicalDeptUid)
		).collect(Collectors.toList());
	}

	public void onSelectSiteTreeNode(NodeSelectEvent event) {
		if(searchFilters!=null) searchFilters.clear();

		if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
			selectSite = (OrgInfo) event.getTreeNode().getData();
		} else {
			selectSite = null;
		}

	}

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
    	if(eventObject!=null){
    		try {
    			if("changeSite".equals(eventName)){
    				loadWholeCheckItem(currentUser.getTenantUID());
					selectCheckItemController.onServerEvent("",assetSelectController.getSelectedOrg());
				}else if ("change".equals(eventName)){
					listCheckAssets=(List<AssetInfo>)eventObject;
				}else{

				}
    			checkSaveDisable();
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error("onServerEvent"+e.getMessage());
			}
    	}
    }

    @Override
    public void save(){

		boolean complete=false;
		if(CollectionUtils.isEmpty(listCheckAssets)){
			WebUtil.addWarnMessage("设备绑定不能为空");

		}else{
			saveMaintainConfig(this.selected,true);
			this.selected=null;
			WebUtil.addSuccessMessage("保存成功");
			complete=true;
		}
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("complete", complete);
    }
    
    public void update(){
		boolean complete=false;
		if(CollectionUtils.isEmpty(listCheckAssets)){
			WebUtil.addWarnMessage("设备绑定不能为空");
		}else{
			saveMaintainConfig(this.selected,false);
			this.selected=null;
			WebUtil.addSuccessMessage("保存成功");
			complete=true;
		}
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("complete", complete);

    }
    
    @Override
    public void delete() {
    	// TODO Auto-generated method stub
  
    	if(CollectionUtils.isEmpty(this.listSelected)){
			WebUtil.addErrorMessage("请至少选择一条数据！");
			return;
		}
		nurseSiteMaintainService.deleteMaintainConfig(listSelected,currentUser);
		WebUtil.addSuccessMessage("操作完毕！");
    	if(this.listSelected!=null){
    		this.listSelected.clear();
    	}
    	this.selected=null;
    }

	
	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "日常维护设置"+" "+WebUtil.getMessage(actionName);
	}
	
	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
		searchController.setType(4);
		searchController.setUpdateViewIDs(":maintainConfigCreateForm:saveButtonId");
		searchController.setBlnAssetUpdate(true);
		searchController.updateData();
		isCreate=true;
		initValues();
		loadMaintainConfig(this.selected);
		//TODO
        assetSelectController.loadData4Create();
        loadWholeCheckItem(currentUser.getTenantUID());
	}
	
	@Override
	public void prepareEdit() {
		// TODO Auto-generated method stub
		super.prepareEdit();
		isCreate=false;
		initValues();
		loadMaintainConfig(this.selected);
		setDefaultValueForAssetList();
		//TODO
		this.objPairCheckItem = null;
        assetSelectController.loadData4Edit(this.selected);
        loadWholeCheckItem(selected.getTenantUID());
		//loadSelectedCheckItem();
	}
	
	private void initValues(){
		this.listCheckAssets=new LinkedList<AssetInfo>();
		this.listCheckDate=new ArrayList<String>();
		this.listMonthDay=new ArrayList<String>();
		this.listWeekDay=new ArrayList<String>();
		listPairCheckItems=null;
		isSaveDisable=true;
//		initObjPairCheckItem();
		if(listOrgInfo==null){
			listOrgInfo=new ArrayList<OrgInfo>();
		}
		else{
			listOrgInfo.clear();
		}
		if(checkListAssets==null){
			checkListAssets=new LinkedList<AssetInfo>();
		}
		else{
			checkListAssets.clear();
		}
		this.assetInfoName="";
		this.assetGroup=null;
		this.functionType="";
		this.selectedOrgInfo=null;
		this.selectedParentOrgInfo=null;
	}
	private void initObjPairCheckItem(){
		objPairCheckItem=new NurseSiteMaintainConfigCheckItem();
		objPairCheckItem.setKey(new Date().getTime());
	}
	private void loadParentOrgInfo(){
		checkListParentOrgInfo=nurseSiteMaintainService.getListParentSiteByUid(setOrgUid);
		if(checkListOrgInfo==null){
			checkListOrgInfo=new ArrayList<OrgInfo>();
		}
		else{
			checkListOrgInfo.clear();
		}
		selectedOrgInfo=null;
	}
	private void loadMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig) {
		if(nurseSiteMaintainConfig!=null){
			Integer type=nurseSiteMaintainConfig.getType();
			String typeValue=nurseSiteMaintainConfig.getTypeValue();
			if(type!=null && StringUtils.isNotEmpty(typeValue)){
				List<String> listTemp=Arrays.asList(typeValue.split(","));
				if(type==1){
//					listCheckDate=listTemp;
					perDayCount=Integer.parseInt(typeValue);
				}
				else if(type==2){
					listWeekDay=listTemp;
				}
				else if(type==3){
					listMonthDay=listTemp;
				}
			}
			if(StringUtils.isNotEmpty(this.selected.getClinicalDeptUID())){
				OrgInfo orgInfo=nurseSiteMaintainService.findOrgInfoByUid(this.selected.getClinicalDeptUID());
				if(orgInfo!=null){
					listOrgInfo.add(orgInfo);
				}
			}
			String parentId=nurseSiteMaintainConfig.getId();
			if(StringUtils.isNotEmpty(parentId)){
				listCheckAssets=nurseSiteMaintainService.getListAssetInfoByMaintainConfigParentId(parentId);
			}
			if(checkListAssets==null){
				checkListAssets=new LinkedList<AssetInfo>();
			}
			if(listCheckAssets==null){
				listCheckAssets=new LinkedList<AssetInfo>();
			}
			availableAssets=new DualListModel<AssetInfo>(checkListAssets, listCheckAssets);
		}
		if(isCreate){
			loadParentOrgInfo();
		}
	}
	private void loadWholeCheckItem(String tenantUID){
    	if(CollectionUtils.isEmpty(listWholeCheckItemsAll)){
			listWholeCheckItemsAll = nurseSiteMaintainService.loadWholeCheckItem(tenantUID);
		}
		OrgInfo selectedOrg = assetSelectController.getSelectedOrg();
		this.listWholeCheckItems = listWholeCheckItemsAll.stream().filter(item->{
			String siteUID = item.getSiteUID();
			return siteUID.equals(selectedOrg.getSiteUID())
					|| siteUID.equals(selectedOrg.getHospitalUID())
					|| siteUID.equals(selectedOrg.getInstitutionUID())
					|| siteUID.equals(selectedOrg.getTenantUID());
		}).collect(Collectors.toList());
	}
	private void loadSelectedCheckItem(){
		if(listWholeCheckItems==null){
			listWholeCheckItems=new ArrayList<NurseSiteMaintainCheckItem>();
		}
		if(listSelectedCheckItems==null){
			listSelectedCheckItems=new ArrayList<NurseSiteMaintainCheckItem>();
		}
		else{
			listSelectedCheckItems.clear();
		}
		nurseSiteMaintainService.loadSelectedCheckItem(listWholeCheckItems, listSelectedCheckItems, this.selected);
	}
	
	public boolean checkListCheckAssets(){
		boolean result=false;
		if(listCheckAssets!=null && listCheckAssets.size()>0){
			result=true;
		}
		return result;
	}
	
	public void onParentOrgInfoChange(){
		if(selectedParentOrgInfo!=null){
			checkListOrgInfo=nurseSiteMaintainService.getListClinicalByUidAndParent(setOrgUid, selectedParentOrgInfo.getUid());
		}
		else{
			if(checkListOrgInfo==null){
				checkListOrgInfo=new ArrayList<OrgInfo>();
			}
			else{
				checkListOrgInfo.clear();
			}
		}
	}
	
	private void setDefaultValueForAssetList(){
		onFilterChange();
	}
	
	public void onFilterChange(){
		boolean search=false;
		List<SearchFilter> listSearchFilter=new ArrayList<SearchFilter>();
		listSearchFilter.add(new SearchFilter("status",SearchFilter.Operator.NE,"4","1*"));
		listSearchFilter.add(new SearchFilter("isValid",SearchFilter.Operator.EQ,true,"1*"));
		listSearchFilter.add(new SearchFilter("isDeleted",SearchFilter.Operator.EQ,false,"1*"));
		if(StringUtils.isNotEmpty(assetInfoName)){
			SearchFilter tempFilter=new SearchFilter("name",SearchFilter.Operator.LIKE,assetInfoName,"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
		if(assetGroup!=null){
			SearchFilter tempFilter=new SearchFilter("assetGroup",SearchFilter.Operator.EQ,assetGroup,"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
		if(StringUtils.isNotEmpty(functionType)){
			SearchFilter tempFilter=new SearchFilter("functionType",SearchFilter.Operator.LIKE,functionType,"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
		if(selectedOrgInfo!=null){
			SearchFilter tempFilter=new SearchFilter("clinicalDeptUID",SearchFilter.Operator.EQ,selectedOrgInfo.getUid(),"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
//		if(isCreate){
//			SearchFilter tempFilter=new SearchFilter("clinicalDeptUID",SearchFilter.Operator.IN,listOrgUid,"1*");
//			listSearchFilter.add(tempFilter);
//		}
		else{
			SearchFilter tempFilter=new SearchFilter("clinicalDeptUID",SearchFilter.Operator.EQ,this.selected.getClinicalDeptUID(),"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
		if(search){
			Sort sort=new Sort(Direction.ASC,"namePyCode");
			checkListAssets=nurseSiteMaintainService.getListAssetInfoBySearchFilter(listSearchFilter,sort);
		}
		else{
			checkListAssets=new LinkedList<AssetInfo>();
		}
		if(checkListAssets!=null && listCheckAssets!=null){
			checkListAssets.removeAll(listCheckAssets);
		}
		if(checkListAssets==null){
			checkListAssets=new LinkedList<AssetInfo>();
		}
		if(listCheckAssets==null){
			listCheckAssets=new LinkedList<AssetInfo>();
		}
		availableAssets=new DualListModel<AssetInfo>(checkListAssets, listCheckAssets);
	}
	
	public boolean saveMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig,boolean isSave) {
		Date currentDate=new Date();
		if(nurseSiteMaintainConfig.getId()==null){
//			nurseSiteMaintainConfig.setTenantUID(currentUser.getTenantUID());
//			nurseSiteMaintainConfig.setInstitutionUID(currentUser.getInstitutionUID());
//			nurseSiteMaintainConfig.setHospitalUID(currentUser.getHospitalUID());
//			nurseSiteMaintainConfig.setSiteUID(currentUser.getSiteUID());
			nurseSiteMaintainConfig.setCreateBy(currentUser);
			nurseSiteMaintainConfig.setCreateByUid(currentUser.getUid());
			nurseSiteMaintainConfig.setCreateDate(currentDate);
			nurseSiteMaintainConfig.setModifyBy(currentUser);
			nurseSiteMaintainConfig.setModifyByUid(currentUser.getUid());
			nurseSiteMaintainConfig.setModifyDate(currentDate);
			nurseSiteMaintainConfig.setDeleted(false);
		} else{
			nurseSiteMaintainConfig.setModifyBy(currentUser);
			nurseSiteMaintainConfig.setModifyByUid(currentUser.getUid());
			nurseSiteMaintainConfig.setModifyDate(currentDate);
		}
		//1:day,2:week,3:month,4:year
		Integer type=nurseSiteMaintainConfig.getType();
		String typeValue="";
		if(type!=null){
			if(type==1){
//				if(listCheckDate!=null){
//					for(int i=0;i<listCheckDate.size();++i){
//						typeValue+=listCheckDate.get(i)+",";
//					}
//				}
				if(perDayCount!=null){
					typeValue=perDayCount+"";
				}
				else{
					typeValue="";
				}
			}
			else if(type==2){
				if(listWeekDay!=null){
					for(int i=0;i<listWeekDay.size();++i){
						typeValue+=listWeekDay.get(i)+",";
					}
				}
			}
			else if(type==3){
				if(listMonthDay!=null){
					for(int i=0;i<listMonthDay.size();++i){
						typeValue+=listMonthDay.get(i)+",";
					}
				}
			}
		}
		nurseSiteMaintainConfig.setTypeValue(typeValue);
		JSONArray jsonArray=new JSONArray();
		if(listPairCheckItems!=null && listPairCheckItems.size()>0){
			for(int i=0;i<listPairCheckItems.size();++i){
				JSONObject jsonObject=new JSONObject();
				NurseSiteMaintainConfigCheckItem p=listPairCheckItems.get(i);
				if(StringUtils.isNotEmpty(p.getName())){
					jsonObject=(JSONObject) jsonObject.toJSON(p);
					jsonArray.add(jsonObject);
				}
			}
		}
//		nurseSiteMaintainService.saveSelectedCheckItem(listSelectedCheckItems,jsonArray);
		nurseSiteMaintainConfig.setCheckItemsDetail(jsonArray.toJSONString());
		if(isSave){
			nurseSiteMaintainService.createMaintainConfig(nurseSiteMaintainConfig, currentUser,listCheckAssets);
		}
		else{
			nurseSiteMaintainService.updateMaintainConfig(nurseSiteMaintainConfig,currentUser, listCheckAssets);
		}
		return true;
	}
	
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	private void checkSaveDisable(){
		try {
			isSaveDisable=false;
			if(CollectionUtils.isEmpty(listPairCheckItems)){
				isSaveDisable=true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			isSaveDisable=true;
			LOGGER.error("checkSaveDisable"+e.getMessage());
		}
		closeDlg();
	}

	public void applyChange(int type){
		Boolean applyDirectly = this.selected.getApplyDirectly();
		Boolean applyFeatureData = this.selected.getApplyFeatureData();
		if(type == 1 && applyDirectly){
			this.selected.setApplyFeatureData(false);
		}
		if(type == 2 && applyFeatureData){
			this.selected.setApplyDirectly(false);
		}
	}
	
	public void onSelectedCheckItemChange(){
		checkSaveDisable();
	}
	
	public void onAssetsTransfer(TransferEvent event) {
		List<AssetInfo> collection=(List<AssetInfo>) event.getItems();
		if(collection!=null && collection.size()>0){
			if (event.isAdd()) {
				listCheckAssets.addAll(collection);
			} else {
				listCheckAssets.removeAll(collection);
			}
		}
		checkSaveDisable();
	}
	
	public List<MaintainDataPair> getCheckListHours() {
		if(checkListHours==null){
			checkListHours=new ArrayList<MaintainDataPair>();
			for(int i=1;i<=24;++i){
				MaintainDataPair p=new MaintainDataPair(i+"时",i);
				checkListHours.add(p);
			}
		}
		return checkListHours;
	}

	public void setCheckListHours(List<MaintainDataPair> checkListHours) {
		this.checkListHours = checkListHours;
	}
	
	public void saveCheckListItem(){
		if(StringUtils.isBlank(objPairCheckItem.getFieldType())){
			objPairCheckItem.setRequired(Boolean.FALSE);
		}
		if(StringUtils.isBlank(objPairCheckItem.getName())){
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage("名称不能为空");
			return;
		}
    	if(this.internalCreate){
			for (NurseSiteMaintainConfigCheckItem listPairCheckItem : listPairCheckItems) {
				if(listPairCheckItem.getName().equals(objPairCheckItem.getName())){
					WebUtil.addErrorMessage("添加失败，请勿重复添加");
					initObjPairCheckItem();
					checkSaveDisable();
					return;
				}
			}
			if(objPairCheckItem.getKey() == null){
				objPairCheckItem.setKey(new Date().getTime());
			}
			listPairCheckItems.add(objPairCheckItem);

		}else{
			for (NurseSiteMaintainConfigCheckItem listPairCheckItem : listPairCheckItems) {
				String name = listPairCheckItem.getName();
				if(name.equals(editCheckItem.getName())){
					BeanUtils.copyProperties(objPairCheckItem,listPairCheckItem);
					if(listPairCheckItem.getKey() == null){
						listPairCheckItem.setKey(new Date().getTime());
					}
					break;
				}
			}
		}
//		initObjPairCheckItem();
		checkSaveDisable();
	}
	public void saveSelectCheckList(List<NurseSiteMaintainCheckItem> checkItems){
		checkItems.forEach(checkItem->{
			boolean b = listPairCheckItems.stream().anyMatch(item -> item.getName().equals(checkItem.getName()));
			if(!b){
				NurseSiteMaintainConfigCheckItem temp= new NurseSiteMaintainConfigCheckItem();
				temp.setId(checkItem.getId());
				temp.setName(checkItem.getName());
				temp.setGroupName(checkItem.getGroupName());
				temp.setKey(checkItem.getKey());
				temp.setRequired(checkItem.getRequired());
				temp.setDescription(checkItem.getDescription());
				temp.setDefaultValue(checkItem.getDefaultValue());
				temp.setFieldType(checkItem.getFieldType());
				String options = checkItem.getOptions();
				if(StringUtils.isNotEmpty(options)){
					JSONArray objects = JSONArray.parseArray(options);
					List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
					temp.setOptions(tableOptions);
				}
				listPairCheckItems.add(temp);
			}

		});
	}
	public void setSelectAll(){
		selectCheckItemController.setSelectAll(false);
		selectCheckItemController.clear();
	}
	public void closeDlg(){
		objPairCheckItem=null;
	}
	public void deleteItemDetail(){
		if(listPairCheckItems!=null && listPairCheckItems.size()>0){
			listPairCheckItems = listPairCheckItems.stream().filter(it -> !it.getName().equals(objPairCheckItem.getName())).collect(Collectors.toList());
		}
		checkSaveDisable();
	}
	public void onPageSelect(PageEvent event) {
	}

	public List<MaintainDataPair> getCheckListWeekDays() {
		if(checkListWeekDays==null){
			checkListWeekDays=new ArrayList<MaintainDataPair>();
			MaintainDataPair p=new MaintainDataPair("星期日",1);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期一",2);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期二",3);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期三",4);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期四",5);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期五",6);
			checkListWeekDays.add(p);
			p=new MaintainDataPair("星期六",7);
			checkListWeekDays.add(p);
		}
		return checkListWeekDays;
	}

	public void setCheckListWeekDays(List<MaintainDataPair> checkListWeekDays) {
		this.checkListWeekDays = checkListWeekDays;
	}

	public List<MaintainDataPair> getCheckListMonthDays() {
		if(checkListMonthDays==null){
			checkListMonthDays=new ArrayList<MaintainDataPair>();
			for(int i=1;i<=31;++i){
				MaintainDataPair p=new MaintainDataPair(i+"日", i);
				checkListMonthDays.add(p);
			}
		}
		return checkListMonthDays;
	}

	public void setCheckListMonthDays(List<MaintainDataPair> checkListMonthDays) {
		this.checkListMonthDays = checkListMonthDays;
	}

	public List<AssetInfo> getListCheckAssets() {
		return listCheckAssets;
	}

	public void setListCheckAssets(List<AssetInfo> listCheckAssets) {
		this.listCheckAssets = listCheckAssets;
	}
	
	public List<String> getListCheckDate() {
		return listCheckDate;
	}

	public void setListCheckDate(List<String> listCheckDate) {
		this.listCheckDate = listCheckDate;
	}

	public List<String> getListWeekDay() {
		return listWeekDay;
	}

	public void setListWeekDay(List<String> listWeekDay) {
		this.listWeekDay = listWeekDay;
	}

	public List<String> getListMonthDay() {
		return listMonthDay;
	}

	public void setListMonthDay(List<String> listMonthDay) {
		this.listMonthDay = listMonthDay;
	}

	public List<AssetInfo> getCheckListAssets() {
		return checkListAssets;
	}

	public void setCheckListAssets(List<AssetInfo> checkListAssets) {
		this.checkListAssets = checkListAssets;
	}

	public List<NurseSiteMaintainConfig> getListNurseSiteMaintainConfig() {
		return listNurseSiteMaintainConfig;
	}

	public void setListNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig) {
		this.listNurseSiteMaintainConfig = listNurseSiteMaintainConfig;
	}

	public List<MaintainDataPair> getListStrNurseSiteMaintainConfig() {
		return listStrNurseSiteMaintainConfig;
	}

	public void setListStrNurseSiteMaintainConfig(List<MaintainDataPair> listStrNurseSiteMaintainConfig) {
		this.listStrNurseSiteMaintainConfig = listStrNurseSiteMaintainConfig;
	}
	public NurseSiteMaintainConfigCheckItem getObjCheckItem() {

		return objPairCheckItem;
	}
	public void  createObjCheckItem(){
		this.objPairCheckItem=new NurseSiteMaintainConfigCheckItem();
		objPairCheckItem.setKey(new Date().getTime());
		this.internalCreate=true;
		this.disabled=Boolean.FALSE;
	}
	public void setObjCheckItem(NurseSiteMaintainConfigCheckItem objCheckItem) {
		if(objCheckItem != null){
			editCheckItem = null;
			NurseSiteMaintainConfigCheckItem temp=new NurseSiteMaintainConfigCheckItem();
			BeanUtils.copyProperties(objCheckItem,temp);
			editCheckItem = objCheckItem;
			this.objPairCheckItem = temp;
			this.disabled = StringUtils.isNotEmpty(objPairCheckItem.getId());
		}else{
			this.objPairCheckItem = objCheckItem;
		}
		this.internalCreate=false;
	}
	
	private boolean containsCheckItem(List<NurseSiteMaintainCheckItem> list,NurseSiteMaintainConfigCheckItem p){
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				if(list.get(i).getId().equals(p.getId())){
					return true;
				}
			}
		}
		return false;
	}

	public List<NurseSiteMaintainConfigCheckItem> getListPairCheckItems() {
		String strTemp=this.selected.getCheckItemsDetail();
		if(listPairCheckItems==null){
			List<NurseSiteMaintainConfigCheckItem> list=new ArrayList<NurseSiteMaintainConfigCheckItem>();
			if(StringUtils.isNotEmpty(strTemp)){
				JSONArray jsonArray=JSONObject.parseArray(strTemp);
				for(int i=0;i<jsonArray.size();++i){
					NurseSiteMaintainCheckItem p1= JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
					NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(p1,(JSONObject) jsonArray.get(i));
					if (p.getKey()==null) {
						p.setKey(new Date().getTime());
					}
					list.add(p);
//					if(StringUtils.isEmpty(p.getId())){
//						list.add(p);
//					}
//					//to be done
//					else if(!containsCheckItem(listWholeCheckItems,p)){
//						list.add(p);
//					}
				}
			}
//			NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
//			p.setName("");
//			list.add(p);
			listPairCheckItems=list;
			checkSaveDisable();
		}
		return listPairCheckItems;
	}
	private NurseSiteMaintainConfigCheckItem initConfigCheckItem(NurseSiteMaintainCheckItem checkItem,JSONObject jsonObject){
		NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
		String id = checkItem.getId();
		p.setId(id);
		p.setName(checkItem.getName());
		p.setDefaultValue(checkItem.getDefaultValue());
		p.setDescription(checkItem.getDescription());
		p.setFieldType(checkItem.getFieldType());
		String options = checkItem.getOptions();
		p.setRequired(checkItem.getRequired());
		p.setKey(checkItem.getKey());
		if(CollectionUtils.isNotEmpty(listWholeCheckItems) && StringUtils.isNotEmpty(id)){
			Optional<String> first = listWholeCheckItems.stream().filter(item -> id.equals(item.getId()) && StringUtils.isNotEmpty(item.getGroupName())).map(NurseSiteMaintainCheckItem::getGroupName).findFirst();
			if (first.isPresent()) {
				p.setGroupName(first.get());
			}else{
				p.setGroupName(jsonObject.getString("groupName"));
			}
		}else{
			p.setGroupName(jsonObject.getString("groupName"));
		}
		if(StringUtils.isBlank(options) || options.contains("[]")){
			p.setOptions(null);
		}else{
			JSONArray objects = JSONArray.parseArray(options);
			List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
			p.setOptions(tableOptions);
		}
		return p;
	}
	public void setListPairCheckItems(List<NurseSiteMaintainConfigCheckItem> listPairCheckItems) {
		this.listPairCheckItems = listPairCheckItems;
	}

	public Integer getPerDayCount() {
		return perDayCount;
	}

	public void setPerDayCount(Integer perDayCount) {
		this.perDayCount = perDayCount;
	}

	public boolean isSaveDisable() {
		return isSaveDisable;
	}

	public void setSaveDisable(boolean isSaveDisable) {
		this.isSaveDisable = isSaveDisable;
	}

	public DualListModel<AssetInfo> getAvailableAssets() {
		return availableAssets;
	}

	public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
		this.availableAssets = availableAssets;
	}

	public List<OrgInfo> getListOrgInfo() {
		return listOrgInfo;
	}

	public void setListOrgInfo(List<OrgInfo> listOrgInfo) {
		this.listOrgInfo = listOrgInfo;
	}

	public List<OrgInfo> getCheckListOrgInfo() {
		return checkListOrgInfo;
	}

	public void setCheckListOrgInfo(List<OrgInfo> checkListOrgInfo) {
		this.checkListOrgInfo = checkListOrgInfo;
	}

	public String getAssetInfoName() {
		return assetInfoName;
	}

	public void setAssetInfoName(String assetInfoName) {
		this.assetInfoName = assetInfoName;
	}

	public Integer getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}

	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public OrgInfo getSelectedOrgInfo() {
		return selectedOrgInfo;
	}

	public void setSelectedOrgInfo(OrgInfo selectedOrgInfo) {
		this.selectedOrgInfo = selectedOrgInfo;
	}

	public List<Integer> getListAssetGroup() {
		return listAssetGroup;
	}

	public void setListAssetGroup(List<Integer> listAssetGroup) {
		this.listAssetGroup = listAssetGroup;
	}

	public OrgInfo getSelectedParentOrgInfo() {
		return selectedParentOrgInfo;
	}

	public void setSelectedParentOrgInfo(OrgInfo selectedParentOrgInfo) {
		this.selectedParentOrgInfo = selectedParentOrgInfo;
	}

	public List<OrgInfo> getCheckListParentOrgInfo() {
		return checkListParentOrgInfo;
	}

	public void setCheckListParentOrgInfo(List<OrgInfo> checkListParentOrgInfo) {
		this.checkListParentOrgInfo = checkListParentOrgInfo;
	}

	public UserAuth getCustomRoleAuth() {
		return customRoleAuth;
	}

	public void setCustomRoleAuth(UserAuth customRoleAuth) {
		this.customRoleAuth = customRoleAuth;
	}

	public List<NurseSiteMaintainConfig> getListSelected() {
		return listSelected;
	}
	
	public void setListSelected(List<NurseSiteMaintainConfig> listSelected) {
		if (listSelected != null && listSelected.size() == 1) {
			this.selected = listSelected.get(0);
		} else {
			this.selected = null;
		}
		this.listSelected = listSelected;
	}

	public List<NurseSiteMaintainCheckItem> getListWholeCheckItems() {
		return listWholeCheckItems;
	}

	public void setListWholeCheckItems(List<NurseSiteMaintainCheckItem> listWholeCheckItems) {
		this.listWholeCheckItems = listWholeCheckItems;
	}

	public List<NurseSiteMaintainCheckItem> getListSelectedCheckItems() {
		return listSelectedCheckItems;
	}

	public void setListSelectedCheckItems(List<NurseSiteMaintainCheckItem> listSelectedCheckItems) {
		this.listSelectedCheckItems = listSelectedCheckItems;
	}

	public TableOption getTableOption() {
		return tableOption;
	}

	public void setTableOption(TableOption tableOption) {
		this.tableOption = tableOption;
	}
	public void addOption() {
		List<TableOption> options = objPairCheckItem.getOptions();
		if(options==null){
			objPairCheckItem.setOptions(new ArrayList<>());
			options = objPairCheckItem.getOptions();
		}
		if(createOption){
			// 新建
			for (TableOption tableOption1 : options) {
				if(tableOption1.getKey() != null && tableOption1.getKey().equals(tableOption.getKey())){
					WebUtil.addErrorMessage("添加失败，请勿重复添加");
					return;
				}
			}
			options.add(tableOption);
		}else{
			for (TableOption tableOption1 : options) {
				if(tableOption1.getKey() != null && tableOption1.getKey().equals(tableOption.getKey())){
					tableOption1.setValue(tableOption.getValue());
					tableOption=null;
					return ;
				}
			}
		}

		tableOption=null;
	}
	public void prepareAddOption(){
		tableOption=new TableOption();
		this.createOption=true;
	}
	public void prepareEditOption(TableOption option) {
		tableOption = option;
		this.createOption=false;
	}

	public void removeOption(TableOption option) {
		List<TableOption> options = objPairCheckItem.getOptions();
		List<TableOption> collect = options.stream().filter(item -> {
			String key = item.getKey();
			return key != null && !key.equals(option.getKey());
		}).collect(Collectors.toList());
		objPairCheckItem.setOptions(collect);
	}
	public void onRuleChange(){
		objPairCheckItem.setDefaultValue(null);
	}
	public boolean dateType(){
    	if(objPairCheckItem != null){
			return StringUtils.isEmpty(objPairCheckItem.getFieldType()) || "Date".equals(objPairCheckItem.getFieldType());
		}else{
    		return false;
		}
	}
	public boolean emptyType(){
		if(objPairCheckItem != null){
			return StringUtils.isEmpty(objPairCheckItem.getFieldType());
		}else{
			return false;
		}
	}
	public List<String> getGroupNames(){
		Set<String> result = new HashSet<>();
		if(CollectionUtils.isEmpty(groupNames)){
			List<String> groupNameTemp = nurseSiteMaintainConfigItemRepository.findGroupName(currentUser.getSiteUID());
			groupNames=groupNameTemp.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());

		}
		result.addAll(groupNames);
		if(CollectionUtils.isNotEmpty(this.listPairCheckItems)){
			List<String> groupName = listPairCheckItems.stream().map(NurseSiteMaintainConfigCheckItem::getGroupName).collect(Collectors.toList());
			result.addAll(groupName);
		}
		return result.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
	}
	public void onSelectObjCheckItem(){
		Optional<NurseSiteMaintainCheckItem> first = listWholeCheckItems.stream().filter(item -> item.getName().equals(objPairCheckItem.getName())).findFirst();
		if (first.isPresent()) {
			NurseSiteMaintainCheckItem nurseSiteMaintainCheckItem = first.get();
			BeanUtils.copyProperties(nurseSiteMaintainCheckItem,objPairCheckItem);
			disabled = Boolean.TRUE;
		}else{
			NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = new NurseSiteMaintainConfigCheckItem();
			nurseSiteMaintainConfigCheckItem.setName(objPairCheckItem.getName());
			objPairCheckItem = nurseSiteMaintainConfigCheckItem;
			disabled = Boolean.FALSE;
		}
	}
	public void checkItemMoveUp(){
		int i = listPairCheckItems.indexOf(objPairCheckItem);
		if(i != 0){
			int index = i - 1;
			NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = listPairCheckItems.get(index);
			listPairCheckItems.set(index,objPairCheckItem);
			listPairCheckItems.set(i,nurseSiteMaintainConfigCheckItem);
		}
	}
	public void checkItemMoveDown(){
		int i = listPairCheckItems.indexOf(objPairCheckItem);
		if(i != listPairCheckItems.size() - 1){
			int index = i + 1;
			NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = listPairCheckItems.get(index);
			listPairCheckItems.set(index,objPairCheckItem);
			listPairCheckItems.set(i,nurseSiteMaintainConfigCheckItem);
		}
	}
	public void recover(){
    	BeanUtils.copyProperties(objPairCheckItemEdit,objPairCheckItem);
	}

	public boolean getInternalCreate() {
		return internalCreate;
	}

	public void setInternalCreate(boolean internalCreate) {
		this.internalCreate = internalCreate;
	}

	public boolean getCreateOption() {
		return createOption;
	}

	public void setCreateOption(boolean createOption) {
		this.createOption = createOption;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
