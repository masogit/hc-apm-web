package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetPurchaseTemplateManage;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/11/2
 * @description :
 */
public interface BudgetPurchaseTemplateManageRepository extends GenericRepositoryUUID<BudgetPurchaseTemplateManage> {

    List<BudgetPurchaseTemplateManage> findByTenantUID(String tenantUid);

}
