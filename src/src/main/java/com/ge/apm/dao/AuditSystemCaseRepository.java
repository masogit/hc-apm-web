package com.ge.apm.dao;

import com.ge.apm.domain.AuditSystemCase;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
public interface AuditSystemCaseRepository extends GenericRepositoryUUID<AuditSystemCase> {

    public AuditSystemCase findByUid(String uid);
    public List<AuditSystemCase> findByType(Integer type);
}
