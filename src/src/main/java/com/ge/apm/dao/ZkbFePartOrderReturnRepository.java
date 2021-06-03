package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrderReturn;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/1/4
 */
public interface ZkbFePartOrderReturnRepository extends GenericRepositoryUUID<ZkbFePartOrderReturn> {

    /**
     * 根据备件申请主键获取设备归还列表
     * @param partOrderId 维修流程ID
     * @return
     */
    List<ZkbFePartOrderReturn> findByPartOrderId(String partOrderId);

    /**
     * 求同一个备件所有归还数量
     * @param assetNo 备件号
     * @param orderNo order#号
     * @param partOrderId 流程ID
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_fe_part_order_return where asset_no=?1 and order_no = ?2 and part_order_id = ?3",nativeQuery = true)
    Integer allReturnQty(String assetNo,String orderNo,String partOrderId);

    /**
     * 根据流程ID获取申请备件数量
     * @param fePartOrderId
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_fe_part_order_return where part_order_id = ?1", nativeQuery = true)
    Integer fetchQty(String fePartOrderId);
}
