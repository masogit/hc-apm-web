package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

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

    /**
     * 根据单号获取数据
     * @param partOrderNo
     * @return
     */
    List<ZkbFePartOrder> findByPartOrderNo(String partOrderNo);


    /**
     * 根据父流程编号批量获取数据
     * @param fatherPartOrderNoList 父流程编号
     * @return
     */
    List<ZkbFePartOrder> findByPartOrderNoIn(List<String> fatherPartOrderNoList);

}
