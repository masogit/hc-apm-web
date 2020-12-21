package com.ge.apm.dao;

import com.ge.apm.domain.RoleMsgRel;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface RoleMsgRelRepository extends GenericRepository<RoleMsgRel> {

    @Query(value="select * from role_msg_rel where role_id in ( " +
            "select id from sys_role where id in ( select role_id from user_role where user_id = ?1 and role_type = 0  ) " +
            ")",nativeQuery = true)
    List<RoleMsgRel> getRoleMsgRels(Integer userId);

    @Query(value="select * from role_msg_rel where role_name = ?1 ",nativeQuery = true)
    List<RoleMsgRel> getRoleMsgByRoleName(String roleName);

}
