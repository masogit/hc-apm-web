package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrderWorkflowConfig;

import webapp.framework.dao.GenericRepository;

public interface V2_WorkOrderWorkflowConfigRepository extends GenericRepository<V2_WorkOrderWorkflowConfig> {

	V2_WorkOrderWorkflowConfig findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(String tenantUID, String institutionUID,String hospitalUID, String siteUID);

	
}
