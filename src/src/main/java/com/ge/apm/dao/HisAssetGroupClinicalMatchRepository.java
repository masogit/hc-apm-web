package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface HisAssetGroupClinicalMatchRepository extends GenericRepositoryUUID<HisAssetGroupClinicalMatch> {


    @Query(value = "select asset_uid from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1", nativeQuery = true)
    List<String> findAssetIds(String uid);

    @Modifying
    @Transactional
    @Query(value = "delete from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1 and asset_uid = ?2", nativeQuery = true)
    void deleteAsset(String id, String hisAssetGroupClinicalId);

    @Modifying
    @Transactional
    @Query(value = "delete from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1 ", nativeQuery = true)
    void deleteAssetByHisAssetGroupClinicalId(String hisAssetGroupClinicalId);

    @Modifying
    @Transactional
    @Query(value = "delete from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1", nativeQuery = true)
    void deleteById(String hisAssetGroupUidAndClinicalDeptUid);

    @Query(value = "select * from his_asset_group_clinical_match where his_asset_group_uid in ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findHisAssetGroupClinicalMatchByHisAssetGroupUId(List<String> hisAssetGroupUid);

    @Query(value = "select * from his_asset_group_clinical_match where asset_group_clinical_match_id in ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findAssetInIds(List<String> hisAssetGroupUid);

    @Query(value = "select * from his_asset_group_clinical_match where asset_uid= ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findByAssetUid(String assetUid);

    @Query(value = "select * from his_asset_group_clinical_match where his_asset_group_uid= ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findByAssetGroupUid(String assetGroupUid);

    @Query(value = "select * from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findByAssetGroupClinicalMatchId(String hisAssetGroupUidAndClinicalDeptUid);

    @Query(value = "select count(*) from his_asset_group_clinical_match where asset_uid in ?1 and his_asset_group_uid != ?2", nativeQuery = true)
    int countAssetsInOtherGroups(List<String> assetUids, String currentGroup);

    @Query(value = "select count(*) from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1", nativeQuery = true)
    Integer countByGroupUidAndDeptUid(String assetGroupClinicalId);

    @Query(value = "select count(*) from his_asset_group_clinical_match where his_asset_group_uid = ?1", nativeQuery = true)
    Integer countByGroupUid(String assetGroupUid);



    @Query(value = "select * from his_asset_group_clinical_match where asset_group_clinical_match_id = ?1", nativeQuery = true)
    List<HisAssetGroupClinicalMatch> findByAssetGroupClinicalId(String uid);

    List<HisAssetGroupClinicalMatch>  findByHisAssetGroupUIdAndOrgInfoUid(String groupUid, String siteUid);



    @Modifying
    @Transactional
    @Query(value = "delete from his_asset_group_clinical_match where his_asset_group_uid = ?1    and org_info_uid=?2", nativeQuery = true)
    void deleteAssetByHisAssetGroupAndOrgInfoUid(String groupUid, String siteUid);


    @Query(value = "select count(*) from his_asset_group_clinical_match where his_asset_group_uid = ?1    and org_info_uid=?2", nativeQuery = true)
    Integer countByGroupUidAndOrgInfoUid(String groupUid, String siteUid);



    @Query(value = "select hag.uuid from his_asset_group_clinical_match hagcm left join   his_asset_group hag   on  hagcm.his_asset_group_uid=hag.uuid   where hag.type='Cost'  and    hagcm.asset_uid=?1  and hagcm.tenant_uid=?2  group by  hag.uuid", nativeQuery = true)
    List<String> getHisAssetGroupUidByAssetUid(String asset_uid,String tenantUid);


}
