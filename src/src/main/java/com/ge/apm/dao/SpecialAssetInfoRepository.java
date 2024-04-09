package com.ge.apm.dao;

import com.ge.apm.domain.SpecialAssetInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SpecialAssetInfoRepository extends GenericRepositoryUUID<SpecialAssetInfo> {



    @Query(value = "select * from special_asset_info where asset_id = ?1 ", nativeQuery = true)
    SpecialAssetInfo findByAssetId(Integer id);


    @Query(value = "select * from special_asset_info  where and_assessment_no  = ?1 and tenant_uid=?2  ", nativeQuery = true)
    List<SpecialAssetInfo> findByAssessmentNoAndTenantUID(String assessmentNo, String tenantUID);
}
