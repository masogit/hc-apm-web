package com.ge.apm.service.nurseSite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainDataRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.BatchJob;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.service.utils.NurseSiteMaintainComparator;

import webapp.framework.dao.SearchFilter;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Service
public class NurseSiteMaintainService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainService.class);
	private static final String SHEET_Maintain = "Maintain List";
	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;

	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;

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

	private static final String STR_ASSET_NAME = "设备名";
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
	private static final String STR_NURSESITEREPORT = "护士站日常维护报表";

	public List<NurseSiteMaintainConfig> getMaintainConfigByOrgUids(Set<String> orgUids) {
		return nurseSiteMaintainConfigRepository.findByOrgUids(orgUids);
	}

	public NurseSiteMaintainConfig getMaintainConfigById(String id) {
		return nurseSiteMaintainConfigRepository.findById(id);
	}

	public List<NurseSiteMaintain> getDefaultMaintainData(Set<String> orgUids, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDefaultData(orgUids, startDate, endDate);
	}

	public List<NurseSiteMaintain> getDefaultMaintainDataByStatus(Set<String> orgUids, Date startDate, Date endDate, int status) {
		return nurseSiteMaintainRepository.findDefaultDataByStatus(orgUids, startDate, endDate, status);
	}

	public List<NurseSiteMaintain> getDefaultMaintainDataByStatus(Set<String> orgUids, Date startDate, Date endDate, String configId) {
		return nurseSiteMaintainRepository.findDefaultDataByStatus(orgUids, startDate, endDate, configId);
	}

	public List<NurseSiteMaintain> getDefaultMaintainDataByStatus(Set<String> orgUids, Date startDate, Date endDate, Integer status, String configId) {
		return nurseSiteMaintainRepository.findDefaultDataByStatus(orgUids, startDate, endDate, status, configId);
	}

	public List<NurseSiteMaintain> getDefaultMaintainDataChecked(Set<String> orgUids, Date startDate, Date endDate, String configId) {
		return nurseSiteMaintainRepository.findDefaultDataChecked(orgUids, startDate, endDate, configId);
	}

	public List<NurseSiteMaintain> getDataByClinicalOrgUid(List<String> listOrgUid, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDataByClinicalOrgUid(listOrgUid, startDate, endDate);
	}

	public List<NurseSiteMaintain> findBySearchFilter(List<SearchFilter> searchFilters, Sort sort) {
		return nurseSiteMaintainRepository.findBySearchFilter(searchFilters, sort);
	}

	public List<NurseSiteMaintain> findBySearchFilter(List<SearchFilter> searchFilters) {
		return nurseSiteMaintainRepository.findBySearchFilter(searchFilters);
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


	public List<OrgInfo> getListClinicalByUidAndParent(Set<String> listUid, String parentUid) {
		return orgInfoRepository.findByUidAndParentUid(listUid, parentUid);
	}

	public List<OrgInfo> getListParentSiteByUid(Set<String> listUid) {
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

	@Transactional
	public boolean deleteMaintain(NurseSiteMaintain maintain) {
		if (StringUtils.isNotEmpty(maintain.getSignatureId())) {
			blobFileService.deleteBlobFilesByStorageId(maintain.getSignatureId());
		}
		List<NurseSiteMaintain> listMaintain=new ArrayList<NurseSiteMaintain>();
		listMaintain.add(maintain);
		nativeDeleteMaintain(listMaintain);
		return true;
	}

	@Transactional
	public boolean deleteMaintain(List<NurseSiteMaintain> listMaintain) {
		if (listMaintain != null && listMaintain.size() > 0) {
			// Set<String> setParentId=new HashSet<String>();
			Set<String> setSignatureId = new HashSet<String>();
			for (int i = 0; i < listMaintain.size(); ++i) {
				NurseSiteMaintain tempMaintain = listMaintain.get(i);
				// setParentId.add(tempMaintain.getId());
				// tempMaintain.setListNurseSiteMaintainItem(null);
				if (StringUtils.isNotEmpty(tempMaintain.getSignatureId())) {
					setSignatureId.add(tempMaintain.getSignatureId());
				}
			}
			// Set<NurseSiteMaintainItem>
			// listItem=nurseSiteMaintainItemRepository.findMaintainItemByParentId(setParentId);
			if (setSignatureId.size() > 0) {
				blobFileService.deleteBlobFilesByStorageId(setSignatureId);
			}
			// nurseSiteMaintainItemRepository.delete(listItem);
			if (listMaintain != null && listMaintain.size() > 0) {
				nativeDeleteMaintain(listMaintain);
			}
		}
		return true;
	}

	@Transactional
	public void saveAndDelete(List<NurseSiteMaintain> obj, List<NurseSiteMaintain> obj2) {
		if (obj != null && obj.size() > 0) {
			nurseSiteMaintainRepository.save(obj);
		}
		if (obj2 != null && obj2.size() > 0) {
			deleteMaintain(obj2);
		}
	}

	public void save(NurseSiteMaintainConfig obj) {
		nurseSiteMaintainConfigRepository.save(obj);
	}

	public void delete(NurseSiteMaintain obj) {
		deleteMaintain(obj);
	}

	public void deleteNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> list) {
		nurseSiteMaintainConfigItemRepository.delete(list);
	}

	public void deleteMaintainItem(List<NurseSiteMaintainItem> list) {
		nurseSiteMaintainItemRepository.delete(list);
	}

	public void delete(NurseSiteMaintainConfig obj) {
		nurseSiteMaintainConfigRepository.delete(obj);
	}

	public List<String> getOrgListByUser(String userUid) {
		return uaaService.getOrgListByUser(userUid).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
	}

	public List<String> getOrgUidListByUser(String userUid) {
		return uaaService.getOrgListByUser(userUid).stream().map(item -> item.getUid()).collect(Collectors.toList());
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
			Boolean result=nurseSiteMaintainConfigItemRepository.findExistsAssetUid(assetUid);
			if(result!=null && result){
				addBatchJob("deleteAssetInfo", null, null, null, null,assetUid);
			}
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
		Set<String> setModifyByUids = new HashSet<String>();
		Set<String> setAssetInfoUids = new HashSet<String>();
		Set<String> setItemIds = new HashSet<String>();
		Set<String> setConfigId = new HashSet<String>();
		Map<String, UserAccount> mapModifyBy = new HashMap<String, UserAccount>();
		Map<String, AssetInfo> mapAssetInfo = new HashMap<String, AssetInfo>();
		Map<String, List<NurseSiteMaintainData>> mapData = new HashMap<String, List<NurseSiteMaintainData>>();
		Map<String, NurseSiteMaintainConfig> mapConfig = new HashMap<String, NurseSiteMaintainConfig>();
		List<UserAccount> listModify = null;
		List<AssetInfo> listAssetInfo = null;
		List<NurseSiteMaintainData> listData = null;
		List<NurseSiteMaintainConfig> listConfig = null;

		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItem tempItem = itItem.next();
				if (tempItem.isNeedGetItem()) {
					setModifyByUids.add(tempItem.getModifyByUid());
					setAssetInfoUids.add(tempItem.getAssetUid());
					setItemIds.add(tempItem.getId());
					tempItem.setNeedGetItem(false);
					String configId = tempItem.getConfigId();
					if (!setConfigId.contains(configId)) {
						setConfigId.add(configId);
					}
				}
			}
		}
		if (setModifyByUids.size() > 0) {
			listModify = userAccountRepository.getSimpleByUids(setModifyByUids);
		}
		if (setAssetInfoUids.size() > 0) {
			listAssetInfo = assetInfoRepository.getSimpleDataByUids(setAssetInfoUids);
		}
		if (setItemIds.size() > 0) {
			listData = nurseSiteMaintainDataRepository.findListMaintainDataByParentIds(setItemIds);
		}
		if (setConfigId.size() > 0) {
			listConfig = nurseSiteMaintainConfigRepository.fetchListByIds(setConfigId);
		}
		if (listModify != null) {
			for (int i = 0; i < listModify.size(); ++i) {
				UserAccount tempUser = listModify.get(i);
				mapModifyBy.put(tempUser.getUid(), tempUser);
			}
		}
		if (listAssetInfo != null) {
			for (int i = 0; i < listAssetInfo.size(); ++i) {
				AssetInfo tempAsset = listAssetInfo.get(i);
				mapAssetInfo.put(tempAsset.getUid(), tempAsset);
			}
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
				if (StringUtils.isNotEmpty(tempItem.getAssetUid())) {
					AssetInfo tempAsset=mapAssetInfo.get(tempItem.getAssetUid());
					if(tempAsset!=null){
						tempItem.setAssetInfo(tempAsset);
					}
				}
				if (StringUtils.isNotEmpty(tempItem.getModifyByUid())) {
					UserAccount tempAccount=mapModifyBy.get(tempItem.getModifyByUid());
					if(tempAccount!=null){
						tempItem.setModifyBy(tempAccount);
					}
				}
				List<NurseSiteMaintainData> tempListData=mapData.get(tempItem.getId());
				if(tempListData!=null){
					tempItem.setListData(tempListData);
				}
			}
		}
		// order data
		// if(listConfig!=null){
		// for(int i=0;i<listConfig.size();++i){
		// NurseSiteMaintainConfig tempConfig=listConfig.get(i);
		// mapConfig.put(tempConfig.getId(), tempConfig);
		// }
		// }
		// if(listItem!=null && listItem.size()>0){
		// Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
		// while (itItem.hasNext()) {
		// NurseSiteMaintainItem tempItem = itItem.next();
		// List<NurseSiteMaintainData> listTempData=tempItem.getListData();
		// if(listTempData!=null && listTempData.size()>0){
		// NurseSiteMaintainConfig
		// tempConfig=mapConfig.get(tempItem.getConfigId());
		// JSONArray
		// jsonArray=JSONArray.parseArray(tempConfig.getCheckItemsDetail());
		// List<NurseSiteMaintainData> listNewData=new
		// ArrayList<NurseSiteMaintainData>();
		// for(int i=0;i<jsonArray.size();++i){
		// JSONObject obj=jsonArray.getJSONObject(i);
		// String name=obj.getString("name");
		// NurseSiteMaintainData tempData=findObjInList(listTempData, name);
		// if(tempData!=null){
		// listNewData.add(tempData);
		// }
		// }
		// if(listTempData!=null && listTempData.size()>0){
		// listNewData.addAll(listTempData);
		// }
		// tempItem.setListData(listNewData);
		// }
		// }
		// }
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
		if (listData != null && listData.size()>0) {
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
			if (listItem != null && listItem.size() > 0) {
				Iterator<NurseSiteMaintainItem> itItem = listItem.iterator();
				while (itItem.hasNext()) {
					NurseSiteMaintainItem tempItem = itItem.next();
					tempItem.setListData(mapData.get(tempItem.getId()));
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

	public void supplementMaintain(NurseSiteMaintain tempMaintain,int pageSize,int page,boolean all) {
//		if (!tempMaintain.isNeedGetItem()) {
//			return;
//		}
		Set<String> setModifyByUids = new HashSet<String>();
		Map<String, UserAccount> mapModifyBy = new HashMap<String, UserAccount>();
		List<UserAccount> listModify = null;
		List<NurseSiteMaintainItem> listItem=null;
		if(all){
			listItem = findMaintainItemByParentId(tempMaintain.getId());
		}
		else{
			listItem = findMaintainItemByParentId(tempMaintain.getId(),pageSize,page);
		}
		setModifyByUids.add(tempMaintain.getCreateByUid());
		setModifyByUids.add(tempMaintain.getModifyByUid());
		if (setModifyByUids.size() > 0) {
			listModify = userAccountRepository.getSimpleByUids(setModifyByUids);
		}
		if (listModify != null) {
			for (int i = 0; i < listModify.size(); ++i) {
				UserAccount tempUser = listModify.get(i);
				mapModifyBy.put(tempUser.getUid(), tempUser);
			}
		}
		tempMaintain.setListNurseSiteMaintainItem(listItem);
		if (StringUtils.isNotEmpty(tempMaintain.getModifyByUid())) {
			tempMaintain.setModifyBy(mapModifyBy.get(tempMaintain.getModifyByUid()));
		}
		if (StringUtils.isNotEmpty(tempMaintain.getCreateByUid())) {
			tempMaintain.setCreateBy(mapModifyBy.get(tempMaintain.getCreateByUid()));
		}
//		tempMaintain.setNeedGetItem(false);
		supplementMaintainItem(listItem);
	}

	public void supplementMaintainWithoutItem(List<NurseSiteMaintain> listTempMaintain) {
		Set<String> setModifyByUids = new HashSet<String>();
		Map<String, UserAccount> mapModifyBy = new HashMap<String, UserAccount>();
		List<UserAccount> listModify = null;
		Set<String> nurseSiteMaintainIds = new HashSet<String>();
		if (listTempMaintain != null && listTempMaintain.size() > 0) {
			for (int i = 0; i < listTempMaintain.size(); ++i) {
				NurseSiteMaintain tempMaintain = listTempMaintain.get(i);
				nurseSiteMaintainIds.add(tempMaintain.getId());
				setModifyByUids.add(tempMaintain.getCreateByUid());
				setModifyByUids.add(tempMaintain.getModifyByUid());
			}
		}
		if (setModifyByUids.size() > 0) {
			listModify = userAccountRepository.getSimpleByUids(setModifyByUids);
		}
		if (listModify != null) {
			for (int i = 0; i < listModify.size(); ++i) {
				UserAccount tempUser = listModify.get(i);
				mapModifyBy.put(tempUser.getUid(), tempUser);
			}
		}
		if (listTempMaintain != null && listTempMaintain.size() > 0) {
			for (int i = 0; i < listTempMaintain.size(); ++i) {
				NurseSiteMaintain tempMaintain = listTempMaintain.get(i);
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
		if(listItem!=null){
			Iterator<NurseSiteMaintainItem> it=listItem.iterator();
			while(it.hasNext()){
				NurseSiteMaintainItem tempItem=it.next();
				List<NurseSiteMaintainData> listData=tempItem.getListData();
				Iterator<NurseSiteMaintainData> itData=listData.iterator();
				while(itData.hasNext()){
					NurseSiteMaintainData tempData=itData.next();
					tempData.setCheckValue(true);
				}
			}
		}
	}
	private void updateMaintainItem(NurseSiteMaintain obj, Date date, UserAccount user) {
		List<NurseSiteMaintainItem> listItem = obj.getListNurseSiteMaintainItem();
		if(listItem==null || listItem.size()==0){
			return;
		}
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		int itemStatus = 0;
		boolean hasNotChecked = false;
		while (it.hasNext()) {
			itemStatus = 0;
			NurseSiteMaintainItem tempItem = it.next();
			tempItem.setCheckDate(date);
			tempItem.setModifyBy(user);
			tempItem.setModifyByUid(user.getUid());
			tempItem.setModifyDate(date);
			List<NurseSiteMaintainData> listData = tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData = listData.iterator(); 
			// single,0:未检查，2：已通过，3：有问题，4：进行中
			while (itData.hasNext()) {
				NurseSiteMaintainData tempData = itData.next();
				Boolean value = tempData.getCheckValue();
				if (value == null) {
					hasNotChecked = true;
					if (itemStatus == 2) {
						itemStatus = 4;
					}
				} else if (value) {
					if (itemStatus == 0) {
						itemStatus = 2;
					}
				} else {
					itemStatus = 3;
					break;
				}
			}
			tempItem.setStatus(itemStatus);
		}
	}
	// single,0:未检查，2：已通过，3：有问题，4：进行中
	private void updateMaintain(NurseSiteMaintain obj, Date date, UserAccount user) {
		Boolean blnNotChecked=false;
		Boolean blnNotPass=false;
		Boolean blnPass=false;
		Boolean blnRunning=false;
		int status=0;
		blnNotPass=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),3);
		if(blnNotPass!=null && blnNotPass){
			status=3;
		}
		else{
			blnRunning=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),4);
			if(blnRunning!=null && blnRunning){
				status=4;
			}
			else {
				blnNotChecked=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),0);
				blnPass=nurseSiteMaintainItemRepository.findExistsItemStatus(obj.getId(),2);
				if(blnNotChecked!=null && blnPass!=null && blnNotChecked && blnPass){
					status=4;
				}
				else if(blnPass!=null && blnPass){
					status=2;
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
	}
	private void updateMaintainData(NurseSiteMaintain obj, Date date, UserAccount user) {
		List<NurseSiteMaintainItem> listItem = null;
		supplementMaintain(obj,0,0,true);
		listItem = obj.getListNurseSiteMaintainItem();
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		int itemStatus = 0;
		int objStatus = 0;
		boolean hasNotChecked = false;
		while (it.hasNext()) {
			itemStatus = 0;
			NurseSiteMaintainItem tempItem = it.next();
			tempItem.setCheckDate(date);
			tempItem.setModifyBy(user);
			tempItem.setModifyByUid(user.getUid());
			tempItem.setModifyDate(date);
			List<NurseSiteMaintainData> listData = tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData = listData.iterator();
			// single,0:未检查，2：已通过，3：有问题，4：进行中
			while (itData.hasNext()) {
				NurseSiteMaintainData tempData = itData.next();
				Boolean value = tempData.getCheckValue();
				if (value == null) {
					hasNotChecked = true;
					if (itemStatus == 2) {
						itemStatus = 4;
					}
				} else if (value) {
					if (itemStatus == 0) {
						itemStatus = 2;
					}
				} else {
					itemStatus = 3;
					break;
				}
			}
			tempItem.setStatus(itemStatus);
			if (itemStatus == 0) {
				if (objStatus == 2) {
					objStatus = 4;
				}
			} else if (itemStatus == 2) {
				if (objStatus == 0) {
					objStatus = 2;
				}
			} else if (itemStatus == 3) {
				objStatus = 3;
			} else if (itemStatus == 4) {
				if (objStatus != 3) {
					objStatus = 4;
				}
			}
		}
		if (hasNotChecked && objStatus == 2) {
			objStatus = 4;
		}
		obj.setStatus(objStatus);
		obj.setCheckDate(date);
		obj.setModifyBy(user);
		obj.setModifyByUid(user.getUid());
		obj.setModifyDate(date);
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

	public boolean batchCompleteMaintain(List<NurseSiteMaintain> listMaintain, String signatureId, Date date, UserAccount user) {
		Set<String> setMaintainIds = new HashSet<String>();
		for(int i=0;i<listMaintain.size();++i){
			setMaintainIds.add(listMaintain.get(i).getId());
		}
		nurseSiteMaintainRepository.completeMaintain(setMaintainIds, signatureId, user.getUid(), date);
		return true;
	}

	public void nativeDeleteMaintain(List<NurseSiteMaintain> listMaintain) {
		Set<String> setMaintainIds = new HashSet<String>();
		Set<String> setMaintainItemIds = new HashSet<String>();
		Set<String> setMaintainDataIds = new HashSet<String>();
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				setMaintainIds.add(listMaintain.get(i).getId());
			}
		}
		if (setMaintainIds.size() > 0) {
			setMaintainItemIds = nurseSiteMaintainItemRepository.findIdsByParentId(setMaintainIds);
		}
		if (setMaintainItemIds.size() > 0) {
			setMaintainDataIds = nurseSiteMaintainDataRepository.findIdsByParentIds(setMaintainItemIds);
		}
		if (setMaintainDataIds.size() > 0) {
			nurseSiteMaintainDataRepository.deleteMaintainData(setMaintainDataIds);
		}
		if (setMaintainItemIds.size() > 0) {
			nurseSiteMaintainItemRepository.deleteMaintainItem(setMaintainItemIds);
		}
		if (setMaintainIds.size() > 0) {
			nurseSiteMaintainRepository.deleteMaintain(setMaintainIds);
		}
	}

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
		updateMaintainItem(tempNurseSiteMaintain,date,user);
		saveMaintainItem(tempNurseSiteMaintain);
		updateMaintain(tempNurseSiteMaintain,date,user);
		save(tempNurseSiteMaintain);
		return true;
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
		StreamedContent streamedContent = blobFileService.getBlobObjectByStorageId(signatureId);
		return streamedContent;
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
						clinicalName = assetInfo.getClinicalDeptName();
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
		save(tempConfig);
		saveConfigChildren(tempConfig, listCheckAssets);
		if (tempConfig.getApplyDirectly()) {
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
	public boolean deleteMaintainConfig(NurseSiteMaintainConfig tempConfig) {
		Integer count = nurseSiteMaintainRepository.fetchCountByConfigId(tempConfig.getId());
		if (count != null && count > 0) {
			return false;
		} else {
			deleteConfigChildren(tempConfig.getId());
			delete(tempConfig);
		}
		return true;
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

	@Transactional
	public void updateUnCheckedMaintainData(NurseSiteMaintainConfig nurseSiteMaintainConfig, UserAccount currentUser, Integer status, List<AssetInfo> listCheckAssets) {
		List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findByConfigAndStatus(nurseSiteMaintainConfig.getId(), status);
		List<NurseSiteMaintain> listNewMaintain = new ArrayList<NurseSiteMaintain>();
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				listNewMaintain
						.add(createMaintain(listMaintain.get(i).getScheduleDate(), currentUser, nurseSiteMaintainConfig, listCheckAssets, listMaintain.get(i).getSequence()));
			}
		}
		if (listNewMaintain.size() > 0) {
			nurseSiteMaintainRepository.save(listNewMaintain);
		}
		nativeDeleteMaintain(listMaintain);
	}

	public Set<String> findAccessOrgUidListByUserUid(String userUid) {
		return uaaService.findAccessClinicalUidByUserUid(userUid);
	}

	public boolean existMaintain(NurseSiteMaintain tempMaintain) {
		Date startDate = tempMaintain.getScheduleDate();
		Date endDate = tempMaintain.getScheduleDate();
		Calendar ca = Calendar.getInstance();
		ca.setTime(endDate);
		ca.add(Calendar.DATE, 1);
		endDate = ca.getTime();
		Boolean result = null;
		if (tempMaintain.getSequence() != null) {
			result = nurseSiteMaintainRepository.findExistsDefaultData(tempMaintain.getClinicalDeptUID(), startDate, endDate, tempMaintain.getConfigId(),
					tempMaintain.getSequence());
		} else {
			result = nurseSiteMaintainRepository.findExistsDefaultData(tempMaintain.getClinicalDeptUID(), startDate, endDate, tempMaintain.getConfigId());
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

				} else if (status == 2) {
					statusStr = "已通过";
				} else if (status == 3) {
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listMaintain.sort(new NurseSiteMaintainComparator());
		Map<String, List<NurseSiteMaintainItem>> mapAssetUidMaintainItem = new HashMap<String, List<NurseSiteMaintainItem>>();
		Map<String, String> mapAssetInfo = new HashMap<String, String>();
		Set<String> setSignature = new HashSet<String>();
		Map<String, Integer> mapSignature = new HashMap<String, Integer>();
		Map<String, Integer> mapCheckItemSequence = new HashMap<String, Integer>();
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
			if (StringUtils.isNotEmpty(tempMaintain.getSignatureId())) {
				setSignature.add(tempMaintain.getSignatureId());
				mapSignature.put(tempMaintain.getSignatureId(), tempMaintain.getSignatureSourceType());
			}
		}
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
			Iterator<List<NurseSiteMaintainItem>> it = mapAssetUidMaintainItem.values().iterator();
			while (it.hasNext()) {
				List<NurseSiteMaintainItem> tempListItem = it.next();
				if (tempListItem != null && tempListItem.size() > 0) {
					columnIndex = 1;
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_NAME, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getName());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_DEPTNUM, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getDepartNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_SERIAL_NUM, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getSerialNum());
					headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_FUNCTIONTYPE, doc, headerColumnIndex, keyRow, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getFunctionType());
					// secondHeaderColumnIndex=writeExcelSheetHeader(secondHeader,
					// STR_SEQUENCE, doc, secondHeaderColumnIndex,2,false);
					// secondHeaderColumnIndex=5;
					for (int i = 0; i < tempListItem.size(); ++i) {
						mapCheckItemSequence.clear();
						NurseSiteMaintainItem tempItem = tempListItem.get(i);
						List<NurseSiteMaintainData> listData = tempItem.getListData();
						String keyName = "";
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
								// keyName =
								// sdf.format(tempItem.getScheduleDate())+","+tempItem.getCheckDate()+","+tempItem.getSequenceName()+","+tempData.getCheckItem();
								keyName = "";
								if (tempItem.getScheduleDate() != null) {
									keyName += sdf.format(tempItem.getScheduleDate()) + ",";
								} else {
									keyName += ",";
								}
								keyName += tempItem.getConfigId() + ",";
								keyName += tempItem.getSequenceName() + ",";
								if (tempData.getCheckItem() != null) {
									String checkItemStr = tempData.getCheckItem();
									if (mapCheckItemSequence.containsKey(checkItemStr)) {
										mapCheckItemSequence.put(checkItemStr, mapCheckItemSequence.get(checkItemStr) + 1);
										keyName += checkItemStr + mapCheckItemSequence.get(checkItemStr) + ",";
									} else {
										keyName += checkItemStr + ",";
										mapCheckItemSequence.put(checkItemStr, 1);
									}
								} else {
									keyName += ",";
								}
								headerColumnIndex = writeExcelSheetHeader(header, keyName, doc, headerColumnIndex, keyRow, false);
								doc.writeRowData(SHEET_Maintain, keyRow, rowIndex, keyName, tempData.getStrValue());
								doc.writeRowData(SHEET_Maintain, keyRow, modifyByRow, keyName, tempItem.getModifyBy().getName());
								doc.writeRowData(SHEET_Maintain, keyRow, checkDateRow, keyName, tempItem.getCheckDate());
								doc.writeRowData(SHEET_Maintain, keyRow, checkNameRow, keyName, tempData.getCheckItem());
								// doc.writeCellData(SHEET_Maintain, rowIndex,
								// columnIndex++, tempData.getStrValue());
							}
							String tempComment = tempItem.getComment();
							keyName = "";
							if (tempItem.getScheduleDate() != null) {
								keyName += sdf.format(tempItem.getScheduleDate()) + ",";
							} else {
								keyName += ",";
							}
							keyName += tempItem.getConfigId() + ",";
							keyName += tempItem.getSequenceName() + ",";
							keyName += KEY_COMMENT + ",";
							headerColumnIndex = writeExcelSheetHeader(header, keyName, doc, headerColumnIndex, keyRow, false);
							if (StringUtils.isEmpty(tempComment)) {
								tempComment = "";
							}
							doc.writeRowData(SHEET_Maintain, keyRow, rowIndex, keyName, tempComment);
							doc.writeRowData(SHEET_Maintain, keyRow, checkNameRow, keyName, STR_COMMENT);
						}
					}
					rowIndex++;
				}
			}
			// writeExcelSecondHeader(doc,keyRow,checkNameRow,dataColumnIndex,3,"检查项",null);
			writeExcelSecondHeader(doc, keyRow, scheduleDateRow, dataColumnIndex, 0, STR_SCHEDULE_DATE, null);
			// writeExcelSecondHeader(doc,keyRow,checkDateRow,dataColumnIndex,1,STR_CHECK_DATE,null);
			doc.writeCellData(SHEET_Maintain, checkDateRow, 1, STR_CHECK_DATE);
			writeExcelSecondHeader(doc, keyRow, sequenceRow, dataColumnIndex, 2, STR_SEQUENCE, 0);
			doc.writeCellData(SHEET_Maintain, modifyByRow, 1, STR_MODIFIED_BY);
			if (setSignature.size() > 0) {
				writeExcelSheetHeader(header, STR_SIGNATURE, doc, 1, rowIndex++, false);
				writeSignatureToExcel(doc, rowIndex++, dataColumnIndex, 3, 12, setSignature, mapSignature);
			}
			doc.removeRow(SHEET_Maintain, keyRow);
			// doc.writeCellData(SHEET_Maintain, keyRow, 1,
			// STR_NURSESITEREPORT);
			writeExcelHeader(doc, keyRow);
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

	private void writeSignatureToExcel(ExcelDocument doc, int row, int columnIndex, int width, int height, Set<String> signatureIds, Map<String, Integer> mapSignature) {
		try {
			Iterator<String> it = signatureIds.iterator();
			while (it.hasNext()) {
				String tempSignature = it.next();
				Integer type = mapSignature.get(tempSignature);
				StreamedContent sc = null;
				if (type == 1) {
					sc = getMaintainSignatureWeiXin(tempSignature);
				} else if (type == 0) {
					sc = getMaintainSignatureWebToStream(tempSignature);
				}
				if (sc != null) {
					doc.mergeCell(SHEET_Maintain, row, row + height - 1, columnIndex, columnIndex + width - 1);
					doc.drawPicture(doc.addPicture(sc.getStream(), Workbook.PICTURE_TYPE_JPEG), SHEET_Maintain, row, columnIndex, width, height);
					columnIndex += width;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
	}

	private void writeExcelHeader(ExcelDocument doc, int keyRow) {
		List<String> header = new ArrayList<String>();
		writeExcelSheetHeader(header, STR_NURSESITEREPORT, doc, 1, keyRow, false);
		int headerColumnIndex = 1;
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_NAME, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_DEPTNUM, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_SERIAL_NUM, doc, headerColumnIndex, keyRow + 1, false);
		headerColumnIndex = writeExcelSheetHeader(header, STR_ASSET_FUNCTIONTYPE, doc, headerColumnIndex, keyRow + 1, false);
	}

	private void writeExcelSecondHeader(ExcelDocument doc, int keyRow, int scheduleDateRow, int dataColumnIndex, int keyRowIndex, String label, Integer parentIndex) {
		List<String> listScheduleDate = new ArrayList<String>();
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
