package com.ge.apm.dao;

import com.ge.apm.domain.DataEntryAssetDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


public interface DataEntryAssetDetailRepository extends GenericRepositoryUUID<DataEntryAssetDetail> {

    @Query(value = "select * from data_entry_asset_detail where data_entry_uid= ?1 ", nativeQuery = true)
    List<DataEntryAssetDetail> findByDataEntryUid(String  uid);


    @Modifying
    @Transactional
    @Query(value = "delete from data_entry_asset_detail where data_entry_uid =?1  ", nativeQuery = true)
    int deleteByDataEntryUid(String id);
}
