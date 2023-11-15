package com.ge.apm.dao;

import com.ge.apm.domain.PurchaseVender;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


/**
 * @author lyj
 */
public interface PurchaseVenderRepository extends GenericRepositoryUUID<PurchaseVender> {

    List<PurchaseVender> findByPurchaseIdAndVenderId(String purchaseId, String venderId);

    List<PurchaseVender> findByPurchaseId(String purchaseId);
}
