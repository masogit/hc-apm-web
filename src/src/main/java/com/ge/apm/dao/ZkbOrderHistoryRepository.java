package com.ge.apm.dao;

import com.ge.apm.domain.ZkbOrderHistory;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/9/1
 */
public interface ZkbOrderHistoryRepository extends GenericRepositoryUUID<ZkbOrderHistory> {

    List<ZkbOrderHistory> findByTenantUid(String tenantUid);
}
