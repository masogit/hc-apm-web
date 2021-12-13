package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsWareHouseUser;
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
public interface SimplePartsWarehouseUserRepository extends GenericRepositoryUUID<SimplePartsWareHouseUser>, JpaRepository<SimplePartsWareHouseUser, String> {

    /**
     * @param partsWarehouseId
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseUser>
     * @date 2020/7/13 13:51
     * 根据仓库id,查询仓库下所有的仓库管理员
     */
    @Query(value = "select * from simple_parts_warehouse_user where parts_warehouse_id=?1  order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseUser> findAllByPartsWarehouseId(String partsWarehouseId);

    /**
     * @param userUid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseUser>
     * @date 2021/7/23 15:40
     * 根据用户uid,查询该用户的uid下的所有仓库
     */
    @Query(value = "select parts_warehouse_id from simple_parts_warehouse_user where user_uid=?1", nativeQuery = true)
    List<String> findAllByPartsWarehouseByUserUid(String userUid);
    /**
     * 清空仓库下管理员
     *
     * @param partsWarehouseId
     */
    @Modifying
    @Query(value = "delete from simple_parts_warehouse_user where parts_warehouse_id = ?1", nativeQuery = true)
    @Transactional(propagation = Propagation.REQUIRED)
    void deleteByPartsWarehouseId(String partsWarehouseId);

    /**
     * @param userUid
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsWareHouseUser>
     * @date 2020/7/13 13:51
     * 根据用户id,查询用户下所有的仓库
     */
    @Query(value = "select * from simple_parts_warehouse_user where user_uid=?1  order by created_date desc", nativeQuery = true)
    List<SimplePartsWareHouseUser> findAllByUserUid(String userUid);

}
