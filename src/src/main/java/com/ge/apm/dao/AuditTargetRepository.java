package com.ge.apm.dao;

import com.ge.apm.domain.AuditTarget;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditTargetRepository extends GenericRepositoryUUID<AuditTarget> {

    public List<AuditTarget> findBySystemCaseUid(String systemCaseUid);

}
