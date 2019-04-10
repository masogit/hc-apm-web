package com.ge.apm.service.asset;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SmallAssetPerformanceConfigRepository;
import com.ge.apm.dao.SmallAssetPerformanceReportRepository;
import com.ge.apm.dao.SmallAssetPerformanceRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SmallAssetPerformance;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.SmallAssetPerformanceReport;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelParser;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Service
public class SmallAssetPerformanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmallAssetPerformanceService.class);

    private final String HEADER_DEPARTMENT = "科室";
    private final String HEADER_COSTTYPE = "费用类别";
    private final String HEADER_PROJECTNAME = "项目名称";
    private final String HEADER_PROJECTNUMBER = "项目编号";
    private final String HEADER_STANDARD = "单价";
    private final String HEADER_QUANTITY = "数量";
    private final String HEADER_AMOUNT = "金额";
    private final String HEADER_HAPPENDATE = "日期";

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private SmallAssetPerformanceRepository smallAssetPerformanceDao;

    @Autowired
    private SmallAssetPerformanceReportRepository reportDao;

    @Autowired
    private SmallAssetPerformanceConfigService smallAssetPerformanceConfigService;
    
    @Autowired
	private SmallAssetPerformanceConfigRepository smallAssetPerformanceConfigDao;

    private Map<String, String> mapConfigMsg = new HashMap<String, String>();
    private Map<String,List<SmallAssetPerformanceConfig>> mapConfigSiteUid=new HashMap<String,List<SmallAssetPerformanceConfig>>();

    public boolean checkExists(SmallAssetPerformance detail) {
        Integer result = smallAssetPerformanceDao.findExistsByProjectNameAndHappenDate(detail.getProjectName(), detail.getDepartment(), detail.getHappenDate(),
                detail.getSiteUID());
        if (result != null && result == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkData(SmallAssetPerformance detail) {
        boolean pass = checkConfigRight(detail);
        if (pass) {
            pass = checkConfig(detail);
        }
        return pass;
    }
    
    private List<SmallAssetPerformanceConfig> getConfig(String projectName,String siteUid){
		String str=projectName+siteUid;
		List<SmallAssetPerformanceConfig> listRe=mapConfigSiteUid.get(str);
		if(listRe==null || listRe.size()==0){
			listRe=smallAssetPerformanceConfigDao.findConfigByProjectNameAndSiteUID(projectName, siteUid);
			mapConfigSiteUid.put(str, listRe);
		}
		return listRe;
	}
	
	private List<SmallAssetPerformanceConfig> getConfig(String projectName,String department,String siteUid){
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

    private boolean checkConfigRight(SmallAssetPerformance detail) {
        List<SmallAssetPerformanceConfig> listConfig = getConfig(detail.getProjectName(), detail.getDepartment(), detail.getSiteUID());
        boolean pass = true;
        for (SmallAssetPerformanceConfig config : listConfig) {
            if (!config.getStatusCode().equals("1")) {
                pass = false;
                break;
            }
        }
        if (pass) {
            detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_WRONG, true);
        } else {
            detail.setStatusDisplay(UploadFileErrorTypeEnum.CONFIG_WRONG, false);
        }
        return pass;
    }

    private void saveSmallAssetPerformance(SmallAssetPerformance newDetail) {
        SmallAssetPerformance dbDetail = smallAssetPerformanceDao.findByProjectNameAndHappenDate(newDetail.getProjectName(), newDetail.getDepartment(), newDetail.getHappenDate(),
                newDetail.getSiteUID());
        if (dbDetail != null) {
            newDetail.setId(dbDetail.getId());
            smallAssetPerformanceDao.save(newDetail);
        } else {
            smallAssetPerformanceDao.save(newDetail);
        }
    }

    private void saveSmallAssetPerformance(List<SmallAssetPerformance> list) {
        for (int i = 0; i < list.size(); ++i) {
            SmallAssetPerformance tempobj = list.get(i);
            saveSmallAssetPerformance(tempobj);
            insertReportData(tempobj);
        }
    }

    public int checkDepartName(List<OrgInfo> listOrg, List<SmallAssetPerformanceConfig> listConfig, String department) {
        int result = 0;
        boolean has1 = false;
        if (listOrg != null && listOrg.size() > 0 && listConfig != null && listConfig.size() > 0) {
            for (int i = 0; i < listOrg.size(); ++i) {
                OrgInfo tempOrg = listOrg.get(i);
                if (tempOrg.getName().equals(department)) {
                    has1 = true;
                    for (int j = 0; j < listConfig.size(); ++j) {
                        SmallAssetPerformanceConfig tempConfig = listConfig.get(j);
                        if (department.equals(tempConfig.getDepartment())) {
                            return 2;
                        }
                    }
                }
            }
        }
        if (has1) {
            result = 1;
        }
        return result;
    }

    public boolean checkProjectName(List<SmallAssetPerformanceConfig> listConfig, String projectName) {
        boolean result = false;
        if (listConfig != null && listConfig.size() > 0) {
            for (int j = 0; j < listConfig.size(); ++j) {
                SmallAssetPerformanceConfig tempConfig = listConfig.get(j);
                if (projectName.equals(tempConfig.getProjectName())) {
                    return true;
                }
            }
        }
        return result;
    }

    private boolean checkConfig(SmallAssetPerformance detail) {
        String projectName = detail.getProjectName();
        String department = detail.getDepartment();
        String siteUid = detail.getSiteUID();
        String str = projectName + department + siteUid;
        boolean result = true;
        String errorMessage = mapConfigMsg.get(str);
        if (errorMessage == null) {
            StringBuilder sb=new StringBuilder();
            List<SmallAssetPerformanceConfig> listConfig = getConfig(projectName, department, siteUid);
            if (listConfig != null && listConfig.size() > 0) {
                for (int i = 0; i < listConfig.size(); ++i) {
                    SmallAssetPerformanceConfig tempConfig = listConfig.get(i);
                    result = checkAssetInfo(siteUid, tempConfig.getAssetName(), department);
                    if (!result) {
                    	sb.append(tempConfig.getAssetName()+";");
                    }
                }
            }
            mapConfigMsg.put(str, sb.toString());
        }
        errorMessage = mapConfigMsg.get(str);
        if (errorMessage.length() == 0) {
        } else {
            result = false;
            detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "科室内不存在设备："+errorMessage);
        }
        return result;
    }

    private boolean checkAssetInfo(String siteUid, String assetName, String department) {
        List<AssetInfo> listAssetInfo = smallAssetPerformanceConfigService.getAccessAssetInfo(siteUid, assetName, department);
        if (listAssetInfo != null && listAssetInfo.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void init() {
        mapConfigMsg.clear();
        smallAssetPerformanceConfigService.init();
        mapConfigSiteUid.clear();
    }

    public List<SmallAssetPerformance> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<SmallAssetPerformance> importFailedDataList = new ArrayList<>();
        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<String> headers = new ArrayList<>();
        headers.add(HEADER_DEPARTMENT);
        headers.add(HEADER_COSTTYPE);
        // headers.add(HEADER_CODE);
        headers.add(HEADER_PROJECTNAME);
        headers.add(HEADER_PROJECTNUMBER);
        headers.add(HEADER_STANDARD);
        headers.add(HEADER_QUANTITY);
        headers.add(HEADER_AMOUNT);
        headers.add(HEADER_HAPPENDATE);
        Map<String, Integer> header = ExcelParser.findHeader(sheet, headers);
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

        int departmentIndex = header.get(HEADER_DEPARTMENT);
        int costtypeIndex = header.get(HEADER_COSTTYPE);
        // int codeIndex=header.get(HEADER_CODE);
        int projectnameIndex = header.get(HEADER_PROJECTNAME);
        int projectnumberIndex = header.get(HEADER_PROJECTNUMBER);
        int standardIndex = header.get(HEADER_STANDARD);
        int quantityIndex = header.get(HEADER_QUANTITY);
        int amountIndex = header.get(HEADER_AMOUNT);
        int happendateIndex = header.get(HEADER_HAPPENDATE);
        List<SmallAssetPerformance> listRe = new ArrayList<SmallAssetPerformance>();
        for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            SmallAssetPerformance detail = new SmallAssetPerformance();
            try {
                detail.setExcelIdex(j + 1);
                detail.setTenantUID(user.getTenantUID());
                detail.setInstitutionUID(user.getInstitutionUID());
                detail.setHospitalUID(user.getHospitalUID());
                detail.setSiteUID(user.getSiteUID());
                department = ExcelParser.getCellStringValue(row, departmentIndex);
                costtype = ExcelParser.getCellStringValue(row, costtypeIndex);
                // code=ExcelParser.getCellStringValue(row, codeIndex);
                projectname = ExcelParser.getCellStringValue(row, projectnameIndex);
                projectnumber = ExcelParser.getCellStringValue(row, projectnumberIndex);
                standard = ExcelParser.getCellNumberValue(row, standardIndex);
                quantity = ExcelParser.getCellNumberValue(row, quantityIndex);
                amount = ExcelParser.getCellNumberValue(row, amountIndex);
                happendate = ExcelParser.getCellDateValue(row, happendateIndex, sdf);
                detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, true);
                if (StringUtils.isEmpty(projectname)) {
                    detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "项目名称不能为空；");
                    pass = false;
                }
                if (happendate == null) {
                    detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "日期不能为空；");
                    pass = false;
                }
                if (StringUtils.isEmpty(department)) {
                    detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "科室不能为空；");
                    pass = false;
                }
                if (pass) {
                    pass = checkProjectName(getConfig(projectname,detail.getSiteUID()), projectname);
                    if (!pass) {
                        detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "项目不存在");
                    }
                    if(pass){
                    	pass = checkProjectName(getConfig(projectname, department, detail.getSiteUID()), projectname);
                        if (!pass) {
                            detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "科室不存在");
                        }
                        if (pass) {
                            int result = checkDepartName(smallAssetPerformanceConfigService.getAccessClinical(detail.getSiteUID()),getConfig(projectname, department, detail.getSiteUID()), department);
                            if (result == 0) {
                                pass = false;
                                detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "科室名称不存在");
                            } else if (result == 1) {
                                pass = false;
                                detail.setStatusDisplay(UploadFileErrorTypeEnum.OTHER_ERROR, "配置项目中科室名称不存在");
                            } else if (result == 2) {
                                pass = true;
                            }
                        }
                    }
                }
                detail.setDepartment(department);
                detail.setCostType(costtype);
                // detail.setCode(code);
                detail.setProjectName(projectname);
                detail.setProjectNumber(projectnumber);
                detail.setStandard(standard);
                detail.setQuantity(quantity);
                detail.setAmount(amount);
                detail.setHappenDate(happendate);
                detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR, true);
                if (!pass) {
                    importFailedDataList.add(detail);
                    continue;
                }
                if (checkData(detail)) {
                    listRe.add(detail);
                } else {
                    importFailedDataList.add(detail);
                }
            } catch (Exception e) {
                // TODO: handle exception
                LOGGER.info(e.getMessage());
                detail.setStatusDisplay(UploadFileErrorTypeEnum.FORMAT_ERROR, false);
                importFailedDataList.add(detail);
            }
        }
        if (importFailedDataList.size() == 0) {
            saveSmallAssetPerformance(listRe);
        }
        return importFailedDataList;
    }

    public void insertReportData(SmallAssetPerformance detail) {
        // no update action, so delete it.
        deleteReportData(detail);
        List<SmallAssetPerformanceConfig> listConfig = getConfig(detail.getProjectName(), detail.getDepartment(), detail.getSiteUID());
        if (listConfig != null && listConfig.size() > 0) {
            List<AssetInfo> listAssetInfo = null;
            List<SmallAssetPerformanceReport> listReport = new ArrayList<SmallAssetPerformanceReport>();
            int count = 0;
            SmallAssetPerformanceReport report = null;
            for (SmallAssetPerformanceConfig obj : listConfig) {
                listAssetInfo = smallAssetPerformanceConfigService.getAccessAssetInfo(obj.getSiteUID(), obj.getAssetName(), obj.getDepartment());
                if (listAssetInfo != null && listAssetInfo.size() > 0) {
                    count = listAssetInfo.size();
                    for (AssetInfo assetInfo : listAssetInfo) {
                        report = new SmallAssetPerformanceReport();
                        report.setSmallAssetPerformanceId(detail.getId());
                        report.setAssetInfo(assetInfo);
                        report.setAmount(Double.valueOf(new BigDecimal(detail.getAmount() * obj.getRate() / count).setScale(2, RoundingMode.HALF_UP).toString()));
                        report.setAssetName(obj.getAssetName());
                        report.setAssetUID(assetInfo.getUid());
                        report.setCostType(detail.getCostType());
                        report.setDepartment(detail.getDepartment());
                        report.setHospitalUID(detail.getHospitalUID());
                        report.setInstitutionUID(detail.getInstitutionUID());
                        report.setProjectName(detail.getProjectName());
                        report.setQuantity(Double.valueOf(new BigDecimal(detail.getQuantity() * obj.getRate() / count).setScale(2, RoundingMode.HALF_UP).toString()));
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

    public void deleteReportData(SmallAssetPerformance detail) {
        List<SmallAssetPerformanceReport> listReports = reportDao.findReportBySmallAssetPerformanceId(detail.getId());
        if (listReports != null && listReports.size() > 0) {
            reportDao.delete(listReports);
        }
    }
}
