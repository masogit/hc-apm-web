package com.ge.apm.dao;

import com.ge.apm.domain.AssetLifecycle;
import com.ge.apm.domain.AssetLifecycleEvent;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


public interface AssetLifecycleRepository extends GenericRepositoryUUID<AssetLifecycle> {

    @Query(value="select al.* from asset_lifecycle al where  al.asset_uid = ?1 and current_status = ?2 order by create_time desc  limit 1", nativeQuery = true)
    AssetLifecycle selectByAssetUidAndStatusOne(String assetUid, int status);
}