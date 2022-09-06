package com.ge.apm.dao;

import com.ge.apm.domain.RccEtlDevice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 503266962
 */
public interface RccEtlDeviceRepository extends GenericRepository<RccEtlDevice> {

    @Modifying
    @Transactional
    @Query(value = "update rcc_etl_device set asset_uid=:#{#device.assetUid} " +
            "where aet = :#{#device.pk.aet} and device_type = :#{#device.pk.deviceType} " +
            "and org_id = :#{#device.pk.orgId} and login_name = :#{#device.pk.loginName}", nativeQuery = true)
    void updateAssetById(@Param("device") RccEtlDevice rccEtlDevice);
}
