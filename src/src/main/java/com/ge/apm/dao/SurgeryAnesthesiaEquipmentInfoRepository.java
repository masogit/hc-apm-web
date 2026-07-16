package com.ge.apm.dao;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 手术室绩效设备配置Repository
 */
public interface SurgeryAnesthesiaEquipmentInfoRepository extends GenericRepositoryUUID<SurgeryAnesthesiaEquipmentInfo> {

    @Query("SELECT e FROM SurgeryAnesthesiaEquipmentInfo e WHERE e.tenantUid = ?1 AND e.assetUid = ?2")
    SurgeryAnesthesiaEquipmentInfo findByTenantUidAndAssetUid(String tenantUid, String assetUid);

    @Query("SELECT e FROM SurgeryAnesthesiaEquipmentInfo e WHERE e.tenantUid = ?1")
    List<SurgeryAnesthesiaEquipmentInfo> findByTenantUid(String tenantUid);

    @Query("SELECT e FROM SurgeryAnesthesiaEquipmentInfo e WHERE e.tenantUid = ?1 AND e.assetUid IN ?2")
    List<SurgeryAnesthesiaEquipmentInfo> findByTenantUidAndAssetUidIn(String tenantUid, List<String> assetUids);

    @Query("SELECT e FROM SurgeryAnesthesiaEquipmentInfo e WHERE e.tenantUid = ?1 AND e.siteUid = ?2")
    List<SurgeryAnesthesiaEquipmentInfo> findByTenantUidAndSiteUid(String tenantUid, String siteUid);
}