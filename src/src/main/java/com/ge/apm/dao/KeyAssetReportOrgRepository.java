package com.ge.apm.dao;

import com.ge.apm.domain.KeyAssetReport;
import com.ge.apm.domain.KeyAssetReportOrg;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266962
 */
public interface KeyAssetReportOrgRepository extends GenericRepositoryUUID<KeyAssetReportOrg> {

    @Transactional
    @Modifying
    void deleteByKeyAssetReportId(String id);

    List<KeyAssetReportOrg> findByKeyAssetReportId(String id);

    @Transactional
    @Modifying
    void deleteByKeyAssetReportIdAndOrgIdIn(String id, List<String> deleteDeptUids);

}
