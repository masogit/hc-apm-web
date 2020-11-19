package com.ge.apm.dao;

import com.ge.apm.domain.TenantOptionConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Optional;

public interface TenantOptionConfigRepository extends GenericRepositoryUUID<TenantOptionConfig> {

    Optional<TenantOptionConfig> findByTenantUid(String tenantUid);
}
