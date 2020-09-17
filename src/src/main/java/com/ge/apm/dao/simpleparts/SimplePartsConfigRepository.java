package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Optional;

public interface SimplePartsConfigRepository extends GenericRepositoryUUID<SimplePartsConfig> {

    Optional<SimplePartsConfig> findByTenantUid(String tenantUid);

}
