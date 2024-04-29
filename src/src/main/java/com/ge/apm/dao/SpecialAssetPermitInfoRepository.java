package com.ge.apm.dao;

import com.ge.apm.domain.SpecialAssetPermitInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SpecialAssetPermitInfoRepository extends GenericRepositoryUUID<SpecialAssetPermitInfo> {

    @Modifying
    @Transactional
    @Query(value = "delete from special_asset_permit_info where special_asset_id =?1  ", nativeQuery = true)
    int  deleteBySpecialAssetId(String id);

    @Modifying
    @Transactional
    @Query(value = "delete from special_asset_permit_info where permit_info_uid =?1  ", nativeQuery = true)
    int  deleteByPermitUid(String id);


    @Modifying
    @Transactional
    @Query(value = "delete from special_asset_permit_info where permit_info_uid =?1  ", nativeQuery = true)
    int  deleteByPermitUidIn(List<String> id);


    @Query(value = "select *  from special_asset_permit_info where permit_info_uid =?1  ", nativeQuery = true)
    List<SpecialAssetPermitInfo> findByPermitUid(String uid);
}
