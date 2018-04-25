package com.ge.apm.service.asset;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelParser;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class SmallAssetPerformanceConfigService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmallAssetPerformanceConfigService.class);

	private final String HEADER_PROJECTNAME = "项目名称";
	private final String HEADER_PROJECTNUMBER = "项目编号";
	private final String HEADER_DEPARTMENT = "科室名称";
	private final String HEADER_ASSETNAME = "设备名称";
	private final String HEADER_RATE = "占比";

	@Autowired
	private AssetInfoRepository assetInfoDao;

	@Autowired
	private SmallAssetPerformanceConfigRepository smallAssetPerformanceConfigDao;

	public boolean checkConfigExists(SmallAssetPerformanceConfig detail){
		Integer result=smallAssetPerformanceConfigDao.findExistsByProjectNameAndAssetName(detail.getProjectName(), detail.getAssetName(),detail.getSiteUID());
		if(result!=null && result==1){
			return true;
		}
		else{
			return false;
		}
	}
	public boolean checkData(SmallAssetPerformanceConfig detail,String id,boolean deleteAction) {
		boolean pass=true;
		if(deleteAction == false){
			if(!checkOneConfig(detail, id)){
				pass=false;
			}
		}
		if(!checkListConfig(detail, id, deleteAction)){
			pass=false;
		}
		return pass;
	}
	
	private boolean checkOneConfig(SmallAssetPerformanceConfig detail,String id){
		boolean pass=true;
		//check asset exists
		List<AssetInfo> listAssetInfo = this.getAssetInfoByNameAndSiteUID(detail.getAssetName(), detail.getSiteUID());
		if (listAssetInfo != null && listAssetInfo.size() > 0) {
			detail.setStatusDisplay(UploadFileErrorTypeEnum.ASSET_NOT_EXISTS,true);
		} else {
			detail.setStatusDisplay(UploadFileErrorTypeEnum.ASSET_NOT_EXISTS,false);
			pass=false;
		}
		return pass;
	}
	
	public boolean checkListConfig(SmallAssetPerformanceConfig detail,String id,boolean deleteAction){
		boolean pass=true;
		//check configList rate
		List<SmallAssetPerformanceConfig> listDetails=smallAssetPerformanceConfigDao.findConfigByProjectNameAndSiteUID(detail.getProjectName(), detail.getSiteUID());
		double rate=0;
		if(listDetails!=null && listDetails.size()>0){
			for(SmallAssetPerformanceConfig config: listDetails){
				if(config.getProjectName().equals(detail.getProjectName())){
					if(config.getRate()!=null){
						//update, so ignore the saved id
						if(id!=null && id.equals(config.getId())){
							continue;
						}
						else{
							rate+=config.getRate();
						}
					}
				}
			}
		}
		if(!deleteAction){
			rate+=detail.getRate();
		}
		if(rate!=1){
			updateOtherConfigList(listDetails,detail,UploadFileErrorTypeEnum.RATE_WRONG,false);
			detail.setStatusDisplay(UploadFileErrorTypeEnum.RATE_WRONG,false);
			pass=false;
		}
		else{
			updateOtherConfigList(listDetails,detail,UploadFileErrorTypeEnum.RATE_WRONG,true);
			detail.setStatusDisplay(UploadFileErrorTypeEnum.RATE_WRONG,true);
		}
		return pass;
	}
	
	private void updateOtherConfigList(List<SmallAssetPerformanceConfig> list,SmallAssetPerformanceConfig detail,UploadFileErrorTypeEnum statusDisplay,boolean pass ){
		
		for(SmallAssetPerformanceConfig config: list){
			if(config.getProjectName().equals(detail.getProjectName())){
				config.setStatusDisplay(statusDisplay,pass);
			}
		}
		smallAssetPerformanceConfigDao.save(list);
	}

	private List<AssetInfo> getAssetInfoByNameAndSiteUID(String name, String siteUID) {
		List<AssetInfo> listAssetInfo = null;

		if (!StringHelper.isNullOrEmptyString(name)) {
			listAssetInfo = assetInfoDao.getAssetInfoByNameAndSiteUID(name, siteUID);
		}

		return listAssetInfo;
	}
	
	private void saveSmallAssetPerformanceConfig(SmallAssetPerformanceConfig newDetail){
		SmallAssetPerformanceConfig dbDetail=smallAssetPerformanceConfigDao.findByProjectNameAndAssetName(newDetail.getProjectName(), newDetail.getAssetName(),newDetail.getSiteUID());
		if(dbDetail!=null){
			newDetail.setId(dbDetail.getId());
			checkData(newDetail,dbDetail.getId(),false);
			smallAssetPerformanceConfigDao.save(newDetail);
		}
		else{
			checkData(newDetail,null,false);
			smallAssetPerformanceConfigDao.save(newDetail);
		}
	}

	public List<SmallAssetPerformanceConfig> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<SmallAssetPerformanceConfig> importFailedDataList = new ArrayList<>();
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
        headers.add(HEADER_PROJECTNAME);
        headers.add(HEADER_PROJECTNUMBER);
        headers.add(HEADER_DEPARTMENT);
        headers.add(HEADER_ASSETNAME);
        headers.add(HEADER_RATE);
		Map<String, Integer> header = ExcelParser.findHeader(sheet,headers);
		if (header.isEmpty()) {
			return null;
		}
		String projectName;
		String projectNumber;
		String department;
		String assetName;
		double rate;
		int projectNameIndex=header.get(HEADER_PROJECTNAME);
		int projectNubmerIndex=header.get(HEADER_PROJECTNUMBER);
		int departmentIndex=header.get(HEADER_DEPARTMENT);
		int assetNameIndex=header.get(HEADER_ASSETNAME);
		int rateIndex=header.get(HEADER_RATE);
		for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);
			if (row == null) {
				continue;
			}
			boolean pass=true;
			SmallAssetPerformanceConfig detail = new SmallAssetPerformanceConfig();			
			try {
				detail.setExcelIdex(j + 1);
				detail.setTenantUID(user.getTenantUID());
				detail.setInstitutionUID(user.getInstitutionUID());
				detail.setHospitalUID(user.getHospitalUID());
				detail.setSiteUID(user.getSiteUID());
				projectName=ExcelParser.getCellStringValue(row, projectNameIndex);
				projectNumber=ExcelParser.getCellStringValue(row, projectNubmerIndex);
				department=ExcelParser.getCellStringValue(row, departmentIndex);
				assetName=ExcelParser.getCellStringValue(row, assetNameIndex);
				rate=ExcelParser.getCellNumberValue(row, rateIndex);
				if(StringUtils.isEmpty(projectName)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"项目名称不能为空；");
					pass=false;
				}
				if(StringUtils.isEmpty(assetName)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"设备名称不能为空；");
					pass=false;
				}
				if(StringUtils.isNotEmpty(projectName)&&StringUtils.isNotEmpty(assetName)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,true);
				}
				if(!pass){
					importFailedDataList.add(detail);
					continue;
				}
				detail.setProjectName(projectName);
				detail.setProjectNumber(projectNumber);
				detail.setDepartment(department);
				detail.setAssetName(assetName);
				detail.setRate(rate);
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,true);
//				checkData(detail,null,false);
//				smallAssetPerformanceConfigDao.save(detail);
				saveSmallAssetPerformanceConfig(detail);
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.info(e.getMessage());
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,false);
				importFailedDataList.add(detail);
			}
		}
		return importFailedDataList;
	}
}
