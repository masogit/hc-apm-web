package com.ge.apm.view.budget;

/**
 * @author Yangfan, Liu
 * @date 4/12/2022 20:14
 */
public interface BudgetConstant {
    Integer BUDGET_APPLICATION_STATUS_PENDING = 1;//未提交
    Integer BUDGET_APPLICATION_STATUS_IN_PROGRESS = 2;//已提交
    Integer BUDGET_APPLICATION_STATUS_APPROVED = 3;//审核通过
    Integer BUDGET_APPLICATION_STATUS_NOT_APPROVED = 4;//审核不通过

    String BUDGET_APPLICATION_NUM_PREFIX = "YSSQ";
}
