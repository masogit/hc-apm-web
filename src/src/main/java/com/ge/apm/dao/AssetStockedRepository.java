/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AssetStocked;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetStockedRepository extends GenericRepositoryUUID<AssetStocked> {
    List<AssetStocked> findByInStockNo(String inStockNo);

    List<AssetStocked> findByOutStockNo(String inStockNo);

    List<AssetStocked> findByArrivalListId(String arrivalListId);

    AssetStocked findByAssetUid(String assetUid);

    @Query(value = "select RIGHT(max(in_stock_no), 3) from asset_stocked ali, asset_info al where al.tenant_uid = ?1 and ali.asset_uid = al.uid and ali.in_stock_no like ?2",nativeQuery = true)
    String queryInStockNo(String tenantUid, String inStockNo);

    @Query(value = "select RIGHT(max(out_stock_no),3) from asset_stocked ali, asset_info al where al.tenant_uid = ?1 and ali.asset_uid = al.uid and ali.out_stock_no like ?2",nativeQuery = true)
    String queryOutStockNo(String tenantUid, String outStockNo);

    @Query(value = "select RIGHT(max(returned_stock_no),3) from asset_stocked ali, asset_info al where al.tenant_uid = ?1 and ali.asset_uid = al.uid and ali.returned_stock_no like ?2",nativeQuery = true)
    String queryReturnedStockNo(String tenantUid, String outStockNo);

    @Transactional
    @Modifying
    void deleteByArrivalItemIdIn(List<String> arrivalItemIds);
}
