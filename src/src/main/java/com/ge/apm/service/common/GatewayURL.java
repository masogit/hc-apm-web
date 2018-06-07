package com.ge.apm.service.common;


public enum GatewayURL {

    // Security
    GET_AUTHENTICATE_BY_OPEN_ID("/api/apm/security/userAccounts/authenticateWeChat"),
    GET_AUTHENTICATE_BY_USER_PASSWORD("/api/apm/security/userAccounts/authenticateBasic"),
    // Objects
    UPLOAD_SINGLE_FILE_BY_URL("/hcapmobjecthubservice/api/apm/objectHub/objects/urlFile"),
    UPLOAD_SINGLE_FILE("/zuul/hcapmobjecthubservice/api/apm/objectHub/objects/single"),
    DELETE_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects"),
    DOWNLOAD_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects/download"),
    // Service Request Service
    SERVICE_REQUEST_CREATE("/hcapmassetservice/api/apm/asset/serviceRequests"),
    WORK_ORDER_ACTION("/hcapmassetservice/api/apm/asset/workOrders/{workOrderId}/action"),
    CREATE_WORK_ORDER("/hcapmassetservice/api/apm/asset/workOrders"),
    // Asset Owners
    GET_RESPONSER_BY_ASSET("/hcapmassetservice/api/apm/asset/tags/assets/{assetId}/owners"),
    // Send Message to Wechat
    SEND_WECHAT_MSG("/hcapmgateway/wechat/message"),
    // Asset Installation process
    SUBMIT_ARRIVAL_LIST("/hcapmworkflowservice/api/apm/installation/{arrivalListId}/arrived"),
    SUBMIT_ARRIVAL_LIST_ITEM("/hcapmworkflowservice/api/apm/installation/{arrivalListItemId}/install"),
    REASSIGN_ARRIVAL_LIST_ITEM_OWNER("/hcapmworkflowservice/api/apm/installation/{arrivalListItemId}/reAssign"),
    // PM Order URL
    PM_ORDER_ACTION("/hcapmworkflowservice/api/apm/pmOrders/{pmOrderId}/action"),
    PM_REQUEST_CREATE("/hcapmworkflowservice/api/apm/pmOrders"),
    GET_APPROVE_BY_ASSET("/hcapmworkflowservice/api/apm/pmOrders/{assetUid}/approver"),
    PM_ORDER_STEPS("/hcapmworkflowservice/api/apm/pmOrders/{pmOrderId}/pmOrderSteps"),
    CREATE_PM_ORDER_BY_BATCH("/hcapmworkflowservice/api/apm/pmOrders/batch"),
    AUTO_GENERATE_FINANCING_NUM("/hcapmassetservice/api/apm/asset/assetinfos/finacing_num"),
    // User Schedule
    USER_VACATION_CREATE("/hcapmassetservice/api/apm/userschedule/userVacation"),
    // Batch Operation
    BATCH_TAKE_ORDER("/hcapmworkflowservice/api/apm/pmOrders/batch"),
    // Allocation Center URLs
    SHARED_ASSET_PUSH_MESSAGE("/hcapmassetservice/api/apm/allocation/remote/message/{requestId}"),
    REMOTE_ACCEPT_RENT("/hcapmassetservice/api/apm/allocation/remote/acceptRent/{requestId}");

    private final String url;

    GatewayURL(String url) {
        this.url = url;
    }

//    public String toGatewayURL(String baseURL) {
//        return baseURL + url;
//    }

    @Override
    public String toString() {
        return url;
    }
}
