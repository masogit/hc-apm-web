package com.ge.apm.dao;

import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.I18nMessage;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface CustomRoleRepository extends GenericRepository<CustomRole> {

    List<CustomRole> findByTenantUID(String tenantUID);

    CustomRole findByTenantUIDAndName(String tenantUID, String newRoleName);


}
