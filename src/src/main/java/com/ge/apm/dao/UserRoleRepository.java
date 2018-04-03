package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface UserRoleRepository extends GenericRepository<UserRole> {
    @Modifying
    //@Query("delete from UserRole o where o.userAccount=?1")
    @Query(value="delete from user_role where user_id = ?1",nativeQuery = true)
    public void deleteByUserAccount(Integer userId);
    
    public List<UserRole> findByUserId(int userId);
}
