package com.ge.apm.service.constant;

public interface WorkOrderConstant {

    public static final String PARTS_TYPE_RECEIVING = "1";//库房领用
    public static final String PARTS_TYPE_PURCHASE = "2";//医工采购

    public static final Integer STATUS_FLAG_RECEIVING = 1;
    public static final Integer STATUS_FLAG_PURCHASE = 2;
    public static final Integer STATUS_FLAG_OTHER = 3;

    public static final String APPROVAL_STATUS_INIT = "0";//维修初始化(未审批)
    public static final String APPROVAL_STATUS_SUBMIT_FOR_APPROVAL = "1";//维修(老流程)审核中(不走多级审核,走老流程)
    public static final String APPROVAL_STATUS_AGREE = "2";//维修审批同意
    public static final String APPROVAL_STATUS_DISAGREE = "3";//维修审批不同意
    public static final String APPROVAL_STATUS_PARTIAL_AGREE = "4";//维修部分同意
    public static final String APPROVAL_STATUS_PENDING = "5";//维修多级审批审核中(走多级审核,不走老流程)

//    public static final String DESIGNATED_USER = "北京积水潭医院"; 替换配置configItem.enableAuditGroupConfiguration
    public static final String MEDICAL_TEAM_GROUP = "医工组长分组";
    public static final String CORE_GROUP_MEMBERS = "核心组成员";
}
