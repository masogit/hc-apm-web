package com.ge.apm.service.constant;

public interface WorkOrderConstant {

    public static final String PARTS_TYPE_RECEIVING = "1";//库房领用
    public static final String PARTS_TYPE_PURCHASE = "2";//医工采购

    public static final Integer STATUS_FLAG_RECEIVING = 1;
    public static final Integer STATUS_FLAG_PURCHASE = 2;
    public static final Integer STATUS_FLAG_OTHER = 3;

    public static final String APPROVAL_STATUS_INIT = "0";//初始化
    public static final String APPROVAL_STATUS_PENDING = "1";//审批中
    public static final String APPROVAL_STATUS_AGREE = "2";//审批同意
    public static final String APPROVAL_STATUS_DISAGREE = "3";//审批不同意
    public static final String APPROVAL_STATUS_PIECE_AGREE = "4";//部分同意

}
