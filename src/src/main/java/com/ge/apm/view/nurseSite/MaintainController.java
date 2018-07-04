package com.ge.apm.view.nurseSite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.MaintainDataPair;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;

/**
 * Created by 212605082 on 2018/5/8.
 */
@ManagedBean
@ViewScoped
public class MaintainController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainController.class);
	private NurseSiteMaintainService nurseSiteMaintainService;
	private UserAccount currentUser;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

	private ScheduleModel lazyEventModel;
	private DefaultScheduleEvent event;
	private List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig;
	private NurseSiteMaintainConfig nurseSiteMaintainConfig;
	private List<AssetInfo> listCheckAssets;
	private List<String> listCheckAssetsName;
	//use modify values
	private List<MaintainDataPair> listCheckItems;
	//from config default
	private NurseSiteMaintain nurseSiteMaintain;
	private List<Date> existMaintainDate;
	private List<NurseSiteMaintain> existMaintainData;
	private Date startBatchDate;
  	private Date endBatchDate;
	//validate
	private boolean isDeleteDisable;
    private boolean isSaveDisable;
    private boolean isDeptHead;

	@PostConstruct
	protected void init() {
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		List<String> orgUidList = nurseSiteMaintainService.getOrgListByUser(currentUser.getUid());
		this.loadMaintainData(orgUidList);
		if(UserContextService.getRoles().contains("DeptHead")){
			isDeptHead=true;
		}
		else{
			isDeptHead=false;
		}
		listNurseSiteMaintainConfig=nurseSiteMaintainService.getMaintainConfigBySiteUid(currentUser.getSiteUID());
		if(listNurseSiteMaintainConfig!=null&& listNurseSiteMaintainConfig.size()>0){
			nurseSiteMaintainConfig=listNurseSiteMaintainConfig.get(0);
			System.out.println("init config");
			System.out.println(nurseSiteMaintainConfig.getName());
			loadMaintainConfig();
		}
	}

	private void loadMaintainData(List<String> userOrgList) {
		System.out.println("loadMaintainData");
		lazyEventModel = new LazyScheduleModel() {
			@Override
			public void loadEvents(Date start, Date end) {
				List<SearchFilter> searchFilters = new ArrayList<>();
				searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
				searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
				searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
				searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, userOrgList, "1*2+"));
				List<NurseSiteMaintain> listData = nurseSiteMaintainService.findBySearchFilter(searchFilters);
				for (NurseSiteMaintain data : listData) {
					DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(data), data.getScheduleDate(), data.getScheduleDate(), true);
					tempEvent.setData(data);
					addEvent(tempEvent);
				}
			}
		};
	}

	private MaintainDataPair getContentJsonObj() {
		MaintainDataPair p=new MaintainDataPair(listCheckItems);
		return p;
	}
	private String getShowTitle(NurseSiteMaintain tempNurseSiteMaintain) {
		//0：未检查,1:已检查，2:通过，3有问题
		String strStatus="未检查";
		if(tempNurseSiteMaintain.getStatus()==null || tempNurseSiteMaintain.getStatus()==0){
		}
		else if(tempNurseSiteMaintain.getStatus()==1){
			strStatus="已检查";
		}
		else if(tempNurseSiteMaintain.getStatus()==2){
			strStatus="通过";
		}
		else if(tempNurseSiteMaintain.getStatus()==3){
			strStatus="有问题";
		}
		String str=tempNurseSiteMaintain.getName().replaceAll(",", "-")+"-"+strStatus;
		return str;
	}
	
	private void loadMaintainConfig() {
		if(nurseSiteMaintainConfig!=null){
			String assets=nurseSiteMaintainConfig.getAssets();
			if(StringUtils.isNotEmpty(assets)){
				String arr[]=assets.split(",");
				if(arr!=null && arr.length>0){
					listCheckAssets=nurseSiteMaintainService.getListAssetInfoByUid(Arrays.asList(arr));
					if(listCheckAssets!=null){
						listCheckAssetsName=new ArrayList<String>();
						for(int i=0;i<listCheckAssets.size();++i){
							listCheckAssetsName.add(listCheckAssets.get(i).getName());
						}
					}
				}
			}
			loadConfigCheckItems();
		}
	}
	
	private void loadConfigCheckItems(){
		if(listCheckItems==null){
			listCheckItems = new ArrayList<MaintainDataPair>();
		}
		else{
			listCheckItems.clear();
		}
		String strCheckItems = nurseSiteMaintainConfig.getCheckItems();
		if (StringUtils.isNotEmpty(strCheckItems) && listCheckAssets!=null) {
			String arr2[] = strCheckItems.split(",");
			for (int i = 0; i < listCheckAssets.size(); ++i) {
				List<MaintainDataPair> listTemp=new ArrayList<MaintainDataPair>();
				for (int j = 0; j < arr2.length; ++j) {
					MaintainDataPair p = new MaintainDataPair(arr2[j], false);
					listTemp.add(p);
				}
				MaintainDataPair p2 = new MaintainDataPair();
				p2.setKey(listCheckAssets.get(i).getName());
				p2.setListSimplePairValue(listTemp);
				listCheckItems.add(p2);
			}
		}
	}
	private NurseSiteMaintain createMaintainData(Date date){
		NurseSiteMaintain tempNurseSiteMaintain=new NurseSiteMaintain();
		Date currentDate=new Date();
		tempNurseSiteMaintain.setTenantUID(currentUser.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(currentUser.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(currentUser.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(currentUser.getSiteUID());
		tempNurseSiteMaintain.setCheckDate(currentDate);
		tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
		tempNurseSiteMaintain.setContentJSON(getContentJsonObj().getJsonStr());
		tempNurseSiteMaintain.setCreateBy(currentUser);
		tempNurseSiteMaintain.setCreateByUid(currentUser.getUid());
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyBy(currentUser);
		tempNurseSiteMaintain.setModifyByUid(currentUser.getUid());
		tempNurseSiteMaintain.setModifyDate(currentDate);
		tempNurseSiteMaintain.setScheduleDate(date);
		tempNurseSiteMaintain.setStatus(0);
		String assetNames=listCheckAssetsName.toString();
		tempNurseSiteMaintain.setAssets(assetNames.substring(1, assetNames.length()-1));
		tempNurseSiteMaintain.setCheckItems(nurseSiteMaintainConfig.getCheckItems());
		tempNurseSiteMaintain.setName(nurseSiteMaintainConfig.getName());
		return tempNurseSiteMaintain;
	}
	
	private void updateMaintainData(NurseSiteMaintain obj,boolean batchUpdate){
		
		if(batchUpdate){
			//insert new record
			//0：未检查,1:已检查，2:通过，3有问题
			if(obj.getStatus()==null || obj.getStatus()==0){
				MaintainDataPair p=new MaintainDataPair(obj.getListCheckItems());
				MaintainDataPair.replaceWithDefaultValue(p.getListSimplePairValue(), true);
				p.setStatus(1);
				obj.setContentJSON(p.getJsonStr());
				obj.setStatus(p.getStatus());
				Date currentDate=new Date();
				obj.setCheckDate(currentDate);
				obj.setModifyBy(currentUser);
				obj.setModifyByUid(currentUser.getUid());
				obj.setModifyDate(currentDate);
			}
		}
		else{
			//update exist record
			MaintainDataPair p=getContentJsonObj();
			p.setStatus(1);
			obj.setContentJSON(p.getJsonStr());
			obj.setStatus(p.getStatus());
			Date currentDate=new Date();
			obj.setCheckDate(currentDate);
			obj.setModifyBy(currentUser);
			obj.setModifyByUid(currentUser.getUid());
			obj.setModifyDate(currentDate);
		}
	}
	
	private void mergeMaintainData(NurseSiteMaintain data){
		String itemsStr=nurseSiteMaintainConfig.getCheckItems();
		if(StringUtils.isNotEmpty(itemsStr)){
			String[] itemsArr=itemsStr.split(",");
			String[] assetsArrData=data.getAssets().split(",");
			String[] itemsArrData=data.getCheckItems().split(",");
			List<String> assetsResult=mergeList(listCheckAssets, assetsArrData);
			List<String> itemsResult=mergeList(itemsArr, itemsArrData);
			List<MaintainDataPair> listPairTemp=new ArrayList<MaintainDataPair>();
			for(int i=0;i<assetsResult.size();++i){
				List<MaintainDataPair> innerList=new ArrayList<MaintainDataPair>();
				MaintainDataPair p=new MaintainDataPair(assetsResult.get(i),innerList);
				for(int j=0;j<itemsResult.size();++j){
					MaintainDataPair innerP=new MaintainDataPair(itemsResult.get(j),false);
					innerList.add(innerP);
				}
				listPairTemp.add(p);
			}
			MaintainDataPair.copyCheckItems(data.getListCheckItems(), listPairTemp);
			System.out.println("print xxx");
			MaintainDataPair.print(listPairTemp);
			data.setListCheckItems(listPairTemp);
			String result=assetsResult.toString();
			data.setAssets(result.substring(1, result.length()-1));
			result=itemsResult.toString();
			data.setCheckItems(result.substring(1,result.length()-1));
			String []nameArr=data.getName().split(",");
			List<String> nameList=mergeList(nurseSiteMaintainConfig.getName(), nameArr);
			result=nameList.toString();
			data.setName(result.substring(1,result.length()-1));
		}
	}
	private List<String> mergeList(String[] source,String[] dest){
		List<String> newList=new ArrayList<String>();
		for(int i=0;i<source.length;++i){
			newList.add(source[i]);
		}
		for(int i=0;i<dest.length;++i){
			if(!newList.contains(dest[i])){
				newList.add(dest[i]);
			}
		}
		return newList;
	}
	
	private List<String> mergeList(List<AssetInfo> source,String[] dest){
		List<String> newList=new ArrayList<String>();
		for(int i=0;i<source.size();++i){
			newList.add(source.get(i).getName());
		}
		for(int i=0;i<dest.length;++i){
			if(!newList.contains(dest[i])){
				newList.add(dest[i]);
			}
		}
		return newList;
	}
	
	private List<String> mergeList(String source,String[] dest){
		List<String> newList=new ArrayList<String>();
		newList.add(source);
		for(int i=0;i<dest.length;++i){
			if(!newList.contains(dest[i])){
				newList.add(dest[i]);
			}
		}
		return newList;
	}
	
	
	private void batchCreateMaintainData(Date date,List<NurseSiteMaintain> listMaintainData){
		try {
			date=sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		}
		
		Calendar ca=Calendar.getInstance();
		ca.setTime(endBatchDate);
		ca.set(Calendar.DATE,ca.get(Calendar.DATE)+1);
		if(date.getTime()>=startBatchDate.getTime()&&date.getTime()<ca.getTime().getTime()){
			boolean findExist=false;
			if(existMaintainDate.size()>0){
				for(int i=0;i<existMaintainDate.size();++i){
					if(existMaintainDate.get(i).getTime()==date.getTime()){
						findExist=true;
						NurseSiteMaintain tempNurseSiteMaintain=existMaintainData.get(i);
						System.out.println("print listcheckitems");
						MaintainDataPair.print(listCheckItems);
						System.out.println("print tempNurseSiteMaintain");
						MaintainDataPair.print(tempNurseSiteMaintain.getListCheckItems());
						//merge
						mergeMaintainData(tempNurseSiteMaintain);
						System.out.println("print after tempNurseSiteMaintain");
						MaintainDataPair.print(tempNurseSiteMaintain.getListCheckItems());
						listMaintainData.add(tempNurseSiteMaintain);
					}
				}
			}
			if(!findExist){
				NurseSiteMaintain tempNurseSiteMaintain=createMaintainData(date);
//				DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(tempNurseSiteMaintain), date, date, true);
//		        tempEvent.setData(tempNurseSiteMaintain);
//		        this.lazyEventModel.addEvent(tempEvent);
		        listMaintainData.add(tempNurseSiteMaintain);
			}
		}
	}
	
	private void findExistMaintainData(){
		existMaintainDate=new ArrayList<Date>();
		existMaintainData=new ArrayList<NurseSiteMaintain>();
		List<NurseSiteMaintain> listNurseSiteMaintain=nurseSiteMaintainService.getMaintainData(startBatchDate, endBatchDate,currentUser.getSiteUID(),currentUser.getUid());
		if(listNurseSiteMaintain!=null && listNurseSiteMaintain.size()>0){
			for(int i=0;i<listNurseSiteMaintain.size();++i){
				existMaintainDate.add(listNurseSiteMaintain.get(i).getScheduleDate());
				existMaintainData.add(listNurseSiteMaintain.get(i));
			}
		}
		formatExistMaintainDate();
	}
	
	private void formatExistMaintainDate(){
		List<Date> tempList=new ArrayList<Date>();
		for(int i=0;i<existMaintainDate.size();++i){
			Date date=existMaintainDate.get(i);
			try {
				tempList.add(sdf.parse(sdf.format(date)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage());
			}
		}
		existMaintainDate=tempList;
	}
	
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void checkRightAll(){
		MaintainDataPair.replaceWithDefaultValue(listCheckItems, true);
	}

	public void saveMaintainData() {
		System.out.println("xxx save data");
		updateMaintainData(nurseSiteMaintain, false);
//		event.setTitle(getShowTitle(nurseSiteMaintain));
		System.out.println(getShowTitle(nurseSiteMaintain));
		nurseSiteMaintainService.save(nurseSiteMaintain);
//		if (event.getId() == null) {
//			// 新增记录
//			lazyEventModel.addEvent(event);
//			nurseSiteMaintainService.save(nurseSiteMaintain);
//		} else {
//			// 更新记录
//			if (event.getData() != null) {
//				lazyEventModel.updateEvent(event);
//				nurseSiteMaintainService.save(nurseSiteMaintain);
//			}
//		}
		addMessage("报告已保存");
	}

	public void deleteMaintainData() {
		if (event.getId() != null) {
			if (lazyEventModel.deleteEvent(event)) {
				nurseSiteMaintainService.delete((NurseSiteMaintain) event.getData());
				addMessage("报告已删除");
			}
		}
	}

	
	
	public void onNurseSiteMaintainConfigChange(){
		loadMaintainConfig();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (DefaultScheduleEvent) selectEvent.getObject();
		nurseSiteMaintain = (NurseSiteMaintain)event.getData();
		this.listCheckItems=nurseSiteMaintain.getListCheckItems();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		if (!isDeptHead) {
			return;
		}
		if(nurseSiteMaintainConfig==null){
			addMessage("请创建并选择配置模板");
			return;
		}
		Date date=(Date) selectEvent.getObject();
		Calendar ca=Calendar.getInstance();
		int currentHour=ca.get(Calendar.HOUR_OF_DAY);
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY,currentHour);
		loadConfigCheckItems();
		nurseSiteMaintain=createMaintainData(ca.getTime());
//		DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(nurseSiteMaintain), date, date, true);
//		tempEvent.setData(nurseSiteMaintain);
//		event = tempEvent;
	}

	public void onMaintainDataValidate() {
		
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//		addMessage(message);
	}

	//1:day,2:week,3:month,4:year
	public void batchSaveMaintainData() {
		if(nurseSiteMaintainConfig==null){
			addMessage("请创建并选择配置模板");
			return;
		}
		if(startBatchDate==null){
			startBatchDate=new Date();
		}
		if(endBatchDate==null){
			endBatchDate=new Date();
		}
		Integer type=nurseSiteMaintainConfig.getType();
		String typeValue=nurseSiteMaintainConfig.getTypeValue();
		List<NurseSiteMaintain> listMaintainData=new ArrayList<NurseSiteMaintain>();
		Calendar currentCalendar=Calendar.getInstance();
		Calendar startCalendar=Calendar.getInstance();
		startCalendar.setTime(startBatchDate);
		Calendar endCalendar=Calendar.getInstance();
		endCalendar.setTime(endBatchDate);
		int currentYear=currentCalendar.get(Calendar.YEAR);
		int currentMonth=currentCalendar.get(Calendar.MONTH);
		if(StringUtils.isNotEmpty(typeValue)){
			findExistMaintainData();
			loadConfigCheckItems();
			String arr[]=typeValue.split(",");
			if(type==1){
				//value,hours,1-24
				int startDay=startCalendar.get(Calendar.DAY_OF_MONTH);
				int endDay=endCalendar.get(Calendar.DAY_OF_MONTH);
//				for(int j=startDay;j<=endDay;++j){
//					for(int i=0;i<arr.length;++i){
//						Date date=null;
//						try {
//							currentCalendar=Calendar.getInstance();
//							currentCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[i]));
//							currentCalendar.set(Calendar.DAY_OF_MONTH,j);
//							date=currentCalendar.getTime();
//							
//						} catch (Exception e) {
//							LOGGER.error(e.getMessage());
//						}
//						batchCreateMaintainData(date, listMaintainData);
//					}
//				}
				Integer intTypeValue=Integer.parseInt(typeValue);
				for(int j=startDay;j<=endDay;++j){
					for(int i=0;i<intTypeValue;++i){
						Date date=null;
						try {
							currentCalendar=Calendar.getInstance();
							currentCalendar.set(Calendar.HOUR_OF_DAY, i);
							currentCalendar.set(Calendar.DAY_OF_MONTH,j);
							date=currentCalendar.getTime();
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
						batchCreateMaintainData(date, listMaintainData);
					}
				}
			}
			else if(type==2){
				//weekday,1-7
				int startWeek=startCalendar.get(Calendar.WEEK_OF_MONTH);
				int endWeek=endCalendar.get(Calendar.WEEK_OF_MONTH);
				for(int j=startWeek;j<=endWeek;++j){
					for(int i=0;i<arr.length;++i){
						try {
							currentCalendar=Calendar.getInstance();
							currentCalendar.set(Calendar.DAY_OF_WEEK,Integer.parseInt(arr[i]));
							currentCalendar.set(Calendar.WEEK_OF_MONTH,j);
							Date date=currentCalendar.getTime();
							batchCreateMaintainData(date, listMaintainData);
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
				}
			}
			else if(type==3){
				//value:day,1-31
				int startMonth=startCalendar.get(Calendar.MONTH);
				int endMonth=endCalendar.get(Calendar.MONTH);
				for(int j=startMonth;j<=endMonth;++j){
					for(int i=0;i<arr.length;++i){
						try {
							currentCalendar=Calendar.getInstance();
							int monthDays=DateUtil.getMonthDays(currentYear, j);
							if(Integer.parseInt(arr[i])<=monthDays){
								currentCalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(arr[i]));
								currentCalendar.set(Calendar.MONTH,j);
								Date date=currentCalendar.getTime();
								batchCreateMaintainData(date, listMaintainData);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							LOGGER.error(e.getMessage());
						}
					}
				}
			}
		}
		if(listMaintainData.size()>0){
			nurseSiteMaintainService.save(listMaintainData);
		}
		addMessage("报告已批量生成");
	}
	public void batchCompleteInit(){
		nurseSiteMaintain=createMaintainData(new Date());
	}
	public void batchCompleteMaintainData(){
		if(startBatchDate==null){
			startBatchDate=new Date();
		}
		if(endBatchDate==null){
			endBatchDate=new Date();
		}
		List<NurseSiteMaintain> listMaintainData=nurseSiteMaintainService.getMaintainData(startBatchDate, endBatchDate,currentUser.getSiteUID(),currentUser.getUid());
		if(listMaintainData!=null && listMaintainData.size()>0){
			for(int i=0;i<listMaintainData.size();++i){
				updateMaintainData(listMaintainData.get(i),true);
			}
		}
		nurseSiteMaintainService.save(listMaintainData);
		addMessage("报告已批量填写");
	}

	public boolean isValideDate() {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		calender.add(Calendar.DAY_OF_MONTH, -1);
		return true;
	}

	

	public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	public void setLazyEventModel(ScheduleModel lazyEventModel) {
		this.lazyEventModel = lazyEventModel;
	}

	public DefaultScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(DefaultScheduleEvent event) {
		this.event = event;
	}
	public NurseSiteMaintain getNurseSiteMaintain() {
		return nurseSiteMaintain;
	}

	public void setNurseSiteMaintain(NurseSiteMaintain nurseSiteMaintain) {
		this.nurseSiteMaintain = nurseSiteMaintain;
	}

	public Date getStartBatchDate() {
		return startBatchDate;
	}

	public void setStartBatchDate(Date startBatchDate) {
		this.startBatchDate = startBatchDate;
	}

	public Date getEndBatchDate() {
		return endBatchDate;
	}

	public void setEndBatchDate(Date endBatchDate) {
		this.endBatchDate = endBatchDate;
	}

	

	public boolean isDeleteDisable() {
		return isDeleteDisable;
	}

	public void setDeleteDisable(boolean isDeleteDisable) {
		this.isDeleteDisable = isDeleteDisable;
	}

	public boolean isSaveDisable() {
		return isSaveDisable;
	}

	public void setSaveDisable(boolean isSaveDisable) {
		this.isSaveDisable = isSaveDisable;
	}

	public boolean isDeptHead() {
		return isDeptHead;
	}

	public void setDeptHead(boolean isDeptHead) {
		this.isDeptHead = isDeptHead;
	}

	public List<NurseSiteMaintainConfig> getListNurseSiteMaintainConfig() {
		return listNurseSiteMaintainConfig;
	}

	public void setListNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig) {
		this.listNurseSiteMaintainConfig = listNurseSiteMaintainConfig;
	}

	public NurseSiteMaintainConfig getNurseSiteMaintainConfig() {
		return nurseSiteMaintainConfig;
	}

	public void setNurseSiteMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig) {
		this.nurseSiteMaintainConfig = nurseSiteMaintainConfig;
	}

	public List<AssetInfo> getListCheckAssets() {
		return listCheckAssets;
	}

	public void setListCheckAssets(List<AssetInfo> listCheckAssets) {
		this.listCheckAssets = listCheckAssets;
	}

	public List<MaintainDataPair> getListCheckItems() {
		return listCheckItems;
	}

	public void setListCheckItems(List<MaintainDataPair> listCheckItems) {
		this.listCheckItems = listCheckItems;
	}

	
}
