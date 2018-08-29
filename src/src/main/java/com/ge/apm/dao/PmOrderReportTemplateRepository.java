package com.ge.apm.dao;

import com.ge.apm.domain.PmOrderReportTemplate;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface PmOrderReportTemplateRepository extends GenericRepositoryUUID<PmOrderReportTemplate> {

    @Query(value = "SELECT t from PmOrderReportTemplate t WHERE t.siteUID in ?1")
    List<PmOrderReportTemplate> findBySiteUIDList(List<String> uids);

    List<PmOrderReportTemplate> findBySiteUID(String uid);
}
