package com.ge.apm.dao;

import com.ge.apm.domain.SupplierQrCode;
import webapp.framework.dao.GenericRepositoryUUID;

public interface SupplierQrCodeRepository extends GenericRepositoryUUID<SupplierQrCode> {

    SupplierQrCode findFirstByTenantUIDAndType(String tenantUID, String type);
}
