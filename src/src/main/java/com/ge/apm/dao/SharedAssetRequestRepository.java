package com.ge.apm.dao;

import com.ge.apm.domain.SharedAssetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SharedAssetRequestRepository extends GenericRepositoryUUID<SharedAssetRequest> {
    @Modifying
    @Transactional
    @Query("update SharedAssetRequest a set a.state = :state where a.id = :id")
    public void updateState(@Param("id") String id, @Param("state") int state);

    @Modifying
    @Transactional
    @Query("update SharedAssetRequest a set a.state = :state, a.owner = :owner where a.id = :id")
    public void updateStateAndOwner(@Param("id") String id, @Param("state") int state, @Param("owner") String owner);

    public List<SharedAssetRequest> findByState(int state);
    
    @Query(value = "select dept_for_shared_asset from workflow_config where site_uid =?1",nativeQuery = true)
    public List<String> findDeptForSharedAsset(String UID);
    
    @Query(value = "select clinical_dept_uid from asset_info where clinical_dept_uid =?1",nativeQuery = true)
    public List<String> findSharedDeptForAssetInfo(String UID);
    
//    @Query(value = "select id from shared_asset_request where site_uid =?1 AND state in(6,12)",nativeQuery = true)
    @Query(value = "select id from shared_asset_request where site_uid =?1",nativeQuery = true)
    public List<String> findSharedIdforQuery(String siteUID);
    
//    @Query(value = "select id from shared_asset_request where clinical_dept_uid = ?1 and state in(6,12)",nativeQuery = true)
    @Query(value = "select id from shared_asset_request where clinical_dept_uid = ?1",nativeQuery = true)
    public List<String> findSharedIdByOrgUidforQuery(String orgUid);
    
    @Query("select o from SharedAssetRequest o where o.id = :id")
    public List<SharedAssetRequest> findByDept(@Param("id") String id);
    @Query("select o from SharedAssetRequest o where o.type is not null")
    public Page<SharedAssetRequest> find(Pageable request);
    @Query(value = "select name from user_account where login_name = ?1",nativeQuery = true)
    public List<String> findNameByLoginName(String loginName);

//    @Query("select o from AssetInfo o  where (o.departNum = :departNum or o.financingNum = :financingNum or o.qrCode = :qrCode) and o.siteUID = :siteUID")
//    public List<AssetInfo> findSwapTypeInfo(@Param("departNum") String departNum ,@Param("financingNum") String financingNum ,@Param("qrCode") String qrCode, @Param("siteUID") String siteUID );

    @Query(value = "select o from SharedAssetRequest o where o.state in :states")
    public List<SharedAssetRequest> findListByState(@Param("states") List<Integer> states);

    @Query(value = "select  sum(sart.actual_qty) "+
                        " from  shared_asset_request  sar \n" +
                        "left join shared_asset_req_type sart on sart.shared_id=sar.id \n" +
                        "left join  shared_asset_req_detail  sard on sard.borrow_id=sar.id and sard.return_id is null \n" +
                        "where sar.site_uid=?1 and sart.shared_asset_type=?2 and sar.state in (3,6) and sard.id is not null ",nativeQuery = true)
    Integer getBorrowingCount(String siteUid, String sharedAssetTypeId);

    @Query(value = "select  \n" +
            "sum(rent_qty) \n" +
            "from  shared_asset_request  sar \n" +
            "left join shared_asset_req_type sart on sart.shared_id=sar.id \n" +
            "left join  shared_asset_req_detail  sard on sard.return_id=sar.id \n" +
            "where sar.site_uid=?1 and sart.shared_asset_type=?2 and sar.state in (7,8) and sard.id is not null",nativeQuery = true)
    Integer getReturningCount(String siteUid, String sharedAssetTypeId);
}
