package com.ge.apm.dao;

import com.ge.apm.domain.budget.BudgetAssetInfo;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author Yangfan, Liu
 */
public interface BudgetAssetInfoRepository extends GenericRepositoryUUID<BudgetAssetInfo> {

    List<BudgetAssetInfo> findByTenantUIDAndIsDeleted(String tenantUid,Integer isDeleted);
}
