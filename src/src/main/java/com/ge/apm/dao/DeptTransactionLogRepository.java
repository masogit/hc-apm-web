package com.ge.apm.dao;

import com.ge.apm.domain.DeptTransactionLog;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.JpaCRUDController;

import java.util.List;

public interface DeptTransactionLogRepository extends GenericRepository<DeptTransactionLog> {

    public List<DeptTransactionLog> getByTenantUID(String tenantUid);
}
