package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItemValue;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266962
 */
public interface ConfigItemValueRepository extends GenericRepositoryUUID<ConfigItemValue> {

    ConfigItemValue findFirstByKeyAndSiteUid(String key, String siteUid);

    @Query(value = "select u.* from config_item_value u where u.key=?1", nativeQuery = true)
    List<ConfigItemValue> findByKey(String key);
}
