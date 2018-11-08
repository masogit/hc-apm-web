package com.ge.apm.dao;

import com.ge.apm.domain.MsgConfig;
import com.ge.apm.domain.MsgTenant;
import com.ge.apm.domain.SysRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MsgConfigTenantRepository extends GenericRepository<MsgTenant> {
    @Transactional
    @Modifying
    @Query(value="delete from msg_tenant where tenant_uid = ?1",nativeQuery = true)
    public void deleteByTenantUID(String tenantUid);



}
