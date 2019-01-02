package com.ge.apm.dao;

import com.ge.apm.domain.CustomRole;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface CustomRoleRepository extends GenericRepository<CustomRole> {

    List<CustomRole> findByTenantUID(String tenantUID);

    CustomRole findByTenantUIDAndName(String tenantUID, String newRoleName);
}
