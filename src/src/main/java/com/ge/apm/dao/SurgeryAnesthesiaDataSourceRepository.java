package com.ge.apm.dao;

import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaDataSource;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * 手术麻醉源数据Repository
 */
public interface SurgeryAnesthesiaDataSourceRepository extends GenericRepositoryUUID<SurgeryAnesthesiaDataSource> {

    /**
     * 获取租户最新的手术日期
     */
    @Query("SELECT MAX(s.surgeryDate) FROM SurgeryAnesthesiaDataSource s WHERE s.tenantUid = ?1")
    Date getTenantMaxSurgeryDate(String tenantUid);

    /**
     * 根据租户UID查询所有源数据
     */
    @Query("SELECT s FROM SurgeryAnesthesiaDataSource s WHERE s.tenantUid = ?1")
    List<SurgeryAnesthesiaDataSource> findByTenantUid(String tenantUid);
}
