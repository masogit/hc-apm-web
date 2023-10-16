package com.ge.apm.dao;

import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/10/12
 * @description :
 */
public interface PurchaseBudgetConfigRepository extends GenericRepositoryUUID<PurchaseBudgetConfig> {

    List<PurchaseBudgetConfig> findByYearAndTenantUID(String year,String tenantUID);
    List<PurchaseBudgetConfig> findByYearAndTenantUIDAndIdIsNot(String year,String id,String tenantUID);

    @Query(value = "select * from purcahse_budget_config c where c.tenantUid =?1 and c.site_uid = ?2 and c.start_date <= ?3 and c.end_date >= ?3", nativeQuery = true)
    List<PurchaseBudgetConfig> queryEffiveConfig(String tenantUid, String siteUid, Date date);
}
