package com.ge.apm.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.SmallAssetPerformance;

import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 212605082 on 2018/3/7.
 */
public interface SmallAssetPerformanceRepository extends GenericRepositoryUUID<SmallAssetPerformance> {
	
	@Query("select o from SmallAssetPerformance o where o.projectName = ?1 and o.happenDate = ?2 and o.siteUID = ?3")
	public SmallAssetPerformance findByProjectNameAndHappenDate(String projectName, Date happenDate,String siteUID);
	

	@Query("select 1 from SmallAssetPerformance o where o.projectName = ?1 and o.happenDate = ?2 and o.siteUID = ?3")
	public Integer findExistsByProjectNameAndHappenDate(String projectName, Date happenDate,String siteUID);
}
