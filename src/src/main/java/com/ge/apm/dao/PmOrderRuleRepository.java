package com.ge.apm.dao;

import java.util.List;

import com.ge.apm.domain.PmOrderRule;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleRepository extends GenericRepository<PmOrderRule> {

	List<PmOrderRule> findByTenantUIDAndStatus(String tenantUID, Integer status);

}
