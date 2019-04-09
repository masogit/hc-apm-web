package com.ge.apm.dao;

import com.ge.apm.domain.SysRole;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import webapp.framework.dao.GenericRepository;
import java.util.List;

public interface SysRoleRepository extends GenericRepository<SysRole> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public SysRole getByName(String name);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public SysRole getByRoleDesc(String name);
    
    public List<SysRole> getByNameIn(List<String> names);

    List<SysRole> findByIdIn(List<Integer> sysRoleIds);

    @Query(value="select * from sys_role where id in(select role_id from user_role where user_id = ?1 and role_type = 0)",nativeQuery = true)
    List<SysRole> getSysRoles(Integer userId);
}
