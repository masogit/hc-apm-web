package com.ge.apm.service.constant;

public interface SimplePartsConstant {

    /**
     * @author csh
     * @createTime 2020/6/11 15:37
     * @description 备件申领单
     */
    static final int PARTS_REQUEST_FORM = 1;
    static final String PARTS_REQUEST_FORM_NAME = "备件申领";
    /**
     * @author csh
     * @createTime 2020/6/11 15:38
     * @description 备件申购单
     */
    static final int PARTS_PURCHASE_FORM = 2;
    static final String PARTS_PURCHASE_FORM_NAME = "备件申购";

    /**
     * @author csh
     * @createTime 2020/6/11 15:38
     * @description 备件申调拨
     */
    static final int PARTS_TRANSFER_FORM = 3;
    static final String PARTS_TRANSFER_FORM_NAME = "备件调拨";
    /**
     * @author csh
     * @createTime 2020/6/11 15:38
     * @description 备件库存盘点
     */
    static final int PARTS_PHYSICAL_COUNT_FORM = 4;
    static final String PARTS_PHYSICAL_COUNT_FORM_NAME = "备件库存盘点";

    static final int PARTS_RETURN_FORM = 5;  //归还入库
    static final String PARTS_RETURN_FORM_NAME = "备件归还入库";
    /**
     * @author csh
     * @createTime 2020/6/11 15:28
     * @description 备件单据已同意-审核通过
     */
    static final int PARTS_PROPOSER_STATUS_AGREE = 1;

    /**
     * @author csh
     * @createTime 2020/6/11 15:28
     * @description 备件单据未同意-审核不同意
     */
    static final int PARTS_PROPOSER_STATUS_DISAGREE = 2;

    /**
     * @date 2020/7/29 16:12
     * 审批中
     */
    static final int PARTS_PROPOSER_STATUS_IN_PROGRESS = 3;

    /**
     * 已入库/已出库
     */
    static final int PARTS_TRANSACTIONED = 1;

    /**
     *  未入库/未出库
     */
    static final int PARTS_NOT_TRANSACTIONED = 2;

    /**
     * @date 2020/7/27 17:49
     * 已入库
     */
    static final String PARTS_STATUS_IN_STOCK = "已入库";

    /**
     * @date 2020/7/27 17:49
     * 未入库
     */
    static final String PARTS_STATUS_NOT_IN_STOCK = "未入库";

    /**
     * @date 2020/7/27 17:49
     * 已出库
     */
    static final String PARTS_STATUS_OUT_STOCK = "已出库";

    /**
     * @date 2020/7/27 17:49
     * 未出库
     */
    static final String PARTS_STATUS_NOT_OUT_STOCK = "未出库";

    static final int TRANSACTION_TYPE_IN_STOCK = 0;    //入库标志
    static final int TRANSACTION_TYPE_OUT_STOCK = 1;   //出库标志

    int WAREHOUSE_DISABLED = 0;
    int WAREHOUSE_ENABLED = 1;

    String WAREHOUSE_DISABLED_NAME = "禁用";
    String WAREHOUSE_ENABLED_NAME = "启用";

    /**
     * @params
     * @return
     * @author csh
     * @createTime 2020/6/11 15:30
     * @description 备件管理员角色
     */
    static final String PARTS_ADMIN_MANAGER = "SparePartsAdmin";

    /**
     * 备件本地使用的无真实意义的orgId.用作前后台交流使用
     */
    static final String PARTS_LOCAL_ORG_ID = "000";

    static final String OPERATE_TYPE_CREATE = "create";
    static final String OPERATE_TYPE_EDIT = "edit";
    static final String OPERATE_TYPE_VIEW = "view";
    static final String BUSINESS_TYPE = "businessType";

    String VIRTUAL_LOCATOR_ID = "VIRTUAL_LOCATOR";
    String VIRTUAL_LOCATOR_NAME = "虚拟仓位";

    static final String ASSET_FOR_BORROW = "B";
    static final String ASSET_FOR_RECEIVE = "R";
    //维修工单
    static final Integer WORK_ORDER = 0;
    //保养工单
    static final Integer PM_ORDER = 1;

    public static final String WX_MESSAGE_PARTS_STOCK_IN_OR_OUT="【备件出入库提醒】";

    String I18N_REQUEST_TYPE = "simplePartsRequestType";
}


