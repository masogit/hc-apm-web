package com.ge.apm.dao;

import com.ge.apm.domain.MsgConfig;
import com.ge.apm.domain.MsgTenant;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MsgRepository extends GenericRepository<MsgConfig> {

    public MsgConfig getByMsgName(String name);


    @Query(value="select mc.msg_name from msg_tenant mt ,msg_config mc where  mc.id = msgconfig_id and tenant_uid = ?1",nativeQuery = true)
    public List<String> findByTenantUid(String tenantUid);

}
