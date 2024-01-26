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


    @Query(value = "with recursive cte_bm as( select bm.* from build_management bm where bm.id = ?1 union select bm.* from build_management bm join cte_bm cte on bm.parent_id = cte.id ) select * from cte_bm",nativeQuery=true)
    List<BuildManagement>  findByTree(Integer id);



    @Query(value = "with recursive cte_bm as( select bm.* from build_management bm where bm.site_uid = ?1 union select bm.* from build_management bm join cte_bm cte on bm.parent_id = cte.id   ) select * from cte_bm where org_type=6",nativeQuery=true)
    List<BuildManagement> findBySiteUidTree(String siteUID);

    BuildManagement findByUid(String value);


    @Query(value="select o.* from build_management o where o.name_py_code=?1 ",nativeQuery=true)
    BuildManagement findByNamePyCode(String locationCode);
}
