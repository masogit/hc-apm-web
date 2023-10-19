package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetApplicationAsset;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author liyongjian
 */
public interface BudgetApplicationAssetRepository extends GenericRepositoryUUID<BudgetApplicationAsset> {

    /**
     * count using budget asset list
     *
     * @param budgetAssetId budgetAssetId
     * @return count
     */
    @Query(nativeQuery = true, value = "select count(tb.application_asset_id) from budget_application ta" +
            " left join budget_application_asset tb on ta.id = tb.application_id" +
            " where ta.is_deleted = 0 and tb.application_asset_id = ?1")
    Integer countByApplicationAssetId(String budgetAssetId);

    BudgetApplicationAsset findByApplicationId(String applicationId);



}
