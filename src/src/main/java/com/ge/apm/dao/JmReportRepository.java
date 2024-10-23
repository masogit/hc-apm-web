package com.ge.apm.dao;

import com.ge.apm.domain.JmReport;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface JmReportRepository extends GenericRepositoryUUID<JmReport>{
    @Query(nativeQuery = true, value = "SELECT a.* FROM table_jm_report a WHERE a.id in(SELECT b.jm_report_id FROM jm_report_assign_tenant b WHERE b.assign_tenant = ?1) \n" +
            "or a.visible_range = '-1' ;")
    List<JmReport> findByTenantUid(String tenantUID);

    @Query(nativeQuery = true, value = "SELECT a.* FROM table_jm_report a WHERE a.id in(SELECT b.jm_report_id FROM jm_report_assign_tenant b) ;")
    List<JmReport> findAll();
    @Query(nativeQuery = true, value = "SELECT a.* FROM table_jm_report a WHERE  a.name='工单备件单打印'")
    JmReport findPartPrintReport();
}
