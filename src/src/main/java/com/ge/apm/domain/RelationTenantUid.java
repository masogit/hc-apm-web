package com.ge.apm.domain;

public enum RelationTenantUid {
    /**
     *
     */
    SharedAssetReqDetail("shared_asset_req_detail", "asset_info", "asset_info_id", "uid"),
    AdverseEventsFile("adverse_events_file", "adverse_events", "adverse_events_uid", "uid"),
    ArrivalListItem("arrival_list_item", "arrival_list", "arrival_list_id", "id"),
    AssetLocationInfo("asset_location_info", "org_info", "site_uid", "uid"),
    AssetTagBiomedGroup("asset_tag_biomed_group", "asset_tag", "tag_id", "id"),
    AssetTagMsgSubscriber("asset_tag_msg_subscriber", "asset_tag", "tag_id", "id"),
    AssetTagRule("asset_tag_rule", "asset_tag", "tag_id", "id"),
    AttrDesc("attr_desc", "attribute", "attribute_id", "id"),
    BiomedGroupUser("biomed_group_user", "biomed_group", "group_id", "id"),
    ContractInfoItem("contract_info_item", "contract_info", "contract_id", "id"),
    LoginRecord("login_record", "user_account", "login_name", "login_name"),
    MsgConfig("msg_config", "msg_tenant", "id", "msgconfig_id"),
    NurseSiteMaintainData("nurse_site_maintain_data", "nurse_site_maintain", "parent_id", "id"),
    SimplePartsWarehouseLocator("simple_parts_warehouse_locator", "simple_parts_warehouse", "parts_warehouse_id", "id"),
    SimplePartsWarehouseOrg("simple_parts_warehouse_org", "simple_parts_warehouse", "parts_warehouse_id", "id"),
    SimplePartsWarehouseUser("simple_parts_warehouse_user", "simple_parts_warehouse", "parts_warehouse_id", "id"),
    SupplierTenantJoin("supplier_tenant_join", "supplier", "supplier_id", "id"),
    SupplierUser("supplier_user", "supplier", "supplier_uid", "uid"),
    UserOrg("user_org", "user_account", "user_uid", "uid"),
    UserRole("user_role", "user_account", "user_id", "id"),
    UserSignature("user_signature", "user_account", "user_uid", "uid"),
    V2WorkOrderDetailReturn("v2_work_order_detail_return", "v2_work_order_detail", "work_order_detail_id", "id"),
    UnstructuredDataCollection("unstructured_data_collection", "v2_pm_order", "bo_uuid", "id"),
    ;

    private final String tableA;
    private final String tableB;
    private final String fieldA;
    private final String fieldB;

    RelationTenantUid(String tableA, String tableB, String fieldA, String fieldB) {
        this.tableA = tableA;
        this.tableB = tableB;
        this.fieldA = fieldA;
        this.fieldB = fieldB;
    }

    public String getTableA() {return tableA;}
    public String getTableB() {return tableB;}
    public String getFieldA() {return fieldA;}
    public String getFieldB() {return fieldB;}
}
