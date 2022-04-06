package com.ge.apm.dao;

import com.ge.apm.domain.AuditResult;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditResultRepository extends GenericRepositoryUUID<AuditResult> {

    void deleteByCaseUid(String caseUid);

}
