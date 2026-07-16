package com.ge.apm.dao;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentMatch;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 手麻系统设备匹配Repository
 */
public interface SurgeryAnesthesiaEquipmentMatchRepository extends GenericRepositoryUUID<SurgeryAnesthesiaEquipmentMatch> {

    @Query("SELECT m FROM SurgeryAnesthesiaEquipmentMatch m WHERE m.connectId = ?1")
    SurgeryAnesthesiaEquipmentMatch findByConnectId(String connectId);

    @Query("SELECT m FROM SurgeryAnesthesiaEquipmentMatch m WHERE m.assetUid = ?1 AND m.tenantUid = ?2")
    List<SurgeryAnesthesiaEquipmentMatch> findByAssetUidAndTenantUid(String assetUid, String tenantUid);

    @Query("SELECT m FROM SurgeryAnesthesiaEquipmentMatch m WHERE m.tenantUid = ?1 AND m.orgCode = ?2")
    List<SurgeryAnesthesiaEquipmentMatch> findByTenantUidAndOrgCode(String tenantUid, String orgCode);
}
