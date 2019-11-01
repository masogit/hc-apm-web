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

    private static LinkedHashMap<String, String> allFields;
    private static HashMap<String, String> exportFields;

    public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"orgPath\",\"clinicalDeptName\",\"locationName\",\"assetLocation\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";

   /* public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"clinicalDeptName\",\"locationName\",\"serialNum\",\"financingNum\",\"qrCode\",\"assetGroup\",\"factoryWarrantyDate\",\"installDate\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";*/
    
    //默认不使用的字段
    static public List<String> getExtFields() {
        return Arrays.asList("assetGroupSecondary","acceptDate","warrantyMonth","subjectProject","createdDate","proprietaryClass");
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

    public synchronized static Map<String, String> getExportFields() {
        if (null == exportFields) {
            exportFields = new LinkedHashMap();
            exportFields.put(WebUtil.getMessage("EquipmentID"), "departNum");
            exportFields.put(WebUtil.getMessage("EquipmentName"), "name");
            exportFields.put(WebUtil.getMessage("EquipmentAlias"), "aliasName");
            exportFields.put(WebUtil.getMessage("RepairOwner"), "assetOwnerName");
            exportFields.put(WebUtil.getMessage("RepairOwner2"), "assetOwnerName2");
            exportFields.put(WebUtil.getMessage("RepairOwner3"), "assetOwnerName3");
            exportFields.put(WebUtil.getMessage("pmOwnerName"), "pmOwnerName");
            exportFields.put(WebUtil.getMessage("Site"), "siteUID");
            exportFields.put(WebUtil.getMessage("AssetDepartment"), "clinicalDeptName");
            exportFields.put(WebUtil.getMessage("Manufacturer"), "assetDeptName");
            exportFields.put(WebUtil.getMessage("manufacture"), "manufacture");
            exportFields.put(WebUtil.getMessage("BrandManufacturerShortName"), "makerShortName");

            exportFields.put(WebUtil.getMessage("EquipmentStandardName"), "deviceName");
            exportFields.put(WebUtil.getMessage("RegistrationNumber"),"registrationNo");
            exportFields.put(WebUtil.getMessage("Vendor"), "vendor");
            exportFields.put(WebUtil.getMessage("MedicalDeviceClassification"), "functionGroup");
            exportFields.put(WebUtil.getMessage("MedicalDeviceSubclassification"), "assetCode68");
            exportFields.put(WebUtil.getMessage("EquipmentClassification"),"assetGroup");
            exportFields.put(WebUtil.getMessage("assetGroupSecondary"), "assetGroupSecondary");
            exportFields.put(WebUtil.getMessage("parentAsset"), "parentAssetId");
            exportFields.put(WebUtil.getMessage("FunctionGrade"), "functionGrade");
            exportFields.put(WebUtil.getMessage("RepairType"), "repairForm");
            exportFields.put(WebUtil.getMessage("EquipmentModel"), "functionType");
            exportFields.put(WebUtil.getMessage("ManufacturerSerialNumber"), "serialNum");

            exportFields.put(WebUtil.getMessage("SystemIDManufacturerRequestID"), "systemId");
            exportFields.put(WebUtil.getMessage("2010NationalCode"), "nationalStandardCode");
            exportFields.put(WebUtil.getMessage("AssetID"),"financingNum");
            exportFields.put(WebUtil.getMessage("BarCode"), "barcode");
            exportFields.put(WebUtil.getMessage("ITSystemID"),"modalityId");
            exportFields.put(WebUtil.getMessage("PurchasePrice"), "purchasePrice");
            exportFields.put(WebUtil.getMessage("FinalSalvageValue"), "salvageValue");
            exportFields.put(WebUtil.getMessage("LifeCycle"), "lifecycle");
            exportFields.put(WebUtil.getMessage("DepreciationMethod"), "depreciationMethod");

            exportFields.put(WebUtil.getMessage("ManufactureDate"), "manufactDate");
            exportFields.put(WebUtil.getMessage("PurchaseDate"), "purchaseDate");
            exportFields.put(WebUtil.getMessage("ArrivalDate"), "arriveDate");
            exportFields.put(WebUtil.getMessage("StartUsingDate"),"installDate");
            exportFields.put(WebUtil.getMessage("WarrantyDate"), "factoryWarrantyDate");
            exportFields.put(WebUtil.getMessage("WarrantyExpirationDate"), "warrantyDate");
            exportFields.put(WebUtil.getMessage("TerminateDate"), "terminateDate");
            exportFields.put(WebUtil.getMessage("LastMaintenanceDate"), "lastPmDate");
            exportFields.put(WebUtil.getMessage("LastMeasurementDate"), "lastQaDate");
            exportFields.put(WebUtil.getMessage("LastInspectionDate"), "lastMeteringDate");
            exportFields.put(WebUtil.getMessage("acceptDate"), "acceptDate");
            exportFields.put(WebUtil.getMessage("warrantyMonth"), "warrantyMonth");

            exportFields.put(WebUtil.getMessage("Maintainer"),"maitanance");
            exportFields.put(WebUtil.getMessage("MaintainerTelephone"), "maitananceTel");
            exportFields.put(WebUtil.getMessage("QRCode"), "qrCode");
            exportFields.put(WebUtil.getMessage("InstallationLocationCode"), "locationCode");
            exportFields.put(WebUtil.getMessage("InstallationLocation"), "locationName");
            exportFields.put(WebUtil.getMessage("UseOwner"), "clinicalOwnerName");
            exportFields.put(WebUtil.getMessage("Remark"), "comments");


            exportFields.put(WebUtil.getMessage("UsageClassification"), "assetUsage");
            exportFields.put(WebUtil.getMessage("CapitalSource"), "capitalSource");
            exportFields.put(WebUtil.getMessage("capitalSource2"), "capitalSource2");
            exportFields.put(WebUtil.getMessage("MeasurementGrade"), "measureGrade");
            exportFields.put(WebUtil.getMessage("RiskGrade"), "riskGrade");
            exportFields.put(WebUtil.getMessage("EmergencyEquipment?"), "emergencyEquipment");
            exportFields.put(WebUtil.getMessage("ImportEquipment?"), "importEquipment");
            exportFields.put(WebUtil.getMessage("MeasurementItemCategory"), "measuringItemCategory");
            exportFields.put(WebUtil.getMessage("accountingCategory"), "accountingCategory");
            exportFields.put(WebUtil.getMessage("CurrentStatus"), "status");

            exportFields.put(WebUtil.getMessage("IsPM"),"isPm");
            exportFields.put(WebUtil.getMessage("IsChecked?"), "isChecked");
            exportFields.put(WebUtil.getMessage("assetValueCategory"), "assetValueCategory");
            exportFields.put(WebUtil.getMessage("deviceStatus"), "deviceStatus");
            exportFields.put(WebUtil.getMessage("isWarrantyNotice"), "warranty");
            exportFields.put(WebUtil.getMessage("subjectProject"), "subjectProject");
            exportFields.put(WebUtil.getMessage("createdDate"), "createdDate");
            exportFields.put(WebUtil.getMessage("proprietaryClass"), "proprietaryClass");

            exportFields.put(WebUtil.getMessage("UUID"), "UUID");

//            exportFields.put(WebUtil.getMessage("assetOwnerTel"), "assetOwnerTel");
//            exportFields.put(WebUtil.getMessage("assetOwnerTel2"), "assetOwnerTel2");
//            exportFields.put(WebUtil.getMessage("assetOwnerTel3"), "assetOwnerTel3");
//            exportFields.put(WebUtil.getMessage("clinicalOwnerTel"), "clinicalOwnerTel");
//            exportFields.put(WebUtil.getMessage("pmOwnerTel"), "pmOwnerTel");
        }
        return exportFields;
    }

     public static synchronized LinkedHashMap<String, String> getAllFields() {
        if (null == allFields) {
            allFields = new LinkedHashMap();
            allFields.put("serialNum", WebUtil.getMessage("serialNum"));
            allFields.put("departNum", WebUtil.getMessage("departNum"));
            allFields.put("name", WebUtil.getMessage("name"));
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

        }
        return allFields;
    }

    public static List<String> getAssetsListPageFields() {
        return Arrays.asList("name", "functionType", "serialNum", "departNum", "assetGroup",
                "clinicalDeptName", "manufacture", "vendor", "status", "warrantyDate", "financingNum", "qrCode");
    }

    public static List<String> getAssetsDetailpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID",
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
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID", "clinicalDeptName",
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
                "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "serialNum", "nationalStandardCode", "repairForm", "assetUsage",
                "functionGrade", "functionGroup", "assetCode68", "capitalSource", "riskGrade", "measureGrade", "emergencyEquipment", "importEquipment",
                "isPm", "isChecked", "accountingCategory", "measuringItemCategory", "financingNum", "barcode", "registrationNo", "modalityId", "locationCode",
                "locationName", "functionType", "manufactDate", "purchaseDate", "warrantyDate", "arriveDate", "installDate", "factoryWarrantyDate", "terminateDate",
                "lastPmDate", "lastQaDate", "lastMeteringDate", "salvageValue", "purchasePrice", "lifecycle", "depreciationMethod");
    }
    
    public static List<String> getAssetsMobilepageHeaderFields() {
        return Arrays.asList("clinicalDeptName", "departNum", "installDate", "assetGroup", "siteUID");
    }
}
