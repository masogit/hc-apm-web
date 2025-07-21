package com.ge.apm.dao;

import com.ge.apm.domain.SupplierContactPerson;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SupplierContactPersonRepository extends GenericRepositoryUUID<SupplierContactPerson> {
    List<SupplierContactPerson> findBySupplierUid(String supplierUid);
    List<SupplierContactPerson> findByVenderCompanyId(String venderCompanyId);
}
