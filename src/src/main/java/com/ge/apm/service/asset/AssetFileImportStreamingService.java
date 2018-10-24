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
                break;
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
        int keyRow = 6;
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
                    if (asset == null || !asset.getTenantUID().equals(selectedOrgToImport.getTenantUID())) throw new Exception("无效UUID");

                    //save old data to compare later
                    map.put(AssetFileImportSupportingService.OLD_QRCODE_KEY, asset.getQrCode());
                } else {
                    asset = new AssetInfo();
                }

                asset.setLineNum(dataRow);

                String assetName = ExcelDocument.getCellStringValue(rowMap.get("设备名称"));
                if (StringUtils.isEmpty(assetName)) {
                    break; //return if go beyond the valid cell range
                }

                String inputQrcode = ExcelDocument.getCellStringValue(rowMap.get("二维码"));
                if (!StringUtils.isEmpty(inputQrcode) && !inputQrcode.equals(asset.getQrCode())) {
                    asset.setQrCode(inputQrcode.replace("-", ""));
                }

                AssetRow assetRow = new AssetRow(asset, rowMap);
                assetRow.setFieldStringValue("设备名称", "setName");
                assetRow.setFieldStringValue("设备编号", "setDepartNum");
                assetRow.setFieldStringValue("资产编号", "setFinancingNum");
                assetRow.setFieldStringValue("设备别名", "setAliasName");
                assetRow.setFieldStringValue("维修负责人", "setAssetOwnerName");
                assetRow.setFieldStringValue("维修负责人2", "setAssetOwnerName2");
                assetRow.setFieldStringValue("维修负责人3", "setAssetOwnerName3");
                assetRow.setFieldStringValue("保养负责人", "setPmOwnerName");
                assetRow.setFieldStringValue("所属科室", "setClinicalDeptName");
                assetRow.setFieldStringValue("制造商", "setManufacture");
                assetRow.setFieldStringValue("品牌/制造商简称", "setMakerShortName");
                assetRow.setFieldStringValue("设备标准名称", "setDeviceName");
                assetRow.setFieldStringValue("注册证号", "setRegistrationNo");
                assetRow.setFieldStringValue("供应商", "setVendor");

                assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", assetFunctionTypes, map, null);
                if (asset.getFunctionGroup() != null) {
                    List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(String.valueOf(asset.getFunctionGroup()), siteId);
                    assetRow.setFieldDropdownValue("医疗器械子分类", "setAssetCode68", msgList, map, null);
                } else {
                    //only update need to clear old data
                    asset.setAssetCode68(null);
                }

                assetRow.setFieldDropdownValue("设备分类", "setAssetGroup", assetGroup, map, null);
                if (asset.getAssetGroup() != null) {
                    String msgType = "assetGroupSecondary_" + asset.getAssetGroup();
                    List<I18nMessage> msgList = FieldValueMessageController.getFieldValueList(msgType, siteId);
                    assetRow.setFieldDropdownValue("设备分类2级", "setAssetGroupSecondary", msgList, map, null);
                } else {
                    asset.setAssetGroupSecondary(null);
                }

                assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
                assetRow.setFieldDropdownValue("维修类型", "setRepairForm", repairFormList, map, StringUtils.isEmpty(asset.getUid()) ? 1 : null); //新增资产默认内修
                assetRow.setFieldStringValue("设备型号", "setFunctionType");
                assetRow.setFieldStringValue("制造商序列号", "setSerialNum");
                assetRow.setFieldStringValue("SystemID/厂商报修ID", "setSystemId");
                assetRow.setFieldStringValue("条码", "setBarcode");
                assetRow.setFieldStringValue("2010国家编码", "setNationalStandardCode");
                assetRow.setFieldStringValue("IT系统编号", "setModalityId");
                assetRow.setFieldDoubleValue("采购价格", "setPurchasePrice");
                assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
                assetRow.setFieldDoubleValue("使用年限", "setLifecycleInYear");

                assetRow.setFieldDropdownValue("折旧算法", "setDepreciationMethod", depreciationMethod, map, null);

                assetRow.setFieldDateValue("出厂日期", "setManufactDate");
                assetRow.setFieldDateValue("采购日期", "setPurchaseDate");
                assetRow.setFieldDateValue("到货日期", "setArriveDate");
                assetRow.setFieldDateValue("启用日期", "setInstallDate");
                assetRow.setFieldDateValue("质保日期", "setFactoryWarrantyDate");
                assetRow.setFieldDateValue("维保截止日期", "setWarrantyDate");
                assetRow.setFieldDateValue("报废日期", "setTerminateDate");
                assetRow.setFieldDateValue("上次保养日期", "setLastPmDate");
                assetRow.setFieldDateValue("上次计量日期", "setLastMeteringDate");
                assetRow.setFieldDateValue("上次巡检日期", "setLastQaDate");

                assetRow.setFieldStringValue("维修商", "setMaitanance");
                assetRow.setFieldStringValue("维修商电话", "setMaitananceTel");
                assetRow.setFieldStringValue("安装位置编码", "setLocationCode");
                assetRow.setFieldStringValue("安装位置", "setLocationName");
                assetRow.setFieldStringValue("使用责任人", "setClinicalOwnerName");
                assetRow.setFieldStringValue("备注", "setComments");

                assetRow.setFieldDropdownValue("用途分类", "setAssetUsage", assetUsageList, map, null);
                assetRow.setFieldDropdownValue("资金来源", "setCapitalSource", capitalSourceList, map, null);
                assetRow.setFieldDropdownValue("风险等级", "setRiskGrade", riskGradeList, map, null);
                assetRow.setFieldDropdownValue("计量等级", "setMeasureGrade", measureGradeList, map, null);
                assetRow.setFieldDropdownValue("计量项目类别", "setMeasuringItemCategory", measuringItemCategoryList, map, null);
                assetRow.setFieldDropdownValue("是否急救设备", "setEmergencyEquipment", emergencyEquipmentList, map, null);
                assetRow.setFieldDropdownValue("是否进口设备", "setImportEquipment", importEquipmentList, map, null);
                assetRow.setFieldDropdownValue("当前状态", "setStatus", assetStatusList, map, 1);
                assetRow.setFieldDropdownValue("核算类别", "setAccountingCategory", accountingCategoryList, map, null);
                assetRow.setFieldBooleanValue("是否PM", "setIsPm", true);
                assetRow.setFieldBooleanValue("是否盘点", "setIsChecked", true);

                assetRow.setFieldDropdownValueAsString("资产价值分类", "setAssetValueCategory", assetValueCategoryList, map, null);

                assetRow.setFieldDropdownValue("资产状态", "setDeviceStatus", deviceStatusList, map, null);
                assetRow.setFieldDropdownValue("资金来源2", "setCapitalSource2", capitalSource2List, map, null);
                assetRow.setFieldBooleanValue("是否对维保到期提醒", "setWarranty", null);


                if (StringUtils.isEmpty(asset.getUid())) {

                    //only new asset need to set this
                    asset.setUsageStatus(3); //已建档

                    if(isNeedAudit){
                        asset.setUsageStatus(2); //待审核
                    }

                } else {
                    supportingService.cleanRelatedFieldsWhenUpdating(asset);
                }

                supportingService.markDefautValue(selectedOrgToImport, asset, now);

                map.put("asset", asset);
                map.put("status", StringUtils.isEmpty(asset.getUid()) ? ImportStatus.New : ImportStatus.Exist);
                map.put("rowData", rowMap);

                result.add(map);

            }catch(Exception e){
                logger.error(e.getMessage(), e);
                throw new Exception("第"+dataRow+"行数据错误:" + e.getMessage());
            }


            ++dataRow;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getAssetMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public boolean checkDocVersion(ExcelDocumentStreamingReader doc) {
        return EXCEL_ASSET_TEMPLATE_VERSION.equalsIgnoreCase(doc.getVersion());
    }

}
