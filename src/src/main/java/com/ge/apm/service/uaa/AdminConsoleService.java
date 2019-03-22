package com.ge.apm.service.uaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;

@Service
public class AdminConsoleService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	AssetInfoRepository assetInfoRepository;
	
	@Autowired
	TenantInfoRepository tenantInfoRepository;
	
	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgDao;
	
	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;
	
	@Autowired
	SysRoleRepository sysRoleRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	NurseSiteMaintainService nurseSiteMaintainService;
	
	public void addVersion(String version,String key) {
		Iterable<TenantInfo> listTenantInfo=tenantInfoRepository.findAll();
		if(listTenantInfo!=null){
			Iterator<TenantInfo> it=listTenantInfo.iterator();
			while(it.hasNext()){
				TenantInfo tempInfo=it.next();
				String strTenantInfo=tempInfo.getTenantConfig();
				if(StringUtils.isNotEmpty(strTenantInfo)){
					JSONObject jsonObj=null;
					try {
						jsonObj=JSONObject.parseObject(strTenantInfo);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(jsonObj!=null){
						String tempVersion=jsonObj.getString("edition");
						if(tempVersion.equals(version)){
							JSONArray tempArr=jsonObj.getJSONArray("menuList");
							if(tempArr!=null){
								if(!tempArr.contains(key)){
									tempArr.add(key);
									String result=jsonObj.toString();
									tempInfo.setTenantConfig(result);
									tenantInfoRepository.save(tempInfo);
								}
							}
						}
					}
				}
			}
		}
	}
	public void deleteVersion(String version,String key) {
		Iterable<TenantInfo> listTenantInfo=tenantInfoRepository.findAll();
		if(listTenantInfo!=null){
			Iterator<TenantInfo> it=listTenantInfo.iterator();
			while(it.hasNext()){
				TenantInfo tempInfo=it.next();
				String strTenantInfo=tempInfo.getTenantConfig();
				if(StringUtils.isNotEmpty(strTenantInfo)){
					JSONObject jsonObj=null;
					try {
						jsonObj=JSONObject.parseObject(strTenantInfo);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(jsonObj!=null){
						String tempVersion=jsonObj.getString("edition");
						if(tempVersion.equals(version)){
							JSONArray tempArr=jsonObj.getJSONArray("menuList");
							if(tempArr!=null){
								if(tempArr.contains(key)){
									tempArr.remove(key);
									String result=jsonObj.toString();
									tempInfo.setTenantConfig(result);
									tenantInfoRepository.save(tempInfo);
								}
							}
						}
					}
				}
			}
		}
	}
	public void updateNurseSiteDeleteAsset(){
		int limit=1000;
		int offset=0;
		List<NurseSiteMaintainConfigItem> listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteDeleteAsset(listR);
			++offset;
			listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
			logger.info("updateNurseSiteUID done.");
		}
	}
	public void updateNurseSiteStatus(){
		int limit=1000;
		int offset=0;
		Calendar ca=Calendar.getInstance();
		Integer []intArr=new Integer[2];
		intArr[0]=2;
		intArr[1]=3;
		List<NurseSiteMaintain> listR=nurseSiteMaintainRepository.findByLimit(limit, offset,ca.getTime(),Arrays.asList(intArr));
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteStatus(listR);
			++offset;
			listR=nurseSiteMaintainRepository.findByLimit(limit, offset,ca.getTime(),Arrays.asList(intArr));
			logger.info("updateNurseSiteUID done.");
		}
	}
	public void updateNurseSiteUID(){
		int limit=1000;
		int offset=0;
		List<NurseSiteMaintainConfigItem> listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteUID(listR);
			++offset;
			listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
			logger.info("updateNurseSiteUID done.");
		}
	}
	public void tUpdateNurseSiteStatus(List<NurseSiteMaintain> listR){
		for(int i=0;i<listR.size();++i){
			NurseSiteMaintain tempMaintain=listR.get(i);
			nurseSiteMaintainService.updateMaintain(tempMaintain,tempMaintain.getModifyDate(),tempMaintain.getModifyBy());
			nurseSiteMaintainRepository.save(tempMaintain);
		}
	}
	@Transactional
	public void tUpdateNurseSiteDeleteAsset(List<NurseSiteMaintainConfigItem> listR){
		Set<String> setAssetUid=new HashSet<String>();
		Map<String,AssetInfo> mapAsset=new HashMap<String,AssetInfo>();
		Map<String,AssetInfo> mapConfigIdAsset=new HashMap<String,AssetInfo>();
		List<NurseSiteMaintainConfigItem> listNew=new ArrayList<NurseSiteMaintainConfigItem>();
		Set<String> setConfigId=new HashSet<String>();
		List<NurseSiteMaintain> listNoCheckMaintain=new ArrayList<NurseSiteMaintain>();
		List<NurseSiteMaintainConfig> listConfig=null;
		List<NurseSiteMaintain> listMaintain=null;
		List<AssetInfo> listAssetInfo=null;
		for(int i=0;i<listR.size();++i){
			setAssetUid.add(listR.get(i).getAssetUid());
		}
		if(setAssetUid.size()>0){
			listAssetInfo=assetInfoRepository.getByUids(setAssetUid);
		}
		if(listAssetInfo!=null && listAssetInfo.size()>0){
			for(int i=0;i<listAssetInfo.size();++i){
				AssetInfo tempAsset=listAssetInfo.get(i);
				if(tempAsset.getStatus()==4 || tempAsset.getIsValid()==false || tempAsset.getIsDeleted()==true){
					mapAsset.put(tempAsset.getUid(), tempAsset);
				}
			}
		}
		if(mapAsset.size()>0){
			for(int i=0;i<listR.size();++i){
				NurseSiteMaintainConfigItem tempItem=listR.get(i);
				AssetInfo tempAsset=mapAsset.get(tempItem.getAssetUid());
				if(tempAsset==null){
					continue;
				}
				nurseSiteMaintainService.deleteAssetUid(tempAsset.getUid());
			}
		}
	}
	@Transactional
	public void tUpdateNurseSiteUID(List<NurseSiteMaintainConfigItem> listR){
		Set<String> setAssetUid=new HashSet<String>();
		List<AssetInfo> listAssetInfo=null;
		Map<String,AssetInfo> mapAsset=new HashMap<String,AssetInfo>();
		Map<String,AssetInfo> mapConfigIdAsset=new HashMap<String,AssetInfo>();
		List<NurseSiteMaintainConfigItem> listNew=new ArrayList<NurseSiteMaintainConfigItem>();
		Set<String> setConfigId=new HashSet<String>();
		List<NurseSiteMaintainConfig> listConfig=null;
		List<NurseSiteMaintain> listMaintain=null;
		for(int i=0;i<listR.size();++i){
			setAssetUid.add(listR.get(i).getAssetUid());
		}
		if(setAssetUid.size()>0){
			listAssetInfo=assetInfoRepository.getByUids(setAssetUid);
		}
		if(listAssetInfo!=null && listAssetInfo.size()>0){
			for(int i=0;i<listAssetInfo.size();++i){
				AssetInfo tempAsset=listAssetInfo.get(i);
				mapAsset.put(tempAsset.getUid(), tempAsset);
			}
		}
		if(mapAsset.size()>0){
			for(int i=0;i<listR.size();++i){
				NurseSiteMaintainConfigItem tempItem=listR.get(i);
				AssetInfo tempAsset=mapAsset.get(tempItem.getAssetUid());
				if(tempAsset==null){
					continue;
				}
				mapConfigIdAsset.put(tempItem.getParentId(), tempAsset);
				if((!tempAsset.getTenantUID().equals(tempItem.getTenantUID())) || (!tempAsset.getInstitutionUID().equals(tempItem.getInstitutionUID()))||(!tempAsset.getHospitalUID().equals(tempItem.getHospitalUID()))||(!tempAsset.getSiteUID().equals(tempItem.getSiteUID()))){
					listNew.add(tempItem);
					tempItem.setTenantUID(tempAsset.getTenantUID());
					tempItem.setInstitutionUID(tempAsset.getInstitutionUID());
					tempItem.setHospitalUID(tempAsset.getHospitalUID());
					tempItem.setSiteUID(tempAsset.getSiteUID());
				}
			}
		}
		if(listNew.size()>0){
			nurseSiteMaintainConfigItemRepository.save(listNew);
			logger.info("update nurseSiteMaintainConfigItem size:"+listNew.size());
			for(int i=0;i<listNew.size();++i){
				setConfigId.add(listNew.get(i).getParentId());
			}
			if(setConfigId.size()>0){
				listConfig=nurseSiteMaintainConfigRepository.fetchListByIds(setConfigId);
				listMaintain=nurseSiteMaintainRepository.findByConfig(setConfigId);
			}
			if(listConfig!=null && listConfig.size()>0){
				for(int i=0;i<listConfig.size();++i){
					NurseSiteMaintainConfig tempConfig=listConfig.get(i);
					AssetInfo tempAsset=mapAsset.get(tempConfig.getId());
					if(tempAsset==null){
						continue;
					}
					tempConfig.setTenantUID(tempAsset.getTenantUID());
					tempConfig.setInstitutionUID(tempAsset.getInstitutionUID());
					tempConfig.setHospitalUID(tempAsset.getHospitalUID());
					tempConfig.setSiteUID(tempAsset.getSiteUID());
				}
				nurseSiteMaintainConfigRepository.save(listConfig);
				logger.info("update nurseSiteMaintainConfig size:"+listConfig.size());
			}
			if(listMaintain!=null && listMaintain.size()>0){
				for(int i=0;i<listMaintain.size();++i){
					NurseSiteMaintain tempMaintain=listMaintain.get(i);
					AssetInfo tempAsset=mapAsset.get(tempMaintain.getConfigId());
					if(tempAsset==null){
						continue;
					}
					tempAsset.setTenantUID(tempAsset.getTenantUID());
					tempAsset.setInstitutionUID(tempAsset.getInstitutionUID());
					tempAsset.setHospitalUID(tempAsset.getHospitalUID());
					tempAsset.setSiteUID(tempAsset.getSiteUID());
				}
				nurseSiteMaintainRepository.save(listMaintain);
				logger.info("update nurseSiteMaintain size:"+listMaintain.size());
			}
		}
	}
}
