package com.ge.apm.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.PmOrderRule;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRuleRepository extends GenericRepository<PmOrderRule> {

	List<PmOrderRule> findByTenantUIDAndStatus(String tenantUID, Integer status);

	@Query(value = "select content from default_report_template where type = ?1",nativeQuery = true)
	String getDefalutReport(int type);

	PmOrderRule findByUid(String ruleUID);

	List<PmOrderRule> findByUidIn(List<String> ruleUids);

	@Query(value= "select * from pm_order_rule where uid = (select rule_uid1 from rel_rule_asset where asset_uid = ?1)" ,nativeQuery = true)
	PmOrderRule findPmOrderRuleLevel1(String uid);

	@Query(value= "select * from pm_order_rule where uid = (select rule_uid2 from rel_rule_asset where asset_uid = ?1)" ,nativeQuery = true)
	PmOrderRule findPmOrderRuleLevel2(String uid);

	@Query(value= "select * from pm_order_rule where uid = (select rule_uid from rel_rule_asset where asset_uid = ?1)" ,nativeQuery = true)
	PmOrderRule findPmOrderRuleLevel3(String uid);

	@Query(value= "select por.uid from pm_order_rule por,rel_rule_asset rra where rra.asset_uid in ?1 and por.uid = rra.rule_uid2" ,nativeQuery = true)
	List<String> findPmRuleForBatch(Set<String> uidList);


}
