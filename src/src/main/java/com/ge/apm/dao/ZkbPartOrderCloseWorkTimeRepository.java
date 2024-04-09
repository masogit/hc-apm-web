package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderCloseWorkTime;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2023/10/24
 * @description :
 */
public interface ZkbPartOrderCloseWorkTimeRepository extends GenericRepositoryUUID<ZkbPartOrderCloseWorkTime> {


    List<ZkbPartOrderCloseWorkTime> findByPartOrderId(String partOrderId);
    List<ZkbPartOrderCloseWorkTime> findBySrNo(String srNo);
    List<ZkbPartOrderCloseWorkTime> findByCloseId(String closeId);
}
