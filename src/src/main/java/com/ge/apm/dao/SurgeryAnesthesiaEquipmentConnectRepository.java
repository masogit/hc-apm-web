package com.ge.apm.dao;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentConnect;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 手麻系统设备Repository
 */
public interface SurgeryAnesthesiaEquipmentConnectRepository extends GenericRepositoryUUID<SurgeryAnesthesiaEquipmentConnect> {

    @Query(value = "SELECT u.* FROM surgery_anesthesia_equipment_connect u WHERE u.is_matched = false LIMIT 100", nativeQuery = true)
    List<SurgeryAnesthesiaEquipmentConnect> findUnmatchedLimit();

    @Transactional
    @Modifying
    @Query(value = "UPDATE surgery_anesthesia_equipment_connect SET is_matched = true WHERE id IN ?1", nativeQuery = true)
    void updateMatchedByIds(List<String> ids);

    @Transactional
    @Modifying
    @Query(value = "UPDATE surgery_anesthesia_equipment_connect SET is_matched = false WHERE id = ?1", nativeQuery = true)
    void updateUnmatchedById(String id);

    @Query("SELECT o FROM SurgeryAnesthesiaEquipmentConnect o WHERE o.isMatched = true AND o.id IN ?1")
    List<SurgeryAnesthesiaEquipmentConnect> findMatchedByIds(List<String> ids);

    SurgeryAnesthesiaEquipmentConnect findByDeviceCode(String deviceCode);

    SurgeryAnesthesiaEquipmentConnect findByDeviceCodeAndOrgCode(String deviceCode, String orgCode);

    SurgeryAnesthesiaEquipmentConnect findByDeviceCodeAndOrgCodeAndSiteUid(String deviceCode, String orgCode, String siteUid);
}
