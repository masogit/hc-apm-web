package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.PmOrderRuleItem;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleItemRepository extends GenericRepository<PmOrderRuleItem> {

	List<PmOrderRuleItem> findByTenantUID(String tenantUID);

	List<PmOrderRuleItem> findByIdIn(List<Integer> ids);

	@Query(value = "select min(period) from pm_order_rule_item where tenant_uid = ?1 and id in ?2",nativeQuery = true)
	Integer fetchMinPeriod(String tenantUID, List<Integer> ids);

}
