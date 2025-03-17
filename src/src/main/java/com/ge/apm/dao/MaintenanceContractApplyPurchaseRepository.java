package com.ge.apm.dao;

import com.ge.apm.domain.budget.MaintenanceContractApplyPurchase;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.lang.annotation.Native;

public interface MaintenanceContractApplyPurchaseRepository  extends GenericRepositoryUUID<MaintenanceContractApplyPurchase> {


    @Query(value = "select max(application_num) from maintenance_contract_apply_purchase where application_num like ?1", nativeQuery = true)
    String getMaxNum(String prefix);
}
