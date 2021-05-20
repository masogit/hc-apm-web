package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderUserConfig;
import org.springframework.data.jpa.repository.Query;
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

    @Query("select c from ZkbPartOrderUserConfig  c where c.configType = ?1 and (c.orderType is null or c.orderType = '')")
    List<ZkbPartOrderUserConfig> findByConfigTypeAndOrderTypeNull(String configType);


    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetType(String configType,String assetType);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndLoginNameContains(String configType,String assetType,String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndLoginNameIsNotContaining(String configType,String assetType,String loginName);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndRegion(String configType,String region);

    List<ZkbPartOrderUserConfig> findByLoginNameAndIdNot(String loginName,String id);

    List<ZkbPartOrderUserConfig> findByLoginName(String loginName);

    /**
     * 根据loginName 和配置类型查找
     * @param loginName
     * @param configType
     * @return
     */
    List<ZkbPartOrderUserConfig> findByLoginNameAndConfigType(String loginName,String configType);
    List<ZkbPartOrderUserConfig> findByLoginNameAndConfigTypeIsIn(String loginName,List<String> conFigTypes);
}
