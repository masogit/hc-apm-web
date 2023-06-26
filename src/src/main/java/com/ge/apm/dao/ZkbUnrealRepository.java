package com.ge.apm.dao;

import com.ge.apm.domain.ZkbUnreal;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbUnrealRepository extends GenericRepositoryUUID<ZkbUnreal> {

    List<ZkbUnreal> findBySrNumAndItemNo(String srNum, String itemNo);

    List<ZkbUnreal> findByPartOrderIdAndItemNo(String partOrderId, String itemNo);
}
