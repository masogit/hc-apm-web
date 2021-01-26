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
     * 统计数量
     * @param partOrderIdList 退还单ID
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order_return where part_order_id in ?1 and return_date between ?2 and ?3", nativeQuery = true)
    Integer fetchQty(List<String> partOrderIdList,Date firstday, Date lastday);

    /**
     * 统计数量
     * @param partOrderIdList 退还单ID
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order_return where part_order_id in ?1 and return_type='goodReturn'", nativeQuery = true)
    Integer fetchReturnGoodQty(List<String> partOrderIdList);
}
