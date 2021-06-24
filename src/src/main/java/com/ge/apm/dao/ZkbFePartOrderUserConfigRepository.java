package com.ge.apm.dao;

import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503202755
 * @date : 2020/12/14
 * @description: 获取FE审批角色
 */
public interface ZkbFePartOrderUserConfigRepository extends GenericRepositoryUUID<ZkbFePartOrderUserConfig> {

    /**
     * 根据登录名获取角色配置信息
     * @param loginName 登录名
     * @return 角色配置列表
     */
    List<ZkbFePartOrderUserConfig> findByLoginName(String loginName);

    /**
     * 根据用户名和ID查找
     * @param loginName 用户名
     * @param id 主键
     * @return
     */
    List<ZkbFePartOrderUserConfig> findByLoginNameAndIdNot(String loginName, String id);

    /**
     * 根据登录用户名和类型查找
     * @param loginName 登录用户名
     * @param configType 配置类型
     * @return
     */
    List<ZkbFePartOrderUserConfig> findByLoginNameAndConfigType(String loginName,String configType);

}
