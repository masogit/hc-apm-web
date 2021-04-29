package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourseTrainUserConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/4/28
 * @description :
 */
public interface ZkbCourseTrainUserConfigRepository extends GenericRepositoryUUID<ZkbCourseTrainUserConfig> {

    /**
     * 根据登录名查找
     * @param loginName 登录名
     * @return
     */
    List<ZkbCourseTrainUserConfig> findByLoginName(String loginName);
}
