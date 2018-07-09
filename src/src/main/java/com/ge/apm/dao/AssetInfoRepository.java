package com.ge.apm.dao;

import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;

public interface AssetInfoRepository extends GenericRepository<AssetInfo> {

    @Query("select o from AssetInfo o where o.hospitalId = ?1 and o.isValid = true")
    public List<AssetInfo> getByHospitalId(int hospitalId);

    @Query("select o from AssetInfo o where o.siteId=?1 and o.modalityId = ?2 and o.isValid = true")
    public List<AssetInfo> getBySiteIdAndModalityId(int siteId, String modalityId);

    @Query("select o from AssetInfo o where o.siteId = ?1 and o.isValid = true")
    public List<AssetInfo> getBySiteId(int siteId);

    @Query("select 1 from AssetInfo o where o.systemId=?1")
    public Integer checkExistsBySystemId(String systemId);
    
    @Query(value="select o.* from asset_info o where o.system_id=?1 and o.is_deleted = false and o.is_valid = true limit 1",nativeQuery=true)
    public AssetInfo getBySystemId(String systemId);

    @Query("select o from AssetInfo o where o.qrCode = ?1 and o.siteUID= ?2")
    public AssetInfo getByQrCodeAndSiteUid(String qrCode,String site_uid);
    
    @Query("select o from AssetInfo o where o.name = ?1 and o.siteUID = ?2 and o.isDeleted = false and o.isValid = true and o.status in (1,2,3)")
    public List<AssetInfo> getAssetInfoByNameAndSiteUID(String name, String siteUID);

    @Modifying
    @Query("update AssetInfo ai set ai.lastPmDate = now() where id = ?1 and siteId = ?2")
    public void updateAssetInfoPmOrderDate(int id, int siteId);

    public AssetInfo getById(Integer id);
    
    public AssetInfo getByUid(String uid);

    public AssetInfo findByUid(String uid);
    
    @Query("select o from AssetInfo o where o.financingNum = ?1 and o.isValid = true and o.siteUID=?2")
    public List<AssetInfo> findByFinancingNum(String financingNum,String siteUID);
    
    @Query(value="select o.* from asset_info o,user_org b where o.financing_num = ?1 and o.is_valid = true and (o.site_uid=?2 or o.site_uid=b.org_uid or o.hospital_uid=b.org_uid or o.institution_uid=b.org_uid or o.tenant_uid=b.org_uid or o.clinical_dept_uid=b.org_uid) and b.user_uid=?3",nativeQuery=true)
    public List<AssetInfo> findByFinancingNumAccessScope(String financingNum,String siteUID,String userUid);
    
    @Query(value="select o.* from asset_info o,user_org b where o.financing_num = ?1 and o.is_valid = true and (o.site_uid=?2 or o.site_uid=b.org_uid or o.clinical_dept_uid=b.org_uid) and b.user_uid=?3",nativeQuery=true)
    public List<AssetInfo> findByFinancingNumSelfScope(String financingNum,String siteUID,String userUid);
    
    @Query(value="select o.* from asset_info o,user_org b where o.is_valid = true and (o.site_uid=?1 or o.site_uid=b.org_uid or o.hospital_uid=b.org_uid or o.institution_uid=b.org_uid or o.tenant_uid=b.org_uid or o.clinical_dept_uid=b.org_uid) and b.user_uid=?2",nativeQuery=true)
    public List<AssetInfo> findByAccessScope(String siteUID,String userUid);
    
    @Query(value="select o.* from asset_info o,user_org b where o.is_valid = true and (o.site_uid=?1 or o.site_uid=b.org_uid or o.clinical_dept_uid=b.org_uid) and b.user_uid=?2",nativeQuery=true)
    public List<AssetInfo> findBySelfScope(String siteUID,String userUid);


    public List<AssetInfo> getByQrCodeAndHospitalId(String qrCode, Integer hospitalId);

    @Query(value = "select count(1) from v2_work_order where asset_id = ?1 and status = 1", nativeQuery = true)
    public int fetchCurrentWorkOrders(Integer id);

    @Query("select a from AssetInfo a where a.uid is null or  a.uid='' ")
    public List<AssetInfo> getNeedUpdateUidAssets();

    @Query(value = "select max(a.financingNum) from AssetInfo a where a.siteUID = ?1 and  a.nationalStandardCode=?2"
    		+ " and a.financingNum like concat('%',?2,'%')")
	public String fetchMaxAssetFinacingNumWithSiteUID(String uid, String nationalStardCode);

    @Query("select max(a.financingNum) from AssetInfo a where a.hospitalUID = ?1 and  a.nationalStandardCode=?2 and a.financingNum like concat('%',?2,'%') ")
	public String fetchMaxAssetFinacingNumWithHospitalUID(String uid, String nationalStardCode);

    @Query("select max(a.financingNum) from AssetInfo a where a.institutionUID = ?1 and  a.nationalStandardCode=?2 and a.financingNum like concat('%',?2,'%') ")
	public String fetchMaxAssetFinacingNumWithInstitutionUID(String uid, String nationalStardCode);

    @Query("select max(a.financingNum) from AssetInfo a where a.tenantUID = ?1 and  a.nationalStandardCode=?2 and a.financingNum like concat('%',?2,'%') ")
	public String fetchMaxAssetFinacingNumWithTenantUID(String uid, String nationalStardCode);

	public List<AssetInfo> findByTenantUID(String tenantUID);

    @Query("select a from AssetInfo a where a.isDeleted = false and a.financingNum = ?1 and a.tenantUID=?2 order by a.id desc ")
    public List<AssetInfo> findByFinancingNumAndTenantUID(String financingNum, String tenantUID);

    @Query("select a from AssetInfo a where a.isDeleted = false and a.serialNum = ?1 and a.tenantUID=?2 order by a.id desc ")
    public List<AssetInfo> findBySerialNumAndTenantUID(String serialNum, String tenantUID);

    @Query("select a from AssetInfo a where a.isDeleted = false and a.financingNum = ?1 and a.siteUID=?2 and (a.measureGrade <> 4 or a.measureGrade is null) order by a.id desc ")
    public List<AssetInfo> getByFinancingNumAndSiteUID(String financingNum, String siteUID);

    @Query("select a from AssetInfo a where a.isDeleted = false and a.serialNum = ?1 and a.siteUID=?2 and (a.measureGrade <> 4 or a.measureGrade is null) order by a.id desc ")
    public List<AssetInfo> getBySerialNumAndSiteUID(String serialNum, String siteUID);

    @Query(value = "select distinct a.name from asset_info a where a.tenant_uid=?1 and (a.name like concat('%',?2,'%') or a.name_py_code like concat('%',?2,'%'))order by a.name fetch first 20 rows only", nativeQuery = true)
    public List<String> findDistinctAssetNamesByPrefix(String tenantUID, String prefix);

    @Query(value = "select distinct a.name from asset_info a where a.tenant_uid=?1 order by a.name fetch first 20 rows only", nativeQuery = true)
    public List<String> findDistinctAssetNames(String tenantUID);

    @Query(value = "select distinct a.manufacture from asset_info a where a.tenant_uid=?1 and (a.manufacture like concat('%',?2,'%') or a.manufacture_py_code like concat('%',?2,'%')) order by a.manufacture fetch first 20 rows only", nativeQuery = true)
    public List<String> findDistinctManufacturesByPrefix(String tenantUID, String prefix);

	@Modifying
	@Transactional
	@Query(value = "update AssetInfo set clinicalDeptName=?1 where clinicalDeptUID=?2")
	public void updateClinicalName(String name, String uid);


	@Modifying
	@Transactional
	@Query(value = "update AssetInfo set nationalStandardCode=?1 where uid=?2")
	public void updateStandardCode(String name, String uid);

	public List<AssetInfo> findByUidIn(List<String> assetUids);

    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and (ai.assetOwnerId = ?2 or ai.assetOwnerId2 = ?2 or ai.assetOwnerId3 = ?2)")
    public List<AssetInfo> getByTenantUIDAndAssetOwner(String tenantUID, Integer assetOwnerId);

    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and ai.uid in ?2")
	public List<AssetInfo> findByTenantUidAndUidIn(String tenantUid, List<String> uids);

    public List<AssetInfo> findByDepartNumAndSiteUID(String num, String siteUID);
    public List<AssetInfo> findByFinancingNumAndSiteUID(String num, String siteUID);
    public List<AssetInfo> findByQrCodeAndSiteUID(String qrCode, String siteUID);

    @Modifying
    @Transactional
    @Query("update AssetInfo ai set ai.swapTypeState = :swapTypeState where ai.id = :id")
    public void updateAssetInfoSwapTypeState(@Param("swapTypeState") String swapTypeState, @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("update AssetInfo ai set ai.sharedAssetTypeUid = :batchSharedAssetTypeUid,ai.assetDeptUID = :orgId where ai.uid in :assetUids")
    public void batchUpdateAssetInfoSwapType(@Param("batchSharedAssetTypeUid") String batchSharedAssetTypeUid, @Param("assetUids") List<String> assetUids, @Param("orgId") String orgId);

    public List<AssetInfo> findByClinicalDeptUIDIn(List<String> clinicalDeptUIDs);

    @Query("select o from AssetInfo o  where (o.departNum = :departNum or o.financingNum = :financingNum or o.qrCode = :qrCode) and o.siteUID = :siteUID")
    public List<AssetInfo> findSwapTypeInfo(@Param("departNum") String departNum, @Param("financingNum") String financingNum, @Param("qrCode") String qrCode, @Param("siteUID") String siteUID);

    
    @Modifying
    @Query("update AssetInfo ai set ai.inventoryStatus=?2 where ai.id in ?1")
    void updateInventoryStatusAndIdIn(List<Integer> assetIds, Integer ORDER_STATUS_DONE_FINISHED);

    
   // SELECT count(1) as counta, asset_owner_id,asset_owner_id2,asset_owner_id3 FROM "public".asset_info where asset_owner_id=479 group by asset_owner_id2,asset_owner_id3,asset_owner_id

//    @Query("select count(1) as totalTimes, ai.assetOwnerId,ai.assetOwnerId2,ai.assetOwnerId3 from AssetInfo ai where ai.assetOwnerId=:assetOwnerId and ai.siteUID=:siteUID "
//            + "group by ai.assetOwnerId2,ai.assetOwnerId3,ai.assetOwnerId ordey by totalTimes desc")
    @Query(value = "select count(1) as totalTimes, ai.asset_owner_id,ai.asset_owner_id2,ai.asset_owner_id3 from asset_info ai where ai.asset_owner_id=:assetOwnerId and ai.site_uid=:siteUID "
            + "group by ai.asset_owner_id2,ai.asset_owner_id3,ai.asset_owner_id order by totalTimes desc limit 1",nativeQuery = true)
    public List<Object[]> findAssetOwnerComposition(@Param("assetOwnerId") Integer assetOwnerId, @Param("siteUID") String siteUID);
    
    @Query(value="select o.* from asset_info o where o.uid in (select b.asset_uid from nurse_site_maintain_config_item b where b.parent_id=?1)",nativeQuery=true)
    public List<AssetInfo> findAssetByMaintainConfigParentId(String configParentId);

    @Query("select w.deptForSharedAsset from WorkflowConfig w where w.siteUID = :siteUID")
    public List<String> getOrgId(@Param("siteUID") String siteUID);
}
