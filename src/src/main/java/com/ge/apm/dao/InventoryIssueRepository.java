package com.ge.apm.dao;


import com.ge.apm.domain.InventoryIssue;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212615494 on 8/11/2017.
 */
public interface InventoryIssueRepository extends GenericRepositoryUUID<InventoryIssue> {
//    @Modifying
//    @Query("update InventoryIssue i set i.isSolved = true, i.lastModifiedBy = ?2, i.lastModifiedDate = CURRENT_TIMESTAMP where i.id = ?1")
//    void resolveInventoryIssue(String id, String username);

    @Query(value = "SELECT * FROM inventory_issue   WHERE asset_id =?1 and inventory_order_id =?2 ORDER BY last_modified_date DESC LIMIT 1 ",nativeQuery = true)
    InventoryIssue findProjectionByAssetIdAndInventoryOrderId(Integer assetId,String inventoryOrderId);
    @Modifying
    @Transactional
    @Query(value = "delete from inventory_issue where id in ?1",nativeQuery = true)
    Integer deleteById(List<String> collect);
}
