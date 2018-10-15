package com.ge.apm.dao;
import java.util.List;
import com.ge.apm.domain.SharedAssetReqDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

public interface SharedAssetReqDetailRepository extends GenericRepositoryUUID<SharedAssetReqDetail> {
    
    public Page<SharedAssetReqDetail> findBySharedId(Pageable request, String sharedId);
//    public SharedAssetReqDetail findByAssetInfoAndState(@Param("assetInfoId") String assetInfoId,@Param("state") String state);
    public Page<SharedAssetReqDetail> findByReturnId(Pageable request, String returnId);
    public Page<SharedAssetReqDetail> findByBorrowId(Pageable request, String borrowId);
    @Query("select o from SharedAssetReqDetail o  where o.sharedId = :sharedId")
    public List<SharedAssetReqDetail> findBySharedId(@Param("sharedId") String sharedId);
    @Query("select o from SharedAssetReqDetail o  where o.borrowId = :borrowId")
    public List<SharedAssetReqDetail> findByBorrowId(@Param("borrowId") String borrowId);
    @Query("select o from SharedAssetReqDetail o  where o.returnId = :returnId")
    public List<SharedAssetReqDetail> findByReturnId(@Param("returnId") String returnId);
    
    @Query("select o from SharedAssetReqDetail o  where o.sharedId = :sharedId")
    public List<SharedAssetReqDetail> selectBySharedId(@Param("sharedId") String sharedId);
    
    @Query("select o from SharedAssetReqDetail o  where o.sharedId = :sharedId")
    public List<SharedAssetReqDetail> selectByIouId(@Param("sharedId") String sharedId);
    
    @Query("select o from SharedAssetReqDetail o  where o.assetInfoId = :assetInfoId and o.state = :state")
    public List<SharedAssetReqDetail> findByAssetInfoAndUid(@Param("assetInfoId") String assetInfoId);

    @Query("select o from SharedAssetReqDetail o  where o.assetInfoId = :assetInfoId")
    public List<SharedAssetReqDetail> findByAssetInfoUid(@Param("assetInfoId") String assetInfoId);
    
    @Query(value = "select a.* from shared_asset_req_detail a inner join shared_asset_request b on b.id = a.borrow_id where a.asset_info_id =?1 and b.clinical_dept_uid =?2 order by create_date desc LIMIT 1",nativeQuery = true)
    public List<SharedAssetReqDetail> getAssetUid(String assetInfoId, String clinicalDeptUid);
    
    @Query(value = "select a.* from shared_asset_req_detail a inner join shared_asset_request b on b.id = a.shared_id where a.asset_info_id =?1 and b.state = 6 order by create_date desc LIMIT 1",nativeQuery = true)
    public List<SharedAssetReqDetail> getBeginTime(String assetInfoId);
    
    @Query(value = "select a.* from shared_asset_req_detail a inner join shared_asset_request b on b.id = a.borrow_id where a.asset_info_id =?1 and b.state = 6 AND a.return_id is NULL",nativeQuery = true)
    public List<SharedAssetReqDetail> getBorrowInfo(String assetInfoId);

    @Query(value = "select borrowId from SharedAssetReqDetail where assetInfoId = :assetInfoId and clinicalDeptUid = :clinicalDeptUid and returnId is null")
    public List<String> getBorrowDetail(@Param("assetInfoId") String assetInfoId, @Param("clinicalDeptUid") String clinicalDeptUid);
    
    
}
