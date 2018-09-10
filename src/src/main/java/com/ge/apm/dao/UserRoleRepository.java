package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.UserRole;

import webapp.framework.dao.GenericRepository;

public interface UserRoleRepository extends GenericRepository<UserRole> {
    @Modifying
    //@Query("delete from UserRole o where o.userAccount=?1")
    @Query(value="delete from user_role where user_id = ?1",nativeQuery = true)
    public void deleteByUserAccount(Integer userId);
    
    @Query(value="delete from user_role where id in ?1",nativeQuery=true)
    public void deleteByUserRoleIds(Set<Integer> ids);
    
    public List<UserRole> findByUserId(int userId);
}
