/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AdminFeatureActive;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author 212579464
 */
public interface AdminFeatureActiveRepository extends GenericRepositoryUUID<AdminFeatureActive>{

    List<AdminFeatureActive> findByTenantUID(String tenantUID);

    List<AdminFeatureActive> findByHospitalUID(String hospitalUID);

    AdminFeatureActive findByHospitalUIDAndFeatureKey(String hospitalUID, String featureKey);

    List<AdminFeatureActive> findByFeatureKey(String featureKey);

}
