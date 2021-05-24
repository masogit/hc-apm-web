package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrderAssetInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/1/4
 */
public interface ZkbFePartOrderAssetInfoRepository extends GenericRepositoryUUID<ZkbFePartOrderAssetInfo> {

    /**
     * 根据备件申请主键获取设备列表
     * @param fePartOrderId
     * @return
     */
    List<ZkbFePartOrderAssetInfo> findByFePartOrderId(String fePartOrderId);


    /**
     * 根据fepartOrderId和assetNo 查找数据
     * @param fePartOrderId
     * @param assetNo
     * @return 归还列表
     */
    List<ZkbFePartOrderAssetInfo> findByFePartOrderIdAndAssetNo(String fePartOrderId,String assetNo);

    @Query(value = "SELECT sum(asset_qty) from zkb_fe_part_order_asset_info where fe_part_order_id = ?1", nativeQuery = true)
    Integer fetchQty(String fePartOrderId);

}
