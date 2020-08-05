package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderStep;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderStepRepository extends GenericRepositoryUUID<ZkbPartOrderStep> {
    List<ZkbPartOrderStep> findByPartOrderId(String partOrderId);

    void deleteByPartOrderId(String partOrderId);
}
