package com.ge.apm.dao;

import com.ge.apm.domain.NurseSiteMaintainHistory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainHistoryRepository extends GenericRepositoryUUID<NurseSiteMaintainHistory> {

	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultData(List<String> orgUids,Date startDate,Date endDate);

	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultDataUnchecked(List<String> orgUids,Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?1 and o.schedule_date<=?2 and o.config_id = ?3 and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultDataUnchecked(Date startDate,Date endDate,String configId);
	
	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?1 and o.schedule_date<=?2 and o.config_id in (?3) and o.status=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultDataUnchecked(Date startDate,Date endDate,Set<String> setConfigId);
	
	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.status!=0 order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultDataChecked(List<String> orgUids,Date startDate,Date endDate,String configId);

	@Query(value="select exists(select 1 from nurse_site_maintain_history o where o.status!=0 and o.schedule_date=?1 and o.sequence=?2 and o.config_id = ?3)",nativeQuery=true)
	public Boolean findDefaultDataChecked(Date startDate,Integer sequence,String configId);

	@Query(value="select exists(select 1 from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(Set<String> orgUids,Date startDate,Date endDate,String configId,Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4 and o.sequence = ?5)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId, Integer sequence);
	
	@Query(value="select exists(select 1 from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) and o.config_id = ?4)",nativeQuery=true)
	public Boolean findExistsDefaultData(String clinicalDeptUid,Date startDate, Date endDate,String configId);
	
	@Query(value="select o.* from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDataByClinicalOrgUid(List<String> listOrgUid,Date startDate,Date endDate);

	@Query(value="select o.* from nurse_site_maintain_history o where o.name=?1 and o.schedule_date=?2",nativeQuery=true)
	public List<NurseSiteMaintainHistory> checkAgain(String name,Date currDate);

	@Query(value="select o.* from nurse_site_maintain_history o where o.status=0 and o.config_id = ?1 and o.schedule_date=?2 and o.sequence=?3",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultDataUnchecked(String configId,Date scheduleDate,Integer sequence);

	@Query(value="select distinct o.* from nurse_site_maintain_history o where o.schedule_date>=?2 and o.schedule_date<=?3 and o.clinical_dept_uid in (?1) order by o.schedule_date",nativeQuery=true)
	public List<NurseSiteMaintainHistory> findDefaultData(Set<String> orgUids,Date startDate,Date endDate);

}

