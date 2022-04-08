package com.ge.apm.dao;

import com.ge.apm.domain.AuditConfigItem;
import com.ge.apm.domain.AuditEntryItemRel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditEntryItemRelRepository extends GenericRepository<AuditEntryItemRel> {

    List<AuditEntryItemRel> findByItemUid(String itemUid);
    List<AuditEntryItemRel> findByItemUidAndSystemEntryUidAndOrderBy(String itemUid,String systemEntryUid,Integer orderBy);

    @Transactional
    @Modifying
    public void deleteByItemUidAndOrderBy(String itemUid,Integer orderBy);
    @Transactional
    @Modifying
    public void deleteBySystemEntryUid(String systemEntryUid);
}
