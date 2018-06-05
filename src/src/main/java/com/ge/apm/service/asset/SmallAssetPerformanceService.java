package com.ge.apm.service.asset;

import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.ge.apm.service.utils.ExcelParser;

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
		Integer result=smallAssetPerformanceDao.findExistsByProjectNameAndHappenDate(detail.getProjectName(), detail.getHappenDate(),detail.getSiteUID());
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
		List<SmallAssetPerformanceConfig> listConfig = this.getConfigByProjectNameAndSiteUID(detail.getProjectName(), detail.getSiteUID());
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
			if(!config.getStatusCode().equals("1")){
				pass=false;
				break;
			}
		}
		return pass;
	}

	private List<SmallAssetPerformanceConfig> getConfigByProjectNameAndSiteUID(String projectName, String SiteUID) {
		List<SmallAssetPerformanceConfig> listAssetInfo = null;

		if (!StringHelper.isNullOrEmptyString(projectName)) {
			listAssetInfo = configDao.findConfigByProjectNameAndSiteUID(projectName, SiteUID);
		}

		return listAssetInfo;
	}
	
	private void saveSmallAssetPerformance(SmallAssetPerformance newDetail){
		SmallAssetPerformance dbDetail=smallAssetPerformanceDao.findByProjectNameAndHappenDate(newDetail.getProjectName(), newDetail.getHappenDate(),newDetail.getSiteUID());
		if(dbDetail!=null){
			newDetail.setId(dbDetail.getId());
			smallAssetPerformanceDao.save(newDetail);
		}
		else{
			smallAssetPerformanceDao.save(newDetail);
		}
	}

	public List<SmallAssetPerformance> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SmallAssetPerformance> importFailedDataList = new ArrayList<>();
		Workbook workbook = null;
		if(file.getFileName().toLowerCase().endsWith(".xls")){
			workbook=new HSSFWorkbook(file.getInputstream());
		}
		else if(file.getFileName().toLowerCase().endsWith(".xlsx")){
			workbook=new XSSFWorkbook(file.getInputstream());
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			return null;
		}
		List<String> headers = new ArrayList<>();
		headers.add(HEADER_DEPARTMENT);
		headers.add(HEADER_COSTTYPE);
		headers.add(HEADER_CODE);
		headers.add(HEADER_PROJECTNAME);
		headers.add(HEADER_PROJECTNUMBER);
		headers.add(HEADER_STANDARD);
		headers.add(HEADER_QUANTITY);
		headers.add(HEADER_AMOUNT);
		headers.add(HEADER_HAPPENDATE);
		Map<String, Integer> header = ExcelParser.findHeader(sheet,headers);
		if (header.isEmpty()) {
			return null;
		}
		String department;
		String costtype;
		String code;
		String projectname;
		String projectnumber;
		double standard;
		double quantity;
		double amount;
		Date happendate;
		
		int departmentIndex=header.get(HEADER_DEPARTMENT);
		int costtypeIndex=header.get(HEADER_COSTTYPE);
		int codeIndex=header.get(HEADER_CODE);
		int projectnameIndex=header.get(HEADER_PROJECTNAME);
		int projectnumberIndex=header.get(HEADER_PROJECTNUMBER);
		int standardIndex=header.get(HEADER_STANDARD);
		int quantityIndex=header.get(HEADER_QUANTITY);
		int amountIndex=header.get(HEADER_AMOUNT);
		int happendateIndex=header.get(HEADER_HAPPENDATE);

		for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);
			if (row == null) {
				continue;
			}
			boolean pass=true;
			SmallAssetPerformance detail = new SmallAssetPerformance();
			try {
				detail.setExcelIdex(j + 1);
				detail.setTenantUID(user.getTenantUID());
				detail.setInstitutionUID(user.getInstitutionUID());
				detail.setHospitalUID(user.getHospitalUID());
				detail.setSiteUID(user.getSiteUID());
				department=ExcelParser.getCellStringValue(row, departmentIndex);
				costtype=ExcelParser.getCellStringValue(row, costtypeIndex);
				code=ExcelParser.getCellStringValue(row, codeIndex);
				projectname=ExcelParser.getCellStringValue(row, projectnameIndex);
				projectnumber=ExcelParser.getCellStringValue(row, projectnumberIndex);
				standard=ExcelParser.getCellNumberValue(row, standardIndex);
				quantity=ExcelParser.getCellNumberValue(row, quantityIndex);
				amount=ExcelParser.getCellNumberValue(row, amountIndex);
				happendate=ExcelParser.getCellDateValue(row, happendateIndex,sdf);
				if(StringUtils.isEmpty(projectname)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"项目名称不能为空；");
					pass=false;
				}
				if(happendate==null){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"日期不能为空；");
					pass=false;
				}
				if(StringUtils.isNotEmpty(projectname) && (happendate != null)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,true);
				}
				if(!pass){
					importFailedDataList.add(detail);
					continue;
				}
				detail.setDepartment(department);
				detail.setCostType(costtype);
				detail.setCode(code);
				detail.setProjectName(projectname);
				detail.setProjectNumber(projectnumber);
				detail.setStandard(standard);
				detail.setQuantity(quantity);
				detail.setAmount(amount);
				detail.setHappenDate(happendate);
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,true);

				if(checkData(detail)){
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
		List<SmallAssetPerformanceConfig> listConfig=configDao.findConfigByProjectNameAndSiteUID(detail.getProjectName(), detail.getSiteUID());
		if(listConfig!=null && listConfig.size()>0){
			String assetName="";
			List<AssetInfo> listAssetInfo=null;
			List<SmallAssetPerformanceReport> listReport=new ArrayList<SmallAssetPerformanceReport>();
			int count=0;
			SmallAssetPerformanceReport report=null;
			for(SmallAssetPerformanceConfig obj : listConfig){
				assetName=obj.getAssetName();
				listAssetInfo=assetInfoDao.getAssetInfoByNameAndSiteUID(assetName, detail.getSiteUID());
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
}
