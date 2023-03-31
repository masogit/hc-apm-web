package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItem;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 503266962
 */
public interface ConfigItemRepository extends GenericRepositoryUUID<ConfigItem> {
    @Query(value = "select u.* from config_item u where u.key=?1 and u.category=?2 ", nativeQuery = true)
    public ConfigItem findByDefaultValueForKeyAndCategory(String key, Integer category);


    @Query(value = "select u.* from config_item u where u.key=?1", nativeQuery = true)
    public ConfigItem findByDefaultValueForKey(String key);
}
