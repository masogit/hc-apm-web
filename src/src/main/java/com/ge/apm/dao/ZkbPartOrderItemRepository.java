package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractAsset;
import com.ge.apm.domain.ZkbPartOrderItem;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
public interface ZkbPartOrderItemRepository extends GenericRepositoryUUID<ZkbPartOrderItem> {
    public List<ZkbPartOrderItem> findByPartOrderId(String partOrderId);
}
