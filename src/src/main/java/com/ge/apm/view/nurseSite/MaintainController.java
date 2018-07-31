package com.ge.apm.view.nurseSite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.utils.StringComparator;
import com.ge.apm.view.sysutil.UserContextService;

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
	private ScheduleModel lazyEventModel;
	private DefaultScheduleEvent event;
	private List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig;
	private List<NurseSiteMaintainConfig> showListNurseSiteMaintainConfig;
	private NurseSiteMaintainConfig nurseSiteMaintainConfig;
	private List<AssetInfo> listCheckAssets;
	private List<OrgInfo> listCheckClinicalOrgInfo;
	//from config default
	private NurseSiteMaintain nurseSiteMaintain;
	private List<NurseSiteMaintain> existMaintainData;
	private List<NurseSiteMaintain> listMaintainData;
	private Date initStartDate;
	private Date initEndDate;
	private Date startBatchDate;
  	private Date endBatchDate;
  	private Date userClickDate;
	//validate
	private boolean isDeleteDisable;
    private boolean isSaveDisable;
    //show
    private List<OrgInfo> listOrgInfo; 
    //inner
    private Map<String,Integer> mapDayIndex=new HashMap<String,Integer>();
    private Date dayDate;
    private String signatureContent;
    private StreamedContent signatureContentWeiXin;
    private boolean blnWebSignatureEidt;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    private Set<String> orgUidList;

	@PostConstruct
	protected void init() {
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		this.loadMaintainData();
		orgUidList = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
		listNurseSiteMaintainConfig=nurseSiteMaintainService.getMaintainConfigByOrgUids(orgUidList);
		showListNurseSiteMaintainConfig=new ArrayList<NurseSiteMaintainConfig>();
		showListNurseSiteMaintainConfig.addAll(listNurseSiteMaintainConfig);
		loadUserOrgInfo();
	}
	
	private List<NurseSiteMaintain> searchMaintainData(Date start,Date end){
		List<NurseSiteMaintain> listData=null;
		Calendar ca=Calendar.getInstance();
		if(start==null){
			ca.set(Calendar.DAY_OF_MONTH, 1);
			start=ca.getTime();
		}
		if(end==null){
			ca.add(Calendar.MONTH, 1);
			end=ca.getTime();
		}
		if(listCheckClinicalOrgInfo==null || listCheckClinicalOrgInfo.size()==0){
			listData=findDefaultMaintain(start,end);
		}
		else{
			List<String> listUidTemp=new ArrayList<String>();
			for(int i=0;i<listCheckClinicalOrgInfo.size();++i){
				listUidTemp.add(listCheckClinicalOrgInfo.get(i).getUid());
			}
			listData=nurseSiteMaintainService.getDataByClinicalOrgUid(listUidTemp, start, end);
		}
		return listData;
	}
	
	private List<NurseSiteMaintain> findDefaultMaintain(Date startDate,Date endDate){
		List<NurseSiteMaintain> listResult=null;
		listResult=nurseSiteMaintainService.getDefaultMaintainData(orgUidList, startDate, endDate);
		return listResult;
	}

	private void loadMaintainData() {
		if(initStartDate!=null && initEndDate!=null){
			listMaintainData=searchMaintainData(initStartDate, initEndDate);
		}
		lazyEventModel = new LazyScheduleModel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void loadEvents(Date start, Date end) {
				mapDayIndex.clear();
				if(initStartDate==null || initEndDate==null || start.getTime()!=initStartDate.getTime() || end.getTime()!=initEndDate.getTime()){
					listMaintainData=searchMaintainData(start, end);
					initStartDate=start;
					initEndDate=end;
				}
				if(listMaintainData!=null){
					for (NurseSiteMaintain data : listMaintainData) {
						DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(getShowTitle(data),data.getScheduleDate(),data.getScheduleDate(), true);
						tempEvent.setData(data);
						Integer status=data.getStatus();
						if(status!=null){
							if(status==0){
								tempEvent.setStyleClass("schedule-item-nocheck");
							}
							else if(status==2){
								tempEvent.setStyleClass("schedule-item-pass");
							}
							else if(status==3){
								tempEvent.setStyleClass("schedule-item-nopass");
							}
						}
						tempEvent.setId(UUID.randomUUID().toString());
						if(lazyEventModel.getEvents().add(tempEvent)){
						}
						else{
							updateEvent(tempEvent);
						}
					}
				}
			}
		};
	}
	
	private void loadUserOrgInfo(){
		if(orgUidList.size()>0){
			listOrgInfo=nurseSiteMaintainService.getListClinicalByUid(orgUidList);
			listCheckClinicalOrgInfo=new ArrayList<OrgInfo>();
		}
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
		int dayIndex=0;
		if(mapDayIndex.containsKey(tempNurseSiteMaintain.getConfigId())){
			dayIndex=mapDayIndex.get(tempNurseSiteMaintain.getConfigId());
		}
		Date newDate=tempNurseSiteMaintain.getScheduleDate();
		Calendar newCa=Calendar.getInstance();
		newCa.setTime(newDate);
		if(dayDate==null){
			dayIndex=1;
		}
		else{
			Calendar oldCa=Calendar.getInstance();
			oldCa.setTime(dayDate);
			if(oldCa.get(Calendar.DAY_OF_YEAR)==newCa.get(Calendar.DAY_OF_YEAR)){
				dayIndex++;
			}
			else{
				dayIndex=1;
				mapDayIndex.clear();
			}
		}
		dayDate=newDate;
		mapDayIndex.put(tempNurseSiteMaintain.getConfigId(), dayIndex);
		String str=tempNurseSiteMaintain.getName()+"-"+dayIndex+"-"+strStatus;
		return str;
	}
	
	private void loadMaintainConfig() {
		if(nurseSiteMaintainConfig!=null){
			String configId=nurseSiteMaintainConfig.getId();
			listCheckAssets=nurseSiteMaintainService.getListAssetInfoByMaintainConfigParentId(configId);
			if(userClickDate==null){
				userClickDate=new Date();
			}
			Calendar ca=Calendar.getInstance();
			int currentHour=ca.get(Calendar.HOUR_OF_DAY);
			ca.setTime(userClickDate);
			ca.set(Calendar.HOUR_OF_DAY,currentHour);
			nurseSiteMaintain=nurseSiteMaintainService.createMaintain(ca.getTime(),currentUser,nurseSiteMaintainConfig,listCheckAssets);
		}
	}
	
	private void batchCreateMaintainData(Date date,List<NurseSiteMaintain> listMaintainData){		
		NurseSiteMaintain tempNurseSiteMaintain=nurseSiteMaintainService.createMaintain(date,currentUser,nurseSiteMaintainConfig,listCheckAssets);
        listMaintainData.add(tempNurseSiteMaintain);
	}
	
	private void findExistMaintainData(){
		existMaintainData=findDefaultMaintain(startBatchDate, endBatchDate);
		if(existMaintainData!=null && existMaintainData.size()>0){
			for(int i=0;i>-1&&i<existMaintainData.size();++i){
				NurseSiteMaintain temp=existMaintainData.get(i);
				if((temp.getStatus()!=null && temp.getStatus()!=0)||(!temp.getConfigId().equals(nurseSiteMaintainConfig.getId()))){
					existMaintainData.remove(i);
					--i;
				}
			}
		}
	}
	
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	private void insertMaintain(NurseSiteMaintain data){
		if(listMaintainData==null){
			listMaintainData=new ArrayList<NurseSiteMaintain>();
		}
		int beforeSize=listMaintainData.size();
		Date afterDate=data.getScheduleDate();
		boolean add=false;
		for(int i=0;i<listMaintainData.size();++i){
			Date tempDate=listMaintainData.get(i).getScheduleDate();
			try {
				if(sdf.parse(sdf.format(tempDate)).compareTo(sdf.parse(sdf.format(afterDate)))==0){
					add=true;
				}
				else{
					if(add){
						listMaintainData.add(i, data);
						return;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		}
		int afterSize=listMaintainData.size();
		if(afterSize==beforeSize){
			listMaintainData.add(data);
		}
	}
	
	private void generateNurseSiteMaintainData(){
		List<String> showLables=nurseSiteMaintain.getShowLables();
		Set<NurseSiteMaintainItem> listItem=nurseSiteMaintain.getListNurseSiteMaintainItem();
		if(nurseSiteMaintain.isNeedGetItem()){
			listItem=nurseSiteMaintainService.findMaintainItemByParentId(nurseSiteMaintain.getId());
			nurseSiteMaintain.setListNurseSiteMaintainItem(listItem);
			nurseSiteMaintain.setModifyBy(nurseSiteMaintainService.getUserAccountByUid(nurseSiteMaintain.getModifyByUid()));
			nurseSiteMaintain.setNeedGetItem(false);
		}
		if(showLables==null){
			showLables=new ArrayList<String>();
		}
		else{
			showLables.clear();
		}
		try {
			if(listItem!=null && listItem.size()>0){
				Set<NurseSiteMaintainData> listTempData=listItem.iterator().next().getListData();
				if(listTempData!=null){
					Iterator<NurseSiteMaintainData> it=listTempData.iterator();
					while(it.hasNext()){
						NurseSiteMaintainData tempData=it.next();
						showLables.add(tempData.getCheckItem());
					}
					showLables.sort(new StringComparator());
				}
			}
			nurseSiteMaintain.setShowLables(showLables);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("getLables error"+e.getMessage());
		}
	}
	
	private void initValueAfterEvent(){
		signatureContent=null;
		signatureContentWeiXin=null;
	}
	
	private void initValueBeforeEvent(){
		signatureContent=null;
		signatureContentWeiXin=null;
	}
	
	public void checkRightAll(){
		Iterator<NurseSiteMaintainItem> it=nurseSiteMaintain.getListNurseSiteMaintainItem().iterator();
		while(it.hasNext()){
			NurseSiteMaintainItem tempItem=it.next();
			Set<NurseSiteMaintainData> listData=tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData=listData.iterator();
			while(itData.hasNext()){
				NurseSiteMaintainData tempData=itData.next();
				tempData.setCheckValue(true);
			}
		}
	}

	public void saveMaintain() {
		Date currentDate=new Date();
		nurseSiteMaintain.setCheckDate(currentDate);
		nurseSiteMaintain.setModifyBy(currentUser);
		nurseSiteMaintain.setModifyDate(currentDate);
		nurseSiteMaintainService.updateMaintainDataStatus(nurseSiteMaintain, false,null);
		nurseSiteMaintainService.saveMaintain(nurseSiteMaintain);
		lazyEventModel.updateEvent(event);
		addMessage("报告已保存");
	}
	
	public void createMaintain() {
		if(nurseSiteMaintainConfig==null){
			addMessage("请创建并选择配置模板");
			return;
		}
		Calendar ca=Calendar.getInstance();
		int currentHour=ca.get(Calendar.HOUR_OF_DAY);
		ca.setTime(userClickDate);
		ca.set(Calendar.HOUR_OF_DAY,currentHour);
		nurseSiteMaintain=nurseSiteMaintainService.createMaintain(ca.getTime(),currentUser,nurseSiteMaintainConfig,listCheckAssets);
		nurseSiteMaintainService.saveMaintain(nurseSiteMaintain);
//		event = new DefaultScheduleEvent(getShowTitle(nurseSiteMaintain),userClickDate, userClickDate, true);
//		event.setData(nurseSiteMaintain);
//		lazyEventModel.addEvent(event);
		insertMaintain(nurseSiteMaintain);
		addMessage("报告已创建");
	}

	public void deleteMaintain() {
		if (event.getId() != null) {
			if (lazyEventModel.deleteEvent(event)) {
				nurseSiteMaintain=(NurseSiteMaintain) event.getData();
				nurseSiteMaintainService.deleteMaintain(nurseSiteMaintain);
				listMaintainData.remove(nurseSiteMaintain);
				addMessage("报告已删除");
			}
		}
	}
	
	public void saveMaintainSignature(){
		if(StringUtils.isNotEmpty(signatureContent)){
			nurseSiteMaintain.setSignatureId(nurseSiteMaintainService.saveMaintainSignature(signatureContent,nurseSiteMaintain.getId()));
			initValueAfterEvent();
		}
	}
	
	public void getMaintainSignature(String signatureId,Integer signatureSourceType){
		if(StringUtils.isNotEmpty(signatureId)){
			if(signatureSourceType==null){
				blnWebSignatureEidt=true;
			}
			else if(signatureSourceType==0){
				signatureContent=nurseSiteMaintainService.getMaintainSignatureWeb(signatureId);
				blnWebSignatureEidt=false;
			}
			else if(signatureSourceType==1){
				signatureContentWeiXin=nurseSiteMaintainService.getMaintainSignatureWeiXin(signatureId);
				blnWebSignatureEidt=false;
			}
		}
		else{
			blnWebSignatureEidt=true;
		}
	}
	
	public void resetMaintainSignature(){
		signatureContent=null;
	}
	
	public void onNurseSiteMaintainConfigChange(){
		loadMaintainConfig();
	}
	
	public void onClinicalOrgInfoChange(){
		if(showListNurseSiteMaintainConfig==null){
			showListNurseSiteMaintainConfig=new ArrayList<NurseSiteMaintainConfig>();
		}
		else{
			showListNurseSiteMaintainConfig.clear();
		}
		if(listNurseSiteMaintainConfig!=null){
			Set<String> setClinicalDeptUid=new HashSet<String>();
			for(int i=0;i<listCheckClinicalOrgInfo.size();++i){
				setClinicalDeptUid.add(listCheckClinicalOrgInfo.get(i).getUid());
			}
			for(int i=0;i<listNurseSiteMaintainConfig.size();++i){
				String tempUid=listNurseSiteMaintainConfig.get(i).getClinicalDeptUID();
				if(setClinicalDeptUid.contains(tempUid)){
					showListNurseSiteMaintainConfig.add(listNurseSiteMaintainConfig.get(i));
				}
			}
		}
		loadMaintainData();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (DefaultScheduleEvent) selectEvent.getObject();
		nurseSiteMaintain = (NurseSiteMaintain)event.getData();
		generateNurseSiteMaintainData();
		initValueBeforeEvent();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		userClickDate=(Date) selectEvent.getObject();
		initValueBeforeEvent();
	}

//	public void onEventMove(ScheduleEntryMoveEvent event) {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//	}
//	public void onEventResize(ScheduleEntryResizeEvent event) {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//	}

	//1:day,2:week,3:month,4:year
	public String batchSaveMaintainData() {
		if(nurseSiteMaintainConfig==null){
			addMessage("请创建并选择配置模板");
			return "maintainMain?faces-redirect=false";
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
		if(StringUtils.isNotEmpty(typeValue)){
			findExistMaintainData();
			String arr[]=typeValue.split(",");
			if(type==1){
				//value,hours,1-24
				Integer intTypeValue=Integer.parseInt(typeValue);
				while(startCalendar.getTimeInMillis()<=endCalendar.getTimeInMillis()){
					for(int i=0;i<intTypeValue;++i){
						Date date=null;
						try {
							startCalendar.set(Calendar.HOUR_OF_DAY, i);
							date=startCalendar.getTime();
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
						batchCreateMaintainData(date, listMaintainData);
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
			else if(type==2){
				//weekday,1-7
				while(startCalendar.getTimeInMillis()<=endCalendar.getTimeInMillis()){
					for(int i=0;i<arr.length;++i){
						try {
							if(startCalendar.get(Calendar.DAY_OF_WEEK)==Integer.parseInt(arr[i])){
								startCalendar.set(Calendar.HOUR_OF_DAY,currentCalendar.get(Calendar.HOUR_OF_DAY));
								batchCreateMaintainData(startCalendar.getTime(), listMaintainData);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
		}
		if(listMaintainData.size()>0){
			nurseSiteMaintainService.saveAndDelete(listMaintainData,existMaintainData);
		}
		addMessage("报告已批量生成");
		return "maintainMain?faces-redirect=true";
	}
	public void batchCompleteMaintainData(){
		if(startBatchDate==null){
			startBatchDate=new Date();
		}
		if(endBatchDate==null){
			endBatchDate=new Date();
		}
		Date currentDate=new Date();
		List<NurseSiteMaintain> listNewNurseSiteMaintain=new ArrayList<NurseSiteMaintain>();
		if(listMaintainData!=null && listMaintainData.size()>0){
			String signatureId=null;
			if(StringUtils.isNotEmpty(signatureContent)){
				signatureId=nurseSiteMaintainService.saveMaintainSignature(signatureContent, "");
			}
			for(int i=0;i<listMaintainData.size();++i){
				NurseSiteMaintain tempMaintain=listMaintainData.get(i);
				Date tempDate=tempMaintain.getScheduleDate();
				if(tempDate.getTime()>=startBatchDate.getTime() && tempDate.getTime()<endBatchDate.getTime()){
					tempMaintain.setCheckDate(currentDate);
					tempMaintain.setModifyBy(currentUser);
					tempMaintain.setModifyDate(currentDate);
					nurseSiteMaintainService.updateMaintainDataStatus(tempMaintain,true,signatureId);
					listNewNurseSiteMaintain.add(tempMaintain);
				}
			}
			nurseSiteMaintainService.save(listNewNurseSiteMaintain);
			addMessage("报告已批量填写");
		}
		else{
			addMessage("报告批量填写失败");
		}
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
		Calendar ca=Calendar.getInstance();
		ca.setTime(endBatchDate);
		ca.set(Calendar.DATE, ca.get(Calendar.DATE)+1);
		this.endBatchDate = ca.getTime();
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

	public List<OrgInfo> getListOrgInfo() {
		return listOrgInfo;
	}

	public void setListOrgInfo(List<OrgInfo> listOrgInfo) {
		this.listOrgInfo = listOrgInfo;
	}

	public List<OrgInfo> getListCheckClinicalOrgInfo() {
		return listCheckClinicalOrgInfo;
	}

	public void setListCheckClinicalOrgInfo(List<OrgInfo> listCheckClinicalOrgInfo) {
		this.listCheckClinicalOrgInfo = listCheckClinicalOrgInfo;
	}

	public List<NurseSiteMaintainConfig> getShowListNurseSiteMaintainConfig() {
		return showListNurseSiteMaintainConfig;
	}

	public void setShowListNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> showListNurseSiteMaintainConfig) {
		this.showListNurseSiteMaintainConfig = showListNurseSiteMaintainConfig;
	}

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}

	public StreamedContent getSignatureContentWeiXin() {
		return signatureContentWeiXin;
	}

	public void setSignatureContentWeiXin(StreamedContent signatureContentWeiXin) {
		this.signatureContentWeiXin = signatureContentWeiXin;
	}

	public boolean isBlnWebSignatureEidt() {
		return blnWebSignatureEidt;
	}

	public void setBlnWebSignatureEidt(boolean blnWebSignatureEidt) {
		this.blnWebSignatureEidt = blnWebSignatureEidt;
	}
}
