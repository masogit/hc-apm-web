package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface HisAssetGroupAssetMatchRepository extends GenericRepositoryUUID<HisAssetGroupAssetMatch> {

    @Query(value = "select * from his_asset_group_asset_match as his where his.asset_group_name = ?1 and his.clinical_dept_name = ?2", nativeQuery = true)
    HisAssetGroupAssetMatch findByAssetGroupAndClinicalId(String assetGroupUid, String clinicalDeptUid);

    @Query(value = "select * from his_asset_group_asset_match as his where his.clinical_dept_name = ?1", nativeQuery = true)
    List<HisAssetGroupAssetMatch> findByClinicalName(String clinicalDeptName);

    @Query(value = "select * from his_asset_group_asset_match as his where his.org_info_uid in ?1", nativeQuery = true)
    List<HisAssetGroupAssetMatch> findByOrgInfoUids(List<String> orgInfoUids);

    @Query("select o from HisAssetGroupAssetMatch o  where o.uuid in ?1 ")
    public List<HisAssetGroupAssetMatch> findByIds(List<String> ids);

    HisAssetGroupAssetMatch findByAssetGroupUidAndClinicalDeptUid(String assetGroupUid, String clinicalDeptUid);
}
