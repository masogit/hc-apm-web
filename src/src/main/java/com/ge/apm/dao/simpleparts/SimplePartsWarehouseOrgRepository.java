package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsWareHouseOrg;
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
public interface SimplePartsWarehouseOrgRepository extends GenericRepositoryUUID<SimplePartsWareHouseOrg>, JpaRepository<SimplePartsWareHouseOrg, String> {

    /**
     * @param partsWarehouseId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseOrg>
     * @date 2020/7/13 13:51
     * 根据仓库id,查询仓库下所有的仓库管理员
     */
    @Query(value = "select * from simple_parts_warehouse_org where parts_warehouse_id=?1  order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseOrg> findAllByPartsWarehouseId(String partsWarehouseId);

    /**
     * 清空仓库下管理员
     *
     * @param partsWarehouseId
     */
    @Modifying
    @Query(value = "delete from simple_parts_warehouse_org where parts_warehouse_id = ?1", nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void deleteByPartsWarehouseId(String partsWarehouseId);

    /**
     * @param siteUid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseOrg>
     * @date 2020/7/13 13:51
     * 根据机构id,查询仓库下所有的仓库管理员
     */
    @Query(value = "select * from simple_parts_warehouse_org where org_uid =?1  order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseOrg> findAllByOrgUid(String siteUid);
}
