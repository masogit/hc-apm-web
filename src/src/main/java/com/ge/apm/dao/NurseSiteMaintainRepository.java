package com.ge.apm.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintain;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainRepository extends GenericRepositoryUUID<NurseSiteMaintain> {

	@Query("select o from NurseSiteMaintain o where o.siteUID = ?1 and o.scheduleDate >= ?2 and o.scheduleDate <?3")
	public List<NurseSiteMaintain> findBySiteUidAndDate(String siteUID,Date startDate,Date endDate);
}
