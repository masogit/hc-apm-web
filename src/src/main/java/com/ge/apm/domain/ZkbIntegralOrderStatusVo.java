package com.ge.apm.domain;

import lombok.Data;

@Data
public class ZkbIntegralOrderStatusVo {

    public enum BtnEnum{
        Audit,Submit
    }


    private boolean showPendingStatusBtn = true;

    private long pendingQty = 0;

    private long pendingEngineerQualificationQty = 0;

    private long pendingManageQty = 0;

    private long pendingOrderQty = 0;

    private boolean showStatusBtn = true;

    private String quotaTips;

    private Integer currentScore = 0;

    private boolean scoreCanApply = true;

    //审批页面状态
    private boolean showManage = false;

    private boolean showCommercial = false;

    private boolean showOrder=false;

    private boolean showEngineer=false;

    private boolean canEditFlag=false;

    private boolean whetherDeductEdit = false;

    private boolean haveTrainCourseFlag = false;

    private boolean toolBoxAuditBtnFlag = true;

    /**
     * 空代表显示
     * Audit 代表审批显示按钮
     * Submit 代表提交显示按钮
     */
    private String showBtn = "";

}
