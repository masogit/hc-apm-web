package com.ge.apm.service.nurseSite;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.mapper.NurseSiteMaintainItemMapper;
import com.ge.apm.dao.mapper.NurseSiteMaintainMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.nurseSite.export.NurseSiteMaintainExport;
import com.ge.apm.service.nurseSite.export.NurseSiteMaintainExportUtil;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BatchJobRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.NurseSiteMaintainCheckItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainDataRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.service.utils.NurseSiteMaintainComparator;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.imageio.ImageIO;

/**
 * Created by 503035627
 */
@Service
public class NurseSiteMaintainService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainService.class);
	private static final String SHEET_Maintain = "Maintain List";
	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;

	@Autowired
	NurseSiteMaintainMapper nurseSiteMaintainMapper;

	@Autowired
	NurseSiteMaintainItemMapper nurseSiteMaintainItemMapper;

	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	NurseSiteMaintainCheckItemRepository nurseSiteMaintainCheckItemRepository;

	@Autowired
	AssetInfoRepository assetInfoRepository;

	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	BatchJobRepository batchJobRepository;

	@Autowired
	private BlobFileService blobFileService;

	@Autowired
	private UaaService uaaService;
	
	@Autowired
	private CustomRoleRepository customRoleRepository;
	
	@Autowired
	BlobObjectRepository blobObjectRepository;
	@Autowired
	private MicroServiceUtil microServiceUtil;
	@Autowired
	private SystemEventAuditAopAspect systemEventAuditUtil;
	private static final String STR_ASSET_NAME = "设备名";
	private static final String CLINICAL_DEPT_NAME = "所属科室";
	private static final String STR_ASSET_DEPTNUM = "设备编号";
	private static final String STR_SERIAL_NUM = "制造商序列号";
	private static final String STR_ASSET_FUNCTIONTYPE = "设备型号";
	private static final String STR_COMMENT = "备注";
	private static final String KEY_COMMENT = "comment";
	private static final String STR_SCHEDULE_DATE = "计划检查时间";
	private static final String STR_CHECK_DATE = "实际检查时间";
	private static final String STR_SEQUENCE = "班次";
	private static final String STR_MODIFIED_BY = "负责人";
	private static final String STR_SIGNATURE = "签名";
	private static final String STR_NURSESITEREPORT = "临床助手日常检查报表";

	private Map<String,UserAccount> mapUserAccount=new HashMap<String,UserAccount>();
	private Map<String,AssetInfo> mapAssetInfo=new HashMap<String,AssetInfo>();
	private Map<String,OrgInfo> mapOrgInfo=new HashMap<String,OrgInfo>();

	public void init(){
		mapUserAccount.clear();
		mapAssetInfo.clear();
		mapOrgInfo.clear();
	}

	@Transactional
	public void tBatchCompleteMaintain(NurseSiteMaintain maintain, String signatureId,String userUid, Date date,Date delayDate) {
		int status = maintain.getStatus();
		if (status==0 || status == 2 || status == 5 || status == 6 || status == 7) {
			Set<String> setMaintainIds = new HashSet<String>();
			Set<String> setMaintainItemIds = new HashSet<String>();
			setMaintainIds.add(maintain.getId());
			if (setMaintainIds.size() > 0) {
				setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId0(setMaintainIds);
				if (setMaintainItemIds!=null && setMaintainItemIds.size() > 0) {
//					nurseSiteMaintainDataRepository.completeMaintainDataByParent(setMaintainItemIds);
					getListPairCheckItems(maintain,setMaintainItemIds);
					nurseSiteMaintainItemRepository.completeMaintainItem2(setMaintainItemIds, signatureId, userUid, date,delayDate,userUid,true);
					nurseSiteMaintainItemRepository.completeMaintainItem5(setMaintainItemIds, signatureId, userUid, date,delayDate,userUid,true);
					nurseSiteMaintainItemRepository.completeMaintainItem6(setMaintainItemIds, signatureId, userUid, date,delayDate,userUid,true);
					nurseSiteMaintainItemRepository.completeMaintainItem7(setMaintainItemIds, signatureId, userUid, date,delayDate,userUid,true);
				}
			}
		}
	}
	public void getListPairCheckItems(NurseSiteMaintain maintain,Set<String> setMaintainItemIds) {
		NurseSiteMaintainConfig byId = nurseSiteMaintainConfigRepository.findById(maintain.getConfigId());
		Set<NurseSiteMaintainData> maintainDataByParentIds = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(setMaintainItemIds);
		Map<String,NurseSiteMaintainConfigCheckItem> map=new HashMap<>();
		if(byId!=null){
			String strTemp=byId.getCheckItemsDetail();
			if(StringUtils.isNotEmpty(strTemp)){
				JSONArray jsonArray=JSONObject.parseArray(strTemp);
				for(int i=0;i<jsonArray.size();++i){
					NurseSiteMaintainCheckItem p1= JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
					NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(p1);
					map.put(p.getName(),p);
				}
			}
		}
		List<NurseSiteMaintainData> collect = maintainDataByParentIds.stream().map(item -> {
			NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = map.get(item.getCheckItem());
			item.setCheckValue(true);
			if (nurseSiteMaintainConfigCheckItem != null) {
				item.setVariableText(nurseSiteMaintainConfigCheckItem.getDefaultValue());
			}
			return item;
		}).collect(Collectors.toList());
		nurseSiteMaintainDataRepository.save(collect);
	}
	private NurseSiteMaintainConfigCheckItem initConfigCheckItem(NurseSiteMaintainCheckItem checkItem){
		NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
		String fieldType = checkItem.getFieldType();
		if(StringUtils.isBlank(fieldType)){
			fieldType="Text";
		}
		p.setId(checkItem.getId());
		p.setName(checkItem.getName());
		p.setDefaultValue(checkItem.getDefaultValue());
		p.setDescription(checkItem.getDescription());
		p.setFieldType(fieldType);
		p.setRequired(checkItem.getRequired());
		String options = checkItem.getOptions();
		if(StringUtils.isBlank(options) || options.contains("[]")){
			p.setOptions(null);
		}else{
			JSONArray objects = JSONArray.parseArray(options);
			List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
			p.setOptions(tableOptions);
		}
		return p;
	}
	@Transactional
	public void nativeDeleteMaintain(String id) {
		Set<String> setMaintainItemIds = null;
		setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId(id);
		if (setMaintainItemIds!=null && setMaintainItemIds.size() > 0) {
			nurseSiteMaintainDataRepository.deleteMaintainDataByParentId(setMaintainItemIds);
		}
		nurseSiteMaintainItemRepository.deleteMaintainItemByParentId(id);
		nurseSiteMaintainRepository.deleteMaintain(id);
	}

	@Transactional
	public void tDeleteUnCheckedMaintainData(List<NurseSiteMaintain> listMaintain, String assetUid, boolean all) {
		Set<String> setMaintainId = new HashSet<String>();
		List<NurseSiteMaintainItem> listMaintainItem = null;
		if (all) {
			nativeDeleteMaintain(listMaintain);
		} else {
			if (listMaintain != null && listMaintain.size() > 0) {
				for (int i = 0; i < listMaintain.size(); ++i) {
					setMaintainId.add(listMaintain.get(i).getId());
				}
			}
			if (setMaintainId.size() > 0) {
				listMaintainItem = nurseSiteMaintainItemRepository.findByParentIdAndAssetUid(assetUid,setMaintainId);
			}
			if (listMaintainItem != null && listMaintainItem.size() > 0) {
				nativeDeleteMaintainItem(listMaintainItem);
			}
		}
	}

	private void nativeDeleteMaintain(List<NurseSiteMaintain> listMaintain) {
		Set<String> setMaintainIds = new HashSet<String>();
		Set<String> setMaintainItemIds = null;
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				setMaintainIds.add(listMaintain.get(i).getId());
			}
		}
		if (setMaintainIds.size() > 0) {
			setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId(setMaintainIds);
		}
		if (setMaintainItemIds!=null && setMaintainItemIds.size() > 0) {
			nurseSiteMaintainDataRepository.deleteMaintainDataByParentId(setMaintainItemIds);
		}
		if (setMaintainIds.size() > 0) {
			nurseSiteMaintainItemRepository.deleteMaintainItemByParentId(setMaintainIds);
		}
		if (setMaintainIds.size() > 0) {
			nurseSiteMaintainRepository.deleteMaintain(setMaintainIds);
		}
	}

	private void nativeDeleteMaintainItem(List<NurseSiteMaintainItem> listMaintainItem) {
		Set<String> setMaintainItemIds = new HashSet<String>();
		if (listMaintainItem != null && listMaintainItem.size() > 0) {
			for (int i = 0; i < listMaintainItem.size(); ++i) {
				setMaintainItemIds.add(listMaintainItem.get(i).getId());
			}
		}
		if (setMaintainItemIds.size() > 0) {
			nurseSiteMaintainDataRepository.deleteMaintainDataByParentId(setMaintainItemIds);
			nurseSiteMaintainItemRepository.deleteMaintainItem(setMaintainItemIds);
		}
	}
	
	public List<CustomRole> getCustomRole(Integer userId){
		return customRoleRepository.findCustomRolesByUserId(userId);
	}

	public List<NurseSiteMaintainConfig> getMaintainConfigByOrgUids(List<String> orgUids) {
		return nurseSiteMaintainConfigRepository.findByOrgUids(orgUids);
	}

	public NurseSiteMaintainConfig getMaintainConfigById(String id) {
		return nurseSiteMaintainConfigRepository.findById(id);
	}
	
	public NurseSiteMaintainCheckItem getMaintainCheckItemById(String id) {
		return nurseSiteMaintainCheckItemRepository.findById(id);
	}

	public List<NurseSiteMaintain> getDefaultMaintainData(List<String> orgUids, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDefaultData(orgUids, startDate, endDate);
	}
	public List<NurseSiteMaintain> getDefaultMaintainDataUnchecked(List<String> orgUids, Date startDate, Date endDate, String configId) {
		return nurseSiteMaintainRepository.findDefaultDataUnchecked(orgUids, startDate, endDate, configId);
	}

	public List<NurseSiteMaintain> getDefaultMaintainDataChecked(List<String> orgUids, Date startDate, Date endDate, String configId) {
		return nurseSiteMaintainRepository.findDefaultDataChecked(orgUids, startDate, endDate, configId);
	}

	public List<NurseSiteMaintain> getDataByClinicalOrgUid(List<String> listOrgUid, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDataByClinicalOrgUid(listOrgUid, startDate, endDate);
	}

	public List<NurseSiteMaintain> findJoinAssetList(Wrapper<NurseSiteMaintain> wrapper) {
		return nurseSiteMaintainMapper.findJoinAssetList(wrapper);
	}

	public List<NurseSiteMaintainItem> findItemJoinAssetList(Pagination pagination, Wrapper<NurseSiteMaintainItem> wrapper) {
		return nurseSiteMaintainItemMapper.findJoinAssetList(pagination, wrapper);
	}

	public List<NurseSiteMaintain> findByIds(Set<String> ids) {
		return nurseSiteMaintainRepository.findByIds(ids);
	}

	public AssetInfo getAssetInfoByUid(String uid) {
		return assetInfoRepository.getByUid(uid);
	}

	public List<AssetInfo> getListAssetInfoByUid(List<String> assetUids) {
		return assetInfoRepository.findByUidIn(assetUids);
	}

	public List<AssetInfo> getListAssetInfoByMaintainConfigParentId(String parentId) {
		return assetInfoRepository.findAssetByMaintainConfigParentId(parentId);
	}

	public List<AssetInfo> getListAssetInfoBySearchFilter(List<SearchFilter> listFilter, Sort sort) {
		return assetInfoRepository.findBySearchFilter(listFilter, sort);
	}


	public List<OrgInfo> getListClinicalByUidAndParent(List<String> listUid, String parentUid) {
		return orgInfoRepository.findByUidAndParentUid(listUid, parentUid);
	}

	public List<OrgInfo> getListParentSiteByUid(List<String> listUid) {
		return orgInfoRepository.findParentSiteByUids(listUid);
	}

	public OrgInfo findOrgInfoByUid(String uid) {
		return orgInfoRepository.getByUid(uid);
	}

	public UserAccount getUserAccountByUid(String userUid) {
		return userAccountRepository.getByUid(userUid);
	}

	public List<NurseSiteMaintainConfigItem> findByParentId(String parentId) {
		return nurseSiteMaintainConfigItemRepository.findByParentId(parentId);
	}

	public List<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId) {
		return nurseSiteMaintainItemRepository.findMaintainItemByParentId(parentId);
	}

	public List<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId,int pageSize, int page) {
		return nurseSiteMaintainItemRepository.findMaintainItemByParentId(parentId,pageSize,page);
	}

	public int findCountMaintainItemByParentId(String parentId) {
		Integer result=nurseSiteMaintainItemRepository.findCountMaintainItemByParentId(parentId);
		if(result==null){
			result=0;
		}
		return result;
	}

	// public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String
	// maintainId) {
	// return
	// nurseSiteMaintainDataRepository.findMaintainDataByMaintainId(maintainId);
	// }

	public List<String> findUserUidListByOrgUids(List<String> orgUids) {
		return userAccountRepository.findUserUidListByOrgUids(orgUids);
	}

	public void save(NurseSiteMaintain obj) {
		nurseSiteMaintainRepository.save(obj);
	}

	public void save(List<NurseSiteMaintain> listObj) {
		nurseSiteMaintainRepository.save(listObj);
	}

	public void saveMaintainItem(NurseSiteMaintain obj) {
		List<NurseSiteMaintainItem> listItem=obj.getListNurseSiteMaintainItem();
		if(listItem!=null && listItem.size()>0){
			for(NurseSiteMaintainItem item : listItem) {
				boolean isFinish = true;
				List<NurseSiteMaintainData> listData = item.getListData();
				for(NurseSiteMaintainData data : listData) {
					if(data.getCheckValue()==null) isFinish = false;
				}
				item.setIsFinish(isFinish);
			}
			nurseSiteMaintainItemRepository.save(listItem);
		}
	}

	public void saveMaintainItem(List<NurseSiteMaintainItem> obj) {
		nurseSiteMaintainItemRepository.save(obj);
	}

	public void saveMaintainData(Set<NurseSiteMaintainData> obj) {
		nurseSiteMaintainDataRepository.save(obj);
	}

	public void saveNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> obj) {
		nurseSiteMaintainConfigItemRepository.save(obj);
	}

	public boolean deleteMaintain(NurseSiteMaintain maintain,String userUid) {
		if (StringUtils.isNotEmpty(maintain.getSignatureId())) {
			blobFileService.deleteBlobFilesByStorageId(maintain.getSignatureId());
		}
		//Set<String> setItemIds=nurseSiteMaintainItemRepository.findIdsByParentId(maintain.getId());
		List<V2_BlobObject> listV2Obj=blobObjectRepository.fetchListByMaintainId(maintain.getId());
		blobFileService.deleteBlobFile(listV2Obj);
		simpleDeleteNurseSiteMaintain(maintain);
//		addBatchJob("delete", null, null, null, userUid, maintain.getId());
		return true;
	}
	
	public List<NurseSiteMaintainCheckItem> loadWholeCheckItem(String tenantUID){
		List<SearchFilter> searchFilters = new ArrayList<>();
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenantUID));
		return nurseSiteMaintainCheckItemRepository.findBySearchFilter(searchFilters);
	}
	
	public void loadSelectedCheckItem(List<NurseSiteMaintainCheckItem> listWhole,List<NurseSiteMaintainCheckItem> listSelected,NurseSiteMaintainConfig config){
		String checkDetail=config.getCheckItemsDetail();
		if(StringUtils.isNotEmpty(checkDetail)){
			JSONArray arr=JSONArray.parseArray(checkDetail);
			for(int i=0;i<arr.size();++i){
				NurseSiteMaintainCheckItem item=JSONObject.parseObject(arr.getString(i), NurseSiteMaintainCheckItem.class);
				if(StringUtils.isNotEmpty(item.getId())){
					if(listWhole.contains(item)){
						listSelected.add(item);
					}
				}
//				if(!listWhole.contains(item)){
//					listWhole.add(item);
//				}
			}
		}
	}
	
	public void saveSelectedCheckItem(List<NurseSiteMaintainCheckItem> listSelected,JSONArray arr){
		if(listSelected!=null && listSelected.size()>0){
			for(int i=0;i<listSelected.size();++i){
				NurseSiteMaintainCheckItem item=listSelected.get(i);
				JSONObject obj=new JSONObject();
				obj.put("name", item.getName());
				obj.put("key", item.getKey());
				obj.put("description", item.getDescription());
				obj.put("id", item.getId());
				obj.put("groupName", item.getGroupName());
				String fieldType = item.getFieldType();
				String defaultValue = item.getDefaultValue();
				if(StringUtils.isEmpty(fieldType)){
					fieldType="Switch";
					defaultValue="false";
				}
				obj.put("fieldType",fieldType);
				obj.put("defaultValue",defaultValue);
				obj.put("options",item.getOptions());
				obj.put("required",item.getRequired());
				arr.add(obj);
			}
		}
	}
	
	public boolean batchDeleteMaintainData(Date startDate,Date endDate,List<NurseSiteMaintainConfig> listConfig,String userUid) {
		Thread t=new Thread(new Runnable(){
			@Override
			public void run() {
				if(listConfig!=null && listConfig.size()>0){
					Set<String> setConfigIds=new HashSet<String>();
					for(int i=0;i<listConfig.size();++i){
						setConfigIds.add(listConfig.get(i).getId());
					}
					List<NurseSiteMaintain> listMaintain=nurseSiteMaintainRepository.findDefaultDataUnchecked(startDate, endDate, setConfigIds);
					if(listMaintain!=null && listMaintain.size()>0){
						for(int i=0;i<listMaintain.size();++i){
							deleteMaintain(listMaintain.get(i), userUid);
						}
					}
				}
			}
		});
		t.start();
		return true;
	}
	
	

//	@Transactional
//	public boolean deleteMaintain(List<NurseSiteMaintain> listMaintain) {
//		if (listMaintain != null && listMaintain.size() > 0) {
//			Set<String> setSignatureId = new HashSet<String>();
//			for (int i = 0; i < listMaintain.size(); ++i) {
//				NurseSiteMaintain tempMaintain = listMaintain.get(i);
//				if (StringUtils.isNotEmpty(tempMaintain.getSignatureId())) {
//					setSignatureId.add(tempMaintain.getSignatureId());
//				}
//			}
//			if (setSignatureId.size() > 0) {
//				blobFileService.deleteBlobFilesByStorageId(setSignatureId);
//			}
//			if (listMaintain != null && listMaintain.size() > 0) {
//				nativeDeleteMaintain(listMaintain);
//			}
//		}
//		return true;
//	}

//	@Transactional
//	public void saveAndDelete(List<NurseSiteMaintain> obj, List<NurseSiteMaintain> obj2) {
//		if (obj != null && obj.size() > 0) {
//			nurseSiteMaintainRepository.save(obj);
//		}
//		if (obj2 != null && obj2.size() > 0) {
//			deleteMaintain(obj2);
//		}
//	}
	@Transactional
	public void simpleDeleteNurseSiteMaintain(NurseSiteMaintain maintain){
		nurseSiteMaintainDataRepository.deleteMaintainDataByMaintainId(maintain.getId());
		nurseSiteMaintainItemRepository.deleteMaintainItemByParentId(maintain.getId());
		nurseSiteMaintainRepository.deleteMaintainById(maintain.getId());
	}

	public void save(NurseSiteMaintainConfig obj) {
		nurseSiteMaintainConfigRepository.save(obj);
	}

	public void deleteNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> list) {
		nurseSiteMaintainConfigItemRepository.delete(list);
	}

	public void delete(NurseSiteMaintainConfig obj) {
		nurseSiteMaintainConfigRepository.delete(obj);
	}

	public List<String> getOrgListByUser(String userUid) {
		return uaaService.getOrgListByUser(userUid).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
	}

	public List<String> getOrgUidListByUser(String userUid) {
		return uaaService.getOrgListByUser(userUid).stream().map(OrgInfo::getUid).collect(Collectors.toList());
	}

	public NurseSiteMaintainRepository getNurseSiteMaintainRepository() {
		return nurseSiteMaintainRepository;
	}

	public void setNurseSiteMaintainRepository(NurseSiteMaintainRepository nurseSiteMaintainRepository) {
		this.nurseSiteMaintainRepository = nurseSiteMaintainRepository;
	}

	public NurseSiteMaintainConfigRepository getNurseSiteMaintainConfigRepository() {
		return nurseSiteMaintainConfigRepository;
	}

	public void setNurseSiteMaintainConfigRepository(NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository) {
		this.nurseSiteMaintainConfigRepository = nurseSiteMaintainConfigRepository;
	}

	public AssetInfoRepository getAssetInfoRepository() {
		return assetInfoRepository;
	}

	public void setAssetInfoRepository(AssetInfoRepository assetInfoRepository) {
		this.assetInfoRepository = assetInfoRepository;
	}

	public boolean getBatchJobStatus(String siteUID) {
		List<BatchJob> listJob = batchJobRepository.findNewBatchJobWithSite(siteUID);
		if (listJob != null && listJob.size() > 0) {
			return true;
		}
		return false;
	}

	public void deleteAssetUid(String assetUid){
		if(StringUtils.isNotEmpty(assetUid)){
			List<NurseSiteMaintainConfigItem> result=nurseSiteMaintainConfigItemRepository.findExistsAssetUid(assetUid);
			if(CollectionUtils.isNotEmpty(result)){
				addBatchJob("deleteAssetInfo", null, null, null, UserContextService.getCurrentUserAccount().getUid(),assetUid);
				operationRecord(result);
			}
		}
	}
	private void operationRecord(List<NurseSiteMaintainConfigItem> result) {
		NurseSiteMaintainConfigItem nurseSiteMaintainConfigItem = result.get(0);
		UserAccount userAccount = new UserAccount();
		userAccount.setTenantUID(nurseSiteMaintainConfigItem.getTenantUID());
		userAccount.setInstitutionUID(nurseSiteMaintainConfigItem.getInstitutionUID());
		userAccount.setHospitalUID(nurseSiteMaintainConfigItem.getHospitalUID());
		userAccount.setSiteUID(nurseSiteMaintainConfigItem.getSiteUID());
		for (NurseSiteMaintainConfigItem item : result) {
			SystemEventAudit systemEventAudit = systemEventAuditUtil.buildEventAudit(userAccount, SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM_DELETE, item.getId(), true, null, AuditBoTypeEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM.getBoType(), JSONObject.toJSONString(item));
			systemEventAuditUtil.sendSystemAuditMessage(systemEventAudit);
		}
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
		List<BatchJob> listJob=null;
		if(config!=null){
			listJob = batchJobRepository.findNewBatchJobWithContentSite(action,content, config.getSiteUID());
		}
		if (listJob != null && listJob.size() > 0) {
			return false;
		} else {
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
		}
		return true;
	}

	public List<NurseSiteMaintainItem> loadConfigCheckItems(NurseSiteMaintainConfig nurseSiteMaintainConfig, List<AssetInfo> listCheckAssets) {
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
					if(obj.containsKey("required")){
						Boolean required = obj.getBoolean("required");
						p.setRequired(required);
					}else{
						p.setRequired(Boolean.FALSE);
					}
					p.setItem(item);
					listData.add(p);
				}
				listMaintainItem.add(item);
			}
		}
		return listMaintainItem;
	}

	private void updateMaintainDataAllRight(NurseSiteMaintain obj, Date date, UserAccount user) {
		List<NurseSiteMaintainItem> listItem = null;
		supplementMaintain(obj,0,0,true);
		listItem = obj.getListNurseSiteMaintainItem();
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		while (it.hasNext()) {
			NurseSiteMaintainItem tempItem = it.next();
			tempItem.setCheckDate(date);
			tempItem.setModifyBy(user);
			tempItem.setModifyByUid(user.getUid());
			tempItem.setModifyDate(date);
			List<NurseSiteMaintainData> listData = tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData = listData.iterator();
			tempItem.setStatus(2);
			while (itData.hasNext()) {
				NurseSiteMaintainData tempData = itData.next();
				tempData.setCheckValue(true);
			}
		}
		obj.setStatus(2);
		obj.setCheckDate(date);
		obj.setModifyBy(user);
		obj.setModifyByUid(user.getUid());
		obj.setModifyDate(date);
	}

	private void supplementMaintainItem(List<NurseSiteMaintainItem> listItem) {
		Set<String> setAssetInfoUids = new HashSet<String>();
		Set<String> setItemIds = new HashSet<String>();
		Map<String, List<NurseSiteMaintainData>> mapData = new HashMap<String, List<NurseSiteMaintainData>>();
		List<NurseSiteMaintainData> listData = null;
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				if (tempItem.isNeedGetItem()) {
					setAssetInfoUids.add(tempItem.getAssetUid());
					setItemIds.add(tempItem.getId());
					tempItem.setNeedGetItem(false);
				}
			}
		}
		if (setAssetInfoUids.size() > 0) {
			getAssetInfo(setAssetInfoUids);
		}
		if (setItemIds.size() > 0) {
			listData = nurseSiteMaintainDataRepository.findListMaintainDataByParentIds(setItemIds);
		}
		if (listData != null) {
			for (int i = 0; i < listData.size(); ++i) {
				NurseSiteMaintainData tempData = listData.get(i);
				String parentId = tempData.getParentId();
				if (mapData.containsKey(parentId)) {
					mapData.get(parentId).add(tempData);
				} else {
					List<NurseSiteMaintainData> tempSetData = new ArrayList<NurseSiteMaintainData>();
					tempSetData.add(tempData);
					mapData.put(parentId, tempSetData);
				}
			}
		}
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				String modifyByUid=tempItem.getModifyByUid();
				String createByUid=tempItem.getCreateByUid();
				String assetUid=tempItem.getAssetUid();
				if (StringUtils.isNotEmpty(assetUid)) {
					tempItem.setAssetInfo(getAssetInfo(assetUid));
				}
				if (StringUtils.isNotEmpty(modifyByUid)) {
					tempItem.setModifyBy(getUserAccount(modifyByUid));
				}
				if (StringUtils.isNotEmpty(createByUid)) {
					tempItem.setCreateBy(getUserAccount(createByUid));
				}
				List<NurseSiteMaintainData> tempListData=mapData.get(tempItem.getId());
				if(tempListData!=null){
					tempItem.setListData(tempListData);
					Map<String, NurseSiteMaintainConfigCheckItem> configMap = initConfig(tempItem);
					tempItem.setConfigMap(configMap);
				}
			}
		}
	}

	private void supplementMaintainItemWithOut(List<NurseSiteMaintainItem> listItem) {
		Set<String> setItemIds = new HashSet<String>();
		Map<String, List<NurseSiteMaintainData>> mapData = new HashMap<String, List<NurseSiteMaintainData>>();
		List<NurseSiteMaintainData> listData = null;
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				if (tempItem.isNeedGetItem()) {
					setItemIds.add(tempItem.getId());
					tempItem.setNeedGetItem(false);
				}
			}
		}
		if (setItemIds.size() > 0) {
			listData = nurseSiteMaintainDataRepository.findListMaintainDataByParentIds(setItemIds);
		}
		if (listData != null) {
			for (int i = 0; i < listData.size(); ++i) {
				NurseSiteMaintainData tempData = listData.get(i);
				String parentId = tempData.getParentId();
				if (mapData.containsKey(parentId)) {
					mapData.get(parentId).add(tempData);
				} else {
					List<NurseSiteMaintainData> tempSetData = new ArrayList<NurseSiteMaintainData>();
					tempSetData.add(tempData);
					mapData.put(parentId, tempSetData);
				}
			}
		}
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				List<NurseSiteMaintainData> tempListData=mapData.get(tempItem.getId());
				if(tempListData!=null){
					tempItem.setListData(tempListData);
				}
			}
		}
	}

	private NurseSiteMaintainData findObjInList(List<NurseSiteMaintainData> list, String name) {
		for (int i = 0; i < list.size(); ++i) {
			NurseSiteMaintainData tempDate = list.get(i);
			String tempName = list.get(i).getCheckItem();
			if (tempName.equals(name)) {
				return list.remove(i);
			}
		}
		return null;
	}

	private UserAccount getUserAccount(String uid){
		UserAccount tempUser=null;
		tempUser=mapUserAccount.get(uid);
		if(tempUser==null){
			tempUser=userAccountRepository.getSimpleByUids(uid);
			if(tempUser==null){
				tempUser=new UserAccount();
			}
			mapUserAccount.put(uid, tempUser);
		}
		return tempUser;
	}
	private OrgInfo getOrgInfo(String uid){
		OrgInfo orgInfo=null;
		orgInfo=mapOrgInfo.get(uid);
		if(orgInfo==null){
			orgInfo=orgInfoRepository.findSimpleByUid(uid);
			if(orgInfo!=null){
				mapOrgInfo.put(uid, orgInfo);
			}
		}
		return orgInfo;
	}
	private void getAssetInfo(Set<String> setAssetUids){
		List<AssetInfo> listAssetInfo=null;
		Set<String> setNewAssetUids=new HashSet<String>();
		if(setAssetUids.size()>0){
			for(String uid:setAssetUids){
				if(mapAssetInfo.get(uid)==null){
					setNewAssetUids.add(uid);
				}
			}
			if(setNewAssetUids.size()>0){
				listAssetInfo = assetInfoRepository.getSimpleDataByUids(setNewAssetUids);
				if(listAssetInfo!=null && listAssetInfo.size()>0){
					for(int i=0;i<listAssetInfo.size();++i){
						mapAssetInfo.put(listAssetInfo.get(i).getUid(), listAssetInfo.get(i));
					}
				}
			}
		}
	}
	private AssetInfo getAssetInfo(String uid){
		AssetInfo tempAssetInfo=null;
		tempAssetInfo=mapAssetInfo.get(uid);
		if(tempAssetInfo==null){
			tempAssetInfo=assetInfoRepository.getSimpleDataByUids(uid);
			if(tempAssetInfo==null){
				tempAssetInfo=new AssetInfo();
			}
			mapAssetInfo.put(uid, tempAssetInfo);
		}
		return tempAssetInfo;
	}

	public void supplementMaintain(NurseSiteMaintain tempMaintain,int pageSize,int page,boolean all) {
		UserAccount modifyBy=null;
		UserAccount createBy=null;
		String modifyUid=tempMaintain.getModifyByUid();
		String createUid=tempMaintain.getCreateByUid();
		if (StringUtils.isNotEmpty(modifyUid)) {
			modifyBy=getUserAccount(modifyUid);
		}
		if (StringUtils.isNotEmpty(createUid)) {
			createBy=getUserAccount(createUid);
		}
		tempMaintain.setModifyBy(modifyBy);
		tempMaintain.setCreateBy(createBy);
		//items
		List<NurseSiteMaintainItem> listItem = ListUtil.empty();
		UserAccount currentUser = UserContextService.getCurrentUserAccount();
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return;
		}
		Wrapper<NurseSiteMaintainItem> wrapper = new EntityWrapper<>();
		if (currentUser.isEnableAssetTypeGroup()) {
			wrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
		}
		wrapper.eq("nsmi.parent_id", tempMaintain.getId());
		Pagination pagination;
		if(all){
//			listItem = findMaintainItemByParentId(tempMaintain.getId());
			pagination = new Pagination(1, 5000);
		} else{
			pagination = new Pagination(page + 1, pageSize);
//			listItem = findMaintainItemByParentId(tempMaintain.getId(),pageSize,page);
		}
		List<NurseSiteMaintainItem> joinAssetList = nurseSiteMaintainItemMapper.findJoinAssetList(pagination, wrapper);

		if (ObjectUtil.isNotEmpty(joinAssetList)) {
			Set<String> ids = joinAssetList.stream().map(NurseSiteMaintainItem::getId).collect(Collectors.toSet());
			listItem = nurseSiteMaintainItemRepository.findByIdIn(ids);
		}
		tempMaintain.setListNurseSiteMaintainItem(listItem);
		supplementMaintainItem(listItem);
	}

	public void supplementMaintainWithoutItem(List<NurseSiteMaintain> listTempMaintain) {
		Set<String> setModifyByUids = new HashSet<>();
		Map<String, UserAccount> mapModifyBy = new HashMap<>();
		List<UserAccount> listModify = null;
		if (ObjectUtil.isNotEmpty(listTempMaintain)) {
			listTempMaintain.sort((item,next)->next.getScheduleDate().compareTo(item.getScheduleDate()));
			setModifyByUids = listTempMaintain.stream().map(NurseSiteMaintain::getCreateByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
			setModifyByUids.addAll(listTempMaintain.stream().map(NurseSiteMaintain::getModifyByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet()));
		}
		if (ObjectUtil.isNotEmpty(setModifyByUids)) {
			listModify = userAccountRepository.getSimpleByUids(setModifyByUids);
		}
		if (listModify != null) {
			for (UserAccount tempUser : listModify) {
				mapModifyBy.put(tempUser.getUid(), tempUser);
			}
		}
		if (ObjectUtil.isNotEmpty(listTempMaintain)) {
			for (NurseSiteMaintain tempMaintain : listTempMaintain) {
				if (StringUtils.isNotEmpty(tempMaintain.getModifyByUid())) {
					tempMaintain.setModifyBy(mapModifyBy.get(tempMaintain.getModifyByUid()));
				}
				if (StringUtils.isNotEmpty(tempMaintain.getCreateByUid())) {
					tempMaintain.setCreateBy(mapModifyBy.get(tempMaintain.getCreateByUid()));
				}
			}
		}
	}
	public void checkRightAll(NurseSiteMaintain obj){
		List<NurseSiteMaintainItem> listItem=obj.getListNurseSiteMaintainItem();
		List<String> listCheckData=obj.getCheckData();
		boolean blnFilter=false;
		if(listCheckData!=null && listCheckData.size()>0){
			blnFilter=true;
		}
		if(listItem!=null){
			Iterator<NurseSiteMaintainItem> it=listItem.iterator();
			while(it.hasNext()){
				NurseSiteMaintainItem tempItem=it.next();
				List<NurseSiteMaintainData> listData=tempItem.getListData();
				Iterator<NurseSiteMaintainData> itData=listData.iterator();
				while(itData.hasNext()){
					NurseSiteMaintainData tempData=itData.next();
					if(blnFilter){
						if(listCheckData.contains(tempData.getCheckItem())){
							tempData.setCheckValue(true);
						}
					}
					else{
						tempData.setCheckValue(true);
					}
				}
			}
		}
	}
	private boolean updateMaintainItem(NurseSiteMaintain obj, Date date, UserAccount user) {
		List<NurseSiteMaintainItem> listItem = obj.getListNurseSiteMaintainItem();
		if(listItem==null || listItem.size()==0){
			return false;
		}
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		int itemStatus = 0;
		boolean hasNotChecked = false;
		Calendar ca=Calendar.getInstance();
		ca.setTime(obj.getScheduleDate());
		ca.add(Calendar.DAY_OF_YEAR, 1);
		Date sheculeDate=ca.getTime();
		while (it.hasNext()) {
			itemStatus = 0;
			NurseSiteMaintainItem tempItem = it.next();
			tempItem.setCheckDate(date);
			tempItem.setModifyBy(user);
			tempItem.setModifyByUid(user.getUid());
			tempItem.setModifyDate(date);
			List<NurseSiteMaintainData> listData = tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData = listData.iterator();
			Calendar calendar1 = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
			calendar1.setTime(tempItem.getCheckDate());
			calendar2.setTime(obj.getScheduleDate());
			boolean delayNextMonth =  calendar1.get(Calendar.MONTH)>calendar2.get(Calendar.MONTH) ? true : false;

			if(delayNextMonth) {
				UserContextService userContextService = WebUtil.getBean(UserContextService.class);
				if(userContextService.accessContainsOfJiangNing()) {  //江宁医院，delay到下个月，只有科长（医学工程处主任）才能补单
					if(!userContextService.hasRole("AssetHead")) {
						WebUtil.addErrorMessage("超过补单时间！");
						return false;
					}
				}
			}
//			boolean delay=tempItem.getCheckDate().after(sheculeDate)?true:false;
			boolean hasComment=StringUtils.isNotEmpty(tempItem.getComment())?true:false;
			//0：未检查,1:已检查，4进行中,2:通过没commnet没有delay，5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，3有问题，延期了,8，有问题，没有延期
            String itemId = tempItem.getId();
            List<NurseSiteMaintainData>  list= nurseSiteMaintainDataRepository.findLByParentId(itemId);
            for (NurseSiteMaintainData nurseSiteMaintainDataNew : listData){
                for (NurseSiteMaintainData nurseSiteMaintainDataOld : list){
                    if(nurseSiteMaintainDataNew.getId().equals(nurseSiteMaintainDataOld.getId())){
                        Boolean checkValueNew = nurseSiteMaintainDataNew.getCheckValue();
						String variableTextNew = nurseSiteMaintainDataNew.getVariableText();
						Boolean checkValueOld = nurseSiteMaintainDataOld.getCheckValue();
						String variableTextOld = nurseSiteMaintainDataNew.getVariableText();
						if((checkValueNew != null && !checkValueNew.equals(checkValueOld))
								|| (StringUtils.isNotBlank(variableTextNew) && !variableTextNew.equals(variableTextOld))){
                            tempItem.setUpdateByUid(user.getUid());
							tempItem.setUpdateDate(date);
                        }
						if((checkValueOld != null && !checkValueOld.equals(checkValueNew))
							   || (StringUtils.isNotBlank(variableTextOld) && !variableTextOld.equals(variableTextNew))){
							tempItem.setUpdateByUid(user.getUid());
							tempItem.setUpdateDate(date);
						}
                    }
                }
            }
            if(tempItem.getUpdateDate()!=null){
				boolean delay=tempItem.getUpdateDate().after(sheculeDate)?true:false;
				int nullNum=0;
				while (itData.hasNext()) {
					NurseSiteMaintainData tempData = itData.next();
					Boolean value = tempData.getCheckValue();
					if (value == null) {
						nullNum++;
						hasNotChecked = true;
						if (itemStatus == 2 || itemStatus == 5|| itemStatus == 6|| itemStatus == 7) {
							itemStatus = 4;
						}
					} else if (value) {
						if (itemStatus == 0) {
							if(delay&& hasComment){
								itemStatus = 6;
							}
							else if(delay){
								itemStatus = 7;
							}
							else if(hasComment){
								itemStatus = 5;
							}
							else{
								itemStatus = 2;
							}
						}
					} else {
						if(delay){
							itemStatus = 3;
						}
						else{
							itemStatus = 8;
						}
						break;
					}
				}
				if(nullNum > 0 && !(itemStatus ==3 || itemStatus ==8 )){
					itemStatus=nullNum==listData.size()?0:4;
				}
				tempItem.setStatus(itemStatus);

			}
		}
		return true;
	}
	//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
	public void updateMaintain(NurseSiteMaintain obj, Date date, UserAccount user) {
		Boolean blnNotChecked=false;
		Boolean blnNotPass=false;
		Boolean blnPass=false;
		Boolean blnRunning=false;
		int status=0;
		Integer[] errArr=new Integer[2];
		errArr[0]=3;
		errArr[1]=8;
		Integer[] passArr=new Integer[4];
		passArr[0]=2;
		passArr[1]=5;
		passArr[2]=6;
		passArr[3]=7;
		boolean unCheck=nurseSiteMaintainItemRepository.findUnCheck(obj.getId());
		boolean delay=nurseSiteMaintainItemRepository.findDelay(obj.getId());
		boolean hasComment=nurseSiteMaintainItemRepository.findHasComment(obj.getId());
		blnNotPass=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),Arrays.asList(errArr));
		if(blnNotPass!=null && blnNotPass){
			if(unCheck){
				status=9;
			}else{
				if(delay){
					status=3;
				}
				else{
					status=8;
				}
			}
		}
		else{
			blnRunning=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),4);
			if(blnRunning!=null && blnRunning){
				status=4;
			}
			else {
				blnNotChecked=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),0);
				blnPass=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),Arrays.asList(passArr));
				if(blnNotChecked!=null && blnPass!=null && blnNotChecked && blnPass){
					status=4;
				}
				else if(blnPass!=null && blnPass){
					if(hasComment && delay){
						status=6;
					}
					else if(hasComment){
						status=5;
					}
					else if(delay){
						status=7;
					}
					else{
						status=2;
					}
				}
				else if(blnNotChecked!=null && blnNotChecked){
					status=0;
				}
			}
		}
		obj.setStatus(status);
		obj.setCheckDate(date);
		obj.setModifyBy(user);
		obj.setModifyByUid(user.getUid());
		obj.setModifyDate(date);
		int noCheckCount = nurseSiteMaintainItemRepository.noCheckCount(obj.getId());
		if(noCheckCount == 0) {
			obj.setIsFinish(true);
		} else {
			obj.setIsFinish(false);
		}
	}

	private void completeMaintainData(List<NurseSiteMaintain> listMaintain) {
		Set<String> setParentIds = new HashSet<String>();
		Set<String> setModifyByUids = new HashSet<String>();
		List<NurseSiteMaintainItem> listMaintainItem = null;
		List<UserAccount> listModifyBy = null;
		Map<String, Set<NurseSiteMaintainItem>> mapItem = new HashMap<String, Set<NurseSiteMaintainItem>>();
		Map<String, UserAccount> mapUser = new HashMap<String, UserAccount>();
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintain tempMaintain = listMaintain.get(i);
			supplementMaintain(tempMaintain,0,0,true);
		}
		if (setParentIds.size() > 0) {
			listMaintainItem = nurseSiteMaintainItemRepository.findMaintainItemByParentId(setParentIds);
		}
		if (setModifyByUids.size() > 0) {
			listModifyBy = userAccountRepository.getByUids(setModifyByUids);
		}
		if (listMaintainItem != null && listMaintainItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listMaintainItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				String parentId = tempItem.getParentId();
				if (mapItem.containsKey(parentId)) {
					mapItem.get(parentId).add(tempItem);
				} else {
					Set<NurseSiteMaintainItem> tempSetItem = new HashSet<NurseSiteMaintainItem>();
					tempSetItem.add(tempItem);
					mapItem.put(tempItem.getParentId(), tempSetItem);
				}
			}
		}
		if (listModifyBy != null && listModifyBy.size() > 0) {
			for (int i = 0; i < listModifyBy.size(); ++i) {
				UserAccount tempUser = listModifyBy.get(i);
				mapUser.put(tempUser.getUid(), tempUser);
			}
		}
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintain tempMaintain = listMaintain.get(i);
			supplementMaintain(tempMaintain,0,0,true);
		}
	}

	public void batchUpdateMaintainDataAllRight(List<NurseSiteMaintain> listMaintain, String signatureId, Date date, UserAccount user) {
		completeMaintainData(listMaintain);
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintain tempMaintain = listMaintain.get(i);
			if (tempMaintain.getStatus() == 0) {
				if (StringUtils.isNotEmpty(signatureId)) {
					tempMaintain.setSignatureId(signatureId);
				}
				updateMaintainDataAllRight(tempMaintain, date, user);
			}
		}
	}

	public boolean completeMaintain(List<NurseSiteMaintain> listMaintain, String signatureId, Date date, UserAccount user) {
		Calendar ca=Calendar.getInstance();
		Date currentTime=ca.getTime();
		for(int i=0;i<listMaintain.size();++i){
			NurseSiteMaintain tempMaintain=listMaintain.get(i);
			if(tempMaintain.getStatus()!=0){
				continue;
			}
			Calendar tempCa=Calendar.getInstance();
			tempCa.setTime(tempMaintain.getScheduleDate());
			tempCa.add(Calendar.DAY_OF_YEAR, 1);
			int status=2;
			boolean delay=false;
			if(currentTime.after(tempCa.getTime())){
				delay=true;
			}
			if(delay){
				status=7;
			}
			nurseSiteMaintainRepository.completeMaintain(tempMaintain.getId(), signatureId, user.getUid(), date,status,true);
		}
		return true;
	}


//	public void nativeDeleteMaintain(List<NurseSiteMaintain> listMaintain) {
//		Set<String> setMaintainIds = new HashSet<String>();
//		Set<String> setMaintainItemIds = new HashSet<String>();
//		Set<String> setMaintainDataIds = new HashSet<String>();
//		if (listMaintain != null && listMaintain.size() > 0) {
//			for (int i = 0; i < listMaintain.size(); ++i) {
//				setMaintainIds.add(listMaintain.get(i).getId());
//			}
//		}
//		if (setMaintainIds.size() > 0) {
//			setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId(setMaintainIds);
//		}
//		if (setMaintainItemIds.size() > 0) {
//			setMaintainDataIds = nurseSiteMaintainDataRepository.findIdsByParentIds(setMaintainItemIds);
//		}
//		if (setMaintainDataIds.size() > 0) {
//			nurseSiteMaintainDataRepository.deleteMaintainData(setMaintainDataIds);
//		}
//		if (setMaintainItemIds.size() > 0) {
//			nurseSiteMaintainItemRepository.deleteMaintainItem(setMaintainItemIds);
//		}
//		if (setMaintainIds.size() > 0) {
//			nurseSiteMaintainRepository.deleteMaintain(setMaintainIds);
//		}
//	}

	public boolean updateMaintain(NurseSiteMaintain obj, boolean batchUpdate, UserAccount currentUser) {
		Date currentDate = new Date();
		if (batchUpdate) {
			// insert new record
			// 0：未检查,1:已检查，2:通过，3有问题
			if (obj.getStatus() == null || obj.getStatus() == 0) {
				obj.setCheckDate(currentDate);
				obj.setModifyBy(currentUser);
				obj.setModifyByUid(currentUser.getUid());
				obj.setModifyDate(currentDate);
			}
		} else {
			obj.setStatus(1);
			obj.setCheckDate(currentDate);
			obj.setModifyBy(currentUser);
			obj.setModifyByUid(currentUser.getUid());
			obj.setModifyDate(currentDate);
		}
		return true;
	}

	public NurseSiteMaintain createMaintainWithoutItem(Date date, UserAccount currentUser, NurseSiteMaintainConfig nurseSiteMaintainConfig, Integer sequence) {
		if (nurseSiteMaintainConfig.getType() != null && nurseSiteMaintainConfig.getType() != 1) {
			sequence = 0;
		}
		NurseSiteMaintain tempNurseSiteMaintain = new NurseSiteMaintain();
		Date currentDate = new Date();
		tempNurseSiteMaintain.setTenantUID(nurseSiteMaintainConfig.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(nurseSiteMaintainConfig.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(nurseSiteMaintainConfig.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(nurseSiteMaintainConfig.getSiteUID());
		tempNurseSiteMaintain.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
		tempNurseSiteMaintain.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
		tempNurseSiteMaintain.setCheckDate(null);
		tempNurseSiteMaintain.setCreateBy(currentUser);
		tempNurseSiteMaintain.setCreateByUid(currentUser.getUid());
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyBy(currentUser);
		tempNurseSiteMaintain.setModifyByUid(currentUser.getUid());
		tempNurseSiteMaintain.setModifyDate(currentDate);
		tempNurseSiteMaintain.setScheduleDate(date);
		tempNurseSiteMaintain.setSequence(sequence);
		tempNurseSiteMaintain.setStatus(0);
		tempNurseSiteMaintain.setName(nurseSiteMaintainConfig.getName());
		tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
		return tempNurseSiteMaintain;
	}

	public List<NurseSiteMaintain> checkAgain(String name , Date currDate){
		return nurseSiteMaintainRepository.checkAgain(name,currDate);
	}

	public NurseSiteMaintain createMaintain(Date date, UserAccount currentUser, NurseSiteMaintainConfig nurseSiteMaintainConfig, List<AssetInfo> listCheckAssets,
			Integer sequence) {
		if (nurseSiteMaintainConfig.getType() != null && nurseSiteMaintainConfig.getType() != 1) {
			sequence = 0;
		}
		List<NurseSiteMaintainItem> listItem = loadConfigCheckItems(nurseSiteMaintainConfig, listCheckAssets);
		NurseSiteMaintain tempNurseSiteMaintain = new NurseSiteMaintain();
		Date currentDate = new Date();
		tempNurseSiteMaintain.setTenantUID(nurseSiteMaintainConfig.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(nurseSiteMaintainConfig.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(nurseSiteMaintainConfig.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(nurseSiteMaintainConfig.getSiteUID());
		tempNurseSiteMaintain.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
		tempNurseSiteMaintain.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
		tempNurseSiteMaintain.setCheckDate(null);
		tempNurseSiteMaintain.setCreateBy(currentUser);
		tempNurseSiteMaintain.setCreateByUid(currentUser.getUid());
		tempNurseSiteMaintain.setCreateDate(currentDate);
		tempNurseSiteMaintain.setModifyBy(currentUser);
		tempNurseSiteMaintain.setModifyByUid(currentUser.getUid());
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
			tempItem.setCreateByUid(currentUser.getUid());
			tempItem.setCreateDate(currentDate);
			tempItem.setModifyByUid(currentUser.getUid());
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

	public boolean saveMaintain(NurseSiteMaintain tempNurseSiteMaintain,Date date,UserAccount user) {
		if(!updateMaintainItem(tempNurseSiteMaintain,date,user)) return false;
		saveMaintainItem(tempNurseSiteMaintain);
		updateMaintain(tempNurseSiteMaintain,date,user);
		save(tempNurseSiteMaintain);
		return true;
	}

	public boolean completeMaintain(NurseSiteMaintain tempNurseSiteMaintain,Date date,UserAccount user) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(date);
		calendar2.setTime(tempNurseSiteMaintain.getScheduleDate());
		boolean delayNextMonth =  calendar1.get(Calendar.MONTH)>calendar2.get(Calendar.MONTH) ? true : false;
		if(delayNextMonth) {
			UserContextService userContextService = WebUtil.getBean(UserContextService.class);
			if(userContextService.accessContainsOfJiangNing()) {  //江宁医院，delay到下个月，只有科长（医学工程处主任）才能补单
				if(!userContextService.hasRole("AssetHead")) {
					WebUtil.addErrorMessage("超过补单时间！");
					return false;
				}
			}
		}
		Set<String> setMaintainIds = new HashSet<String>();
		Set<String> setMaintainItemIds = new HashSet<String>();
		Set<String> setMaintainDataIds = new HashSet<String>();
		List<NurseSiteMaintainData> listMaintainDataByParentIds =new ArrayList<>();
		setMaintainIds.add(tempNurseSiteMaintain.getId());
		if (setMaintainIds.size() > 0) {
			setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId(setMaintainIds);
		}
		if (setMaintainItemIds.size() > 0) {
			setMaintainDataIds = nurseSiteMaintainDataRepository.findIdsByParentIds(setMaintainItemIds);
			listMaintainDataByParentIds = nurseSiteMaintainDataRepository.findListMaintainDataByParentIds(setMaintainItemIds);
		}
		if (setMaintainDataIds.size() > 0) {
//			List<String> listCheckData=tempNurseSiteMaintain.getCheckData();
//			if(listCheckData!=null && listCheckData.size()>0){
//				nurseSiteMaintainDataRepository.completeMaintainData(setMaintainDataIds,listCheckData);
//			}
//			else{
//				nurseSiteMaintainDataRepository.completeMaintainData(setMaintainDataIds);
//			}
			List<NurseSiteMaintainConfigCheckItem> listCheckData = tempNurseSiteMaintain.getListCheckData();
			if(CollectionUtils.isNotEmpty(listCheckData) && CollectionUtils.isNotEmpty(listMaintainDataByParentIds)){
				Map<String, NurseSiteMaintainConfigCheckItem> collect = listCheckData.stream().collect(Collectors.toMap(NurseSiteMaintainConfigCheckItem::getName, (item) -> item));
				List<NurseSiteMaintainData> collect1 = listMaintainDataByParentIds.stream().map(item -> {
					NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = collect.get(item.getCheckItem());
					if(nurseSiteMaintainConfigCheckItem != null){
						Boolean checkValue = nurseSiteMaintainConfigCheckItem.getCheckValue();
						if(Boolean.TRUE.equals(checkValue)){
							item.setCheckValue(checkValue);
							item.setVariableText(nurseSiteMaintainConfigCheckItem.getDefaultValue());
						}

					}
					return item;
				}).collect(Collectors.toList());
				nurseSiteMaintainDataRepository.save(collect1);
			}
		}
		supplementMaintain(tempNurseSiteMaintain, 0, 0, true);
		updateMaintainItem(tempNurseSiteMaintain,date,user);
		updateTimeAndUser(tempNurseSiteMaintain,date,user);
		saveMaintainItem(tempNurseSiteMaintain);
		updateMaintain(tempNurseSiteMaintain,date,user);
		save(tempNurseSiteMaintain);
		return true;
	}
	private void updateTimeAndUser(NurseSiteMaintain obj, Date date, UserAccount user){
		if(obj == null)return;
		List<NurseSiteMaintainItem> listItem = obj.getListNurseSiteMaintainItem();
		if(CollectionUtils.isEmpty(listItem)) return;
		if(user == null)return;
		listItem.forEach(item->{
			item.setUpdateDate(date);
			item.setUpdateByUid(user.getUid());
		});
	}
	@Transactional
	public boolean saveMaintain(NurseSiteMaintain tempNurseSiteMaintain) {
		save(tempNurseSiteMaintain);
		return true;
	}

	public String saveMaintainSignature(String content, String id) {
		V2_BlobObject blobObject = null;
		try {
			blobObject = blobFileService.uploadFileString(content);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
			return null;
		}
		if (null != blobObject) {
			blobObject.setBoType(V2_BlobObject.BO_TYPE_ServiceRequest);
			blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
			blobObject.setBoUuid(id);
			blobFileService.saveBlobObject(blobObject);
			return blobObject.getObjectStorageId();
		} else {
			return null;
		}
	}

	public String getMaintainSignatureWeb(String signatureId) {
		StreamedContent streamedContent = blobFileService.getBlobObjectByStorageId(signatureId);
		InputStream input = streamedContent.getStream();
		byte[] b = new byte[1024];
		StringBuilder sb = new StringBuilder();
		String result = "";
		int i = 0;
		try {
			while ((i = input.read(b)) > 0) {
				String str = new String(b, 0, i);
				sb.append(str);
			}
			input.close();
		} catch (Exception e) {
			// TODO: handle exception
			try {
				input.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage());
			}
			LOGGER.error(e.getMessage());
			return "";
		}
		result = sb.toString();
		return result;
	}

	public StreamedContent getMaintainSignatureWeiXin(String signatureId) {
		return blobFileService.getBlobObjectByStorageId(signatureId);
	}

	public StreamedContent getMaintainSignature(String signatureId){
		String typePrefix="text";
		List<V2_BlobObject> blobObjectsByStorageId = blobFileService.getBlobObjectsByStorageId(signatureId);
		V2_BlobObject v2BlobObject;
		if(CollectionUtils.isNotEmpty(blobObjectsByStorageId) && (v2BlobObject=blobObjectsByStorageId.get(0)) != null && v2BlobObject.getObjectType().contains(typePrefix)){
				return getMaintainSignatureWebToStream(signatureId);
		}else{
			    return getMaintainSignatureWeiXin(signatureId);
		}
	}
	public StreamedContent getMaintainSignatureWebToStream(String signatureId) {
		String str = getMaintainSignatureWeb(signatureId);
		DefaultStreamedContent streamedContent = new DefaultStreamedContent();
		streamedContent.setStream(new ByteArrayInputStream(ImageUtils.convertPrimefacesBase64ToByte(str)));
		return streamedContent;
	}

	@Transactional
	public boolean createMaintainConfig(NurseSiteMaintainConfig tempConfig, UserAccount currentUser, List<AssetInfo> listCheckAssets) {
		String originName = tempConfig.getName();
		while (listCheckAssets.size() > 0) {
			String clinicalUid = "";
			String clinicalName = "";
			String tenantUID = "";
			String institutionUID = "";
			String hospitalUID = "";
			String siteUID = "";

			List<AssetInfo> listNew = new ArrayList<AssetInfo>();
			for (int i = 0; i > -1 && i < listCheckAssets.size(); ++i) {
				AssetInfo assetInfo = listCheckAssets.get(i);
				if (assetInfo != null) {
					if (StringUtils.isEmpty(clinicalUid)) {
						clinicalUid = assetInfo.getClinicalDeptUID();
						OrgInfo tempOrgInfo=getOrgInfo(clinicalUid);
						if(tempOrgInfo!=null){
							clinicalName = tempOrgInfo.getShortName();
						}
						else{
							listCheckAssets.remove(i);
							--i;
							continue;
						}
						tenantUID = assetInfo.getTenantUID();
						institutionUID = assetInfo.getInstitutionUID();
						hospitalUID = assetInfo.getHospitalUID();
						siteUID = assetInfo.getSiteUID();
					}
					if (clinicalUid != null && clinicalUid.equals(assetInfo.getClinicalDeptUID())) {
						listNew.add(assetInfo);
						listCheckAssets.remove(i);
						--i;
					}
				}
			}
			NurseSiteMaintainConfig newConfig = tempConfig.cloneObj();
			if (newConfig != null) {
				newConfig.setName(originName + "-" + clinicalName);
				newConfig.setClinicalDeptName(clinicalName);
				newConfig.setClinicalDeptUID(clinicalUid);
				newConfig.setTenantUID(tenantUID);
				newConfig.setInstitutionUID(institutionUID);
				newConfig.setHospitalUID(hospitalUID);
				newConfig.setSiteUID(siteUID);
				save(newConfig);
				saveConfigChildren(newConfig, listNew);
			}
		}
		return true;
	}

	@Transactional
	public boolean updateMaintainConfig(NurseSiteMaintainConfig tempConfig, UserAccount currentUser, List<AssetInfo> listCheckAssets) {
		// 记录‘修改将来未完成检查’时间参照点
		if(tempConfig.getApplyFeatureData()){
			tempConfig.setApplyFeatureTime(new Date());
		}

		save(tempConfig);
		saveConfigChildren(tempConfig, listCheckAssets);
		if (tempConfig.getApplyDirectly() || tempConfig.getApplyFeatureData()) {
//			updateUnCheckedMaintainData(tempConfig, currentUser, 0, listCheckAssets);
			addBatchJob("update",tempConfig, null, null, currentUser.getUid(),null);
		}
		return true;
	}

	public boolean saveConfigChildren(NurseSiteMaintainConfig tempConfig, List<AssetInfo> listCheckAssets) {
		String parentId = tempConfig.getId();
		if (StringUtils.isNotEmpty(parentId)) {
			List<NurseSiteMaintainConfigItem> list = findByParentId(parentId);
			if (list != null) {
				deleteNurseSiteMaintainConfigItem(list);
			}
			if (listCheckAssets != null && listCheckAssets.size() > 0) {
				list = new ArrayList<NurseSiteMaintainConfigItem>();
				for (int i = 0; i < listCheckAssets.size(); ++i) {
					NurseSiteMaintainConfigItem obj = new NurseSiteMaintainConfigItem();
					obj.setAssetUid(listCheckAssets.get(i).getUid());
					obj.setCreateBy(tempConfig.getCreateBy());
					obj.setCreateByUid(tempConfig.getCreateByUid());
					obj.setCreateDate(tempConfig.getCreateDate());
					obj.setHospitalUID(tempConfig.getHospitalUID());
					obj.setInstitutionUID(tempConfig.getInstitutionUID());
					obj.setClinicalDeptName(tempConfig.getClinicalDeptName());
					obj.setClinicalDeptUID(tempConfig.getClinicalDeptUID());
					obj.setModifyBy(tempConfig.getModifyBy());
					obj.setModifyByUid(tempConfig.getModifyByUid());
					obj.setModifyDate(tempConfig.getModifyDate());
					obj.setParentId(tempConfig.getId());
					obj.setSiteUID(tempConfig.getSiteUID());
					obj.setTenantUID(tempConfig.getTenantUID());
					obj.setType(tempConfig.getType());
					obj.setTypeValue(tempConfig.getTypeValue());
					list.add(obj);
				}
			}
			if (list != null && list.size() > 0) {
				saveNurseSiteMaintainConfigItem(list);
			}
		}
		return true;
	}

	@Transactional
	public void deleteMaintainConfig(List<NurseSiteMaintainConfig> configList, UserAccount currentUser) {

		for (NurseSiteMaintainConfig tempConfig : configList) {
			// YHAAO-15742 日常检查--批量删除日常检查设置 批量删除做逻辑删除
			// deleteConfigChildren(tempConfig.getId());
			// delete(tempConfig);
			tempConfig.setDeleted(true);
			tempConfig.setDeleteResult(false);
			tempConfig.setModifyByUid(currentUser.getUid());
			tempConfig.setModifyBy(currentUser);
			tempConfig.setModifyDate(new Date());
			nurseSiteMaintainConfigRepository.deleteToSoft(tempConfig.getDeleted(),tempConfig.getDeleteResult(),tempConfig.getModifyByUid(),tempConfig.getModifyDate(),tempConfig.getId());
		}
		addBatchJob("deleteCheckConfig", null, null, null, currentUser.getUid(),null);
	}

	public boolean deleteConfigChildren(String parentId) {
		if (StringUtils.isNotEmpty(parentId)) {
			List<NurseSiteMaintainConfigItem> list = findByParentId(parentId);
			if (list != null) {
				deleteNurseSiteMaintainConfigItem(list);
			}
		}
		return true;
	}

//	@Transactional
//	public void updateUnCheckedMaintainData(NurseSiteMaintainConfig nurseSiteMaintainConfig, UserAccount currentUser, Integer status, List<AssetInfo> listCheckAssets) {
//		Integer[] arrInt=new Integer[4];
//		arrInt[0]=2;
//		arrInt[0]=5;
//		arrInt[0]=6;
//		arrInt[0]=7;
//		List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findByConfigAndStatus(nurseSiteMaintainConfig.getId(), Arrays.asList(arrInt));
//		List<NurseSiteMaintain> listNewMaintain = new ArrayList<NurseSiteMaintain>();
//		if (listMaintain != null && listMaintain.size() > 0) {
//			for (int i = 0; i < listMaintain.size(); ++i) {
//				listNewMaintain
//						.add(createMaintain(listMaintain.get(i).getScheduleDate(), currentUser, nurseSiteMaintainConfig, listCheckAssets, listMaintain.get(i).getSequence()));
//			}
//		}
//		if (listNewMaintain.size() > 0) {
//			nurseSiteMaintainRepository.save(listNewMaintain);
//		}
//		nativeDeleteMaintain(listMaintain);
//	}

	public List<String> findAccessOrgUidListByUserUid(String userUid) {
		return uaaService.findAccessClinicalUidByUserUid(userUid).stream().distinct().collect(Collectors.toList());
	}

	public List<OrgInfo> findAccessOrgDeptList(String siteUid) {
		return uaaService.getClinicalDeptListBySiteUID(siteUid);
	}

	public boolean existMaintain(NurseSiteMaintain tempMaintain) {
		Boolean result = null;
		if (tempMaintain.getSequence() != null) {
			result = nurseSiteMaintainRepository.findExistsDefaultData(tempMaintain.getClinicalDeptUID(), tempMaintain.getScheduleDate(), tempMaintain.getScheduleDate(), tempMaintain.getConfigId(),
					tempMaintain.getSequence());
		} else {
			result = nurseSiteMaintainRepository.findExistsDefaultData(tempMaintain.getClinicalDeptUID(), tempMaintain.getScheduleDate(), tempMaintain.getScheduleDate(), tempMaintain.getConfigId());
		}

		return result == null ? false : result;
	}

	public List<Map<String, Object>> parseMaintainToMap(List<NurseSiteMaintain> listMaintain) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				NurseSiteMaintain tempMaintain = listMaintain.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				Integer status = tempMaintain.getStatus();
				String statusStr = "未检查";
				if (status == null || status == 0) {

				} else if (status == 2||status == 5||status == 6||status == 7) {
					statusStr = "已通过";
				} else if (status == 3||status == 8) {
					statusStr = "有问题";
				} else if (status == 4) {
					statusStr = "进行中";
				}
				tempMap.put("名称", tempMaintain.getShowTitle());
				if (tempMaintain.getModifyBy() != null && tempMaintain.getModifyBy().getName() != null) {
					tempMap.put(STR_MODIFIED_BY, tempMaintain.getModifyBy().getName());
				} else {
					tempMap.put(STR_MODIFIED_BY, "");
				}
				tempMap.put("计划检查日期", sdf.format(tempMaintain.getScheduleDate()));
				if (tempMaintain.getCheckDate() != null) {
					tempMap.put("实际检查日期", sdf2.format(tempMaintain.getCheckDate()));
				} else {
					tempMap.put("实际检查日期", "");
				}
				tempMap.put("状态", statusStr);
				tempMap.put("创建人", tempMaintain.getCreateBy().getName());
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
	}

	public byte[] writeMaintainDetailToZip(ExcelDocument doc, List<NurseSiteMaintain> listMaintain) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<NurseSiteMaintain> copyList = new ArrayList<NurseSiteMaintain>();
		copyList.addAll(listMaintain);
		copyList.sort(new NurseSiteMaintainComparator());
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)) {
			while (copyList.size() > 0) {
				String dateStr = null;
				List<NurseSiteMaintain> listNewMaintain = new ArrayList<NurseSiteMaintain>();
				for (int i = 0; i > -1 && i < copyList.size(); ++i) {
					NurseSiteMaintain tempMaintain = copyList.get(i);
					String tempDateStr = sdf.format(tempMaintain.getScheduleDate());
					if (dateStr == null) {
						dateStr = tempDateStr;
					}
					if (dateStr.equals(tempDateStr)) {
						listNewMaintain.add(tempMaintain);
						copyList.remove(i);
						--i;
					}
				}
				if (listNewMaintain.size() > 0) {
					ExcelDocument tempDoc = null;
					try {
						zout.putNextEntry(new ZipEntry("NurseSiteMaintainDetail-" + dateStr + ".xlsx"));
						tempDoc = new ExcelDocument(doc.getStream(), ExcelDocument.ExcelType.XLSX);
						byte[] bytes = writeMaintainDetailToExcelByDate(tempDoc, listNewMaintain);
						if (bytes != null) {
							IOUtils.write(bytes, zout);
						}
						zout.closeEntry();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage());
					}
				}
			}
			zout.close();
			return bout.toByteArray();
		}
	}

	private byte[] writeMaintainDetailToExcelByDate(ExcelDocument doc, List<NurseSiteMaintain> listMaintain) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listMaintain.sort(new NurseSiteMaintainComparator());
		Map<String, List<NurseSiteMaintainItem>> mapAssetUidMaintainItem = new HashMap<String, List<NurseSiteMaintainItem>>();
		Map<String, String> mapAssetInfo = new HashMap<String, String>();
		List<String> header = new ArrayList<String>();
		List<String> secondHeader = new ArrayList<String>();
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintain tempMaintain = listMaintain.get(i);
			supplementMaintain(tempMaintain,0,0,true);
			List<NurseSiteMaintainItem> listItem = tempMaintain.getListNurseSiteMaintainItem();
			if (listItem != null && listItem.size() > 0) {
				Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
				while (itItem.hasNext()) {
					NurseSiteMaintainItem tempItem = itItem.next();
					if (mapAssetUidMaintainItem.containsKey(tempItem.getAssetUid())) {
						List<NurseSiteMaintainItem> tempListItem = mapAssetUidMaintainItem.get(tempItem.getAssetUid());
						tempListItem.add(tempItem);
					} else {
						List<NurseSiteMaintainItem> tempListItem = new ArrayList<NurseSiteMaintainItem>();
						tempListItem.add(tempItem);
						mapAssetUidMaintainItem.put(tempItem.getAssetUid(), tempListItem);
						mapAssetInfo.put(tempItem.getAssetUid(), tempItem.getAssetInfo().getName());
					}
				}
			}
		}
		int headerColumnIndex = 1;
		// int secondHeaderColumnIndex=1;
		int columnIndex = 1;
		int rowIndex = 1;
		int keyRow = 1;
		int secondKeyRow = 2;
		if (mapAssetUidMaintainItem.size() > 0) {
			Iterator<List<NurseSiteMaintainItem>> it = mapAssetUidMaintainItem.values().iterator();
			while (it.hasNext()) {
				List<NurseSiteMaintainItem> tempListItem = it.next();
				if (tempListItem != null && tempListItem.size() > 0) {
					rowIndex++;
					columnIndex = 1;
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_NAME, doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getName());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_DEPTNUM, doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getDepartNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_SERIAL_NUM, doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getSerialNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_FUNCTIONTYPE, doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getFunctionType());
					// secondHeaderColumnIndex=writeExcelSheetHeader(secondHeader,
					// STR_SEQUENCE, doc, secondHeaderColumnIndex,2,false);
					// secondHeaderColumnIndex=5;
					for (int i = 0; i < tempListItem.size(); ++i) {
						NurseSiteMaintainItem tempItem = tempListItem.get(i);
						List<NurseSiteMaintainData> listData = tempItem.getListData();
						if (listData != null && listData.size() > 0) {
							Iterator<NurseSiteMaintainData> itData = listData.iterator();
							while (itData.hasNext()) {
								NurseSiteMaintainData tempData = itData.next();
								// doc.writeCellData(SHEET_Maintain, rowIndex,
								// headerColumnIndex++,tempData.getCheckItem()+"-"+
								// tempItem.getSequence());
								// secondHeaderColumnIndex=writeExcelSheetHeader(secondHeader,
								// tempItem.getSequenceName(), doc,
								// secondHeaderColumnIndex,2,true);
								String keyName = "";
								if (tempItem.getSequence() == null || tempItem.getSequence() <= 0) {
									keyName = tempData.getCheckItem();
									headerColumnIndex = writeExcelSheetHeader(header, keyName, doc, headerColumnIndex, 1, false);
									doc.writeRowData(SHEET_Maintain, keyRow, rowIndex, keyName, tempData.getStrValue());
								} else {
									keyName = tempData.getCheckItem() + "-" + tempItem.getSequenceName();
									headerColumnIndex = writeExcelSheetHeader(header, keyName, doc, headerColumnIndex, 1, false);
									doc.writeRowData(SHEET_Maintain, keyRow, rowIndex, keyName, tempData.getStrValue());
								}
								// doc.writeCellData(SHEET_Maintain, rowIndex,
								// columnIndex++, tempData.getStrValue());
							}
						}
					}
				}
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			doc.getWorkBook().write(out);
			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	public byte[] writeMaintainDetailToExcel(ExcelDocument doc, List<NurseSiteMaintain> listMaintain) throws IOException {
		long l1 = System.currentTimeMillis();
		LOGGER.error("NurseSiteMaintainService writeMaintainDetailToExcel start");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		listMaintain.sort(new NurseSiteMaintainComparator());
		Map<String, List<NurseSiteMaintainItem>> mapAssetUidMaintainItem = new HashMap<>();
		Set<String> setSignature = new HashSet<>();
		Map<String, Integer> mapSignature = new HashMap<>();
		Map<String, Integer> mapCheckItemSequence = new HashMap<>();
		List<String> header = new ArrayList<>();
		List<NurseSiteMaintainItem> itemList = ListUtil.newArrayList();
		for (NurseSiteMaintain tempMaintain : listMaintain) {
			List<NurseSiteMaintainItem> listItem = tempMaintain.getListNurseSiteMaintainItem();
			if (listItem != null && !listItem.isEmpty()) {
				itemList.addAll(listItem);
				for (NurseSiteMaintainItem tempItem : listItem) {
					if (mapAssetUidMaintainItem.containsKey(tempItem.getAssetUid())) {
						List<NurseSiteMaintainItem> tempListItem = mapAssetUidMaintainItem.get(tempItem.getAssetUid());
						tempListItem.add(tempItem);
					} else {
						List<NurseSiteMaintainItem> tempListItem = new ArrayList<>();
						tempListItem.add(tempItem);
						mapAssetUidMaintainItem.put(tempItem.getAssetUid(), tempListItem);
					}
				}
			}
			if (StringUtils.isNotEmpty(tempMaintain.getSignatureId())) {
				setSignature.add(tempMaintain.getSignatureId());
				mapSignature.put(tempMaintain.getSignatureId(), tempMaintain.getSignatureSourceType());
			}
		}
		Set<String> ids = itemList.stream().map(NurseSiteMaintainItem::getId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
		Set<String> assetUidList = itemList.stream().map(NurseSiteMaintainItem::getAssetUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
		Set<String> userUidList = itemList.stream().map(NurseSiteMaintainItem::getModifyByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
		ids.add("-1");assetUidList.add("-1");userUidList.add("-1");
		List<NurseSiteMaintainData> dataList = nurseSiteMaintainItemMapper.findDataList(ids);
		Map<String, List<NurseSiteMaintainData>> dataMap = dataList.stream().collect(Collectors.groupingBy(NurseSiteMaintainData::getParentId));
		List<AssetInfo> assetList = nurseSiteMaintainItemMapper.findAssetList(assetUidList);
		Map<String, AssetInfo> assetMap = assetList.stream().collect(Collectors.toMap(AssetInfo::getUid, t -> t));
		List<UserAccount> userList = nurseSiteMaintainItemMapper.findUserList(userUidList);
		Map<String, String> userMap = userList.stream().collect(Collectors.toMap(UserAccount::getUid, UserAccount::getName));
		List<NurseSiteMaintainConfig> configs = nurseSiteMaintainConfigRepository.fetchListByIds(listMaintain.stream().map(NurseSiteMaintain::getConfigId).collect(Collectors.toSet()));
		Map<String, List<NurseSiteMaintainConfig>> configMap = configs.stream().collect(Collectors.groupingBy(NurseSiteMaintainConfig::getId));

		int headerColumnIndex = 1;
		int columnIndex = 1;
		int keyRow = 1;
		int checkNameRow = keyRow + 1;
		int scheduleDateRow = keyRow + 2;
		int checkDateRow = keyRow + 3;
		int sequenceRow = keyRow + 4;
		int modifyByRow = keyRow + 5;
		int rowIndex = 7;
		int dataColumnIndex = 6;
		if (mapAssetUidMaintainItem.size() > 0) {
			LOGGER.error("NurseSiteMaintainService query end {}", System.currentTimeMillis() - l1);
			List<V2_BlobObject> v2BlobObjects = blobObjectRepository.fetchListByBoUUIds(ids);
			Set<String> objectIds = v2BlobObjects.stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toSet());
			Map<String, List<V2_BlobObject>> blobMap = v2BlobObjects.stream().collect(Collectors.groupingBy(V2_BlobObject::getBoUuid));
//			Map<String, List<V2_BlobObject>> blobMap = new HashMap<>();
			List<FileInfoResult> files = ListUtil.newArrayList();
			if (ObjectUtil.isNotEmpty(objectIds)) {
				files = microServiceUtil.getFiles(objectIds);
			}
			LOGGER.error("NurseSiteMaintainService getFiles end {}", System.currentTimeMillis() - l1);
			Map<String, byte[]> fileMap = files.stream().collect(Collectors.toMap(FileInfoResult::getObjectId, FileInfoResult::getBytes));
			for (List<NurseSiteMaintainItem> tempListItem : mapAssetUidMaintainItem.values()) {
				if (tempListItem != null && !tempListItem.isEmpty()) {
					columnIndex = 1;
					AssetInfo assetInfo = assetMap.get(tempListItem.get(0).getAssetUid());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_NAME, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, assetInfo.getName());
					headerColumnIndex = writeExcelSheetHeader(header, CLINICAL_DEPT_NAME, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, assetInfo.getClinicalDeptName());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_DEPTNUM, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, assetInfo.getDepartNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_SERIAL_NUM, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, assetInfo.getSerialNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_FUNCTIONTYPE, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex, assetInfo.getFunctionType());
					StringBuilder keyName = new StringBuilder();
					for (NurseSiteMaintainItem nurseSiteMaintainItem : tempListItem) {
						Map<String, List<NurseSiteMaintainCheckItem>> checkItemMap = Optional.ofNullable(configMap.get(nurseSiteMaintainItem.getConfigId()))
								.map(nurseSiteMaintainConfigs -> {
									if (nurseSiteMaintainConfigs.isEmpty()) {
										return new  HashMap<String, List<NurseSiteMaintainCheckItem>>();
									}
									NurseSiteMaintainConfig nurseSiteMaintainConfig = nurseSiteMaintainConfigs.get(0);
									String checkItemsDetail = nurseSiteMaintainConfig.getCheckItemsDetail();
									if (StringUtils.isBlank(checkItemsDetail)){
										return new  HashMap<String, List<NurseSiteMaintainCheckItem>>();
									}
									List<NurseSiteMaintainCheckItem> nurseSiteMaintainCheckItems = JSONObject.parseObject(checkItemsDetail, new TypeReference<List<NurseSiteMaintainCheckItem>>() {});
                                    return nurseSiteMaintainCheckItems.stream()
											.filter(i -> i.getName() != null || i.getKey() != null)
											.collect(Collectors.groupingBy(item -> item.getKey() != null ? item.getKey().toString() : item.getName()));
								})
								.orElse(new HashMap<>());
						mapCheckItemSequence.clear();
						List<NurseSiteMaintainData> listData = dataMap.get(nurseSiteMaintainItem.getId());
						listData.sort(Comparator.comparing(NurseSiteMaintainData::getCheckItem));
						keyName.delete(0, keyName.length());
						if (listData != null && !listData.isEmpty()) {
							for (NurseSiteMaintainData tempData : listData) {
								if (nurseSiteMaintainItem.getScheduleDate() != null) {
									keyName.append(sdf.format(nurseSiteMaintainItem.getScheduleDate())).append(",");
								} else {
									keyName.append(",");
								}
								keyName.append(nurseSiteMaintainItem.getConfigId()).append(",");
								keyName.append(nurseSiteMaintainItem.getSequenceName()).append(",");
								if (tempData.getCheckItem() != null) {
									String checkItemStr = tempData.getCheckItem();
									if (mapCheckItemSequence.containsKey(checkItemStr)) {
										mapCheckItemSequence.put(checkItemStr, mapCheckItemSequence.get(checkItemStr) + 1);
										keyName.append(checkItemStr).append(mapCheckItemSequence.get(checkItemStr)).append(",");
									} else {
										keyName.append(checkItemStr).append(",");
										mapCheckItemSequence.put(checkItemStr, 1);
									}
								} else {
									keyName.append(",");
								}
								headerColumnIndex = writeExcelSheetHeader(header, keyName.toString(), doc, headerColumnIndex, keyRow, false);
								List<NurseSiteMaintainCheckItem> nurseSiteMaintainCheckItems1 = checkItemMap.get(tempData.getIndex()!=null?tempData.getIndex().toString():tempData.getCheckItem());
								NurseSiteMaintainCheckItem nurseSiteMaintainCheckItem = Optional.ofNullable(nurseSiteMaintainCheckItems1)
										.map(list -> list.isEmpty() ? null : list.get(0))
										.orElse(new NurseSiteMaintainCheckItem());
								String description="";
								if(tempData.getVariableText()!=null  && !"false".equals(tempData.getVariableText()) && !"true".equals(tempData.getVariableText()) && nurseSiteMaintainCheckItem.getFieldType()!=null){
									switch (nurseSiteMaintainCheckItem.getFieldType()){
										case "Select":
											String options = nurseSiteMaintainCheckItem.getOptions();
			                                JSONArray objects = JSONArray.parseArray(options);
			                                List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
											Optional<TableOption> first = tableOptions.stream().filter(item -> item.getKey().equals(tempData.getVariableText())).findFirst();
											if(first.isPresent()){
												description=" "+first.get().getValue();
											}
											break;
										case "Date":
											try {
													Instant instant = Instant.ofEpochMilli(Long.parseLong(tempData.getVariableText()));
													LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
													DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
													description = " "+localDateTime.format(formatter);

											} catch (Exception e) {
												LOGGER.error("日期转换异常 tempData:{},nurseSiteMaintainCheckItem:{}", JSON.toJSONString(tempData),JSON.toJSONString(nurseSiteMaintainCheckItem));
											}
											break;
										case "Switch":
											break;
										default:
											description=" "+tempData.getVariableText();
											break;
									}
								}
								doc.writeRowData(SHEET_Maintain, header, rowIndex, keyName.toString(), tempData.getStrValue()+description);
								doc.writeRowData(SHEET_Maintain, header, modifyByRow, keyName.toString(), userMap.get(nurseSiteMaintainItem.getModifyByUid()));
								doc.writeRowData(SHEET_Maintain, header, checkDateRow, keyName.toString(), nurseSiteMaintainItem.getCheckDate());
								doc.writeRowData(SHEET_Maintain, header, checkNameRow, keyName.toString(), tempData.getCheckItem());
							}
							String tempComment = nurseSiteMaintainItem.getComment();
							keyName.delete(0, keyName.length());
							if (nurseSiteMaintainItem.getScheduleDate() != null) {
								keyName.append(sdf.format(nurseSiteMaintainItem.getScheduleDate())).append(",");
							} else {
								keyName.append(",");
							}
							keyName.append(nurseSiteMaintainItem.getConfigId()).append(",");
							keyName.append(nurseSiteMaintainItem.getSequenceName()).append(",");
							keyName.append(KEY_COMMENT).append(",");
							headerColumnIndex = writeExcelSheetHeader(header, keyName.toString(), doc, headerColumnIndex, keyRow, false);
							if (StringUtils.isEmpty(tempComment)) {
								tempComment = "";
							}
							doc.writeRowData(SHEET_Maintain, header, rowIndex, keyName.toString(), tempComment);
							doc.writeRowData(SHEET_Maintain, header, checkNameRow, keyName.toString(), STR_COMMENT);
						}
						List<V2_BlobObject> byBoUuid = blobMap.get(nurseSiteMaintainItem.getId());
						if (CollectionUtils.isNotEmpty(byBoUuid)) {
							for (int x = 0; x < byBoUuid.size(); x++) {
								V2_BlobObject blobObject = byBoUuid.get(x);
								try {
									keyName.delete(0, keyName.length());
									if (nurseSiteMaintainItem.getScheduleDate() != null) {
										keyName.append(sdf.format(nurseSiteMaintainItem.getScheduleDate())).append(",");
									} else {
										keyName.append(",");
									}
									keyName.append(nurseSiteMaintainItem.getConfigId()).append(",");
									keyName.append(nurseSiteMaintainItem.getSequenceName()).append(",");
									keyName.append("图片").append(x + 1).append(",");
									headerColumnIndex = writeExcelSheetHeader(header, keyName.toString(), doc, headerColumnIndex, keyRow, false);
									doc.writeRowData(SHEET_Maintain, header, checkNameRow, keyName.toString(), "图片" + (x + 1));
									Integer[] integers = doc.writeRowDataImg(SHEET_Maintain, header.toArray(new String[]{}), rowIndex, keyName.toString(), "");
									if (integers != null) {
										int x1 = integers[0];
										int y1 = integers[1];
										byte[] bytes = fileMap.get(blobObject.getObjectStorageId());
										ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
										BufferedImage bufferImg = ImageIO.read(inputStream);
										ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
										ImageIO.write(bufferImg, "png", byteArrayOut);
										XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) y1,
												x1, (short) (y1 + 1), x1 + 1);
										anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
										byteArrayOut.close();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					rowIndex++;
				}
			}
			LOGGER.error("NurseSiteMaintainService writeData end {}", System.currentTimeMillis() - l1);
			writeExcelSecondHeader(doc, keyRow, scheduleDateRow, dataColumnIndex, 0, STR_SCHEDULE_DATE, null);
			doc.writeCellData(SHEET_Maintain, checkDateRow, 1, STR_CHECK_DATE);
			writeExcelSecondHeader(doc, keyRow, sequenceRow, dataColumnIndex, 2, STR_SEQUENCE, 0);
			doc.writeCellData(SHEET_Maintain, modifyByRow, 1, STR_MODIFIED_BY);
			if (!setSignature.isEmpty()) {
				writeExcelSheetHeader(header, STR_SIGNATURE, doc, 1, rowIndex++, false);
				writeSignatureToExcel(doc, rowIndex++, dataColumnIndex, 3, 12, setSignature, mapSignature);
			}
			doc.removeRow(SHEET_Maintain, keyRow);
			writeExcelHeader(doc, keyRow);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			doc.getWorkBook().write(out);
			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	private Map<String,NurseSiteMaintainConfigCheckItem> initConfig(NurseSiteMaintainItem item){
		NurseSiteMaintainConfig byId = nurseSiteMaintainConfigRepository.findById(item.getConfigId());
		Map<String,NurseSiteMaintainConfigCheckItem> map=new HashMap<>();
		if(byId!=null){
			String strTemp=byId.getCheckItemsDetail();
			if(StringUtils.isNotEmpty(strTemp)){
				JSONArray jsonArray=JSONObject.parseArray(strTemp);
				for(int i=0;i<jsonArray.size();++i){
					NurseSiteMaintainCheckItem p1= JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
					NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(p1);
					map.put(p.getName(),p);
				}
			}
		}
		return map;
	}
	private String initVariableText(NurseSiteMaintainData tempData,Map<String,NurseSiteMaintainConfigCheckItem> configMap){
		String variableTextData = tempData.getVariableText();
		String variableText=variableTextData;
		if(StringUtils.isEmpty(variableTextData))return "";
		if(configMap == null || configMap.isEmpty())return variableTextData;
		NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = configMap.get(tempData.getCheckItem());
		if(nurseSiteMaintainConfigCheckItem == null)return variableTextData;
		String fieldType = nurseSiteMaintainConfigCheckItem.getFieldType();
		List<TableOption> options = nurseSiteMaintainConfigCheckItem.getOptions();
		if(StringUtils.isEmpty(fieldType)) return variableTextData;
		switch (fieldType){
			case "Text":
				break;
			case "Number":
				break;
			case "Select":
				Optional<TableOption> first = options.stream().filter(item -> item.getKey().equals(variableText)).findFirst();
				if(first.isPresent()){
					variableTextData=first.get().getValue();
				}
				break;
			case "Date":
				try {
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					variableTextData = format.format(new Date(Long.valueOf(variableTextData)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
		return StringUtils.isEmpty(variableTextData)?"":"-"+variableTextData;
	}
	private void writeSignatureToExcel(ExcelDocument doc, int row, int columnIndex, int width, int height, Set<String> signatureIds, Map<String, Integer> mapSignature) {
		try {
			long start = System.currentTimeMillis();
			List<FileInfoResult> files = microServiceUtil.getFiles(signatureIds);
			LOGGER.error("NurseSiteMaintainService getSignatureFiles {}", System.currentTimeMillis() - start);
			Map<String, byte[]> fileMap = files.stream().collect(Collectors.toMap(FileInfoResult::getObjectId, FileInfoResult::getBytes));
			for (String tempSignature : signatureIds) {
				Integer type = mapSignature.get(tempSignature);
				InputStream inputStream = null;
				byte[] bytes = fileMap.get(tempSignature);
				if (ObjectUtil.isEmpty(bytes)) {
					continue;
				}
				if (type == 1) {
					inputStream = new ByteArrayInputStream(bytes);
				} else if (type == 0) {
					InputStream input = new ByteArrayInputStream(bytes);
					byte[] b = new byte[1024];
					StringBuilder sb = new StringBuilder();
					int i;
					try {
						while ((i = input.read(b)) > 0) {
							String str = new String(b, 0, i);
							sb.append(str);
						}
						input.close();
					} catch (Exception e) {
						try {
							input.close();
						} catch (IOException e1) {
							LOGGER.error("NurseSiteMaintainService input close error {}", e.getMessage(), e);
						}
						LOGGER.error("NurseSiteMaintainService input read error {}", e.getMessage(), e);
					}
					String result = sb.toString();
					inputStream = new ByteArrayInputStream(ImageUtils.convertPrimefacesBase64ToByte(result));
				}
				doc.mergeCell(SHEET_Maintain, row, row + height - 1, columnIndex, columnIndex + width - 1);
				doc.drawPicture(doc.addPicture(inputStream, Workbook.PICTURE_TYPE_JPEG), SHEET_Maintain, row, columnIndex, width, height);
				columnIndex += width;
			}
			LOGGER.error("NurseSiteMaintainService writeSignatureFiles {}", System.currentTimeMillis() - start);
		} catch (Exception e) {
			LOGGER.error("NurseSiteMaintainService addPicture error {}", e.getMessage(), e);
		}
	}

	private void writeExcelHeader(ExcelDocument doc, int keyRow) {
		List<String> header = new ArrayList<>();
		writeExcelSheetHeader(header, STR_NURSESITEREPORT, doc, 1, keyRow, false);
		int headerColumnIndex = 1;
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_NAME, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, CLINICAL_DEPT_NAME, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_DEPTNUM, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_SERIAL_NUM, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_FUNCTIONTYPE, doc, headerColumnIndex, keyRow + 1, false);
	}

	private void writeExcelSecondHeader(ExcelDocument doc, int keyRow, int scheduleDateRow, int dataColumnIndex, int keyRowIndex, String label, Integer parentIndex) {
		List<String> listScheduleDate = new ArrayList<>();
		String[] headerStrRow = doc.getKeyRow(SHEET_Maintain, keyRow);
		for (int i = 0; i < headerStrRow.length; ++i) {
			if (i + 1 < dataColumnIndex) {

			} else {
				String tempStr = headerStrRow[i];
				String[] tempArr = tempStr.split(",");
				if (parentIndex == null) {
					listScheduleDate.add(tempArr[keyRowIndex]);
				} else {
					listScheduleDate.add(tempArr[keyRowIndex] + "," + tempArr[parentIndex]);
				}
			}
		}
		doc.mergeCell(SHEET_Maintain, scheduleDateRow, scheduleDateRow, 1, dataColumnIndex - 1);
		doc.writeCellData(SHEET_Maintain, scheduleDateRow, 1, label);
		String originScheduleDate = null;
		int originScheduleDateColumn = dataColumnIndex;
		for (int i = 0; i > -1 && i < listScheduleDate.size(); ++i) {
			if (originScheduleDate == null) {
				originScheduleDate = listScheduleDate.get(0);
				dataColumnIndex++;
				continue;
			}
			if (originScheduleDate.equals(listScheduleDate.get(i))) {
				listScheduleDate.remove(i);
				--i;
				dataColumnIndex++;
			} else {
				doc.mergeCell(SHEET_Maintain, scheduleDateRow, scheduleDateRow, originScheduleDateColumn, dataColumnIndex - 1);
				if (parentIndex == null) {
					doc.writeCellData(SHEET_Maintain, scheduleDateRow, originScheduleDateColumn, originScheduleDate);
				} else {
					doc.writeCellData(SHEET_Maintain, scheduleDateRow, originScheduleDateColumn, originScheduleDate.split(",")[0]);
				}
				originScheduleDate = listScheduleDate.get(i);
				originScheduleDateColumn = dataColumnIndex;
				dataColumnIndex++;
			}
		}
		doc.mergeCell(SHEET_Maintain, scheduleDateRow, scheduleDateRow, originScheduleDateColumn, dataColumnIndex - 1);
		if (parentIndex == null) {
			doc.writeCellData(SHEET_Maintain, scheduleDateRow, originScheduleDateColumn, originScheduleDate);
		} else {
			doc.writeCellData(SHEET_Maintain, scheduleDateRow, originScheduleDateColumn, originScheduleDate.split(",")[0]);
		}
	}

	private int writeExcelSheetHeader(List<String> header, String str, ExcelDocument doc, int headerIndex, int rowIndex, boolean add) {
		if (header.contains(str)) {
			if (add) {
				headerIndex++;
			}
			return headerIndex;
		} else {
			doc.writeCellData(SHEET_Maintain, rowIndex, headerIndex++, str);
			header.add(str);
		}
		return headerIndex;
	}

	public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {

		Integer keyRow = 1;
		Integer dataRow = 2;

		for (Map<String, Object> item : data) {
			doc.writeRowData(SHEET_Maintain, keyRow, dataRow++, item);
		}

		return Boolean.TRUE;
	}

	public void updateNurseSiteMaintainCheckItem(NurseSiteMaintainCheckItem item,String siteUid){
		List<SearchFilter> searchFilters = new ArrayList<>();
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,siteUid,"1*2+"));
		searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ,siteUid,"1*2+"));
		searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,siteUid,"1*2+"));
		searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,siteUid,"1*2+"));
		List<NurseSiteMaintainConfig> listConfig=nurseSiteMaintainConfigRepository.findBySearchFilter(searchFilters);
		List<NurseSiteMaintainConfig> listModifyConfig=new ArrayList<NurseSiteMaintainConfig>();
		if(listConfig!=null && listConfig.size()>0){
			boolean find=false;
			for(int i=0;i<listConfig.size();++i){
				find=false;
				NurseSiteMaintainConfig config=listConfig.get(i);
				String jsonStr=config.getCheckItemsDetail();
				if(StringUtils.isNotEmpty(jsonStr)){
					JSONArray arr=JSONArray.parseArray(jsonStr);
					for(int j=0;j<arr.size();++j){
						JSONObject jsonObj=arr.getJSONObject(j);
						NurseSiteMaintainCheckItem obj=JSONObject.parseObject(arr.getString(j), NurseSiteMaintainCheckItem.class);
						if(item.getId().equals(obj.getId())){
							find=true;
							jsonObj.put("name", item.getName());
							jsonObj.put("description", item.getDescription());
							jsonObj.put("fieldType", item.getFieldType());
							jsonObj.put("defaultValue", item.getDefaultValue());
							jsonObj.put("options", item.getOptions());
							jsonObj.put("required", item.getRequired());
						}
					}
					if(find){
						config.setCheckItemsDetail(arr.toJSONString());
						listModifyConfig.add(config);
					}
				}
			}
		}
		if(listModifyConfig.size()>0){
			nurseSiteMaintainConfigRepository.save(listModifyConfig);
		}
	}
	
	public void deleteNurseSiteMaintainCheckItem(NurseSiteMaintainCheckItem item,String siteUid){
		List<NurseSiteMaintainConfig> listConfig=nurseSiteMaintainConfigRepository.findBySiteUid(siteUid);
		List<NurseSiteMaintainConfig> listModifyConfig=new ArrayList<NurseSiteMaintainConfig>();
		if(listConfig!=null && listConfig.size()>0){
			boolean find=false;
			for(int i=0;i<listConfig.size();++i){
				find=false;
				NurseSiteMaintainConfig config=listConfig.get(i);
				String jsonStr=config.getCheckItemsDetail();
				if(StringUtils.isNotEmpty(jsonStr)){
					JSONArray arr=JSONArray.parseArray(jsonStr);
					for(int j=0;j<arr.size();++j){
						NurseSiteMaintainCheckItem obj=JSONObject.parseObject(arr.getString(j), NurseSiteMaintainCheckItem.class);
						if(item.getId().equals(obj.getId())){
							find=true;
							arr.remove(j);
							--j;
						}
					}
					if(find){
						config.setCheckItemsDetail(arr.toJSONString());
						listModifyConfig.add(config);
					}
				}
			}
		}
		if(listModifyConfig.size()>0){
			nurseSiteMaintainConfigRepository.save(listModifyConfig);
		}
	}

	public List<NurseSiteMaintainExport> getMaintainDataList(String assetUid) {
		if(StringUtils.isBlank(assetUid)) return null;
		Date date=Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		List<NurseSiteMaintainItem> maintainDataList = nurseSiteMaintainItemRepository.getMaintainDataList(assetUid,date);
		if(CollectionUtils.isNotEmpty(maintainDataList)){
			return maintainDataList.stream().map(item->{
				NurseSiteMaintainExport nurseSiteMaintainExport = new NurseSiteMaintainExport();
				NurseSiteMaintain byId = nurseSiteMaintainRepository.findById(item.getParentId());
				if(byId != null){
					UserAccount createBy = byId.getCreateBy();
					if(createBy == null){
						createBy= userAccountRepository.getByUid(byId.getCreateByUid());
					}
					nurseSiteMaintainExport.setPrincipal(createBy.getName());
					nurseSiteMaintainExport.setStatus(setStatus(byId.getStatus()));
				}
				nurseSiteMaintainExport.setCheckDate(item.getCheckDate());
				nurseSiteMaintainExport.setScheduleDate(item.getScheduleDate());
				String updateByUid = item.getUpdateByUid();
				if(StringUtils.isNotBlank(updateByUid)){
					UserAccount byUid = userAccountRepository.getByUid(updateByUid);
					if(byUid != null){
						nurseSiteMaintainExport.setChecker(byUid.getName());
					}
				}else{
					nurseSiteMaintainExport.setChecker("");
				}
				return nurseSiteMaintainExport;
			}).collect(Collectors.toList());
		}
		return null;
	}
	private String setStatus(Integer status){
		//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
		String str=null;
		switch (status){
			case 0:
				str="未检查";
				break;
			case 2:
			case 5:
			case 6:
			case 7:
				str="已通过";
				break;
			case 3:
			case 8:
				str="有问题";
				break;
			case 4:
				str="进行中";
				break;
			default:
				str="";
		}
		return str;
	}
	public static String[] basicInfo={"院区","科室","设备名称","设备编号","序列号","型号"};
	public StreamedContent exportNurseSiteMaintainByAssetInfoId(AssetInfo assetInfo, Date startDate, Date endDate) {
		InputStream inputStream =null;
		try {
			Map<String, List<NurseSiteMaintainExport>> nurseSiteMaintainExport = getNurseSiteMaintainExport(assetInfo.getUid(), startDate, endDate);
			List<Map<String, String>> maps = setAssetBasicInfo(assetInfo);
			inputStream = new NurseSiteMaintainExportUtil().export(maps, nurseSiteMaintainExport,this);
			return new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", "NurseSiteMaintainData.xlsx");
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
	private Map<String,List<NurseSiteMaintainExport>> getDataTest(){
		List<NurseSiteMaintainExport> maintainItemDataList=new ArrayList<>();
		for (int i=0;i<10;i++){
			NurseSiteMaintainExport item=new NurseSiteMaintainExport();
			item.setCheckDate(new Date());
			item.setScheduleDate(new Date());
			item.setPrincipal("3277e91babd04081b185bd0c2d15e415");
			item.setChecker("3277e91babd04081b185bd0c2d15e415");
			item.setComment("123");
			item.setSignatureId("632ecc7910eec84f18700b3e");
			item.setCheckData(getNurseSiteMaintainData(i));
			if(i%2==0){
				item.setConfigId("2c91898b6c68d696016c694819650039");
			}else{
				item.setConfigId("2c91898b6c68d696016c6948197e003f");
			}

			maintainItemDataList.add(item);
		}
		Map<String,List<NurseSiteMaintainExport>> result=maintainItemDataList.stream().collect(Collectors.groupingBy(NurseSiteMaintainExport::getConfigId));
		return result;
	}
	private List<NurseSiteMaintainData> getNurseSiteMaintainData(int y){
		List<NurseSiteMaintainData> list=new ArrayList<>();
		for (int i = 0; i < y; i++) {
			NurseSiteMaintainData data=new NurseSiteMaintainData();
			data.setCheckItem("检查项"+i);
			data.setCheckValue(i<3);
			list.add(data);
		}
		return list;
	}
	private Map<String,List<NurseSiteMaintainExport>> getNurseSiteMaintainExport(String assetUid, Date startDate, Date endDate){
		List<NurseSiteMaintainItem> maintainItemDataList = nurseSiteMaintainItemRepository.getMaintainItemDataList(assetUid, startDate, endDate);
		if(CollectionUtils.isEmpty(maintainItemDataList)) return null;
		Set<String> collect1 = maintainItemDataList.stream().map(item -> item.getParentId()).collect(Collectors.toSet());
		List<NurseSiteMaintain> nurseSiteMaintainList=nurseSiteMaintainRepository.findByIds(collect1);
		if(CollectionUtils.isEmpty(nurseSiteMaintainList)) return null;
		Map<String, String>  signatureIds = nurseSiteMaintainList.stream().filter(item->StringUtils.isNotBlank(item.getSignatureId())).collect(Collectors.toMap(NurseSiteMaintain::getId, NurseSiteMaintain::getSignatureId));
		Map<String, String> signatureIdMap=signatureIds;
		Map<String, List<NurseSiteMaintainExport>> collect = maintainItemDataList.stream().map(item -> {
			NurseSiteMaintainExport nurseSiteMaintainExport = new NurseSiteMaintainExport();
			nurseSiteMaintainExport.setScheduleDate(item.getScheduleDate());
			nurseSiteMaintainExport.setCheckDate(item.getCheckDate());
/*			nurseSiteMaintainExport.setPrincipal(getUserName(item.getCreateByUid()));*/
			nurseSiteMaintainExport.setChecker(getUserName(item.getUpdateByUid()));
			String signatureId = item.getSignatureId();
			if ((StringUtils.isBlank(signatureId)|| "".equals(signatureId)) && signatureIdMap != null && signatureIdMap.size() > 0) {
				String s = signatureIdMap.get(item.getParentId());
				signatureId = StringUtils.isBlank(s)?"":s;
			}
			nurseSiteMaintainExport.setConfigId(item.getConfigId());
			nurseSiteMaintainExport.setSignatureId(signatureId);
			nurseSiteMaintainExport.setComment(item.getComment());
			nurseSiteMaintainExport.setCheckData(getNurseSiteMaintainData(item.getId()));
			return nurseSiteMaintainExport;
		}).sorted((m1, m2) -> {
			Date scheduleDate = m1.getScheduleDate() == null ? new Date() : m1.getScheduleDate();
			Date scheduleDate1 = m2.getScheduleDate() == null ? new Date() : m2.getScheduleDate();
			return scheduleDate.compareTo(scheduleDate1);
		}).collect(Collectors.groupingBy(NurseSiteMaintainExport::getConfigId));
		return collect;
	}
	private List<NurseSiteMaintainData> getNurseSiteMaintainData(String uid){
		if(StringUtils.isBlank(uid))return null;
		return  nurseSiteMaintainDataRepository.findLByParentId(uid);
	}
	private String getUserName(String uid){
		if(StringUtils.isBlank(uid))return "";
		UserAccount byUid = userAccountRepository.getByUid(uid);
		if(byUid != null){
			return byUid.getName();
		}else{
			return "";
		}
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
	public InputStream getSignature(String signatureId){
			try {
				return microServiceUtil.downloadRepeatedReadFile(signatureId);
//				//BufferedImage bufferImg = ImageIO.read(new File("C:\\Users\\503263052\\Downloads\\图片2.png"));
//				BufferedImage bufferImg = ImageIO.read(inputStream);
//				ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
//				ImageIO.write(bufferImg, "png", byteArrayOut);
//				XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, ( short)0,
//						0, (short)1, 1);
//				anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
//				XSSFPicture picture = patriarch.createPicture(anchor, doc.getWorkBook().addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
				//byteArrayOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	public List<NurseSiteMaintain> searchMaintain(Map<String, Object> param) {
		return nurseSiteMaintainMapper.searchMaintain(param);
	}

	/**
	 * 批量处理资产未完成的日常检查
	 * @param bathSelectedList asset list
	 */
	@Transactional
	public boolean processUnfinishedNurseSiteMaintain(List<AssetInfo> bathSelectedList, String comment){
		UserAccount loginUser = UserContextService.getCurrentUserAccount();
		List<String> uids = bathSelectedList.stream().map(AssetInfo::getUid).collect(Collectors.toList());
		List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findNoCheckedByAssetUidIn(uids);
		if(!org.springframework.util.StringUtils.isEmpty(nurseSiteMaintainItemList) && nurseSiteMaintainItemList.size()>0) {
			Set<String> maintainIds = nurseSiteMaintainItemList.stream().map(NurseSiteMaintainItem::getParentId).collect(Collectors.toSet());
			List<NurseSiteMaintainItem> maintainItemByParentId = nurseSiteMaintainItemRepository.findMaintainItemByParentId(maintainIds);
			Map<String, List<NurseSiteMaintainItem>> maintainIdsMap = maintainItemByParentId.stream().collect(Collectors.groupingBy(NurseSiteMaintainItem::getParentId));
			List<NurseSiteMaintainItem> after = nurseSiteMaintainItemList.stream().filter(item -> item.getScheduleDate().compareTo(new Date()) > 0).collect(Collectors.toList());
			List<NurseSiteMaintainItem> history = nurseSiteMaintainItemList.stream().filter(item -> item.getScheduleDate().compareTo(new Date()) < 0).collect(Collectors.toList());
			ArrayList<NurseSiteMaintainItem> nurseSiteMaintainItems = new ArrayList<>();
			for (NurseSiteMaintainItem nurseSiteMaintainItem : history) {
				if(!nurseSiteMaintainItem.getIsFinish()){
					nurseSiteMaintainItem.setCheckDate(new Date());
					nurseSiteMaintainItem.setIsFinish(true);
					nurseSiteMaintainItem.setUpdateByUid(loginUser.getUid());
					nurseSiteMaintainItem.setUpdateDate(new Date());
					nurseSiteMaintainItem.setModifyByUid(loginUser.getUid());
					nurseSiteMaintainItem.setComment(org.apache.commons.lang.StringUtils.isBlank(nurseSiteMaintainItem.getComment()) ? comment:nurseSiteMaintainItem.getComment() + "," + comment);
					nurseSiteMaintainItems.add(nurseSiteMaintainItem);
				}
			}
			Set<String> afterItemIds = after.stream().map(NurseSiteMaintainItem::getId).collect(Collectors.toSet());
			if(CollectionUtils.isNotEmpty(after)){
				nurseSiteMaintainDataRepository.deleteMaintainDataByParentId(new ArrayList<>(afterItemIds));
//                nurseSiteMaintainItemRepository.delete(after);
				nurseSiteMaintainItemRepository.deleteMaintainItem(afterItemIds);
				Set<String> strings = new HashSet<>();

				for (NurseSiteMaintainItem nurseSiteMaintainItem : after) {
					List<NurseSiteMaintainItem> nurseSiteMaintainItemsParent = maintainIdsMap.get(nurseSiteMaintainItem.getParentId());
					if(CollectionUtils.isNotEmpty(nurseSiteMaintainItemsParent) &&  nurseSiteMaintainItemsParent.size()==1 &&  nurseSiteMaintainItemsParent.get(0).getId().equals(nurseSiteMaintainItem.getId())){
//                        nurseSiteMaintainRepository.deleteById(nurseSiteMaintainItem.getParentId());
						strings.add(nurseSiteMaintainItem.getParentId());
					}
				}
				if(CollectionUtils.isNotEmpty(strings)){
					nurseSiteMaintainRepository.deleteMaintain(strings);
				}
			}
			Set<String> historyItemIds = history.stream().map(NurseSiteMaintainItem::getId).collect(Collectors.toSet());
			historyItemIds.add("-1");
			Set<NurseSiteMaintainData> historyDatas = nurseSiteMaintainDataRepository.findMaintainDataByParentIds(historyItemIds);
			ArrayList<NurseSiteMaintainData> nurseSiteMaintainData1 = new ArrayList<>();
			for (NurseSiteMaintainData nurseSiteMaintainData : historyDatas) {
				if(nurseSiteMaintainData.getCheckValue() == null ){
					nurseSiteMaintainData.setCheckValue(true);
					nurseSiteMaintainData1.add(nurseSiteMaintainData);
				}
			}
			if(CollectionUtils.isNotEmpty(nurseSiteMaintainItems)){
				for (NurseSiteMaintainItem nurseSiteMaintainItem : nurseSiteMaintainItems) {
					List<NurseSiteMaintainItem> nurseSiteMaintainItemsParent = maintainIdsMap.get(nurseSiteMaintainItem.getParentId());
					if (CollectionUtils.isNotEmpty(nurseSiteMaintainItemsParent)) {
						NurseSiteMaintain nurseSiteMaintain = nurseSiteMaintainRepository.findById(nurseSiteMaintainItem.getParentId());
						Map<String, List<NurseSiteMaintainItem>> historyMap = history.stream().collect(Collectors.groupingBy(NurseSiteMaintainItem::getParentId));

						List<NurseSiteMaintainItem> nurseSiteMaintainItems1 = historyMap.get(nurseSiteMaintainItem.getParentId());
						for (NurseSiteMaintainItem siteMaintainItem : nurseSiteMaintainItems1) {
							Map<String, List<NurseSiteMaintainData>> historyDataMap = historyDatas.stream().collect(Collectors.groupingBy(NurseSiteMaintainData::getParentId));
							siteMaintainItem.setListData(historyDataMap.get(siteMaintainItem.getId()));
						}
						nurseSiteMaintain.setListNurseSiteMaintainItem(nurseSiteMaintainItems1);
						saveMaintain(nurseSiteMaintain,new Date(),loginUser);
					}
				}
			}
			//删除检查项配置相关数据
			nurseSiteMaintainItemRepository.deleteMaintainConfigItemByAssetUidIn(uids);
			return true;
		}
		return false;
	}

	// private void formatExistMaintainDate(){
	// List<Date> tempList=new ArrayList<Date>();
	// for(int i=0;i<existMaintainDate.size();++i){
	// Date date=existMaintainDate.get(i);
	// try {
	// date=sdf.parse(sdf.format(date));
	// tempList.add(date);
	// } catch (ParseException e) {
	// LOGGER.error(e.getMessage());
	// }
	// }
	// existMaintainDate=tempList;
	// }
}
