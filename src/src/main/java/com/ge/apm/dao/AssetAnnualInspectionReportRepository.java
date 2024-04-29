package com.ge.apm.dao;

import com.ge.apm.domain.AssetAnnualInspectionReport;
import com.ge.apm.domain.AuditGroupConfigurationDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetAnnualInspectionReportRepository  extends GenericRepositoryUUID<AssetAnnualInspectionReport> {

    @Modifying
    @Transactional
    @Query(value = "delete from asset_annual_inspection_report where special_asset_uid =?1  ", nativeQuery = true)
    int deleteBySpecialAssetUid(String id);
}
