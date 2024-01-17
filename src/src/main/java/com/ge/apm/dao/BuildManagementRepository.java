package com.ge.apm.dao;

import com.ge.apm.domain.BuildManagement;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface BuildManagementRepository extends GenericRepositoryUUID<BuildManagement> {
    @Query(value="select o.* from build_management o where o.site_uid=?1 order by o.name asc ",nativeQuery=true)
    List<BuildManagement> findBySiteUID(String siteUID);

    @Query(value="select o.* from build_management o where o.parent_id=?1 order by o.name asc ",nativeQuery=true)
    List<BuildManagement> findByParent(Integer parentId);


    @Query(value="select o.* from build_management o where o.tenant_uid=?1 ",nativeQuery=true)
    List<BuildManagement> findByTenantUID(String tenantUID);
}
