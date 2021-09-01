/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import webapp.framework.web.WebUtil;

import java.util.*;

/**
 *
 * @author 212579464
 */
public class CustomConfigConstant {

    private static HashMap<String, String> exportFields;

    public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"orgPath\",\"clinicalDeptName\",\"locationName\",\"assetLocation\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";

   /* public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"clinicalDeptName\",\"locationName\",\"serialNum\",\"financingNum\",\"qrCode\",\"assetGroup\",\"factoryWarrantyDate\",\"installDate\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";*/

    //默认不使用的字段
    static public List<String> getExtFields() {
        return Arrays.asList("assetGroupSecondary","acceptDate","warrantyMonth","subjectProject","createdDate","proprietaryClass","connectToInsite","oneQrCode","installStatus","assetClass","geibTable_installedTime");
    }

    //只读字段
    static public List<String> getReadOnlyFields() {
        return Arrays.asList("createdDate");
    }

    static public List<String> getSystemFields() {
        return Arrays.asList("name", "assetGroup","status","siteUID");
    }

    static public List<String> getInstallRequiredFields() {
        return Arrays.asList("serialNum");
    }

    static public List<String> getConfigRolesName() {
        return Arrays.asList("HospitalHead", "AssetHead", "DeptHead", "AssetStaff", "ClinicalStaff");
    }

    //默认可修改角色
    static public List<String> getConfigEditRolesName() {
        return Arrays.asList("AssetHead", "AssetStaff");
    }

    public static List<String> getExportFieldsOrder() {
        return Arrays.asList(
                "departNum", "name", "aliasName",
                "assetOwnerName", "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3",
                "pmOwnerName", "pmOwnerTel", "measuringOwner",
                "siteUID", "clinicalDeptName", "clinicalOwnerName", "clinicalOwnerTel", "assetDeptName",
                "manufacture", "makerShortName",
                "deviceName", "registrationNo", "vendor", "vendorTel", "functionGroup", "assetCode68","medicalApparatusClassificationName","medicalApparatusClassificationCode","medicalApparatusClassification","assetGroup",
                "assetGroupSecondary", "parentAssetId", "functionGrade", "repairForm", "functionType", "serialNum",
                "systemId", "nationalStandardCode", "financingNum", "barcode", "modalityId", "purchasePrice",
                "salvageValue", "lifecycle", "depreciationMethod", "manufactDate", "purchaseDate", "arriveDate",
                "installDate", "factoryWarrantyDate", "warrantyDate", "terminateDate", "lastPmDate", "lastQaDate",
                "lastMeteringDate", "acceptDate", "warrantyMonth","maitanance", "maitananceTel", "qrCode",
                "locationCode", "locationName", "comments", "locationNotes","assetUsage", "capitalSource",
                "capitalSource2", "measureGrade", "riskGrade", "emergencyEquipment", "importEquipment", "measuringItemCategory",
                "accountingCategory", "status","isPm", "isChecked", "assetValueCategory", "deviceStatus",
                "warranty", "subjectProject", "proprietaryClass", "assetTags", "UCMID", "createdDate",
                "createdBy","mainDirectory","secondDirectory","geDeviceType","UUID"
        );
    }

    /**
     * 导入导出所需要的两个字段，别的地方不需要
     * @return
     */
    public static synchronized Map<String, String> getExportOrImportFields() {
        Map<String,String> result = getAllFields();
        result.put("medicalApparatusClassificationCode", WebUtil.getMessage("medicalApparatusClassification")+ "编码");
        result.put("medicalApparatusClassificationName", WebUtil.getMessage("medicalApparatusClassification"));
        return result;
    }

    public static synchronized LinkedHashMap<String, String> getAllFields() {
        LinkedHashMap<String,String> allFields = new LinkedHashMap();
        allFields.put("serialNum", WebUtil.getMessage("serialNum"));
        allFields.put("departNum", WebUtil.getMessage("departNum"));
        allFields.put("name", WebUtil.getMessage("EquipmentName"));
        allFields.put("aliasName", WebUtil.getMessage("aliasName"));
        allFields.put("assetGroup", WebUtil.getMessage("assetGroup"));
        allFields.put("assetGroupSecondary", WebUtil.getMessage("assetGroupSecondary"));
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
        allFields.put("vendorTel", WebUtil.getMessage("vendorTel"));
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
        allFields.put("medicalApparatusClassification", WebUtil.getMessage("medicalApparatusClassification"));
        allFields.put("functionGrade", WebUtil.getMessage("functionGrade"));
        allFields.put("repairForm", WebUtil.getMessage("repairForm"));
        allFields.put("locationCode", WebUtil.getMessage("locationCode"));
        allFields.put("locationName", WebUtil.getMessage("locationName"));
        allFields.put("status", WebUtil.getMessage("status"));
        allFields.put("deviceStatus", WebUtil.getMessage("deviceStatus"));
        allFields.put("comments", WebUtil.getMessage("comments"));
        allFields.put("locationNotes", WebUtil.getMessage("locationNotes"));
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
        allFields.put("assetValueCategory", WebUtil.getMessage("assetValueCategory"));
        allFields.put("pmOwnerName", WebUtil.getMessage("pmOwnerName"));
        allFields.put("pmOwnerTel", WebUtil.getMessage("pmOwnerTel"));
        allFields.put("acceptDate", WebUtil.getMessage("acceptDate"));
        allFields.put("warrantyMonth", WebUtil.getMessage("warrantyMonth"));
        allFields.put("subjectProject", WebUtil.getMessage("subjectProject"));
        allFields.put("createdDate", WebUtil.getMessage("createdDate"));
        allFields.put("proprietaryClass", WebUtil.getMessage("proprietaryClass"));
        allFields.put("measuringOwner", WebUtil.getMessage("measuringOwner"));
        allFields.put("orgPath", WebUtil.getMessage("orgPath"));
        allFields.put("assetLocation", WebUtil.getMessage("assetLocation"));
        allFields.put("UCMID", WebUtil.getMessage("UCMID"));
        allFields.put("assetTags", WebUtil.getMessage("assetTags"));
        allFields.put("createdBy", WebUtil.getMessage("createdBy"));
        allFields.put("connectToInsite", WebUtil.getMessage("connectToInsite"));
        allFields.put("assetClass", WebUtil.getMessage("assetClass"));
        allFields.put("oneQrCode", WebUtil.getMessage("oneQrCode"));
        allFields.put("installStatus", WebUtil.getMessage("installStatus"));
        allFields.put("geibTable_installedTime", WebUtil.getMessage("geibTable_installedTime"));
        return allFields;
    }

    public static List<String> getAssetsListPageFields() {
        return Arrays.asList("name", "functionType", "serialNum", "departNum", "assetGroup",
                "clinicalDeptName", "manufacture", "vendor", "status", "warrantyDate", "financingNum", "qrCode");
    }

    public static List<String> getAssetsDetailpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID",
                "clinicalDeptName", "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "makerShortName", "vendor", "vendorTel",
                "maitanance", "maitananceTel", "nationalStandardCode");
    }

    public static List<String> getAssetsDetailpageDetailFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode", "functionType", "functionGroup", "assetCode68", "medicalApparatusClassification","functionGrade",
                "repairForm", "locationCode", "locationName", "status", "deviceStatus", "isPm", "isChecked", "comments", "assetUsage",
                "capitalSource", "measureGrade", "riskGrade", "emergencyEquipment", "importEquipment", "measuringItemCategory",
                "accountingCategory", "manufactDate", "purchaseDate", "arriveDate", "installDate", "factoryWarrantyDate",
                "warrantyDate", "terminateDate", "purchasePrice", "salvageValue", "lifecycle", "depreciationMethod", "lastPmDate",
                "lastQaDate", "lastMeteringDate", "warranty","createdBy");
    }

    public static List<String> getAssetsEditpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID", "clinicalDeptName",
                "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "makerShortName", "vendor", "vendorTel","maitanance", "maitananceTel", "nationalStandardCode","UCMID");
    }

    public static List<String> getAssetsEditpageCodesFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode");
    }

    public static List<String> getAssetsEditpageStatusFields() {
        return Arrays.asList("functionType", "functionGroup", "assetCode68", "medicalApparatusClassification","functionGrade", "repairForm",
                "locationCode", "locationName", "status", "deviceStatus", "isPm", "isChecked", "comments", "locationNotes");
    }

    public static List<String> getAssetsEditpageCatalogFields() {
        return Arrays.asList("assetUsage", "capitalSource", "measureGrade", "riskGrade",
                "emergencyEquipment", "importEquipment", "measuringItemCategory", "accountingCategory");
    }

    public static List<String> getAssetsEditpageFinicialFields() {
        return Arrays.asList("purchasePrice", "salvageValue", "lifecycle", "depreciationMethod","createdBy");
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
                "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "serialNum", "nationalStandardCode", "repairForm", "assetUsage",
                "functionGrade", "functionGroup", "assetCode68","medicalApparatusClassification", "capitalSource", "riskGrade", "measureGrade", "emergencyEquipment", "importEquipment",
                "isPm", "isChecked", "accountingCategory", "measuringItemCategory", "financingNum", "barcode", "registrationNo", "modalityId", "locationCode",
                "locationName", "functionType", "manufactDate", "purchaseDate", "warrantyDate", "arriveDate", "installDate", "factoryWarrantyDate", "terminateDate",
                "lastPmDate", "lastQaDate", "lastMeteringDate", "salvageValue", "purchasePrice", "lifecycle", "depreciationMethod","createdBy");
    }

    public static List<String> getAssetsMobilepageHeaderFields() {
        return Arrays.asList("clinicalDeptName", "departNum", "installDate", "assetGroup", "siteUID");
    }

    /**
     * the below fields are only used by wechat, no need to export in asset
     * @return
     */
    public static List<String> getAssetsDisabledExportFields() {
        return Arrays.asList("orgPath", "assetLocation");
    }

    public static List<String> getAssetsImportantFieldsAll() {
        return Arrays.asList("assetDeptName", "clinicalDeptName", "locationName", "clinicalOwnerName","deviceStatus"
                ,"purchaseDate","purchasePrice","qrCode","installDate","departNum","name","financingNum","locationNotes","functionType");
    }

    public static List<String> getTemplateEnableList() {
        return Arrays.asList("GEIBTableExcel", "AssetInfoImportExcel", "DealerFEAndSalesExcel", "ZKBDealerFEAndSalesExcel", "UserAccountImportExcel");
    }
    public static List<String> ninthHospitalNineAuthority() {
        return Arrays.asList("AssetHead","ITAdmin","DataMaintainStaff","AssetStaff","HospitalHead","DeptHead","GEInternalStaff");
    }
}
