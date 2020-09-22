package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderReturn;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
public interface ZkbPartOrderReturnRepository extends GenericRepositoryUUID<ZkbPartOrderReturn> {
    List<ZkbPartOrderReturn> findByPartOrderId(String partOrderId);
    List<ZkbPartOrderReturn> findByPartOrderIdAndReturnTypeIsNot(String partOrderId,String returnType);
    List<ZkbPartOrderReturn> findByPartOrderIdAndDataStatus(String partOrderId,String DataStatus);

    void deleteByPartOrderId(String partOrderId);

    List<ZkbPartOrderReturn> findByPartOrderIdAndPartNo(String partOrderId,String partNo);

    List<ZkbPartOrderReturn> findByPartOrderIdAndPartNoAndIdIsNot(String partOrderId,String partNo,String id);
}
