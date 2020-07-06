package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderRepository extends GenericRepositoryUUID<ZkbPartOrder> {

    @Query("select max(p.partOrderIndex) from ZkbPartOrder p ")
    Object getMaxIndex();
}

