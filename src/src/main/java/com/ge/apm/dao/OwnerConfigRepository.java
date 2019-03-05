package com.ge.apm.dao;

import com.ge.apm.domain.OwnerConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2019/2/26.
 */
public interface OwnerConfigRepository extends GenericRepositoryUUID<OwnerConfig> {

    public List<OwnerConfig> findByOwnerConfigTypeAndTypeUIDAndSiteUID(String ownerConfig, String typeUID, String siteUID);

}
