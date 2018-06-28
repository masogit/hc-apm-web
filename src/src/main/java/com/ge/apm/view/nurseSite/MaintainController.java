package com.ge.apm.view.nurseSite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.SimplePair;
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
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");

	private ScheduleModel lazyEventModel;
	private ScheduleEvent event;
	private NurseSiteMaintainConfig nurseSiteMaintainConfig;
	private NurseSiteMaintain nurseSiteMaintain;
	private String checkItemsStr;
	private List<Date> existMaintainDate;
	
	//config
	private Date startBatchDate;
	private Date endBatchDate;
	private int dayCheckCount;
	private List<SimplePair> listCheckItems;
	private List<AssetInfo> listCheckAssets;
	private List<String> listCheckDate;
	private List<String> listWeekDay;
	private List<String> listMonthDay;
	
	//dropdownlist
	private List<MutablePair<String,Integer>> checkListHours;
	private List<MutablePair<String,Integer>> checkListWeekDays;
	private List<MutablePair<String,Integer>> checkListMonthDays;
	private List<AssetInfo> checkListAssets;

	
	//validate
	private boolean isDeleteDisable;
    private boolean isSaveDisable;

	@PostConstruct
	protected void init() {

		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		List<String> orgUidList = nurseSiteMaintainService.getOrgListByUser(currentUser.getUid());
		nurseSiteMaintainConfig = nurseSiteMaintainService.getMaintainConfigBySiteUid(currentUser.getSiteUID());
		this.loadMaintainData(orgUidList);
		this.loadMaintainConfig();
		checkListAssets=nurseSiteMaintainService.getListAsset(currentUser.getSiteUID(), currentUser.getUid());
	}

	private void loadMaintainData(List<String> userOrgList) {
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
					DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(), data.getScheduleDate(), data.getScheduleDate(), true);
					tempEvent.setData(data);
					addEvent(tempEvent);
				}
			}
		};
	}
	
	private void parseContentJson(String contentJSON){
		try {
			SimplePair p=new SimplePair();
			p.parseStr(listCheckItems,contentJSON, false);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
	}

	private String getContentJson() {
		SimplePair p=new SimplePair(listCheckItems);
		return p.toString();
	}

	public void saveMaintainData(ActionEvent actionEvent) {
		if (event.getId() == null) {
			// 新增记录
			updateMaintainData(nurseSiteMaintain,false);
		} else {
			// 更新记录
			if (event.getData() != null) {
				updateMaintainData(nurseSiteMaintain,false);
			}
		}
		if (nurseSiteMaintain != null) {
			if (nurseSiteMaintainService.canAddMaintainData()) {
				if (nurseSiteMaintain.getId() == null) {
					lazyEventModel.addEvent(event);
				} else {
					lazyEventModel.updateEvent(event);
				}
				nurseSiteMaintainService.save(nurseSiteMaintain);
			} else {
				WebUtil.addErrorMessage("不能新增");
				return;
			}

		} else {
			lazyEventModel.updateEvent(new DefaultScheduleEvent(event.getTitle(), event.getStartDate(), event.getEndDate(), true));
		}
		event = new DefaultScheduleEvent();
	}

	public void deleteMaintainData() {
		if (event.getId() != null) {
			if (lazyEventModel.deleteEvent(event)) {
				nurseSiteMaintainService.delete((NurseSiteMaintain) event.getData());
			}
		}
	}

	private String getShowTitle() {
		return new Date().toString();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
		nurseSiteMaintain = (NurseSiteMaintain)event.getData();
		parseContentJson(nurseSiteMaintain.getContentJSON());
	}

	public void onDateSelect(SelectEvent selectEvent) {
		Date date=(Date) selectEvent.getObject();
		nurseSiteMaintain=createMaintainData(date);
		parseContentJson(nurseSiteMaintain.getContentJSON());
		DefaultScheduleEvent tempEvent = new DefaultScheduleEvent("", date, date, true);
		tempEvent.setData(nurseSiteMaintain);
		event = tempEvent;
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
			String arr[]=typeValue.split(",");
			if(type==1){
				//value,hours,1-24
				int startDay=startCalendar.get(Calendar.DAY_OF_MONTH);
				int endDay=endCalendar.get(Calendar.DAY_OF_MONTH);
				for(int j=startDay;j<=endDay;++j){
					for(int i=0;i<arr.length;++i){
						try {
							currentCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[i]));
							currentCalendar.set(Calendar.DAY_OF_MONTH,j);
							Date date=currentCalendar.getTime();
							createMaintainData(date, listMaintainData);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							LOGGER.error(e.getMessage());
						}
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
							currentCalendar.set(Calendar.DAY_OF_WEEK,Integer.parseInt(arr[i]));
							currentCalendar.set(Calendar.WEEK_OF_MONTH,j);
							Date date=currentCalendar.getTime();
							createMaintainData(date, listMaintainData);
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
							int monthDays=DateUtil.getMonthDays(currentYear, j);
							if(Integer.parseInt(arr[i])<=monthDays){
								currentCalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(arr[i]));
								currentCalendar.set(Calendar.MONTH,j);
								Date date=currentCalendar.getTime();
								createMaintainData(date, listMaintainData);
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
		parseContentJson(nurseSiteMaintain.getContentJSON());
	}
	public void batchCompleteMaintainData(){
		if(startBatchDate==null){
			startBatchDate=new Date();
		}
		if(endBatchDate==null){
			endBatchDate=new Date();
		}
		Integer type=nurseSiteMaintainConfig.getType();
		String typeValue=nurseSiteMaintainConfig.getTypeValue();
		List<NurseSiteMaintain> listMaintainData=nurseSiteMaintainService.getMaintainData(currentUser.getSiteUID(), startBatchDate, endBatchDate);
		if(listMaintainData!=null && listMaintainData.size()>0){
			for(int i=0;i<listMaintainData.size();++i){
				updateMaintainData(listMaintainData.get(i),true);
			}
		}
		nurseSiteMaintainService.save(listMaintainData);
	}
	
	private void findExistMaintainData(){
		existMaintainDate=new ArrayList<Date>();
		List<NurseSiteMaintain> listNurseSiteMaintain=nurseSiteMaintainService.getMaintainData(currentUser.getSiteUID(), startBatchDate, endBatchDate);
		if(listNurseSiteMaintain!=null && listNurseSiteMaintain.size()>0){
			for(int i=0;i<listNurseSiteMaintain.size();++i){
				existMaintainDate.add(listNurseSiteMaintain.get(i).getScheduleDate());
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
		tempNurseSiteMaintain.setCheckDate(null);
		tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
		tempNurseSiteMaintain.setContentJSON(null);
		tempNurseSiteMaintain.setCreateBy(currentUser);
		tempNurseSiteMaintain.setCreateByUid(currentUser.getUid());
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyBy(null);
		tempNurseSiteMaintain.setModifyByUid(null);
		tempNurseSiteMaintain.setModifyDate(null);
		tempNurseSiteMaintain.setScheduleDate(date);
		return tempNurseSiteMaintain;
	}
	
	private void updateMaintainData(NurseSiteMaintain obj,boolean batchUpdate){
		if(batchUpdate){
			if(obj.getContentJSON()==null){
				obj.setContentJSON(getContentJson());
				Date currentDate=new Date();
				obj.setCheckDate(currentDate);
				obj.setModifyBy(currentUser);
				obj.setModifyByUid(currentUser.getUid());
				obj.setModifyDate(currentDate);
			}
		}
		else{
			obj.setContentJSON(getContentJson());
			Date currentDate=new Date();
			obj.setCheckDate(currentDate);
			obj.setModifyBy(currentUser);
			obj.setModifyByUid(currentUser.getUid());
			obj.setModifyDate(currentDate);
		}
	}
	
	private void createMaintainData(Date date,List<NurseSiteMaintain> listMaintainData){
		try {
			date=sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
		}
		if(!DateUtil.listContainsDate(existMaintainDate, date)){
			NurseSiteMaintain tempNurseSiteMaintain=createMaintainData(date);
			DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(), date, date, true);
	        tempEvent.setData(tempNurseSiteMaintain);
	        this.lazyEventModel.addEvent(tempEvent);
	        listMaintainData.add(tempNurseSiteMaintain);
		}
	}

	private void loadMaintainConfig() {
		listCheckAssets=new ArrayList<AssetInfo>();
		listCheckItems = new ArrayList<SimplePair>();
		if(nurseSiteMaintainConfig!=null){
			String assets=nurseSiteMaintainConfig.getAssets();
			if(StringUtils.isNotEmpty(assets)){
				String arr[]=assets.split(",");
				for(int i=0;i<arr.length;++i){
					AssetInfo info=nurseSiteMaintainService.getAssetInfoByUid(arr[i]);
					if(info!=null){
						listCheckAssets.add(info);
					}
				}
			}
			String strCheckItems = nurseSiteMaintainConfig.getCheckItems();
			if (StringUtils.isNotEmpty(strCheckItems) && listCheckAssets!=null) {
				String arr2[] = strCheckItems.split(" ");
				for (int i = 0; i < listCheckAssets.size(); ++i) {
					List<SimplePair> listTemp=new ArrayList<SimplePair>();
					for (int j = 0; j < arr2.length; ++j) {
						SimplePair p = new SimplePair(arr2[j], "");
						listTemp.add(p);
					}
					SimplePair p2 = new SimplePair();
					p2.setKey(listCheckAssets.get(i).getName());
					p2.setListSimplePairValue(listTemp);
					listCheckItems.add(p2);
				}
			}
			Integer type=nurseSiteMaintainConfig.getType();
			String typeValue=nurseSiteMaintainConfig.getTypeValue();
			if(type!=null && StringUtils.isNotEmpty(typeValue)){
				if(type==1){
					listCheckDate=Arrays.asList(typeValue.split(","));
				}
				else if(type==2){
					listWeekDay=Arrays.asList(typeValue.split(","));
				}
				else if(type==3){
					listMonthDay=Arrays.asList(typeValue.split(","));
				}
			}
		}
		else{
			nurseSiteMaintainConfig=new NurseSiteMaintainConfig();
		}
	}

	public void saveMaintainConfig() {
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
				if(listCheckDate!=null){
					for(int i=0;i<listCheckDate.size();++i){
						typeValue+=listCheckDate.get(i)+",";
					}
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
		if(listCheckAssets!=null && listCheckAssets.size()>0){
			String temp="";
			for(int i=0;i<listCheckAssets.size();++i){
				temp+=listCheckAssets.get(i).getUid()+",";
			}
			nurseSiteMaintainConfig.setAssets(temp);
		}
		nurseSiteMaintainConfig.setTypeValue(typeValue);
		nurseSiteMaintainService.save(nurseSiteMaintainConfig);
		addMessage("配置已保存");
		loadMaintainConfig();
	}
	
	public void enterCheckItem(){
		String temp=nurseSiteMaintainConfig.getCheckItems();
		if(StringUtils.isNotEmpty(temp) && StringUtils.isNotEmpty(checkItemsStr)){
			temp+=",";
			if(temp.contains(checkItemsStr)){	
			}
			else{
				nurseSiteMaintainConfig.setCheckItems(nurseSiteMaintainConfig.getCheckItems()+","+checkItemsStr);
				checkItemsStr="";
			}
		}
		else{
			nurseSiteMaintainConfig.setCheckItems(checkItemsStr);
		}
	}

	public boolean isValideDate() {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		calender.add(Calendar.DAY_OF_MONTH, -1);
		return true;
	}

	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
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

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public NurseSiteMaintainConfig getNurseSiteMaintainConfig() {
		return nurseSiteMaintainConfig;
	}

	public void setNurseSiteMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig) {
		this.nurseSiteMaintainConfig = nurseSiteMaintainConfig;
	}

	public List<SimplePair> getListCheckItems() {
		return listCheckItems;
	}

	public void setListCheckItems(List<SimplePair> listCheckItems) {
		this.listCheckItems = listCheckItems;
	}

	public int getDayCheckCount() {
		return dayCheckCount;
	}

	public void setDayCheckCount(int dayCheckCount) {
		this.dayCheckCount = dayCheckCount;
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

	public List<MutablePair<String,Integer>> getCheckListHours() {
		if(checkListHours==null){
			checkListHours=new ArrayList<MutablePair<String,Integer>>();
			for(int i=1;i<=24;++i){
				MutablePair<String,Integer> p=new MutablePair<String,Integer>(i+"时",i);
				checkListHours.add(p);
			}
		}
		return checkListHours;
	}

	public void setCheckListHours(List<MutablePair<String,Integer>> checkListHours) {
		this.checkListHours = checkListHours;
	}

	public List<MutablePair<String,Integer>> getCheckListWeekDays() {
		if(checkListWeekDays==null){
			checkListWeekDays=new ArrayList<MutablePair<String,Integer>>();
			MutablePair<String,Integer> p=new MutablePair<String,Integer>("星期日",1);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期一",2);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期二",3);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期三",4);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期四",5);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期五",6);
			checkListWeekDays.add(p);
			p=new MutablePair<String,Integer>("星期六",7);
			checkListWeekDays.add(p);
		}
		return checkListWeekDays;
	}

	public void setCheckListWeekDays(List<MutablePair<String,Integer>> checkListWeekDays) {
		this.checkListWeekDays = checkListWeekDays;
	}

	public List<MutablePair<String,Integer>> getCheckListMonthDays() {
		if(checkListMonthDays==null){
			checkListMonthDays=new ArrayList<MutablePair<String,Integer>>();
			for(int i=1;i<=31;++i){
				MutablePair<String,Integer> p=new MutablePair<String,Integer>(i+"日", i);
				checkListMonthDays.add(p);
			}
		}
		return checkListMonthDays;
	}

	public void setCheckListMonthDays(List<MutablePair<String,Integer>> checkListMonthDays) {
		this.checkListMonthDays = checkListMonthDays;
	}

	public List<AssetInfo> getListCheckAssets() {
		return listCheckAssets;
	}

	public void setListCheckAssets(List<AssetInfo> listCheckAssets) {
		this.listCheckAssets = listCheckAssets;
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

	public String getCheckItemsStr() {
		return checkItemsStr;
	}

	public void setCheckItemsStr(String checkItemsStr) {
		this.checkItemsStr = checkItemsStr;
	}

	public List<AssetInfo> getCheckListAssets() {
		return checkListAssets;
	}

	public void setCheckListAssets(List<AssetInfo> checkListAssets) {
		this.checkListAssets = checkListAssets;
	}
	
	
	
	
}
