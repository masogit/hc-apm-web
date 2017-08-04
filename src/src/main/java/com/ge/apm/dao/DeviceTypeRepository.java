package com.ge.apm.dao;

import java.util.List;

import com.ge.apm.domain.V2_DeviceType;

import webapp.framework.dao.GenericRepository;

public interface DeviceTypeRepository extends GenericRepository<V2_DeviceType> {
	List<V2_DeviceType> findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(String tenantUID,String institutionUID, String hospitalUID, String siteUID);

}
