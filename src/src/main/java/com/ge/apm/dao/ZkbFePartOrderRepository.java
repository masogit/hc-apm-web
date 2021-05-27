package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/12/8
 */
public interface ZkbFePartOrderRepository extends GenericRepositoryUUID<ZkbFePartOrder> {

    /**
     * 获取索引
     * @param dateCondition 日期条件
     * @return
     */
    @Query("select max(p.partOrderIndex) from ZkbFePartOrder p where p.partOrderNo like ?1 ")
    Object getMaxIndex(String dateCondition);

    /**
     * 根据单号获取数据
     * @param partOrderNo
     * @return
     */
    List<ZkbFePartOrder> findByPartOrderNo(String partOrderNo);


    /**
     * 根据父流程编号批量获取数据
     * @param fatherPartOrderNoList 父流程编号
     * @return
     */
    List<ZkbFePartOrder> findByPartOrderNoIn(List<String> fatherPartOrderNoList);

    /**
     *根据流程编号，类型，步骤查找数据
     * @param partOrderNo 流程编号
     * @param orderType 流程类型
     * @return
     */
    List<ZkbFePartOrder> findByPartOrderNoAndOrderType(String partOrderNo,String orderType);

    /**
     * 根据流程订单编号 备件号 Order#查找信息
     * @param partOrderNo 订单编号
     * @param orderNo Order#
     * @param assetNo 备件号
     * @return
     */
    @Query(value = "SELECT zkb_fe_part.* from zkb_fe_part_order zkb_fe_part INNER JOIN zkb_fe_part_order_asset_info zkb_fe_asset on zkb_fe_part.id = zkb_fe_asset.fe_part_order_id where zkb_fe_part.part_order_no = ?1 and zkb_fe_asset.order_no = ?2 and zkb_fe_asset.part_order_no=?3",nativeQuery = true)
    List<ZkbFePartOrder> findNeedReturnPartOrder(String partOrderNo,String orderNo,String assetNo);

}
