package com.ge.apm.service.constant;

public interface PmConstant {

    String PM_APPROVAL_STATUS_INITIAL = "0";//初始化(未提交审核)
    String PM_APPROVAL_STATUS_SUBMIT_FOR_APPROVAL = "1";//保养(老流程)审核中(不走多级审核,走老流程)
    String PM_APPROVAL_STATUS_AGREE = "2";//保养审批同意状态
    String PM_APPROVAL_STATUS_DISAGREE = "3";//保养审批不同意状态
    String PM_APPROVAL_STATUS_PARTIAL = "4";//保养部分审批同意
    String PM_APPROVAL_STATUS_PENDING = "5";//保养多级审批审核中(走多级审核,不走老流程)

}
