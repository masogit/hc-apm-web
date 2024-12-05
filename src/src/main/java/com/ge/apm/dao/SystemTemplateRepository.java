package com.ge.apm.dao;

import com.ge.apm.domain.SystemTemplate;

import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;


public interface SystemTemplateRepository extends GenericRepositoryUUID<SystemTemplate> {


    @Query(value = "select * from system_template where template_type=?1 and tenant_uid=?2 limit 1",nativeQuery = true)
    SystemTemplate getByTypeAndTenantUid(String type, String tenantUID);

}
