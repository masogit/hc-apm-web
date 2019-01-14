package com.ge.apm.dao;


import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.AssetOptRecord;

import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetOptRecordRepository extends GenericRepositoryUUID<AssetOptRecord> {


    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1",nativeQuery=true)
    public List<AssetOptRecord> fetchRecordByAssetUid(String assetUid);
    
    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1 and o.status=?2",nativeQuery=true)
    public List<AssetOptRecord> fetchRecordByAssetUid(String assetUid,int status);
    
    
}
