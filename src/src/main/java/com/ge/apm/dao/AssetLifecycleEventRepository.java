package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.AssetLifecycleEvent;

import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetLifecycleEventRepository extends GenericRepositoryUUID<AssetLifecycleEvent> {


	@Query(value="select ale.* from asset_lifecycle_event ale where ale.event_type = 3 and ale.asset_uid = ?1 limit 4", nativeQuery = true)
	public List<AssetLifecycleEvent> getLifeCycleEventList(String assetUid);
        
        
	public List<AssetLifecycleEvent> findByAssetUid(String assetUid);


	/*public List<ExternalWorkOrder> getExWorkOrderList();*/



}