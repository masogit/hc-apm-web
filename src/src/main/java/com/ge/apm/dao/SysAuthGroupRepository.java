package com.ge.apm.dao;

import com.ge.apm.domain.SysAuthGroup;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface SysAuthGroupRepository extends GenericRepository<SysAuthGroup> {

    @Query(value="select uid,classification_code,encoded,split_part(encoded, '-', length(replace(encoded, '-', '//')) - length(encoded) +1) || name as name,parent_uid from sys_auth_group where parent_uid is not null",nativeQuery=true)
    List<SysAuthGroup> listSysAuthGroup();

    @Query(value = "select uid,classification_code,encoded,name,parent_uid from sys_auth_group where parent_uid is not null order by classification_code", nativeQuery = true)
    List<SysAuthGroup> listSysAuthGroupForUdid();

    @Query(value = "select uid,classification_code,encoded,name,parent_uid from sys_auth_group where parent_uid is not null and (classification_code ilike concat('%',?1,'%') or name ilike concat('%',?1,'%')) order by classification_code", nativeQuery = true)
    List<SysAuthGroup> listSysAuthGroupForUdid(String query);

//    SysAuthGroup findByEncoded(String encoded);

    SysAuthGroup findByClassificationCode(String classificationCode);

//    List<SysAuthGroup> findByEncodedInOrderByEncodedAsc(Collection<String> encoded);

    List<SysAuthGroup> findByParentUidIn(List<String> parentUid);

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.uid, sag.name, sag.encoded, sag.classification_code, sag.parent_uid\n" +
            "from sys_auth_group sag\n" +
            "where parent_uid is null\n" +
            "\n" +
            "union\n" +
            "\n" +
            "select sag.uid, sag.name, sag.encoded, sag.classification_code, sag.parent_uid\n" +
            "from sys_auth_group sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.uid\n" +
            ") select  * from cte_auth_group", nativeQuery = true)
    List<SysAuthGroup> listSysAuthGroupInHierarchy();

    @Query(value = "with RECURSIVE cte as (" +
            "select a.parent_uid,cast(a.name as varchar(500)) " +
            "from sys_auth_group a where a.classification_code = ?1 " +
            "union all select k.parent_uid,cast(k.name||' ->'||c.name as varchar(500)) as name  " +
            "from cte  c inner join sys_auth_group k on c.parent_uid = k.uid)select name from cte where  parent_uid is null", nativeQuery = true)
    String listSysAuthGroupNamePathInHierarchyByClassificationCode(String classificationCode);




    @Query(value = "with RECURSIVE cte as (" +
            "select a.classification_code,a.parent_uid,cast(a.name as varchar(500)) from sys_auth_group a " +
            "union all select c.classification_code,k.parent_uid,cast(k.name||' ->'||c.name as varchar(500)) as name from cte c " +
            "inner join sys_auth_group k on c.parent_uid = k.uid)select classification_code,name from cte where  parent_uid is null " +
            "AND classification_code IS NOT NULL", nativeQuery = true)
    List<Object> listSysAuthGroupNamePathInHierarchy();





}