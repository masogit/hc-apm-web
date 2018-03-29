package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;

import webapp.framework.dao.GenericRepository;

/**
 * Created by 212605082 on 2017/6/20.
 */
public interface InventoryOrderDetailAssetInfoRepository extends GenericRepository<AssetInfo> {
//	AssetInfo findById(String id);

//    InventoryOrderDetail findByAssetInfo(AssetInfo assetInfo);

//    @Query("select d.assetInfo from InventoryOrderDetail d where d.inventoryOrderId = ?1 and d.assetInfo.clinicalDeptUID = ?2 and d.status = ?3")
//    Page<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID, Integer status, Pageable pageRequest);
//    
//    @Query("select d.assetInfo from InventoryOrderDetail d where d.inventoryOrderId = ?1 and d.assetInfo.clinicalDeptUID = ?2 and d.status = ?3")
//    List<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID, Integer status);
//    
//    @Query("select d.assetInfo from InventoryOrderDetail d where d.inventoryOrderId = ?1 and d.assetInfo.clinicalDeptUID = ?2")
//    List<AssetInfo> getInventoryAssetList(String orderId, String clinicalDeptUID);
//
//    @Query(nativeQuery = true, value = "SELECT * FROM inventory_order_detail d LEFT JOIN asset_info a ON d.asset_id = a.id LEFT JOIN inventory_order o ON d.inventory_order_id = o.id WHERE o.is_finished = FALSE AND a.is_deleted = FALSE AND a.qr_code = ?1 AND a.tenant_uid = ?2")
//    List<InventoryOrderDetail> findByQrCodeAndTenant(String qrCode, String tenantUID);
//
//    @Query(nativeQuery = true, value = "SELECT * FROM inventory_order_detail d LEFT JOIN asset_info a ON d.asset_id = a.id LEFT JOIN inventory_order o ON d.inventory_order_id = o.id WHERE o.is_finished = FALSE AND a.is_deleted = FALSE AND a.depart_num = ?1 AND a.tenant_uid = ?2")
//    List<InventoryOrderDetail> findByDepartNumAndTenant(String departNum, String tenantUID);
}
