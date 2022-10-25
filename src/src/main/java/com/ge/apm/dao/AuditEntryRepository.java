package com.ge.apm.dao;

import com.ge.apm.domain.AuditEntry;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 * Created by 503266963
 */
public interface AuditEntryRepository extends GenericRepositoryUUID<AuditEntry> {

    List<AuditEntry> findByCaseUid(String caseUid);

    @Query(value = "select *  from  audit_entry where case_uid in ?1",nativeQuery = true)
    List<AuditEntry> findByCaseUidList(Set<String> collect);
}
