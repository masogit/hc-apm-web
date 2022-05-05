package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetApplicationAsset;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author Yangfan, Liu
 */
public interface BudgetApplicationAssetRepository extends GenericRepositoryUUID<BudgetApplicationAsset> {

    /**
     * count using budget asset list
     *
     * @param budgetAssetId budgetAssetId
     * @return count
     */
    Integer countByApplicationAssetId(String budgetAssetId);
}
