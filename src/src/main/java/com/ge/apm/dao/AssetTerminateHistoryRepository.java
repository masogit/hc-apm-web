package com.ge.apm.dao;

import com.ge.apm.domain.AssetTerminateHistory;
import webapp.framework.dao.GenericRepositoryUUID;


public interface AssetTerminateHistoryRepository extends GenericRepositoryUUID<AssetTerminateHistory> {
    AssetTerminateHistory findByAssetUid(String assetUid);
}
