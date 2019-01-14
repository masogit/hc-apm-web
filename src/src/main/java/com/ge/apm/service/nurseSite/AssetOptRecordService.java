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
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetOptRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;


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
	UaaService uaaService;
	public AssetOptRecordRepository getAssetOptRecordRepository(){
		return assetOptRecordRepository;
	}
	
	public Set<String> findAccessClinicalUidListByUserUid(String userUid){
		return uaaService.findAccessClinicalUidListByUserUid(userUid);
	}
	public void supplementCostTime(List<AssetOptRecord> listRecord){
		if(listRecord!=null && listRecord.size()>0){
	        for(int i=0;i<listRecord.size();++i){
				AssetOptRecord tempRecord=listRecord.get(i);
				//add
				Date startDate=tempRecord.getStartDate();
				Date endDate=tempRecord.getEndDate();
				if(startDate!=null && endDate!=null){
					long costTime=endDate.getTime()-startDate.getTime();
					Date costDate=new Date();
					costDate.setTime(costTime);
					tempRecord.setCostTime(costDate);
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
					long costTime=endDate.getTime()-startDate.getTime();
					Date costDate=new Date();
					costDate.setTime(costTime);
					tempRecord.setCostTime(costDate);
				}
			}
		}
	}
	
	
}
