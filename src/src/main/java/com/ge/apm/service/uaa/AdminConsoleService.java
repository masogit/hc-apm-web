package com.ge.apm.service.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AdminConsoleMapper;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.MvsIbMappingMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.dto.AssetDto;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.reminder.WechatParam;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.PinyinUtil;
import com.ge.apm.view.SparePart.OrderNumberGenerator;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmAdminActiveFeature;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.service.DbMessageSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ge.healthcare.cn.common.cons.ApmFunction.*;

@Service
public class AdminConsoleService extends MicroServiceInvoker {
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
	CustomRoleRepository customRoleRepository;

	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;

	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;

	@Autowired
	AssetLifecycleEventRepository assetLifecycleEventRepository;

	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

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

	@Autowired
	private AssetHistoryRepository assetHistoryRepository;

	@Autowired
	private WorkflowConfigRepository workflowConfigRepository;

	@Autowired
	private ConfigItemRepository configItemRepository;

	@Autowired
	private ConfigItemValueRepository configItemValueRepository;

	@Autowired
	private UpdateTenantFeatureService updateTenantFeatureService;

	@Autowired
	private ReminderRuleRepository reminderRuleRepository;

	@Autowired
	private StockRecordRepository stockRecordRepository;

	@Autowired
	private AssetInfoMapper assetInfoMapper;

	@Autowired
	private V2_PmOrderRepository pmOrderRepository;

	@Autowired
	private PmOrderRuleRepository pmOrderRuleRepository;

	@Autowired
	private RelRuleAssetRepository relRuleAssetRepository;

	@Autowired
	private AdminConsoleMapper adminConsoleMapper;

	@Autowired
	private AdminFeatureActiveRepository adminFeatureActiveRepository;

	@Autowired
	private TenantOptionConfigRepository tenantOptionConfigRepository;

	@Autowired
	private AdverseEventsRepository adverseEventsRepository;

	@Autowired
	private SharedAssetRequestRepository sharedAssetRequestRepository;

	@Autowired
	private AuditCaseRepository auditCaseRepository;

	@Autowired
	private BudgetApplicationRepository budgetApplicationRepository;

	@Autowired
	private MultiLevelApprovalConfigRepository multiLevelApprovalConfigRepository;

	@Autowired
	private UaaService uaaService;

	@Autowired
	AssetTypeGroupRepository assetTypeGroupDao;

	@Autowired
	private MvsIbMappingMapper mvsIbMappingMapper;

	@Autowired
	private CustomConfigRepository customConfigDao;

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
	public void batchUpdateAssetLifecycleEvent(Date start,Date end){
		int limit=1000;
		int offset=0;
		Calendar currentCa = Calendar.getInstance();
		Set<String> listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,3);
		while(listR!=null && listR.size()>0){
			List<AssetLifecycleEvent> listR2=assetLifecycleEventRepository.findByAssetUid(listR,3);
			if(listR2!=null && listR2.size()>0){
				ignoreErrorDate(listR2,currentCa.getTime());
			}
			++offset;
			listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,3);
			logger.info("batchUpdateAssetLifecycleEvent done.");
		}
		offset=0;
		listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,4);
		while(listR!=null && listR.size()>0){
			List<AssetLifecycleEvent> listR2=assetLifecycleEventRepository.findByAssetUid(listR,4);
			if(listR2!=null && listR2.size()>0){
				ignoreErrorDate(listR2,currentCa.getTime());
			}
			++offset;
			listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,4);
			logger.info("batchUpdateAssetLifecycleEvent done.");
		}
		offset=0;
		listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,5);
		while(listR!=null && listR.size()>0){
			List<AssetLifecycleEvent> listR2=assetLifecycleEventRepository.findByAssetUid(listR,5);
			if(listR2!=null && listR2.size()>0){
				ignoreErrorDate(listR2,currentCa.getTime());
			}
			++offset;
			listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,5);
			logger.info("batchUpdateAssetLifecycleEvent done.");
		}
		offset=0;
		listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,6);
		while(listR!=null && listR.size()>0){
			List<AssetLifecycleEvent> listR2=assetLifecycleEventRepository.findByAssetUid(listR,6);
			if(listR2!=null && listR2.size()>0){
				ignoreErrorDate(listR2,currentCa.getTime());
			}
			++offset;
			listR=assetLifecycleEventRepository.findAssetUidByLimit(limit, offset,6);
			logger.info("batchUpdateAssetLifecycleEvent done.");
		}
	}

	public void deleteInvalidOrder(){
		nurseSiteMaintainDataRepository.deleteInvalid();
		nurseSiteMaintainItemRepository.deleteInvalid();
		nurseSiteMaintainRepository.deleteInvalid();
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
	private void ignoreErrorDate(List<AssetLifecycleEvent> tempList,Date currentDate) {
		Set<String> listErrorDateEvent=new HashSet<String>();
		Set<String> listCorrectDateEvent=new HashSet<String>();
		if (tempList != null) {
			Map<String, Date> mapCloseDesc = new HashMap<String, Date>();
			Map<String, Date> mapOpenAsc = new HashMap<String, Date>();
			if (tempList != null && tempList.size() > 0) {
				for (int i = 0; i < tempList.size(); ++i) {
					AssetLifecycleEvent tempEvent = tempList.get(i);
					String assetUid = tempEvent.getAssetUid();
					// 1:open,2:closed,3:cancelled
					Integer status = tempEvent.getStatus();
					Date closeTime = tempEvent.getCreateTime();
					Date startTime = tempEvent.getScheduleDate();
					if (closeTime != null && closeTime.after(currentDate)) {
						if(tempEvent.getCheckStatus().equals(0)){
							listErrorDateEvent.add(tempEvent.getId());
						}
						tempEvent.setCreateTime(null);
						closeTime=null;
					}
					if(status.equals(1)){
						if (startTime != null) {
							Calendar ca = Calendar.getInstance();
							ca.setTime(startTime);
							ca.add(Calendar.DAY_OF_YEAR, 60);
							if (ca.getTime().before(currentDate)) {
								if(tempEvent.getCheckStatus().equals(0)){
									listErrorDateEvent.add(tempEvent.getId());
								}
								tempEvent.setScheduleDate(null);
								startTime=null;
							}
						}
					}
					if(status.equals(2)){
						if(closeTime!=null){
							if(mapCloseDesc.get(assetUid)==null){
								mapCloseDesc.put(assetUid, closeTime);
							}
						}
					}
					if(startTime!=null){
						mapOpenAsc.put(assetUid, startTime);
					}
				}
				for (int i = 0; i < tempList.size(); ++i) {
					AssetLifecycleEvent tempEvent = tempList.get(i);
					String assetUid = tempEvent.getAssetUid();
					// 1:open,2:closed,3:cancelled
					Integer status = tempEvent.getStatus();
					Date startTime = tempEvent.getScheduleDate();
					Date closeDesc = mapCloseDesc.get(assetUid);
					if(status.equals(1)){
						if (startTime != null&&closeDesc != null && closeDesc.after(startTime)) {
							if(tempEvent.getCheckStatus().equals(0)){
								listErrorDateEvent.add(tempEvent.getId());
							}
							tempEvent.setScheduleDate(null);
						}
					}
				}
			}
		}
		if(listErrorDateEvent.size()>0){
			assetLifecycleEventRepository.recordErrorData(listErrorDateEvent);
		}
		for(int i=0;i<tempList.size();++i){
			if(!listErrorDateEvent.contains(tempList.get(i).getId())){
				listCorrectDateEvent.add(tempList.get(i).getId());
			}
		}
		if(listCorrectDateEvent.size()>0){
			assetLifecycleEventRepository.recordCorrectData(listCorrectDateEvent);
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
	public Boolean isOkay(ResponseEntity res) {

		if (!res.getStatusCode().equals(HttpStatus.OK)) {
			return false;
		}
		LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();
		return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
	}

	public void updatePmOrders(String userUid,String date) {
		String token = UserContextService.getAccessToken();
		HttpEntity<String> requestEntity = new HttpEntity<>(createHttpHeaders(token));
		String gatewayURL = getGatewayURL(GatewayURL.CREATE_PM_ORDER_BY_CONTINUEFAILEDPMORDER);
		String url = gatewayURL.concat("?userUid=").concat(userUid);
		url = url.concat("&date=").concat(date);
		ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.PUT, requestEntity, LinkedHashMap.class,userUid,date);
		if (isOkay(response)) {
			List<V2_PmOrder> pmOrders = (List<V2_PmOrder>) response.getBody();
			logger.info("批量创建保养失败记录：{}",pmOrders);
		} else {
		}
	}


	public void batchUpdateAssetHistoryEvent() {
		try {
			logger.info("batchUpdateAssetHistoryEvent start .......");
			long startTimeUpdate = System.currentTimeMillis();

			assetHistoryRepository.batchUpdateAssetHistoryEvent();
			long endTimeUpdate = System.currentTimeMillis();

			logger.info("batchUpdateAssetHistoryEvent end .......耗时:{}ms",endTimeUpdate-startTimeUpdate);
			logger.info("batchInsertAssetHistoryEvent start .......");
			int pageNum=0;
			int pageSize=3000;
			Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			long startTime = System.currentTimeMillis();
			List<AssetHistory> assetHistorys =new ArrayList<>(2000);
			while (true){
				List<AssetInfo> assetInfos=assetInfoRepository.queryAssetHistoryNotExistsInsert();
				if(CollectionUtils.isEmpty(assetInfos)){
					break;
				}
				for(AssetInfo assetInfo:assetInfos){
					if(assetInfo.getHospital()!=null){
						assetInfo.setAssetDeptName(assetInfo.getHospital().getName());
					}
					AssetHistory assetHistory = new AssetHistory();
					assetHistory.setAssetUid(assetInfo.getUid());
					assetHistory.setAssetName(assetInfo.getName());
					assetHistory.setFinancingNum(assetInfo.getFinancingNum());
					assetHistory.setJsonData("[]");
					assetHistory.setCreatedDate(new Date());
					assetHistory.setCreatedBy(assetInfo.getCreatedBy()!=null?assetInfo.getCreatedBy():"admin");
					assetHistory.setSiteUID(assetInfo.getSiteUID());
					assetHistory.setHospitalUID(assetInfo.getHospitalUID());
					assetHistory.setTenantUID(assetInfo.getTenantUID());
					assetHistory.setInstitutionUID(assetInfo.getInstitutionUID());
					assetHistory.setClinicalDeptUid(assetInfo.getClinicalDeptUID());

					assetInfo.setHospital(null);
//					assetInfo.setQrCodeLib(null);
					assetInfo.setAssetTags(null);
					assetInfo.setBrand(null);
					assetInfo.setTags(null);
					String assetJson = g.toJson(assetInfo);
					assetHistory.setAssetJson(assetJson);

					assetHistorys.add(assetHistory);
				}
				if(!CollectionUtils.isEmpty(assetHistorys)){
					assetHistoryRepository.save(assetHistorys);
				}
				assetHistorys.clear();
				pageNum=pageNum+pageSize;
				Thread.sleep(10000);
			}
			long endTime = System.currentTimeMillis();
			logger.info("batchInsertAssetHistoryEvent end 添加设备到历史数据运行时间：{} ms",endTime-startTime);

		}catch (Exception e){
			logger.info("batchUpdateAssetHistoryEvent error e:",e);
		}
	}
	public void batchSynchronizationConfig() {
//		saveDefaultValue("assetInstallApprove",3,"false",3,"安装入库完成是否审批","安装入库完成是否审批");
		saveLevelValue("whetherToEnableUnattended","whethertoenableunattended");
		saveLevelValue("appointmentStatusLockedTime","appointmentstatuslockedtime");
	}

	public void analyseMenus() {
		//analyse tenant_config
		Iterable<TenantInfo> tenantInfos = tenantInfoRepository.findAll();
		//find menus by tenant_config
		for (TenantInfo tenantInfo : tenantInfos) {
			analyseMenu(tenantInfo);
		}
		//analyse custom_role
		List<CustomRole> customRoles = customRoleRepository.find();
		customRoles.forEach(customRole -> {
			analyseCustomMenu(customRole);
		});
	}

	public void analyseMenu(TenantInfo tenantInfo) {
		if (StringUtils.isNotEmpty(tenantInfo.getTenantConfig())) {
			JSONObject jsonObject = JSON.parseObject(tenantInfo.getTenantConfig());
			String version = jsonObject.getString("edition");
			List<String> finalEnumNames = jsonObject.getObject("menuList", List.class);
			List<String> requiredEnums = new ArrayList<>();
			for (AssetManagementEnum e : AssetManagementEnum.values()) {
				if (e.isRequired()) {
					if (e.getIgnore().contains(version)) {
						continue;
					}
					if (e.isChekConfig()) {
						if (chekConfig(e, tenantInfo)) {
							requiredEnums.add(e.name());
						}
						continue;
					}
					requiredEnums.add(e.name());
					continue;
				}
				if (e.name().equals(AssetManagementEnum.REPAIR_APPROVE_PROCESS.name())) {
					if (tenantInfo.getName().contains("积水潭")) {
						requiredEnums.add(e.name());
						continue;
					}
				}
				if (finalEnumNames.contains(e.getOldName())) {
					if (e.isChekConfig()) {
						if (chekConfig(e, tenantInfo)) {
							requiredEnums.add(e.name());
						}
					} else {
						requiredEnums.add(e.name());
					}
					continue;
				}
			}
			tenantInfo.setFeatureList(JSONObject.toJSON(requiredEnums).toString());
			tenantInfoRepository.save(tenantInfo);
		}
	}

	public void analyseCustomMenu(CustomRole customRole) {
		if (StringUtils.isNotEmpty(customRole.getWebAuthorities())) {
			List<String> functionList = analyseCustomRole(customRole.getWebAuthorities());
			customRole.setFunctionList(JSONObject.toJSON(functionList).toString());
			customRoleRepository.save(customRole);
		}
	}

	private boolean chekConfig(AssetManagementEnum e, TenantInfo tenantInfo) {
		Method method = ReflectionUtils.findMethod(e.getChekClass(), e.getChekMethod(), TenantInfo.class);
		Boolean result = (Boolean) ReflectionUtils.invokeMethod(method, updateTenantFeatureService, tenantInfo);
		return result == e.isExpectedValue();
	}

	private List<String> analyseCustomRole(String webAuth) {
		List<String> functionList = new ArrayList<>();
		List<Map<String, Object>> authList = new Gson().fromJson(webAuth, new TypeToken<List<Map<String, Object>>>() {}.getType());
		for (Map<String, Object> auth : authList) {
			String name = (String) auth.get("v");
			boolean create = (boolean) auth.get("a");
			boolean view = (boolean) auth.get("s");
			boolean delete = (boolean) auth.get("d");
			boolean importData = (boolean) auth.get("i");
			boolean process = (boolean) auth.get("m");
			boolean exportData = (boolean) auth.get("o");
			switch (name) {
				case "日常检查":
					if (view) {
						functionList.add(NURSESITE_VIEW.name());
					}
					if (process) {
						functionList.add(NURSESITE_CHECK.name());
					}
					if (create) {
						functionList.add(NURSESITE_CREATE.name());
					}
					if (delete) {
						functionList.add(NURSESITE_DELETE.name());
					}
					break;
				case "日常检查设置":
					if (view) {
						functionList.add(NURSESITE_CONFIGITEM_VIEW.name());
					}
					if (process || create || delete) {
						functionList.add(NURSESITE_CONFIGITEM_EDIT.name());
					}
					break;
				case "日常检查报表":
					if (view || exportData) {
						functionList.add(NURSESITE_HISTORY_VIEW.name());
					}
					break;
				case "开关机登记":
					if (view || exportData) {
						functionList.add(NURSESITE_OPERATE_RECORD_EDIT.name());
					}
					break;
				case "计量信息":
					if (view || exportData) {
						functionList.add(MEASURE_INFO_VIEW.name());

					}
					if (process || create || delete) {
						functionList.add(MEASURE_INFO_EDIT.name());
					}
					break;
				case "资产报废":
					if (view) {
						functionList.add(ASSET_SCRAP_VIEW.name());
						functionList.add(ASSET_SCRAPED_VIEW.name());
					}
					if (process || create) {
						functionList.add(ASSET_SCRAP_EDIT.name());
						functionList.add(ASSET_SCRAPED_EDIT.name());
						functionList.add(ASSET_SCRAP_EVALUATE.name());
						functionList.add(ASSET_SCRAPED.name());
					}
					if (delete) {
						functionList.add(ASSET_SCRAP_DELETE.name());
						functionList.add(ASSET_SCRAPED_DELETE.name());
					}
					break;
				case "资产信息":
					if (view || exportData) {
						functionList.add(ASSET_INFO_READ.name());
					}
					if (create || process) {
						functionList.add(ASSET_INFO_EDIT.name());
					}
					if (delete) {
						functionList.add(ASSET_INFO_DELETE.name());
					}
					break;
			}
		}
		return functionList;
	}

	public void batchUpdateAssetApprove() {
		List<String> allAssetApprove = workflowConfigRepository.findAllAssetApprove();
		allAssetApprove.forEach(assetApprove -> {
			if (StringUtils.isNotEmpty(assetApprove)) {
				UserAccount userAccount = userAccountRepository.getByUid(assetApprove);
				CustomRole customRole = buildCustomRole(userAccount);
				UserRole userRole = new UserRole();
				userRole.setUserId(userAccount.getId());
				userRole.setRoleId(customRole.getId());
				userRole.setRoleType(1);
				userRoleRepository.save(userRole);
			}
		});
	}

	private CustomRole buildCustomRole(UserAccount account) {
		CustomRole customRole = customRoleRepository.findByTenantUIDAndName(account.getTenantUID(), "资产审核");
		if (customRole != null) return customRole;
		customRole = new CustomRole();
		customRole.setName("资产审核");
		customRole.setCreatedBy("system");
		customRole.setCreatedDate(new Date());
		customRole.setLastModifiedBy("system");
		customRole.setHospitalUID(account.getHospitalUID());
		customRole.setInstitutionUID(account.getInstitutionUID());
		customRole.setLastModifiedDate(new Date());
		customRole.setRoleDesc("系统同步创建");
		customRole.setSiteUID(account.getSiteUID());
		customRole.setTenantUID(account.getTenantUID());
		List<ApmFunction> customFunction = new ArrayList<>();
		customFunction.add(ASSET_AUDIT_APPROVE);
		StringBuilder stringBuilder = new StringBuilder();
		for (ApmFunction function : customFunction) {
			stringBuilder.append(function.getFunctionName() + ",");
		}
		customRole.setWebAuthoritiesCH(stringBuilder.substring(0, stringBuilder.length()-1));
		customRole.setFunctionList(JSON.toJSONString(customFunction.stream().map(ApmFunction::name).collect(Collectors.toList())));
		customRoleRepository.save(customRole);
		return customRole;
	}

	private void saveLevelValue(String key,String fieldName){
		Class<WorkflowConfig> workflowConfigClass = WorkflowConfig.class;
		Field[] declaredFields = workflowConfigClass.getDeclaredFields();
		Map<String, Field> collect = Arrays.asList(declaredFields).stream().collect(Collectors.toMap(item -> item.getName(), item -> item));
		List<WorkflowConfig> workflowConfigs = getWorkflowConfig();
		if(CollectionUtils.isEmpty(workflowConfigs)) return;
		List<ConfigItemValue> byKey = configItemValueRepository.findByKey(key);
		List<ConfigItemValue> result=new ArrayList<>();
		if(CollectionUtils.isEmpty(byKey)) {
			result = workflowConfigs.stream()
					.map(item -> {
						try {
							Field field = collect.get(fieldName);
							field.setAccessible(true);
							Object o = field.get(item);
							if(o != null){
								return initConfigItemValue(item,key,o);
							}else{
								return null;
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return null;
						}
					}).filter(item ->item != null).collect(Collectors.toList());
		}else{
			Map<String, ConfigItemValue> configItemValueMap = byKey.stream().filter(item->StringUtils.isNotEmpty(item.getSiteUid())).collect(Collectors.toMap(ConfigItemValue::getSiteUid, item -> item));
			result=workflowConfigs.stream()
					.filter(item-> item.getSiteUID() !=null && !configItemValueMap.containsKey(item.getSiteUID()))
					.map(item->{
						try {
							Field field = collect.get(fieldName);
							field.setAccessible(true);
							Object o = field.get(item);
							if(o != null){
								return initConfigItemValue(item,key,o);
							}else{
								return null;
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return null;
						}
					}).filter(item ->item != null).collect(Collectors.toList());
		}
		configItemValueRepository.save(result);
	}
	private List<WorkflowConfig> getWorkflowConfig(){
		Map<String,WorkflowConfig> workflowConfigMap=new HashMap<>();
		Iterable<WorkflowConfig> workflowConfigIterable = workflowConfigRepository.findAll();
		if(workflowConfigIterable == null) return null;
		Iterator<WorkflowConfig> iterator = workflowConfigIterable.iterator();
		while (iterator.hasNext()){
			WorkflowConfig next = iterator.next();
			if(StringUtils.isNotEmpty(next.getSiteUID())){
				workflowConfigMap.put(next.getSiteUID(),next);
			}
		}
		return workflowConfigMap.entrySet().stream().map(item->item.getValue()).collect(Collectors.toList());

	}
	private ConfigItemValue initConfigItemValue(WorkflowConfig item,String key,Object val){
		String siteUID = item.getSiteUID();
		if(StringUtils.isEmpty(siteUID)){
			return null;
		}
		ConfigItemValue value = new ConfigItemValue();
		value.setTenantUid(item.getTenantUID());
		value.setInstitutionUid(item.getInstitutionUID());
		value.setHospitalUid(item.getHospitalUID());
		value.setSiteUid(siteUID);
		value.setKey(key);
		value.setValue(String.valueOf(val));
		return value;
	}
	private ConfigItem saveDefaultValue(String key,Integer category,String defaultValue,Integer valueType,String name,String description){
		ConfigItem configItemDefault = configItemRepository.findByDefaultValueForKey(key);
		if(configItemDefault == null){
			configItemDefault = new ConfigItem();
			configItemDefault.setKey(key);
			configItemDefault.setCategory(category);
			configItemDefault.setDefaultValue(defaultValue);
			configItemDefault.setUserParentConfig(true);
			configItemDefault.setSuperAdminEdit(false);
			configItemDefault.setValueType(valueType);
			configItemDefault.setName(name);
			configItemDefault.setDescription(description);
			configItemRepository.save(configItemDefault);
		}
		return configItemDefault;
	}

	public void batchUpdateReminderRule() {
		reminderRuleRepository.deleteByType("AssetLifecycle");
		reminderRuleRepository.deleteByType("measuring");
		Iterable<TenantInfo> tenantInfos = tenantInfoRepository.findAll();
		List<ReminderRule> list = ListUtil.newArrayList();
		for (TenantInfo tenantInfo : tenantInfos) {
			ReminderRule reminderRule = new ReminderRule();
			reminderRule.setTenantUid(tenantInfo.getUid());
			reminderRule.setInstitutionUid(tenantInfo.getUid());
			reminderRule.setHospitalUid(tenantInfo.getUid());
			reminderRule.setSiteUid(tenantInfo.getUid());
			reminderRule.setDescription("资产到期提醒推送微信消息");
			reminderRule.setName("资产到期提醒");
			reminderRule.setParameters("{\"_templateId\":\"un_take_task_template_id\",\"keyword1\":\"设备名称：{name}\\r\\n设备编号：{departNum}\\r\\n出厂序列号：{serialNum}\",\"keyword2\":\"{agencyDate}\",\"pushNum\":3,\"noticeType\":\"noticeTypeRole\",\"remark\":\"设备待报废提醒, 设备即将达到出厂使用年限要求，请进一步处理\",\"noticeUser\":[\"2\"],\"pushCycle\":5,\"first\":\"即将达到使用年限的设备提醒\",\"pushType\":2}");
			reminderRule.setReminderType("wechat");
			reminderRule.setState(1);
			reminderRule.setType("AssetLifecycle");
			list.add(reminderRule);
		}
		List<ReminderRule> contractOverdue = reminderRuleRepository.findByType("ContractOverdue");
		for (ReminderRule reminderRule : contractOverdue) {
			if (ObjectUtil.isEmpty(reminderRule.getSiteUid())) {
				reminderRule.setState(0);
			}
			String parameters = reminderRule.getParameters();
			WechatParam wechatParam = JSON.parseObject(parameters, WechatParam.class);
			wechatParam.setTemplateId("un_take_task_template_id");
			wechatParam.setFirst("合同应到货日期逾期提醒");
			wechatParam.setRemark("合同编号：{contractNum}\n合同名称：{name}\n合同类型：设备\n供应商：{supplierName}\n合同设备数量：{equipmentNum}\n应到货日期：{expectArrivalDate}");
			wechatParam.setKeyword1("{name}合同已逾期，请尽快处理！");
			wechatParam.setKeyword2("{expectArrivalDate}");
			String wechatParamStr = JSON.toJSONString(wechatParam);
			JSONObject jsonObject = JSON.parseObject(parameters);
			jsonObject.putAll(JSON.parseObject(wechatParamStr));
			reminderRule.setParameters(JSON.toJSONString(jsonObject));
			list.add(reminderRule);
		}
		reminderRuleRepository.save(list);
	}

	public void batchUpdateSparPart() {
		stockRecordRepository.findByType(1).forEach(v -> {
			if (v.getInboundNumber() == null) {
				v.setInboundNumber(OrderNumberGenerator.generateOrderNumber("R", v.getRecordDate()));
				stockRecordRepository.save(v);
				try {
					Thread.sleep(1/10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		stockRecordRepository.findByType(2).forEach(v -> {
			if (v.getInboundNumber() == null) {
				v.setOutboundNumber(OrderNumberGenerator.generateOrderNumber("C", v.getRecordDate()));
				stockRecordRepository.save(v);
				try {
					Thread.sleep(1/10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void batchUpdateAssetDeptId() {
		List<AssetInfo> assetInfos = assetInfoRepository.notEqualAssetDeptId();
		logger.info("update asset_dept_id data size:{}",assetInfos.size());
		for (AssetInfo assetInfo : assetInfos) {
			String uid=assetInfo.getUid();
			Integer assetDeptId=assetInfo.getAssetDeptId();
			String  assetDeptUid=assetInfo.getAssetDeptUID();
			try {
				logger.info("update asset_dept_id data before assetUid:{},assetDeptId:{},assetDeptUid:{}",uid,assetDeptId,assetDeptUid);
				OrgInfo byUid = orgInfoRepository.findByUid(assetDeptUid);
				assetInfo.setAssetDeptId(byUid.getId());
				assetInfoRepository.save(assetInfo);
				logger.info("update asset_dept_id data after assetUid:{},assetDeptId:{},assetDeptUid:{}",uid,byUid.getId(),assetDeptUid);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("update asset_dept_id data error assetUid:{},assetDeptId:{},assetDeptUid:{}",uid,assetDeptId,assetDeptUid);

			}
		}
	}


	public void batchUpdateI18nMsgType() {
		String msgType="riskGradeNew";
		List<SearchFilter> searchFilters=new ArrayList<>();
		searchFilters.add(new SearchFilter("useNewRiskGrade", SearchFilter.Operator.EQ,Boolean.TRUE));
		List<WorkflowConfig> bySearchFilter = workflowConfigRepository.findBySearchFilter(searchFilters);
		List<I18nMessage> i18nByType = i18nRepository.findI18nByType(msgType);
		if(CollectionUtils.isEmpty(bySearchFilter) || CollectionUtils.isEmpty(i18nByType)) return ;
		List<I18nMessage> collect = bySearchFilter.stream().filter(item-> item.getSiteId() != 70).filter(distinctByKey(item->item.getSiteId())).map(item -> {
			return i18nByType.stream().map(i18n -> {
				I18nMessage i18nMessage = new I18nMessage();
				BeanUtils.copyProperties(i18n, i18nMessage);
				i18nMessage.setId(item.getSiteId());
				i18nMessage.setMsgKey("riskGrade");
				return i18nMessage;
			}).collect(Collectors.toList());
		}).flatMap(item -> item.stream()).collect(Collectors.toList());
		i18nRepository.save(collect);
		DbMessageSource.reLoadMessages();
	}


	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public void batchUpdateAssetInfo() {
		try {
			Long count=assetInfoRepository.findByCreatedByIsLoginNameSize();
			logger.info("query batchUpdateAssetInfo count:{}",count);
			long startTimeUpdate = System.currentTimeMillis();
//		int size=assetInfoRepository.batchUpdateAssetInfoCreatedBy();
			int size=0;
			while (true){
				List<AssetDto> assetInfos=assetInfoMapper.findByCreatedByIsLoginName();
				if(CollectionUtils.isEmpty(assetInfos)){
					break;
				}
				for (AssetDto assetInfo : assetInfos) {
					assetInfoRepository.updateCreatedBy(assetInfo.getId(),assetInfo.getUserName());
				}
				size=size+assetInfos.size();
				Thread.sleep(10000);
			}
			long endTimeUpdate = System.currentTimeMillis();
			logger.info("update  batchUpdateAssetInfo size:{},耗时:{}",size,startTimeUpdate-endTimeUpdate);
		}catch (Exception e){
			logger.error("update  batchUpdateAssetInfo error :",e);
		}


	}

	@Autowired
	private UnstructuredDataCollectionRepository collectionRepository;
	@Autowired
	private PmOrderRuleCheckListRepository orderRuleCheckListRepository;
	@Autowired
	private TransactionalDBService transactionalDBService;


	public void batchUpdatePmOrderFields() {
		try {
			List<PmOrderRule> pmOrderRules = adminConsoleMapper.findAllPmOrderRule();
			Map<String, PmOrderRule> ruleMap = pmOrderRules.stream().collect(Collectors.toMap(PmOrderRule::getUid, t -> t));
			List<PmOrderRuleCheckList> checkItemList = adminConsoleMapper.findAllPmOrderRuleCheckList();
			Map<String, List<PmOrderRuleCheckList>> checkItemMap = checkItemList.stream().collect(Collectors.groupingBy(PmOrderRuleCheckList::getRuleUID));
			Map<String, List<PmOrderRuleCheckList>> checkUidMap = checkItemList.stream().collect(Collectors.groupingBy(PmOrderRuleCheckList::getUid));
			int offset = 0;
			logger.info("batchUpdatePmOrderFields start");
			while (true) {
				List<V2_PmOrder> orders = adminConsoleMapper.limitPmOrder(5000, offset++ * 5000);
				if (ObjectUtil.isEmpty(orders)) {
					logger.info("batchUpdatePmOrderFields end");
					break;
				}
				logger.info("batchUpdatePmOrderFields {}, {}", 5000, offset * 5000);
				Set<String> assetUidList = orders.stream().map(V2_PmOrder::getAssetUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
				Set<String> orderUidList = orders.stream().map(V2_PmOrder::getId).collect(Collectors.toSet());
				List<RelRuleAsset> relRuleAssets = adminConsoleMapper.findRelRuleAssetByAssetUidList(assetUidList);
				Map<String, String> relRuleAssetMap = MapUtil.newHashMap();
				for (RelRuleAsset relRuleAsset : relRuleAssets) {
					relRuleAssetMap.put(relRuleAsset.getAssetUID() + "#1", relRuleAsset.getRuleUID1());
					relRuleAssetMap.put(relRuleAsset.getAssetUID() + "#2", relRuleAsset.getRuleUID2());
					relRuleAssetMap.put(relRuleAsset.getAssetUID() + "#3", relRuleAsset.getRuleUID());
					relRuleAssetMap.put(relRuleAsset.getAssetUID() + "#4", relRuleAsset.getRuleUID4());
				}
				List<UnstructuredDataCollection> dataCollectionList = collectionRepository.findCKByBoUuidIn(orderUidList, "pm-checklist-report");
				Map<String, List<UnstructuredDataCollection>> dataCollectionMap = dataCollectionList.stream().collect(Collectors.groupingBy(UnstructuredDataCollection::getBoUuid));
				List<V2_PmOrder> saveList = ListUtil.newArrayList();
				List<UnstructuredDataCollection> saveDataCollectionList = ListUtil.newArrayList();
				for (V2_PmOrder order : orders) {
					UnstructuredDataCollection dataCollection = new UnstructuredDataCollection();
					dataCollection.setBoType("pm-checklist-report");
					dataCollection.setBoUuid(order.getId());
					Map<String, UnstructuredData> dataMap = MapUtil.newHashMap();
					List<UnstructuredData> dataList = ListUtil.newArrayList();
					List<UnstructuredDataCollection> dataCollections = dataCollectionMap.get(order.getId());
					if (order.isFinished() && ObjectUtil.isAllEmpty(dataCollections, order.getPmRuleUid())) {
						continue;
					}
					if (ObjectUtil.isEmpty(order.getRuleLevel())) {
						continue;
					}
					String ruleUid = order.getPmRuleUid();
					if (ObjectUtil.isNotEmpty(dataCollections)) {
						dataCollection = dataCollections.get(0);
						dataMap = dataCollection.getData().stream().collect(Collectors.toMap(UnstructuredData::getKey, t -> t));
					}
					PmOrderRule pmOrderRule = ruleMap.get(ruleUid);
					if (ObjectUtil.isEmpty(pmOrderRule)) {
						ruleUid = relRuleAssetMap.get(order.getAssetUid() + "#" + order.getRuleLevel());
						pmOrderRule = ruleMap.get(ruleUid);
					}
					if (ObjectUtil.isEmpty(pmOrderRule) && Objects.equals(4,order.getRuleLevel())) {
						pmOrderRule = ruleMap.get(order.getPmRuleUid());
					}
					if (ObjectUtil.isNotEmpty(dataMap) && !dataMap.keySet().isEmpty()) {
						for (Map.Entry<String, UnstructuredData> dataEntry : dataMap.entrySet()) {
							List<PmOrderRuleCheckList> checkListList = checkUidMap.get(dataEntry.getKey());
							if (ObjectUtil.isNotEmpty(checkListList) && checkListList.size() == 1) {
								PmOrderRuleCheckList item = checkListList.get(0);
								dataCollection.setSchemaId(item.getRuleUID());
								order.setPmRuleUid(item.getRuleUID());
								pmOrderRule = ruleMap.get(item.getRuleUID());
								UnstructuredData data = dataEntry.getValue();
								String valueSerialized = data.getValueSerialized();
								if ("null".equals(valueSerialized) || ObjectUtil.isEmpty(valueSerialized)) {
									data.setValue("pass");
								}
								data.setName(item.getName());
								data.setRemark(item.getRemark());
								data.setPeriod(item.getPeriod());
								dataList.add(data);
							}
						}
						List<UnstructuredData> removeList = ListUtil.newArrayList();
						Map<String, List<UnstructuredData>> collect = dataList.stream().collect(Collectors.groupingBy(t -> t.getCollectionId() + "#" + t.getKey()));
						for (Map.Entry<String, List<UnstructuredData>> entry : collect.entrySet()) {
							List<UnstructuredData> value = entry.getValue();
							if (value.size() > 1) {
								removeList.addAll(value);
							}
						}
						dataList.removeAll(removeList);
					}
					if (ObjectUtil.isEmpty(pmOrderRule)) {
						continue;
					}
					order.setPmRuleUid(pmOrderRule.getUid());
					saveList.add(order);
					boolean isEnd = order.isFinished() || "Acceptance".equals(order.getCurrentStepId());
					if (ObjectUtil.isEmpty(dataCollection.getId()) && isEnd) {
						continue;
					}
					dataCollection.setSchemaId(pmOrderRule.getUid());
					if (ObjectUtil.isEmpty(dataMap) && ObjectUtil.isEmpty(dataCollection.getId())) {
						// 更新检查项
						List<PmOrderRuleCheckList> items = Optional.ofNullable(checkItemMap.get(pmOrderRule.getUid())).orElse(ListUtil.empty());
						for (PmOrderRuleCheckList item : items) {
							if (Objects.equals(item.getStatus(),0)) {
								continue;
							}
							UnstructuredData data = new UnstructuredData();
							data.setKey(item.getUid());
							data.setValue("pass");
							data.setName(item.getName());
							data.setRemark(item.getRemark());
							data.setPeriod(item.getPeriod());
							dataList.add(data);
						}
						List<UnstructuredData> removeList = ListUtil.newArrayList();
						Map<String, List<UnstructuredData>> collect = dataList.stream().collect(Collectors.groupingBy(t -> t.getCollectionId() + "#" + t.getKey()));
						for (Map.Entry<String, List<UnstructuredData>> entry : collect.entrySet()) {
							List<UnstructuredData> value = entry.getValue();
							if (value.size() > 1) {
								removeList.addAll(value);
							}
						}
						dataList.removeAll(removeList);
					}
					dataCollection.setData(dataList);
					saveDataCollectionList.add(dataCollection);
				}
				// 保存数据库
				logger.info("batchUpdatePmOrderFields save db ===== orderSize : {} collectionSize : {}", saveList.size(), saveDataCollectionList.size());
				logger.info("savePmOrderDB saveDataCollectionList : {}", JSON.toJSONString(saveDataCollectionList));
				transactionalDBService.savePmOrderDB(ruleMap, saveList, saveDataCollectionList);
			}
		} catch (Exception e) {
			logger.error("batchUpdatePmOrderFields error : {} ", e.getMessage(), e);
			throw new RuntimeException(e);
		}
		logger.info("batchUpdatePmOrderFields 同步结束");
	}

	public void batchUpdateSharedAssetRequest() {
		List<ConfigItemValue> whetherToEnableUnattended = configItemValueRepository.findByKey("whetherToEnableUnattended");
		if(CollectionUtils.isEmpty(whetherToEnableUnattended)) return;
		List<String> strings = adminConsoleMapper.selectGroupBySiteUidSharedAssetRequest();
		if(CollectionUtils.isEmpty(strings)) return;
		Map<String, Boolean> collect = whetherToEnableUnattended.stream().collect(Collectors.toMap(ConfigItemValue::getSiteUid, item -> Boolean.TRUE.toString().equals(item.getValue())));
		for (String siteUid : strings) {
			if(collect.containsKey(siteUid)){
				Boolean aBoolean = collect.get(siteUid);
				Integer dataType=aBoolean?2:1;
				adminConsoleMapper.updateBySiteUidSharedAssetRequest(dataType,siteUid);
			}else{
				adminConsoleMapper.updateBySiteUidSharedAssetRequest(1,siteUid);
			}
		}
	}

	public void batchUpdateTenantSupplier() {
		List<TenantOptionConfig> tenantOptionConfigs = tenantOptionConfigRepository.findIsActiveSupplier();
		tenantOptionConfigs.stream().map(TenantOptionConfig::getTenantUid).collect(Collectors.toSet()).forEach(uid -> {
			TenantInfo tenantInfo = tenantInfoRepository.findByUid(uid);
			if (ObjectUtil.isNotEmpty(tenantInfo)) {
				Set<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class).stream().collect(Collectors.toSet());
				featureList.add(ApmFeature.ASSET_SUPPLIER_APPLICATION.name());
				if (featureList.contains("ASSET_SUPPLIER")) {
					featureList.remove("ASSET_SUPPLIER");
				}
				tenantInfo.setFeatureList(JSON.toJSONString(featureList));
				tenantInfoRepository.save(tenantInfo);
			}
		});
		List<WorkflowConfig> workflowConfigs = workflowConfigRepository.findIsActiveSupplier();
		List<AdminFeatureActive> waitList = new ArrayList<>();
		workflowConfigs.forEach(config -> {
			buildActiveFeatureByHospitalUid(config.getHospitalUID(), waitList, ApmFeature.ASSET_SUPPLIER_APPLICATION.name());
			adminFeatureActiveRepository.save(waitList);
			waitList.clear();
		});
	}

	public void batchUpdateTenantAdverseEvent() {
		List<TenantInfo> tenantInfoList = tenantInfoRepository.getAllActiveAdverseEvent();
		tenantInfoList.forEach(tenantInfo -> {
			if (StringUtils.isNotEmpty(tenantInfo.getFeatureList())) {
				Set<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class).stream().collect(Collectors.toSet());
				featureList.add(ApmFeature.ASSET_ADVERSE_EVENT.name());
				tenantInfo.setFeatureList(JSON.toJSONString(featureList));
				tenantInfoRepository.save(tenantInfo);
			}
		});
		List<String> hospitals = adverseEventsRepository.findByAllUseHospital();
		List<AdminFeatureActive> waitList = new ArrayList<>();
		hospitals.forEach(uid -> {
			buildActiveFeatureByHospitalUid(uid, waitList, ApmFeature.ASSET_ADVERSE_EVENT.name());
			adminFeatureActiveRepository.save(waitList);
			waitList.clear();
		});
	}

	/**
	 * 批量删除并更新所有租户单独收费的 调剂中心 指控评审 预算管理  并激活已使用的医院
	 * */
	public void batchUpdateTenantFeature() {
		Iterable<TenantInfo> tenantInfos = tenantInfoRepository.findAll();
		List<TenantInfo> waitSaveTenant = new ArrayList<>();
		List<AdminFeatureActive> waitSaveActiveHospital = new ArrayList<>();
		Map<String, String> exTenant = new HashMap<>();
		tenantInfos.forEach(tenantInfo -> {
			try {
				if (StringUtils.isNotEmpty(tenantInfo.getFeatureList())) {
					Set<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class).stream().collect(Collectors.toSet());
					if (featureList.contains(ApmFeature.ADJUST_ASSET.name())) {
						List<SharedAssetRequest> requests = sharedAssetRequestRepository.findByTenantUID(tenantInfo.getUid());
						if (CollectionUtils.isEmpty(requests)) {
							featureList.remove(ApmFeature.ADJUST_ASSET.name());
							tenantInfo.setFeatureList(JSON.toJSONString(featureList));
							tenantInfo.setLastModifiedDate(new Date());
							waitSaveTenant.add(tenantInfo);
						} else {
							Set<String> hospitals = requests.stream().map(SharedAssetRequest::getHospitalUID).collect(Collectors.toSet());
							hospitals.forEach(hospitalUid -> {
								buildActiveFeatureByHospitalUid(hospitalUid, waitSaveActiveHospital, ApmFeature.ADJUST_ASSET.name());
							});
						}
					}
					if (featureList.contains(ApmFeature.AUDIT.name())) {
						List<AuditCase> requests = auditCaseRepository.findByTenantUid(tenantInfo.getUid());
						if (CollectionUtils.isEmpty(requests)) {
							featureList.remove(ApmFeature.AUDIT.name());
							tenantInfo.setFeatureList(JSON.toJSONString(featureList));
							tenantInfo.setLastModifiedDate(new Date());
							waitSaveTenant.add(tenantInfo);
						} else {
							Set<String> siteUid = requests.stream().map(AuditCase::getSiteUid).collect(Collectors.toSet());
							List<OrgInfo> orgInfoList = orgInfoRepository.findInUIds(siteUid);
							orgInfoList.forEach(orgInfo -> {
								buildActiveFeatureByHospitalUid(orgInfo.getHospitalUID(), waitSaveActiveHospital, ApmFeature.AUDIT.name());
							});
						}
					}
					if (featureList.contains(ApmFeature.BUDGET.name())) {
						List<BudgetApplication> requests = budgetApplicationRepository.findByTenantUID(tenantInfo.getUid());
						if (CollectionUtils.isEmpty(requests)) {
							featureList.remove(ApmFeature.BUDGET.name());
							tenantInfo.setLastModifiedDate(new Date());
							tenantInfo.setFeatureList(JSON.toJSONString(featureList));
							waitSaveTenant.add(tenantInfo);
						} else {
							Set<String> hospitals = requests.stream().map(BudgetApplication::getHospitalUID).collect(Collectors.toSet());
							hospitals.forEach(hospitalUid -> {
								buildActiveFeatureByHospitalUid(hospitalUid, waitSaveActiveHospital, ApmFeature.BUDGET.name());
							});
						}
					}
				}
			} catch (Exception e) {
				exTenant.put(tenantInfo.getUid(), tenantInfo.getName());
			}
		});
		logger.error("异常租户:" + JSON.toJSONString(exTenant));
		tenantInfoRepository.save(waitSaveTenant);
		adminFeatureActiveRepository.save(waitSaveActiveHospital);
	}

	public void batchActiveDigitalAssetStaff() {
		Iterable<TenantInfo> tenantInfos = tenantInfoRepository.findAll();
		String featureKey = ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING.name();
		tenantInfos.forEach(tenantInfo -> {
			List<AdminFeatureActive> waitList = new ArrayList<>();
			if (BooleanUtils.isTrue(tenantInfo.getIsEnabled())) {
				List<OrgInfo> hospitals = orgInfoRepository.findByTenantUIDAndOrgType(tenantInfo.getUid(), 2);
				hospitals.forEach(info -> {
					if (Objects.equals(info.getOrgLevel() , 2)) {
						Set<String> features = adminFeatureActiveRepository.findByHospitalUID(info.getUid()).stream().map(AdminFeatureActive::getFeatureKey).collect(Collectors.toSet());
						if (!features.contains(featureKey)) {
							AdminFeatureActive adminFeatureActive = new AdminFeatureActive();
							adminFeatureActive.setFeatureKey(featureKey);
							adminFeatureActive.setHospitalUID(info.getUid());
							adminFeatureActive.setTenantUID(info.getTenantUID());
							adminFeatureActive.setActiveRole(ApmSystemRole.SuperAdmin.name());
							adminFeatureActive.setCreateDate(new Date());
							adminFeatureActive.setActiveDate(new Date());
							waitList.add(adminFeatureActive);
						}
					}
				});
				adminFeatureActiveRepository.save(waitList);
			}
		});
	}

	private void buildActiveFeatureByHospitalUid(String uid, List<AdminFeatureActive> waitSave, String featureKey) {
		OrgInfo info = orgInfoRepository.getByUid(uid);
		if (info.getOrgLevel() == 2) {
			Set<String> features = adminFeatureActiveRepository.findByHospitalUID(uid).stream().map(AdminFeatureActive::getFeatureKey).collect(Collectors.toSet());
			if (!features.contains(featureKey)) {
				AdminFeatureActive adminFeatureActive = new AdminFeatureActive();
				adminFeatureActive.setFeatureKey(featureKey);
				adminFeatureActive.setHospitalUID(info.getUid());
				adminFeatureActive.setTenantUID(info.getTenantUID());
				adminFeatureActive.setActiveRole(ApmSystemRole.SuperAdmin.name());
				adminFeatureActive.setCreateDate(new Date());
				adminFeatureActive.setActiveDate(new Date());
				waitSave.add(adminFeatureActive);
			}
		}
	}

	public void updateNurseSiteMaintainConfigPyCode() {
		Iterable<NurseSiteMaintainConfig> configs = nurseSiteMaintainConfigRepository.findAll();
		configs.forEach(v -> {
			if (StringUtils.isNotEmpty(v.getName())) {
				v.setNamePyCode(PinyinUtil.getShortPinyin(v.getName()).toLowerCase().replaceAll("[^0-9a-zA-Z]", ""));
				nurseSiteMaintainConfigRepository.save(v);
			}
		});
	}

    public void batchMultiLevelApprovalConfig() {

		Iterable<MultiLevelApprovalConfig> configs = multiLevelApprovalConfigRepository.findAll();
		List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup");

		configs.forEach(config->{

			String configration = config.getConfigration();
			if (null != configration) {
				Map map = JSON.parseObject(configration, Map.class);
				config.setMinAmount((Integer) map.get("minAmount"));
				config.setMaxAmount((Integer) map.get("maxAmount"));
				config.setSelectAssetTypes((List<String>) map.get("selectAssetTypes"));
				config.setSelectAssetGroups((List<String>) map.get("selectAssetGroups"));
				config.setSelectAssetClinicalDeptUids((List<String>) map.get("selectAssetClinicalDeptUids"));

				if(CollectionUtils.isEmpty(config.getSelectAssetGroups())){
					config.setSelectAssetGroups(assetGroup.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList()));
				}

				if(CollectionUtils.isEmpty(config.getSelectAssetClinicalDeptUids())){
				  List<OrgInfo>	deptInfoList = uaaService.getClinicalDeptListBySiteUID(config.getHierOrgUid());
				  if(!CollectionUtils.isEmpty(deptInfoList)){
				  	config.setSelectAssetClinicalDeptUids(deptInfoList.stream().map(OrgInfo::getUid).collect(Collectors.toList()));
				  }
				}

				if(CollectionUtils.isEmpty(config.getSelectAssetTypes())){
					List<AssetTypeGroup> resultList = assetTypeGroupDao.getTopAuthGroupAndTenantUid(config.getTenantUID());
					if(!CollectionUtils.isEmpty(resultList)){
						config.setSelectAssetTypes(resultList.stream().map(AssetTypeGroup::getUid).collect(Collectors.toList()));
					}
				}
			}
			Map<String, Object> map = new HashMap();
			map.put("minAmount", config.getMinAmount());
			map.put("maxAmount", config.getMaxAmount());
			map.put("selectAssetClinicalDeptUids", config.getSelectAssetClinicalDeptUids());
			map.put("selectAssetGroups", config.getSelectAssetGroups());
			map.put("selectAssetTypes", config.getSelectAssetTypes());
			String newConfigration = JSON.toJSONString(map);
			config.setConfigration(newConfigration);
			multiLevelApprovalConfigRepository.save(config);

		});

	}

	public void batchUpdatePmRuleBlob() {
		List<PmOrderRule> pmRuleReports = pmOrderRuleRepository.findPmRuleReports();
		Set<String> objectIds = pmRuleReports.stream().map(PmOrderRule::getReportStorageId).collect(Collectors.toSet());
		List<V2_BlobObject> blobObjects = blobObjectRepository.findByObjectStorageIdIn(objectIds);
		Map<String, List<V2_BlobObject>> blobMap = blobObjects.stream().collect(Collectors.groupingBy(V2_BlobObject::getObjectStorageId));
		List<V2_BlobObject> saveList = ListUtil.newArrayList();
		for (PmOrderRule pmRuleReport : pmRuleReports) {
			List<V2_BlobObject> objects = blobMap.get(pmRuleReport.getReportStorageId());
			if (ObjectUtil.isNotEmpty(objects)) {
				V2_BlobObject blobObject = objects.get(0);
				if (ObjectUtil.isNotEmpty(blobObject.getBoUuid())) {
					V2_BlobObject newBlobObject = new V2_BlobObject();
					BeanUtils.copyProperties(blobObject, newBlobObject);
					newBlobObject.setId(null);
					newBlobObject.setBoId(pmRuleReport.getId());
					newBlobObject.setBoUuid(pmRuleReport.getUid());
					newBlobObject.setBoType(V2_BlobObject.BO_TYPE_PM_RULE);
					newBlobObject.setTenantUid(pmRuleReport.getTenantUID());
					saveList.add(newBlobObject);
				} else {
					blobObject.setBoId(pmRuleReport.getId());
					blobObject.setBoUuid(pmRuleReport.getUid());
					blobObject.setBoType(V2_BlobObject.BO_TYPE_PM_RULE);
					blobObject.setTenantUid(pmRuleReport.getTenantUID());
					saveList.add(blobObject);
				}
			} else {
				V2_BlobObject blobObject = new V2_BlobObject();
				blobObject.setBoId(pmRuleReport.getId());
				blobObject.setBoUuid(pmRuleReport.getUid());
				blobObject.setBoType(V2_BlobObject.BO_TYPE_PM_RULE);
				blobObject.setTenantUid(pmRuleReport.getTenantUID());
				blobObject.setObjectStorageId(pmRuleReport.getReportStorageId());
				blobObject.setCreatedBy("anonymousUser");
				blobObject.setCreatedDate(new Date());
				blobObject.setLastModifiedBy("anonymousUser");
				blobObject.setLastModifiedDate(new Date());
				saveList.add(blobObject);
			}
		}
		blobObjectRepository.save(saveList);
	}

	public void initMvsMapping() {
		mvsIbMappingMapper.initMvsMapping();
	}

	public void initAssetListSort() {
		List<TenantOptionConfig> assetListSortConfigs = tenantOptionConfigRepository.findAssetListSort();
		Set<String> tenantUids = assetListSortConfigs.stream().map(TenantOptionConfig::getTenantUid).collect(Collectors.toSet());
		List<CustomConfig> configs = customConfigDao.findByTenantUIDIn(tenantUids);
		for (CustomConfig config : configs) {
			CustomConfigObject customConfigObject = JSONObject.parseObject(config.getConfig(), CustomConfigObject.class);
			JSONObject assetListSort = new JSONObject();
			assetListSort.put("createdDate", false);
			customConfigObject.setAssetListSort(assetListSort);
			config.setConfig(JSON.toJSONString(customConfigObject));
		}
		customConfigDao.save(configs);
	}
}
