package com.ge.apm.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.NurseSiteMaintain;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainRepository extends GenericRepositoryUUID<NurseSiteMaintain> {
	
	@Query(value="select o.* from nurse_site_maintain o where o.config_id=?1 and o.status in (?2)",nativeQuery=true)
	public List<NurseSiteMaintain> findByConfigAndStatus(String configId,List<Integer> arrStatus);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultData(List<String> orgUids,Date startDate,Date endDate);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.status in (?4) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataByStatus(Set<String> orgUids,Date startDate,Date endDate,List<Integer> arrStatus);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataUnchecked(List<String> orgUids,Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?1 and o.schedule_date<=?2 and o.config_id = ?3 and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataUnchecked(Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?1 and o.schedule_date<=?2 and o.config_id in (?3) and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataUnchecked(Date startDate,Date endDate,Set<String> setConfigId);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.status!=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataChecked(List<String> orgUids,Date startDate,Date endDate,String configId);

	@Query(value="select exists(select 1 from nurse_site_maintain o where o.status!=0 and o.schedule_date=?1 and o.sequence=?2 and o.config_id = ?3)",nativeQuery=true)
	public Boolean findDefaultDataChecked(Date startDate,Integer sequence,String configId);

	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.status in (?4) and o.config_id = ?5 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataByStatus(List<String> orgUids,Date startDate,Date endDate,List<Integer> arrStatus,String configId);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(Set<String> orgUids,Date startDate,Date endDate,String configId,Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId, Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId);
	
	@Query(value="select o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDataByClinicalOrgUid(List<String> listOrgUid,Date startDate,Date endDate);

	@Query(value="select o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid =?1 order by o.schedule_date",nativeQuery=true)
	public List<NurseSiteMaintain> findDataByClinicalOrgUid(String clinicalUid,Date startDate,Date endDate);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0, status=?5 where id = ?1", nativeQuery = true)
	void completeMaintain(String id,String signature,String modifyUid,Date modifyDate,int status);
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain set status=?3 where id in ?1 and status=?2", nativeQuery = true)
	void updateErrorMaintain(Set<String> ids,int originStatus,int newStatus);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain where id in ?1", nativeQuery = true)
	void deleteMaintain(Set<String> ids);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain where id = ?1", nativeQuery = true)
	void deleteMaintain(String id);

	@Query(value="select o.* from nurse_site_maintain o where o.status=0 and o.config_id = ?1 and o.schedule_date=?2 and o.sequence=?3",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataUnchecked(String configId,Date scheduleDate,Integer sequence);

	@Query(value="select o.* from nurse_site_maintain o where o.config_id=?1 and o.status=0 order by o.schedule_date",nativeQuery=true)
	public List<NurseSiteMaintain> findUncheckedByConfig(String configId);

	@Query(value="select count(o.id) from nurse_site_maintain o where o.config_id = ?1",nativeQuery=true)
	public Integer fetchCountByConfigId(String configId);
	
	@Query(value="select o.* from nurse_site_maintain o order by o.schedule_date limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintain> findByLimit(int limit,int offset);
	
	@Query(value="select o.* from nurse_site_maintain o where o.schedule_date<?3 and o.status in (?4) order by o.schedule_date limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintain> findByLimit(int limit,int offset,Date date,List<Integer> arrStatus);
	
	@Query(value="select o.* from nurse_site_maintain o where o.config_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintain> findByConfig(Set<String> configIds);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain where id = ?1", nativeQuery = true)
	void deleteMaintainById(String id);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain a where a.id not in (select b.parent_id from nurse_site_maintain_item b )", nativeQuery = true)
	void deleteInvalid();

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain set check_date=?5,modify_date=?5,status=?6 where schedule_date>=?1 and schedule_date<?2 and id in (select b.parent_id from nurse_site_maintain_item b where b.status=?7 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and (a.status in ?3 or a.is_valid=false or a.is_deleted=true) ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain nsm set check_date=?5,modify_date=?5,status=?6 where schedule_date>=?1 and schedule_date<?2 and exists(select * from nurse_site_maintain_item nsmi where nsmi.parent_id=nsm.id and nsmi.status=?7 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and (ai.status in ?3 or ai.is_valid=false or ai.is_deleted=true) and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.id=nsmi.config_id and nsmc.type=?4))", nativeQuery = true)
	void updateDailyByStatus(Date startDate, Date endDate, Collection<Integer> statusArr, int configType, Date currentDate, int status, int originStatus);

	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultData(Set<String> orgUids,Date startDate,Date endDate);

}

