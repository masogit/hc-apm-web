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
import org.hibernate.validator.internal.util.StringHelper;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 212605082 on 2017/11/7.
 */
public class MeasuringService {
    private final String HEADER_CERTIFICATION_NO = "证书编号";
    private final String HEADER_APPLIANCE_NAME = "器具名称";
    private final String HEADER_MANUFACTURE = "制造厂";
    private final String HEADER_MODEL = "型号";
    private final String HEADER_ASSET_NO = "财产编号";
    private final String HEADER_APPLIANCE_NO = "器具编号";
    private final String HEADER_PAYED = "付款否";
    private final String HEADER_VERIFICATION_CONCLUSION = "检定结论";
    private final String HEADER_VERIFICATION_DATE = "检定日期";
    private final String HEADER_EXPIRE_DATE = "有效期至";
    private final String HEADER_VERIFICATION_UNIT = "检测单位";
    private final String HEADER_VERIFIER = "检定人员";
    private final String HEADER_ITEM = "项别";
    private final String HEADER_CATEGORY = "种别";
    private final String HEADER_CLINICAL_DEPT = "所在科室";
    private final String HEADER_REMARK = "备注";
    private final String HEADER_OVERDUE = "过期否";


    @Autowired
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasuringService.class);

    public void operateMeasuringAndAssetInfo(MeasuringCertificateDetail measuringCertificateDetail) {
        if (!StringHelper.isNullOrEmptyString(measuringCertificateDetail.getCertificateNo())) {
            MeasuringCertificateDetail tempData = measuringCertificateDetailDao.findByCertificateNoAndTenantUID(measuringCertificateDetail.getCertificateNo(), measuringCertificateDetail.getTenantUID());

            if (tempData != null) {
                measuringCertificateDetail.setId(tempData.getId());
            }

            measuringCertificateDetailDao.save(measuringCertificateDetail);

            AssetInfo tempAssetInfo = assetInfoDao.findByFinancingNumAndTenantUID(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getTenantUID());

            tempAssetInfo.setNextMeteringDate(measuringCertificateDetail.getValidDateTo());
            tempAssetInfo.setLastMeteringDate(measuringCertificateDetail.getCheckDate());
            tempAssetInfo.setAccountingCategory(measuringCertificateDetail.getStatus());
            if (measuringCertificateDetail.getStatus() == 2) {//计量不合格
                tempAssetInfo.setUsageStatus(2);//停用
            }
        }
    }

    public void handleExcel(UploadedFile file, UserAccount user) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputstream());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            Map<String, Integer> header = findHeader(sheet);
            for (int j = header.get("HEADER_ROW") + 1; j < sheet.getLastRowNum(); j++) {
                HSSFRow row = sheet.getRow(j);
                MeasuringCertificateDetail detail = new MeasuringCertificateDetail();
                detail.setTenantUID(user.getTenantUID());
                detail.setInstitutionUID(user.getInstitutionUID());
                detail.setHospitalUID(user.getHospitalUID());
                detail.setSiteUID(user.getSiteUID());
                detail.setAssetManufacturer(row.getCell(header.get("MANUFACTURE_CELL")).getStringCellValue());
                detail.setAssetModel(row.getCell(header.get("MODEL_CELL")).getStringCellValue());
                detail.setAssetName(row.getCell(header.get("APPLIANCE_NAME_CELL")).getStringCellValue());
                detail.setAssetNo(row.getCell(header.get("APPLIANCE_NO_CELL")).getStringCellValue());
                detail.setCertificateNo(row.getCell(header.get("CERTIFICATION_NO_CELL")).getStringCellValue());
                detail.setCheckDate(row.getCell(header.get("VERIFICATION_DATE_CELL")).getDateCellValue());
                detail.setCheckCompany(row.getCell(header.get("VERIFICATION_UNIT_CELL")).getStringCellValue());
                detail.setCheckPerson(row.getCell(header.get("VERIFIER_CELL")).getStringCellValue());
                detail.setComments(row.getCell(header.get("REMARK_CELL")).getStringCellValue());
                detail.setExpired(row.getCell(header.get("OVERDUE_CELL")).getBooleanCellValue());
                detail.setDepartment(row.getCell(header.get("CLINICAL_DEPT_CELL")).getStringCellValue());
                detail.setFinanceNo(row.getCell(header.get("ASSET_NO_CELL")).getStringCellValue());
                detail.setCheckItem(row.getCell(header.get("ITEM_CELL")).getStringCellValue());
                detail.setPay(row.getCell(header.get("PAYED_CELL")).getBooleanCellValue());
                Integer status = 1;
                switch (row.getCell(header.get("VERIFICATION_CONCLUSION_CELL")).getStringCellValue()) {
                    case "A级合格":
                        status = 1;
                        break;
                    case "合格":
                        status = 1;
                        break;
                    case "校准不合格":
                        status = 2;
                        break;
                    case "没有校准结果":
                        status = 3;
                        break;
                    case "校准合格":
                        status = 1;
                        break;
                }
                detail.setStatus(status);
                operateMeasuringAndAssetInfo(detail);
            }
        }
    }

    private HSSFSheet handleSheet(HSSFSheet sheet) {
        return sheet;
    }

    private Map<String, Integer> findHeader(HSSFSheet sheet) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            boolean hasCertificationNo = false;
            boolean hasApplianceName = false;
            boolean hasManufacture = false;
            boolean hasModel = false;
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

            HSSFRow row = sheet.getRow(i);
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                hasCertificationNo = HEADER_CERTIFICATION_NO.equals(cell.getStringCellValue());
                hasApplianceName = HEADER_APPLIANCE_NAME.equals(cell.getStringCellValue());
                hasManufacture = HEADER_MANUFACTURE.equals(cell.getStringCellValue());
                hasModel = HEADER_MODEL.equals(cell.getStringCellValue());
                hasAssetNo = HEADER_ASSET_NO.equals(cell.getStringCellValue());
                hasApplianceNo = HEADER_APPLIANCE_NO.equals(cell.getStringCellValue());
                hasPayed = HEADER_PAYED.equals(cell.getStringCellValue());
                hasVerificationConclusion = HEADER_VERIFICATION_CONCLUSION.equals(cell.getStringCellValue());
                hasVerificationDate = HEADER_VERIFICATION_DATE.equals(cell.getStringCellValue());
                hasExpireDate = HEADER_EXPIRE_DATE.equals(cell.getStringCellValue());
                hasVerificationUnit = HEADER_VERIFICATION_UNIT.equals(cell.getStringCellValue());
                hasVerifier = HEADER_VERIFIER.equals(cell.getStringCellValue());
                hasItem = HEADER_ITEM.equals(cell.getStringCellValue());
                hasCategory = HEADER_CATEGORY.equals(cell.getStringCellValue());
                hasClinicalDept = HEADER_CLINICAL_DEPT.equals(cell.getStringCellValue());
                hasRemark = HEADER_REMARK.equals(cell.getStringCellValue());
                hasOverdue = HEADER_OVERDUE.equals(cell.getStringCellValue());


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
                        header.put("EXPIRE_DATE_CELL", j);
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
                    default:
                        break;
                }
            }
            if (hasCertificationNo && hasApplianceName && hasManufacture && hasModel && hasAssetNo
                    && hasApplianceNo && hasPayed && hasVerificationConclusion && hasVerificationDate && hasExpireDate
                    && hasVerificationUnit && hasVerifier && hasItem && hasCategory && hasClinicalDept && hasRemark && hasOverdue) {
                header.put("HEADER_ROW", i);
                break;
            }
        }


        return header;
    }

}
