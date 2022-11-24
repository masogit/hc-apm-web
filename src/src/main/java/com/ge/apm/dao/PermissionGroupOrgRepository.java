package com.ge.apm.dao;

import com.ge.apm.domain.PermissionGroupOrg;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 503310146
 */
public interface PermissionGroupOrgRepository extends GenericRepositoryUUID<PermissionGroupOrg> {

    /**
     * 根据权限组 uid 删除权限组关联组织
     * @param uid 权限组 uid
     */
    @Query(value = "delete from permission_group_org where permission_group_uid = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteByPermissionGroupUid(String uid);
}
