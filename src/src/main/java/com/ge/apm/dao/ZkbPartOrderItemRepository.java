package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderItem;
import com.ge.apm.domain.ZkbPartOrderItemVo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
public interface ZkbPartOrderItemRepository extends GenericRepositoryUUID<ZkbPartOrderItem> {
    List<ZkbPartOrderItem> findByPartOrderId(String partOrderId);

    void deleteByPartOrderId(String partOrderId);

    @Query("select new com.ge.apm.domain.ZkbPartOrderItemVo(i.partNo,i.partName,i.partQty,i.serialNumber,i.orderNo,i.price,i.remark,z.partOrderNo,z.currentStep,z.status,z.srNo,i.prePartQty) from ZkbPartOrder z, ZkbPartOrderItem i where z.id = i.partOrderId and z.srNum = ?1 and z.id !=?2 and z.assetTypeDi = ?3")
    List<ZkbPartOrderItemVo>  queryHistoryPart(String srNum, String id,String assetTypeDi);
}
