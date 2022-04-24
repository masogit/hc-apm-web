package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetApplication;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author Yangfan, Liu
 */
public interface BudgetApplicationRepository extends GenericRepositoryUUID<BudgetApplication> {

    /**
     * get last application number
     *
     * @param tenantUID tenantUID
     * @return last applicationNum
     */
    @Query(nativeQuery = true, value = "select max(application_num) from budget_application where tenant_uid = ?1")
    String lastApplicationNum(String tenantUID);

    /**
     * find by tenant uid and application number
     *
     * @param tenantUID      tenantUID
     * @param applicationNum applicationNum
     */
    BudgetApplication findByTenantUIDAndApplicationNum(String tenantUID, String applicationNum);
}
