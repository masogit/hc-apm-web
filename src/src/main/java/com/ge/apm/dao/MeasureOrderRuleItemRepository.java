package com.ge.apm.dao;

import com.ge.apm.domain.MeasureOrderRuleItem;
import com.ge.apm.domain.PmOrderRuleItem;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MeasureOrderRuleItemRepository extends GenericRepository<MeasureOrderRuleItem> {

	List<MeasureOrderRuleItem> findByTenantUID(String tenantUID);

	List<MeasureOrderRuleItem> findByIdIn(List<Integer> ids);

	@Query(value = "select min(period) from measure_order_rule_item where tenant_uid = ?1 and id in ?2",nativeQuery = true)
	Integer fetchMinPeriod(String tenantUID, List<Integer> ids);

}
