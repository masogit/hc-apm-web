/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Service
@Scope("prototype")
public class AssetFileImportStreamingService extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportStreamingService.class);

    public List<Map<String, Object>> getOrgInfoMapFromTemplate(ExcelDocumentStreamingReader doc) {

        List<Map<String, Object>> result = new ArrayList<>();
        int dataRow = 5;

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iterator = doc.iterator();
        while (iterator.hasNext()) {
            Map<String, ExcelDocument.ValueWrapper> rowMap = iterator.next();


            String orgName = ExcelDocument.getCellStringValue(rowMap.get("部门名称"));

            if (orgName == null || orgName.isEmpty() || orgName.equals(AssetRow.DELETE_MARK)) {
                continue;
            }

            OrgInfo org = new OrgInfo();
//            org.setShortName(orgName);
            org.setName(orgName);
            org.setLineNum(dataRow);

            Map<String, Object> map = new LinkedHashMap();
            map.put("org", org);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);
            result.add(map);


            ++dataRow;
        }

        return result;
    }

    public List<Map<String, Object>> getAssetMapFromTemplate(ExcelDocumentStreamingReader doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();

        Date now = new Date();

        List<Map<String, Object>> result = new ArrayList<>();

        int dataRow = 7;

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iterator = doc.iterator();
        while (iterator.hasNext()) {
            Map<String, ExcelDocument.ValueWrapper> rowMap = iterator.next();
            try{
                Map<String, Object> map = new LinkedHashMap();

                AssetInfo asset;

                String uuid = ExcelDocument.getCellStringValue(rowMap.get("UUID"));
                if (!StringUtils.isEmpty(uuid)) {
                    asset = assetDao.findByUid(uuid);
                    if (asset == null || !asset.getTenantUID().equals(selectedOrgToImport.getTenantUID())) throw new Exception(WebUtil.getMessage("InvalidUUID"));

                    //save old data to compare later
                    map.put(AssetFileImportSupportingService.OLD_QRCODE_KEY, asset.getQrCode());
                } else {
                    asset = new AssetInfo();
                }

                asset.setLineNum(dataRow);

                String assetName = ExcelDocument.getCellStringValue(rowMap.get(trimMsgKey(WebUtil.getMessage("EquipmentName"))));
                if (StringUtils.isEmpty(assetName)) {
                    break; //return if go beyond the valid cell range
                }

                AssetRow assetRow = new AssetRow(asset, rowMap);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("EquipmentName")), "setName", false);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("EquipmentID")), "setDepartNum", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("AssetID")), "setFinancingNum", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("EquipmentAlias")), "setAliasName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("RepairOwner")), "setAssetOwnerName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("RepairOwner2")), "setAssetOwnerName2", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("RepairOwner3")), "setAssetOwnerName3", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("PMOwner")), "setPmOwnerName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("clinicalDeptId")), "setClinicalDeptName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("manufacture")), "setManufacture", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("BrandManufacturerShortName")), "setMakerShortName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("EquipmentStandardName")), "setDeviceName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("RegistrationNumber")), "setRegistrationNo", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("Vendor")), "setVendor", true);

                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("medicalInstrumentClassification")), "setFunctionGroup", assetFunctionTypes, map, null);
                if (asset.getFunctionGroup() != null) {
                    List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(String.valueOf(asset.getFunctionGroup()), siteId);
                    assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("assetCode68")), "setAssetCode68", msgList, map, null);
                } else {
                    //only update need to clear old data
                    asset.setAssetCode68(null);
                }

                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("assetGroup")), "setAssetGroup", assetGroup, map, null);
                if (asset.getAssetGroup() != null) {
                    String msgType = "assetGroupSecondary_" + asset.getAssetGroup();
                    List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(msgType, siteId);
                    assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("assetGroupSecondary")), "setAssetGroupSecondary", msgList, map, null);
                } else {
                    asset.setAssetGroupSecondary(null);
                }

                assetRow.setFieldIntegerValue(trimMsgKey(WebUtil.getMessage("functionGrade")), "setFunctionGrade");
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("repairForm")), "setRepairForm", repairFormList, map, StringUtils.isEmpty(asset.getUid()) ? 1 : null); //新增资产默认内修
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("functionType")), "setFunctionType", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("serialNum")), "setSerialNum", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("systemId")), "setSystemId", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("BarCode")), "setBarcode", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("NationalCode")), "setNationalStandardCode", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("ITSystemID")), "setModalityId", true);
                assetRow.setFieldDoubleValue(trimMsgKey(WebUtil.getMessage("PurchasePrice")), "setPurchasePrice");
                assetRow.setFieldDoubleValue(trimMsgKey(WebUtil.getMessage("FinalSalvageValue")), "setSalvageValue");
                assetRow.setFieldDoubleValue(trimMsgKey(WebUtil.getMessage("LifeCycle")), "setLifecycleInYear");

                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("DepreciationMethod")), "setDepreciationMethod", depreciationMethod, map, null);

                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("manufactDate")), "setManufactDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("purchaseDate")), "setPurchaseDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("arriveDate")), "setArriveDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("StartUsingDate")), "setInstallDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("WarrantyDate")), "setFactoryWarrantyDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("WarrantyExpirationDate")), "setWarrantyDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("terminateDate")), "setTerminateDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("lastPmDate")), "setLastPmDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("lastMeteringDate")), "setLastMeteringDate");
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("LastInspectionDate")), "setLastQaDate");

                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("Maintainer")), "setMaitanance", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("MaintainerTelephone")), "setMaitananceTel", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("InstallationLocationCode")), "setLocationCode", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("InstallationLocation")), "setLocationName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("UseOwner")), "setClinicalOwnerName", true);
                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("Remark")), "setComments", true);

                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("UsageClassification")), "setAssetUsage", assetUsageList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("CapitalSource")), "setCapitalSource", capitalSourceList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("riskGrade")), "setRiskGrade", riskGradeList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("measureGrade")), "setMeasureGrade", measureGradeList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("measuringItemCategory")), "setMeasuringItemCategory", measuringItemCategoryList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("emergencyEquipment")), "setEmergencyEquipment", emergencyEquipmentList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("IsImportEquipment")), "setImportEquipment", importEquipmentList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("CurrentStatus")), "setStatus", assetStatusList, map, 1);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("AccountingCategory")), "setAccountingCategory", accountingCategoryList, map, null);
                assetRow.setFieldBooleanValue(trimMsgKey(WebUtil.getMessage("IsPM")), "setIsPm", true);
                assetRow.setFieldBooleanValue(trimMsgKey(WebUtil.getMessage("IsChecked")), "setIsChecked", true);

                assetRow.setFieldDropdownValueAsString(trimMsgKey(WebUtil.getMessage("assetValueCategory")), "setAssetValueCategory", assetValueCategoryList, map, null);

                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("deviceStatus")), "setDeviceStatus", deviceStatusList, map, null);
                assetRow.setFieldDropdownValue(trimMsgKey(WebUtil.getMessage("capitalSource2")), "setCapitalSource2", capitalSource2List, map, null);
                assetRow.setFieldBooleanValue(trimMsgKey(WebUtil.getMessage("isWarrantyNotice")), "setWarranty", true);

                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("subjectProject")), "setSubjectProject", true);
                assetRow.setFieldDateValue(trimMsgKey(WebUtil.getMessage("acceptDate")), "setAcceptDate");
                assetRow.setFieldIntegerValue(trimMsgKey(WebUtil.getMessage("WarrantyPeriodMonths")), "setWarrantyMonth");

                assetRow.setFieldStringValue(trimMsgKey(WebUtil.getMessage("QRCode")), "setQrCode", true);
//                supportingService.tryFormatQrCodeIfPossible(rowMap, asset);

                assetRow.setFieldDropdownValueAsString(trimMsgKey(WebUtil.getMessage("ProprietaryClass")), "setProprietaryClass", proprietaryClassList, map, null);

                supportingService.markDefautValue(selectedOrgToImport, asset, now, isNeedAudit);

                map.put("asset", asset);
                map.put("status", StringUtils.isEmpty(asset.getUid()) ? ImportStatus.New : ImportStatus.Exist);
                map.put("rowData", rowMap);

                result.add(map);

            }catch(Exception e){
                logger.error(e.getMessage(), e);
                throw new Exception(WebUtil.getMessage("RowStart")+dataRow+WebUtil.getMessage("DataError") + e.getMessage());
            }


            ++dataRow;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getAssetMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }
    
    public String trimMsgKey(String msgKey){
        return msgKey == null ? "" : msgKey.trim();
    }



    public boolean checkDocVersion(ExcelDocumentStreamingReader doc) {
        return EXCEL_ASSET_TEMPLATE_VERSION.equalsIgnoreCase(doc.getVersion());
    }

}
