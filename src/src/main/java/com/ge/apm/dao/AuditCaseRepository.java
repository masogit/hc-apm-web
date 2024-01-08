package com.ge.apm.dao;

import com.ge.apm.domain.AuditCase;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditCaseRepository extends GenericRepositoryUUID<AuditCase> {

    public List<AuditCase> findBySystemCaseUid(String systemCaseUid);

    @Query(value = "select count(1) from audit_case t1 inner join audit_system_case t2 on t1.system_case_uid = t2.uid " +
            "where t1.status = 1 and t1.site_uid = ?1 and t2.type = ?2", nativeQuery = true)
    int preSaveJudge(String siteUid, Integer type);

    @Modifying
    @Transactional
    @Query(value = "update audit_case set status = 2 where uid = ?1", nativeQuery = true)
    void expiredCase(String caseUid);

    public List<AuditCase> findByTenantUid(String tenantUID);

}
