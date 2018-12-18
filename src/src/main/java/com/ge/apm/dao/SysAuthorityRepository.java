package com.ge.apm.dao;

import com.ge.apm.domain.SysAuthority;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface SysAuthorityRepository extends GenericRepository<SysAuthority> {

    @Query(value = "select * from sys_authority where id in(select sys_auth_id from role_authority where role_id = ?1)",nativeQuery = true)
    List<SysAuthority> findSysAuthorityByRoleId(Integer roleId);
}
