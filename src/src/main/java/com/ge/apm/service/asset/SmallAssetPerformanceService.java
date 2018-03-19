package com.ge.apm.service.asset;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.validator.internal.util.StringHelper;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SmallAssetPerformanceConfigRepository;
import com.ge.apm.dao.SmallAssetPerformanceReportRepository;
import com.ge.apm.dao.SmallAssetPerformanceRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SmallAssetPerformance;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.SmallAssetPerformanceReport;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class SmallAssetPerformanceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmallAssetPerformanceService.class);

	private final String HEADER_DEPARTMENT = "科室";
	private final String HEADER_COSTTYPE = "费别";
	private final String HEADER_CODE = "编码";
	private final String HEADER_PROJECTNAME = "项目名称";
	private final String HEADER_PROJECTNUMBER = "项目编号";
	private final String HEADER_STANDARD = "一类标准";
	private final String HEADER_QUANTITY = "数量";
	private final String HEADER_AMOUNT = "金额";
	private final String HEADER_HAPPENDATE = "日期";

	@Autowired
	private AssetInfoRepository assetInfoDao;

	@Autowired
	private SmallAssetPerformanceConfigRepository configDao;

	@Autowired
	private SmallAssetPerformanceRepository smallAssetPerformanceDao;
	
	@Autowired
	private SmallAssetPerformanceReportRepository reportDao;
	
	public boolean checkExists(SmallAssetPerformance detail){
		Integer result=smallAssetPerformanceDao.findExistsByProjectNameAndHappenDate(detail.getProjectName(), detail.getHappenDate(),detail.getHospitalUID());
		if(result!=null && result==1){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean checkData(SmallAssetPerformance detail) {
		boolean pass=true;
		if(!checkConfigExists(detail)){
			pass=false;
		}
		return pass;
	}
	private boolean checkConfigExists(SmallAssetPerformance detail){
		boolean pass=true;
		List<SmallAssetPerformanceConfig> listConfig = this.getConfigByProjectNameAndHospitalUID(detail.getProjectName(), detail.getHospitalUID());
		if (listConfig != null && listConfig.size() > 0) {
			detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_NOT_EXISTS,true);
			if(checkConfigRight(listConfig)){
				detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_WRONG,true);
			}
			else{
				detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_WRONG,false);
				pass=false;
			}
		} else {
			detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_NOT_EXISTS,false);
			pass=false;
		}
		return pass;
	}
	
	private boolean checkConfigRight(List<SmallAssetPerformanceConfig> listConfig){
		boolean pass=true;
		for(SmallAssetPerformanceConfig config : listConfig){
			if(!config.getStatus().equals("1")){
				pass=false;
				break;
			}
		}
		return pass;
	}

	private List<SmallAssetPerformanceConfig> getConfigByProjectNameAndHospitalUID(String projectName, String hospitalUID) {
		List<SmallAssetPerformanceConfig> listAssetInfo = null;

		if (!StringHelper.isNullOrEmptyString(projectName)) {
			listAssetInfo = configDao.findConfigByProjectNameAndHospitalUID(projectName, hospitalUID);
		}

		return listAssetInfo;
	}
	
	private void saveSmallAssetPerformance(SmallAssetPerformance newDetail){
		SmallAssetPerformance dbDetail=smallAssetPerformanceDao.findByProjectNameAndHappenDate(newDetail.getProjectName(), newDetail.getHappenDate(),newDetail.getHospitalUID());
		if(dbDetail!=null){
			newDetail.setId(dbDetail.getId());
			smallAssetPerformanceDao.save(newDetail);
		}
		else{
			smallAssetPerformanceDao.save(newDetail);
		}
	}

	public List<SmallAssetPerformance> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SmallAssetPerformance> importFailedDataList = new ArrayList<>();
		HSSFWorkbook workbook = new HSSFWorkbook(file.getInputstream());
		HSSFSheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			return null;
		}
		Map<String, Integer> header = findHeader(sheet);
		if (header.isEmpty()) {
			return null;
		}
		for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
			HSSFRow row = sheet.getRow(j);
			if (row == null || row.getCell(header.get("HEADER_PROJECTNAME")) == null || row.getCell(header.get("HEADER_PROJECTNAME")).getStringCellValue().trim() == "") {
				continue;
			}
			SmallAssetPerformance detail = new SmallAssetPerformance();
			try {
				detail.setExcelIdex(j + 1);
				detail.setTenantUID(user.getTenantUID());
				detail.setInstitutionUID(user.getInstitutionUID());
				detail.setHospitalUID(user.getHospitalUID());
				detail.setSiteUID(user.getSiteUID());
				detail.setDepartment(row.getCell(header.get("HEADER_DEPARTMENT")) == null ? null : row.getCell(header.get("HEADER_DEPARTMENT")).getStringCellValue());
				detail.setCostType(row.getCell(header.get("HEADER_COSTTYPE")) == null ? null : row.getCell(header.get("HEADER_COSTTYPE")).getStringCellValue());
				detail.setCode(row.getCell(header.get("HEADER_CODE")) == null ? null : row.getCell(header.get("HEADER_CODE")).getStringCellValue());
				detail.setProjectName(row.getCell(header.get("HEADER_PROJECTNAME")) == null ? null : row.getCell(header.get("HEADER_PROJECTNAME")).getStringCellValue());
				detail.setProjectNumber(row.getCell(header.get("HEADER_PROJECTNUMBER")) == null ? null : row.getCell(header.get("HEADER_PROJECTNUMBER")).getStringCellValue());
				detail.setStandard(row.getCell(header.get("HEADER_STANDARD")) == null ? null : row.getCell(header.get("HEADER_STANDARD")).getNumericCellValue());
				detail.setQuantity(row.getCell(header.get("HEADER_QUANTITY")) == null ? null : row.getCell(header.get("HEADER_QUANTITY")).getNumericCellValue());
				detail.setAmount(row.getCell(header.get("HEADER_AMOUNT")) == null ? null : row.getCell(header.get("HEADER_AMOUNT")).getNumericCellValue());
				detail.setHappenDate(row.getCell(header.get("HEADER_HAPPENDATE")) == null ? null : row.getCell(header.get("HEADER_HAPPENDATE")).getDateCellValue());
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,true);
				
				boolean pass=checkData(detail);
				if(pass){
//					smallAssetPerformanceDao.save(detail);
					saveSmallAssetPerformance(detail);
					insertReportData(detail);
				}
				else{
					importFailedDataList.add(detail);
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.info(e.getMessage());
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,false);
				importFailedDataList.add(detail);
			}
		}
		return importFailedDataList;
	}
	
	public void insertReportData(SmallAssetPerformance detail){
		//no update action, so delete it.
		deleteReportData(detail);
		List<SmallAssetPerformanceConfig> listConfig=configDao.findConfigByProjectNameAndHospitalUID(detail.getProjectName(), detail.getHospitalUID());
		if(listConfig!=null && listConfig.size()>0){
			String assetName="";
			List<AssetInfo> listAssetInfo=null;
			List<SmallAssetPerformanceReport> listReport=new ArrayList<SmallAssetPerformanceReport>();
			int count=0;
			SmallAssetPerformanceReport report=null;
			for(SmallAssetPerformanceConfig obj : listConfig){
				assetName=obj.getAssetName();
				listAssetInfo=assetInfoDao.getAssetInfoByNameAndHospitalUID(assetName, detail.getHospitalUID());
				if(listAssetInfo!=null && listAssetInfo.size()>0){
					count=listAssetInfo.size();
					for(AssetInfo assetInfo: listAssetInfo){
						report=new SmallAssetPerformanceReport();
						report.setSmallAssetPerformanceId(detail.getId());
						report.setAssetInfo(assetInfo);
						report.setAmount(Double.valueOf(new BigDecimal(detail.getAmount()*obj.getRate()/count).setScale(2, RoundingMode.HALF_UP).toString()));
						report.setAssetName(assetName);
						report.setAssetUID(assetInfo.getUid());
						report.setCostType(detail.getCostType());
						report.setDepartment(detail.getDepartment());
						report.setHospitalUID(detail.getHospitalUID());
						report.setInstitutionUID(detail.getInstitutionUID());
						report.setProjectName(detail.getProjectName());
						report.setQuantity(Double.valueOf(new BigDecimal(detail.getQuantity()*obj.getRate()/count).setScale(2, RoundingMode.HALF_UP).toString()));
						report.setReportDate(detail.getHappenDate());
						report.setSiteUID(detail.getSiteUID());
						report.setStandard(detail.getStandard());
						report.setTenantUID(detail.getTenantUID());
						report.setStatusDisplay(obj.getStatusDisplay());
						listReport.add(report);
					}
				}
			}
			reportDao.save(listReport);
		}
	}
	
	public void deleteReportData(SmallAssetPerformance detail){
		List<SmallAssetPerformanceReport> listReports=reportDao.findReportBySmallAssetPerformanceId(detail.getId());
		if(listReports!=null && listReports.size()>0){
			reportDao.delete(listReports);
		}
	}

	private Map<String, Integer> findHeader(HSSFSheet sheet) {
		Map<String, Integer> header = new HashMap<>();
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			boolean hasDepartment = false;
			boolean hasCostType = false;
			boolean hasCode = false;
			boolean hasProjectName = false;
			boolean hasProjectNumber = false;
			boolean hasStandard = false;
			boolean hasQuantity = false;
			boolean hasAmount = false;
			boolean hasHappenDate = false;

			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				HSSFCell cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				try {

					switch (cell.getStringCellValue()) {
					case HEADER_DEPARTMENT:
						hasDepartment = true;
						header.put("HEADER_DEPARTMENT", j);
						break;
					case HEADER_COSTTYPE:
						hasCostType = true;
						header.put("HEADER_COSTTYPE", j);
						break;
					case HEADER_CODE:
						hasCode = true;
						header.put("HEADER_CODE", j);
						break;
					case HEADER_PROJECTNAME:
						hasProjectName = true;
						header.put("HEADER_PROJECTNAME", j);
						break;
					case HEADER_PROJECTNUMBER:
						hasProjectNumber = true;
						header.put("HEADER_PROJECTNUMBER", j);
						break;
					case HEADER_STANDARD:
						hasStandard = true;
						header.put("HEADER_STANDARD", j);
						break;
					case HEADER_QUANTITY:
						hasQuantity = true;
						header.put("HEADER_QUANTITY", j);
						break;
					case HEADER_AMOUNT:
						hasAmount = true;
						header.put("HEADER_AMOUNT", j);
						break;
					case HEADER_HAPPENDATE:
						hasHappenDate = true;
						header.put("HEADER_HAPPENDATE", j);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
					continue;
				}
			}
			if (hasDepartment && hasCostType && hasCode && hasProjectName && hasProjectNumber && hasStandard && hasQuantity && hasAmount && hasHappenDate) {
				header.put("HEADER_ROW", i);
				break;
			}
		}

		return header;
	}

}
