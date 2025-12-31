package com.ge.apm.dao;

import com.ge.apm.domain.PmOrderPreCreate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface PmOrderPreCreateRepository extends GenericRepositoryUUID<PmOrderPreCreate> {

    @Modifying
    @Query(value = "delete from pm_order_pre_create where asset_uid = ?1",nativeQuery = true)
    @Transactional
    public void deleteByAssetUid(String assetUid);


    @Modifying
    @Query(value = "delete from pm_order_pre_create where asset_uid  in (select uid from asset_info where site_uid=?1)",nativeQuery = true)
    @Transactional
    public void  deleteBySiteUid(String siteUid);

    @Modifying
    @Query(value = "delete from pm_order_pre_create where asset_uid  in (select asset_uid from rel_rule_asset where rule_uid=?1 or rule_uid2=?1 or rule_uid1=?1 or rule_uid4=?1)",nativeQuery = true)
    @Transactional
    public void  deleteByPmOrderRuleId(String ruleUid);
}
