package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 * @author: csh
 * @date: 2020/7/13 13:48
 * @version: 1.0
 */
public interface SimplePartsWarehouseRepository extends GenericRepositoryUUID<SimplePartsWarehouse>, JpaRepository<SimplePartsWarehouse, String> {

    /**
     * @param setIds
     * @return
     */
    @Query(value = "select u.* from simple_parts_warehouse u where u.tenant_uid = ?1 and u.id in ?2 ", nativeQuery = true)
    List<SimplePartsWarehouse> getByIds(String tenantUid, Set<String> setIds);

    @Query(value = "select u.* from simple_parts_warehouse u where u.status = 1 and u.tenant_uid = ?1 and u.id in ?2 ", nativeQuery = true)
    List<SimplePartsWarehouse> getPartsWarehouseListInUseByIds(String tenantUid, Set<String> setIds);

    @Query(value = "select u.* from simple_parts_warehouse u where u.id = ?1 ", nativeQuery = true)
    SimplePartsWarehouse getById(String id);

    @Query(value = "select * from simple_parts_warehouse where tenant_uid = ?1  and status =1 order by created_date desc", nativeQuery = true)
    List<SimplePartsWarehouse> findAllWarehouseList(String tenantUid);

    @Query(value = "select * from simple_parts_warehouse where tenant_uid = ?1 and status = 1 order by created_date desc", nativeQuery = true)
    List<SimplePartsWarehouse> findWarehouseListInActive(String tenantUid);
}
