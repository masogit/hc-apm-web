package com.ge.apm.dao;

import com.ge.apm.domain.IdleEquipmentApplication;
import com.ge.apm.domain.IdleEquipmentStock;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface IdleEquipmentStockRepository extends GenericRepositoryUUID<IdleEquipmentStock> {

    public IdleEquipmentStock findByUid(String uid);



    public List<IdleEquipmentStock> findByAssetUidAndStatusIn(String uid,List<Integer> status);

    @Query("SELECT s.assetUid FROM IdleEquipmentStock s WHERE (s.stockHospitalUid IN (?1) OR s.stockSiteUid IN (?1)) AND s.status = ?2")
    List<String> findLentOutAssetUids(List<String> orgUids, Integer status);

    @Query("SELECT s.assetUid FROM IdleEquipmentStock s WHERE (s.borrowHospitalUid IN (?1) OR s.borrowSiteUid IN (?1)) AND s.status = ?2")
    List<String> findBorrowedInAssetUids(List<String> orgUids, Integer status);

}
