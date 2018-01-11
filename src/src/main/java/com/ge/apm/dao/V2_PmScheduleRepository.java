package com.ge.apm.dao;

import com.ge.apm.domain.PmScheduleData;

import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface V2_PmScheduleRepository extends GenericRepositoryUUID<PmScheduleData> {

	@Query(value="select psd.* from pm_schedule_data psd where psd.asset_uid = ?1 and sr_type=?2", nativeQuery = true)
	public List<PmScheduleData> getPmScheduleList(String assetUid,String sr_type);


}
