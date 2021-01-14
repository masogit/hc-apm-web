package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author @author 503202755 2020/12/8
 */
public interface ZkbFePartOrderRepository extends GenericRepositoryUUID<ZkbFePartOrder> {

    /**
     * 获取索引
     * @param dateCondition 日期条件
     * @return
     */
    @Query("select max(p.partOrderIndex) from ZkbFePartOrder p where p.partOrderNo like ?1 ")
    Object getMaxIndex(String dateCondition);

}
