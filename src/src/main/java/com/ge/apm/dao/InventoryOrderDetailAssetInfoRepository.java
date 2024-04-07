package com.ge.apm.dao;

import com.ge.apm.domain.InventoryOrderDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * Created by 212605082 on 2017/6/20.
 */
public interface InventoryOrderDetailAssetInfoRepository extends GenericRepositoryUUID<InventoryOrderDetail> {

    @Query(value = "SELECT count(1) FROM inventory_order_detail LEFT JOIN inventory_order ON inventory_order_detail.inventory_order_id = inventory_order.\"id\" WHERE asset_id IN (?1) AND status = 0 AND inventory_order.is_finished = FALSE",nativeQuery = true)
    int getInventoryingAssetCount(List<Integer> assetIds);

    @Query(value = "SELECT asset_id FROM inventory_order_detail LEFT JOIN inventory_order ON inventory_order_detail.inventory_order_id = inventory_order.\"id\" WHERE asset_id IN (?1) AND status = 0 AND inventory_order.is_finished = FALSE",nativeQuery = true)
    List<Integer> getInventoryingAssetCountByBatchEdit(List<Integer> assetIds);

    @Query(value = "SELECT * FROM inventory_order_detail   WHERE asset_id =?1 ORDER BY last_modified_date DESC LIMIT 1 ",nativeQuery = true)
    List<InventoryOrderDetail> findProjectionByAssetId(Integer assetId);
    @Modifying
    @Transactional
    @Query(value = "delete from inventory_order_detail where id in ?1",nativeQuery = true)
    Integer deleteById(List<String> ids);

    @Query(value = "SELECT max(last_modified_date) FROM inventory_order_detail where asset_id = ?1 and status = 1",nativeQuery = true)
    Date findLastInventoryDateByAssetId(Integer assetId);
}
