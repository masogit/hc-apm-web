package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderExportTemplate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface WorkOrderExportTemplateRepository extends GenericRepositoryUUID<WorkOrderExportTemplate> {

    @Query(value = "select w from WorkOrderExportTemplate w where w.siteUID in ?1")
    public List<WorkOrderExportTemplate> getBySiteUIDList(List<String> uidList);

    @Query(value = "select * from work_order_export_template w where w.site_UID=:uid order by w.id desc limit 1", nativeQuery = true)
    public WorkOrderExportTemplate getBySiteUID(@Param("uid") String site_uid);

    public List<WorkOrderExportTemplate> findBySiteUID(String institutionUID);

    public WorkOrderExportTemplate findFirstBySiteUID(String siteUid);

    @Query(value = "delete from WorkOrderExportTemplate wc where wc.siteUID = ?1")
    @Modifying
    @Transactional
    public void deleteBySiteUID(String siteUID);

    List<WorkOrderExportTemplate> findByTenantUID(String tenantUID);

}
