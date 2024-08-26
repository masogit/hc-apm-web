package com.ge.apm.dao;

import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetCostAmountInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface AssetCostAmountInfoRepository  extends GenericRepositoryUUID<AssetCostAmountInfo> {

//    public List<AssetCostAmountInfo> findByDataEntryUid(String  uid);

    @Modifying
    @Transactional
    @Query(value = "delete from asset_cost_amount_info where data_entry_uid =?1  ", nativeQuery = true)
    int deleteByDataEntryUid(String id);




    @Query(value = "select * from asset_cost_amount_info where asset_uid in ?1  AND statistical_date BETWEEN ?2 AND ?3", nativeQuery = true)
    List<AssetCostAmountInfo> getByAssetUidInAndDateIn(List<String> assetUids, Date startDate, Date endDate);
}
