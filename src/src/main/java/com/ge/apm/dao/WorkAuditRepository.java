package com.ge.apm.dao;

import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * @author 503189829
 * @since 1.0.0
 */
public interface WorkAuditRepository extends GenericRepository<WorkAudit> {

    public List<WorkAudit> findByWorkOrderDetailId(String workOrderDetailId);



}
