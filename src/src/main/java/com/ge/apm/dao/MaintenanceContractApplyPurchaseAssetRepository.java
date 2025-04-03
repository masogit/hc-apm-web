package com.ge.apm.dao;

import com.ge.apm.domain.budget.MaintenanceContractApplyPurchaseAsset;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface MaintenanceContractApplyPurchaseAssetRepository extends GenericRepositoryUUID<MaintenanceContractApplyPurchaseAsset> {

    List<MaintenanceContractApplyPurchaseAsset> findByApplicationId(String applicationId);

    @Transactional
    @Modifying
    @Query(value = "delete from maintenance_contract_apply_purchase_asset where application_id = ?1", nativeQuery = true)
    void deleteByApplicationId(String applicationId);

}
