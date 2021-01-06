package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrderStep;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author @author 503202755 2020/12/8
 */
public interface ZkbFePartOrderStepRepository extends GenericRepositoryUUID<ZkbFePartOrderStep> {

    /**
     * 根据partOrderId查询并且按创建时间倒序
     * @param partOrderId fepartOrder主键
     * @return
     */
    List<ZkbFePartOrderStep> findByPartOrderIdOrderByCreateDateDesc(String partOrderId);

    /**
     * 根据ID更新结束时间
     * @param endDate 结束时间
     * @param id 步骤ID
     */
    @Modifying
    @Transactional
    @Query(value = "update ZkbFePartOrderStep set endTime=?1 where id=?2")
    void updateEndDateById(Date endDate, String id);

}
