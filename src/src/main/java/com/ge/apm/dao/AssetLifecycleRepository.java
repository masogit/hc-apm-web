package com.ge.apm.dao;

import com.ge.apm.domain.AssetLifecycle;
import com.ge.apm.domain.AssetLifecycleEvent;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;


public interface AssetLifecycleRepository extends GenericRepositoryUUID<AssetLifecycle> {

    @Query(value="select al.* from asset_lifecycle al where  al.asset_uid = ?1 and current_status = ?2 order by create_time desc  limit 1", nativeQuery = true)
    AssetLifecycle selectByAssetUidAndStatusOne(String assetUid, int status);

    @Query(value="select al.* from asset_lifecycle al where  al.asset_uid in ?1 and current_status = ?2 order by create_time desc", nativeQuery = true)
    List<AssetLifecycle> selectByAssetUidsAndStatus(Set<String> assetUids, int status);



    @Query(value="select al.* from asset_lifecycle al inner join (select asset_uid ,max(modify_time) as modify_time from asset_lifecycle where asset_uid in(?1) and current_status = ?2 group by asset_uid ) as alf on al .asset_uid = alf.asset_uid and al.modify_time = alf.modify_time", nativeQuery = true)
    List<AssetLifecycle> selectByAssetUidsAndStatusGroupAssetUid(Set<String> assetUids, int status);
}