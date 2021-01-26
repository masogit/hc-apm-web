package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrderReturn;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/1/4
 */
public interface ZkbFePartOrderReturnRepository extends GenericRepositoryUUID<ZkbFePartOrderReturn> {

    /**
     * 根据备件申请主键获取设备归还列表
     * @param partOrderId 维修流程ID
     * @return
     */
    List<ZkbFePartOrderReturn> findByPartOrderId(String partOrderId);
}
