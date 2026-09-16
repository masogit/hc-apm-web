package com.ge.apm.domain;

import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;

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
    CONTRACT(SystemTemplateEnum.CONTRACT, SystemTemplateEnum.CONTRACT_EN),
    CONTRACT_ITEM(SystemTemplateEnum.CONTRACT_ITEM, SystemTemplateEnum.CONTRACT_ITEM),


    SMALL_ASSET_PERFORMANCE(SystemTemplateEnum.SMALL_ASSET_PERFORMANCE, SystemTemplateEnum.SMALL_ASSET_PERFORMANCE),
    SMALL_ASSET_PERFORMANCE_CONFIG(SystemTemplateEnum.SMALL_ASSET_PERFORMANCE_CONFIG, SystemTemplateEnum.SMALL_ASSET_PERFORMANCE_CONFIG),
    ORG_INFO(SystemTemplateEnum.ORG_INFO, SystemTemplateEnum.ORG_INFO),
    USER_ACCOUNT(SystemTemplateEnum.USER_ACCOUNT, SystemTemplateEnum.USER_ACCOUNT),
    MEASURING_RESULT(SystemTemplateEnum.MEASURING_RESULT, SystemTemplateEnum.MEASURING_RESULT),
    MEASURE_ELECTRIC_SAFETY(SystemTemplateEnum.MEASURE_ELECTRIC_SAFETY, SystemTemplateEnum.MEASURE_ELECTRIC_SAFETY),
    MVS_CONTRACT(SystemTemplateEnum.MVS_CONTRACT, SystemTemplateEnum.MVS_CONTRACT),
    MVS_SITE(SystemTemplateEnum.MVS_SITE, SystemTemplateEnum.MVS_SITE),
    HIS_EQUIPMENT_CONNECT(SystemTemplateEnum.HIS_EQUIPMENT_CONNECT, SystemTemplateEnum.HIS_EQUIPMENT_CONNECT),
    HIS_CLINICAL(SystemTemplateEnum.HIS_CLINICAL, SystemTemplateEnum.HIS_CLINICAL),
    HIS_EQUIPMENT_SFXM(SystemTemplateEnum.HIS_EQUIPMENT_SFXM, SystemTemplateEnum.HIS_EQUIPMENT_SFXM),
    HIS_EQUIPMENT_DATA_SOURCE(SystemTemplateEnum.HIS_EQUIPMENT_DATA_SOURCE, SystemTemplateEnum.HIS_EQUIPMENT_DATA_SOURCE),
    SUPPLIER(SystemTemplateEnum.SUPPLIER, SystemTemplateEnum.SUPPLIER),
    ARRIVAL_ITEM(SystemTemplateEnum.ARRIVAL_ITEM, SystemTemplateEnum.ARRIVAL_ITEM),

    ASSET_PURCHASE(SystemTemplateEnum.ASSET_PURCHASE, SystemTemplateEnum.ASSET_PURCHASE),
    SPECIAL_ASSET(SystemTemplateEnum.SPECIAL_ASSET_INFO, SystemTemplateEnum.SPECIAL_ASSET_INFO),
    DEPT_TRANSACTION_LOG(SystemTemplateEnum.DEPT_TRANSACTION_LOG, SystemTemplateEnum.DEPT_TRANSACTION_LOG),
    ;

    private final SystemTemplateEnum enumZh;
    private final SystemTemplateEnum enumEn;


    DownloadTemplateFileType(SystemTemplateEnum enumZh, SystemTemplateEnum enumEn) {
        this.enumZh = enumZh;
        this.enumEn = enumEn;
    }


    public static SystemTemplateEnum getSystemTemplate(String type, Boolean isLocal) {
        DownloadTemplateFileType templateFileType = Arrays.stream(DownloadTemplateFileType.values()).filter(value -> value.name().equals(type)).findFirst().orElse(DownloadTemplateFileType.DEFAULT);
        return Boolean.TRUE.equals(isLocal) ? templateFileType.enumZh : templateFileType.enumEn;
    }
}
