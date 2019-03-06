package com.ge.apm.dao;

import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.I18nMessage;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface CustomRoleRepository extends GenericRepository<CustomRole> {

    List<CustomRole> findByTenantUID(String tenantUID);

    CustomRole findByTenantUIDAndName(String tenantUID, String newRoleName);

    @Query(value = "select * from custom_role where id in (select role_id from user_role where role_type = 1 and user_id = ?1)",nativeQuery = true)
    List<CustomRole> findCustomRolesByUserId(Integer userId);
}
