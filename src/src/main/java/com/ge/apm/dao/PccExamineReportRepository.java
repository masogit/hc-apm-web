package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.PccExamineReport;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface PccExamineReportRepository extends GenericRepositoryUUID<PccExamineReport> {
	
	@Query(value="select o.* from pcc_examine_report o where o.site_uid =?1",nativeQuery=true)
	List<PccExamineReport> fetchPccExamineReportBySiteUid(String siteUid);
}
