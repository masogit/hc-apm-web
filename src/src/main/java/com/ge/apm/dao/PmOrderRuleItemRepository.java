package com.ge.apm.dao;

import java.util.List;

import com.ge.apm.domain.PmOrderRuleItem;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleItemRepository extends GenericRepository<PmOrderRuleItem> {

	List<PmOrderRuleItem> findByTenantUID(String tenantUID);

	List<PmOrderRuleItem> findByIdIn(List<Integer> ids);

}
