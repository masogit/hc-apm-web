package com.ge.apm.dao;

import com.ge.apm.domain.JmReportAssignTenant;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;


public interface JmReportAssignTenantRepository extends GenericRepositoryUUID<JmReportAssignTenant>{
    @Query(nativeQuery = true, value = "SELECT o.* FROM jm_report_assign_tenant o WHERE o.jm_report_id = ?1 and o.assign_tenant = ?2 ")
    JmReportAssignTenant findByJmReportIdAndTenantUid(String jmReportId, String tenantUID);

}
