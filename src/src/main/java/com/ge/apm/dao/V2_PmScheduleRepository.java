package com.ge.apm.dao;

import com.ge.apm.domain.PmScheduleData;

import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface V2_PmScheduleRepository extends GenericRepositoryUUID<PmScheduleData> {

	@Query(value="select psd.* from pm_schedule_data psd where psd.asset_uid = ?1", nativeQuery = true)
	public List<PmScheduleData> getPmScheduleList(String assetUid);


}
