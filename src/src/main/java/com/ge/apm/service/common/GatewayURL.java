package com.ge.apm.service.common;


public enum GatewayURL {
    //Transaction
    LISt_ALL_DEPTTRANSACTION_LOG("/hcapmassetservice/api/apm/asset/deptTransaction/deptTransactionLog"),
    CREATE_DEPTTRANSACTION_LOG("/hcapmassetservice/api/apm/asset/deptTransaction/deptTransactionLog"),
    DELETE_DEPTTRANSACTION_LOG("/hcapmassetservice/api/apm/asset/deptTransaction/deptTransactionLog/{id}"),
    QUERY_DEPTTRANSACTION_LOG("/hcapmassetservice/api/apm/asset/deptTransaction/deptTransactionLog?from={from}&to={to}&staffId={staffId}&page={page}&pageSize={pageSize}"),
    UPDATE_DEPTTRANSACTION_LOG("/hcapmassetservice/api/apm/asset/deptTransaction/deptTransactionLog/{id}"),
    GET_DEPTRASACTION_LOG_SUMMARY("/hcapmassetservice/api/apm/asset/deptTransaction/summary?from={from}&to={to}"),


    // Security
    GET_AUTHENTICATE_BY_DINGTALK_ID("/api/apm/security/userAccounts/dingTalk/authenticate"),
    GET_AUTHENTICATE_BY_OPEN_ID("/api/apm/security/userAccounts/authenticateWeChat"),
    GET_AUTHENTICATE_BY_USER_PASSWORD("/api/apm/security/userAccounts/authenticateBasic"),
    GET_AUTHENTICATE_BY_USER_PASSWORD_ENCRYPTED("/api/apm/security/userAccounts/authenticateInternal"),
    GET_APMUSER_OR_SYSCLIENT("/api/apm/security/userAccounts/getApmUserOrSysClient"),
    // Objects
    UPLOAD_SINGLE_FILE_BY_URL("/hcapmobjecthubservice/api/apm/objectHub/objects/urlFile"),
    UPLOAD_SINGLE_FILE("/zuul/hcapmobjecthubservice/api/apm/objectHub/objects/single"),
    UPLOAD_SINGLE_COMPRESS_FILE("/zuul/hcapmobjecthubservice/api/apm/objectHub/objects/compressImageSingle"),
    DELETE_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects"),
    DOWNLOAD_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects/download"),
    GET_FILES("/hcapmobjecthubservice/api/apm/objectHub/objects/getByIdIn"),
    GET_NOT_BYTE_FILES("/hcapmobjecthubservice/api/apm/objectHub/objects/getNotByteByIdIn"),
    AUTH_DOWNLOAD_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects/auth/download"),
    SEARCH_FILE("/hcapmobjecthubservice/api/apm/objectHub/objects"),

    COMPRESS_IMAGE("/hcapmobjecthubservice/api/apm/objectHub/objects/compress/{objectId}"),

    // Service Request Service
    SERVICE_REQUEST_CREATE("/hcapmassetservice/api/apm/asset/serviceRequests"),
    SERVICE_NO_ASSET_REQUEST_CREATE("/hcapmassetservice/api/apm/asset/serviceRequestsNoAsset"),
    SERVICE_NO_ASSET_ASSIGNEE_USERS("/hcapmassetservice/api/apm/asset/serviceRequestsNoAsset/assigneeUsers"),
    SERVICE_NO_ASSET_ACTION("/hcapmassetservice/api/apm/asset/serviceRequestsNoAsset/{id}/action"),
    WORK_ORDER_ACTION("/hcapmassetservice/api/apm/asset/workOrders/{workOrderId}/action"),
    CREATE_WORK_ORDER("/hcapmassetservice/api/apm/asset/workOrders"),
    UPDATE_WODETAIL_PRICE("/hcapmassetservice/api/apm/asset/workOrderDetails"),
    //租户的mvs的设备uid获取
    GET_MVSASSECT_UID("/hcapmassetservice/api/apm/asset/mvsassetinfos/byUserVisibleScope"),
    GET_MVSASSECT_UID_STR("/hcapmassetservice/api/apm/asset/mvsassetinfos/byUserVisibleScopeStr"),

    //租户的mvs的设备uid获取
    GET_MVSASSECT_UID_BYSITE("/hcapmassetservice/api/apm/asset/mvsassetinfos/bySite"),
    //租户的mvs的设备uid获取
    GET_MVSASSECT_UID_BYCLINICALDEPT("/hcapmassetservice/api/apm/asset/mvsassetinfos/byClinicalDept"),
    // Asset Owners
    GET_RESPONSER_BY_ASSET("/hcapmassetservice/api/apm/asset/tags/assets/{assetId}/owners"),
    // 处理未完成日常检查
    PROCESS_UN_FINISHED_NURSE_SITE_MAINTAIN("/hcapmassetservice/api/apm/assetScrap/processUnfinishedNurseSiteMaintain/{assetId}"),

    //ACKER
    GET_ACKER_BY_OrderId("/hcapmassetservice/api/apm/asset/{workOrderId}/acker"),

    GET_WORKFLOW_CONFIG("/hcapmassetservice/api/apm/asset/workflowConfig/{assetId}"),

    BEFORE_CREATE_INVENTORY_ORDER("/hcapmassetservice/api/apm/asset/inventoryorders/orgSelection"),

    SUMBIT_CREATE_INVENTORY_ORDER("/hcapmassetservice/api/apm/asset/inventoryorders"),

    FINISH_INVENTORY_ORDER("/hcapmassetservice/api/apm/asset/inventoryorders/{orderId}"),

    // Send Message to Wechat
    SEND_WECHAT_MSG("/hcapmgateway/wechat/message"),
    // Asset Installation process
    SUBMIT_ARRIVAL_LIST("/hcapmworkflowservice/api/apm/installation/{arrivalListId}/arrived"),
    SUBMIT_ARRIVAL_LIST_ITEM("/hcapmworkflowservice/api/apm/installation/{arrivalListItemId}/install"),
    AUDIT_IN_STOCKED("/hcapmworkflowservice/api/apm/installation/{arrivalListItemId}/auditInStocked"),
    REASSIGN_ARRIVAL_LIST_ITEM_OWNER("/hcapmworkflowservice/api/apm/installation/{arrivalListItemId}/reAssign"),
    // PM Order URL
    PM_ORDER_ACTION("/hcapmworkflowservice/api/apm/pmOrders/{pmOrderId}/action"),
    PM_REQUEST_CREATE("/hcapmworkflowservice/api/apm/pmOrders"),
    GET_APPROVE_BY_ASSET("/hcapmworkflowservice/api/apm/pmOrders/{assetUid}/approver"),
    PM_ORDER_STEPS("/hcapmworkflowservice/api/apm/pmOrders/{pmOrderId}/pmOrderSteps"),
    CREATE_PM_ORDER_BY_BATCH("/hcapmworkflowservice/api/apm/pmOrders/batch"),
    CREATE_PM_ORDER_BY_CONTINUEFAILEDPMORDER("/hcapmworkflowservice/api/apm/pmOrders/continueFailedPmOrder"),
    CREATE_MEASURE_ORDER_BY_BATCH("/hcapmworkflowservice/api/apm/measuringOrders/batch"),
    AUTO_GENERATE_FINANCING_NUM("/hcapmassetservice/api/apm/asset/assetinfos/finacing_num"),
    AUTO_GENERATE_BATCH_FINANCING_NUM("/hcapmassetservice/api/apm/asset/assetinfos/batch/finacing_num"),
    CANCEL_PM_ORDER("/hcapmworkflowservice/api/apm/pmOrders/{pmOrderId}/cancel"),
    PM_GET_ACKER("/hcapmassetservice/api/apm/asset/pmorders/{pmOrderId}/acker"),
    PM_GET_ACKER_FOR_BATCH_COMPLETE("/hcapmassetservice/api/apm/asset/pmorders/acker"),
    PM_GET_ACKER_FOR_MULTIPLE_PM_ORDERS("/hcapmassetservice/api/apm/asset/pmorders/batch/acker_with_pm_ids"),

    // MEASURE Order URL
    MEASURE_ORDER_ACTION("/hcapmworkflowservice/api/apm/measuringOrders/{orderId}/action"),
    MEASURE_REQUEST_CREATE("/hcapmworkflowservice/api/apm/measuringOrders"),
    MEASURE_GET_APPROVE_BY_ASSET("/hcapmworkflowservice/api/apm/measuringOrders/{assetUid}/approver"),
    MEASURE_ORDER_STEPS("/hcapmworkflowservice/api/apm/measuringOrders/{OrderId}/orderSteps"),
    MEASURE_CREATE_MEASURE_ORDER_BY_BATCH("/hcapmworkflowservice/api/apm/measuringOrders/batch"),
    MEASURE_CANCEL_MEASURE_ORDER("/hcapmworkflowservice/api/apm/measuringOrders/{OrderId}/cancel"),
    MEASURE_BATCH_TAKE_ORDER("/hcapmworkflowservice/api/apm/measuringOrders/batch"),
    //批量取消
    PM_BATCH_CANCEL("/hcapmworkflowservice/api/apm/pmOrders/cancel"),
    // User Schedule
    USER_VACATION_CREATE("/hcapmassetservice/api/apm/userschedule/userVacation"),
    // Batch Operation
    BATCH_TAKE_ORDER("/hcapmworkflowservice/api/apm/pmOrders/batch"),
    EXTERNAL_ASSET_OCR("/hcapmworkflowservice/api/apm/asset/probation/ocrCreated"),
    //supplier Api
    SUPPLIER_WX_CODE_IMAGE("/hcapmgateway/api/apm/weChat/wxaCodeImage"),
    SUPPLIER_WX_QRCODE_IMAGE("/hcapmgateway/api/apm/weChat/qrCodeImage"),
    MSM_SEND_CODE("/hcapmgateway/api/apm/login/valid/sendSmsCode/{phone}"),
    MSM_GET_CODE("/hcapmgateway/api/apm/login/valid/getSmsCode/{phone}"),

    //备件增删
    ACCESSADD("/hcapmassetservice/api/apm/asset/workOrderDetails/{assetUid}"),
    ACCESSDEL("/hcapmassetservice/api/apm/asset/workOrderDetails/{id}"),

    ACCESSAD_MEASURING("/hcapmassetservice/api/apm/asset/MeasuringOrderDetails/{assetUid}"),
    ACCESSDEL_MEASURING("/hcapmassetservice/api/apm/asset/MeasuringOrderDetails/{id}"),

    // Allocation Center URLs
    SHARED_ASSET_PUSH_MESSAGE("/hcapmworkflowservice/api/apm/allocation/remote/message/{requestId}"),
    ASSIGNEE_LIST("/hcapmworkflowservice/api/apm/allocation/assigneeList?requestId={requestId}"),
    ALLOCATION_ON_DUTY("/hcapmworkflowservice/api/apm/allocation/onDuty"),
    REMOTE_ASSIGN_REQUEST("/hcapmworkflowservice/api/apm/allocation/assign?requestId={requestId}&assigneeUID={assigneeUID}"),
    REMOTE_ACCEPT_RENT("/hcapmworkflowservice/api/apm/allocation/remote/acceptRent/{requestId}"),
    LEASE_SHORTCUT("/hcapmworkflowservice/api/apm/allocation/rent/shortcut"),
    RETURN_SHORTCUT("/hcapmworkflowservice/api/apm/allocation/return/shortcut"),
    GET_VOUCHER_NUM("/hcapmworkflowservice/api/apm/allocation/voucherNum"),
    REJECT_APPLY("/hcapmworkflowservice/api/apm/allocation/rent/rejectApply"),
    REJECT_RETURN("/hcapmworkflowservice/api/apm/allocation/rent/rejectReturn"),

    BATCH_FILE_UPLOAD("/hcapmreportservice/api/apm/batchFile/upload"),

    PART_ORDER_SEND_EMAIL("/hcapmreportservice/api/apm/report/partOrder/sendHtmlForPartOrder"),
    PART_ORDER_SEND_LAST_DAY_EMAIL("/hcapmreportservice/api/apm/report/partOrder/sendHtmlForLastDayRemind"),
    PART_ORDER_SEND_RETURN_EMAIL("/hcapmreportservice/api/apm/report/partOrder/sendHtmlForReturn"),
    PART_ORDER_SEND_UN_ORDER("/hcapmreportservice/api/apm/report/partOrder/sendHtmlForUnOrder"),
    PART_ORDER_SEND_MESSAGE("/hcapmreportservice/api/apm/report/partOrder/sendMessage"),
    PART_ORDER_SEND_PROBE_LETTER_EMAIL("/hcapmreportservice/api/apm/report/partOrder/sendHtmlForProbeLetter"),
    PART_ORDER_SEND_WECHAT_MESSAGE("/hcapmassetservice/api/apm/zkb/partOrder/sendWeChatMessages"),
    PART_ORDER_SEND_WECHAT_BACK_MESSAGE("/hcapmassetservice/api/apm/zkb/partOrder/sendWeChatBackMessages"),
    PART_ORDER_SEND_WECHAT_REMIND_LETTER("/hcapmassetservice/api/apm/zkb/partOrder/sendWeChatRemindLetter"),
    PART_ORDER_SEND_WECHAT_RETURN_AUDIT("/hcapmassetservice/api/apm/zkb/partOrder/sendWeChatReturnAudit"),
    PART_ORDER_SEND_EMAIL_ENGINEER("/hcapmreportservice/api/apm/report/partOrder/sendHtmlToEngineerByGeAdmin"),
    INTEGRAL_ORDER_SEND_MESSAGE("/hcapmreportservice/api/apm/report/integralOrder/sendMessage"),
    INTEGRAL_ORDER_SEND_EMAIL("/hcapmreportservice/api/apm/report/integralOrder/sendEmail"),
    INTEGRAL_ORDER_SEND_GE_ADMIN_EMAIL("/hcapmreportservice/api/apm/report/integralOrder/sendGeAdminHtml"),
    INTEGRAL_ORDER_SEND_WECHAT_MESSAGE("/hcapmassetservice/api/apm/zkb/integralOrder/sendWeChatMessages"),

    COURSE_TRAIN_SEND_WE_CHART("/hcapmassetservice/api/apm/zkb/courseTrain/sendWeChatMessages"),
    COURSE_TRAIN_SEND_EMAIL_AUDIT("/hcapmreportservice/api/apm/report/courseTrain/sendHtmlForAudit"),
    SEND_EMAIL_FOR_TEXT("/hcapmreportservice/api/apm/report/email/sendEmail"),
    SEND_MESSAGE_FOR_TEXT("/hcapmreportservice/api/apm/report/email/sendMessage"),
    PART_ORDER_QR_CODE_JOB("/hcapmassetservice/api/apm/zkb/partOrder/qrCodeJob"),
    PART_ORDER_QR_CODE_JOB_SCHEDULE("/hc-apm-schedule/job/api/929"),
    APPROVAL_TO_SEND_WE_CHAT("/hcapmassetservice/api/apm/approvals/submit/approvalToSendWeChat"),
    PURCHASE_BUDGET_CONFIG_SEND_WX_MESSAGE("/hcapmassetservice/api/apm/budget/config/sendWeChatMessages"),

    PURCHASE_SEND_WX_MESSAGE("/hcapmassetservice/api/apm/budget/config/sendWeChatMessagesPurchase"),
    RESULT_PURCHASE_SEND_WX_MESSAGE("/hcapmassetservice/api/apm/budget/config/sendWeChatMessagesPurchaseRes"),

    SEND_WE_CHAT("/hcapmassetservice/api/apm/approvals/submit/sendWeChat"),

    SUPPLIER_FE_BY_ASSETUID("/hcapmsuppliermanager/api/apm/supplierManager/userAccount/findSupplierFE/{assetUid}"),

    SMS_VERIFICATION_CODE("/hcapmreportservice/api/apm/report/partOrder/sendTheVerificationCode"),
    SMS_SEND_MAIL("/hcapmreportservice/api/apm/report/partOrder/sendMail"),
    FE_PART_ORDER_SEND_EMAIL("/hcapmreportservice/api/apm/report/fepartOrder/sendMail"),
    FE_PART_ORDER_SEND_MESSAGE("/hcapmreportservice/api/apm/report/fepartOrder/sendMessage"),
    FE_PART_ORDER_SEND_WECHART("/hcapmassetservice/api/apm/zkb/zkbfepartorder/sendWechatMessage"),
    FE_PART_ORDER_SEND_WECHART_REMIND_LETTER("/hcapmassetservice/api/apm/zkb/zkbfepartorder/sendWeChatRemindLetter"),
    CONTRACT_COURSE_SCHEDULE_MESSAGE("/hcapmreportservice/api/apm/zkb/zkbCourseSchedule/sendMessage"),
    CONTRACT_COURSE_SCHEDULE_SEND_MESSAGE_BY_TELEPHONE("/hcapmreportservice/api/apm/zkb/zkbCourseSchedule/sendMessageByTelephone"),

    // 不良事件详情
    GET_ADVERSE_EVENTS_INFO("/hcapmassetservice/api/apm/asset/adverse/events/getDetailByUid/{uid}"),
    GET_ADVERSE_EVENTS_INFO_BY_ID("/hcapmassetservice/api/apm/asset/adverse/events/getDetail/{id}"),
    // // 审核不良事件上报工单 （通过、退回）
    APPROVE_ADVERSE_EVENTS("/hcapmassetservice/api/apm/asset/adverse/events/approveAdverseEvent"),
    // 上报人不良事件列表
    ADVERSE_EVENTS_LIST("/hcapmassetservice/api/apm/asset/adverse/events/reporterListToWeb"),
    ADVERSE_EVENTS_LIST_ALL("/hcapmassetservice/api/apm/asset/adverse/events/queryAllAdverseEvent"),
    // 不良事件作废
    ADVERSE_EVENTS_INVALID("/hcapmassetservice/api/apm/asset/adverse/events/invalidAdverseEvent"),
    // 不良事件暂存上报
    ADD_ADVERSE_EVENTS("/hcapmassetservice/api/apm/asset/adverse/events/addAdverseEvent"),

    ADVERSE_EVENTS_WITHDRAWN("/hcapmassetservice/api/apm/asset/adverse/events/adverseEventWithdrawn"),

    // 审核人不良事件列表
    APPROVE_ADVERSE_EVENTS_LIST("/hcapmassetservice/api/apm/asset/adverse/events/approveListToWeb"),
    // 审核暂存
    APPROVE_TEMP_SAVE_ADVERSE_EVENTS("/hcapmassetservice/api/apm/asset/adverse/events/approveStagingAdverseEvent"),
    // 不良事件发送邮件
    ADVERSE_EVENTS_SENDMAIL("/hcapmassetservice/api/apm/asset/adverse/events/sendMail"),
    // 不良事件pdf
    ADVERSE_EVENTS_EXPORT_PDF("/hcapmassetservice/api/apm/asset/adverse/events/exportPdf"),

    RESET_USER_TO_REDIS("/hcapmgateway/api/apm/security/userAccounts/resetUser/{loginName}"),
    REMOVE_USER_FROM_REDIS("/hcapmgateway/api/apm/security/userAccounts/removeUser/{loginName}"),
    USER_SIGNATURE("/hcapmmasterdataservice/api/apm/masterData/userSignature"),
    GET_CONFIG_BY_KEY_AND_SITE("/hcapmmasterdataservice/api/apm/masterData/configItem/getByKeyAndSite"),
    ASSET_CLASSIFICATION_MODEL_SAVE("/hcapmmasterdataservice/api/apm/masterData/assetClassificationModel/saveModel"),
    ASSET_CLASSIFICATION_MODEL_EDIT("/hcapmmasterdataservice/api/apm/masterData/assetClassificationModel/editModel"),
    ASSET_CLASSIFICATION_MODEL_DELETE("/hcapmmasterdataservice/api/apm/masterData/assetClassificationModel/deleteModel"),
    ASSET_CLASSIFICATION_MODEL_GETRESMAPBYASSET("/hcapmmasterdataservice/api/apm/masterData/assetClassificationModel/getResMapByAsset"),
    ASSET_CLASSIFICATION_MODEL_UPLOAD_FILE("/hcapmmasterdataservice/api/apm/masterData/assetClassificationModel/uploadFileModel"),

    //es查询设备修改历史记录分页
    ELASTICSEARCH_QUERY("/hcapmelasticservice/elasticSearch/query"),
    SEARCH_ASSET_HISTORY_LIST("/hcapmelasticservice/assetHistory/searchAssetHistoryList"),
    INSERT_ASSET_HISTORY("/hcapmelasticservice/assetHistory/insertAssetHistory"),
    INSERT_PHYSICAL_DATA_DEL("/hcapmelasticservice/physicalDataDel/insertPhysicalDataDel"),
    ELASTICSEARCH_INSERT("/hcapmelasticservice/elasticSearch/insert"),
    INSERT_BUSINESS_ERROR_LOG("/hcapmelasticservice/businessErrorLog/insertBusinessErrorLog"),
    // jimureport
    JI_MU_REPORT("/apmReport/jmreport/view"),

    ASSET_SCRAP_APPLY("/hcapmassetservice/api/apm/assetScrap/apply"),
    ASSET_CHECK_ORDER_CONDITION("/hcapmassetservice/api/apm/asset/assetinfos/checkOrderCondition"),
    ASSET_DEPARTMENT_COVERT_APPLY("/hcapmassetservice/api/apm/departmentConvert/assetDepartmentConvert"),

    // davinci
    DAVINCI_EXTERNAL("/davinci/externalSharePageLink"),

    // davinci
    DAVINCI_GET_TOKEN("/davinci/tokenToToken"),
    // 科室间调剂
    SECONDMENT_PUSH_WECHAT_MSG("/hcapmworkflowservice/api/apm/secondment/remote/message/{orderId}/{type}"),
    SECONDMENT_REMINDER_TO_RETURN("/hcapmworkflowservice/api/apm/secondment/reminderToReturn/{orderId}"),
    SECONDMENT_GENERATE_NO("/hcapmworkflowservice/api/apm/secondment/getNo"),
    ORDERNUMBER_NO("/hcapmworkflowservice/api/apm/secondment/getNo"),  //orderNumber
    // clear redis cache
    CLEAN_ORG_CACHE("/api/apm/configCache/cleanOrgInfoCache/{uid}"),
    CLEAN_TENANT_CACHE("/api/apm/configCache/cleanTenantCache/{uid}"),
    CLEAN_CACHE_NAMES("/api/apm/configCache/cleanCacheNames"),

    CLEAN_I18N_MESSAGE_CACHE_BY_SITE_ID("/hcapmmasterdataservice/api/apm/masterData/i8nMessages/clearI18nMessageCacheBySiteId"),
    // OCR action
    OCR_IMAGE("/hcapmgateway/api/apm/ocr/recognition/readImageToObject"),
    OCR_DOCUMENT("/hcapmgateway/api/apm/ocr/recognition/readFileToObject"),
    OCR_RECOGNITION_BASE64("/hcapmgateway/api/apm/ocr/recognition"),
    OCR_RECOGNITION_IMAGE("/hcapmgateway/api/apm/ocr/recognition/image"),
    OCR_RECOGNITION_DOC("/hcapmgateway/api/apm/ocr/recognition/doc"),
    OCR_DOC_RESULT("/hcapmgateway/api/apm/ocr/doc/result/{id}"),

    FILES_UPLOAD_AND_WORKFLOWS_RUN("/hcapmgateway/api/apm/model/filesUploadAndWorkflowsRun"),

    // site to site auto ack
    SITE_TO_SITE_AUTO_ACK("/hcapmworkflowservice/api/apm/siteToSiteAllocation/autoAck"),
    PLATE_RECOGNITION("/hcapmassetservice/api/apm/asset/assetinfos/plateRecognition/{objectId}"),
    IMAGE_CONTENT_EXTRACTION("/hcapmassetservice/api/apm/asset/assetinfos/plateRecognition/image/{objectId}"),
    DIFY_CREATE_SHARE_MESSAGE("/hcapmgateway/api/apm/model/share"),
    DIFY_GET_SHARE_MESSAGE("/hcapmgateway/api/apm/model/shareMessage"),
    ;
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
