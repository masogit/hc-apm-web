package com.ge.apm.dao;

import com.ge.apm.domain.SysAuthGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;

public interface SysAuthGroupRepository extends GenericRepository<SysAuthGroup> {

    @Query(value="select * from sys_auth_group where parent_uid is not null",nativeQuery=true)
    List<SysAuthGroup> listSysAuthGroup();

    SysAuthGroup findByEncoded(String encoded);

    List<SysAuthGroup> findByEncodedInOrderByEncodedAsc(Collection<String> encoded);

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

}