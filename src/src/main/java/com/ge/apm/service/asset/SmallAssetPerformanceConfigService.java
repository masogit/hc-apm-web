package com.ge.apm.service.asset;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SmallAssetPerformanceConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
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
	private AssetInfoRepository assetInfoRepository;
	@Autowired
	private OrgInfoRepository orgInfoRepository;
	@Autowired
	private SmallAssetPerformanceConfigRepository smallAssetPerformanceConfigDao;
	
	private Map<String,List<AssetInfo>> mapAssetInfoSiteUid=new HashMap<String,List<AssetInfo>>();
	private Map<String,List<OrgInfo>> mapOrgInfoSiteUid=new HashMap<String,List<OrgInfo>>();

	public boolean checkConfigExists(SmallAssetPerformanceConfig detail){
		Integer result=smallAssetPerformanceConfigDao.findExistsByProjectNameAndAssetName(detail.getProjectName(), detail.getAssetName(),detail.getDepartment(),detail.getSiteUID());
		if(result!=null && result==1){
			return true;
		}
		else{
			return false;
		}
	}
	public void save(SmallAssetPerformanceConfig config){
		smallAssetPerformanceConfigDao.save(config);
	}
	public boolean checkData(SmallAssetPerformanceConfig detail) {
		boolean pass=true;
		if(detail.getAverage()!=null && detail.getAverage()==true){
			if(!checkListConfigAverage(detail)){
				pass=false;
			}
		}
		else{
			if(!checkListConfig(detail,false)){
				pass=false;
			}
		}
		return pass;
	}
	
	public boolean checkListConfigAverage(SmallAssetPerformanceConfig detail){
		boolean pass=true;
		String id=detail.getId();
		List<SmallAssetPerformanceConfig> listDetails=getConfig(detail.getProjectName(),detail.getDepartment(), detail.getSiteUID());
		double rate=0;
		if(listDetails!=null && listDetails.size()>0){
			double size=listDetails.size();
			double total=1;
			if(StringUtils.isEmpty(id)){
				rate=total/(size+1);
			}
			else{
				rate=total/(size);
			}
			for(SmallAssetPerformanceConfig config: listDetails){
				config.setRate(rate);
				config.setAverage(true);
			}
			detail.setRate(rate);
		}
		updateOtherConfigList(listDetails,detail,UploadFileErrorTypeEnum.RATE_WRONG,true);
		detail.setStatusDisplay(UploadFileErrorTypeEnum.RATE_WRONG,true);
		return pass;
	}
	
	public void init(){
		mapAssetInfoSiteUid.clear();
		mapOrgInfoSiteUid.clear();
	}
	public boolean checkListConfig(SmallAssetPerformanceConfig detail,boolean delete){
		boolean pass=true;
		String id=detail.getId();
		//check configList rate
		List<SmallAssetPerformanceConfig> listDetails=getConfig(detail.getProjectName(), detail.getDepartment(),detail.getSiteUID());
		double rate=0;
		if(listDetails!=null && listDetails.size()>0){
			for(SmallAssetPerformanceConfig config: listDetails){
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
		if(!delete){
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
	
	public void rateCalculate(List<SmallAssetPerformanceConfig> list,String projectName,String department,String siteUid){
		double size=list.size()-1;
		List<SmallAssetPerformanceConfig> listDetails=getConfig(projectName, department,siteUid);
		if(listDetails!=null && listDetails.size()>0){
			size+=listDetails.size();
		}
    	double total=1;
    	double re=total/size;
    	for(int i=0;i<list.size();++i){
    		SmallAssetPerformanceConfig tempConfig=list.get(i);
    		if(StringUtils.isNotEmpty(tempConfig.getAssetName())){
    			tempConfig.setRate(re);
    		}
    	}
	}
	
	private void updateOtherConfigList(List<SmallAssetPerformanceConfig> list,SmallAssetPerformanceConfig detail,UploadFileErrorTypeEnum statusDisplay,boolean pass ){
		
		for(SmallAssetPerformanceConfig config: list){
			if(config.getProjectName().equals(detail.getProjectName())){
				config.setStatusDisplay(statusDisplay,pass);
			}
		}
		smallAssetPerformanceConfigDao.save(list);
	}
	
	private void saveSmallAssetPerformanceConfig(SmallAssetPerformanceConfig newDetail){
		SmallAssetPerformanceConfig dbDetail=smallAssetPerformanceConfigDao.findByProjectNameAndAssetName(newDetail.getProjectName(), newDetail.getAssetName(),newDetail.getDepartment(),newDetail.getSiteUID());
		if(dbDetail!=null){
			newDetail.setId(dbDetail.getId());
			checkData(newDetail);
			smallAssetPerformanceConfigDao.save(newDetail);
		}
		else{
			checkData(newDetail);
			smallAssetPerformanceConfigDao.save(newDetail);
		}
	}
	
	public boolean checkDepartName(List<OrgInfo> list,String name){
		boolean result=false;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				if(list.get(i).getName().equals(name)){
					return true;
				}
			}
		}
		return result;
	}
	private List<SmallAssetPerformanceConfig> getConfig(String projectName,String siteUid){
		return smallAssetPerformanceConfigDao.findConfigByProjectNameAndSiteUID(projectName, siteUid);
	}
	
	public List<SmallAssetPerformanceConfig> getConfig(String projectName,String department,String siteUid){
		List<SmallAssetPerformanceConfig> listRe=getConfig(projectName,siteUid);
		List<SmallAssetPerformanceConfig> listNew=new ArrayList<SmallAssetPerformanceConfig>();
		if(listRe!=null&& listRe.size()>0){
			for(int i=0;i<listRe.size();++i){
				SmallAssetPerformanceConfig tempConfig=listRe.get(i);
				if(tempConfig.getDepartment().equals(department)){
					listNew.add(tempConfig);
				}
			}
		}
		return listNew;
	}
	
	public List<OrgInfo> getDepart(List<OrgInfo> list,String name){
		List<OrgInfo> listRe=new ArrayList<OrgInfo>();
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				OrgInfo tempOrg=list.get(i);
				if(tempOrg.getName().equals(name)){
					listRe.add(tempOrg);
				}
			}
		}
		return listRe;
	}
	
	public boolean checkAssetInfoName(List<AssetInfo> listAsset,List<OrgInfo> listOrg,String name){
		boolean result=false;
		if(listAsset!=null && listAsset.size()>0 && listOrg!=null && listOrg.size()>0){
			for(int i=0;i<listAsset.size();++i){
				AssetInfo tempAsset=listAsset.get(i);
				if(tempAsset.getName().equals(name)){
					for(int j=0;j<listOrg.size();++j){
						OrgInfo tempOrg=listOrg.get(j);
						if(tempAsset.getClinicalDeptUID().equals(tempOrg.getUid())){
							return true;
						}
					}
				}
			}
		}
		return result;
	}
	
	public List<OrgInfo> getAccessClinical(String siteUid){
		List<OrgInfo> listOrg=mapOrgInfoSiteUid.get(siteUid);
		if(listOrg==null){
			listOrg=orgInfoRepository.getDeptListBySiteUid(siteUid);
			mapOrgInfoSiteUid.put(siteUid, listOrg);
		}
		return listOrg;
	}
	
	public List<AssetInfo> getAccessAssetInfo(String siteUid,boolean blnDistinct){
		List<AssetInfo> listAsset=mapAssetInfoSiteUid.get(siteUid);
		if(listAsset==null){
			listAsset=assetInfoRepository.findBySiteUid(siteUid);
			mapAssetInfoSiteUid.put(siteUid, listAsset);
		}
		if(blnDistinct){
			List<AssetInfo> listNewAsset=new ArrayList<AssetInfo>();
			Set<String> setName=new HashSet<String>();
			if(listAsset!=null && listAsset.size()>0){
				for(int i=0;i<listAsset.size();++i){
					AssetInfo temp=listAsset.get(i);
					if(!setName.contains(temp.getName())){
						setName.add(temp.getName());
						listNewAsset.add(temp);
					}
				}
			}
			return listNewAsset;
		}
		else{
			return listAsset;
		}
	}
	
	public List<AssetInfo> getAccessAssetInfo(String siteUid,String assetName,String department){
		List<AssetInfo> listAsset=mapAssetInfoSiteUid.get(siteUid);
		if(listAsset==null){
			listAsset=assetInfoRepository.findBySiteUid(siteUid);
			mapAssetInfoSiteUid.put(siteUid, listAsset);
		}
		List<AssetInfo> listNewAsset=new ArrayList<AssetInfo>();
		if(listAsset!=null && listAsset.size()>0){
			for(int i=0;i<listAsset.size();++i){
				AssetInfo tempAsset=listAsset.get(i);
				if(tempAsset.getName().equals(assetName)&& department.equals(orgInfoRepository.findByUid(tempAsset.getClinicalDeptUID()).getName())){
					listNewAsset.add(tempAsset);
				}
			}
		}
		return listNewAsset;
	}
	
	public List<AssetInfo> getAccessAssetInfo(String siteUid,String clinicalUid){
		List<AssetInfo> listAsset=assetInfoRepository.findBySiteAndClinical(siteUid,clinicalUid);
		List<AssetInfo> listNewAsset=new ArrayList<AssetInfo>();
		Set<String> setName=new HashSet<String>();
		if(listAsset!=null && listAsset.size()>0){
			for(int i=0;i<listAsset.size();++i){
				AssetInfo temp=listAsset.get(i);
				if(!setName.contains(temp.getName())){
					setName.add(temp.getName());
					listNewAsset.add(temp);
				}
			}
		}
		return listNewAsset;
	}

	public OrgInfo getOrgInfoByName(List<OrgInfo> list,String name){
		OrgInfo orgInfo=null;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				OrgInfo temp=list.get(i);
				if(name.equals(temp.getName())){
					orgInfo=temp;
					break;
				}
			}
		}
		return orgInfo;
	}
	public AssetInfo getAssetInfoByName(List<AssetInfo> list,String name){
		AssetInfo r=null;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();++i){
				AssetInfo temp=list.get(i);
				if(name.equals(temp.getName())){
					r=temp;
					break;
				}
			}
		}
		return r;
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
				detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,true);
				if(StringUtils.isEmpty(projectName)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"项目名称不能为空；");
					pass=false;
				}
				if(StringUtils.isEmpty(assetName)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"设备名称不能为空；");
					pass=false;
				}
				if(StringUtils.isEmpty(department)){
					detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"科室名称不能为空；");
					pass=false;
				}
				if(pass){
					pass=checkDepartName(getAccessClinical(detail.getSiteUID()), department);
					if(!pass){
						detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"科室名称不存在");
					}
					pass=checkAssetInfoName(getAccessAssetInfo(detail.getSiteUID(),false),getDepart(getAccessClinical(detail.getSiteUID()), department), assetName);
					if(!pass){
						detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR,"对应科室设备名称不存在");
					}
				}
				detail.setProjectName(projectName);
				detail.setProjectNumber(projectNumber);
				detail.setDepartment(department);
				detail.setAssetName(assetName);
				detail.setRate(rate);
				detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR,true);
				if(!pass){
					importFailedDataList.add(detail);
					continue;
				}
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
