package com.ge.apm.dao;

import com.ge.apm.domain.KeyAssetReport;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 503266962
 */
public interface KeyAssetReportRepository extends GenericRepositoryUUID<KeyAssetReport> {

    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and dept_id = ?2 and report_year = ?3")
    int countByReport(String assetName, Integer deptId, Integer reportYear);

    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and dept_id = ?2 and report_year = ?3 and id != ?4")
    int countByReportAndId(String assetName, Integer deptId, Integer reportYear, String uid);
}
