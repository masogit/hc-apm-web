package com.ge.apm.service.constant;

public interface ApprovalConstant {
    //审批类型
    public static final Integer ROLE_APPROVAL_TYPE = 1;//角色审批
    public static final Integer USER_APPROVAL_TYPE = 2;//用户审批
    //审批业务类型businessType
    public static final String BUSINESS_TYPE_SPARE_PARTS_REQUEST = "申领单";//备件申领单
    public static final String BUSINESS_TYPE_SPARE_PARTS_PURCHASE = "申购单";//备件申购单
    String BUSINESS_TYPE_SPARE_PARTS_RETURN = "退货单";
    public static final String BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION = "档案信息修改";

    /**
     * @date 2020/7/14 13:58
     * 审批状态--待审批
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_WAITING = 0;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME = "未审批";

    /**
     * @date 2020/7/14 13:59
     * 审批状态--审批中
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_ING = 1;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_ING_NAME = "审批中";

    /**
     * @date 2020/7/14 13:59
     * 审批状态--审批通过
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_AGREE = 2;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME = "审批通过";
    /**
     * @date 2020/7/14 14:00
     * 审批状态--审批不同意
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE = 3;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME = "审批不通过";
    public static final String APPROVAL_NAME = "级审批人待审批中";

    /**
     *
     * 审批状态--审批退回
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_RETURN= 4;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_RETURN_NAME = "审批退回";




    /**
     * 当前所在审批级别 完全通过时
     */
    public static final Integer APPROVAL_NOW_AGREE = 0;
    /**
     * 当前所在审批级别 不通过时
     */
    public static final Integer APPROVAL_NOW_DISAGREE = -1;

    public static final String APPROVAL_NAME_SUCCESS = "审批已通过";

    public static final String APPROVAL_NAME_DISAGREE = "审批不通过";

    public static final String WX_MESSAGE_APPROVAL="【审批提醒】";
    /**
     * 审批不同意发送微信内容给发起人
     */
    public static final String WX_MESSAGE_DISAGREE_1 = "您发起的[";
    public static final String WX_MESSAGE_DISAGREE_2 = "]单号: ";
    public static final String WX_MESSAGE_DISAGREE_3 = " 审批失败请及时查看,修改后可再次发起,谢谢!!!";
    /**
     * 审批同意发送微信内容给发起人
     */
    public static final String WX_MESSAGE_AGREE_1 = "您发起的[";
    public static final String WX_MESSAGE_AGREE_2 = "]单号: ";
    public static final String WX_MESSAGE_AGREE_3 = " 审批已通过请及时查看,谢谢!!!";
    /**
     * 给轮到审批的人发送微信内容
     */
    public static final String WX_MESSAGE_APPROVAL_1 = "您有一条待审批数据";
    public static final String WX_MESSAGE_APPROVAL_2 = " 单号: ";
    public static final String WX_MESSAGE_APPROVAL_3 = " 请及时查看并进行审批操作,谢谢!!!";


    String APPROVAL_ASSET_REPAIR_CHi = "设备维修";//设备维修 assetRepair
    String APPROVAL_ASSET_MAINTAIN_CHi = "设备保养";//设备保养 assetMaintain
    String APPROVAL_ASSET_BIG_REPAIR = "设备大修";
    String APPROVAL_ASSET_BORROW_CHI = "设备借用";
    String APPROVAL_ASSET_RECEIVE_CHI = "设备领用";
    String APPROVAL_ASSET_BORROW_RETURN_CHI = "设备借用归还";
    String APPROVAL_ASSET_RECEIVE_RETURN_CHI = "设备领用归还";
    String APPROVAL_ASSET_TRANSFER_CHI = "设备转移";

    //审批单号头部
    String APPROVAL_NUM = "SP";

    //审批业务类型
    String APPROVAL_PARTS_STOCK = "partsStock";//备件库存
    String APPROVAL_ASSET_INFO_MODIFY = "assetInfoModify";//档案信息修改
    String APPROVAL_ASSET_OVERHAUL = "assetOverhaul";//设备大修
    String APPROVAL_ASSET_BORROW = "assetBorrow";//设备借用
    String APPROVAL_ASSET_RECEIVE = "assetReceive";//设备领用
    String APPROVAL_ASSET_REPAIR = "assetRepair";//设备维修
    String APPROVAL_ASSET_MAINTAIN = "assetMaintain";//设备保养
    String APPROVAL_ASSET_BORROW_RETURN = "assetBorrowReturn";//设备借用归还
    String APPROVAL_ASSET_RECEIVE_RETURN = "assetReceiveReturn";//设备领用归还
    String APPROVAL_ASSET_TRANSFER = "assetTransfer";//设备转移
    String APPROVAL_ADVERSE_EVENTS = "adverseEvents"; // 不良事件上报
    String APPROVAL_BUDGET_MANAGEMENT = "budgetManagement"; // 预算管理
    String APPROVAL_PURCHASE_MANAGEMENT = "purchaseManagement"; // 采购管理
    String APPROVAL_MAINTENANCE_CONTRACT_APPLY_PURCHASE = "maintenanceContractApplyPurchase"; // 维保合同申购
    String APPROVAL_MAINTENANCE_CONTRACT_APPLY_PURCHASE_CHI = "维保合同申购"; //
    String APPROVAL_ASSET_SCRAP = "assetScrap"; // 资产报废流程
    String APPROVAL_ASSET_DEPARTMENT_CONVERT = "assetDepartmentConvert"; // 资产转科流程
    String APPROVAL_BATH_ASSET_DEPARTMENT_CONVERT = "bathAssetDepartmentConvert"; // 批量设备转科
    String APPROVAL_ASSET_SCRAP_CHI = "设备报废"; // 设备报废
    String APPROVAL_ASSET_DEPARTMENT_CONVERT_CHI = "设备转科"; // 设备转科
    String APPROVAL_BATH_ASSET_DEPARTMENT_CONVERT_CHI = "批量设备转科"; // 批量设备转科
    String APPROVAL_ASSET_EQUIPMENT_ADJUSTMENT  = "equipmentAdjustment"; // 设备调剂

    int BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT = 1;//备件申领单
    int BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT = 2;//备件申购单
    int BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT = 3;//档案信息修改
    int BUSINESS_TYPE_ASSET_OVERHAUL_INT = 4;//设备大修
    int BUSINESS_TYPE_ASSET_BORROW = 5;//借用申请
    int BUSINESS_TYPE_ASSET_RECEIVE = 6;//领用申请
    int BUSINESS_TYPE_ASSET_REPAIR_INT = 7;//设备维修
    int BUSINESS_TYPE_ASSET_MAINTAIN_INT = 8;//设备保养
    int BUSINESS_TYPE_ASSET_BORROW_RETURN = 9;//借用归还申请
    int BUSINESS_TYPE_ASSET_RECEIVE_RETURN = 10;//领用归还申请
    int BUSINESS_TYPE_ASSET_TRANSFER = 11;//设备转移
    int BUSINESS_TYPE_SPARE_PARTS_RETURN_INT = 12; //备件申退单
    int BUSINESS_TYPE_ADVERSE_EVENTS = 13; // 不良事件上报
    int BUSINESS_TYPE_BUDGET_MANAGEMENT = 14; // 预算管理
    int BUSINESS_TYPE_ASSET_SCRAP = 15; // 设备报废流程
    int BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT = 16; // 资产转科
    int BUSINESS_TYPE_PURCHASE_MANAGEMENT = 17; // 采购管理
    int BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT = 18; // 设备调剂
    int BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT = 19; // 批量资产转科
    int BUSINESS_TYPE_MAINTENANCE_CONTRACT_APPLY_PURCHASE = 20; //维保合同申购管理

    String BUSINESS_TYPE_BUDGET_MANAGEMENT_NAME = "申购管理"; // 预算管理
    String BUSINESS_TYPE_PURCHASE_MANAGEMENT_NAME = "采购管理"; // 预算管理


    String BUSINESS_TYPE_ADVERSE_EVENTS_NAME = "不良事件上报"; // 不良事件上报
    String BUSINESS_TYPE_ASSET_SCRAP_NAME = "设备报废"; // 资产报废

    String BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT_NAME = "设备转科"; // 资产转科
    String BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT_NAME = "批量设备转科"; // 资产转科
    String BUSINESS_TYPE_ASSET_EQUIPMENT_ADJUSTMENT_NAME = "设备调剂"; // 设备调剂

    /**
     * 档案信息修改 是否需要审批标识
     * 1.资产维修审批中 2.资产维修不需要审批
     */
    public static final int ASSET_INTO_APPROVE_STATUS_YES = 1;
    public static final int ASSET_INTO_APPROVE_STATUS_NO = 2;

    //资产分类
    String GROUP_MEDICAL = "01"; //医疗组
    String GROUP_OFFICE = "02"; //办公组
    String GROUP_INFORMATION = "03"; //信息组

    public static final int BUSINESSSTATUS_COMPLETE_NOT_APPROVAL = 4;
    public static final int BUSINESSSTATUS_COMPLETE_APPLY = 5;
    public static final int BUSINESSSTATUS_COMPLETE = 6;

    String MULTILEVEL_AUDITOR = "MultiLevelAuditor";

//    static final String ALL_GROUP = "allGroup"; // TODO ACCT 不需要资产分类,测试稳定后删除
    static final String BUSINESS_TYPE = "businessType";

    String OPERATE_TYPE_CREATE = "create";
    String OPERATE_TYPE_EDIT = "edit";
    String  OPERATE_TYPE_VIEW = "view";

    public static final String APPROVAL_SEPARATOR = ",";

    //维修保养工单审批备件,同意状态
    Integer WORK_DETAIL_STATUS_DISAGREE = 0;//不同意
    Integer WORK_DETAIL_STATUS_AGREE = 1;//同意



    Integer SCRAP_STATUS_APPLY=1 ;//申请中
    Integer SCRAP_STATUS_UNDER_REVIEW=2 ;//审核中
    Integer SCRAP_STATUS_SUCCESS=3;//完成
    Integer SCRAP_STATUS_APPLY_REFUSE=4;//申请拒绝
    Integer SCRAP_STATUS_REVIEW_REFUSE=5;//审核拒绝
}
