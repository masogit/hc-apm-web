package com.ge.apm.dao;

import com.ge.apm.domain.DeptTransactionLog;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface DeptTransactionLogRepository extends GenericRepositoryUUID<DeptTransactionLog> {

    public List<DeptTransactionLog> getByTenantUID(String tenantUid);
}
