package com.ge.apm.service.utils;

import com.ge.apm.service.constant.ApprovalConstant;

public class MultilApproveStatusMapping {

    public static String getApprovalNodeStatus(Integer approvalNodeStatus){
        if(null == approvalNodeStatus){
            return null;
        }
        switch (approvalNodeStatus){
            case 0:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING_NAME;
            case 2:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE_NAME;
            case 3:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE_NAME;
            case 4:
                return ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_RETURN_NAME;
        }
        return "";
    }
}
