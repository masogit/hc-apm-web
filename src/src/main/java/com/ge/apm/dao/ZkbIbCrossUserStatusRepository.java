package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIbCrossUserStatus;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


/**
 *
 */
public interface ZkbIbCrossUserStatusRepository extends GenericRepositoryUUID<ZkbIbCrossUserStatus> {

    List<ZkbIbCrossUserStatus> findByLoginName(String loginName);

    void deleteByLoginName(String loginName);

    @Query(value = "select * from zkb_ib_cross_user_status s where exists (select * from user_account u where u.tenant_uid = ?1 and u.login_name = s.login_name)", nativeQuery = true)
    List<ZkbIbCrossUserStatus> queryByTenantUid(String tenantUid);
}
