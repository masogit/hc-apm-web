package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.PmOrderRule;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleRepository extends GenericRepository<PmOrderRule> {

	List<PmOrderRule> findByTenantUIDAndStatus(String tenantUID, Integer status);

	@Query(value = "select content from default_report_template where type = ?1",nativeQuery = true)
	String getDefalutReport(int type);

}
