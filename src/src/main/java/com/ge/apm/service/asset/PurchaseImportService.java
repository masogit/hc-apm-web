package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetPurchaseListRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetPurchaseList;
import com.ge.apm.domain.I18nMessage;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webapp.framework.web.WebUtil;

/**
 * pc
 */
@Component
public class PurchaseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseImportService.class);

    private final String HEADER_ASSETTYPE = "设备类别";
    private final String HEADER_CLINICALDEPTNAME = "使用科室";
    private final String HEADER_PURCHASEQTY = "采购数量";
    private final String HEADER_CREATEDATE = "创建日期";
    I18nMessageRepository importdao = null;
    AssetPurchaseListRepository assetPurchaseListDao = null;
//    @Autowired
//    private AssetInfoRepository assetInfoDao;
//
//    public boolean operateMeasuringAndAssetInfo(MeasuringCertificateDetail measuringCertificateDetail) {
//        boolean flag = false;
//        if (!StringHelper.isNullOrEmptyString(measuringCertificateDetail.getCertificateNo())) {
//            MeasuringCertificateDetail tempData = measuringCertificateDetailDao.findByFinanceNoAndCheckDateAndTenantUID(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getCheckDate(), measuringCertificateDetail.getTenantUID());
//
//            if (tempData != null) {
//                measuringCertificateDetail.setId(tempData.getId());
//            }
//
//            measuringCertificateDetailDao.save(measuringCertificateDetail);
//        }
//
//        if (!StringHelper.isNullOrEmptyString(measuringCertificateDetail.getFinanceNo())) {
//            AssetInfo tempAssetInfo = assetInfoDao.findByFinancingNumAndTenantUID(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getTenantUID());
//
//            if (tempAssetInfo != null) {
//                tempAssetInfo.setNextMeteringDate(measuringCertificateDetail.getValidDateTo());
//                tempAssetInfo.setLastMeteringDate(measuringCertificateDetail.getCheckDate());
//                tempAssetInfo.setAccountingCategory(measuringCertificateDetail.getStatus());
//                tempAssetInfo.setMeteringNO(measuringCertificateDetail.getCertificateNo());
//                if (measuringCertificateDetail.getStatus() == 2) {//计量不合格
//                    tempAssetInfo.setUsageStatus(2);//停用
//                }
//
//                assetInfoDao.save(tempAssetInfo);
//
//                flag = true;
//            }
//        }
//
//        return flag;
//    }

    public List<AssetPurchaseList> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
        importdao = WebUtil.getBean(I18nMessageRepository.class);
        assetPurchaseListDao = WebUtil.getBean(AssetPurchaseListRepository.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        List<AssetPurchaseList> tempPurchaseList = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputstream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        Map<String, Integer> header = findHeader(sheet);
        if (header.isEmpty()) {
            return null;
        }
        //sss
        for (int j = header.get("HEADER_ROW") + 1; j <= sheet.getLastRowNum(); j++) {
            HSSFRow row = sheet.getRow(j);
            if (row == null || row.getCell(header.get("PURCHASEQTY_CELL")) == null) {
                continue;
            }
            AssetPurchaseList list = new AssetPurchaseList();
            //I18nMessage im = new I18nMessage();
            String valueEn = row.getCell(header.get("ASSETTYPE_CELL")).getStringCellValue();
            I18nMessage im = importdao.getByMsgTypeAndMsgKey("assetGroup",valueEn);
            Integer msgKey = Integer.parseInt(im.getMsgKey());
            list.setTenantUID(user.getTenantUID());
            list.setInstitutionUID(user.getInstitutionUID());
            list.setHospitalUID(user.getHospitalUID());
            list.setSiteUID(user.getSiteUID());
            
            
            //row.getCell(header.get("ASSETTYPE_CELL")).getNumericCellValue();
            list.setAssetType(row.getCell(header.get("ASSETTYPE_CELL")) == null ? null : msgKey);
            list.setClinicalDeptName(row.getCell(header.get("CLINICALDEPTNAME_CELL")) == null ? null : row.getCell(header.get("CLINICALDEPTNAME_CELL")).getStringCellValue());
            list.setPurchaseQty(row.getCell(header.get("PURCHASEQTY_CELL")) == null ? null : (int) (row.getCell(header.get("PURCHASEQTY_CELL")).getNumericCellValue()));
            if (row.getCell(header.get("CreateDate_CELL")) != null) {
              
                list.setCreateDate(CellType.NUMERIC.equals(row.getCell(header.get("CreateDate_CELL")).getCellTypeEnum()) ? row.getCell(header.get("CreateDate_CELL")).getDateCellValue() : sdf.parse(row.getCell(header.get("CreateDate_CELL")).getStringCellValue()));
            }

           
            tempPurchaseList.add(list);
            assetPurchaseListDao.save(list);


        }

        return tempPurchaseList;
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
            boolean hasAssetType = false;
            boolean hasClinicalDeptName = false;
            boolean hasPurchaseQty = false;
            boolean hasCreateDate = false;

            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                HSSFCell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                try {
                    hasAssetType = HEADER_ASSETTYPE.equals(cell.getStringCellValue()) ? true : hasAssetType;
                    hasClinicalDeptName = HEADER_CLINICALDEPTNAME.equals(cell.getStringCellValue()) ? true : hasClinicalDeptName;
                    hasPurchaseQty = HEADER_PURCHASEQTY.equals(cell.getStringCellValue()) ? true : hasPurchaseQty;
                    hasCreateDate = HEADER_CREATEDATE.equals(cell.getStringCellValue()) ? true : hasCreateDate;

                    switch (cell.getStringCellValue()) {
                        case HEADER_ASSETTYPE:
                            hasAssetType = true;
                            header.put("ASSETTYPE_CELL", j);
                            break;
                        case HEADER_CLINICALDEPTNAME:
                            hasClinicalDeptName = true;
                            header.put("CLINICALDEPTNAME_CELL", j);
                            break;
                        case HEADER_PURCHASEQTY:
                            hasPurchaseQty = true;
                            header.put("PURCHASEQTY_CELL", j);
                            break;
                        case HEADER_CREATEDATE:
                            hasCreateDate = true;
                            header.put("CreateDate_CELL", j);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    LOGGER.debug(e.getMessage());
                    continue;
                }
            }
            if (hasAssetType && hasClinicalDeptName && hasPurchaseQty && hasCreateDate) {
                header.put("HEADER_ROW", i);
                break;
            }
        }

        return header;
    }

}
