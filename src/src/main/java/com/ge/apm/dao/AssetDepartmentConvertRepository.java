package com.ge.apm.dao;

import com.ge.apm.domain.AssetDepartmentConvert;

import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetDepartmentConvertRepository extends GenericRepositoryUUID<AssetDepartmentConvert> {

    @Query(value="select * from asset_department_convert where asset_uid=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetDepartmentConvert getByAssetUidOne(String assetUid);

    @Query(value="select count(*) from asset_department_convert where current_status=3  and  convert_no LIKE ?1% ",nativeQuery=true)
    int selectCurrentFinishCount(String prefix);

    @Query(value="SELECT adc.* from asset_department_convert adc, asset_info ai where adc.asset_uid = ai.uid and ai.tenant_uid = ?1 and adc.business_number = ?2",nativeQuery=true)
    List<AssetDepartmentConvert> findByTenantAndBusinessNumber(String tenantUid, String businessNumber);
}
