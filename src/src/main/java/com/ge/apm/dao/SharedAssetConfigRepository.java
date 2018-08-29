package com.ge.apm.dao;

import com.ge.apm.domain.SharedAssetConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2018/7/12.
 */
public interface SharedAssetConfigRepository extends GenericRepositoryUUID<SharedAssetConfig> {

    public List<SharedAssetConfig> findBySiteUID(String siteUID);

}
