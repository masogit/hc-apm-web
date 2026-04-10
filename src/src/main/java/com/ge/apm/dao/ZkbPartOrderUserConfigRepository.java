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


    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndDeletedFlagIsNull(String configType,String assetType);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndLoginNameNotLikeAndDeletedFlagIsNull(String configType,String assetType,String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndLoginNameContainsAndDeletedFlagIsNull(String configType,String assetType,String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndAssetTypeAndLoginNameIsNotContainingAndDeletedFlagIsNull(String configType,String assetType,String loginName);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndRegion(String configType,String region);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndZoneContains(String configType,String zone);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndZoneContainsAndAssetTypeAndDeletedFlagIsNull(String configType,String zone, String assetType);

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

    List<ZkbPartOrderUserConfig> findByConfigTypeAndRegionAndLoginNameContains(String configType, String region, String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndZoneContainsAndLoginNameContains(String configType, String zone, String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndLoginNameContains(String configType, String loginName);

    List<ZkbPartOrderUserConfig> findByConfigTypeAndRegionAndLoginNameIsNotContaining(String configType, String region, String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndZoneContainsAndLoginNameIsNotContaining(String configType, String region, String loginName);
    List<ZkbPartOrderUserConfig> findByConfigTypeAndLoginNameIsNotContaining(String configType, String loginName);

    @Query(value = "select * from zkb_part_order_user_config c where c.login_name = ?1 and c.config_type in ?2 ",nativeQuery = true)
    List<ZkbPartOrderUserConfig> queryForUnrealRoles(String loginName, List<String> configs);

}
