package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;

import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetLifecycleEventRepository extends GenericRepositoryUUID<AssetLifecycleEvent> {


	@Query(value="select ale.* from asset_lifecycle_event ale where ale.event_type = 3 and ale.asset_uid = ?1 limit 4", nativeQuery = true)
	public List<AssetLifecycleEvent> getLifeCycleEventList(String assetUid);
        
        
	public List<AssetLifecycleEvent> findByAssetUid(String assetUid);

	@Transactional
	@Modifying
	@Query(value = "update asset_lifecycle_event set check_status=1 where id in ?1", nativeQuery = true)
	void recordErrorData(Set<String> ids);
	
	@Query(value="select distinct o.asset_uid from asset_lifecycle_event o where o.create_time>=?1 and o.create_time<?2 limit ?3 offset ?4*?3",nativeQuery=true)
	public Set<String> findAssetUidByLimit(Date start,Date end,int limit,int offset);
	
	@Query(value="select o.* from asset_lifecycle_event o where o.asset_uid in ?1",nativeQuery=true)
	public List<AssetLifecycleEvent> findByAssetUid(Set<String> assetUid);

	/*public List<ExternalWorkOrder> getExWorkOrderList();*/



}