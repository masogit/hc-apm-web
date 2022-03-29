package com.ge.apm.dao;

import com.ge.apm.domain.AuditCase;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditCaseRepository extends GenericRepositoryUUID<AuditCase> {

    public List<AuditCase> findBySystemCaseUid(String systemCaseUid);

}
