/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.SharedAssetReqType;

import java.util.List;

/**
 *
 * @author caoyong
 */
public interface SharedAssetReqTypeRepository extends GenericRepositoryUUID<SharedAssetReqType>{
    public org.springframework.data.domain.Page<SharedAssetReqType> findBySharedId(org.springframework.data.domain.Pageable request, String sharedId);

    @Query("select o from SharedAssetReqType o  where o.sharedId = :sharedId")
    public List<SharedAssetReqType> findSwapTypeId(@Param("sharedId") String sharedId);

    @Query("select o from SharedAssetReqType o  where o.sharedId = :sharedId and o.sharedAssetTypeId = :sharedAssetTypeId")
    public SharedAssetReqType findByIdsharedIdandSwapTypeid(@Param("sharedId") String sharedId, @Param("sharedAssetTypeId") String swapTypeId);

    @Query("select o from SharedAssetReqType o  where o.sharedId = :sharedId and o.sharedAssetTypeId = :sharedAssetTypeId")
    public SharedAssetReqType findByIdSharedIdandSharedAssetTypeId(@Param("sharedId") String sharedId, @Param("sharedAssetTypeId") String sharedAssetTypeId);

}
