package com.ge.apm.dao;

import com.ge.apm.domain.MeasureOrderRule;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import java.util.List;
import java.util.Set;


import com.ge.apm.domain.MeasureOrderRule;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import java.util.Set;

public interface MeasureOrderRuleRepository extends GenericRepository<MeasureOrderRule> {
    List<MeasureOrderRule> findByTenantUIDAndStatus(String tenantUID, Integer status);

    @Query(value = "select content from default_report_template where type = ?1",nativeQuery = true)
    String getDefalutReport(int type);

    MeasureOrderRule findByUid(String ruleUID);

    List<MeasureOrderRule> findByUidIn(List<String> ruleUids);

    @Query(value= "select * from measure_order_rule where uid = (select rule_uid1 from rel_measure_rule where asset_uid = ?1)" ,nativeQuery = true)
    MeasureOrderRule findMeasureOrderRuleLevel1(String uid);

    @Query(value= "select * from measure_order_rule where uid = (select rule_uid2 from rel_measure_rule where asset_uid = ?1)" ,nativeQuery = true)
    MeasureOrderRule findMeasureOrderRuleLevel2(String uid);

    @Query(value= "select * from measure_order_rule where uid = (select rule_uid from rel_measure_rule where asset_uid = ?1)" ,nativeQuery = true)
    MeasureOrderRule findMeasureOrderRuleLevel3(String uid);

    @Query(value= "select por.uid from measure_order_rule por,rel_measure_rule rra where rra.asset_uid in ?1 and por.uid = rra.rule_uid2" ,nativeQuery = true)
    List<String> findMeasureRuleForBatch(Set<String> uidList);
    @Query(value= "select * from measure_order_rule where uid = (select rule_uid from rel_rule_asset where asset_uid = ?1)" ,nativeQuery = true)
    MeasureOrderRule findMeasureOrderRuleByAssetUid(String uid);


}
