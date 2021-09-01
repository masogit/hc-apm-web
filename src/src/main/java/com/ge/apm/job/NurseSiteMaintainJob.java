package com.ge.apm.job;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BatchJob;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.dao.mapper.TenantInfoMapper;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BatchJobRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainDataRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
/**
 * Created by 503035627
 */
@Component
@EnableScheduling
public class NurseSiteMaintainJob {
	public static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainJob.class);

	@Autowired
	AssetInfoRepository assetInfoRepository;

	@Autowired
	BatchJobRepository batchJobRepository;

	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;

	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;

	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;

	@Autowired
	TenantInfoMapper tenantInfoMapper;

	@Autowired
	NurseSiteMaintainService nurseSiteMaintainService;

	@Scheduled(cron = "20/20 * * * * *")
	public void execute() {
		try {
			System.out.println("aa");
			checkJob();
			doJob();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
			if (firstJob != null) {
				firstJob.setLog(e.getMessage());
				firstJob.setProcessEndTime(new Date());
				firstJob.setStatus(BatchJob.STATUS_FAILED);
				batchJobRepository.save(firstJob);
			}
		}
	}
	private NurseSiteMaintainConfig nurseSiteMaintainConfig;
	private Date startBatchDate;
	private Date endBatchDate;
	private String userUid;
	private List<AssetInfo> listCheckAssets;
	private Set<String> orgUidList;
	private BatchJob firstJob;
	private String backupUserUid;
	private List<NurseSiteMaintainItem> listNewMaintainItem;
	private void doJob() throws Exception  {
		LOGGER.info("NurseSiteMaintainJob start to work ...");
		while (true) {
			List<BatchJob> listJob = batchJobRepository.findNewBatchJobWithName(BatchJob.NAME_NURSESITE);
			if (listJob != null && listJob.size() > 0) {
				for (int i = 0; i < listJob.size(); ++i) {
					firstJob = listJob.get(i);
					tDoJob();
				}
			} else {
				break;
			}
		}
		LOGGER.info("NurseSiteMaintainJob end ...");
	}

	private void tDoJob()throws Exception{
		firstJob.setProcessStartTime(new Date());
		firstJob.setStatus(BatchJob.STATUS_START);
		batchJobRepository.save(firstJob);
		String action = firstJob.getSubName();
		String content = firstJob.getContent();
		if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(action)) {
			// content:nurseSiteMaintainConfigId,startBatchDate,endBatchDate,currentUser
			String[] params = content.split(",");
			if ("create".equals(action)) {
				doJobCreate(params);
			} else if ("update".equals(action)) {
				doJobUpdate(params);
			} else if ("deleteAssetInfo".equals(action)) {
				doJobDeleteAssetInfo(params);
			} else if ("complete".equals(action)) {
				doJobComplete(params, firstJob.getCreateTime());
			} else if ("delete".equals(action)) {
				doJobDelete(params);
			}
			else if("dailyUpdate".equals(action)){
				doJobDailyUpdate(content);
			}
		}
		firstJob.setProcessEndTime(new Date());
		firstJob.setStatus(BatchJob.STATUS_SUCCESS);
		batchJobRepository.save(firstJob);
	}

	private void checkJob(){
		LOGGER.info("NurseSiteMaintainJob start check ...");
		batchJobRepository.updateJobStatus(1, 0);
		LOGGER.info("NurseSiteMaintainJob end check ...");
	}

	public void doJobDailyUpdate(String content){
		//2,3,5
		//1:正常,2:停机,3:限制功能使用,4:待报废,5:停用,6:报警
		Date startDate=null;
		Date nextDate=null;
		Date weekFirst=null;
		Date nextWeekFirst=null;
		Date monthFirst=null;
		Date nextMonthFirst=null;
		Calendar cd=Calendar.getInstance();
		Date currentDate=cd.getTime();
		List<Integer> listStatus=new ArrayList<Integer>();
		listStatus.add(2);
		listStatus.add(3);
		listStatus.add(4);
		listStatus.add(5);
		if(StringUtils.isEmpty(content)){
			startDate=cd.getTime();
		}
		else{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			try {
				startDate=sdf.parse(content);
				cd.setTime(startDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				startDate=cd.getTime();
			}
		}
		cd.set(Calendar.HOUR, 0);
		cd.set(Calendar.MINUTE, 0);
		cd.set(Calendar.SECOND, 0);
		startDate=cd.getTime();
		cd.add(Calendar.DAY_OF_YEAR, 1);
		nextDate=cd.getTime();
		cd.setTime(startDate);
		cd.set(Calendar.DAY_OF_WEEK, 1);
		weekFirst=cd.getTime();
		cd.add(Calendar.WEEK_OF_YEAR, 1);
		nextWeekFirst=cd.getTime();
		cd.setTime(startDate);
		cd.set(Calendar.DAY_OF_MONTH, 1);
		monthFirst=cd.getTime();
		cd.add(Calendar.MONTH, 1);
		nextMonthFirst=cd.getTime();
//		Set<String> listDeleteAssetUid=nurseSiteMaintainItemRepository.findDeleteAssetUid(date);
//		if(listDeleteAssetUid!=null && listDeleteAssetUid.size()>0){
//			for(String assetUid:listDeleteAssetUid){
//				addBatchJob("deleteAssetInfo", null, null, null, null,assetUid);
//			}
//		}
		//day
		//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
		nurseSiteMaintainDataRepository.updateDailyByStatus(startDate,nextDate,listStatus,1);
		nurseSiteMaintainItemRepository.updateDailyByStatus(startDate,nextDate,2,1,"停机");
		nurseSiteMaintainItemRepository.updateDailyByStatus(startDate,nextDate,3,1, "限制功能使用");
		nurseSiteMaintainItemRepository.updateDailyByStatus(startDate,nextDate,4,1,"待报废");
		nurseSiteMaintainItemRepository.updateDailyByStatus(startDate,nextDate,5,1,"停用");
		nurseSiteMaintainItemRepository.updateDailyByValid(startDate,nextDate,false,1,"报废");
		nurseSiteMaintainItemRepository.updateDailyByDelete(startDate,nextDate,true,1,"已删除");
		nurseSiteMaintainItemRepository.updateDailyByStatus(startDate,nextDate,listStatus,1,currentDate,8);
		nurseSiteMaintainRepository.updateDailyByStatus(startDate,nextDate,listStatus,1,currentDate,8,8);
		//week
		nurseSiteMaintainDataRepository.updateDailyByStatus(weekFirst,nextWeekFirst,listStatus,2);
		nurseSiteMaintainItemRepository.updateDailyByStatus(weekFirst,nextWeekFirst,2,2,"停机");
		nurseSiteMaintainItemRepository.updateDailyByStatus(weekFirst,nextWeekFirst,3,2, "限制功能使用");
		nurseSiteMaintainItemRepository.updateDailyByStatus(weekFirst,nextWeekFirst,4,2,"待报废");
		nurseSiteMaintainItemRepository.updateDailyByStatus(weekFirst,nextWeekFirst,5,2,"停用");
		nurseSiteMaintainItemRepository.updateDailyByValid(weekFirst,nextWeekFirst,false,2,"报废");
		nurseSiteMaintainItemRepository.updateDailyByDelete(weekFirst,nextWeekFirst,true,2,"已删除");
		nurseSiteMaintainItemRepository.updateDailyByStatus(weekFirst,nextWeekFirst,listStatus,2,currentDate,8);
		nurseSiteMaintainRepository.updateDailyByStatus(weekFirst,nextWeekFirst,listStatus,2,currentDate,8,8);
		//month
		nurseSiteMaintainDataRepository.updateDailyByStatus(monthFirst,nextMonthFirst,listStatus,3);
		nurseSiteMaintainItemRepository.updateDailyByStatus(monthFirst,nextMonthFirst,2,3,"停机");
		nurseSiteMaintainItemRepository.updateDailyByStatus(monthFirst,nextMonthFirst,3,3, "限制功能使用");
		nurseSiteMaintainItemRepository.updateDailyByStatus(monthFirst,nextMonthFirst,4,3,"待报废");
		nurseSiteMaintainItemRepository.updateDailyByStatus(monthFirst,nextMonthFirst,5,3,"停用");
		nurseSiteMaintainItemRepository.updateDailyByValid(monthFirst,nextMonthFirst,false,3,"报废");
		nurseSiteMaintainItemRepository.updateDailyByDelete(monthFirst,nextMonthFirst,true,3,"已删除");
		nurseSiteMaintainItemRepository.updateDailyByStatus(monthFirst,nextMonthFirst,listStatus,3,currentDate,8);
		nurseSiteMaintainRepository.updateDailyByStatus(monthFirst,nextMonthFirst,listStatus,3,currentDate,8,8);
	}

	public boolean addBatchJob(String action,NurseSiteMaintainConfig config, Date startDate, Date endDate, String userUid,String other) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String content = "";
		if(config!=null){
			sb.append(config.getId());
		}
		sb.append(",");
		if(startDate!=null){
			sb.append(sdf.format(startDate));
		}
		sb.append(",");
		if(endDate!=null){
			sb.append(sdf.format(endDate));
		}
		sb.append(",");
		if(StringUtils.isNotEmpty(userUid)){
			sb.append(userUid);
		}
		sb.append(",");
		if(StringUtils.isNotEmpty(other)){
			sb.append(other);
		}
		sb.append(",0");
		content = sb.toString();
		BatchJob batchJob = new BatchJob();
		batchJob.setSubName(action);
		batchJob.setContent(content);
		if(config!=null){
			batchJob.setSiteUID(config.getSiteUID());
		}
		batchJob.setCreateTime(new Date());
		batchJob.setCreateByUid(userUid);
		batchJob.setName(BatchJob.NAME_NURSESITE);
		batchJobRepository.save(batchJob);
		return true;
	}

	public void doJobDeleteAssetInfo(String[] params) {
		String assetUid = params[4];
		Set<String> configIds = new HashSet<String>();
		List<NurseSiteMaintainConfigItem> listItem = nurseSiteMaintainConfigItemRepository.findByAssetUid(assetUid);
		if (listItem != null && listItem.size() > 0) {
			for (int i = 0; i < listItem.size(); ++i) {
				NurseSiteMaintainConfigItem tempItem = listItem.get(i);
				configIds.add(tempItem.getParentId());
			}
			nurseSiteMaintainConfigItemRepository.deleteByAssetUid(assetUid);
		}
		if (configIds.size() > 0) {
			Iterator<String> it = configIds.iterator();
			while (it.hasNext()) {
				String id = it.next();
				if (StringUtils.isNotEmpty(id)) {
					listItem = nurseSiteMaintainConfigItemRepository.findByParentId(id);
					if (listItem != null && listItem.size() > 0) {
						deleteUnCheckedMaintainData(id, assetUid, false);
					} else {
						deleteUnCheckedMaintainData(id, assetUid, true);
						NurseSiteMaintainConfig tempConfig = nurseSiteMaintainConfigRepository.findById(id);
						if (tempConfig != null) {
							nurseSiteMaintainConfigRepository.delete(tempConfig);
						}
					}
				}
			}
		}
	}

	private void doJobUpdate(String[] params) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String configId = params[0];
		userUid = params[3];
		nurseSiteMaintainConfig = nurseSiteMaintainConfigRepository.findById(configId);
		List<NurseSiteMaintainConfigItem> listConfigItem = nurseSiteMaintainConfigItemRepository.findByParentId(configId);
		Set<String> listAssetUid = new HashSet<String>();
		for (int i = 0; i < listConfigItem.size(); ++i) {
			listAssetUid.add(listConfigItem.get(i).getAssetUid());
		}
		listCheckAssets = assetInfoRepository.fetchListByUid(listAssetUid);
		if (nurseSiteMaintainConfig != null && userUid != null && listCheckAssets != null) {
			listNewMaintainItem=loadConfigCheckItems();
			updateUnCheckedMaintainData();
		}
	}

	private void doJobCreate(String[] params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String configId = params[0];
		String strStart = params[1];
		String strEnd = params[2];
		userUid = params[3];
		nurseSiteMaintainConfig = nurseSiteMaintainConfigRepository.findById(configId);
		startBatchDate = sdf.parse(strStart);
		endBatchDate = sdf.parse(strEnd);
		List<NurseSiteMaintainConfigItem> listConfigItem = nurseSiteMaintainConfigItemRepository.findByParentId(configId);
		Set<String> listAssetUid = new HashSet<String>();
		for (int i = 0; i < listConfigItem.size(); ++i) {
			listAssetUid.add(listConfigItem.get(i).getAssetUid());
		}
		listCheckAssets = assetInfoRepository.fetchListByUid(listAssetUid);
		if (!userUid.equals(backupUserUid)) {
			orgUidList = findAccessClinicalUidByUserUid(userUid);
			backupUserUid = userUid;
		}
		if (nurseSiteMaintainConfig != null && startBatchDate != null && endBatchDate != null && userUid != null && listCheckAssets != null && orgUidList != null) {
			if(orgUidList.contains(nurseSiteMaintainConfig.getClinicalDeptUID())){
				listNewMaintainItem=loadConfigCheckItems();
				batchSaveMaintainData();
			}
		}
	}

	private void doJobComplete(String[] params, Date date) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String strStart = params[1];
		String strEnd = params[2];
		userUid = params[3];
		startBatchDate = sdf.parse(strStart);
		endBatchDate = sdf.parse(strEnd);
		Calendar ca=Calendar.getInstance();
		ca.setTime(date);
		Calendar ca2=Calendar.getInstance();
		ca2.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
		Date delayDate=ca2.getTime();
		String signature = params[4];
		String clinical = params[5];
		if (!userUid.equals(backupUserUid)) {
			orgUidList = findAccessClinicalUidByUserUid(userUid);
			backupUserUid = userUid;
		}
		List<NurseSiteMaintain> listMaintain = null;
		if (StringUtils.isEmpty(clinical)) {
			listMaintain = nurseSiteMaintainRepository.findDefaultData(orgUidList, startBatchDate, endBatchDate);
		} else {
			listMaintain = nurseSiteMaintainRepository.findDataByClinicalOrgUid(clinical, startBatchDate, endBatchDate);
		}
		if (listMaintain != null && listMaintain.size() > 0) {
			for(int i=0;i<listMaintain.size();++i){
				nurseSiteMaintainService.tBatchCompleteMaintain(listMaintain.get(i), userUid,signature, date,delayDate);
			}
		}
	}

	private void doJobDelete(String[] params) {
		String maintainId = params[4];
		nurseSiteMaintainService.nativeDeleteMaintain(maintainId);
	}

	private void batchSaveMaintainData() {
		if (nurseSiteMaintainConfig == null) {
			return;
		}
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		startCalendar.setTime(startBatchDate);
		endCalendar.setTime(endBatchDate);
		Integer type = nurseSiteMaintainConfig.getType();
		String typeValue = nurseSiteMaintainConfig.getTypeValue();
		if (StringUtils.isNotEmpty(typeValue)) {
			String arr[] = typeValue.split(",");
			if (type == 1) {
				// value,hours,1-24
				Integer intTypeValue = Integer.parseInt(typeValue);
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					for (int i = 0; i < intTypeValue; ++i) {
						Date date = null;
						try {
							date = startCalendar.getTime();
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
						saveAndDelete(date,i+1);
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
				}
			} else if (type == 2) {
				// weekday,1-7
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					for (int i = 0; i < arr.length; ++i) {
						try {
							if (startCalendar.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(arr[i])) {
								Date date=startCalendar.getTime();
								saveAndDelete(date, null);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
				}
			} else if (type == 3) {
				// month day,1-31
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					int currentDay = startCalendar.get(Calendar.DAY_OF_MONTH);
					int currentMonth = startCalendar.get(Calendar.MONTH);
					int currentValue = 1;
					for (int i = 0; i < arr.length; ++i) {
						try {
							currentValue = Integer.parseInt(arr[i]);
							if (currentDay == currentValue) {
								Date date=startCalendar.getTime();
								saveAndDelete(date, null);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
					int nextMonth = startCalendar.get(Calendar.MONTH);
					if (nextMonth != currentMonth) {
						if (currentDay < currentValue) {
							startCalendar.add(Calendar.DAY_OF_YEAR, -1);
							Date date=startCalendar.getTime();
							saveAndDelete(date, null);
							startCalendar.add(Calendar.DAY_OF_YEAR, 1);
						}
					}
				}
			}
		}
	}

	// where or
	public Map<Integer, Set<String>> findAccessUidByUserUid(String userUid) {
		List<UserOrgInfo> listUserOrgInfo = userOrgInfoRepository.findOrgUidListByUserUid(userUid);
		Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();
		if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
			for (int i = 0; i < listUserOrgInfo.size(); ++i) {
				UserOrgInfo temp = listUserOrgInfo.get(i);
				Integer type = temp.getOrgType();
				if (map.containsKey(type)) {
					map.get(type).add(temp.getOrgUid());
				} else {
					Set<String> tempSet = new HashSet<String>();
					tempSet.add(temp.getOrgUid());
					map.put(type, tempSet);
				}
			}
		}
		return map;
	}

	// where and
	public Set<String> findAccessClinicalUidByUserUid(String userUid) {
		Map<Integer, Set<String>> map = findAccessUidByUserUid(userUid);
		if (map.size() > 0) {
			Set<String> tempSet = null;
			Set<String> clinicalSet = null;
			tempSet = map.get(4);
			boolean hasClinical = false;
			if (tempSet != null) {
				hasClinical = true;
				clinicalSet = tempSet;
			}
			if (!hasClinical) {
				clinicalSet = new HashSet<String>();
				map.put(4, clinicalSet);
			}
			tempSet = map.get(3);
			if (tempSet != null) {
				List<String> re = orgInfoRepository.findClinicalUidBySiteUid(tempSet);
				if (re != null) {
					clinicalSet.addAll(re);
				}
			}
			tempSet = map.get(2);
			if (tempSet != null) {
				List<String> re = orgInfoRepository.findClinicalUidByHospitalUid(tempSet);
				if (re != null) {
					clinicalSet.addAll(re);
				}
			}
			tempSet = map.get(1);
			if (tempSet != null) {
				List<String> re = orgInfoRepository.findClinicalUidByInstitutionUid(tempSet);
				if (re != null) {
					clinicalSet.addAll(re);
				}
			}
			tempSet = map.get(0);
			if (tempSet != null) {
				List<String> re = orgInfoRepository.findClinicalUidByTenantUid(tempSet);
				if (re != null) {
					clinicalSet.addAll(re);
				}
			}
		}
		return map.get(4);
	}
	private List<NurseSiteMaintain> getMaintainDataNeedToRemove(Date scheduleDate,Integer sequence) {
		List<NurseSiteMaintain> existMaintainDataNoCheck = nurseSiteMaintainRepository.findDefaultDataUnchecked(nurseSiteMaintainConfig.getId(), scheduleDate,sequence);
		return existMaintainDataNoCheck;
	}
	private boolean isMaintainChecked(Date date,Integer sequence){
		return nurseSiteMaintainRepository.findDefaultDataChecked(date, sequence, nurseSiteMaintainConfig.getId());
	}
	private List<NurseSiteMaintainItem> cloneMaintainItem(){
		for(int i=0;i<listNewMaintainItem.size();++i){
			NurseSiteMaintainItem tempItem=listNewMaintainItem.get(i);
			tempItem.setId(null);
			List<NurseSiteMaintainData> listData=tempItem.getListData();
			for(int j=0;j<listData.size();++j){
				listData.get(j).setId(null);
			}
		}
		return listNewMaintainItem;
	}
	private NurseSiteMaintain createMaintain(Date date, Integer sequence) {
		if (nurseSiteMaintainConfig.getType() != null && nurseSiteMaintainConfig.getType() != 1) {
			sequence = 0;
		}
		List<NurseSiteMaintainItem> listItem=cloneMaintainItem();
		NurseSiteMaintain tempNurseSiteMaintain = new NurseSiteMaintain();
		Date currentDate = new Date();
		tempNurseSiteMaintain.setTenantUID(nurseSiteMaintainConfig.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(nurseSiteMaintainConfig.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(nurseSiteMaintainConfig.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(nurseSiteMaintainConfig.getSiteUID());
		tempNurseSiteMaintain.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
		tempNurseSiteMaintain.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
		tempNurseSiteMaintain.setCheckDate(null);
		tempNurseSiteMaintain.setCreateByUid(userUid);
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyByUid(userUid);
		tempNurseSiteMaintain.setModifyDate(currentDate);
		tempNurseSiteMaintain.setScheduleDate(date);
		tempNurseSiteMaintain.setSequence(sequence);
		tempNurseSiteMaintain.setStatus(0);
		tempNurseSiteMaintain.setName(nurseSiteMaintainConfig.getName());
		tempNurseSiteMaintain.setListNurseSiteMaintainItem(listItem);
		tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		while (it.hasNext()) {
			NurseSiteMaintainItem tempItem = it.next();
			tempItem.setNurseSiteMaintain(tempNurseSiteMaintain);
			tempItem.setCheckDate(null);
			tempItem.setCreateByUid(userUid);
			tempItem.setCreateDate(currentDate);
			tempItem.setModifyByUid(userUid);
			tempItem.setModifyDate(currentDate);
			tempItem.setScheduleDate(date);
			tempItem.setConfigId(nurseSiteMaintainConfig.getId());
			tempItem.setSequence(sequence);
			tempItem.setName(nurseSiteMaintainConfig.getName());
			tempItem.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
			tempItem.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
			tempItem.setStatus(0);
		}
		return tempNurseSiteMaintain;
	}

	public List<NurseSiteMaintainItem> loadConfigCheckItems() {
		List<NurseSiteMaintainItem> listMaintainItem = new ArrayList<NurseSiteMaintainItem>();
		// String strCheckItems = nurseSiteMaintainConfig.getCheckItems();
		String strCheckDetail = nurseSiteMaintainConfig.getCheckItemsDetail();
		if (StringUtils.isNotEmpty(strCheckDetail) && listCheckAssets != null) {
			// String arr2[] = strCheckItems.split(",");
			JSONArray jsonArray = JSONArray.parseArray(strCheckDetail);
			for (int i = 0; i < listCheckAssets.size(); ++i) {
				NurseSiteMaintainItem item = new NurseSiteMaintainItem();
				item.setAssetInfo(listCheckAssets.get(i));
				item.setAssetUid(listCheckAssets.get(i).getUid());
				List<NurseSiteMaintainData> listData = new ArrayList<NurseSiteMaintainData>();
				item.setListData(listData);
				for (int j = 0; j < jsonArray.size(); ++j) {
					NurseSiteMaintainData p = new NurseSiteMaintainData();
					JSONObject obj = jsonArray.getJSONObject(j);
					p.setCheckItem(obj.getString("name"));
					p.setIndex(obj.getLong("key"));
					// p.setCheckValue(false);
					p.setItem(item);
					listData.add(p);
				}
				listMaintainItem.add(item);
			}
		}
		return listMaintainItem;
	}

	public void updateUnCheckedMaintainData() {
		List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfig(nurseSiteMaintainConfig.getId());
		if (listMaintain != null && listMaintain.size() > 0) {
			for(int i=0;i<listMaintain.size();++i){
				NurseSiteMaintain tempMaintain=listMaintain.get(i);
				saveAndDelete(tempMaintain.getScheduleDate(), tempMaintain.getSequence());
			}
		}
	}

	public void deleteUnCheckedMaintainData(String configId, String assetUid, boolean all) {
		List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfig(configId);
		if (listMaintain != null && listMaintain.size() > 0) {
			nurseSiteMaintainService.tDeleteUnCheckedMaintainData(listMaintain, assetUid, all);
		}
	}




	private void saveAndDelete(Date date,Integer sequence){
		if(sequence==null){
			sequence=0;
		}
		List<NurseSiteMaintain> listRemoveMaintain=getMaintainDataNeedToRemove(date,sequence);
		if(listRemoveMaintain!=null && listRemoveMaintain.size()>0){
			for(int i=0;i<listRemoveMaintain.size();++i){
				nurseSiteMaintainService.nativeDeleteMaintain(listRemoveMaintain.get(i).getId());
			}
		}
		NurseSiteMaintain newMaintain=createMaintain(date,sequence);
		if(!isMaintainChecked(date,sequence)){
			nurseSiteMaintainRepository.save(newMaintain);
		}
	}
}
