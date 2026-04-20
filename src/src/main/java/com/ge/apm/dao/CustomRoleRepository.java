package com.ge.apm.dao;

import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.I18nMessage;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface CustomRoleRepository extends GenericRepository<CustomRole> {

    List<CustomRole> findByTenantUID(String tenantUID);

    CustomRole findByTenantUIDAndName(String tenantUID, String newRoleName);

    @Query(value = "select * from custom_role where id in (select role_id from user_role where role_type = 1 and user_id = ?1)",nativeQuery = true)
    List<CustomRole> findCustomRolesByUserId(Integer userId);

    CustomRole findById(int id);

    @Query(value = "select * from custom_role where site_uid in (?1) and function_list LIKE concat('%',?2,'%') ",nativeQuery = true)
    List<CustomRole> findCustomRolesBySiteUIdsAndFunction(List<String> SiteUIds, String functionName);

    @Query(value = "select  ua.uid from user_account ua left join user_role ur on ua.id=ur.user_id  where ua.tenant_uid = ?1 group by ua.uid HAVING sum(ur.role_type) is null or sum(ur.role_type) = count(ur.id) ",nativeQuery = true)
    List<String> findCustomRolesUser(String tenantUid);


    List<CustomRole> findByTenantUIDAndNameIn(String tenantUID, List<String> newRoleNames);

    @Query(value = "select  *  from  custom_role   where  tenant_uid=?1 ", nativeQuery = true)
    List<CustomRole> findByTenantUidAndFunctionListContains(String tenantUid );

}
