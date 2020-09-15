package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsWareHouseLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author: kwg
 * @date: 2020/7/13 13:48
 * @version: 1.0
 */
public interface SimplePartsWarehouseLocatorRepository extends GenericRepositoryUUID<SimplePartsWareHouseLocator>, JpaRepository<SimplePartsWareHouseLocator, String> {

    /**
     * @param partsWarehouseId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseLocator>
     * @date 2020/7/13 13:51
     * 根据仓库id,查询仓库下所有的仓位
     */
    @Query(value = "select * from simple_parts_warehouse_locator where parts_warehouse_id=?1 order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseLocator> findAllByPartsWarehouseId(String partsWarehouseId);

    /**
     * 清空仓库下仓位
     *
     * @param partsWarehouseId
     */
    @Modifying
    @Query(value = "delete from simple_parts_warehouse_locator where parts_warehouse_id = ?1", nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void deleteByPartsWarehouseId(String partsWarehouseId);

    /**
     * @param id
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseLocator>
     * @date 2020/7/27 17:07
     * 根据仓位的id查询名称
     */
    @Query(value = "select * from simple_parts_warehouse_locator where id = ?1", nativeQuery = true)
    SimplePartsWareHouseLocator findById(String id);


    /**
     * 删除虚拟仓位
     *
     * @param partsWarehouseId
     * @param id
     */
    @Modifying
    @Query(value = "delete from simple_parts_warehouse_locator where parts_warehouse_id = ?1 and id = ?2", nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void deleteByPartsWarehouseIdAndEnableVirtual(String partsWarehouseId, String id);

    /**
     * @param partsWarehouseId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseLocator>
     * @date 2020/7/13 13:51
     * 根据仓库id,查询仓库下所有的仓位
     */
    @Query(value = "select * from simple_parts_warehouse_locator where parts_warehouse_id=?1 order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseLocator> findByPartsWarehouseId(String partsWarehouseId);
}
