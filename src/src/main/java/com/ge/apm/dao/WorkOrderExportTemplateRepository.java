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
    public List<WorkOrderExportTemplate> findBySiteUIDList(List<String> uidList);

    public List<WorkOrderExportTemplate> findBySiteUID(String siteUid);

}
