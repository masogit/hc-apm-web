package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItemValue;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 503266962
 */
public interface ConfigItemValueRepository extends GenericRepositoryUUID<ConfigItemValue> {

    ConfigItemValue findFirstByKeyAndSiteUid(String key, String siteUid);

}
