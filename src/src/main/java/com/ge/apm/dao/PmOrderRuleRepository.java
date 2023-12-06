package com.ge.apm.dao;

import com.ge.apm.domain.PmOrderRule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import java.util.Set;

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

	@Query(value= "select * from pm_order_rule where uid = (select rule_uid4 from rel_rule_asset where asset_uid = ?1)" ,nativeQuery = true)
	PmOrderRule findPmOrderRuleLevel4(String uid);

	@Query(value= "select por.uid from pm_order_rule por,rel_rule_asset rra where rra.asset_uid in ?1 and por.uid = rra.rule_uid2" ,nativeQuery = true)
	List<String> findPmRuleForBatch(Set<String> uidList);

	@Query(value= "select por.* from pm_order_rule por where por.uid in ?1" ,nativeQuery = true)
	List<PmOrderRule> getPmOrderRulesByUids(List<String> ruleUids);

	@Query(value= "select * from pm_order_rule por where por.tenant_uid = ?1" ,nativeQuery = true)
	List<PmOrderRule> findPmRuleByTenant(String tenantUid);

	@Query(value= "select por.* from v2_pm_order vpo inner join rel_rule_asset rra on vpo.pm_num=?1 and vpo.tenant_uid=?2 and vpo.asset_uid = rra.asset_uid inner join pm_order_rule por on por.uid in (rra.rule_uid,rra.rule_uid1,rra.rule_uid2,rra.rule_uid4) and vpo.rule_level = por.level" ,nativeQuery = true)
	List<PmOrderRule> findPmRuleByPmNum(String pmNum,String TenantUID);

	@Query(value= "select por.* from v2_pm_order vpo inner join rel_rule_asset rra on vpo.pm_num in (?1) and vpo.tenant_uid=?2 and vpo.asset_uid = rra.asset_uid inner join pm_order_rule por on por.uid in (rra.rule_uid,rra.rule_uid1,rra.rule_uid2,rra.rule_uid4) and vpo.rule_level = por.level" ,nativeQuery = true)
	List<PmOrderRule> findPmRuleByPmNumBatch(List<String> pmNums,String TenantUID);

	@Modifying
	@Transactional
	@Query(value = "delete from pm_order_rule where uid in ?1",nativeQuery = true)
	Integer deleteById(List<String> collect);

	@Override
	List<PmOrderRule> findAll();

}
