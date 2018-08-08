package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MaintainDataPair;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigCheckItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.dao.SearchFilter.Operator;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MaintainConfigController extends GenericCRUDUUIDController<NurseSiteMaintainConfig> implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainConfigController.class);
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private NurseSiteMaintainConfigRepository dao;
    private List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig;
    private List<MaintainDataPair> listStrNurseSiteMaintainConfig;
    //config
  	private NurseSiteMaintainConfigCheckItem objPairCheckItem;
  	private List<AssetInfo> listCheckAssets;
  	private List<OrgInfo> listOrgInfo;
  	private List<String> listCheckDate;
  	private List<String> listWeekDay;
  	private List<String> listMonthDay;
  	private List<NurseSiteMaintainConfigCheckItem> listPairCheckItems;
  	
  	//input
  	private String assetInfoName;
  	private Integer assetCategory;
  	private String functionType;
  	private OrgInfo selectedOrgInfo;
  	
  	//dropdownlist
  	private Integer perDayCount;
  	private List<MaintainDataPair> checkListHours;
  	private List<MaintainDataPair> checkListWeekDays;
  	private List<MaintainDataPair> checkListMonthDays;
  	private List<AssetInfo> checkListAssets;
  	private List<OrgInfo> checkListOrgInfo;
  	private Set<String> setOrgUid;
  	private List<String> listOrgUid;
  	private DualListModel<AssetInfo> availableAssets;
  	private boolean isSaveDisable=true;
  	private List<Integer> listAssetCategory;
  	private boolean isCreate=false;
  	
    @Override
    protected void init() {
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
        dao=nurseSiteMaintainService.getNurseSiteMaintainConfigRepository();
        setOrgUid = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
        if(setOrgUid!=null){
        	listOrgUid=new ArrayList<String>();
        	listOrgUid.addAll(setOrgUid);
        }
        
        //test
    }

    @Override
    protected GenericRepositoryUUID<NurseSiteMaintainConfig> getDAO() {
        return dao;
    }

    @Override
    protected Page<NurseSiteMaintainConfig> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        //need list or set
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, listOrgUid, "1*"));
        Page<NurseSiteMaintainConfig> pageResult=dao.findBySearchFilter(searchFilters, pageRequest);
        return pageResult;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(NurseSiteMaintainConfig object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void save() {
    	saveMaintainConfig(this.selected,true);
        this.selected=null;
        WebUtil.addSuccessMessage("保存成功");
    }
    
    public void update(){
    	saveMaintainConfig(this.selected,false);
        this.selected=null;
        WebUtil.addSuccessMessage("保存成功");
    }
    
    @Override
    public void delete() {
    	// TODO Auto-generated method stub
    	nurseSiteMaintainService.deleteMaintainConfig(this.selected);
    	this.selected=null;
    	WebUtil.addSuccessMessage("删除成功");
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
		isCreate=true;
		initValues();
		loadMaintainConfig(this.selected);
	}
	
	@Override
	public void prepareEdit() {
		// TODO Auto-generated method stub
		super.prepareEdit();
		isCreate=false;
		initValues();
		loadMaintainConfig(this.selected);
	}
	private void initValues(){
		this.listCheckAssets=new ArrayList<AssetInfo>();
		this.listCheckDate=new ArrayList<String>();
		this.listMonthDay=new ArrayList<String>();
		this.listWeekDay=new ArrayList<String>();
		listPairCheckItems=null;
		isSaveDisable=true;
		initObjPairCheckItem();
		if(listOrgInfo==null){
			listOrgInfo=new ArrayList<OrgInfo>();
		}
		else{
			listOrgInfo.clear();
		}
		if(checkListAssets==null){
			checkListAssets=new ArrayList<AssetInfo>();
		}
		else{
			checkListAssets.clear();
		}
		this.assetInfoName="";
		this.assetCategory=null;
		this.functionType="";
		this.selectedOrgInfo=null;
	}
	private void initObjPairCheckItem(){
		objPairCheckItem=new NurseSiteMaintainConfigCheckItem();
		objPairCheckItem.setKey(new Date().getTime()+"");
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
				checkListAssets=new ArrayList<AssetInfo>();
			}
			if(listCheckAssets==null){
				listCheckAssets=new ArrayList<AssetInfo>();
			}
			availableAssets=new DualListModel<AssetInfo>(checkListAssets, listCheckAssets);
		}
		if(isCreate){
			checkListOrgInfo=nurseSiteMaintainService.getListClinicalByUid(setOrgUid);
		}
		else{
			if(checkListOrgInfo==null){
				checkListOrgInfo=new ArrayList<OrgInfo>();
			}
			else{
				checkListOrgInfo.clear();
			}
			checkListOrgInfo.add(nurseSiteMaintainService.findOrgInfoByUid(nurseSiteMaintainConfig.getClinicalDeptUID()));
		}
	}
	
	public boolean checkListCheckAssets(){
		boolean result=false;
		if(listCheckAssets!=null && listCheckAssets.size()>0){
			result=true;
		}
		return result;
	}
	
	public void onFilterChange(){
		boolean search=false;
		List<SearchFilter> listSearchFilter=new ArrayList<SearchFilter>();
		if(StringUtils.isNotEmpty(assetInfoName)){
			SearchFilter tempFilter=new SearchFilter("name",SearchFilter.Operator.LIKE,assetInfoName,"1*");
			listSearchFilter.add(tempFilter);
			search=true;
		}
		if(assetCategory!=null){
			SearchFilter tempFilter=new SearchFilter("assetCategory",SearchFilter.Operator.EQ,assetCategory,"1*");
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
		if(isCreate){
			SearchFilter tempFilter=new SearchFilter("clinicalDeptUID",SearchFilter.Operator.IN,listOrgUid,"1*");
			listSearchFilter.add(tempFilter);
		}
		else{
			SearchFilter tempFilter=new SearchFilter("clinicalDeptUID",SearchFilter.Operator.EQ,this.selected.getClinicalDeptUID(),"1*");
			listSearchFilter.add(tempFilter);
		}
		if(search){
			checkListAssets=nurseSiteMaintainService.getListAssetInfoBySearchFilter(listSearchFilter);
		}
		else{
			checkListAssets=new ArrayList<AssetInfo>();
		}
		if(checkListAssets!=null && listCheckAssets!=null){
			checkListAssets.removeAll(listCheckAssets);
		}
		if(checkListAssets==null){
			checkListAssets=new ArrayList<AssetInfo>();
		}
		if(listCheckAssets==null){
			listCheckAssets=new ArrayList<AssetInfo>();
		}
		availableAssets=new DualListModel<AssetInfo>(checkListAssets, listCheckAssets);
	}
	
	public boolean saveMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig,boolean isSave) {
		Date currentDate=new Date();
		if(nurseSiteMaintainConfig.getId()==null){
			nurseSiteMaintainConfig.setTenantUID(currentUser.getTenantUID());
			nurseSiteMaintainConfig.setInstitutionUID(currentUser.getInstitutionUID());
			nurseSiteMaintainConfig.setHospitalUID(currentUser.getHospitalUID());
			nurseSiteMaintainConfig.setSiteUID(currentUser.getSiteUID());
			nurseSiteMaintainConfig.setCreateBy(currentUser);
			nurseSiteMaintainConfig.setCreateByUid(currentUser.getUid());
			nurseSiteMaintainConfig.setCreateDate(currentDate);
			nurseSiteMaintainConfig.setModifyBy(currentUser);
			nurseSiteMaintainConfig.setModifyByUid(currentUser.getUid());
			nurseSiteMaintainConfig.setModifyDate(currentDate);
		}
		else{
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
		String tempResult="";
		JSONArray jsonArray=new JSONArray();
		if(listPairCheckItems!=null && listPairCheckItems.size()>0){
			for(int i=0;i<listPairCheckItems.size();++i){
				JSONObject jsonObject=new JSONObject();
				NurseSiteMaintainConfigCheckItem p=listPairCheckItems.get(i);
				if(StringUtils.isNotEmpty(p.getName())){
					tempResult+=p.getName()+",";
					jsonObject=(JSONObject) jsonObject.toJSON(p);
					jsonArray.add(jsonObject);
				}
			}
			nurseSiteMaintainConfig.setCheckItemsDetail(jsonArray.toJSONString());
		}
		nurseSiteMaintainConfig.setCheckItems(tempResult);
		if(isSave){
			nurseSiteMaintainService.createMaintainConfig(nurseSiteMaintainConfig, currentUser,listCheckAssets);
		}
		else{
			nurseSiteMaintainService.updateMaintainConfig(nurseSiteMaintainConfig,currentUser, listCheckAssets);
		}
		return true;
	}
	
	public void onAssetsTransfer(TransferEvent event) {
		for (Object item : event.getItems()) {
			if (event.isAdd()) {
				listCheckAssets.add((AssetInfo) item);
			} else {
				listCheckAssets.remove((AssetInfo) item);
			}
		}
		if(listCheckAssets.size()==0){
			isSaveDisable=true;
		}
		else{
			isSaveDisable=false;
		}
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
	
	public void addCheckListItem(){
		listPairCheckItems.add(listPairCheckItems.size()-1,objPairCheckItem);
		initObjPairCheckItem();
		if(listPairCheckItems.size()>1){
			isSaveDisable=false;
		}
		else{
			isSaveDisable=true;
		}
	}
	public void deleteItemDetail(String str){
		for(int i=0;i<listPairCheckItems.size();++i){
			NurseSiteMaintainConfigCheckItem p=listPairCheckItems.get(i);
			if(p.getName()!=null &&p.getName().equals(str)){
				listPairCheckItems.remove(i);
				--i;
			}
		}
		if(listPairCheckItems.size()>1){
			isSaveDisable=false;
		}
		else{
			isSaveDisable=true;
		}
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
		if(objPairCheckItem==null){
			initObjPairCheckItem();
		}
		return objPairCheckItem;
	}

	public void setObjCheckItem(NurseSiteMaintainConfigCheckItem objCheckItem) {
		this.objPairCheckItem = objCheckItem;
	}

	public List<NurseSiteMaintainConfigCheckItem> getListPairCheckItems() {
		String strTemp=this.selected.getCheckItemsDetail();
		if(listPairCheckItems==null){
			List<NurseSiteMaintainConfigCheckItem> list=new ArrayList<NurseSiteMaintainConfigCheckItem>();
			if(StringUtils.isNotEmpty(strTemp)){
				JSONArray jsonArray=JSONObject.parseArray(strTemp);
				for(int i=0;i<jsonArray.size();++i){
					NurseSiteMaintainConfigCheckItem p=(NurseSiteMaintainConfigCheckItem) JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainConfigCheckItem.class);
					list.add(p);
				}
			}
			NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
			p.setName("");
			list.add(p);
			listPairCheckItems=list;
			if(listPairCheckItems.size()>1){
				isSaveDisable=false;
			}
		}
		return listPairCheckItems;
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

	public Integer getAssetCategory() {
		return assetCategory;
	}

	public void setAssetCategory(Integer assetCategory) {
		this.assetCategory = assetCategory;
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

	public List<Integer> getListAssetCategory() {
		return listAssetCategory;
	}

	public void setListAssetCategory(List<Integer> listAssetCategory) {
		this.listAssetCategory = listAssetCategory;
	}
	
}
