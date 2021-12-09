package com.ge.apm.dao;

import com.ge.apm.domain.AuditGroupConfiguration;
import webapp.framework.dao.GenericRepositoryUUID;


public interface AuditGroupConfigurationRepository extends GenericRepositoryUUID<AuditGroupConfiguration> {

    public AuditGroupConfiguration findByHierOrgUidAndGroupName(String hierOrgUid,String groupName);

}
