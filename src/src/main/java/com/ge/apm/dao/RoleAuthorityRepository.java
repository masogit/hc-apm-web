package com.ge.apm.dao;

import com.ge.apm.domain.RoleAuthority;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface RoleAuthorityRepository extends GenericRepository<RoleAuthority> {

    @Modifying
    @Transactional
    @Query(value = "delete from role_authority where role_id = ?1",nativeQuery = true)
    void deleteByRoleId(Integer roleId);

    @Query(value = "select sys_auth_id from role_authority where role_id = ?1",nativeQuery = true)
    List<Integer> findByAuthIds(Integer id);
}
