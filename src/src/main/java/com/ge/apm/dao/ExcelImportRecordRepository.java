package com.ge.apm.dao;

import com.ge.apm.domain.his.ExcelImportRecord;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ExcelImportRecordRepository extends GenericRepositoryUUID<ExcelImportRecord> {
    List<ExcelImportRecord> findByModuleAndTenantUid(String hisEquipmentDataSource, String tenantUID);
}
