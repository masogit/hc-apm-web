package com.ge.apm.dao;

import com.ge.apm.domain.SysAuthGroup;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;

public interface SysAuthGroupRepository extends GenericRepository<SysAuthGroup> {

    @Query(value="select * from sys_auth_group where parent_uid is not null",nativeQuery=true)
    List<SysAuthGroup> listSysAuthGroup();

    SysAuthGroup findByEncoded(String encoded);

    List<SysAuthGroup> findByEncodedInOrderByEncodedAsc(Collection<String> encoded);
}