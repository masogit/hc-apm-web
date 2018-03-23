package com.ge.apm.service.asset;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;

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
		Integer result=smallAssetPerformanceConfigDao.findExistsByProjectNameAndAssetName(detail.getProjectName(), detail.getAssetName(),detail.getHospitalUID());
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
		List<AssetInfo> listAssetInfo = this.getAssetInfoByNameAndHospitalUID(detail.getAssetName(), detail.getSiteUID());
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
		List<SmallAssetPerformanceConfig> listDetails=smallAssetPerformanceConfigDao.findConfigByProjectNameAndHospitalUID(detail.getProjectName(), detail.getHospitalUID());
		if(listDetails!=null && listDetails.size()>0){
			double rate=0;
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

	private List<AssetInfo> getAssetInfoByNameAndHospitalUID(String name, String siteUID) {
		List<AssetInfo> listAssetInfo = null;

		if (!StringHelper.isNullOrEmptyString(name)) {
			listAssetInfo = assetInfoDao.getAssetInfoByNameAndHospitalUID(name, siteUID);
		}

		return listAssetInfo;
	}
	
	private void saveSmallAssetPerformanceConfig(SmallAssetPerformanceConfig newDetail){
		SmallAssetPerformanceConfig dbDetail=smallAssetPerformanceConfigDao.findByProjectNameAndAssetName(newDetail.getProjectName(), newDetail.getAssetName(),newDetail.getHospitalUID());
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
			SmallAssetPerformanceConfig detail = new SmallAssetPerformanceConfig();
			try {
				detail.setExcelIdex(j + 1);
				detail.setTenantUID(user.getTenantUID());
				detail.setInstitutionUID(user.getInstitutionUID());
				detail.setHospitalUID(user.getHospitalUID());
				detail.setSiteUID(user.getSiteUID());
				detail.setProjectName(row.getCell(header.get("HEADER_PROJECTNAME")) == null ? null : row.getCell(header.get("HEADER_PROJECTNAME")).getStringCellValue());
				detail.setProjectNumber(row.getCell(header.get("HEADER_PROJECTNUMBER")) == null ? null : row.getCell(header.get("HEADER_PROJECTNUMBER")).getStringCellValue());
				detail.setDepartment(row.getCell(header.get("HEADER_DEPARTMENT")) == null ? null : row.getCell(header.get("HEADER_DEPARTMENT")).getStringCellValue());
				detail.setAssetName(row.getCell(header.get("HEADER_ASSETNAME")) == null ? null : row.getCell(header.get("HEADER_ASSETNAME")).getStringCellValue());
				detail.setRate(row.getCell(header.get("HEADER_RATE")) == null ? null : row.getCell(header.get("HEADER_RATE")).getNumericCellValue());
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

	private Map<String, Integer> findHeader(HSSFSheet sheet) {
		Map<String, Integer> header = new HashMap<>();
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			boolean hasProjectName = false;
			boolean hasProjectNumber = false;
			boolean hasDepartment = false;
			boolean hasAssetName = false;
			boolean hasRate = false;

			for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
				HSSFCell cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				try {

					switch (cell.getStringCellValue()) {
					case HEADER_PROJECTNAME:
						hasProjectName = true;
						header.put("HEADER_PROJECTNAME", j);
						break;
					case HEADER_PROJECTNUMBER:
						hasProjectNumber = true;
						header.put("HEADER_PROJECTNUMBER", j);
						break;
					case HEADER_DEPARTMENT:
						hasDepartment = true;
						header.put("HEADER_DEPARTMENT", j);
						break;
					case HEADER_ASSETNAME:
						hasAssetName = true;
						header.put("HEADER_ASSETNAME", j);
						break;
					case HEADER_RATE:
						hasRate = true;
						header.put("HEADER_RATE", j);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
					continue;
				}
			}
			if (hasProjectName && hasProjectNumber && hasDepartment && hasAssetName && hasRate) {
				header.put("HEADER_ROW", i);
				break;
			}
		}

		return header;
	}

}
