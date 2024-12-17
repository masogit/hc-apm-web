package com.ge.apm.dao;

import com.ge.apm.domain.DataEntryAssetDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface DataEntryAssetDetailRepository extends GenericRepositoryUUID<DataEntryAssetDetail> {

    @Query(value = "select * from data_entry_asset_detail where data_entry_uid= ?1 ", nativeQuery = true)
    List<DataEntryAssetDetail> findByDataEntryUid(String  uid);


    @Modifying
    @Transactional
    @Query(value = "delete from data_entry_asset_detail where data_entry_uid =?1  ", nativeQuery = true)
    int deleteByDataEntryUid(String id);



    @Query(value = "select * from data_entry_asset_detail where asset_group_uid=?1 and asset_group_org_info_uid =?2 and    statistical_date BETWEEN ?3 AND ?4 ", nativeQuery = true)
    List<DataEntryAssetDetail> getByAssetGroupUidAndOrgInfoUidAndDateIn(String uuid, String siteUID, Date updateStartDate, Date updateEndDate);



    @Query(value = "select count(*) from data_entry_asset_detail where asset_uid=?1 and entry_type =?2 and  statistical_date between ?3 and ?4", nativeQuery = true)
    int findByAssetUidAndEntryTypeAndDate(String uid, String entryType, Date startDate, Date endDate);


    @Query(value = "select * from data_entry_asset_detail where asset_uid in ?1 and entry_type =?2 and  statistical_date between ?3 and ?4", nativeQuery = true)
    List<DataEntryAssetDetail> findByAssetUidsAndEntryTypeAndDate(Set<String> assetUids, String entryType, Date startDate, Date endDate);

}
