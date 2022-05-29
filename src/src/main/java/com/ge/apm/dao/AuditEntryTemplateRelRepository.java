package com.ge.apm.dao;

import com.ge.apm.domain.AuditConfigItem;
import com.ge.apm.domain.AuditEntryItemRel;
import com.ge.apm.domain.AuditEntryTemplateRel;
import com.ge.apm.domain.AuditTemplate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditEntryTemplateRelRepository extends GenericRepositoryUUID<AuditEntryTemplateRel> {

    List<AuditEntryTemplateRel> findByEntryUid(String entryUid);
    @Transactional
    @Modifying
    public void deleteByTemplateUidAndEntryUid(String templateUid,String entryUid);
    @Transactional
    @Modifying
    public void deleteByEntryUid(String entryUid);



    @Query(value = "SELECT c FROM AuditTemplate c WHERE c.uid in (SELECT r.templateUid FROM  AuditEntryTemplateRel r WHERE r.entryUid = ?1 )")
    public List<AuditTemplate> entryAndTemplateList( String entryUid);


    @Query(value = "SELECT c FROM AuditTemplate c WHERE c.uid in (SELECT r.templateUid FROM  AuditEntryTemplateRel r WHERE r.entryUid = ?1)")
    public List<AuditTemplate> auditTemplateList( String entryUid);

    List<AuditEntryTemplateRel> findByTemplateUid(String templateUid);

    List<AuditEntryTemplateRel> findByTemplateUidAndEntryUidAndOrderBy(String templateUid, String entryUid, Integer orderBy);

}
