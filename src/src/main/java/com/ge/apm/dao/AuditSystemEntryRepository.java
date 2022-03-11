package com.ge.apm.dao;

import com.ge.apm.domain.AuditSystemEntry;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
public interface AuditSystemEntryRepository extends GenericRepository<AuditSystemEntry> {

    List<AuditSystemEntry> findByMark(String mark);

    @Query("select a from AuditSystemEntry a where (a.name like %?1% or a.mark like %?1%) and a.caseUid = ?2  order by a.createTime desc")
    List<AuditSystemEntry> findListByName(String name,String caseUid);

    List<AuditSystemEntry> findByUid(String uid);

    List<AuditSystemEntry> findByCaseUid(String caseUid);

    List<AuditSystemEntry> findByParentUid(String parentUid);

    @Query(value = "select o from AuditSystemEntry o where o.caseUid = ?1 order by o.createTime desc")
    List<AuditSystemEntry> entryList( String caseUid);



}
