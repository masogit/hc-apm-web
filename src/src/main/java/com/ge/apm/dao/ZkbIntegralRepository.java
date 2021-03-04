package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIntegral;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbIntegralRepository extends GenericRepositoryUUID<ZkbIntegral> {
    public ZkbIntegral findFirstByTenantUID(String tenantUid);


}
