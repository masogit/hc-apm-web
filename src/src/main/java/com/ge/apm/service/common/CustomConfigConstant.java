/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
public class CustomConfigConstant {

    private static LinkedHashMap<String, String> allFields;

    static public List<String> getSysTemFields() {
        return Arrays.asList("name", "assetGroup");
    }

    static public List<String> getConfigRolesName() {
        return Arrays.asList("HospitalHead", "AssetHead", "DeptHead", "AssetStaff", "ClinicalStaff");
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
            allFields.put("warranty", "是否对质保到期提醒");
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
                "capitalSource", "capitalSource2", "measureGrade", "riskGrade", "emergencyEquipment", "importEquipment", "measuringItemCategory",
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
        return Arrays.asList("assetUsage", "capitalSource", "capitalSource2", "measureGrade", "riskGrade",
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
        return Arrays.asList("departNum", "name", "assetGroup", "status", "siteUID","clinicalDeptName", "clinicalOwnerName", "qrCode","comments");
    }

    public static List<String> getAssetsMobilepageDetailFields() {
        return Arrays.asList("aliasName", "systemId", "manufacture", "vendor", "maitanance","maitananceTel", "assetOwnerName", "assetOwnerTel",
                "assetOwnerName2","assetOwnerTel2","assetOwnerName3","assetOwnerTel3","serialNum","nationalStandardCode","repairForm","assetUsage",
                "functionGrade","functionGroup","assetCode68","capitalSource","riskGrade","measureGrade","emergencyEquipment","importEquipment",
                "isPm","isChecked","accountingCategory","measuringItemCategory","financingNum","barcode","registrationNo","modalityId","locationCode",
                "locationName","functionType","manufactDate","purchaseDate","warrantyDate","arriveDate","installDate","factoryWarrantyDate","terminateDate",
                "lastPmDate","lastQaDate","lastMeteringDate","salvageValue","purchasePrice","lifecycle","depreciationMethod");
    }
}
