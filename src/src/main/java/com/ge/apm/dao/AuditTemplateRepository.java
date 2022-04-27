package com.ge.apm.dao;

import com.ge.apm.domain.AuditTemplate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditTemplateRepository extends GenericRepositoryUUID<AuditTemplate> {

    @Modifying
    @Query("delete from AuditTemplate a where a.uid=?1 and a.type=2")
    void deleteUserTemplateByUid(String uid);

    public List<AuditTemplate> findByType(Integer type);
    public  AuditTemplate findByUid(String uid);
}
