package com.ge.apm.service.uaa;

import java.util.Arrays;
import java.util.List;

public enum AssetManagementEnum {
    ASSET_INFO("资产管理",  true, true, "asset.info", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    ASSET_PARENTS_INFO("主子设备信息", false, true, "asset.parents.info", false, UpdateTenantFeatureService.class, "enableAssetPreview", true, "audit", "swapCenter", "supplierVersion"),
    ASSET_BATCH_MODIFY("资产批量修改院区", false,true, "asset.batch.modify", true, UpdateTenantFeatureService.class, "isAllowAssetBatchUpdate", true, "audit", "swapCenter", "supplierVersion"),
    ASSET_RISK_EVALUATE("设备风险评估", false, true , "asset.risk.evaluate", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    ASSET_SCRAP("报废资产", true, false, "asset.scrap", false, UpdateTenantFeatureService.class, "needScrappedControl", true),
    ASSET_LOCATION("设备定位", false, true, "asset.location", false, UpdateTenantFeatureService.class, "hasLocationEn", true, "audit", "swapCenter", "supplierVersion"),
    ASSET_BATCH_IMPORT("批量导入资产数据", true, false, "asset.batchImportData", false, null, null, false),
    ASSET_SCRAP_TEMPLATE("报损模板管理", true, false, "asset.scrapTempMgt", false, null, null, false),
    ASSET_APPROVE("资产审核", true, false, "asset.audit", false, null, null, false),
    ASSET_KEYASSETDATAINPUT("重点医疗设备", true, false, "asset.keyAssetDataInput", false, null, null, false),
//    ASSET_INVENTORY_A("盘点工单A(云阜)", true, false, "asset.inventory", true, UpdateTenantFeatureService.class, "hasCurrentTenantName", true),
    ASSET_INVENTORY("盘点工单", true, false, "asset.inventory", true, UpdateTenantFeatureService.class, "hasCurrentTenantName", false),
    ASSET_PERFORMANCE("设备绩效记录", true, false, "asset.achievements", false, null, null, false),
    ASSET_CONTRACT("合同管理", true, false, "asset.mgt", false, null, null, false),
    ASSET_SUPPLIER("供应商管理", true, false, "asset.supplierMgt", false, null, null, false),
    ASSET_SUPPLIER_EVALUATION("供应商评价", true, false, "asset.supplierEvaluation", false, null, null, false),
    ASSET_SUPPLIER_EMPOWERMENT("供应商负责人赋权", false, true, "supplier.empowerment", true, UpdateTenantFeatureService.class, "showSupplierViewAndEditFieldsTable", true),
    MULTILEVEL_APPROVE("多级审批", false, true, "multilevel.approve", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    INSTALL("到货安装", true, false, "install", false, null, null, false),
    INSTALL_CHECK("到货安装.核对入库", true,false, "install.check", false, UpdateTenantFeatureService.class, "needInstorageCheck", true),
    SPECIAL_ASSET("特种设备管理", true, false, "specialAsset", false, null, null, false),
    PARTS("备件管理", true, false, "sparePartsManagement", false, null, null, false),
    PARTS_APPLY("备件管理(申请流程)", true, false, "parts", false, null, null, false),
    REPAIR("维修", true, true, "repair", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    REPAIR_ARRANGE("排班管理", true, false, "repair.workSchedule", false, null, null, false),
    REPAIR_APPROVE_PROCESS("核心组审核进度查看", false, true,  "repair.approve.process", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    REPAIR_VACATION("休假管理", true, false, "repair.vacationMgt", false, null, null, false),
    REPAIR_ADVERSE("不良事件", true, false, "repair.adverseEvents", false, null, null, false),
    REPAIR_TEMPLATE("报告模版管理", true, false, "repair.reportMgt", false, null, null, false),
    MAINTAIN("保养", true, true, "maintain", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    MAINTAIN_ELECTRICDATAIMPORT("电气安全数据导入", true, false, "maintain.electricDataImport", false, null, null, false),
    MAINTAIN_TEMPLATE("报表模版管理", true, false, "maintain.reportFormMgt", false, null, null, false),
    MEASURE("计量", true, true, "measure", false, null, null, false, "audit", "swapCenter", "supplierVersion", "opsVersion", "ZKBVersion"),
    NURSESITE("日常检查", true, false, "nurseSite", false, null, null, false),
    NURSESITE_RECORD("开关机登记", true, false, "nurseSite.optRecord", false, null, null, false),
    NURSESITE_USING("使用登记", true, false, "nurseSite_useRegistration", false, null, null, false),
    KNOWLEDGE("知识文档", true, false, "knowledge", false, null, null, false),
    KNOWLEDGE_ZKB("智康保文档", true, false, "knowledge_zkbMgt", false, null, null, false),
    ADJUST_ASSET("调剂中心", true, false, "assetAdjust", false, null, null, false),
    ADJUST_ARRANGE("调剂中心排班管理", true, false, "assetAdjust.nameConfig", false, null, null, false),
    ADJUST_DEPTMENT("科室借调", false, true, "assetAdjust.deptment", false, UpdateTenantFeatureService.class, "enableSiteToSiteAllocation", true,"audit", "swapCenter", "supplierVersion", "opsVersion", "ZKBVersion"),
    AUDIT("质控评审", true, false, "audit", false, null, null, false),
    LINKABOUT("相关链接", true, false, "link", false, null, null, false),
    PERFORMANCE("绩效分析", true, false, "performance", false, null, null, false),
    BUDGET("预算管理", true, false, "budgetMgr", false, null, null, false),
    TRAINING("培训", false, true, "training", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    HISPERFORMANCE("His绩效分析", true, false, "hisPerform", false, null, null, false),
    REPORT_CENTER("报表中心", true, false, "jmReport", false, null, null, false),
    CONFIG("系统配置", true, true, "config", false, null, null, false, "supplierVersion"),
    ASSET_ACCOUNT_DASHBOARD("资产管理一览", true, false, "config.accountdash", false, null, null, false),
    PLATFORM_CONFIG("平台管理", true, true, "config.platform", false, null, null, false),
    AUDIT_CONFIG("质控评审配置", true, false, "config.auditConfig", false, null, null, false),
    MVS_ASSET("MVS设备管理", false, false, "mvs.asset", false, null, null, false),
    TEST_MANAGE("检测管理", true, false, "examineMgt", false, null, null, false),
    LOG_AUDIT("日志审计", false, true, "logAudit", false, null, null, false, "audit", "swapCenter", "supplierVersion"),
    DEVICEDATAPREVIEW("设备数据概览", true, false, "deviceDataPreview", false, null, null, false),
    COMPANY_FILES("公司档案", true, false, "zkbBasicInfo", false, null, null, false),
    SUPPLIER_TENANT("供应商租户", true, false, "supplierManagement", false, null, null, false),
    ASSET_MVS_DATA("MVS设备信息导入", true, false, "asset.importMvsDataFile", false, null, null, false),
    ;

    private String name;//菜单名称
    private boolean IsInMenu;//是否存在菜单配置
    private boolean required;//是否必须
    private String oldName;//旧菜单配置名称
    private boolean isChekConfig;//是否检查配置
    private Class<?> chekClass;//检查类
    private String chekMethod;//检查方法
    private boolean expectedValue;//期望值
    private List<String> ignore;//忽略特定租户合集

    AssetManagementEnum(String name, boolean isInMenu, boolean required, String oldName, boolean isChekConfig, Class<?> chekClass, String chekMethod, boolean expectedValue, String... ignore) {
        this.name = name;
        this.IsInMenu = isInMenu;
        this.required = required;
        this.oldName = oldName;
        this.isChekConfig = isChekConfig;
        this.chekClass = chekClass;
        this.chekMethod = chekMethod;
        this.expectedValue = expectedValue;
        this.ignore = Arrays.asList(ignore);
    }

    public static Enum<AssetManagementEnum> getEnumByOldName(String oldName) {
        for (AssetManagementEnum assetManagementEnum : AssetManagementEnum.values()) {
            if (assetManagementEnum.getOldName().equals(oldName)) {
                return assetManagementEnum;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getOldName() {
        return oldName;
    }

    public boolean isChekConfig() {
        return isChekConfig;
    }

    public boolean isExpectedValue() {
        return expectedValue;
    }

    public boolean isIsInMenu() {
        return IsInMenu;
    }

    public Class<?> getChekClass() {
        return chekClass;
    }

    public String getChekMethod() {
        return chekMethod;
    }

    public List<String> getIgnore() {
        return ignore;
    }
}
