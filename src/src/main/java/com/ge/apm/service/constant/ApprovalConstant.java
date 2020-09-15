package com.ge.apm.service.constant;

public interface ApprovalConstant {
    //审批类型
    public static final Integer ROLE_APPROVAL_TYPE = 1;//角色审批
    public static final Integer USER_APPROVAL_TYPE = 2;//用户审批
    //审批业务类型businessType
    public static final String BUSINESS_TYPE_SPARE_PARTS_REQUEST = "申领单";//备件申领单
    public static final String BUSINESS_TYPE_SPARE_PARTS_PURCHASE = "申购单";//备件申购单
    /**
     * @date 2020/7/14 13:58
     * 审批状态--待审批
     */
    public static final int MULTI_LEVEL_STATUS_APPROVAL_WAITING = 0;
    public static final String MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME = "待审批";

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

    //审批单号头部
    static final String APPROVAL_NUM = "SP";

    //审批业务类型
    static final String APPROVAL_PARTS_STOCK = "partsStock";
}
