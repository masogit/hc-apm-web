package com.ge.apm.dao;

import com.ge.apm.domain.AssetLifecycleEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
import java.util.Set;

public interface AssetLifecycleEventRepository extends GenericRepositoryUUID<AssetLifecycleEvent> {

	@Query(value="select ale.* from asset_lifecycle_event ale where ale.event_type = 3 and ale.asset_uid = ?1 limit 4", nativeQuery = true)
	public List<AssetLifecycleEvent> getLifeCycleEventList(String assetUid);

	public List<AssetLifecycleEvent> findByAssetUid(String assetUid);

	@Transactional
	@Modifying
	@Query(value = "update asset_lifecycle_event set check_status=1 where id in ?1", nativeQuery = true)
	void recordErrorData(Set<String> ids);
	
	@Transactional
	@Modifying
	@Query(value = "update asset_lifecycle_event set check_status=0 where id in ?1", nativeQuery = true)
	void recordCorrectData(Set<String> ids);
	
	@Query(value="select distinct o.asset_uid from asset_lifecycle_event o where o.check_status is null and o.event_type=?3 limit ?1 offset ?2*?1",nativeQuery=true)
	public Set<String> findAssetUidByLimit(int limit,int offset,int eventType);
	
	@Query(value="select o.* from asset_lifecycle_event o where o.asset_uid in ?1 and o.event_type=?2",nativeQuery=true)
	public List<AssetLifecycleEvent> findByAssetUid(Set<String> assetUid,int eventType);

	/*public List<ExternalWorkOrder> getExWorkOrderList();*/

	@Query(nativeQuery=true, value= "select le.* from v2_work_order wo, asset_lifecycle_event le where wo.ID = le.event_uid and wo.status != le.status and le.description like '%待关单%' and wo.status = 2")
	public List<AssetLifecycleEvent> getIncorrectedEvent();

}