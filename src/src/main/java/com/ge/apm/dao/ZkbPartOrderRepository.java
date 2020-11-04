package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderRepository extends GenericRepositoryUUID<ZkbPartOrder> {

    @Query("select max(p.partOrderIndex) from ZkbPartOrder p where p.partOrderNo like ?1 ")
    Object getMaxIndex(String dateCondition);

    /**
     * 根据合同ID获取所有归还单
     * @param contractIdList
     * @param firstday 某月第一天
     * @param lastday 某月最后一天
     * @return
     */
    List<ZkbPartOrder> findByContractIdInAndCreatedDateIsGreaterThanEqualAndCreatedDateLessThanEqual(List<String> contractIdList, Date firstday, Date lastday);

    /**
     * 根据多个合同ID，获取总申请数
     * @param contractIdList
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order where contract_id in ?1", nativeQuery = true)
    Integer fetchQty(List<String> contractIdList);

    /**
     * 根据多个合同ID，获取总申请数
     * @param contractIdList
     * @return
     */
    @Query(value = "SELECT sum(return_qty) from zkb_part_order where contract_id in ?1 and return_type='goodReturn'", nativeQuery = true)
    Integer fetchReturnGoodQty(List<String> contractIdList);
}

