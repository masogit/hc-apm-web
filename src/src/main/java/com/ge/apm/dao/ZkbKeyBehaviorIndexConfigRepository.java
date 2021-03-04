package com.ge.apm.dao;

import com.ge.apm.domain.ZkbKeyBehaviorIndexConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author @author 503202755 2020/10/20
 */
public interface ZkbKeyBehaviorIndexConfigRepository extends GenericRepositoryUUID<ZkbKeyBehaviorIndexConfig> {

    /**
     * 根据租户ID查找配置信息
     * @param tenantUID
     * @return
     */
    List<ZkbKeyBehaviorIndexConfig> findByTenantUID(String tenantUID);


    /**
     * 根据租户ID和权重ID查找配置信息
     * @param tenantUID 租户ID
     * @param id 权重ID
     * @return
     */
    List<ZkbKeyBehaviorIndexConfig> findByTenantUIDAndIdNot(String tenantUID,String id);

    /**
     * 获取默认权重
     * @return 返回权重信息
     */
    @Query(value = "select * from zkb_key_behavior_index_config where default_config = 1 limit 1 ", nativeQuery = true)
    ZkbKeyBehaviorIndexConfig getDefaultConfig();

}
