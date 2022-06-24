package com.ge.apm.dao;

import com.ge.apm.domain.AuditConfigItem;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditConfigItemRepository extends GenericRepositoryUUID<AuditConfigItem> {

    public List<AuditConfigItem> findByContentType(Integer contentType);
    public List<AuditConfigItem> findByType( Integer type);
    public  AuditConfigItem findByUid( String uid);

    @Query(value = "SELECT c FROM AuditConfigItem c WHERE c.uid in (SELECT r.itemUid FROM  AuditEntryItemRel r WHERE r.systemEntryUid in ?1 and r.orderBy=?2)")
    public List<AuditConfigItem> entryAndContentList(List<String> systemEntryUids,Integer orderBy);

    @Query(value = "SELECT c FROM AuditConfigItem c WHERE c.uid in (SELECT r.itemUid FROM  AuditEntryItemRel r WHERE r.systemEntryUid = ?1)")
    public List<AuditConfigItem> AuditConfigItemtList(String systemEntryUid);

    @Query(value = "select c.* from audit_config_item c join audit_entry_item_rel r on r.item_uid=c.uid where r.system_entry_uid=?1 order by r.order_by",nativeQuery = true)
    public List<AuditConfigItem> findConfigItemListByEntryUID(String systemEntryUid);

}
