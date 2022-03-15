package com.ge.apm.dao;

import com.ge.apm.domain.AuditEntryTemplateRel;
import com.ge.apm.domain.AuditTemplate;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditEntryTemplateRelRepository extends GenericRepositoryUUID<AuditEntryTemplateRel> {

    List<AuditEntryTemplateRel> findByEntryUid(String entryUid);

    List<AuditEntryTemplateRel> findByTemplateUid(String templateUid);

}
