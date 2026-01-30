package com.ge.apm.dao;

import com.ge.apm.domain.OwnerConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2019/2/26.
 */
public interface OwnerConfigRepository extends GenericRepositoryUUID<OwnerConfig> {

    public List<OwnerConfig> findByDeptUIDAndSiteUID(String deptUID, String siteUID);

    public List<OwnerConfig> findByAssetGroupIDAndSiteUID(Integer assetGroupID, String siteUID);

    public List<OwnerConfig> findByAssetGroupIDAndAssetGroupSecondaryIDAndSiteUID(Integer assetGroupID, Integer assetGroupSecondaryID, String siteUID);

    List<OwnerConfig> findByDeptUIDAndSiteUIDAndAssetType(String clinicalDeptUID, String siteUID, String assetType);
}
