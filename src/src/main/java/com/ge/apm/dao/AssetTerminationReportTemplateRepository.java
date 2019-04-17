package com.ge.apm.dao;

import com.ge.apm.domain.AssetTerminationReportTemplate;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetTerminationReportTemplateRepository extends GenericRepositoryUUID<AssetTerminationReportTemplate> {

    @Query(value = "select w from AssetTerminationReportTemplate w where w.siteUID in ?1")
    List<AssetTerminationReportTemplate> findBySiteUIDList(List<String> uidList);

    List<AssetTerminationReportTemplate> findBySiteUID(String siteUid);

}
