package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.SmallAssetPerformanceReport;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 212605082 on 2018/3/7.
 */
public interface SmallAssetPerformanceReportRepository extends GenericRepositoryUUID<SmallAssetPerformanceReport> {
	
	@Query("select o from SmallAssetPerformanceReport o where o.smallAssetPerformanceId = ?1")
	public List<SmallAssetPerformanceReport> findReportBySmallAssetPerformanceId(String smallAssetPerformanceId);
	
}
