package com.ge.apm.dao;

import com.ge.apm.domain.SupplierUser;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SupplierUserRepository extends GenericRepositoryUUID<SupplierUser> {

    List<SupplierUser> findBySupplierIDAndStatus(Integer supplierID, String status);

    List<SupplierUser> findBySupplierIDAndUserIDAndStatus(Integer supplierID, Integer userID, String status);

    void deleteBySupplierIDAndStatus(Integer supplierID, String status);

    void deleteBySupplierIDAndStatusAndUserIDIn(Integer supplierID, String status, List<Integer> userIDList);

}
