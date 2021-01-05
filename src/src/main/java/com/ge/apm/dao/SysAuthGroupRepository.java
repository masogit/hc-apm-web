package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SysAuthGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface SysAuthGroupRepository extends GenericRepository<SysAuthGroup> {

    @Query(value="select * from sys_auth_group where parent_uid is not null",nativeQuery=true)
    List<SysAuthGroup> listSysAuthGroup();

    SysAuthGroup findByEncoded(String encoded);
}