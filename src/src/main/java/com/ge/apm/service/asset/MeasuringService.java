package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MeasuringCertificateDetail;
import com.ge.apm.domain.UserAccount;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.hibernate.validator.internal.util.StringHelper;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/11/7.
 */
@Component
public class MeasuringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasuringService.class);

    private final String HEADER_CERTIFICATION_NO = "证书编号";
    private final String HEADER_APPLIANCE_NAME = "器具名称";
    private final String HEADER_MANUFACTURE = "制造厂";
    private final String HEADER_MODEL = "型号";
    private final String HEADER_ASSET_SN = "设备序列号SN";
    private final String HEADER_ASSET_NO = "资产编号";
    private final String HEADER_APPLIANCE_NO = "器具编号";
    private final String HEADER_PAYED = "付款否";
    private final String HEADER_VERIFICATION_CONCLUSION = "检定结论";
    private final String HEADER_VERIFICATION_DATE = "检定日期";
    private final String HEADER_EXPIRE_DATE = "有效期至";
    private final String HEADER_VERIFICATION_UNIT = "检测单位";
    private final String HEADER_VERIFIER = "检定人员";
    private final String HEADER_ITEM = "项别";
    private final String HEADER_CATEGORY = "种别";
    private final String HEADER_CLINICAL_DEPT = "送检单位";
    private final String HEADER_REMARK = "备注";
    private final String HEADER_OVERDUE = "过期否";
    private final String HEADER_FILE_NO = "存档记录";

    @Autowired
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;

    public MeasuringCertificateDetail operateMeasuringAndAssetInfo(MeasuringCertificateDetail measuringCertificateDetail) {
        boolean flag = false;
        AssetInfo tempAssetInfo = null;
        String errorInfo = "";
        List<AssetInfo> tempAssetInfoList = null;

        if(null != measuringCertificateDetail.getAssetSN()){
            tempAssetInfoList = this.getAssetInfoBySerialNum(measuringCertificateDetail.getAssetSN(), measuringCertificateDetail.getSiteUID());
            if(tempAssetInfoList == null || tempAssetInfoList.isEmpty()){
                errorInfo += "[设备序列号SN不能匹配到资产设备]";
            }else{
                if(tempAssetInfoList.size() == 1){
                    flag = true;
                }else{
                    errorInfo += "[设备序列号SN匹配到(" + tempAssetInfoList.size() + ")个资产设备]";
                }
            }
        }

        if(null != measuringCertificateDetail.getFinanceNo() && !flag){
            tempAssetInfoList = this.getAssetInfoByFinancingNum(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getSiteUID());
            if(tempAssetInfoList == null || tempAssetInfoList.isEmpty()){
                errorInfo += "[资产编号不能匹配到设备]";
            }else{
                if(tempAssetInfoList.size() == 1){
                    flag = true;
                }else{
                    errorInfo += "[设备序列号SN匹配到(" + tempAssetInfoList.size() + ")个资产设备]";
                }
            }
        }

        if(flag){
            if(tempAssetInfoList.size() == 1){
                tempAssetInfo = tempAssetInfoList.get(0);
            }
        }

        if(tempAssetInfo != null){
            if(tempAssetInfo.getLastMeteringDate() != null && tempAssetInfo.getLastMeteringDate().before(measuringCertificateDetail.getCheckDate())){
                tempAssetInfo.setNextMeteringDate(measuringCertificateDetail.getValidDateTo());
                tempAssetInfo.setLastMeteringDate(measuringCertificateDetail.getCheckDate());
                tempAssetInfo.setAccountingCategory(measuringCertificateDetail.getStatus());
                tempAssetInfo.setMeteringNO(measuringCertificateDetail.getCertificateNo());
                if (measuringCertificateDetail.getStatus() == 2) {//计量不合格
                    tempAssetInfo.setUsageStatus(2);//停用
                    tempAssetInfo.setStatus(5);
                }
            }

            assetInfoDao.save(tempAssetInfo);

            //向MeasuringCertificateDetail表中插入导入记录
            /*MeasuringCertificateDetail tempMeasuringCertificateDetail = measuringCertificateDetailDao.findByAssetSNAndCheckDateAndTenantUID(measuringCertificateDetail.getAssetSN(), measuringCertificateDetail.getCheckDate(), measuringCertificateDetail.getTenantUID());
            if(tempMeasuringCertificateDetail == null){
                tempMeasuringCertificateDetail = measuringCertificateDetailDao.findByFinanceNoAndCheckDateAndTenantUID(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getCheckDate(), measuringCertificateDetail.getTenantUID());
            }*/

            MeasuringCertificateDetail tempMeasuringCertificateDetail = measuringCertificateDetailDao.findByAssetUIDAndCheckDateAndTenantUID(tempAssetInfo.getUid(), measuringCertificateDetail.getCheckDate(), measuringCertificateDetail.getTenantUID());

            if (tempMeasuringCertificateDetail != null) {
                measuringCertificateDetail.setId(tempMeasuringCertificateDetail.getId());
            }
            measuringCertificateDetail.setAssetUID(tempAssetInfo.getUid());
            measuringCertificateDetailDao.save(measuringCertificateDetail);
        }

        if(!flag){
            measuringCertificateDetail.setErrorInfo(errorInfo);
            return measuringCertificateDetail;
        }else{
            return null;
        }
    }

    private List<AssetInfo> getAssetInfoByFinancingNum(String financingNum, String siteUID){
        List<AssetInfo> tempAssetInfoList = null;

        if(!StringHelper.isNullOrEmptyString(financingNum)){
            tempAssetInfoList = assetInfoDao.getByFinancingNumAndSiteUID(financingNum, siteUID);
        }

        return tempAssetInfoList;
    }

    private List<AssetInfo> getAssetInfoBySerialNum(String serialNum, String siteUID){
        List<AssetInfo> tempAssetInfoList = null;

        if(!StringHelper.isNullOrEmptyString(serialNum)){
            tempAssetInfoList = assetInfoDao.getBySerialNumAndSiteUID(serialNum, siteUID);
        }

        return tempAssetInfoList;
    }

    public List<MeasuringCertificateDetail> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        List<MeasuringCertificateDetail> tempMCDList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputstream());
        //for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return null;
            }
            Map<String, Integer> header = findHeader(sheet);
            if(header.isEmpty()) {
                return null;
            }
            for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
                String errorInfo = "";

                HSSFRow row = sheet.getRow(j);

                MeasuringCertificateDetail detail = new MeasuringCertificateDetail();
                detail.setTenantUID(user.getTenantUID());
                detail.setInstitutionUID(user.getInstitutionUID());
                detail.setHospitalUID(user.getHospitalUID());
                detail.setSiteUID(user.getSiteUID());
                detail.setAssetManufacturer(row.getCell(header.get("MANUFACTURE_CELL")) == null ? null : row.getCell(header.get("MANUFACTURE_CELL")).getStringCellValue());

                if(row.getCell(header.get("MODEL_CELL")) != null) {
                    detail.setAssetModel(CellType.NUMERIC.equals(row.getCell(header.get("MODEL_CELL")).getCellTypeEnum()) ? String.valueOf(Double.valueOf(row.getCell(header.get("MODEL_CELL")).getNumericCellValue()).intValue()) : row.getCell(header.get("MODEL_CELL")).getStringCellValue());
                }

                detail.setAssetName(row.getCell(header.get("APPLIANCE_NAME_CELL")) == null ? null : row.getCell(header.get("APPLIANCE_NAME_CELL")).getStringCellValue());

                if(row.getCell(header.get("APPLIANCE_NO_CELL")) != null) {
                    detail.setAssetNo(CellType.NUMERIC.equals(row.getCell(header.get("APPLIANCE_NO_CELL")).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(header.get("APPLIANCE_NO_CELL")).getNumericCellValue()) : row.getCell(header.get("APPLIANCE_NO_CELL")).getStringCellValue());
                }

                if(row.getCell(header.get("CERTIFICATION_NO_CELL")) != null) {
                    detail.setCertificateNo(CellType.NUMERIC.equals(row.getCell(header.get("CERTIFICATION_NO_CELL")).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(header.get("CERTIFICATION_NO_CELL")).getNumericCellValue()) : row.getCell(header.get("CERTIFICATION_NO_CELL")).getStringCellValue());
                }

                if(row.getCell(header.get("VERIFICATION_DATE_CELL")) != null) {
                    try {
                        detail.setCheckDate(CellType.NUMERIC.equals(row.getCell(header.get("VERIFICATION_DATE_CELL")).getCellTypeEnum()) ? row.getCell(header.get("VERIFICATION_DATE_CELL")).getDateCellValue() : sdf.parse(row.getCell(header.get("VERIFICATION_DATE_CELL")).getStringCellValue()));
                    }catch (Exception ex){
                        errorInfo += "[检定日期格式不对]";
                    }

                    detail.setCheckDateDisplay(CellType.NUMERIC.equals(row.getCell(header.get("VERIFICATION_DATE_CELL")).getCellTypeEnum()) ? sdf.format(row.getCell(header.get("VERIFICATION_DATE_CELL")).getDateCellValue()) : row.getCell(header.get("VERIFICATION_DATE_CELL")).getStringCellValue());
                }
                if(row.getCell(header.get("EXPIRE_DATE_CELL")) != null) {
                    try {
                        detail.setValidDateTo(CellType.NUMERIC.equals(row.getCell(header.get("EXPIRE_DATE_CELL")).getCellTypeEnum()) ? row.getCell(header.get("EXPIRE_DATE_CELL")).getDateCellValue() : sdf.parse(row.getCell(header.get("EXPIRE_DATE_CELL")).getStringCellValue()));
                    }catch (Exception ex){
                        errorInfo += "[有效期至格式不对]";
                    }

                    detail.setValidDateToDisplay(CellType.NUMERIC.equals(row.getCell(header.get("EXPIRE_DATE_CELL")).getCellTypeEnum()) ? sdf.format(row.getCell(header.get("EXPIRE_DATE_CELL")).getDateCellValue()) : row.getCell(header.get("EXPIRE_DATE_CELL")).getStringCellValue());
                }
                //detail.setValidDateTo(row.getCell(header.get("EXPIRE_DATE_CELL")) == null ? null : row.getCell(header.get("EXPIRE_DATE_CELL")).getDateCellValue());
                detail.setCheckCompany(row.getCell(header.get("VERIFICATION_UNIT_CELL")) == null ? null : row.getCell(header.get("VERIFICATION_UNIT_CELL")).getStringCellValue());
                detail.setCheckPerson(row.getCell(header.get("VERIFIER_CELL")) == null ? null : row.getCell(header.get("VERIFIER_CELL")).getStringCellValue());
                detail.setComments(row.getCell(header.get("REMARK_CELL")) == null ? null : row.getCell(header.get("REMARK_CELL")).getStringCellValue());
                detail.setExpired(row.getCell(header.get("OVERDUE_CELL")) == null ? null : "待检".equals(row.getCell(header.get("OVERDUE_CELL")).getStringCellValue()) ? false : true);
                detail.setDepartment(row.getCell(header.get("CLINICAL_DEPT_CELL")) == null ? null : row.getCell(header.get("CLINICAL_DEPT_CELL")).getStringCellValue());
                if(row.getCell(header.get("ASSET_SN_CELL")) != null) {
                    detail.setAssetSN(CellType.NUMERIC.equals(row.getCell(header.get("ASSET_SN_CELL")).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(header.get("ASSET_SN_CELL")).getNumericCellValue()) : row.getCell(header.get("ASSET_SN_CELL")).getStringCellValue());
                }
                if(row.getCell(header.get("ASSET_NO_CELL")) != null) {
                    detail.setFinanceNo(CellType.NUMERIC.equals(row.getCell(header.get("ASSET_NO_CELL")).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(header.get("ASSET_NO_CELL")).getNumericCellValue()) : row.getCell(header.get("ASSET_NO_CELL")).getStringCellValue());
                }
                detail.setCheckItem(row.getCell(header.get("ITEM_CELL")) == null ? null : row.getCell(header.get("ITEM_CELL")).getStringCellValue());
                detail.setType(row.getCell(header.get("CATEGORY_CELL")) == null ? null : row.getCell(header.get("CATEGORY_CELL")).getStringCellValue());
                //detail.setPay(row.getCell(header.get("PAYED_CELL")) == null ? null : row.getCell(header.get("PAYED_CELL")).getBooleanCellValue());
                if(row.getCell(header.get("FILE_NO_CELL")) != null) {
                    detail.setFileNo(CellType.NUMERIC.equals(row.getCell(header.get("FILE_NO_CELL")).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(header.get("FILE_NO_CELL")).getNumericCellValue()) : row.getCell(header.get("FILE_NO_CELL")).getStringCellValue());
                }
                detail.setStatusDisplay(row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")) == null ? null : row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")).getStringCellValue());
                Integer status = 3;
                if (row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")) != null) {
                    switch (row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")).getStringCellValue()) {
                        case "A级合格":
                            status = 1;
                            break;
                        case "合格":
                            status = 1;
                            break;
                        case "不合格":
                            status = 2;
                            break;
                        case "无结果":
                            status = 3;
                            break;
                        case "校准合格":
                            status = 1;
                            break;
                    }
                }
                detail.setStatus(status);
                detail.setExcelIdex(j + 1);

                //检定日期不能为空，设备序列号和资产编号不能全为空
                if (row == null || !errorInfo.equals("") || row.getCell(header.get("VERIFICATION_DATE_CELL")) == null || (row.getCell(header.get("ASSET_SN_CELL")) == null && row.getCell(header.get("ASSET_NO_CELL")) == null)) {
                    if(row.getCell(header.get("ASSET_SN_CELL")) == null && row.getCell(header.get("ASSET_NO_CELL")) == null){
                        errorInfo += "[(设备序列号SN)和(资产编号)不能同时为空]";
                    }
                    if(row.getCell(header.get("VERIFICATION_DATE_CELL")) == null){
                        errorInfo += "[检定日期不能为空]";
                    }
                    detail.setErrorInfo(errorInfo);
                    tempMCDList.add(detail);
                    continue;
                }

                if(operateMeasuringAndAssetInfo(detail) != null){
                    tempMCDList.add(detail);
                }
            }
        //}
        return tempMCDList;
    }

    private HSSFSheet handleSheet(HSSFSheet sheet) {
        return sheet;
    }

    private Map<String, Integer> findHeader(HSSFSheet sheet) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            boolean hasCertificationNo = false;
            boolean hasApplianceName = false;
            boolean hasManufacture = false;
            boolean hasModel = false;
            boolean hasAssetSN = false;
            boolean hasAssetNo = false;
            boolean hasApplianceNo = false;
            boolean hasPayed = false;
            boolean hasVerificationConclusion = false;
            boolean hasVerificationDate = false;
            boolean hasExpireDate = false;
            boolean hasVerificationUnit = false;
            boolean hasVerifier = false;
            boolean hasItem = false;
            boolean hasCategory = false;
            boolean hasClinicalDept = false;
            boolean hasRemark = false;
            boolean hasOverdue = false;
            boolean hasFileNo = false;

            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    hasCertificationNo = HEADER_CERTIFICATION_NO.equals(cell.getStringCellValue()) ? true : hasCertificationNo;
                    hasApplianceName = HEADER_APPLIANCE_NAME.equals(cell.getStringCellValue()) ? true : hasApplianceName;
                    hasManufacture = HEADER_MANUFACTURE.equals(cell.getStringCellValue()) ? true : hasManufacture;
                    hasModel = HEADER_MODEL.equals(cell.getStringCellValue()) ? true : hasModel;
                    hasAssetSN = HEADER_ASSET_SN.equals(cell.getStringCellValue()) ? true : hasAssetSN;
                    hasAssetNo = HEADER_ASSET_NO.equals(cell.getStringCellValue()) ? true : hasAssetNo;
                    hasApplianceNo = HEADER_APPLIANCE_NO.equals(cell.getStringCellValue()) ? true : hasApplianceNo;
                    hasPayed = HEADER_PAYED.equals(cell.getStringCellValue()) ? true : hasPayed;
                    hasVerificationConclusion = HEADER_VERIFICATION_CONCLUSION.equals(cell.getStringCellValue()) ? true : hasVerificationConclusion;
                    hasVerificationDate = HEADER_VERIFICATION_DATE.equals(cell.getStringCellValue()) ? true : hasVerificationDate;
                    hasExpireDate = HEADER_EXPIRE_DATE.equals(cell.getStringCellValue()) ? true : hasExpireDate;
                    hasVerificationUnit = HEADER_VERIFICATION_UNIT.equals(cell.getStringCellValue()) ? true : hasVerificationUnit;
                    hasVerifier = HEADER_VERIFIER.equals(cell.getStringCellValue()) ? true : hasVerifier;
                    hasItem = HEADER_ITEM.equals(cell.getStringCellValue()) ? true : hasItem;
                    hasCategory = HEADER_CATEGORY.equals(cell.getStringCellValue()) ? true : hasCategory;
                    hasClinicalDept = HEADER_CLINICAL_DEPT.equals(cell.getStringCellValue()) ? true : hasClinicalDept;
                    hasRemark = HEADER_REMARK.equals(cell.getStringCellValue()) ? true : hasRemark;
                    hasOverdue = HEADER_OVERDUE.equals(cell.getStringCellValue()) ? true : hasOverdue;
                    hasFileNo = HEADER_FILE_NO.equals(cell.getStringCellValue()) ? true : hasFileNo;
                    switch (cell.getStringCellValue()) {
                        case HEADER_CERTIFICATION_NO:
                            hasCertificationNo = true;
                            header.put("CERTIFICATION_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NAME:
                            hasApplianceName = true;
                            header.put("APPLIANCE_NAME_CELL", j);
                            break;
                        case HEADER_MANUFACTURE:
                            hasManufacture = true;
                            header.put("MANUFACTURE_CELL", j);
                            break;
                        case HEADER_MODEL:
                            hasModel = true;
                            header.put("MODEL_CELL", j);
                            break;
                        case HEADER_ASSET_SN:
                            hasAssetSN = true;
                            header.put("ASSET_SN_CELL", j);
                            break;
                        case HEADER_ASSET_NO:
                            hasAssetNo = true;
                            header.put("ASSET_NO_CELL", j);
                            break;
                        case HEADER_APPLIANCE_NO:
                            hasApplianceNo = true;
                            header.put("APPLIANCE_NO_CELL", j);
                            break;
                        case HEADER_PAYED:
                            hasPayed = true;
                            header.put("PAYED_CELL", j);
                            break;
                        case HEADER_VERIFICATION_CONCLUSION:
                            hasVerificationConclusion = true;
                            header.put("VERIFICATION_CONCLUSION_CELL", j);
                            break;
                        case HEADER_VERIFICATION_DATE:
                            hasVerificationDate = true;
                            header.put("VERIFICATION_DATE_CELL", j);
                            break;
                        case HEADER_EXPIRE_DATE:
                            hasExpireDate = true;
                            header.put("EXPIRE_DATE_CELL", j);
                            break;
                        case HEADER_VERIFICATION_UNIT:
                            hasVerificationUnit = true;
                            header.put("VERIFICATION_UNIT_CELL", j);
                            break;
                        case HEADER_VERIFIER:
                            hasVerifier = true;
                            header.put("VERIFIER_CELL", j);
                            break;
                        case HEADER_ITEM:
                            hasItem = true;
                            header.put("ITEM_CELL", j);
                            break;
                        case HEADER_CATEGORY:
                            hasCategory = true;
                            header.put("CATEGORY_CELL", j);
                            break;
                        case HEADER_CLINICAL_DEPT:
                            hasClinicalDept = true;
                            header.put("CLINICAL_DEPT_CELL", j);
                            break;
                        case HEADER_REMARK:
                            hasRemark = true;
                            header.put("REMARK_CELL", j);
                            break;
                        case HEADER_OVERDUE:
                            hasOverdue = true;
                            header.put("OVERDUE_CELL", j);
                            break;
                        case HEADER_FILE_NO:
                            hasFileNo = true;
                            header.put("FILE_NO_CELL", j);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                    continue;
                }
            }
            if (hasCertificationNo && hasApplianceName && hasManufacture && hasModel && hasAssetSN && hasAssetNo
                    && hasApplianceNo && hasPayed && hasVerificationConclusion && hasVerificationDate && hasExpireDate
                    && hasVerificationUnit && hasVerifier && hasItem && hasCategory && hasClinicalDept && hasRemark && hasOverdue && hasFileNo) {
                header.put("HEADER_ROW", i);
                break;
            }
        }


        return header;
    }

}
