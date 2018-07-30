package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

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
	
	@Query(value="select o.* from nurse_site_maintain o where o.schedule_date>=?2 and o.schedule_date<?3 and o.clinical_dept_uid in (?1) order by o.schedule_date asc",nativeQuery=true)
	public List<NurseSiteMaintain> findDataByClinicalOrgUid(List<String> listOrgUid,Date startDate,Date endDate);
}

