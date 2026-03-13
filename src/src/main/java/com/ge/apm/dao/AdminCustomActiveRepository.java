package com.ge.apm.dao;

import com.ge.apm.domain.AdminCustomActive;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AdminCustomActiveRepository extends GenericRepositoryUUID<AdminCustomActive>{

    List<AdminCustomActive> findByTenantUID(String tenantUID);

    List<AdminCustomActive> findByHospitalUID(String hospitalUID);

    AdminCustomActive findByHospitalUIDAndFeatureKey(String hospitalUID, String featureKey);

    List<AdminCustomActive> findByTenantUIDAndHospitalUIDIsNull(String tenantUID);

}
