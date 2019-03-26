package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainItem;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 503035627
 */
public interface NurseSiteMaintainItemRepository extends GenericRepositoryUUID<NurseSiteMaintainItem> {
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.parent_id=?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId);
	
	@Query(value="select count(o.id) from nurse_site_maintain_item o where o.parent_id=?1",nativeQuery=true)
	public Integer findCountMaintainItemByParentId(String parentId);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.parent_id=?1 order by o.id limit ?2 offset ?3",nativeQuery=true)
	public List<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId,int pageSize,int page);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findMaintainItemByParentId(Set<String> parentIds);
	
	@Query(value="select o.id from nurse_site_maintain_item o where o.parent_id in ?1",nativeQuery=true)
	public Set<String> findIdsByParentId(Set<String> parentIds);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.status=?2)",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,int status);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.status in (?2) )",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,List<Integer> statusArr);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and date_trunc('day',o.check_date)>o.schedule_date)",nativeQuery=true)
	public Boolean findDelay(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.comment!='' and o.comment is not null)",nativeQuery=true)
	public Boolean findHasComment(String parentId);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)<=o.schedule_date and o.comment is not null and o.comment !='' order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError5ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)>o.schedule_date and o.comment is not null and o.comment !='' order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError6ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)>o.schedule_date and (o.comment is null or o.comment ='') order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError7ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=3 and date_trunc('day',o.check_date)<=o.schedule_date order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError8ByLimit(int limit,int offset,Date date);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item where id in ?1", nativeQuery = true)
	void deleteMaintainItem(Set<String> ids);
}
