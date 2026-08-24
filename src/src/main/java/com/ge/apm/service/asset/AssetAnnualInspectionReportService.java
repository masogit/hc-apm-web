package com.ge.apm.service.asset;


import com.ge.apm.dao.AssetAnnualInspectionReportRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.SpecialAssetInfoRepository;
import com.ge.apm.dao.mapper.AssetAnnualInspectionReportMapper;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class AssetAnnualInspectionReportService {

    @Autowired
    private AssetAnnualInspectionReportRepository repository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private SpecialAssetInfoRepository specialAssetInfoRepository;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private BlobObjectRepository blobDao;

    @Autowired
    private AssetAnnualInspectionReportMapper assetAnnualInspectionReportMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetAnnualInspectionReportService.class);

    private final String HEADER_ASSET_NAME = "设备名称";
    private final String HEADER_ASSET_SN = "设备序列号SN";
    private final String HEADER_QR_CODE = "二维码";
    private final String HEADER_CLINICAL_DEPT = "所属科室";
    private final String HEADER_NAME = "报告名称";
    private final String HEADER_REPORT_TYPE = "报告类型";
    private final String HEADER_TESTING_UNIT = "检测单位";
    private final String HEADER_TEST_RESULTS = "检测结果";
    private final String HEADER_CERTIFICATE_ISSUING_UNIT = "报告发证单位";
    private final String HEADER_EFFECTIVE_TIME = "报告生效时间";
    private final String HEADER_EXPIRY_DATE = "报告到期时间";
    private final String HEADER_CREATED_BY_NAME = "上传人";

    public static final String IMPORT_STATUS_NEW = "New";
    public static final String IMPORT_STATUS_CHECK_PASS = "CheckPass";
    public static final String IMPORT_STATUS_CHECK_FAIL = "CheckFail";
    public static final String IMPORT_STATUS_SUCCESS = "Success";
    public static final String IMPORT_STATUS_FAILURE = "Failure";

    public void saveAssetReport(List<AssetAnnualInspectionReport> assetAnnualInspectionReports, String id) {
        if(id!=null){
            repository.deleteBySpecialAssetUid(id);
        }
        assetAnnualInspectionReports.stream().forEach(item->{
            item.setSpecialAssetUid(id);
            item.setUid(null);
        });
        repository.save(assetAnnualInspectionReports);

    }


    /**
     * 将校验通过的数据实际保存到数据库
     */
    public List<AssetAnnualInspectionReport> importValidatedData(List<AssetAnnualInspectionReport> validatedList, String currentUserUid) {
        List<AssetAnnualInspectionReport> failedList = new ArrayList<>();
        for (AssetAnnualInspectionReport report : validatedList) {
            if (!IMPORT_STATUS_CHECK_PASS.equals(report.getImportStatus())) {
                report.setImportStatus(IMPORT_STATUS_FAILURE);
                failedList.add(report);
                continue;
            }
            try {
                report.setCreatedByUid(currentUserUid);
                report.setUid(null);
                repository.save(report);
                report.setImportStatus(IMPORT_STATUS_SUCCESS);
            } catch (Exception e) {
                report.setErrorInfo("[导入异常: " + e.getMessage() + "]");
                report.setImportStatus(IMPORT_STATUS_FAILURE);
                failedList.add(report);
            }
        }
        return failedList;
    }

    public List<AssetAnnualInspectionReport> handleZip(UploadedFile file, UserAccount user) throws IOException {
        // 查询当前租户下所有的年检报告
        List<AssetAnnualInspectionReport> reports = assetAnnualInspectionReportMapper.selectAnnualInspectionReportListByTenantUid(user.getTenantUID());
        Map<String, AssetAnnualInspectionReport> reportMap = new HashMap<>();
        for (AssetAnnualInspectionReport report : reports) {
            if (StringUtils.hasLength(report.getSpecialAssetUid())) {
                reportMap.put(report.getAssetSerialNum()+ "+"+report.getName(), report);
                reportMap.put(report.getAssetSerialNum()+ "_"+report.getName(), report);
            }
        }

        List<AssetAnnualInspectionReport> resultList = new ArrayList<>();
        // 获取压缩文件中的每个子文件
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputstream(), Charset.forName("GBK"))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                // 假设文件名（去掉扩展名）对应 specialAssetUid，实际需根据业务调整
                String specialAssetUid = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

                AssetAnnualInspectionReport resultDetail = new AssetAnnualInspectionReport();
                AssetAnnualInspectionReport report = reportMap.get(specialAssetUid);
                String errInfo = "";

                if (ObjectUtil.isEmpty(report)) {
                    errInfo = "未匹配到年检结果";
                } else {
                    V2_BlobObject blobObject = blobFileService.uploadFile(zipInputStream, entry.getName());
                    blobObject.setBoType(V2_BlobObject.BO_TYPE_SpecialAssetReportInfo);
                    blobObject.setBoUuid(report.getUid());
//                    List<V2_BlobObject> hasList = blobDao.findByBoUuidAndObjectName(report.getUid(), blobObject.getObjectName());
//                    if (ObjectUtil.isNotEmpty(hasList)) {
//                        blobDao.delete(hasList);
//                    }
                    blobDao.save(blobObject);
                }

                resultDetail.setSpecialAssetUid(specialAssetUid);
                resultDetail.setErrorInfo(errInfo);
                resultList.add(resultDetail);
            }
        } catch (Exception e) {
            LOGGER.error("handle zip error:{}", e.getMessage());
            throw new RuntimeException("handleZip error", e);
        }
        return resultList;
    }

    public List<AssetAnnualInspectionReport> validateExcel(UploadedFile file, OrgInfo selectedOrg) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);
        List<AssetAnnualInspectionReport> resultList = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputstream());
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return resultList;
        }
        Map<String, Integer> header = findHeader(sheet);
        if (header.isEmpty()) {
            return resultList;
        }
        for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
            StringBuilder errorInfo = new StringBuilder();
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            // 跳过全空行
            boolean allBlank = true;
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                    String val = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : null;
                    if (val != null && !val.trim().isEmpty()) {
                        allBlank = false;
                        break;
                    }
                }
            }
            if (allBlank) continue;

            AssetAnnualInspectionReport report = new AssetAnnualInspectionReport();
            report.setTenantUID(selectedOrg.getTenantUID());
            report.setInstitutionUID(selectedOrg.getInstitutionUID());
            report.setHospitalUID(selectedOrg.getHospitalUID());
            report.setSiteUID(selectedOrg.getSiteUID());

            if (row.getCell(header.get("ASSET_NAME_CELL")) != null) {
                report.setAssetName(getCellStringValue(row.getCell(header.get("ASSET_NAME_CELL"))));
            }
            if (row.getCell(header.get("QR_CODE_CELL")) != null) {
                report.setAssetQrCode(getCellStringValue(row.getCell(header.get("QR_CODE_CELL"))));
            }
            if (row.getCell(header.get("CLINICAL_DEPT_CELL")) != null) {
                report.setAssetClinicalDeptName(getCellStringValue(row.getCell(header.get("CLINICAL_DEPT_CELL"))));
            }
            if (row.getCell(header.get("NAME_CELL")) != null) {
                report.setName(getCellStringValue(row.getCell(header.get("NAME_CELL"))));
            }
            if (row.getCell(header.get("REPORT_TYPE_CELL")) != null) {
                report.setReportType(getCellStringValue(row.getCell(header.get("REPORT_TYPE_CELL"))));
            }
            if (row.getCell(header.get("TESTING_UNIT_CELL")) != null) {
                report.setTestingUnit(getCellStringValue(row.getCell(header.get("TESTING_UNIT_CELL"))));
            }
            if (row.getCell(header.get("TEST_RESULTS_CELL")) != null) {
                report.setTestResults(getCellStringValue(row.getCell(header.get("TEST_RESULTS_CELL"))));
            }
            if (row.getCell(header.get("CERTIFICATE_ISSUING_UNIT_CELL")) != null) {
                report.setCertificateIssuingUnit(getCellStringValue(row.getCell(header.get("CERTIFICATE_ISSUING_UNIT_CELL"))));
            }
            if (row.getCell(header.get("CREATED_BY_NAME_CELL")) != null) {
                report.setCreatedByName(getCellStringValue(row.getCell(header.get("CREATED_BY_NAME_CELL"))));
            }
            if (row.getCell(header.get("EFFECTIVE_TIME_CELL")) != null) {
                try {
                    report.setEffectiveTime(CellType.NUMERIC.equals(row.getCell(header.get("EFFECTIVE_TIME_CELL")).getCellTypeEnum()) ? row.getCell(header.get("EFFECTIVE_TIME_CELL")).getDateCellValue() : sdf.parse(row.getCell(header.get("EFFECTIVE_TIME_CELL")).getStringCellValue()));
                } catch (Exception ex) {
                    errorInfo.append("[报告生效时间格式不对]");
                }
            }
            if (row.getCell(header.get("EXPIRY_DATE_CELL")) != null) {
                try {
                    report.setExpiryDate(CellType.NUMERIC.equals(row.getCell(header.get("EXPIRY_DATE_CELL")).getCellTypeEnum()) ? row.getCell(header.get("EXPIRY_DATE_CELL")).getDateCellValue() : sdf.parse(row.getCell(header.get("EXPIRY_DATE_CELL")).getStringCellValue()));
                } catch (Exception ex) {
                    errorInfo.append("[报告到期时间格式不对]");
                }
            }

            // 设备序列号必填校验
            if (row.getCell(header.get("ASSET_SN_CELL")) == null) {
                errorInfo.append("[设备序列号SN不能为空]");
            } else {
                String assetSerialNum = getCellStringValue(row.getCell(header.get("ASSET_SN_CELL")));
                report.setAssetSerialNum(assetSerialNum);
                if (StringUtils.isEmpty(assetSerialNum)) {
                    errorInfo.append("[设备序列号SN不能为空]");
                } else {
                    // 与台账序列号进行匹配
                    List<AssetInfo> assetInfoList = assetInfoRepository.getBySerialNumAndSiteUID_ForDbTable(assetSerialNum, selectedOrg.getSiteUID());
                    if (assetInfoList == null || assetInfoList.isEmpty()) {
                        errorInfo.append("[未匹配到特种设备]");
                    } else {
                        AssetInfo assetInfo = assetInfoList.get(0);
                        // 检查是否为特种设备
                        report.setAssetQrCode(assetInfo.getQrCode());
                        report.setAssetClinicalDeptName(assetInfo.getClinicalDeptName());
                        SpecialAssetInfo specialAssetInfo = specialAssetInfoRepository.findByAssetId(assetInfo.getId());
                        if (specialAssetInfo == null) {
                            errorInfo.append("[未匹配到特种设备]");
                        } else {
                            report.setSpecialAssetUid(specialAssetInfo.getId());
                        }
                    }
                }
            }

            // 报告生效时间 > 报告到期时间校验
            if (report.getEffectiveTime() != null && report.getExpiryDate() != null && report.getEffectiveTime().after(report.getExpiryDate())) {
                errorInfo.append("[报告生效时间大于报告到期时间]");
            }

            if (report.getEffectiveTime() != null && report.getEffectiveTime().after(sdf.parse("99999/12/31"))) {
                errorInfo.append("[报告生效时间不能超过99999/12/31]");
            }
            if (report.getExpiryDate() != null && report.getExpiryDate().after(sdf.parse("99999/12/31"))) {
                errorInfo.append("[报告到期时间不能超过99999/12/31]");
            }

            report.setExcelIdex(j + 1);
            if (errorInfo.length() > 0) {
                report.setErrorInfo(errorInfo.toString());
                report.setImportStatus(IMPORT_STATUS_CHECK_FAIL);
            } else {
                report.setImportStatus(IMPORT_STATUS_CHECK_PASS);
            }
            resultList.add(report);
        }
        return resultList;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
            return new DecimalFormat("##############.#####").format(cell.getNumericCellValue());
        } else if (CellType.STRING.equals(cell.getCellTypeEnum())) {
            return cell.getStringCellValue();
        }
        return null;
    }

    private Map<String, Integer> findHeader(Sheet sheet) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            boolean hasAssetName = false;
            boolean hasAssetSN = false;
            boolean hasQrCode = false;
            boolean hasClinicalDept = false;
            boolean hasName = false;
            boolean hasReportType = false;
            boolean hasTestingUnit = false;
            boolean hasTestResults = false;
            boolean hasCertificateIssuingUnit = false;
            boolean hasEffectiveTime = false;
            boolean hasExpiryDate = false;
            boolean hasCreatedByName = false;

            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    hasAssetName = HEADER_ASSET_NAME.equals(cell.getStringCellValue()) ? true : hasAssetName;
                    hasAssetSN = HEADER_ASSET_SN.equals(cell.getStringCellValue()) ? true : hasAssetSN;
                    hasQrCode = HEADER_QR_CODE.equals(cell.getStringCellValue()) ? true : hasQrCode;
                    hasClinicalDept = HEADER_CLINICAL_DEPT.equals(cell.getStringCellValue()) ? true : hasClinicalDept;
                    hasName = HEADER_NAME.equals(cell.getStringCellValue()) ? true : hasName;
                    hasReportType = HEADER_REPORT_TYPE.equals(cell.getStringCellValue()) ? true : hasReportType;
                    hasTestingUnit = HEADER_TESTING_UNIT.equals(cell.getStringCellValue()) ? true : hasTestingUnit;
                    hasTestResults = HEADER_TEST_RESULTS.equals(cell.getStringCellValue()) ? true : hasTestResults;
                    hasCertificateIssuingUnit = HEADER_CERTIFICATE_ISSUING_UNIT.equals(cell.getStringCellValue()) ? true : hasCertificateIssuingUnit;
                    hasEffectiveTime = HEADER_EFFECTIVE_TIME.equals(cell.getStringCellValue()) ? true : hasEffectiveTime;
                    hasExpiryDate = HEADER_EXPIRY_DATE.equals(cell.getStringCellValue()) ? true : hasExpiryDate;
                    hasCreatedByName = HEADER_CREATED_BY_NAME.equals(cell.getStringCellValue()) ? true : hasCreatedByName;
                    switch (cell.getStringCellValue()) {
                        case HEADER_ASSET_NAME:
                            hasAssetName = true;
                            header.put("ASSET_NAME_CELL", j);
                            break;
                        case HEADER_ASSET_SN:
                            hasAssetSN = true;
                            header.put("ASSET_SN_CELL", j);
                            break;
                        case HEADER_QR_CODE:
                            hasQrCode = true;
                            header.put("QR_CODE_CELL", j);
                            break;
                        case HEADER_CLINICAL_DEPT:
                            hasClinicalDept = true;
                            header.put("CLINICAL_DEPT_CELL", j);
                            break;
                        case HEADER_NAME:
                            hasName = true;
                            header.put("NAME_CELL", j);
                            break;
                        case HEADER_REPORT_TYPE:
                            hasReportType = true;
                            header.put("REPORT_TYPE_CELL", j);
                            break;
                        case HEADER_TESTING_UNIT:
                            hasTestingUnit = true;
                            header.put("TESTING_UNIT_CELL", j);
                            break;
                        case HEADER_TEST_RESULTS:
                            hasTestResults = true;
                            header.put("TEST_RESULTS_CELL", j);
                            break;
                        case HEADER_CERTIFICATE_ISSUING_UNIT:
                            hasCertificateIssuingUnit = true;
                            header.put("CERTIFICATE_ISSUING_UNIT_CELL", j);
                            break;
                        case HEADER_EFFECTIVE_TIME:
                            hasEffectiveTime = true;
                            header.put("EFFECTIVE_TIME_CELL", j);
                            break;
                        case HEADER_EXPIRY_DATE:
                            hasExpiryDate = true;
                            header.put("EXPIRY_DATE_CELL", j);
                            break;
                        case HEADER_CREATED_BY_NAME:
                            hasCreatedByName = true;
                            header.put("CREATED_BY_NAME_CELL", j);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            if (hasAssetName && hasAssetSN &&  hasName && hasReportType
                    && hasTestingUnit && hasTestResults && hasCertificateIssuingUnit && hasEffectiveTime
                    && hasExpiryDate && hasCreatedByName) {
                header.put("HEADER_ROW", i);
                break;
            }
        }
        return header;
    }
}
