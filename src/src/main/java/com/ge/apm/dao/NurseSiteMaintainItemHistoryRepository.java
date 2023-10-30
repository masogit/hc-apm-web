package com.ge.apm.dao;

import com.ge.apm.domain.NurseSiteMaintainItemHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by 503035627
 */
public interface NurseSiteMaintainItemHistoryRepository extends GenericRepositoryUUID<NurseSiteMaintainItemHistory> {
	
	@Query(value="select o.* from nurse_site_maintain_item_history o where o.parent_id=?1",nativeQuery=true)
	public List<NurseSiteMaintainItemHistory> findMaintainItemByParentId(String parentId);

	@Query(value="select o.* from nurse_site_maintain_item_history o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainItemHistory> findMaintainItemByParentId(Collection<String> parentIds);

	@Query(value="select o.* from nurse_site_maintain_item_history o where o.parent_id=?1 order by o.id limit ?2 offset ?3*?2",nativeQuery=true)
	public List<NurseSiteMaintainItemHistory> findMaintainItemByParentId(String parentId,int pageSize,int page);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item_history o where o.parent_id=?1 and o.status=?2)",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,int status);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item_history o where o.parent_id=?1 and o.status in (?2) )",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,List<Integer> statusArr);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item_history o where o.parent_id=?1 and date_trunc('day',o.check_date)>o.schedule_date)",nativeQuery=true)
	public Boolean findDelay(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item_history o where o.parent_id=?1 and o.comment!='' and o.comment is not null)",nativeQuery=true)
	public Boolean findHasComment(String parentId);

	public List<NurseSiteMaintainItemHistory> findByAssetUid(String assetUid);

	@Query(value="select o.* from nurse_site_maintain_item_history o where o.asset_uid=?1 and o.parent_id in ?2",nativeQuery=true)
	public List<NurseSiteMaintainItemHistory> findByParentIdAndAssetUid(String assetUid,Set<String> parentIds);
	//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期


}
