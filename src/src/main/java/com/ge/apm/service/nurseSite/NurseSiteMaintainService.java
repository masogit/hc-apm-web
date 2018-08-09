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
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigItemRepository;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.NurseSiteMaintainDataRepository;
import com.ge.apm.dao.NurseSiteMaintainItemRepository;
import com.ge.apm.dao.NurseSiteMaintainRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
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
import com.ge.apm.service.utils.NurseSiteMaintainComparator;
import com.ge.apm.service.wo.WorkOrderPublishRenderService.RenderResult;

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
	private BlobFileService blobFileService;

	@Autowired
	private UaaService uaaService;

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

	public List<AssetInfo> getListAsset(String siteUid, String userUid) {
		return assetInfoRepository.findBySelfScope(siteUid, userUid);
	}

	public List<AssetInfo> getListAssetBySelfClinicalScope(String siteUid, String userUid, String clinicalDeptUid) {
		return assetInfoRepository.findBySelfClinicalScope(siteUid, userUid, clinicalDeptUid);
	}

	public List<AssetInfo> getListAssetInfoByUid(List<String> assetUids) {
		return assetInfoRepository.findByUidIn(assetUids);
	}

	public List<AssetInfo> getListAssetInfoByMaintainConfigParentId(String parentId) {
		return assetInfoRepository.findAssetByMaintainConfigParentId(parentId);
	}
	
	public List<AssetInfo> getListAssetInfoBySearchFilter(List<SearchFilter> listFilter){
		return assetInfoRepository.findBySearchFilter(listFilter);
	}

	public List<OrgInfo> getListClinicalByUid(List<String> listUid) {
		return orgInfoRepository.findClinicalByUids(listUid);
	}

	public List<OrgInfo> getListClinicalByUid(Set<String> listUid) {
		return orgInfoRepository.findClinicalByUids(listUid);
	}
	
	public List<OrgInfo> getListClinicalByUidAndParent(Set<String> listUid,String parentUid) {
		return orgInfoRepository.findClinicalByUidsAndParent(listUid,parentUid);
	}
	
	public List<OrgInfo> getListParentSiteByUid(Set<String> listUid) {
		return orgInfoRepository.findParentSiteByUids(listUid);
	}
	
	public List<OrgInfo> getListSiteByUid(Set<String> listUid) {
		return orgInfoRepository.findSiteByUids(listUid);
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

	public Set<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId) {
		return nurseSiteMaintainItemRepository.findMaintainItemByParentId(parentId);
	}

	public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String maintainId) {
		return nurseSiteMaintainDataRepository.findMaintainDataByMaintainId(maintainId);
	}

	public List<String> findUserUidListByOrgUids(List<String> orgUids) {
		return userAccountRepository.findUserUidListByOrgUids(orgUids);
	}

	public void save(NurseSiteMaintain obj) {
		nurseSiteMaintainRepository.save(obj);
	}

	public void saveMaintainItem(Set<NurseSiteMaintainItem> obj) {
		nurseSiteMaintainItemRepository.save(obj);
	}

	public void saveMaintainData(Set<NurseSiteMaintainData> obj) {
		nurseSiteMaintainDataRepository.save(obj);
	}

	public void saveNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> obj) {
		nurseSiteMaintainConfigItemRepository.save(obj);
	}

	public void save(List<NurseSiteMaintain> obj) {
		nurseSiteMaintainRepository.save(obj);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean deleteMaintain(NurseSiteMaintain maintain) {
		// Set<NurseSiteMaintainItem>
		// listItem=maintain.getListNurseSiteMaintainItem();
		// if(maintain.isNeedGetItem()){
		// listItem=findMaintainItemByParentId(maintain.getId());
		// }
		// nurseSiteMaintainItemRepository.delete(listItem);
		if (StringUtils.isNotEmpty(maintain.getSignatureId())) {
			blobFileService.deleteBlobFilesByStorageId(maintain.getSignatureId());
		}
		nurseSiteMaintainRepository.delete(maintain);
		return true;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
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
			nurseSiteMaintainRepository.delete(listMaintain);
		}
		return true;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveAndDelete(List<NurseSiteMaintain> obj, List<NurseSiteMaintain> obj2) {
		if (obj != null && obj.size() > 0) {
			nurseSiteMaintainRepository.save(obj);
		}
		if (obj2 != null && obj2.size() > 0) {
			// nurseSiteMaintainRepository.delete(obj2);
			deleteMaintain(obj2);
		}
	}

	public void save(NurseSiteMaintainConfig obj) {
		nurseSiteMaintainConfigRepository.save(obj);
	}

	public void delete(NurseSiteMaintain obj) {
		// nurseSiteMaintainRepository.delete(obj);
		deleteMaintain(obj);
	}

	public void deleteNurseSiteMaintainConfigItem(List<NurseSiteMaintainConfigItem> list) {
		nurseSiteMaintainConfigItemRepository.delete(list);
	}

	public void delete(List<NurseSiteMaintainItem> list) {
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

	public Set<NurseSiteMaintainItem> loadConfigCheckItems(NurseSiteMaintainConfig nurseSiteMaintainConfig, List<AssetInfo> listCheckAssets) {
		Set<NurseSiteMaintainItem> listMaintainItem = new HashSet<NurseSiteMaintainItem>();
		String strCheckItems = nurseSiteMaintainConfig.getCheckItems();
		if (StringUtils.isNotEmpty(strCheckItems) && listCheckAssets != null) {
			String arr2[] = strCheckItems.split(",");
			for (int i = 0; i < listCheckAssets.size(); ++i) {
				NurseSiteMaintainItem item = new NurseSiteMaintainItem();
				item.setAssetInfo(listCheckAssets.get(i));
				item.setAssetUid(listCheckAssets.get(i).getUid());
				Set<NurseSiteMaintainData> listData = new HashSet<NurseSiteMaintainData>();
				item.setListData(listData);
				for (int j = 0; j < arr2.length; ++j) {
					NurseSiteMaintainData p = new NurseSiteMaintainData();
					p.setCheckItem(arr2[j]);
					// p.setCheckValue(false);
					p.setItem(item);
					listData.add(p);
				}
				listMaintainItem.add(item);
			}
		}
		return listMaintainItem;
	}

	private int updateMaintainData(NurseSiteMaintain obj, Boolean defaultValue) {
		Set<NurseSiteMaintainItem> listItem = obj.getListNurseSiteMaintainItem();
		if (obj.isNeedGetItem()) {
			listItem = findMaintainItemByParentId(obj.getId());
			obj.setListNurseSiteMaintainItem(listItem);
			obj.setModifyBy(getUserAccountByUid(obj.getModifyByUid()));
			obj.setNeedGetItem(false);
		}
		Iterator<NurseSiteMaintainItem> it = listItem.iterator();
		int itemStatus = 0;
		int objStatus = 0;
		while (it.hasNext()) {
			itemStatus = 0;
			NurseSiteMaintainItem tempItem = it.next();
			Set<NurseSiteMaintainData> listData = tempItem.getListData();
			Iterator<NurseSiteMaintainData> itData = listData.iterator();
			if (defaultValue != null) {
				// batch update
				if (defaultValue) {
					tempItem.setStatus(2);
				} else {
					tempItem.setStatus(3);
				}
				while (itData.hasNext()) {
					NurseSiteMaintainData tempData = itData.next();
					tempData.setCheckValue(defaultValue);
				}
			} else {
				// single,0:未检查，2：已通过，3：有问题，4：进行中
				while (itData.hasNext()) {
					NurseSiteMaintainData tempData = itData.next();
					Boolean value = tempData.getCheckValue();
					if (value == null) {
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
		}
		return objStatus;
	}

	public void updateMaintainDataStatus(NurseSiteMaintain obj, boolean batchUpdate, String signatureId) {
		if (batchUpdate) {
			if (obj.getStatus() == 0) {
				obj.setStatus(2);
				if (StringUtils.isNotEmpty(signatureId)) {
					obj.setSignatureId(signatureId);
				}
				updateMaintainData(obj, true);
			}
		} else {
			obj.setStatus(updateMaintainData(obj, null));
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

	public NurseSiteMaintain createMaintain(Date date, UserAccount currentUser, NurseSiteMaintainConfig nurseSiteMaintainConfig, List<AssetInfo> listCheckAssets,
			Integer sequence) {
		if (nurseSiteMaintainConfig.getType() != null && nurseSiteMaintainConfig.getType() == 2) {
			sequence = null;
		}
		Set<NurseSiteMaintainItem> listItem = loadConfigCheckItems(nurseSiteMaintainConfig, listCheckAssets);
		NurseSiteMaintain tempNurseSiteMaintain = new NurseSiteMaintain();
		Date currentDate = new Date();
		tempNurseSiteMaintain.setTenantUID(currentUser.getTenantUID());
		tempNurseSiteMaintain.setInstitutionUID(currentUser.getInstitutionUID());
		tempNurseSiteMaintain.setHospitalUID(currentUser.getHospitalUID());
		tempNurseSiteMaintain.setSiteUID(currentUser.getSiteUID());
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
		if (sequence != null) {
			tempNurseSiteMaintain.setSequence(sequence);
		}
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
			if (sequence != null) {
				tempItem.setSequence(sequence);
			}
			tempItem.setName(nurseSiteMaintainConfig.getName());
			tempItem.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
			tempItem.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
			tempItem.setStatus(0);
		}
		return tempNurseSiteMaintain;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
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
				String str = new String(b);
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

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean createMaintainConfig(NurseSiteMaintainConfig tempConfig, UserAccount currentUser, List<AssetInfo> listCheckAssets) {
		String originName = tempConfig.getName();
		while (listCheckAssets.size() > 0) {
			String clinicalUid = "";
			String clinicalName = "";
			List<AssetInfo> listNew = new ArrayList<AssetInfo>();
			for (int i = 0; i > -1 && i < listCheckAssets.size(); ++i) {
				AssetInfo assetInfo = listCheckAssets.get(i);
				if (assetInfo != null) {
					if (StringUtils.isEmpty(clinicalUid)) {
						clinicalUid = assetInfo.getClinicalDeptUID();
						clinicalName = assetInfo.getClinicalDeptName();
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
				save(newConfig);
				saveConfigChildren(newConfig, listNew);
			}
		}
		return true;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean updateMaintainConfig(NurseSiteMaintainConfig tempConfig, UserAccount currentUser, List<AssetInfo> listCheckAssets) {
		save(tempConfig);
		saveConfigChildren(tempConfig, listCheckAssets);
		if (tempConfig.getApplyDirectly()) {
			updateUnCheckedMaintainData(tempConfig, currentUser, 0, listCheckAssets);
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
					obj.setCheckItems(tempConfig.getCheckItems());
					obj.setCheckItemsDetail(tempConfig.getCheckItemsDetail());
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

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean deleteMaintainConfig(NurseSiteMaintainConfig tempConfig) {
		deleteConfigChildren(tempConfig.getId());
		delete(tempConfig);
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

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateUnCheckedMaintainData(NurseSiteMaintainConfig nurseSiteMaintainConfig, UserAccount currentUser, Integer status, List<AssetInfo> listCheckAssets) {
		List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findByConfigAndStatus(nurseSiteMaintainConfig.getId(), status);
		List<NurseSiteMaintain> listNewMaintain = new ArrayList<NurseSiteMaintain>();
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				listNewMaintain
						.add(createMaintain(listMaintain.get(i).getScheduleDate(), currentUser, nurseSiteMaintainConfig, listCheckAssets, listMaintain.get(i).getSequence()));
			}
		}
		// nurseSiteMaintainRepository.delete(listMaintain);
		deleteMaintain(listMaintain);
		nurseSiteMaintainRepository.save(listNewMaintain);
	}

	public Set<String> findAccessOrgUidListByUserUid(String userUid) {
		Set<String> listResult = new HashSet<String>();
		List<UserOrgInfo> listUserOrgInfo = userOrgInfoRepository.findOrgUidListByUserUid(userUid);
		Set<String> listOrgUid = null;
		if (listUserOrgInfo != null) {
			if (listUserOrgInfo.size() > 1) {

			} else if (listUserOrgInfo.size() == 1) {
				UserOrgInfo tempUserOrgInfo = listUserOrgInfo.get(0);
				if (tempUserOrgInfo.getOrgType() == 4) {

				} else {
					listOrgUid = orgInfoRepository.findClinicalDeptOrgUidListByOrgUid(tempUserOrgInfo.getOrgUid());
				}
			}
		}
		if (listUserOrgInfo != null && listUserOrgInfo.size() > 0) {
			for (int i = 0; i < listUserOrgInfo.size(); ++i) {
				listResult.add(listUserOrgInfo.get(i).getOrgUid());
			}
		}
		if (listOrgUid != null && listOrgUid.size() > 0) {
			listResult.addAll(listOrgUid);
		}
		return listResult;
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
				tempMap.put("负责人", tempMaintain.getModifyBy().getName());
				tempMap.put("计划检查日期", sdf.format(tempMaintain.getScheduleDate()));
				tempMap.put("实际检查日期", sdf2.format(tempMaintain.getCheckDate()));
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
						byte[] bytes = writeMaintainDetailToExcel(tempDoc, listNewMaintain);
						if(bytes!=null){
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

	private byte[] writeMaintainDetailToExcel(ExcelDocument doc, List<NurseSiteMaintain> listMaintain) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listMaintain.sort(new NurseSiteMaintainComparator());
		Map<String, List<NurseSiteMaintainItem>> mapAssetUidMaintainItem = new HashMap<String, List<NurseSiteMaintainItem>>();
		Map<String, String> mapAssetInfo = new HashMap<String, String>();
		List<String> header = new ArrayList<String>();
		List<String> secondHeader = new ArrayList<String>();
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintain tempMaintain = listMaintain.get(i);
			if (tempMaintain.isNeedGetItem()) {
				tempMaintain.setListNurseSiteMaintainItem(findMaintainItemByParentId(tempMaintain.getId()));
				tempMaintain.setNeedGetItem(false);
			}
			Set<NurseSiteMaintainItem> listItem = tempMaintain.getListNurseSiteMaintainItem();
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
					headerColumnIndex = writeExcelSheetHeader(header, "设备名", doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getName());
					headerColumnIndex = writeExcelSheetHeader(header, "设备编号", doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getDepartNum());
					headerColumnIndex = writeExcelSheetHeader(header, "制造商序列号", doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getSerialNum());
					headerColumnIndex = writeExcelSheetHeader(header, "设备型号", doc, headerColumnIndex, 1, false);
					doc.writeCellData(SHEET_Maintain, rowIndex, columnIndex++, tempListItem.get(0).getAssetInfo().getFunctionType());
					// secondHeaderColumnIndex=writeExcelSheetHeader(secondHeader,
					// "班次", doc, secondHeaderColumnIndex,2,false);
					// secondHeaderColumnIndex=5;
					for (int i = 0; i < tempListItem.size(); ++i) {
						NurseSiteMaintainItem tempItem = tempListItem.get(i);
						Set<NurseSiteMaintainData> listData = tempItem.getListData();
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
								if (tempItem.getSequence() == null) {
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
