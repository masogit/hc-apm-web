package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainData;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainDataRepository extends GenericRepositoryUUID<NurseSiteMaintainData> {

	//startDate=yyyy/MM/dd,endDate=yyyy/MM/dd
	@Query(value="select distinct o.* from nurse_site_maintain_data o,user_org b where o.schedule_date >= ?1 and o.schedule_date <?2 and (o.site_uid=?3 or o.site_uid=b.org_uid or o.hospital_uid=b.org_uid or o.institution_uid=b.org_uid or o.tenant_uid=b.org_uid) and b.user_uid=?4",nativeQuery=true)
	public List<NurseSiteMaintainData> findBySiteUidAndDate(Date startDate,Date endDate,String siteUID,String userUid);
	
	//startDate=yyyy/MM/dd,endDate=yyyy/MM/dd
	//and (o.site_uid=?1 or o.site_uid=b.org_uid or o.hospital_uid=b.org_uid or o.institution_uid=b.org_uid or o.tenant_uid=b.org_uid or o.clinical_dept_uid=b.org_uid) and b.user_uid=?2
	@Query(value="select distinct o.* from nurse_site_maintain_data o,user_org b where o.schedule_date =?1 and (o.site_uid=?2 or o.site_uid=b.org_uid or o.hospital_uid=b.org_uid or o.institution_uid=b.org_uid or o.tenant_uid=b.org_uid) and b.user_uid=?3",nativeQuery=true)
	public NurseSiteMaintainData findBySiteUidAndScheduleDate(Date scheduleDate,String siteUID,String userUid);
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in (select b.id from nurse_site_maintain_item where b.parent_id in (select c.id from nurse_site_maintain where c.id=?1))",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String maintainId);
}
