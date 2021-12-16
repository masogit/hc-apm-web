package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.UserRole;

import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

public interface UserRoleRepository extends GenericRepository<UserRole> {
    @Modifying
    @Transactional
    //@Query("delete from UserRole o where o.userAccount=?1")
    @Query(value="delete from user_role where user_id = ?1",nativeQuery = true)
    public void deleteByUserAccount(Integer userId);
    
    @Query(value="delete from user_role where id in ?1",nativeQuery=true)
    public void deleteByUserRoleIds(Set<Integer> ids);
    
    public List<UserRole> findByUserId(int userId);

    @Query(value="select user_id from user_role where role_id in ?1",nativeQuery=true)
    public List<Integer> findByRoleIds(List<Integer> userId);

    //0 means sys role 1 means custom role
    List<UserRole> findByUserIdAndRoleType(Integer userId, Integer roleType);

    @Query(value="select ur.user_id from user_role ur inner join user_account ua on ur.user_id = ua.id  where ua.tenant_uid = ?1 and ur.role_id in ?2",nativeQuery=true)
    public Set<String> findUserIdByRoleIds(String tenantUid ,Set<Integer> roleIds);

    @Query(value="select ua.uid from user_role ur inner join user_account ua on ur.user_id = ua.id where  ua.tenant_uid = ?1 and ur.role_id = ?2",nativeQuery=true)
    public List<String> findUserUidByRoleId(String tenantUid ,int roleId);
}
