package com.ge.apm.service.nurseSite;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetOptRecordRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetOptRecord;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ibm.icu.util.Calendar;


@Service
@Transactional
public class AssetOptRecordService {
	private final Logger log = LoggerFactory.getLogger(AssetOptRecordService.class);
	@Autowired
	AssetOptRecordRepository assetOptRecordRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	AssetInfoRepository assetInfoRepository;
	@Autowired
	OrgInfoRepository orgInfoRepository;
	@Autowired
	UaaService uaaService;
	public AssetOptRecordRepository getAssetOptRecordRepository(){
		return assetOptRecordRepository;
	}
	
	public Set<String> findAccessClinicalUidListByUserUid(String userUid){
		return uaaService.findAccessClinicalUidByUserUid(userUid);
	}
	private String generateCostTime(Date start,Date end){
		Calendar ca1=Calendar.getInstance();
		ca1.setTime(start);
		Calendar ca2=Calendar.getInstance();
		ca2.setTime(end);
		int year=ca1.fieldDifference(end, Calendar.YEAR);
		int day=ca1.fieldDifference(end, Calendar.DAY_OF_YEAR);
		int hour=ca1.fieldDifference(end, Calendar.HOUR_OF_DAY);
		int minute=ca1.fieldDifference(end, Calendar.MINUTE);
		StringBuilder sb=new StringBuilder();
		if(year>0){
			sb.append(year+"年");
		}
		if(day>0){
			sb.append(day+"天");
		}
		if(hour>0){
			sb.append(hour+"时");
		}
		if(minute>0){
			sb.append(minute+"分");
		}
		return sb.toString();
	}
	public OrgInfo fetchOrgInfoByUid(String uid){
		return orgInfoRepository.findByUid(uid);
	}
	public void supplementCostTime(List<AssetOptRecord> listRecord){
		if(listRecord!=null && listRecord.size()>0){
	        for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				//add
				Date startDate=tempRecord.getStartDate();
				Date endDate=tempRecord.getEndDate();
				if(startDate!=null && endDate!=null){
					tempRecord.setCostTime(generateCostTime(startDate, endDate));
				}
			}
		}
	}
	public void supplementOrgInfo(List<AssetOptRecord> listRecord){
		if(listRecord!=null && listRecord.size()>0){
			Set<String> setSiteOrgUids=new HashSet<String>();
			for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				setSiteOrgUids.add(tempRecord.getSiteUID());
			}
			List<OrgInfo> listOrgInfo=null;
			Map<String,OrgInfo> mapOrgInfo=new HashMap<String,OrgInfo>();
	        if (setSiteOrgUids.size() > 0) {
	        	listOrgInfo = orgInfoRepository.findByUids(setSiteOrgUids);
	        }
	        if (listOrgInfo != null) {
	            for (int i = 0; i < listOrgInfo.size(); ++i) {
	            	OrgInfo tempOrgInfo = listOrgInfo.get(i);
	            	mapOrgInfo.put(tempOrgInfo.getUid(), tempOrgInfo);
	            }
	        }
	        for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				String siteUid=tempRecord.getSiteUID();
				tempRecord.setSiteOrgInfo(mapOrgInfo.get(siteUid));
				//add
				Date startDate=tempRecord.getStartDate();
				Date endDate=tempRecord.getEndDate();
				if(startDate!=null && endDate!=null){
					tempRecord.setCostTime(generateCostTime(startDate, endDate));
				}
			}
		}
	}
	public void supplementRecord(List<AssetOptRecord> listRecord){
		if(listRecord!=null && listRecord.size()>0){
			Set<String> setAssetUid=new HashSet<String>();
			Set<String> setUserAccountUid=new HashSet<String>();
			for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				String modifyUid=tempRecord.getModifyByUid();
				setAssetUid.add(tempRecord.getAssetUid());
				setUserAccountUid.add(tempRecord.getCreateByUid());
				if(StringUtils.isNotEmpty(modifyUid)){
					setUserAccountUid.add(modifyUid);
				}
			}
			List<AssetInfo> listAssetInfo=null;
			List<UserAccount> listUserAccount=null;
			Map<String,AssetInfo> mapAssetInfo=new HashMap<String,AssetInfo>();
			Map<String,UserAccount> mapUserAccount=new HashMap<String,UserAccount>();
			if (setUserAccountUid.size() > 0) {
				listUserAccount = userAccountRepository.getSimpleByUids(setUserAccountUid);
	        }
	        if (setAssetUid.size() > 0) {
	            listAssetInfo = assetInfoRepository.getSimpleDataByUids(setAssetUid);
	        }
	        if (listUserAccount != null) {
	            for (int i = 0; i < listUserAccount.size(); ++i) {
	                UserAccount tempUser = listUserAccount.get(i);
	                mapUserAccount.put(tempUser.getUid(), tempUser);
	            }
	        }
	        if (listAssetInfo != null) {
	            for (int i = 0; i < listAssetInfo.size(); ++i) {
	                AssetInfo tempAsset = listAssetInfo.get(i);
	                mapAssetInfo.put(tempAsset.getUid(), tempAsset);
	            }
	        }
	        for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				String modifyUid=tempRecord.getModifyByUid();
				String createUid=tempRecord.getCreateByUid();
				String assetUid=tempRecord.getAssetUid();
				tempRecord.setAssetInfo(mapAssetInfo.get(assetUid));
				tempRecord.setCreateBy(mapUserAccount.get(createUid));
				if(StringUtils.isNotEmpty(modifyUid)){
					tempRecord.setModifyBy(mapUserAccount.get(modifyUid));
				}
				//add
				Date startDate=tempRecord.getStartDate();
				Date endDate=tempRecord.getEndDate();
				if(startDate!=null && endDate!=null){
					tempRecord.setCostTime(generateCostTime(startDate, endDate));
				}
			}
		}
	}
	
	
}
