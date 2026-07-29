package com.ge.apm.dao;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaDataResult;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * 手术麻醉数据结果Repository
 */
public interface SurgeryAnesthesiaDataResultRepository extends GenericRepositoryUUID<SurgeryAnesthesiaDataResult> {

    /**
     * 根据租户UID和日期范围查询数据结果
     */
    @Query("SELECT r FROM SurgeryAnesthesiaDataResult r WHERE r.tenantUid = ?1 AND r.surgeryDate >= ?2 AND r.surgeryDate <= ?3")
    List<SurgeryAnesthesiaDataResult> findByTenantUidAndDateRange(String tenantUid, Date startDate, Date endDate);

    /**
     * 根据租户UID、机构UID和日期范围查询数据结果
     */
    @Query("SELECT r FROM SurgeryAnesthesiaDataResult r WHERE r.tenantUid = ?1 AND r.siteUid = ?2 AND r.surgeryDate >= ?3 AND r.surgeryDate <= ?4")
    List<SurgeryAnesthesiaDataResult> findByTenantUidAndSiteUidAndDateRange(String tenantUid, String siteUid, Date startDate, Date endDate);
}

