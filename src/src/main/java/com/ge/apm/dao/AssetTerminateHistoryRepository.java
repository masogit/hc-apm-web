package com.ge.apm.dao;

import com.ge.apm.domain.AssetTerminateHistory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;


public interface AssetTerminateHistoryRepository extends GenericRepositoryUUID<AssetTerminateHistory> {
    @Query(value="SELECT * FROM asset_terminate_history WHERE asset_uid = ?1 ORDER BY apply_time DESC LIMIT 1",nativeQuery=true)   // 只查询最新的一条报废记录
    AssetTerminateHistory findByAssetUid(String assetUid);
}
