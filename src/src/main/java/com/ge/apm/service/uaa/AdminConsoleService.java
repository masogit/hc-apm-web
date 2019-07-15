package com.ge.apm.service.uaa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BatchJobRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SysRoleRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BatchJob;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
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
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	NurseSiteMaintainService nurseSiteMaintainService;
	
	@Autowired
	I18nMessageRepository i18nRepository;
	
	@Autowired
	BatchJobRepository batchJobRepository;
	
	@Autowired
    RestTemplate template;
	
	@Autowired
	BlobFileService blobFileService;
	
	@Autowired
	BlobObjectRepository blobObjectRepository;
	
	@Value("#{systemEnvironment['GATEWAY_URL'] ?: urlProperties.defaultGateway}")
    private String baseURL;
	
	private static final String MSG_TYPE_VERSION = "tenantVersionMenu";
	
	public List<SysRole> getSysRole(){
		return sysRoleRepository.find();
	}
	public void saveSysRole(SysRole s){
		sysRoleRepository.save(s);
	}
	
	public List<I18nMessage> getI18Menus(){
		List<I18nMessage> list18 = i18nRepository.findI18nByType(MSG_TYPE_VERSION);
		return list18;
	}
	
	public void addVersion(String version,String containsLabelKey,String key) {
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
								if((!tempArr.contains(key)) && tempArr.contains(containsLabelKey)){
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
	public void deleteVersion(String version,String containsLabelKey,String key) {
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
								if(tempArr.contains(key) && tempArr.contains(containsLabelKey)){
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
			logger.info("updateNurseSiteDeleteAsset done.");
		}
	}
	public void updateNurseSiteStatus(){
		int limit=1000;
		int offset=0;
		Calendar ca=Calendar.getInstance();
		ca.add(Calendar.DAY_OF_YEAR, -1);
		Date date=ca.getTime();
		List<NurseSiteMaintainItem> listR=nurseSiteMaintainItemRepository.findError5ByLimit(limit, offset,date);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteStatus(listR,2,5);
			++offset;
			listR=nurseSiteMaintainItemRepository.findError5ByLimit(limit, offset,date);
		}
		offset=0;
		listR=nurseSiteMaintainItemRepository.findError6ByLimit(limit, offset,date);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteStatus(listR,2,6);
			++offset;
			listR=nurseSiteMaintainItemRepository.findError6ByLimit(limit, offset,date);
		}
		offset=0;
		listR=nurseSiteMaintainItemRepository.findError7ByLimit(limit, offset,date);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteStatus(listR,2,7);
			++offset;
			listR=nurseSiteMaintainItemRepository.findError7ByLimit(limit, offset,date);
		}
		offset=0;
		listR=nurseSiteMaintainItemRepository.findError8ByLimit(limit, offset,date);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteStatus(listR,3,8);
			++offset;
			listR=nurseSiteMaintainItemRepository.findError8ByLimit(limit, offset,date);
		}
		logger.info("updateNurseSiteStatus done.");
	}
	public void updateNurseSiteUID(){
		int limit=1000;
		int offset=0;
		List<NurseSiteMaintainConfigItem> listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
		while(listR!=null && listR.size()>0){
			tUpdateNurseSiteUID(listR);
			++offset;
			listR=nurseSiteMaintainConfigItemRepository.findByLimit(limit, offset);
		}
		logger.info("updateNurseSiteUID done.");
	}
	public void tUpdateNurseSiteStatus(List<NurseSiteMaintainItem> listR,int originStatus,int newStatus){
		Set<String> setIds=new HashSet<String>();
		for(int i=0;i<listR.size();++i){
			NurseSiteMaintainItem tempMaintainItem=listR.get(i);
			setIds.add(tempMaintainItem.getParentId());
		}
		if(setIds.size()>0){
			nurseSiteMaintainRepository.updateErrorMaintain(setIds, originStatus, newStatus);
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
	
	public void dailyUpdateNurseSiteJob(Date date){
		BatchJob batchJob = new BatchJob();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		batchJob.setContent(sdf.format(date));
		batchJob.setSubName("dailyUpdate");
		batchJob.setCreateTime(new Date());
		batchJob.setName(BatchJob.NAME_NURSESITE);
		batchJobRepository.save(batchJob);
	}
	
	public void deleteObjectHub(){
		String gatewayURL = getGatewayURL(GatewayURL.SEARCH_FILE);
		Set<String> setSkip=new HashSet<String>();
		while(true){
			int page=0;
			int pageSize=1000;
			boolean blnDelete=false;
			String tempGateway=gatewayURL+"?page="+page+"&pageSize="+pageSize;
	        try {
	        	ResponseEntity<LinkedHashMap> response =template.getForEntity(tempGateway,LinkedHashMap.class);
	            if (isOkay(response)) {
	                LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
	                Collection<LinkedHashMap<String, Object>> listData = (Collection<LinkedHashMap<String, Object>>) responseMap.get("data");
	                try {
	                	if(listData!=null && listData.size()>0){
	                		for(Map<String,Object> objMap:listData){
	                			String id=(String)objMap.get("id");
	                			if(!setSkip.contains(id)){
	                				List<V2_BlobObject> listObj=blobObjectRepository.findAllByObjectStorageId(id);
		                			if(listObj==null || listObj.size()==0){
		                				blnDelete=true;
		                				logger.info("delete storageId:"+id);
		                				blobFileService.deleteBlobFilesByStorageId(id);
		                			}
		                			else{
		                				setSkip.add(id);
		                			}
	                			}
	                		}
	                	}
	                	else{
	                		break;
	                	}
	    			} catch (Exception e) {
	    				// TODO: handle exception
	    				logger.error(e.getMessage());
	    			}
	            }
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
	        if(!blnDelete){
	        	++page;
	        }
	        if(setSkip.size()>100000){
	        	setSkip.clear();
	        }
		}
	}
	public String getGatewayURL(GatewayURL url) {
        return baseURL + url.toString();
    }
	private Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();
        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }
}
