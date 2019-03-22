package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.NurseSiteMaintainItem;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
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
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.status in ?2)",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,List<Integer> status);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and date_trunc('day',o.check_date)>o.schedule_date)",nativeQuery=true)
	public Boolean findDelay(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.comment!='' and o.comment is not null)",nativeQuery=true)
	public Boolean findHasComment(String parentId);
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_item set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0,status=2 where id in ?1", nativeQuery = true)
	void completeMaintainItem(Set<String> ids,String signature,String modifyUid,Date modifyDate);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item where id in ?1", nativeQuery = true)
	void deleteMaintainItem(Set<String> ids);
}
