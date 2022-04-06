package com.ge.apm.dao;

import com.ge.apm.domain.AuditEntry;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditEntryRepository extends GenericRepositoryUUID<AuditEntry> {

    List<AuditEntry> findByCaseUid(String caseUid);

}
