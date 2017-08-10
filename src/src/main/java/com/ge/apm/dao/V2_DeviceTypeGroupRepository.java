package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.V2_DeviceTypeGroup;

import webapp.framework.dao.GenericRepository;

public interface V2_DeviceTypeGroupRepository extends GenericRepository<V2_DeviceTypeGroup>{
	List<V2_DeviceTypeGroup> findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(String tenantUID, String institutionUID,String hospitalUID, String siteUID);

	List<V2_DeviceTypeGroup> findByDeviceTypeIdAndTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(Integer deviceTypeId, String tenantUID, String institutionUID, String hospitalUID, String siteUID);

    @Modifying
    @Transactional
	@Query(value = "delete from V2_DeviceTypeGroup where deviceTypeId = ?1 and tenantUID = ?2 and institutionUID = ?3 and hospitalUID = ?4 and siteUID = ?5")
	public void deleteRecordByDeviceTypeIdAndUids(Integer deviceTypeId, String tenantUID, String institutionUID,String hospitalUID, String siteUID);
}
