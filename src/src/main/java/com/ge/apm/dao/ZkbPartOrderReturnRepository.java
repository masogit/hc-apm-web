package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderReturn;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
public interface ZkbPartOrderReturnRepository extends GenericRepositoryUUID<ZkbPartOrderReturn> {
    List<ZkbPartOrderReturn> findByPartOrderId(String partOrderId);
    List<ZkbPartOrderReturn> findByPartOrderIdAndDataStatusNotNull(String partOrderId);
    List<ZkbPartOrderReturn> findByPartOrderIdAndDataStatus(String partOrderId,String dataStatus);
    List<ZkbPartOrderReturn> findByPartOrderIdAndReturnTypeIsNot(String partOrderId,String returnType);
    List<ZkbPartOrderReturn> findByPartOrderIdAndModifyFlag(String partOrderId,String modifyFlag);

    void deleteByPartOrderId(String partOrderId);

    List<ZkbPartOrderReturn> findByPartOrderIdAndPartNo(String partOrderId,String partNo);

    List<ZkbPartOrderReturn> findByPartOrderIdAndPartNoAndIdIsNot(String partOrderId,String partNo,String id);


    /**
     * 获取配件申请归还数量
     * @param orderedDate
     * @param orderType
     * @param tenantUID
     * @return
     */
    @Query(value = " SELECT sum(zpor.qty) from zkb_part_order_return zpor " +
            " INNER JOIN zkb_part_order zkbp on zkbp.id = zpor.part_order_id " +
            " INNER JOIN zkb_contract zkbc on zkbp.contract_id = zkbc.id" +
            " where zpor.data_satus='Accept' " +
            " and zkbp.order_type = ?2 and zkbp.ordered_date< ?1 " +
            " and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd') and zkbc.end_date>=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd')", nativeQuery = true)
    Integer fetchQty(Date orderedDate,String orderType,String tenantUID);

    /**
     * 统计数量
     * @param partOrderIdList 退还单ID
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order_return where part_order_id in ?1 and return_type='goodReturn'", nativeQuery = true)
    Integer fetchReturnGoodQty(List<String> partOrderIdList);
}
