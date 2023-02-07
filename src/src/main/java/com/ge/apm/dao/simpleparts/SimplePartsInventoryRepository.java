package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @package com.ge.apm.dao
 */
public interface SimplePartsInventoryRepository extends GenericRepositoryUUID<SimplePartsInventory>, JpaRepository<SimplePartsInventory, String> {

    /**
     * @param uid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @date 2020/7/21 18:41
     * 根据仓库的id查询到指定仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory where tenant_uid = ?1", nativeQuery = true)
    List<SimplePartsInventory> findByTenantUid(String tenantUid);

    /**
     * @param uid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @date 2020/7/21 18:41
     * 根据仓库的id查询到指定仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory where parts_warehouse_id = ?1", nativeQuery = true)
    List<SimplePartsInventory> findByPartsWarehouseId(String uid);

    /**
     * @param partsItemId
     * @param partsLocatorId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @date 2020/7/21 18:41
     * 备件id，仓位的id查询到指定仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory where parts_item_id = ?1 and parts_locator_id = ?2", nativeQuery = true)
    List<SimplePartsInventory> findByPartsItemIdAndLocatorId(String partsItemId, String partsLocatorId);

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsInventory>
     * @date 2020/7/21 18:41
     * 根据仓库的id查询到指定仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory", nativeQuery = true)
    List<SimplePartsInventory> findByPartsWarehouse();

    @Query(value = "select * from simple_parts_inventory where parts_warehouse_id = ?1 and parts_locator_id = ?2", nativeQuery = true)
    List<SimplePartsInventory> findByWarehouseIdAndLocatorId(String warehouseId, String locatorId);

    @Query(value = "select * from simple_parts_inventory where parts_warehouse_id = ?1 and parts_locator_id = ?2 and parts_item_id = ?3", nativeQuery = true)
    Optional<SimplePartsInventory> findByWarehouseIdAndLocatorIdAndPartsItemId(String warehouseId, String locatorId, String partsItemId);

    @Query(value = "select * from simple_parts_inventory where parts_item_id=?1", nativeQuery = true)
    List<SimplePartsInventory> findByPartsItemId(String partsItemId);

    @Query(value = "select * from simple_parts_inventory where  parts_warehouse_id = ?1 and parts_locator_id = ?2 and parts_item_id = ?3", nativeQuery = true)
    SimplePartsInventory findByWarehouseIdAndLocatorIdAndPartsItemIdAndPartsItemId(String sourcePartsWarehouseId, String sourcePartsWarehouseLocatorId, String sourcePartsItemId);

    @Query(value = "select * from simple_parts_inventory where parts_item_id = ?1 and  parts_warehouse_id = ?2 and parts_locator_id = ?3", nativeQuery = true)
    SimplePartsInventory findByPartsItemIdAndPartsWarehouseIdAndPartsLocatorId(String partsItemId, String partsWarehouseId, String partsLocatorId);

    @Query(value = "select * from simple_parts_inventory where parts_item_id = ?1 and  parts_warehouse_id = ?2", nativeQuery = true)
    List<SimplePartsInventory> findByPartsItemIdAndPartsWarehouseId(String partsItemId, String partsWarehouseId);

    @Modifying
    @Query(value = "delete from simple_parts_inventory where parts_warehouse_id = ?1 and parts_locator_id = ?2 and parts_item_id = ?3", nativeQuery = true)
    int deleteByWarehouseIdAndLocatorIdAndPartsItemId(String warehouseId, String locatorId, String partsItemId);

    @Query(value = "select COALESCE(sum(onhand_qty), 0) from simple_parts_inventory where parts_warehouse_id = ?1 and parts_locator_id = 'VIRTUAL_LOCATOR'", nativeQuery = true)
    int countOfVirtualLocatorsByPartsWarehouseId(String partsWarehouseId);

    @Query(value = "select COALESCE(sum(onhand_qty), 0) from simple_parts_inventory where parts_warehouse_id in ?1 and parts_locator_id = 'VIRTUAL_LOCATOR'", nativeQuery = true)
    int countOfVirtualLocators(List<String> partsWarehouseIds);

    /**
     * @param tenantUid
     * @return java.util.List<com.ge.apm.domain.SimplePartsInventory>
     * @date 2020/7/21 18:41
     * 根据租户id查询虚拟仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory where tenant_uid = ?1 and parts_locator_id = 'VIRTUAL_LOCATOR'", nativeQuery = true)
    List<SimplePartsInventory> findIsVirtualLoctorByTenantUid(String tenantUid);

    /**
     * @param tenantUid
     * @return java.util.List<com.ge.apm.domain.SimplePartsInventory>
     * @date 2020/7/21 18:41
     * 根据租户id查询非虚拟仓库的备件
     */
    @Query(value = "select * from simple_parts_inventory where tenant_uid = ?1 and parts_locator_id != 'VIRTUAL_LOCATOR'", nativeQuery = true)
    List<SimplePartsInventory> findNotIsVirtualLoctorByTenantUid(String tenantUid);

    /**
     * @param uids
     * @return java.util.List<com.ge.apm.domain.SimplePartsInventory>
     * @date 2020/7/21 18:41
     * 根据仓库id查询库存备件
     */
    @Query(value = "select * from simple_parts_inventory where parts_warehouse_id in ?1 ", nativeQuery = true)
    public List<SimplePartsInventory> findByPartsWarehouseIds(Set<String> uids);

    @Query(value = "select * from simple_parts_inventory where parts_warehouse_id in ?1 and name like concat('%',?2,'%')", nativeQuery = true)
    public List<SimplePartsInventory> findByPartsWarehouseIdsAndName(Set<String> uids, String name);

}
