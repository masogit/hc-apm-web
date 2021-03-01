package com.ge.apm.dao;

import com.ge.apm.domain.SharedAssetConfig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;

/**
 * Created by 212605082 on 2018/7/12.
 */
public interface SharedAssetConfigRepository extends GenericRepositoryUUID<SharedAssetConfig> {

    public List<SharedAssetConfig> findBySiteUID(String siteUID);

    @Transactional
    @Modifying
    @Query(value="delete from shared_asset_config where site_uid = ?1",nativeQuery = true)
    public void deleteBySiteUid(String siteUID);
}
