package com.ge.apm.dao;

import com.ge.apm.domain.AssetDepartmentConvert;

import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetDepartmentConvertRepository extends GenericRepositoryUUID<AssetDepartmentConvert> {



    @Query(value="select * from asset_department_convert where asset_uid=?1 order by created_date desc limit 1",nativeQuery=true)
    AssetDepartmentConvert getByAssetUidOne(String assetUid);
}
