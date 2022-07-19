package com.ge.apm.domain;

public enum BoTypeRelation {
    /**
     *
     */
    InventoryOrderDetail("inventory_order_detail", "id", "bo_uuid"),
    NurseSiteMaintain("nurse_site_maintain", "id", "bo_uuid"),
    PccReport("pcc_examine_report", "id", "bo_uuid"),
    supplier("supplier", "uid", "bo_uuid"),
    MeasuringOrder("measuring_order", "id", "bo_uuid"),
    ArrivalList("arrival_list", "id", "bo_uuid"),
    PmOrder("v2_pm_order", "id", "bo_uuid"),
    ArrivalListItem("arrival_list_item", "id", "bo_uuid"),
    AssetInfo("asset_info", "uid", "bo_uuid"),
    WorkOrderExportTemplate("work_order_export_template", "id", "bo_uuid"),
    QRCodeLib("qr_code_lib", "uid", "bo_uuid"),
    WorkOrder("v2_work_order", "id", "bo_uuid"),
    QrCodeLib("qr_code_lib", "uid", "bo_uuid"),
    TableDefinition("table_definition", "id", "bo_uuid"),
    PartAttachment("v2_work_order_detail", "id", "bo_uuid"),
    PrePmOrder("v2_pm_order", "id", "bo_uuid"),
    AdjustEquipment("shared_asset_type", "id", "bo_uuid"),
    UserSelfRegister("user_self_register", "id", "bo_id"),
    AssetContract("asset_contract", "uid", "bo_uuid"),
    ServiceRequest("service_request_report", "id", "bo_uuid"),
    PmOrderReportTemplate("pm_order_report_template", "id", "bo_uuid"),
    ;

    private final String tableName;
    private final String tableField;
    private final String boField;

    BoTypeRelation(String tableName, String tableField, String boField) {
        this.tableName = tableName;
        this.tableField = tableField;
        this.boField = boField;
    }

    public static BoTypeRelation getBoTypeRelation(String tableName){
        for (BoTypeRelation boTypeRelation : BoTypeRelation.values()) {
            if (boTypeRelation.tableName.equals(tableName)) {
                return boTypeRelation;
            }
        }
        return null;
    }

    public String getTableName(){return tableName;}
    public String getTableField(){return tableField;}
    public String getBoField(){return boField;}

}
