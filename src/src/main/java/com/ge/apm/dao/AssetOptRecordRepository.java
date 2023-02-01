package com.ge.apm.dao;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.AssetOptRecord;

import webapp.framework.dao.GenericRepositoryUUID;

public interface AssetOptRecordRepository extends GenericRepositoryUUID<AssetOptRecord> {


    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1",nativeQuery=true)
    public List<AssetOptRecord> fetchRecordByAssetUid(String assetUid);
    
    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1 and o.status=?2",nativeQuery=true)
    public List<AssetOptRecord> fetchRecordByAssetUid(String assetUid,int status);

    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1 order by o.start_date desc limit 5",nativeQuery=true)
    List<AssetOptRecord> getAssetOptRecordListLimit5(String assetUid);
    @Query(value="select o.* from asset_opt_record o where o.asset_uid=?1 and o.start_date >= ?2 and (o.end_date <= ?3 or o.end_date is null) order by o.start_date desc ",nativeQuery=true)
    List<AssetOptRecord> getAseetOptRecordListByDate(String assetUid, Date startDate, Date endDate);
}
