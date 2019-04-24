package com.ge.apm.dao;

import com.ge.apm.domain.RelMeasureRule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface RelMeasureRuleRepository extends GenericRepositoryUUID<RelMeasureRule> {

	@Transactional
	@Modifying
	public void deleteByAssetUIDIn(List<String> deleteIds);

	@Query("select r.assetUID from RelRuleAsset r where r.tenantUID = ?1")
	public List<String> findAssetUidsByTenantUID(String tenantUID);

	public RelMeasureRule findByAssetUID(String uid);

	@Query(value = "select r.asset_uid from rel_measure_rule r where r.tenant_uid = ?1 and r.rule_uid1 is not null",nativeQuery = true)
	public List<String> findRuleUid1ByLevelAndTenantUID(String tenantUID);
	
	@Query(value = "select r.asset_uid from rel_measure_rule r where r.tenant_uid = ?1 and r.rule_uid2 is not null",nativeQuery = true)
	public List<String> findRuleUid2ByLevelAndTenantUID(String tenantUID);
	
	@Query(value = "select r.asset_uid from rel_measure_rule r where r.tenant_uid = ?1 and r.rule_uid is not null",nativeQuery = true)
	public List<String> findRuleUid3ByLevelAndTenantUID(String tenantUID);


	@Query(value = "select r.* from rel_measure_rule r where (r.rule_uid = ?1 or r.rule_uid2 = ?1)",nativeQuery = true)
    List<RelMeasureRule> findRelRuleAssets(String uid);
}
