package com.ge.apm.dao;

import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Query("select o from AssetInfo o where o.qrCode = ?1")
    public AssetInfo getByQrCode(String qrCode);

    @Modifying
    @Query("update AssetInfo ai set ai.lastPmDate = now() where id = ?1 and siteId = ?2")
    public void updateAssetInfoPmOrderDate(int id, int siteId);

    public AssetInfo getById(Integer id);
    
    public AssetInfo getByUid(String uid);

    public AssetInfo findByUid(String uid);

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

    public AssetInfo findByFinancingNumAndTenantUID(String financingNum, String tenantUID);

    public AssetInfo findBySerialNumAndTenantUID(String serialNum, String tenantUID);

    @Query(value = "select distinct a.name from asset_info a where a.tenant_uid=?1 and a.name like concat('%',?2,'%') order by a.name fetch first 20 rows only", nativeQuery = true)
    public List<String> findDistinctAssetNamesByPrefix(String tenantUID, String prefix);

    @Query(value = "select distinct a.name from asset_info a where a.tenant_uid=?1 order by a.name fetch first 20 rows only", nativeQuery = true)
    public List<String> findDistinctAssetNames(String tenantUID);

	@Modifying
	@Transactional
	@Query(value = "update AssetInfo set clinicalDeptName=?1 where clinicalDeptUID=?2")
	public void updateClinicalName(String name,String uid);


	@Modifying
	@Transactional
	@Query(value = "update AssetInfo set nationalStandardCode=?1 where uid=?2")
	public void updateStandardCode(String name,String uid);

	public List<AssetInfo> findByUidIn(List<String> assetUids);

    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and (ai.assetOwnerId = ?2 or ai.assetOwnerId2 = ?2 or ai.assetOwnerId3 = ?2)")
    public List<AssetInfo> getByTenantUIDAndAssetOwner(String tenantUID, Integer assetOwnerId);

    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and ai.uid in ?2")
	public List<AssetInfo> findByTenantUidAndUidIn(String tenantUid, List<String> uids);

}
