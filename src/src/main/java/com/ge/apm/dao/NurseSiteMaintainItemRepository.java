package com.ge.apm.dao;

import java.util.*;

import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.parent_id=?1 order by o.id limit ?2 offset ?3*?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId,int pageSize,int page);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findMaintainItemByParentId(Set<String> parentIds);
	
	@Query(value="select o.id from nurse_site_maintain_item o where o.parent_id in ?1",nativeQuery=true)
	public Set<String> findIdsByParentId(Set<String> parentIds);
	
	@Query(value="select o.id from nurse_site_maintain_item o where o.parent_id = ?1",nativeQuery=true)
	public Set<String> findIdsByParentId(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.status=?2)",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,int status);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.status in (?2) )",nativeQuery=true)
	public Boolean findExistsItemStatus(String parentId,List<Integer> statusArr);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and date_trunc('day',o.check_date)>o.schedule_date)",nativeQuery=true)
	public Boolean findDelay(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_item o where o.parent_id=?1 and o.comment!='' and o.comment is not null)",nativeQuery=true)
	public Boolean findHasComment(String parentId);
	
	//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)<=o.schedule_date and o.comment is not null and o.comment !='' order by o.id limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError5ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)>o.schedule_date and o.comment is not null and o.comment !='' order by o.id limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError6ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=2 and date_trunc('day',o.check_date)>o.schedule_date and (o.comment is null or o.comment ='') order by o.id limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError7ByLimit(int limit,int offset,Date date);
	
	@Query(value="select o.* from nurse_site_maintain_item o where o.schedule_date<?3 and o.status=3 and date_trunc('day',o.check_date)<=o.schedule_date order by o.id limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainItem> findError8ByLimit(int limit,int offset,Date date);

	public List<NurseSiteMaintainItem> findByAssetUid(String assetUid);

	@Query(value = "SELECT DISTINCT item.* FROM nurse_site_maintain_item item LEFT JOIN nurse_site_maintain_data itemdata ON item.id = itemdata.parent_id WHERE item.asset_uid = ?1 AND item.schedule_date > ?2 AND itemdata.check_value IS NULL ",nativeQuery = true)
	public List<NurseSiteMaintainItem> findNoCheckedByAssetUidAAndScheduleDate(String assetUid,Date now);

	@Query(value = "SELECT parent_id FROM nurse_site_maintain_config_item WHERE asset_uid = ?1",nativeQuery = true)
	public List<String> findConfigItemByAssetUid(String assetUid);

	@Query(value = "SELECT count(*) FROM nurse_site_maintain_config_item WHERE parent_id = ?1",nativeQuery = true)
	public int findConfigItemCountByParentId(String parentId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item where id in ?1", nativeQuery = true)
	void deleteMaintainItem(Set<String> ids);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item where parent_id = ?1", nativeQuery = true)
	void deleteMaintainItemByParentId(String parentId);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item where parent_id in ?1", nativeQuery = true)
	void deleteMaintainItemByParentId(Set<String> parentId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_item a where a.id not in (select b.parent_id from nurse_site_maintain_data b )", nativeQuery = true)
	void deleteInvalid();

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM nurse_site_maintain_data WHERE parent_id = ?1", nativeQuery = true)
	void deleteDataByParentId(String parentId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM nurse_site_maintain WHERE id = ?1", nativeQuery = true)
	void deleteMaintainById(String parentId);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM nurse_site_maintain_config_item WHERE asset_uid = ?1", nativeQuery = true)
	void deleteMaintainConfigItemByAssetUid(String assetUid);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM nurse_site_maintain_config WHERE id = ?1", nativeQuery = true)
	void deleteMaintainConfigById(String id);

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain_item set comment=?5 where schedule_date>=?1 and schedule_date<?2 and id in (select b.id from nurse_site_maintain_item b where b.status=0 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and a.status = ?3 ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain_item nsmi set comment=?5 where nsmi.status = 0 and nsmi.schedule_date>=?1 and nsmi.schedule_date<?2 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and ai.status=?3 and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.id=nsmi.config_id and nsmc.type=?4)", nativeQuery = true)
	void updateDailyByStatus(Date startDate,Date endDate,int status,int configType,String comment);

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain_item set comment=?5 where schedule_date>=?1 and schedule_date<?2 and id in (select b.id from nurse_site_maintain_item b where b.status=0 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and a.is_valid = ?3 ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain_item nsmi set comment=?5 where nsmi.status = 0 and nsmi.schedule_date>=?1 and nsmi.schedule_date<?2 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and ai.is_valid=?3 and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.id=nsmi.config_id and nsmc.type=?4)", nativeQuery = true)
	void updateDailyByValid(Date startDate,Date endDate,boolean status,int configType,String comment);

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain_item set comment=?5 where schedule_date>=?1 and schedule_date<?2 and id in (select b.id from nurse_site_maintain_item b where b.status=0 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and a.is_deleted = ?3 ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain_item nsmi set comment=?5 where nsmi.status = 0 and nsmi.schedule_date>=?1 and nsmi.schedule_date<?2 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and ai.is_deleted=?3 and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.id=nsmi.config_id and nsmc.type=?4)", nativeQuery = true)
	void updateDailyByDelete(Date startDate,Date endDate,boolean status,int configType,String comment);

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain_item set check_date=?5,modify_date=?5,status=?6 where schedule_date>=?1 and schedule_date<?2 and id in (select b.id from nurse_site_maintain_item b where b.status=0 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and (a.status in ?3 or a.is_valid=false or a.is_deleted=true) ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain_item nsmi set check_date=?5,modify_date=?5,status=?6,is_finish = ?7 where nsmi.status = 0 and nsmi.schedule_date>=?1 and nsmi.schedule_date<?2 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and (ai.status in ?3 or ai.is_valid=false or ai.is_deleted=true) and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.id=nsmi.config_id and nsmc.type=?4)", nativeQuery = true)
	void updateDailyByStatus(Date startDate, Date endDate, Collection<Integer> statusArr, int configType, Date currentDate, int status,boolean isFinish);

	@Query(value="select o.id from nurse_site_maintain_item o where o.parent_id in ?1 and o.status=0",nativeQuery=true)
	public Set<String> findIdsByParentId0(Set<String> parentIds);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_item set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0,status=2,update_by=?6,is_finish=?7 where id in ?1 and schedule_date>=?5 and (comment is null or comment='')", nativeQuery = true)
	void completeMaintainItem2(Set<String> ids,String signature,String modifyUid,Date modifyDate,Date delayDate,String updateByUid,Boolean isFinish);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_item set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0,status=5,update_by=?6,is_finish=?7 where id in ?1 and schedule_date>=?5 and (comment is not null and comment!='')", nativeQuery = true)
	void completeMaintainItem5(Set<String> ids,String signature,String modifyUid,Date modifyDate,Date delayDate,String updateByUid,Boolean isFinish);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_item set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0,status=6,update_by=?6,is_finish=?7 where id in ?1 and schedule_date<?5 and (comment is not null and comment!='')", nativeQuery = true)
	void completeMaintainItem6(Set<String> ids,String signature,String modifyUid,Date modifyDate,Date delayDate,String updateByUid,boolean isFinish);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_item set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0,status=7 ,update_by=?6,is_finish=?7 where id in ?1 and schedule_date<?5 and (comment is null or comment='')", nativeQuery = true)
	void completeMaintainItem7(Set<String> ids,String signature,String modifyUid,Date modifyDate,Date delayDate,String updateByUid,Boolean isFinish);

	@Query(value="select o.* from nurse_site_maintain_item o where o.asset_uid=?1 and o.parent_id in ?2",nativeQuery=true)
	public List<NurseSiteMaintainItem> findByParentIdAndAssetUid(String assetUid,Set<String> parentIds);
	//0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期

	@Query(value="select * from nurse_site_maintain_item where asset_uid=?1 and schedule_date <= ?2 order by schedule_date desc limit 5 ",nativeQuery = true)
	List<NurseSiteMaintainItem> getMaintainDataList(String assetUid,Date date);

	@Query(value="select * from nurse_site_maintain_item where asset_uid=?1 and schedule_date >=?2 and schedule_date<=?3 ",nativeQuery = true)
	List<NurseSiteMaintainItem> getMaintainItemDataList(String assetUid,Date startDate,Date endDate);

	@Query(value = "SELECT count(*) FROM nurse_site_maintain_item WHERE is_finish = FALSE AND parent_id = ?1",nativeQuery = true)
	int noCheckCount(String parentId);

	List<NurseSiteMaintainItem> findByIdIn(Collection<String> ids);

	@Query(value = "SELECT DISTINCT item.* FROM nurse_site_maintain_item item LEFT JOIN nurse_site_maintain_data itemdata ON item.id = itemdata.parent_id WHERE item.asset_uid = ?1  AND itemdata.check_value IS NULL ",nativeQuery = true)
	public List<NurseSiteMaintainItem> findNoCheckedByAssetUid(String assetUid );

}
