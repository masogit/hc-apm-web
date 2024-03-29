package com.ge.apm.service.nurseSite;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.NurseSiteMaintainHistoryMapper;
import com.ge.apm.dao.mapper.NurseSiteMaintainItemHistoryMapper;
import com.ge.apm.dao.mapper.NurseSiteMaintainItemMapper;
import com.ge.apm.dao.mapper.NurseSiteMaintainMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.service.utils.NurseSiteMaintainHistoryComparator;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 503035627
 */
@Service
public class NurseSiteMaintainHistoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainHistoryService.class);
	private static final String SHEET_Maintain = "Maintain List";
	@Autowired
	NurseSiteMaintainHistoryRepository nurseSiteMaintainRepository;

	@Autowired
	NurseSiteMaintainHistoryMapper nurseSiteMaintainHistoryMapper;

	@Autowired
	NurseSiteMaintainItemHistoryMapper nurseSiteMaintainHistoryItemMapper;

	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

	@Autowired
	NurseSiteMaintainItemHistoryRepository nurseSiteMaintainItemRepository;

	@Autowired
	NurseSiteMaintainDataHistoryRepository nurseSiteMaintainDataRepository;

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
	private static final String STR_NURSESITEREPORT = "临床助手日常检查报表";

	private Map<String,UserAccount> mapUserAccount=new HashMap<String,UserAccount>();
	private Map<String,AssetInfo> mapAssetInfo=new HashMap<String,AssetInfo>();
	private Map<String,OrgInfo> mapOrgInfo=new HashMap<String,OrgInfo>();

	public void init(){
		mapUserAccount.clear();
		mapAssetInfo.clear();
		mapOrgInfo.clear();
	}

	public List<NurseSiteMaintainHistory> getDefaultMaintainData(List<String> orgUids, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDefaultData(orgUids, startDate, endDate);
	}

	public List<NurseSiteMaintainHistory> getDataByClinicalOrgUid(List<String> listOrgUid, Date startDate, Date endDate) {
		return nurseSiteMaintainRepository.findDataByClinicalOrgUid(listOrgUid, startDate, endDate);
	}
	public AssetInfo getAssetInfoByUid(String uid) {
		return assetInfoRepository.getByUid(uid);
	}

	public List<OrgInfo> getListClinicalByUidAndParent(List<String> listUid, String parentUid) {
		return orgInfoRepository.findByUidAndParentUid(listUid, parentUid);
	}

	public List<OrgInfo> getListParentSiteByUid(List<String> listUid) {
		return orgInfoRepository.findParentSiteByUids(listUid);
	}

	public List<NurseSiteMaintainItemHistory> findMaintainItemByParentId(String parentId) {
		return nurseSiteMaintainItemRepository.findMaintainItemByParentId(parentId);
	}

	public List<NurseSiteMaintainItemHistory> findMaintainItemByParentId(String parentId,int pageSize, int page) {
		return nurseSiteMaintainItemRepository.findMaintainItemByParentId(parentId,pageSize,page);
	}


	private void supplementMaintainItem(List<NurseSiteMaintainItemHistory> listItem) {
		Set<String> setAssetInfoUids = new HashSet<String>();
		Set<String> setItemIds = new HashSet<String>();
		Map<String, List<NurseSiteMaintainDataHistory>> mapData = new HashMap<String, List<NurseSiteMaintainDataHistory>>();
		List<NurseSiteMaintainDataHistory> listData = null;
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItemHistory> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItemHistory tempItem = itItem.next();
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
				NurseSiteMaintainDataHistory tempData = listData.get(i);
				String parentId = tempData.getParentId();
				if (mapData.containsKey(parentId)) {
					mapData.get(parentId).add(tempData);
				} else {
					List<NurseSiteMaintainDataHistory> tempSetData = new ArrayList<NurseSiteMaintainDataHistory>();
					tempSetData.add(tempData);
					mapData.put(parentId, tempSetData);
				}
			}
		}
		if (listItem != null && listItem.size() > 0) {
			Iterator<NurseSiteMaintainItemHistory> itItem = listItem.iterator();
			while (itItem.hasNext()) {
				NurseSiteMaintainItemHistory tempItem = itItem.next();
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
					tempItem.setModifyBy(getUserAccount(createByUid));
				}
				List<NurseSiteMaintainDataHistory> tempListData=mapData.get(tempItem.getId());
				if(tempListData!=null){
					tempItem.setListData(tempListData);
				}
			}
		}
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

	public void supplementMaintain(NurseSiteMaintainHistory tempMaintain,int pageSize,int page,boolean all) {
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
		List<NurseSiteMaintainItemHistory> listItem;
		UserAccount currentUser = UserContextService.getCurrentUserAccount();
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return;
		}
		Wrapper<NurseSiteMaintainItemHistory> wrapper = new EntityWrapper<>();
		if (currentUser.isEnableAssetTypeGroup()) {
			wrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
		}
		wrapper.eq("nsmi.parent_id", tempMaintain.getId());
		Pagination pagination;
		if(all){
//			listItem = findMaintainItemByParentId(tempMaintain.getId());
			pagination = new Pagination();
		}
		else{
//			listItem = findMaintainItemByParentId(tempMaintain.getId(),pageSize,page);
			pagination = new Pagination(page + 1, pageSize);
		}
		listItem = nurseSiteMaintainHistoryItemMapper.findJoinAssetList(pagination, wrapper);
		tempMaintain.setListNurseSiteMaintainItem(listItem);
		supplementMaintainItem(listItem);
	}

	public void supplementMaintainWithoutItem(List<NurseSiteMaintainHistory> listTempMaintain) {
		Set<String> setModifyByUids = new HashSet<>();
		Map<String, UserAccount> mapModifyBy = new HashMap<>();
		List<UserAccount> listModify = null;
		if (ObjectUtil.isNotEmpty(listTempMaintain)) {
			setModifyByUids = listTempMaintain.stream().map(NurseSiteMaintainHistory::getCreateByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
			setModifyByUids.addAll(listTempMaintain.stream().map(NurseSiteMaintainHistory::getModifyByUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet()));
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
			for (NurseSiteMaintainHistory tempMaintain : listTempMaintain) {
				if (StringUtils.isNotEmpty(tempMaintain.getModifyByUid())) {
					tempMaintain.setModifyBy(mapModifyBy.get(tempMaintain.getModifyByUid()));
				}
				if (StringUtils.isNotEmpty(tempMaintain.getCreateByUid())) {
					tempMaintain.setCreateBy(mapModifyBy.get(tempMaintain.getCreateByUid()));
				}
			}
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

	public List<String> findAccessOrgUidListByUserUid(String userUid) {
		return uaaService.findAccessClinicalUidByUserUid(userUid);
	}

	public List<Map<String, Object>> parseMaintainToMap(List<NurseSiteMaintainHistory> listMaintain) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listMaintain != null && listMaintain.size() > 0) {
			for (int i = 0; i < listMaintain.size(); ++i) {
				NurseSiteMaintainHistory tempMaintain = listMaintain.get(i);
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

	public byte[] writeMaintainDetailToExcel(ExcelDocument doc, List<NurseSiteMaintainHistory> listMaintain) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		listMaintain.sort(new NurseSiteMaintainHistoryComparator());
		Map<String, List<NurseSiteMaintainItemHistory>> mapAssetUidMaintainItem = new HashMap<String, List<NurseSiteMaintainItemHistory>>();
		Map<String, String> mapAssetInfo = new HashMap<String, String>();
		Set<String> setSignature = new HashSet<String>();
		Map<String, Integer> mapSignature = new HashMap<String, Integer>();
		Map<String, Integer> mapCheckItemSequence = new HashMap<String, Integer>();
		List<String> header = new ArrayList<String>();
		List<String> secondHeader = new ArrayList<String>();
		for (int i = 0; i < listMaintain.size(); ++i) {
			NurseSiteMaintainHistory tempMaintain = listMaintain.get(i);
			supplementMaintain(tempMaintain,0,0,true);
			List<NurseSiteMaintainItemHistory> listItem = tempMaintain.getListNurseSiteMaintainItem();
			if (listItem != null && listItem.size() > 0) {
				Iterator<NurseSiteMaintainItemHistory> itItem = listItem.iterator();
				while (itItem.hasNext()) {
					NurseSiteMaintainItemHistory tempItem = itItem.next();
					if (mapAssetUidMaintainItem.containsKey(tempItem.getAssetUid())) {
						List<NurseSiteMaintainItemHistory> tempListItem = mapAssetUidMaintainItem.get(tempItem.getAssetUid());
						tempListItem.add(tempItem);
					} else {
						List<NurseSiteMaintainItemHistory> tempListItem = new ArrayList<NurseSiteMaintainItemHistory>();
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
		int dataColumnIndex = 5;
		if (mapAssetUidMaintainItem.size() > 0) {
			Iterator<List<NurseSiteMaintainItemHistory>> it = mapAssetUidMaintainItem.values().iterator();
			while (it.hasNext()) {
				List<NurseSiteMaintainItemHistory> tempListItem = it.next();
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
						NurseSiteMaintainItemHistory tempItem = tempListItem.get(i);
						List<NurseSiteMaintainDataHistory> listData = tempItem.getListData();
						String keyName = "";
						if (listData != null && listData.size() > 0) {
							Iterator<NurseSiteMaintainDataHistory> itData = listData.iterator();
							while (itData.hasNext()) {
								NurseSiteMaintainDataHistory tempData = itData.next();
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

	public List<NurseSiteMaintainHistory> findJoinAssetList(Wrapper<NurseSiteMaintainHistory> wrapper) {
		return nurseSiteMaintainHistoryMapper.findJoinAssetList(wrapper);
	}

	public List<NurseSiteMaintainItemHistory> findItemJoinAssetList(Pagination pagination, Wrapper<NurseSiteMaintainItemHistory> wrapper) {
		return nurseSiteMaintainHistoryItemMapper.findJoinAssetList(pagination, wrapper);
	}

	public List<NurseSiteMaintainHistory> searchMaintainHistory(Map<String, Object> param) {
		return nurseSiteMaintainHistoryMapper.searchMaintainHistory(param);
	}
}
