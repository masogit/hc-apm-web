package com.ge.apm.dao;

import com.ge.apm.domain.AuditGroupConfigurationDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AuditGroupConfigurationDetailRepository extends GenericRepositoryUUID<AuditGroupConfigurationDetail> {

    public List<AuditGroupConfigurationDetail> findByBusinessUidAndAuditStatus(String businessUid, Boolean auditStatus);

    public List<AuditGroupConfigurationDetail> findByBusinessUidAndActualAuditUidAndDeleteFlag(String businessUid, String actualAuditUid, Integer deleteFlag);

}
