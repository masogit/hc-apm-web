package com.ge.apm.service.examine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.http.client.utils.DateUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PccExamineReportRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceUtil;

/**
 * Created by 212605082 on 2018/5/8.
 */
@Service
public class PccExamineReportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportService.class);
	private static final String SHEET_Maintain = "datalist";
	@Autowired
	AssetInfoRepository assetInfoRepository;

	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;

	@Autowired
	PccExamineReportRepository pccExamineReportRepository;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	private BlobFileService blobFileService;

	@Autowired
	private MicroServiceUtil msService;

	@Autowired
	private UaaService uaaService;
	private static List<ApmDropDownItem> lineStatus;
	private static List<ApmDropDownItem> mirrorStatus;
	private static List<ApmDropDownItem> shellStatus;
	private static List<ApmDropDownItem> linkerStatus;
	private static List<ApmDropDownItem> oilStatus;
	private static List<ApmDropDownItem> tipStatus;
	private static List<ApmDropDownItem> electricLeakageStatus;
	private static List<ApmDropDownItem> firstSeenStatus;
	private static List<ApmDropDownItem> firstTipStatus;
	private static List<ApmDropDownItem> resultStatus;
	private static List<ApmDropDownItem> recommandStatus;
	private Map<String, OrgInfo> mapOrgInfo = new HashMap<String, OrgInfo>();
	private Map<String, AssetInfo> mapAssetInfo = new HashMap<String, AssetInfo>();

	public List<PccExamineReport> fetchPccExamineReportBySite(String siteUid) {
		return pccExamineReportRepository.fetchPccExamineReportBySiteUid(siteUid);
	}

	@Transactional
	public V2_BlobObject uploadFile(FileUploadEvent event) {
		return blobFileService.uploadFile(event.getFile());
	}

	public void clear() {
		mapOrgInfo.clear();
		mapAssetInfo.clear();
	}

	public void supplementRecord(List<PccExamineReport> list) {
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); ++i) {
				PccExamineReport pcc = list.get(i);
				String id = pcc.getId();
				if (StringUtils.isEmpty(id)) {
					String assetUid=pcc.getAssetUid();
					if (assetUid != null) {
						pcc.setId(assetUid);
						pcc.setBlnHasId(false);
					}
				}
			}
		}
	}

	public boolean saveFile(List<ApmStreamedContent> list, String boUuid, String boType) {
		boolean result = false;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); ++i) {
				ApmStreamedContent tempContent = list.get(i);
				deleteFile(boUuid, boType, tempContent.getMark());
				result = saveFile(tempContent, boUuid, V2_BlobObject.BO_TYPE_PCCReport, tempContent.getMark());
				if (!result) {
					return result;
				}
			}
		}
		return result;
	}

	@Transactional
	public boolean savePccReport(PccExamineReport report, List<ApmStreamedContent> listContent) {
		pccExamineReportRepository.save(report);
		String id = report.getId();
		if(listContent==null){
			return true;
		}
		if (StringUtils.isNotEmpty(id)) {
			return saveFile(listContent, id, V2_BlobObject.BO_TYPE_PCCReport);
		}
		else{
			return false;
		}
	}
	
	public void savePccReport(PccExamineReport report) {
		pccExamineReportRepository.save(report);
	}

	@Transactional
	public boolean deletePccReport(PccExamineReport report) {
		String id = report.getId();
		pccExamineReportRepository.delete(report);
		if (StringUtils.isNotEmpty(id)) {
			deleteFile(id);
		}
		return true;
	}

	public List<ApmStreamedContent> parseBlobObjectImg(String boUuid) {
		List<V2_BlobObject> listObj = getFile(boUuid, V2_BlobObject.BO_TYPE_PCCReport);
		List<ApmStreamedContent> listResult = new ArrayList<ApmStreamedContent>();
		if (listObj != null && listObj.size() > 0) {
			for (int i = 0; i < listObj.size(); ++i) {
				V2_BlobObject obj = listObj.get(i);
				String storageId = obj.getObjectStorageId();
				if (StringUtils.isNotEmpty(storageId)) {
					ApmStreamedContent tempContent = new ApmStreamedContent(blobFileService.getBlobObjectByStorageId(storageId).getStream(), obj.getObjectType(),
							obj.getObjectName());
					tempContent.setId(storageId);
					tempContent.setMark(obj.getBoSubType());
					listResult.add(tempContent);
				}
			}
		}
		return listResult;
	}

	public ApmStreamedContent parseBlobObjectDoc(String boUuid) {
		List<V2_BlobObject> listObj = getFile(boUuid, V2_BlobObject.BO_TYPE_PCCReport, V2_BlobObject.BO_SUB_TYPE_DOCS);
		if (listObj != null && listObj.size() == 1) {
			for (int i = 0; i < listObj.size(); ++i) {
				V2_BlobObject obj = listObj.get(i);
				String storageId = obj.getObjectStorageId();
				if (StringUtils.isNotEmpty(storageId)) {
					ApmStreamedContent tempContent = new ApmStreamedContent(blobFileService.getBlobObjectByStorageId(storageId).getStream(), obj.getObjectType(),
							obj.getObjectName());
					tempContent.setContentType("application/msword");
					return tempContent;
				}
			}
		}
		return null;
	}

	@Transactional
	public boolean saveFile(ApmStreamedContent content, String boUuid, String boType, String boSubType) {
		if (content != null) {
			String name = content.getName();
			V2_BlobObject blobObj = blobFileService.uploadFile(content.getStream(), name.toLowerCase());
			if(blobObj!=null){
				blobObj.setBoType(boType);
				blobObj.setBoSubType(boSubType);
				blobObj.setBoUuid(boUuid);
				blobFileService.saveBlobObject(blobObj);
			}
			else{
				return false;
			}
		}
		return true;
	}

	public List<V2_BlobObject> getFile(String boUuid) {
		return blobFileService.findBlobObjectsByBoUid(boUuid);
	}

	public StreamedContent getSingleFile(String storageId) {
		return msService.downloadSingleFile(storageId);
	}

	public List<V2_BlobObject> getFile(String boUuid, String boType) {
		return blobFileService.fetchBlobObject(boUuid, boType);
	}

	public List<V2_BlobObject> getFile(String boUuid, String boType, String boSubType) {
		return blobFileService.fetchBlobObject(boUuid, boType, boSubType);
	}

	public Boolean deleteFile(String boUuid, String boType, String boSubType) {
		blobFileService.deleteBlobFileByBoUuidAndType(boUuid, boType, boSubType);
		return true;
	}

	public Boolean deleteFile(String boUuid) {
		blobFileService.deleteBlobFileByBoUuid(boUuid);
		return true;
	}

	public Boolean deleteFileByStorageId(String storageId) {
		return blobFileService.deleteBlobFilesByStorageId(storageId);
	}

	private OrgInfo getOrgInfo(String orgUid) {
		OrgInfo orgInfo = mapOrgInfo.get(orgUid);
		if (orgInfo == null) {
			orgInfo = orgInfoRepository.findByUid(orgUid);
			if (orgInfo != null) {
				mapOrgInfo.put(orgUid, orgInfo);
			}
		}
		return orgInfo;
	}

	public AssetInfo getAssetInfo(String assetUid) {
		AssetInfo assetInfo = mapAssetInfo.get(assetUid);
		if (assetInfo == null) {
			assetInfo = assetInfoRepository.findByUid(assetUid);
			if (assetInfo != null) {
				mapAssetInfo.put(assetUid, assetInfo);
			}
		}
		return assetInfo;
	}

	public ApmStreamedContent getTemplate() {
		List<V2_BlobObject> listBlobObject = getFile("0", V2_BlobObject.BO_TYPE_PCCReport, V2_BlobObject.BO_SUB_TYPE_DOCS);
		if (listBlobObject != null && listBlobObject.size() == 1) {
			V2_BlobObject tempObj = listBlobObject.get(0);
			String storageId = tempObj.getObjectStorageId();
			if (StringUtils.isNotEmpty(storageId)) {
				ApmStreamedContent tempContent = new ApmStreamedContent(blobFileService.getBlobObjectByStorageId(storageId).getStream(), tempObj.getObjectType(),
						tempObj.getObjectName());
				return tempContent;
			}
		} else {
			return getOriginTemplate();
		}
		return null;
	}

	public ApmStreamedContent getOriginTemplate() {
		return new ApmStreamedContent(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/examine/pccExamineReportTemplate.docx"),
				"application/octet-stream", "pccExamineReportTemplate.docx");
	}

	public boolean batchGenerateReport_bak(List<PccExamineReport> listPcc) {
		boolean result = true;
		for (int i = 0; i < listPcc.size(); ++i) {
			PccExamineReport tempPcc = listPcc.get(i);
			ApmStreamedContent tempResult = generateReport(tempPcc);
			if (tempResult == null) {
				return false;
			}
		}
		return result;
	}

	public Map<Integer,List<String>> findAccessUidByUserUid(String userUid) {
		return uaaService.findAccessUidByUserUid(userUid);
	}

	public List<ApmStreamedContent> batchGenerateReport(List<PccExamineReport> listPcc) {
		List<ApmStreamedContent> list = new ArrayList<ApmStreamedContent>();
		for (int i = 0; i < listPcc.size(); ++i) {
			PccExamineReport tempPcc = listPcc.get(i);
			ApmStreamedContent tempResult = generateReport(tempPcc);
			if (tempResult != null) {
				list.add(tempResult);
			}
		}
		return list;
	}

	@Transactional
	public ApmStreamedContent generateReport(PccExamineReport pcc) {
		AssetInfo tempInfo = pcc.getAssetInfo();
		if (tempInfo == null) {
			tempInfo = getAssetInfo(pcc.getAssetUid());
			pcc.setAssetInfo(tempInfo);
		}
		Date date = pcc.getCheckDate();
		List<ApmStreamedContent> listPicture = parseBlobObjectImg(pcc.getId());
		ApmStreamedContent template = getTemplate();
		if (template != null) {
			try {
				String dateStr = date != null ? DateUtils.formatDate(date, "yyyy-MM-dd") : "";
				String fileName = tempInfo.getName() + "_" + dateStr + ".pdf";
				pcc.setReportDate(new Date());
				byte[] docxBytes = renderDocx(getTemplateMap(pcc, listPicture), template.gettBytes());
				byte[] pdfBytes = msService.docxToPdf(docxBytes);
				template.settBytes(pdfBytes);
				template.setContentType("application/pdf");
				template.setName(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage());
			}
		}
		return template;
	}

	@Transactional
	public ApmStreamedContent generateReport_bak(PccExamineReport pcc, List<ApmStreamedContent> listPicture) {
		deleteFile(pcc.getId(), V2_BlobObject.BO_TYPE_PCCReport, V2_BlobObject.BO_SUB_TYPE_DOCS);
		ApmStreamedContent template = getTemplate();
		if (template != null) {
			try {
				String fileName = pcc.getName();
				if (StringUtils.isNotEmpty(fileName)) {
					String tempFileName = template.getName();
					int index = tempFileName.lastIndexOf(".");
					fileName = fileName + tempFileName.substring(index, tempFileName.length());
				} else {
					fileName = template.getName();
				}
				pcc.setReportDate(new Date());
				template.settBytes(renderDocx(getTemplateMap(pcc, listPicture), template.gettBytes()));
				V2_BlobObject tempObj = blobFileService.uploadFile(template.getStream(), fileName);
				if (tempObj != null) {
					tempObj.setBoUuid(pcc.getId());
					tempObj.setBoType(V2_BlobObject.BO_TYPE_PCCReport);
					tempObj.setBoSubType(V2_BlobObject.BO_SUB_TYPE_DOCS);
					blobFileService.saveBlobObject(tempObj);
					pcc.setReportStorageId(tempObj.getObjectStorageId());
					pccExamineReportRepository.save(pcc);
				}
				template.setContentType("application/msword");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage());
			}
		}
		return template;
	}

	private Map<String, Object> getTemplateMap(PccExamineReport pcc, List<ApmStreamedContent> listPicture) {
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		data.put("lineStatus", convertKeyToLable(pcc.getLineStatus(), fetchLineStatus()));
		data.put("mirrorStatus", convertKeyToLable(pcc.getMirrorStatus(), fetchMirrorStatus()));
		data.put("shellStatus", convertKeyToLable(pcc.getShellStatus(), fetchShellStatus()));
		data.put("linkerStatus", convertKeyToLable(pcc.getLinkerStatus(), fetchLinkerStatus()));
		data.put("oilStatus", convertKeyToLable(pcc.getOilStatus(), fetchOilStatus()));
		data.put("tipStatus", convertKeyToLable(pcc.getTipStatus(), fetchTipStatus()));
		data.put("electricLeakageStatus", convertKeyToLable(pcc.getElectricLeakageStatus(), fetchElectricLeakageStatus()));
		data.put("firstSeenStatus", convertKeyToLable(pcc.getFirstSeenStatus(), fetchFirstSeenStatus()));
		data.put("firstTipStatus", convertKeyToLable(pcc.getFirstTipStatus(), fetchFirstTipStatus()));
		data.put("resultStatus", convertKeyToLable(pcc.getResultStatus(), fetchResultStatus()));
		data.put("recommandStatus", convertKeyToLable(pcc.getRecommandStatus(), fetchRecommandStatus()));
		data.put("comment", pcc.getComment());

		AssetInfo tempInfo = pcc.getAssetInfo();
		if (tempInfo != null) {
			data.put("hospitalName", tempInfo.getHospital().getName());
			data.put("clinicalName", tempInfo.getClinicalDeptName());
			data.put("functionType", tempInfo.getFunctionType());
			data.put("serialNum", tempInfo.getSerialNum());
			Date tempDate = tempInfo.getManufactDate();
			if (tempDate != null) {
				data.put("manufactDate", sdf.format(tempDate));
			}
		}
		Date tempDate = pcc.getCheckDate();
		if (tempDate != null) {
			data.put("checkDate", sdf.format(tempDate));
		}
		if (listPicture == null) {
			listPicture = parseBlobObjectImg(pcc.getId());
		}
		if (listPicture != null && listPicture.size() > 0) {
			for (int i = 0; i < listPicture.size(); ++i) {
				ApmStreamedContent tempContent = listPicture.get(i);
				PictureRenderData tempPic = new PictureRenderData(200, 160, tempContent.getName(), tempContent.gettBytes());
				if (tempContent.getMark().equals("picture1")) {
					data.put("picture1", tempPic);
				} else if (tempContent.getMark().equals("picture2")) {
					data.put("picture2", tempPic);
				} else if (tempContent.getMark().equals("picture3")) {
					data.put("picture3", tempPic);
				} else if (tempContent.getMark().equals("picture4")) {
					data.put("picture4", tempPic);
				} else if (tempContent.getMark().equals("pictureTail")) {
					tempPic.setWidth(600);
					tempPic.setHeight(600);
					data.put("pictureTail", tempPic);
				}
			}
		}
		return data;
	}

	private String convertKeyToLable(String key, List<ApmDropDownItem> listItem) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(key)) {
			String[] arr = key.split(",");
			for (int i = 0; i < arr.length; ++i) {
				for (int j = 0; j < listItem.size(); ++j) {
					ApmDropDownItem tempItem = listItem.get(j);
					if (arr[i].equals(tempItem.getKey())) {
						sb.append(tempItem.getLabel());
						sb.append(",");
						break;
					}
				}
			}
			return sb.toString().substring(0, sb.length() - 1);
		}
		return "";
	}

	private byte[] renderDocx(Map<String, Object> datas, byte[] templateBytes) throws IOException {
		byte[] bytes;
		XWPFTemplate template = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
			template = XWPFTemplate.compile(inputStream);
			template.render(datas);
			template.write(out);
			bytes = out.toByteArray();
		} finally {
			try {
				if (template != null) {
					template.close();
				}
			} catch (Exception ignore) {

			}
		}
		return bytes;
	}

	public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> data) {

		Integer keyRow = 1;
		Integer dataRow = 2;

		for (Map<String, Object> item : data) {
			doc.writeRowData(SHEET_Maintain, keyRow, dataRow++, item);
		}

		return Boolean.TRUE;
	}

	public List<Map<String, Object>> parseMaintainToMap(List<PccExamineReport> listPcc) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		List<Map<String, Object>> listMapResult = new ArrayList<Map<String, Object>>();
		if (listPcc != null && listPcc.size() > 0) {
			for (int i = 0; i < listPcc.size(); ++i) {
				PccExamineReport temp = listPcc.get(i);
				Map<String, Object> tempMap = new HashMap<String, Object>();
				String hospitalName = temp.getAssetInfohospitalName();
				String clinicalName = temp.getAssetInfoclinicalDeptName();
				String systemId = temp.getAssetInfosystemId();
				String name = temp.getAssetInfoname();
				String functionType = temp.getAssetInfofunctionType();
				String sn = temp.getAssetInfoserialNum();
				Date manufactureDate = temp.getAssetInfomanufactDate();
				Date installDate = temp.getAssetInfoarriveDate();
				Date startDate = temp.getAssetInfoinstallDate();
				Date endDate = temp.getAssetInfowarrantyDate();
				Date checkDate = temp.getCheckDate();
				String createByName = temp.getCreateByName();
				String rStatus = temp.getRecommandStatus();
				String rStatusStr = "";
				Integer status = temp.getStatus();
				String statusStr = "";
				if (rStatus == null) {
				} else if (rStatus.equals("1")) {
					rStatusStr = "好";
				} else if (rStatus.equals("2")) {
					rStatusStr = "坏";
				}
				if (status == null) {
				} else if (status == 1) {
					statusStr = "未提交";
				} else if (status == 2) {
					statusStr = "已提交";
				}
				if (StringUtils.isNotEmpty(hospitalName)) {
					tempMap.put("医院", hospitalName);
				}
				if (StringUtils.isNotEmpty(clinicalName)) {
					tempMap.put("科室", clinicalName);
				}
				if (StringUtils.isNotEmpty(systemId)) {
					tempMap.put("主机SystemID", systemId);
				}
				if (StringUtils.isNotEmpty(name)) {
					tempMap.put("探头名称", name);
				}
				if (StringUtils.isNotEmpty(functionType)) {
					tempMap.put("探头型号", functionType);
				}
				if (StringUtils.isNotEmpty(sn)) {
					tempMap.put("探头S/N", sn);
				}
				if (StringUtils.isNotEmpty(createByName)) {
					tempMap.put("创建人", createByName);
				}
				if (StringUtils.isNotEmpty(rStatusStr)) {
					tempMap.put("探头好/坏", rStatusStr);
				}
				if (StringUtils.isNotEmpty(statusStr)) {
					tempMap.put("报告状态", statusStr);
				}
				if (manufactureDate != null) {
					tempMap.put("探头生产日期", manufactureDate);
				}
				if (installDate != null) {
					tempMap.put("探头安装日期", installDate);
				}
				if (startDate != null) {
					tempMap.put("探头合同开始日期", startDate);
				}
				if (endDate != null) {
					tempMap.put("探头合同截至日期", endDate);
				}
				if (checkDate != null) {
					tempMap.put("检测日期", checkDate);
				}
				listMapResult.add(tempMap);
			}
		}
		return listMapResult;
	}

	public static List<ApmDropDownItem> fetchLineStatus() {
		if (lineStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"破损（老化）\"},{\"key\":\"3\",\"label\":\"断裂\"}]";
			lineStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return lineStatus;
	}

	public static List<ApmDropDownItem> fetchMirrorStatus() {
		if (mirrorStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"起泡\"},{\"key\":\"3\",\"label\":\"断裂\"}]";
			mirrorStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return mirrorStatus;
	}

	public static List<ApmDropDownItem> fetchShellStatus() {
		if (shellStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"开裂\"}]";
			shellStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return shellStatus;
	}

	public static List<ApmDropDownItem> fetchLinkerStatus() {
		if (linkerStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"良好\"},{\"key\":\"2\",\"label\":\"锁扣脱落\"},{\"key\":\"3\",\"label\":\"外壳破损\"},{\"key\":\"4\",\"label\":\"针脚损坏\"}]";
			linkerStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return linkerStatus;
	}

	public static List<ApmDropDownItem> fetchOilStatus() {
		if (oilStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"无\"},{\"key\":\"2\",\"label\":\"良好\"},{\"key\":\"3\",\"label\":\"漏油\"},{\"key\":\"4\",\"label\":\"破损\"}]";
			oilStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return oilStatus;
	}

	public static List<ApmDropDownItem> fetchTipStatus() {
		if (tipStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"外观良好，通过测试\"},{\"key\":\"2\",\"label\":\"外观存在问题，未通过测试\"}]";
			tipStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return tipStatus;
	}

	public static List<ApmDropDownItem> fetchElectricLeakageStatus() {
		if (electricLeakageStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"未测试\"},{\"key\":\"2\",\"label\":\"通过\"},{\"key\":\"3\",\"label\":\"超标\"}]";
			electricLeakageStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return electricLeakageStatus;
	}

	public static List<ApmDropDownItem> fetchFirstSeenStatus() {
		if (firstSeenStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"因探头外观存在风险，未执行检测\"},{\"key\":\"2\",\"label\":\"通道灵敏度测试值在正常范围内，无信号缺失通道\"},{\"key\":\"3\",\"label\":\"部分通道有短路\"},{\"key\":\"4\",\"label\":\"通道灵敏度缺失/衰弱\"}]";
			firstSeenStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return firstSeenStatus;
	}

	public static List<ApmDropDownItem> fetchFirstTipStatus() {
		if (firstTipStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"未执行FIRSTCALL检测，建议测试其他环节综合判断\"},{\"key\":\"2\",\"label\":\"测试值良好，无功能故障\"},{\"key\":\"3\",\"label\":\"探头功能故障，未通过测试\"}]";
			firstTipStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return firstTipStatus;
	}

	public static List<ApmDropDownItem> fetchResultStatus() {
		if (resultStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"探头状态良好\"},{\"key\":\"2\",\"label\":\"图像有干扰\"},{\"key\":\"3\",\"label\":\"图像远场衰减\"},{\"key\":\"4\",\"label\":\"图像缺波\"},{\"key\":\"5\",\"label\":\"探头角度不能旋转\"},{\"key\":\"6\",\"label\":\"马达不能转\"},{\"key\":\"7\",\"label\":\"无法校准\"},{\"key\":\"8\",\"label\":\"温度报警\"},{\"key\":\"9\",\"label\":\"探头无法识别\"},{\"key\":\"10\",\"label\":\"连接探头报错/导致重启\"},{\"key\":\"11\",\"label\":\"通道短路\"},{\"key\":\"12\",\"label\":\"通道灵敏度减弱\"},{\"key\":\"13\",\"label\":\"漏电流超标\"},{\"key\":\"14\",\"label\":\"漏油\"},{\"key\":\"15\",\"label\":\"油囊破损\"},{\"key\":\"16\",\"label\":\"线缆破损\"},{\"key\":\"17\",\"label\":\"线缆断裂\"},{\"key\":\"18\",\"label\":\"透镜起泡\"},{\"key\":\"19\",\"label\":\"透镜断裂\"},{\"key\":\"20\",\"label\":\"外壳开裂\"},{\"key\":\"21\",\"label\":\"护套脱落或破损\"},{\"key\":\"22\",\"label\":\"连接器锁扣脱落\"},{\"key\":\"23\",\"label\":\"连接器外壳破损\"},{\"key\":\"24\",\"label\":\"连接针脚损坏\"}]";
			resultStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return resultStatus;
	}

	public static List<ApmDropDownItem> fetchRecommandStatus() {
		if (recommandStatus == null) {
			String str = "[{\"key\":\"1\",\"label\":\"建议定期检测\"},{\"key\":\"2\",\"label\":\"建议更换\"}]";
			recommandStatus = (List<ApmDropDownItem>) JSONObject.parseArray(str, ApmDropDownItem.class);
		}
		return recommandStatus;
	}

}
