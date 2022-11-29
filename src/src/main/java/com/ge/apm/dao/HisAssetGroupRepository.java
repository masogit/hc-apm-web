package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAssetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HisAssetGroupRepository extends JpaRepository<HisAssetGroup,String> {

    @Query(value = "select * from his_asset_group where name = ?1", nativeQuery = true)
    HisAssetGroup findByName(String assetGroupName);

    @Query(value = "select name from his_asset_group where uuid = ?1", nativeQuery = true)
    String findNameByUuid(String uuid);
}
