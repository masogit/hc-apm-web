package com.ge.apm.service.nurseSite;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.ge.apm.service.nurseSite.export.AssetOptRecordExport;
import com.ge.apm.service.nurseSite.export.AssetOptRecordExportUtil;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

	public List<String> findAccessClinicalUidListByUserUid(String userUid){
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public List<AssetOptRecord> getAssetOptRecordList(String assetUid){
		if(StringUtils.isEmpty(assetUid)) return null;
		List<AssetOptRecord> assetOptRecordListLimit5 = assetOptRecordRepository.getAssetOptRecordListLimit5(assetUid);
		assetOptRecordListLimit5.forEach(item->{
			if(item.getEndDate() != null){
				item.setCostTime(generateCostTime(item.getStartDate(),item.getEndDate()));
			}
			UserAccount createBy=null ;
			if((createBy=item.getCreateBy()) != null){
				item.setCreateByName(createBy.getName());
			}else{
				String createByUid = item.getCreateByUid();
				if(StringUtils.isNotEmpty(createByUid)){
					createBy= userAccountRepository.getByUid(createByUid);
					item.setCreateByName(createBy.getName());
				}
			}
			UserAccount modifyBy=null ;
			if((modifyBy=item.getModifyBy()) != null){
				item.setModifyByName(modifyBy.getName());
			}else{
				String modifyByUid = item.getModifyByUid();
				if(StringUtils.isNotEmpty(modifyByUid)){
					modifyBy= userAccountRepository.getByUid(modifyByUid);
					item.setModifyByName(modifyBy.getName());
				}
			}

		});
		return assetOptRecordListLimit5;
	}
	public static String[] basicInfo={"院区","科室","设备名称","设备编号","序列号","型号"};
	@Transactional(readOnly = true)
	public StreamedContent exportAllWoByAssetInfoId(AssetInfo assetInfo, Date startDate, Date endDate){
		InputStream inputStream =null;
		try {
			List<AssetOptRecord> bySearchFilter = assetOptRecordRepository.getAseetOptRecordListByDate(assetInfo.getUid(),startDate,endDate);
			List<AssetOptRecordExport> collect =null;
			if(CollectionUtils.isNotEmpty(bySearchFilter)){
					collect = bySearchFilter.stream().map(item -> {
					AssetOptRecordExport export = new AssetOptRecordExport();
					BeanUtils.copyProperties(item, export);
						String createByUid = export.getCreateByUid();
						if(StringUtils.isNotEmpty(createByUid)){
						UserAccount byUid = userAccountRepository.getByUid(createByUid);
						if(byUid != null){
							export.setCreateByUid(byUid.getName());
						}
					}
					String modifyByUid = export.getModifyByUid();
					if(StringUtils.isNotEmpty(modifyByUid) && item.getEndDate() != null){
						UserAccount byUid = userAccountRepository.getByUid(modifyByUid);
						if(byUid != null){
							export.setModifyByUid(byUid.getName());
						}
					}else{
						export.setModifyByUid("");
					}
					if(item.getStartDate() != null && item.getEndDate()!= null){
						export.setCostTime(generateCostTime(item.getStartDate(),item.getEndDate()));
					}else{
						export.setCostTime("");
					}
					return export;
				}).collect(Collectors.toList());
			}
			List<Map<String, String>> maps = setAssetBasicInfo(assetInfo);
			inputStream = AssetOptRecordExportUtil.export(maps,collect);
			return new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", "AssetOptRecordData.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
	private List<Map<String,String>> setAssetBasicInfo(AssetInfo assetInfo){
		List<Map<String,String>> result=new ArrayList<>();
		for (int i = 0; i < basicInfo.length; i++) {
			Map<String,String> map=new HashMap<>();
			map.put("key",basicInfo[i]);
			switch (basicInfo[i]){
				case "院区":
					OrgInfo siteOrg = assetInfo.getSiteOrg();
					if(siteOrg != null){
						map.put("value",siteOrg.getName());
					}else{
						OrgInfo byUid = orgInfoRepository.findByUid(assetInfo.getSiteUID());
						if(byUid != null){
							map.put("value",byUid.getName());
						}else{
							map.put("value","");
						}
					}
					break;
				case "科室":
					map.put("value",assetInfo.getClinicalDeptName());
					break;
				case "设备名称":
					map.put("value",assetInfo.getName());
					break;
				case "设备编号":
					map.put("value",assetInfo.getDepartNum());
					break;
				case "序列号":
					map.put("value",assetInfo.getSerialNum());
					break;
				case "型号":
					map.put("value",assetInfo.getFunctionType());
					break;
				default:
					map.put("value","");
					break;
			}
			result.add(map);
		}
		return result;
	}
}
