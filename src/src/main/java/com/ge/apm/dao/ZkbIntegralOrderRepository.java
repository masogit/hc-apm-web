package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIntegralOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbIntegralOrderRepository extends GenericRepositoryUUID<ZkbIntegralOrder> {

    @Query("select max(p.orderIndex) from ZkbIntegralOrder p where p.integralOrderNo like ?1 ")
    Object getMaxIndex(String dateCondition);

}
