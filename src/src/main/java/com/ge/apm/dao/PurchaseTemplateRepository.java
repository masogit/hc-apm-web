package com.ge.apm.dao;

import com.ge.apm.domain.budget.PurchaseTemplate;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


/**
 * @author lyj
 */
public interface PurchaseTemplateRepository extends GenericRepositoryUUID<PurchaseTemplate> {

    List<PurchaseTemplate> findByPurchaseId(String purchaseId);

}
