/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import webapp.framework.web.WebUtil;

import java.util.*;

import static com.ge.apm.service.common.CustomConfigFields.*;

/**
 *
 * @author 212579464
 */
public class CustomConfigConstant {

    private static HashMap<String, String> exportFields;

    public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"externalQuotation\":{\"enable\":false},\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"orgPath\",\"clinicalDeptName\",\"locationName\",\"assetLocation\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"repairReportSendPlace\":{\"defaultValue\":\"ward\",\"enable\":false,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";

   /* public static final String DEFAULT_MOBILE_PAGE_JSON =
            "{\"pageWorkOrder\":{\"group\":[{\"type\":\"assetInfo\",\"show\":true,\"assetInfoFields\":[\"departNum\",\"name\",\"functionType\",\"status\",\"clinicalDeptName\",\"locationName\",\"serialNum\",\"financingNum\",\"qrCode\",\"assetGroup\",\"factoryWarrantyDate\",\"installDate\"]},{\"type\":\"orderFields\",\"show\":true,\"priority\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true},\"assetStatus\":{\"defaultValue\":\"3\",\"enable\":true,\"required\":true}},{\"type\":\"descriptions\",\"show\":true,\"attachments\":{\"enable\":true,\"required\":false},\"voiceAttachment\":{\"enable\":true,\"required\":false},\"voiceOrReasonRequired\":true,\"requestReason\":{\"defaultValue\":\"\",\"enable\":true,\"required\":false}}]}}";*/

    public static final String DEFAULT_MOBILE_BUILD_ASSET_PAGE_JSON = "{\"pageBuildAsset\":{\"group\":[{\"type\":\"buildAsset\",\"show\":true,\"assetInfoFields\":[\"name\",\"departNum\",\"financingNum\",\"serialNum\",\"functionType\",\"assetGroup\",\"manufactDate\",\"installDate\",\"acceptDate\",\"factoryWarrantyDate\",\"vendor\",\"vendorTel\",\"purchasePrice\",\"registrationNo\",\"assetClass\",\"lifecycle\",\"emergencyEquipment\",\"makerShortName\",\"parentAssetId\",\"locationName\",\"comments\"]}]}}";

    public static final String DEFAULT_ASSET_LIST_SORT_JSON = "{\"namePyCode\":true}";

    //默认不使用的字段
    static public List<String> getExtFields() {
        return Arrays.asList("assetGroupSecondary","acceptDate","warrantyMonth","subjectProject","createdDate","proprietaryClass","connectToInsite","oneQrCode","installStatus","assetClass","geibTable_installedTime","assetQuality","isUseRegistration","networkingStatus","systemNum1","systemNum2","systemNum3","systemNum4","systemNum5","assetQuantity","productFamily","productSegment","mvsContractStartDate","mvsContractEndDate", "assetTypeGroup","spare01","spare02","spare03","spare04","spare05","spare06","spare07","spare08","spare09","spare10","spare11","spare12","spare13","spare14","spare15","meteringCycle");
    }

    //只读字段
    static public List<String> getReadOnlyFields() {
        return Arrays.asList("createdDate","estimatedRiskLevel");
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

    static public List<CustomConfigFields> getRenameFields() {
        return Arrays.asList(capitalSource2, accountingCategory, assetValueCategory, subjectProject, proprietaryClass
                ,systemNum2,systemNum3,systemNum4,systemNum5,mvsContractStartDate, mvsContractEndDate,spare01,spare02,spare03,spare04,spare05,spare06,spare07,spare08,spare09,spare10,spare11,spare12,spare13,spare14,spare15,meteringCycle);
    }


    //默认可修改角色
    static public List<String> getConfigEditRolesName() {
        return Arrays.asList("AssetHead", "AssetStaff");
    }

    public static List<String> getExportFieldsOrder() {
        return Arrays.asList(
                "departNum", "name", "aliasName",
                "assetOwnerName", "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3",
                "pmOwnerName", "pmOwnerTel", "patrolInspectionOwnerName","patrolInspectionOwnerTel","measuringOwner",
                "siteUID", "clinicalDeptName", "clinicalOwnerName", "clinicalOwnerTel", "assetDeptName",
                "manufacture", "manufactureTel", "makerShortName",
                "deviceName", "registrationNo", "vendor", "vendorTel", "functionGroup", "assetCode68","medicalApparatusClassificationName","medicalApparatusClassificationCode","medicalApparatusClassification","assetGroup",
                "assetGroupSecondary", "parentAssetId", "functionGrade", "repairForm", "functionType", "serialNum",
                "usageCount", "ratingUsageCount", "epc",
                "systemId", "nationalStandardCode", "financingNum", "barcode", "modalityId", "purchasePrice",
                "salvageValue", "lifecycle", "depreciationLifecycle", "depreciationMethod", "manufactDate", "purchaseDate", "arriveDate",
                "installDate", "factoryWarrantyDate", "warrantyDate", "terminateDate", "lastPmDate", "lastQaDate",
                "lastMeteringDate", "lastInventoryDate", "acceptDate", "warrantyMonth","maitanance", "maitananceTel", "qrCode",
                "locationCode", "locationName", "comments", "locationNotes","assetUsage", "capitalSource",
                "capitalSource2", "measureGrade", "riskGrade", "estimatedRiskLevel", "emergencyEquipment", "importEquipment", "measuringItemCategory",
                "accountingCategory", "status","isPm", "isFixed","isChecked", "assetValueCategory", "deviceStatus",
                "warranty", "subjectProject", "proprietaryClass", "tags", "UCMID", "createdDate",
                "createdBy","mainDirectory","secondDirectory","geDeviceType","assetClassification32","UUID"
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
        Arrays.stream(CustomConfigFields.values()).forEach(i->
            allFields.put(i.name(), WebUtil.getMessage(i.getMsgKey()))
        );
        return allFields;
    }

    public static List<String> getAssetsListPageFields() {
        return Arrays.asList("name", "functionType", "serialNum", "departNum", "assetGroup",
                "clinicalDeptName", "manufacture", "vendor", "status", "warrantyDate", "financingNum", "qrCode");
    }

    public static List<String> getAssetsDetailpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID",
                "clinicalDeptName", "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "manufactureTel", "makerShortName", "vendor", "vendorTel",
                "maitanance", "maitananceTel", "nationalStandardCode","assetQuality","assetUsedLifecycle", "assetLifecycleDeadline");
    }

    public static List<String> getAssetsDetailpageDetailFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode", "functionType", "functionGroup", "assetCode68", "medicalApparatusClassification","functionGrade",
                "repairForm", "locationCode", "locationName", "status", "deviceStatus", "isPm","isFixed", "isChecked", "comments", "assetUsage",
                "capitalSource", "measureGrade", "riskGrade", "emergencyEquipment", "importEquipment", "measuringItemCategory",
                "accountingCategory", "manufactDate", "purchaseDate", "arriveDate", "installDate", "factoryWarrantyDate",
                "warrantyDate", "terminateDate", "purchasePrice", "salvageValue", "lifecycle", "depreciationLifecycle", "depreciationMethod", "lastPmDate",
                "usageCount", "ratingUsageCount", "epc",
                "lastQaDate", "lastMeteringDate", "lastInventoryDate", "warranty","createdBy","assetUsedLifecycle", "assetLifecycleDeadline", "warrantyStatus");
    }

    public static List<String> getAssetsEditpageBasicFields() {
        return Arrays.asList("serialNum", "name", "aliasName", "assetGroup", "assetOwnerName",
                "assetOwnerTel", "assetOwnerName2", "assetOwnerTel2", "assetOwnerName3", "assetOwnerTel3", "pmOwnerName", "pmOwnerTel", "siteUID", "clinicalDeptName",
                "clinicalOwnerName", "clinicalOwnerTel", "manufacture", "manufactureTel", "makerShortName", "vendor", "vendorTel","maitanance", "maitananceTel", "nationalStandardCode","UCMID","assetQuality");
    }

    public static List<String> getAssetsEditpageCodesFields() {
        return Arrays.asList("parentAssetId", "deviceName", "systemId", "financingNum", "departNum",
                "barcode", "registrationNo", "modalityId", "qrCode");
    }

    public static List<String> getAssetsEditpageStatusFields() {
        return Arrays.asList("functionType", "functionGroup", "assetCode68", "medicalApparatusClassification","functionGrade", "repairForm",
                "locationCode", "locationName", "status", "deviceStatus", "isPm", "isFixed","isChecked", "comments", "locationNotes");
    }

    public static List<String> getAssetsEditpageCatalogFields() {
        return Arrays.asList("assetUsage", "capitalSource", "measureGrade", "riskGrade",
                "emergencyEquipment", "importEquipment", "measuringItemCategory", "accountingCategory");
    }
    public static List<String> getAssetsEditpageEquipmentPerformanceFields() {
        return Arrays.asList("monitorRate", "otherPower","unitPriceElectricity");
    }

    public static List<String> getAssetsEditpageFinicialFields() {
        return Arrays.asList("purchasePrice", "salvageValue", "lifecycle", "depreciationLifecycle", "depreciationMethod","createdBy");
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
                "isPm","isFixed", "isChecked", "accountingCategory", "measuringItemCategory", "financingNum", "barcode", "registrationNo", "modalityId", "locationCode",
                "locationName", "functionType", "manufactDate", "purchaseDate", "warrantyDate", "arriveDate", "installDate", "factoryWarrantyDate", "terminateDate",
                "usageCount", "ratingUsageCount", "epc",
                "lastPmDate", "lastQaDate", "lastMeteringDate", "lastInventoryDate", "salvageValue", "purchasePrice", "lifecycle", "depreciationLifecycle", "depreciationMethod","createdBy");
    }

    public static List<String> getAssetsMobilepageHeaderFields() {
        return Arrays.asList("clinicalDeptName", "departNum", "installDate", "assetGroup", "siteUID");
    }

    public static List<String> getAllAssetsMobilepageBuildAssetFields() {
        return ListUtil.newArrayList("name", "assetGroup", "assetClass", "functionType", "lifecycle", "emergencyEquipment", "departNum", "manufacture", "serialNum", "makerShortName", "registrationNo", "locationName", "manufactDate", "clinicalDeptName", "parentAssetId", "comments", "assetTypeGroup", "assetDeptName","assetClassification32");
    }

    public static List<String> getAssetsMobilepageBuildAssetFields() {
        return ListUtil.newArrayList("name", "assetGroup", "assetClass", "functionType", "lifecycle", "emergencyEquipment", "departNum", "manufacture", "serialNum", "makerShortName", "registrationNo", "locationName", "manufactDate", "clinicalDeptName", "parentAssetId", "comments");
    }

    public static List<String> getAssetsMobilepageFilterFields() {
        return Arrays.asList("siteUID", "assetGroup", "status", "spare10");
    }

    public static List<String> defaultAssetsMobilepageFilterFields() {
        return Arrays.asList("siteUID", "assetGroup", "status", "isChecked","assetTypeGroup", "spare10");
    }

    /**
     * the below fields are only used by wechat, no need to export in asset
     * @return
     */
    public static List<String> getAssetsDisabledExportFields() {
        return Arrays.asList("orgPath", "assetLocation");
    }

    public static List<String> getAssetsImportantFieldsAll() {
        return Arrays.asList("name","assetOwnerName","departNum","clinicalDeptName","assetDeptName","clinicalOwnerName","manufacture","makerShortName","registrationNo","vendor","functionGroup","assetCode68","functionType","serialNum","purchasePrice","lifecycle","manufactDate","installDate","factoryWarrantyDate","locationName","assetUsage","capitalSource","measureGrade","riskGrade","emergencyEquipment","importEquipment","systemNum4","medicalApparatusClassification","deviceStatus","status","isValid","assetDisposalResults","purchaseDate","qrCode","spare06","spare07","spare08","spare09","spare10","financingNum","locationNotes","repairForm");
    }

    public static List<String> getTemplateEnableList() {
        return Arrays.asList("GEIBTableExcel", "AssetInfoImportExcel");
    }
    public static List<String> ninthHospitalNineAuthority() {
        return Arrays.asList("AssetHead","ITAdmin","DataMaintainStaff","AssetStaff","HospitalHead","DeptHead","GEInternalStaff");
    }
}
