package com.ge.apm.dao;

import com.ge.apm.domain.V2_ServiceRequest;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface V2ServiceRequestRepository extends GenericRepository<V2_ServiceRequest> {

    public List<V2_ServiceRequest> findByIdIn(List<String> ids);

    public List<V2_ServiceRequest> findByAssetId(Integer assetId);
//
//    public List<AssetInfo> findByUidIn(List<String> assetUids, Sort sort);
//
//
//    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and ai.uid in ?2")
//    public List<AssetInfo> findByTenantUidAndUidIn(String tenantUid, List<String> uids);
//
//    public List<AssetInfo> findByDepartNumAndSiteUID(String num, String siteUID);
//    public List<AssetInfo> findByFinancingNumAndSiteUID(String num, String siteUID);
//    public List<AssetInfo> findByQrCodeAndSiteUID(String qrCode, String siteUID);
//
//    @Modifying
//    @Transactional
//    @Query("update AssetInfo ai set ai.swapTypeState = :swapTypeState where ai.id = :id")
//    public void updateAssetInfoSwapTypeState(@Param("swapTypeState") String swapTypeState, @Param("id") Integer id);
//
//    @Modifying
//    @Transactional
//    @Query("update AssetInfo ai set ai.sharedAssetTypeUid = :batchSharedAssetTypeUid,ai.assetDeptUID = :orgId where ai.uid in :assetUids")
//    public void batchUpdateAssetInfoSwapType(@Param("batchSharedAssetTypeUid") String batchSharedAssetTypeUid, @Param("assetUids") List<String> assetUids, @Param("orgId") String orgId);
//
//    public List<AssetInfo> findByClinicalDeptUIDIn(List<String> clinicalDeptUIDs);
//
//    @Query("select o from AssetInfo o  where (o.departNum = :departNum or o.financingNum = :financingNum or o.qrCode = :qrCode) and o.siteUID = :siteUID")
//    public List<AssetInfo> findSwapTypeInfo(@Param("departNum") String departNum, @Param("financingNum") String financingNum, @Param("qrCode") String qrCode, @Param("siteUID") String siteUID);
//
//
//    @Modifying
//    @Query("update AssetInfo ai set ai.inventoryStatus=?2 where ai.id in ?1")
//    void updateInventoryStatusAndIdIn(List<Integer> assetIds, Integer ORDER_STATUS_DONE_FINISHED);

//
//    @Query("select siteUID from AssetInfo where id in ?1")
//    Set<String> findSiteUidByAssetIds(Set<Integer> ids);

}
