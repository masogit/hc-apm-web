package com.ge.apm.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.SmallAssetPerformanceConfig;

import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 212605082 on 2017/6/20.
 */
public interface InventoryOrderRepository extends GenericRepositoryUUID<InventoryOrder> {
    InventoryOrder findById(String id);

    @Query(nativeQuery = true, value = "SELECT COUNT(1) FROM inventory_order_detail d LEFT JOIN asset_info a ON d.asset_id = a.id WHERE a.is_deleted = FALSE AND a.clinical_dept_uid = ?1 AND d.status = 1 AND d.inventory_order_id = ?2")
    Long countInventoriedAsset(String orgUID, String orderId);

    @Query(nativeQuery = true, value = "SELECT COUNT(1) FROM inventory_order_detail d LEFT JOIN asset_info a ON d.asset_id = a.id WHERE a.is_deleted = FALSE AND a.clinical_dept_uid = ?1 AND d.status = 0 AND d.inventory_order_id = ?2")
    Long countUninventoriedAsset(String orgUID, String orderId);

    @Query(nativeQuery = true, value = "SELECT count(1) FROM inventory_order_detail d WHERE d.status = 2 AND d.clinical_dept_id = (SELECT id FROM org_info WHERE uid = ?1 AND org_type = 4) AND d.inventory_order_id = ?2")
    Long countUndocumentedAsset(String orgUID, String inventoryOrderId);

    @Query(nativeQuery = true, value = "SELECT count(1) FROM inventory_issue WHERE clinical_dept_id = (SELECT id FROM org_info WHERE uid = ?1 AND org_type = 4) AND inventory_order_id = ?2")
    Long countInventoryIssue(String orgUID, String inventoryOrderId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE asset_info a SET inventory_status = ?1 WHERE a.clinical_dept_uid IN ?2")
    void setAssetInventoryStatus(Integer inventoryStatus, List<String> clinicalDeptIdList);

    @Query(nativeQuery = true, value = "SELECT assignee_org FROM inventory_order WHERE is_finished = FALSE AND tenant_uid = ?1")
    List<String> findByInventoryOrder(String tenantUID);

    @Query(nativeQuery = true, value = "SELECT i.id FROM inventory_order i WHERE i.is_finished = FALSE AND assignee_org LIKE '%' || (SELECT clinical_dept_uid FROM asset_info a WHERE a.id = ?1) || '%' LIMIT 1;")
    String findInventoryOrderByAsset(Integer assetId);

    @Query(nativeQuery = true, value = "SELECT i.id FROM inventory_order i WHERE i.is_finished = FALSE AND assignee_org LIKE '%' || (SELECT uid FROM org_info o WHERE o.id = ?1) || '%' LIMIT 1;")
    String findInventoryOrderByClinicalDept(Integer clinicalDeptId);
}
