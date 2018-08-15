/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
public class CustomConfigConstant {

    private static LinkedHashMap<String, String> allFields;
    private static HashMap<String, String> exportFields;

    static public List<String> getSysTemFields() {
        return Arrays.asList("name", "assetGroup");
    }

    static public List<String> getConfigRolesName() {
        return Arrays.asList("HospitalHead", "AssetHead", "DeptHead", "AssetStaff", "ClinicalStaff");
    }

    public static HashMap<String, String> getExportFields() {
        if (null == exportFields) {
            exportFields = new HashMap();
            exportFields.put("制造商序列号", "serialNum");
            exportFields.put("设备编号", "departNum");
            exportFields.put("设备名称", "name");
            exportFields.put("设备别名", "aliasName");
            exportFields.put("设备分类", "assetGroup");
            exportFields.put("维修负责人", "assetOwnerName");
            exportFields.put(WebUtil.getMessage("assetOwnerTel"), "assetOwnerTel");
            exportFields.put("维修负责人2", "assetOwnerName2");
            exportFields.put(WebUtil.getMessage("assetOwnerTel2"), "assetOwnerTel2");
            exportFields.put("维修负责人3", "assetOwnerName3");
            exportFields.put(WebUtil.getMessage("assetOwnerTel3"), "assetOwnerTel3");
            exportFields.put("院区", "siteUID");
            exportFields.put("所属科室", "clinicalDeptName");
            exportFields.put("使用科室", "assetDeptName");
            exportFields.put("使用责任人", "clinicalOwnerName");
            exportFields.put(WebUtil.getMessage("clinicalOwnerTel"), "clinicalOwnerTel");
            exportFields.put("制造商", "manufacture");
            exportFields.put("品牌/制造商简称", "makerShortName");
            exportFields.put("供应商", "vendor");
            exportFields.put("维修商", "maitanance");
            exportFields.put("维修商电话", "maitananceTel");
            exportFields.put("2010国家编码", "nationalStandardCode");
            exportFields.put(WebUtil.getMessage("parentAsset"), "parentAssetId");
            exportFields.put("设备标准名称", "deviceName");
            exportFields.put("SystemID/厂商报修ID", "systemId");
            exportFields.put("资产编号", "financingNum");
            exportFields.put("条码", "barcode");
            exportFields.put("注册证号", "registrationNo");
            exportFields.put("IT系统编号", "modalityId");
            exportFields.put("二维码", "qrCode");
            exportFields.put("设备型号", "functionType");
            exportFields.put("医疗器械分类", "functionGroup");
            exportFields.put("医疗器械子分类", "assetCode68");
            exportFields.put("功能等级", "functionGrade");
            exportFields.put("维修类型", "repairForm");
            exportFields.put("安装位置编码", "locationCode");
            exportFields.put("安装位置", "locationName");
            exportFields.put("当前状态", "status");
            exportFields.put(WebUtil.getMessage("deviceStatus"), "deviceStatus");
            exportFields.put("备注", "comments");
            exportFields.put("用途分类", "assetUsage");
            exportFields.put("资金来源", "capitalSource");
            exportFields.put(WebUtil.getMessage("capitalSource2"), "capitalSource2");
            exportFields.put("计量等级", "measureGrade");
            exportFields.put("风险等级", "riskGrade");
            exportFields.put("是否急救设备", "emergencyEquipment");
            exportFields.put("是否进口设备", "importEquipment");
            exportFields.put("计量项目类别", "measuringItemCategory");
            exportFields.put("核算类别", "accountingCategory");
            exportFields.put("出厂日期", "manufactDate");
            exportFields.put("采购日期", "purchaseDate");
            exportFields.put("到货日期", "arriveDate");
            exportFields.put("启用日期", "installDate");
            exportFields.put("质保日期", "factoryWarrantyDate");
            exportFields.put("维保截止日期", "warrantyDate");
            exportFields.put("报废日期", "terminateDate");
            exportFields.put("采购价格", "purchasePrice");
            exportFields.put("最终残值", "salvageValue");
            exportFields.put("使用年限", "lifecycle");
            exportFields.put("折旧算法", "depreciationMethod");
            exportFields.put("是否PM", "isPm");
            exportFields.put("是否盘点", "isChecked");
            exportFields.put("上次保养日期", "lastPmDate");
            exportFields.put("上次巡检日期", "lastQaDate");
            exportFields.put("上次计量日期", "lastMeteringDate");
            exportFields.put("是否对维保到期提醒", "warranty");
        }
        return exportFields;
    }

    static public LinkedHashMap<String, String> getAllFields() {
        if (null == allFields) {
            allFields = new LinkedHashMap();
            allFields.put("serialNum", WebUtil.getMessage("serialNum"));
            allFields.put("departNum", WebUtil.getMessage("departNum"));
            allFields.put("name", WebUtil.getMessage("name"));
            allFields.put("aliasName", WebUtil.getMessage("aliasName"));
            allFields.put("assetGroup", WebUtil.getMessage("assetGroup"));
            allFields.put("assetOwnerName", WebUtil.getMessage("assetOwnerName"));
            allFields.put("assetOwnerTel", WebUtil.getMessage("assetOwnerTel"));
            allFields.put("assetOwnerName2", WebUtil.getMessage("assetOwnerName2"));
            allFields.put("assetOwnerTel2", WebUtil.getMessage("assetOwnerTel2"));
            allFields.put("assetOwnerName3", WebUtil.getMessage("assetOwnerName3"));
            allFields.put("assetOwnerTel3", WebUtil.getMessage("assetOwnerTel3"));
            allFields.put("siteUID", WebUtil.getMessage("hospitalId"));
            allFields.put("clinicalDeptName", WebUtil.getMessage("clinicalDeptName"));
            allFields.put("assetDeptName", WebUtil.getMessage("assetDeptName"));
            allFields.put("clinicalOwnerName", WebUtil.getMessage("clinicalOwnerName"));
            allFields.put("clinicalOwnerTel", WebUtil.getMessage("clinicalOwnerTel"));
            allFields.put("manufacture", WebUtil.getMessage("manufacture"));
            allFields.put("makerShortName", WebUtil.getMessage("makerShortName"));
            allFields.put("vendor", WebUtil.getMessage("vendor"));
            allFields.put("maitanance", WebUtil.getMessage("maitanance"));
            allFields.put("maitananceTel", WebUtil.getMessage("maitananceTel"));
            allFields.put("nationalStandardCode", WebUtil.getMessage("nationalStandardCode"));
            allFields.put("parentAssetId", WebUtil.getMessage("parentAsset"));
            allFields.put("deviceName", WebUtil.getMessage("deviceName"));
            allFields.put("systemId", WebUtil.getMessage("systemId"));
            allFields.put("financingNum", WebUtil.getMessage("financingNum"));
            allFields.put("barcode", WebUtil.getMessage("barcode"));
            allFields.put("registrationNo", WebUtil.getMessage("registrationNo"));
            allFields.put("modalityId", WebUtil.getMessage("modalityId"));
            allFields.put("qrCode", WebUtil.getMessage("qrCode"));
            allFields.put("functionType", WebUtil.getMessage("functionType"));
            allFields.put("functionGroup", WebUtil.getMessage("functionGroup"));
            allFields.put("assetCode68", WebUtil.getMessage("assetCode68"));
            allFields.put("functionGrade", WebUtil.getMessage("functionGrade"));
            allFields.put("repairForm", WebUtil.getMessage("repairForm"));
            allFields.put("locationCode", WebUtil.getMessage("locationCode"));
            allFields.put("locationName", WebUtil.getMessage("locationName"));
            allFields.put("status", WebUtil.getMessage("status"));
            allFields.put("deviceStatus", WebUtil.getMessage("deviceStatus"));
            allFields.put("comments", WebUtil.getMessage("comments"));
            allFields.put("assetUsage", WebUtil.getMessage("assetUsage"));
            allFields.put("capitalSource", WebUtil.getMessage("capitalSource"));
            allFields.put("capitalSource2", WebUtil.getMessage("capitalSource2"));
            allFields.put("measureGrade", WebUtil.getMessage("measureGrade"));
            allFields.put("riskGrade", WebUtil.getMessage("riskGrade"));
            allFields.put("emergencyEquipment", WebUtil.getMessage("emergencyEquipment"));
            allFields.put("importEquipment", WebUtil.getMessage("importEquipment"));
            allFields.put("measuringItemCategory", WebUtil.getMessage("measuringItemCategory"));
            allFields.put("accountingCategory", WebUtil.getMessage("accountingCategory"));
            allFields.put("manufactDate", WebUtil.getMessage("manufactDate"));
            allFields.put("purchaseDate", WebUtil.getMessage("purchaseDate"));
            allFields.put("arriveDate", WebUtil.getMessage("arriveDate"));
            allFields.put("installDate", WebUtil.getMessage("installDate"));
            allFields.put("factoryWarrantyDate", WebUtil.getMessage("factoryWarrantyDate"));
            allFields.put("warrantyDate", WebUtil.getMessage("warrantyDate"));
            allFields.put("terminateDate", WebUtil.getMessage("terminateDate"));
            allFields.put("purchasePrice", WebUtil.getMessage("purchasePrice"));
            allFields.put("salvageValue", WebUtil.getMessage("salvageValue"));
            allFields.put("lifecycle", WebUtil.getMessage("lifecycle"));
            allFields.put("depreciationMethod", WebUtil.getMessage("depreciationMethod"));
            allFields.put("isPm", WebUtil.getMessage("isAssetPMed"));
            allFields.put("isChecked", WebUtil.getMessage("isAssetChecked"));
            allFields.put("lastPmDate", WebUtil.getMessage("lastPmDate"));
            allFields.put("lastQaDate", WebUtil.getMessage("lastQaDate"));
            allFields.put("lastMeteringDate", WebUtil.getMessage("lastMeteringDate"));
            allFields.put("warranty", WebUtil.getMessage("isWarrantyNotice"));
        }
        return allFields;
    }

    public static List<String> getAssetsListPageFields() {
        return Arrays.asList("name", "functionType", "serialNum", "departNum", "assetGroup",
                "clinicalDeptName", "manufacture", "vendor", "status", "warrantyDate", "financingNum", "qrCode");
    }

    public static List<String> getAssetsDetailpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "siteUID",
                "clinicalDeptName", "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "makerShortName", "vendor",
                "maitanance", "maitananceTel", "nationalStandardCode");
    }

    public static List<String> getAssetsDetailpageDetailFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode", "functionType", "functionGroup", "assetCode68", "functionGrade",
                "repairForm", "locationCode", "locationName", "status", "deviceStatus", "isPm", "isChecked", "comments", "assetUsage",
                "capitalSource", "measureGrade", "riskGrade", "emergencyEquipment", "importEquipment", "measuringItemCategory",
                "accountingCategory", "manufactDate", "purchaseDate", "arriveDate", "installDate", "factoryWarrantyDate",
                "warrantyDate", "terminateDate", "purchasePrice", "salvageValue", "lifecycle", "depreciationMethod", "lastPmDate",
                "lastQaDate", "lastMeteringDate", "warranty");
    }

    public static List<String> getAssetsEditpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "siteUID", "clinicalDeptName",
                "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "makerShortName", "vendor", "maitanance", "maitananceTel", "nationalStandardCode");
    }

    public static List<String> getAssetsEditpageCodesFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode");
    }

    public static List<String> getAssetsEditpageStatusFields() {
        return Arrays.asList("functionType", "functionGroup", "assetCode68", "functionGrade", "repairForm",
                "locationCode", "locationName", "status", "deviceStatus", "isPm", "isChecked", "comments");
    }

    public static List<String> getAssetsEditpageCatalogFields() {
        return Arrays.asList("assetUsage", "capitalSource", "measureGrade", "riskGrade",
                "emergencyEquipment", "importEquipment", "measuringItemCategory", "accountingCategory");
    }

    public static List<String> getAssetsEditpageFinicialFields() {
        return Arrays.asList("purchasePrice", "salvageValue", "lifecycle", "depreciationMethod");
    }

    public static List<String> getAssetsEditpageDateFields() {
        return Arrays.asList("manufactDate", "purchaseDate", "arriveDate", "installDate", "factoryWarrantyDate",
                "warrantyDate", "terminateDate", "warranty");
    }

    public static List<String> getAssetsMobilepageBasicFields() {
        return Arrays.asList("departNum", "name", "assetGroup", "status", "siteUID", "clinicalDeptName", "clinicalOwnerName", "qrCode", "comments");
    }

    public static List<String> getAssetsMobilepageDetailFields() {
        return Arrays.asList("aliasName", "systemId", "manufacture", "vendor", "maitanance", "maitananceTel", "assetOwnerName", "assetOwnerTel",
                "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "serialNum", "nationalStandardCode", "repairForm", "assetUsage",
                "functionGrade", "functionGroup", "assetCode68", "capitalSource", "riskGrade", "measureGrade", "emergencyEquipment", "importEquipment",
                "isPm", "isChecked", "accountingCategory", "measuringItemCategory", "financingNum", "barcode", "registrationNo", "modalityId", "locationCode",
                "locationName", "functionType", "manufactDate", "purchaseDate", "warrantyDate", "arriveDate", "installDate", "factoryWarrantyDate", "terminateDate",
                "lastPmDate", "lastQaDate", "lastMeteringDate", "salvageValue", "purchasePrice", "lifecycle", "depreciationMethod");
    }
    
    public static List<String> getAssetsMobilepageHeaderFields() {
        return Arrays.asList("clinicalDeptName", "departNum", "installDate", "assetGroup", "siteUID");
    }
}
