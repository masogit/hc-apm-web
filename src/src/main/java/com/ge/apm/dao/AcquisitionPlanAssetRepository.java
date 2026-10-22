package com.ge.apm.dao;

import com.ge.apm.domain.acquisition.AcquisitionPlanAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AcquisitionPlanAssetRepository extends GenericRepositoryUUID<AcquisitionPlanAsset> {

    List<AcquisitionPlanAsset> findByAcquisitionPlanId(String acquisitionPlanId);

    List<AcquisitionPlanAsset> findByAssetUid(String assetUid);

    @Modifying
    @Query("DELETE FROM AcquisitionPlanAsset a WHERE a.acquisitionPlanId = :planId AND a.assetUid = :assetUid")
    void deleteByAcquisitionPlanIdAndAssetUid(@Param("planId") String planId, @Param("assetUid") String assetUid);

    @Query("SELECT a FROM AcquisitionPlanAsset a WHERE a.tenantUID = :tenantUid " +
           "AND EXISTS (SELECT 1 FROM HisEquipmentDataResult h WHERE h.assetUid = a.assetUid AND h.tenantUid = :tenantUid) " +
           "ORDER BY a.createdDate DESC")
    Page<AcquisitionPlanAsset> findWithPerformanceData(@Param("tenantUid") String tenantUid, Pageable pageable);

    @Query("SELECT a FROM AcquisitionPlanAsset a WHERE a.tenantUID = :tenantUid " +
           "AND NOT EXISTS (SELECT 1 FROM HisEquipmentDataResult h WHERE h.assetUid = a.assetUid AND h.tenantUid = :tenantUid) " +
           "ORDER BY a.createdDate DESC")
    Page<AcquisitionPlanAsset> findWithoutPerformanceData(@Param("tenantUid") String tenantUid, Pageable pageable);
}
