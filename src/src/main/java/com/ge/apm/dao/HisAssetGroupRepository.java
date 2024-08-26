package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAssetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface HisAssetGroupRepository  extends GenericRepositoryUUID<HisAssetGroup> {

    @Query(value = "select * from his_asset_group where name = ?1", nativeQuery = true)
    HisAssetGroup findByName(String assetGroupName);

    @Query(value = "select name from his_asset_group where uuid = ?1", nativeQuery = true)
    String findNameByUuid(String uuid);

    List<HisAssetGroup> findByTenantUid(String tenantUID);

    HisAssetGroup findByNameAndTenantUid(String name, String tenantUID);

    @Query(value = "select * from his_asset_group where uuid = ?1", nativeQuery = true)
    HisAssetGroup findByUuid(String uuid);

    HisAssetGroup findByNameAndTypeAndTenantUid(String name, String type, String tenantUID);
}
