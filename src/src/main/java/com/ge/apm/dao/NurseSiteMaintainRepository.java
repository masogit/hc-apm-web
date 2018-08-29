package com.ge.apm.dao;

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
	
	@Query(value="select o.* from nurse_site_maintain o where o.config_id=?1 and o.status=?2",nativeQuery=true)
	public List<NurseSiteMaintain> findByConfigAndStatus(String configId,Integer status);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultData(Set<String> orgUids,Date startDate,Date endDate);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.status = ?4 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataByStatus(Set<String> orgUids,Date startDate,Date endDate,Integer status);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataByStatus(Set<String> orgUids,Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.status!=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataChecked(Set<String> orgUids,Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.status = ?4 and o.config_id = ?5 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDefaultDataByStatus(Set<String> orgUids,Date startDate,Date endDate,Integer status,String configId);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(Set<String> orgUids,Date startDate,Date endDate,String configId,Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId, Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId);
	
	@Query(value="select o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDataByClinicalOrgUid(List<String> listOrgUid,Date startDate,Date endDate);
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain set signature_id = ?2, modify_by = ?3, check_date=?4, modify_date=?4, signature_source_type=0, status=2 where id in ?1", nativeQuery = true)
	void completeMaintain(Set<String> ids,String signature,String modifyUid,Date modifyDate);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain where id in ?1", nativeQuery = true)
	void deleteMaintain(Set<String> ids);
}

