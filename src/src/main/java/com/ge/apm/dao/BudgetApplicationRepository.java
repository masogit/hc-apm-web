package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetApplication;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author liyongjian
 */
public interface BudgetApplicationRepository extends GenericRepositoryUUID<BudgetApplication> {

    /**
     * 获取索引
     * @return
     */
    @Query(value = "select max(t.application_num_index) from budget_application t where t.order_type = ?1 and to_char(t.created_date,'yyyyMMdd') = to_char(now(),'yyyyMMdd') ", nativeQuery = true)
    Object getMaxIndex(String orderType);

}
