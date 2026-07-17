package com.ge.apm.dao;

import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

public interface PlanFilingPeriodRepository extends GenericRepositoryUUID<PlanFilingPeriod> {

    List<PlanFilingPeriod> findByYearAndTenantUID(String year,String tenantUID);

    @Query(value = "select * from plan_filing_period c where c.tenant_uid =?1 and c.start_date <= ?2 and c.end_date >= ?2", nativeQuery = true)
    List<PlanFilingPeriod> queryEffectConfig(String tenantUid,  Date date);
}
