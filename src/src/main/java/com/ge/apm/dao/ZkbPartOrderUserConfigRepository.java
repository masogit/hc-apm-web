package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderUserConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderUserConfigRepository extends GenericRepositoryUUID<ZkbPartOrderUserConfig> {

    List<ZkbPartOrderUserConfig> findByConfigType(String configType);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndOrderType(String configType,String orderType);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndOrderTypeNull(String configType);


    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetType(String configType,String assetType);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndRegion(String configType,String region);

    List<ZkbPartOrderUserConfig> findByLoginNameAndIdNot(String loginName,String id);

    List<ZkbPartOrderUserConfig> findByLoginName(String loginName);

    List<ZkbPartOrderUserConfig> findByConfigTypeIsNot(String configType);
}
