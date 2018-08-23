package com.ge.apm.dao;

import com.ge.apm.domain.Attribute;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AttributeRepository extends GenericRepositoryUUID<Attribute> {

    List<Attribute> findByNameAndTenantUid(String name, String tenantUid);

}
