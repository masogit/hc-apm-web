package com.ge.apm.dao;

import com.ge.apm.domain.AuditConfigItem;
import com.ge.apm.domain.AuditEntryItemRel;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditEntryItemRelRepository extends GenericRepository<AuditEntryItemRel> {

    List<AuditEntryItemRel> findByItemUid(String itemUid);

}
