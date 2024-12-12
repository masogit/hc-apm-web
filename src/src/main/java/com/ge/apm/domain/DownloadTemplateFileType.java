package com.ge.apm.domain;

import java.util.Arrays;

public enum DownloadTemplateFileType {

//    /**
//     * 下载模板文件类型
//     */
//    DEFAULT("", ""),
//    CONTRACT("APM_Contract_Service_Template.xlsx", "APM_Contract_Service_Template_en.xlsx"),
//    CONTRACT_ITEM("APM_ContractItem_Service_Template.xlsx", "APM_ContractItem_Service_Template.xlsx"),
//    SMALL_ASSET_PERFORMANCE("APM_SmallAssetPerformance_Template.xlsx", "APM_SmallAssetPerformance_Template.xlsx"),
//    SMALL_ASSET_PERFORMANCE_CONFIG("APM_SmallAssetPerformanceConfig_Template.xlsx", "APM_SmallAssetPerformanceConfig_Template.xlsx"),
//    ORG_INFO("APM_Org_Template.xlsx", "APM_Org_Template.xlsx"),
//    USER_ACCOUNT("UserAccountImport_Template.xlsx", "UserAccountImport_Template.xlsx"),
//    MEASURING_RESULT("APM_MEASURING_Template.xls", "APM_MEASURING_Template.xls"),
//    MEASURE_ELECTRIC_SAFETY("APM_Electric_Safety_Template.ps", "APM_Electric_Safety_Template.ps"),
//    MVS_CONTRACT("APM_mvs_contract.xlsx", "APM_mvs_contract.xlsx"),
//    MVS_SITE("APM_mvs_site.xlsx", "APM_mvs_site.xlsx"),
//    HIS_EQUIPMENT_CONNECT("His_equipment_connect.xlsx", "His_equipment_connect.xlsx"),
//    HIS_CLINICAL("His_clinical.xlsx", "His_clinical.xlsx"),
//    HIS_EQUIPMENT_SFXM("His_sfxm.xlsx", "His_sfxm.xlsx"),
//    HIS_EQUIPMENT_DATA_SOURCE("His_data_source_Template.xlsx", "His_data_source_Template.xlsx"),
//    SUPPLIER("APM_Supplier.xlsx", "APM_Supplier.xlsx"),
//    ARRIVAL_ITEM("APM_AssetArrivalItem_Template.xlsx", "APM_AssetArrivalItem_Template.xlsx"),
//
//    ;
//
//    private final String templateFileZh;
//    private final String templateFileEn;
//
//    DownloadTemplateFileType(String templateFileZh, String templateFileEn) {
//        this.templateFileZh = templateFileZh;
//        this.templateFileEn = templateFileEn;
//    }
//
//    public static String getTemplateFile(String type, Boolean isLocal) {
//        DownloadTemplateFileType templateFileType = Arrays.stream(DownloadTemplateFileType.values()).filter(value -> value.name().equals(type)).findFirst().orElse(DownloadTemplateFileType.DEFAULT);
//        return Boolean.TRUE.equals(isLocal) ? templateFileType.templateFileZh : templateFileType.templateFileEn;
//    }


    /**
     * 下载模板文件类型
     */
    DEFAULT(null, null),
    CONTRACT(SystemTemplate.SystemTemplateEnum.CONTRACT, SystemTemplate.SystemTemplateEnum.CONTRACT_EN),
    CONTRACT_ITEM(SystemTemplate.SystemTemplateEnum.CONTRACT_ITEM, SystemTemplate.SystemTemplateEnum.CONTRACT_ITEM),


//    SMALL_ASSET_PERFORMANCE("APM_SmallAssetPerformance_Template.xlsx", "APM_SmallAssetPerformance_Template.xlsx"),
//    SMALL_ASSET_PERFORMANCE_CONFIG("APM_SmallAssetPerformanceConfig_Template.xlsx", "APM_SmallAssetPerformanceConfig_Template.xlsx"),
//    ORG_INFO("APM_Org_Template.xlsx", "APM_Org_Template.xlsx"),
    USER_ACCOUNT(SystemTemplate.SystemTemplateEnum.USER_ACCOUNT, SystemTemplate.SystemTemplateEnum.USER_ACCOUNT),
//    MEASURING_RESULT("APM_MEASURING_Template.xls", "APM_MEASURING_Template.xls"),
//    MEASURE_ELECTRIC_SAFETY("APM_Electric_Safety_Template.ps", "APM_Electric_Safety_Template.ps"),
//    MVS_CONTRACT("APM_mvs_contract.xlsx", "APM_mvs_contract.xlsx"),
//    MVS_SITE("APM_mvs_site.xlsx", "APM_mvs_site.xlsx"),
//    HIS_EQUIPMENT_CONNECT("His_equipment_connect.xlsx", "His_equipment_connect.xlsx"),
//    HIS_CLINICAL("His_clinical.xlsx", "His_clinical.xlsx"),
//    HIS_EQUIPMENT_SFXM("His_sfxm.xlsx", "His_sfxm.xlsx"),
//    HIS_EQUIPMENT_DATA_SOURCE("His_data_source_Template.xlsx", "His_data_source_Template.xlsx"),
//    SUPPLIER("APM_Supplier.xlsx", "APM_Supplier.xlsx"),
//    ARRIVAL_ITEM("APM_AssetArrivalItem_Template.xlsx", "APM_AssetArrivalItem_Template.xlsx"),
    ;

    private final SystemTemplate.SystemTemplateEnum enumZh;
    private final SystemTemplate.SystemTemplateEnum enumEn;


    DownloadTemplateFileType(SystemTemplate.SystemTemplateEnum enumZh, SystemTemplate.SystemTemplateEnum enumEn) {
        this.enumZh = enumZh;
        this.enumEn = enumEn;
    }


    public static SystemTemplate.SystemTemplateEnum getSystemTemplate(String type, Boolean isLocal) {
        DownloadTemplateFileType templateFileType = Arrays.stream(DownloadTemplateFileType.values()).filter(value -> value.name().equals(type)).findFirst().orElse(DownloadTemplateFileType.DEFAULT);
        return Boolean.TRUE.equals(isLocal) ? templateFileType.enumZh : templateFileType.enumEn;
    }
}
