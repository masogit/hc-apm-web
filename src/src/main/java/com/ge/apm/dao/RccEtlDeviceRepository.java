package com.ge.apm.dao;

import com.ge.apm.domain.RccEtlDevice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 503266962
 */
public interface RccEtlDeviceRepository extends GenericRepository<RccEtlDevice> {

    @Modifying
    @Transactional
    @Query(value = "update rcc_etl_device set asset_uid=?2 where id=?1", nativeQuery = true)
    void updateAssetById(Integer id, String assetUid);
}
