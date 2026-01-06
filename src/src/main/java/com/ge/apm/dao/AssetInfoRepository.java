package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.*;

public interface AssetInfoRepository extends GenericRepository<AssetInfo>, AssetInfoRepositoryCustom {

    @Query("select o from AssetInfo o where o.hospitalId = ?1 and o.isValid = true")
    public List<AssetInfo> getByHospitalId(int hospitalId);
    @Query("select siteUID from AssetInfo where id = ?1")
    String findSiteUidByAssetId(Integer id);
    @Query("select o from AssetInfo o where o.siteId=?1 and o.modalityId = ?2 and o.isValid = true")
    public List<AssetInfo> getBySiteIdAndModalityId(int siteId, String modalityId);

    @Query("select o from AssetInfo o where o.siteId = ?1 and o.isValid = true")
    public List<AssetInfo> getBySiteId(int siteId);

    @Query("select 1 from AssetInfo o where o.systemId=?1")
    public Integer checkExistsBySystemId(String systemId);

    @Query(value="select o.* from asset_info o where o.system_id=?1 and o.is_deleted = false and o.is_valid = true",nativeQuery=true)
    public List<AssetInfo> getBySystemIdNoConditionGefe(String systemId);

    @Query(value="select o.* from asset_info o where o.system_id=?1 limit 1",nativeQuery=true)
    public AssetInfo getBySystemIdNoCondition(String systemId);

    @Query(value="select o.* from asset_info o where o.system_id=?1 and o.is_deleted = false and o.is_valid = true limit 1",nativeQuery=true)
    public AssetInfo getBySystemId(String systemId);

    @Query("select o from AssetInfo o where o.qrCode = ?1 and o.siteUID= ?2")
    public AssetInfo getByQrCodeAndSiteUid(String qrCode,String site_uid);

    @Query("select o from AssetInfo o where o.name = ?1 and o.siteUID = ?2 and o.isDeleted = false and o.isValid = true and o.status!=4")
    public List<AssetInfo> getAssetInfoByNameAndSiteUID(String name, String siteUID);

    @Modifying
    @Query("update AssetInfo ai set ai.lastPmDate = now() where id = ?1 and siteId = ?2")
    public void updateAssetInfoPmOrderDate(int id, int siteId);

    public AssetInfo getById(Integer id);

    public AssetInfo getByUid(String uid);

    public AssetInfo findByUid(String uid);

    @Query("select ai from AssetInfo ai where ai.uid in ?1 and ai.sharedAssetTypeUid is not null ")
    public List<AssetInfo> findBySharedAssetTypeUid(List<String> uids);

    @Query(value="SELECT count(clinical_dept_uid) FROM asset_info WHERE clinical_dept_uid = ?1 AND clinical_dept_uid != asset_dept_uid",nativeQuery=true)
    public Integer findCountByClinicalDeptUID(String  clinicalDeptUID);

    @Query("select o from AssetInfo o where o.financingNum = ?1 and o.siteUID=?2 and o.isValid=true and o.isDeleted=false and o.status!=4")
    public List<AssetInfo> findByFinancingNum(String financingNum,String siteUID);

    public List<AssetInfo> getByQrCodeAndHospitalId(String qrCode, Integer hospitalId);

    @Query(value="select o.* from asset_info o where o.site_uid=?1 and o.is_valid=true and o.is_deleted=false and o.status!=4",nativeQuery=true)
    public List<AssetInfo> findBySiteUid(String siteUid);

    @Query(value="select o.* from asset_info o where o.is_valid=true and o.is_deleted=false and o.status!=4 and o.site_uid=?1 and o.clinical_dept_uid=?2",nativeQuery=true)
    public List<AssetInfo> findBySiteAndClinical(String siteUid,String departUid);

    @Query(value = "select count(1) from v2_work_order where asset_id = ?1 and status = 1", nativeQuery = true)
    public int fetchCurrentWorkOrders(Integer id);

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

    //excel 报表导出需要忽略大小写 所以使用jpa原生sql 加了ILIKE关键字( getBySerialNumAndSiteUID 方法是原本的业务)
    @Query(value = "select * from asset_info a where a.is_deleted = false and a.financing_num ILIKE ?1 and a.site_uid=?2 " +
            " and (a.measure_grade <> 4 or a.measure_grade is null) order by a.id desc " , nativeQuery = true)
    public List<AssetInfo> getByFinancingNumAndSiteUID_ForDbTable(String financingNum, String siteUID);

    @Query("select a from AssetInfo a where a.isDeleted = false and a.serialNum = ?1 and a.siteUID=?2 and (a.measureGrade <> 4 or a.measureGrade is null) order by a.id desc ")
    public List<AssetInfo> getBySerialNumAndSiteUID(String serialNum, String siteUID);

    //excel 报表导出需要忽略大小写 所以使用jpa原生sql 加了ILIKE关键字( getBySerialNumAndSiteUID 方法是原本的业务)
    @Query(value = "select * from asset_info a where a.is_deleted = false and a.serial_num ILIKE ?1 and a.site_uid=?2 " +
            "and (a.measure_grade <> 4 or a.measure_grade is null) order by a.id desc " , nativeQuery = true)
    public List<AssetInfo> getBySerialNumAndSiteUID_ForDbTable(String serialNum, String siteUID);

    //excel 报表导出需要忽略大小写 所以使用jpa原生sql 加了ILIKE关键字( getBySerialNumAndSiteUID 方法是原本的业务)
    @Query(value = "select * from asset_info a where a.is_deleted = false and a.qr_code ILIKE ?1 and a.site_uid=?2 " +
            "and (a.measure_grade <> 4 or a.measure_grade is null) order by a.id desc " , nativeQuery = true)
    public List<AssetInfo> getByQrCodeAndSiteUID_ForDbTable(String serialNum, String siteUID);

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

    @Query(value = "select distinct a.uid from asset_info a where a.clinical_dept_uid=?1 and a.site_uid = ?2 ", nativeQuery = true)
    List<String> findByClinicalUidAndSiteUid(String clinicalUid,String siteUid);


    @Modifying
    @Transactional
    @Query(value = "update AssetInfo set nationalStandardCode=?1 where uid=?2")
    public void updateStandardCode(String name, String uid);

    public List<AssetInfo> findByUidIn(List<String> assetUids);

    public List<AssetInfo> findByIdIn(Collection<Integer> assetIds);

    public List<AssetInfo> findByUidIn(List<String> assetUids, Sort sort);


    @Query("select ai from AssetInfo ai where ai.tenantUID = ?1 and ai.uid in ?2")
    public List<AssetInfo> findByTenantUidAndUidIn(String tenantUid, List<String> uids);

    public List<AssetInfo> findByDepartNumAndSiteUID(String num, String siteUID);
    public List<AssetInfo> findByFinancingNumAndSiteUID(String num, String siteUID);
    public List<AssetInfo> findByQrCodeAndSiteUID(String qrCode, String siteUID);
    public List<AssetInfo> findByDepartNumAndFinancingNumAndSiteUID(String num1,String num2,String siteUid);
    List<AssetInfo> findByDepartNum(String departNum);
    List<AssetInfo> findByFinancingNum(String financingNum);
    List<AssetInfo> findByDepartNumAndFinancingNum(String departNum,String financingNum);
    @Modifying
    @Transactional
    @Query("update AssetInfo ai set ai.swapTypeState = :swapTypeState where ai.id = :id")
    public void updateAssetInfoSwapTypeState(@Param("swapTypeState") String swapTypeState, @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("update AssetInfo ai set ai.sharedAssetTypeUid = :batchSharedAssetTypeUid,ai.assetDeptUID = :orgId,ai.sharedStatus=:sharedStatus where ai.uid in :assetUids")
    public void batchUpdateAssetInfoSwapType(@Param("batchSharedAssetTypeUid") String batchSharedAssetTypeUid, @Param("assetUids") List<String> assetUids, @Param("orgId") String orgId,@Param("sharedStatus") Integer sharedStatus);

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

    @Query(value="select o.* from asset_info o where o.uid in (select b.asset_uid from nurse_site_maintain_config_item b where b.parent_id=?1) order by o.name_py_code asc",nativeQuery=true)
    public List<AssetInfo> findAssetByMaintainConfigParentId(String configParentId);

    @Query(value="select o.* from asset_info o where o.uid in (select b.asset_uid from nurse_site_maintain_config_item_jn b where b.parent_id=?1) order by o.name_py_code asc",nativeQuery=true)
    public List<AssetInfo> findAssetByMaintainConfigJNParentId(String configParentId);

    @Query("select w.deptForSharedAsset from WorkflowConfig w where w.siteUID = :siteUID")
    public List<String> getOrgId(@Param("siteUID") String siteUID);

    @Transactional
    @Modifying
    @Query(value= "update AssetInfo set clinicalDeptName = ?1 where clinicalDeptUID = ?2 and tenantUID = ?3")
    public void updateClinicalDeptName(String deptName, String deptUid, String tenantUid);


    //给出当前用户能够访问到的资产
    @Query(value = "select distinct ai.* from asset_info ai,user_org uo,user_account ua where ua.uid=uo.user_uid and uo.org_uid in(ai.tenant_uid, ai.institution_uid,ai.hospital_uid,ai.site_uid) and  ua.id =? and ai.is_deleted=false and ai.is_valid=true ",nativeQuery = true)
    public ArrayList<AssetInfo> getUserListByAsset(Integer userId);

    @Query(value= "select u.* from asset_info u where u.uid in ?1 ",nativeQuery=true)
    public List<AssetInfo> getByUids(Set<String> setUids);

    @Query(value= "select new com.ge.apm.domain.AssetInfo(id, name,uid,departNum,serialNum,functionType,clinicalDeptName,clinicalDeptUID,nurseLocation) from AssetInfo u where u.uid in ?1 ")
    public List<AssetInfo> getSimpleDataByUids(Set<String> setUids);

    @Query(value= "select new com.ge.apm.domain.AssetInfo(id, name,uid,departNum,serialNum,functionType,clinicalDeptName,clinicalDeptUID,nurseLocation) from AssetInfo u where u.id in ?1 ")
    public List<AssetInfo> getSimpleDataByIds(Set<Integer> ids);

    @Query(value= "select new com.ge.apm.domain.AssetInfo(id, name,uid,departNum,serialNum,functionType,clinicalDeptName,clinicalDeptUID,nurseLocation) from AssetInfo u where u.uid = ?1 ")
    public AssetInfo getSimpleDataByUids(String uid);

    @Query(value= "select exists(select 1 from asset_info where tenant_uid = ?1)",nativeQuery = true)
    boolean findRecordByTenantUid(String uid);

    @Query(value= "select o.site_uid from asset_info o where o.mvs_equip=1",nativeQuery = true)
    public Set<String> findMVSSiteUid();
    @Query(value= "select o.tenant_uid from asset_info o where o.mvs_equip=1",nativeQuery = true)
    public List<String> findMVSTenantUid();

    public AssetInfo findByUidAndTenantUID(String uid, String tenantUID);

    @Modifying
    @Transactional
    @Query(value = "update AssetInfo set vendor=?1 where supplierId=?2 and tenantUID=?3")
    public void updateSupplierName(String name, int supplierId, String tenantUid);

    @Modifying
    @Transactional
    @Query(value = "update AssetInfo set maitanance=?1 where maitananceId=?2 and tenantUID=?3")
    public void updateMaintainerName(String name, String supplierId, String tenantUid);

    @Modifying
    @Transactional
    @Query(value = "update AssetInfo set manufacture=?1 where manufactureId=?2 and tenantUID=?3")
    public void updateManufactureName(String name, String supplierId, String tenantUid);

    @Query(value = "select distinct clinical_dept_uid from asset_info where status <> 4 and is_valid = true and is_deleted = false and site_uid = ?1", nativeQuery = true)
    List<String> findDistinctClinicalDeptUids(String siteUid);

    @Query(value = "select distinct asset_dept_uid from asset_info where status <> 4 and is_valid = true and is_deleted = false and site_uid = ?1", nativeQuery = true)
    List<String> findDistinctAssetDeptUids(String siteUid);

    @Query(value = "select uid from asset_info where tenant_uid = ?1 and (status in ?2 or is_deleted = true or is_valid = false)",nativeQuery = true)
    List<String> getInvalidAssets(String tenantUID,List<Integer> status);

    List<AssetInfo> findBySupplierIdOrMaitananceIdOrManufactureId(Integer supplierId, String maitananceId, String manufactureId);

    @Query(value = "select * from asset_info where is_deleted = false and (supplier_id = ?1 or maitanance_id = ?2 or manufacture_id = ?3)",nativeQuery = true)
    List<AssetInfo> findCanDelete(Integer supplierId, String maitananceId, String manufactureId);

    AssetInfo findFirstByQrCode(String qrCode);

    /**
     * 根据systemId和用户type类型获取匹配条数
     * @param type 用户类型
     * @param systemId 系统ID
     * @return
     */
    @Query(value = "SELECT COUNT(asset.id) FROM asset_info asset LEFT JOIN tenant_info tenant ON asset.tenant_uid = tenant.uid WHERE tenant.TYPE = ?1 AND asset.system_id =?2",nativeQuery = true)
    Integer findAssetInfoCount(Integer type,String systemId);

    /**
     * 根据SYSTEM ID更新主目录和子目录信息
     * @param mainDirectory 主目录名称
     * @param secondDirectory 子目录名称
     * @param type 用户类型
     * @param systemId 系统ID
     */
    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info SET main_directory = ?1,second_directory = ?2 FROM tenant_info WHERE asset_info.tenant_uid = tenant_info.uid AND tenant_info.TYPE = ?3 AND asset_info.system_id = ?4 ",nativeQuery = true)
    void updateDirecotryInfo(String mainDirectory, String secondDirectory, Integer type,String systemId);

    @Query("select o from AssetInfo o where o.departNum =?1 and o.systemId = ?2")
    AssetInfo findByDepartNumAndSystemId(String departNum, String departNum1);

    @Query(value="select count(*) from asset_info where id=?1 and is_deleted = false",nativeQuery=true)
    public int countByAssetIdAndNotDeleted(int assetId);

    List<AssetInfo> findAllByDepartNumIn(List<String> departsNums);

    @Query(value="SELECT count(count1) from (" +
            "SELECT count(id) as count1,bo_uuid from v2_blob_object where bo_sub_type = '1' and bo_uuid in ?1 GROUP BY bo_uuid HAVING count(id)>=3) T",nativeQuery=true)
    Integer countAssetWithQRcode(List<String> uidList);

    List<AssetInfo> findByParentAssetIdOrId(Integer parentAssetId,Integer id);

    @Query(value="select *from asset_info where parent_asset_id = ?1",nativeQuery=true)
    List<AssetInfo> findChildAssets(Integer id);

    List<AssetInfo> findByParentAssetId(Integer parentAssetId);

    @Query(value="select uid from asset_info where depart_num = ?1  and  tenant_uid = ?2  and is_deleted = false ",nativeQuery=true)
    List<String> findIdByDepartNumAndTenantUID(String departNum,String tenantUID);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asset_info SET shared_asset_type = NULL WHERE uid IN ?1",nativeQuery = true)
    void  clearShareAssetTypeByIds(List<String> assetIds);

    @Query(value = "select o.* from asset_info o where o.uid in ?1", nativeQuery = true)
    List<AssetInfo> fetchListByUid(Set<String> listUid);

    @Query(value = "select a.* from asset_info a " +
            "left outer join zkb_asset_qr_code_from_ib q on a.id = q.asset_id " +
            "left join tenant_info t on t.uid = a.tenant_uid " +
            "where t.type ='3' and (q.is_qr_code != true or q.is_qr_code is null) and a.system_id like '0824%' and a.tenant_uid = ?1", nativeQuery = true)
    List<AssetInfo> queryAssetByZkbQrCode(String tenantUid);

    //@Query(value = "with recursive cte as ( select id, name, parent_asset_id as pid, cast(id as text )as branch,function_type,serial_num,depart_num,asset_group,asset_dept_id,clinical_dept_name,manufacture,vendor from asset_info a where id in (?1) union all select origin.id, origin.name, cte.id as pid, cast(cte.branch as text) || '~' || origin.id,origin.function_type,origin.serial_num,origin.depart_num,origin.asset_group,origin.asset_dept_id,origin.clinical_dept_name,origin.manufacture,origin.vendor from cte join asset_info as origin on origin.parent_asset_id = cte.id ) select id,name, pid, branch, (length(branch)-length(replace(branch, '~', ''))) as rank,function_type,serial_num,depart_num,asset_group,asset_dept_id,clinical_dept_name,manufacture,vendor from cte order by branch; ",nativeQuery = true)
    @Query(value = "with recursive cte as ( select id, name, parent_asset_id as pid, cast(id as text )as branch from asset_info a where id in (?1) union all select origin.id, origin.name, cte.id as pid, cast(cte.branch as text) || '~' || origin.id from cte join asset_info as origin on origin.parent_asset_id = cte.id ) select id,name, pid, branch, (length(branch)-length(replace(branch, '~', ''))) as rank from cte order by branch; ",nativeQuery = true)
    List<Object> findParentAndChildrenAsset1(Set<Integer> rootIds);
    @Query(value = "select count(id) from site_to_site_allocation where asset_uid = ?1 and return_at is null", nativeQuery = true)
    int secondment(String uid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asset_info SET shared_status =?2 where uid in ?1",nativeQuery = true)
    void batchUpdateAssetInfoSharedStatus(List<String> assetUids,Integer sharedStatus);



    @Query(value = "select ai.* from asset_info ai left join asset_history ah on ai.uid=ah.asset_uid where ah.asset_uid is null limit 2000", nativeQuery = true)
    List<AssetInfo> queryAssetHistoryNotExistsInsert();

    @Query(value="select o.* from asset_info o where o.site_uid=?1 and o.is_valid=true and o.is_deleted=false",nativeQuery=true)
    List<AssetInfo> getBySiteUid(String siteUid);

    @Query(value="select uid from asset_info o where o.brand_id=?1 and o.is_valid=true and o.is_deleted=false",nativeQuery=true)
    List<String> findByBrandId(Integer value);

    List<AssetInfo> findAllByUidIn(List<String> orgUids);

    @Query(value="select count(1) from asset_info where asset_class = ?1 and tenant_uid = ?2", nativeQuery = true)
    int countByAssetClassAndTenant(Integer category, String tenantUid);

    @Query(value="select * from asset_info where site_uid = ?1 and shared_asset_type = ?2 and is_valid=true and is_deleted=false ", nativeQuery = true)
    List<AssetInfo> findSharedAssetTypeIdCount(String siteUid, String sharedAssetTypeId);

    public List<AssetInfo> findByAuthGroupUid(String authGroupUid);

    public List<AssetInfo> findByAssetTypeAndTenantUID(String assetTypeUid,String tenantUid);

    @Query(value="select  ai.*  from  asset_info ai  left join org_info  oi  on ai.asset_dept_uid = oi.uid  where  ai.asset_dept_uid != '' and ai.asset_dept_uid is not null and  oi.uid is not null and (ai.asset_dept_id is null or ai.asset_dept_id != oi.id)", nativeQuery = true)
    List<AssetInfo> notEqualAssetDeptId();

    @Query(value="select count(ai.id) from asset_info  ai left join user_account ua on ai.created_by=ua.login_name  where ua.id is not null and ua.login_name!=ua.name", nativeQuery = true)
    Long findByCreatedByIsLoginNameSize();


    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info  set created_by=ua.name from user_account ua where asset_info.created_by=ua.login_name and ua.login_name!=ua.name",nativeQuery = true)
    int batchUpdateAssetInfoCreatedBy();


    @Query(value="select  ai.*,ua.name as userName from asset_info  ai left join user_account ua on ai.created_by=ua.login_name  where ua.id is not null and ua.login_name!=ua.name order by ai.id desc limit 3000", nativeQuery = true)
    List<AssetInfo> findByCreatedByIsLoginName();


    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info  set created_by=?2 where id=?1",nativeQuery = true)
    void updateCreatedBy(int id, String userName);

    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info  set lifecycle =?1, last_modified_date = now() where id in (?2)",nativeQuery = true)
    void updateLifecycleByIds(Integer id, Set<Integer> ids);

    List<AssetInfo> findByLocationCode(String namePyCode);

    List<AssetInfo> findByLocationCodeIn(List<String> collect);

    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info  set asset_group=?1 where tenant_uid=?2 and asset_type=?3 and site_uid in (?4)",nativeQuery = true)
    void batchUpdateAssetInfoAssetGroup(Integer newAssetGroup,String tenantUid, String encoded,List<String> siteUids);

    @Transactional
    @Modifying
    @Query(value= "update asset_info set last_inventory_date = now() where id in (?1)",nativeQuery = true)
    void bathInventory(Collection<Integer> assetIds);

    @Transactional
    @Modifying
    @Query(value= "update asset_info set last_inventory_date = ?2 where id = ?1",nativeQuery = true)
    void updateLastInventoryDate(Integer assetId, Date lastInventoryDate);


    @Transactional
    @Modifying
    @Query(value= "UPDATE asset_info  set is_special_asset=?2 where id=?1",nativeQuery = true)
    void updateSpecialAsset(Integer assetId, boolean b);

    @Query(value = "select  ai.* from asset_info ai  left join  efficiency_equipment_info eei on ai.uid=eei.asset_uid   where ai.tenant_uid=?1 and ai.is_valid =true and ai.is_deleted=false   and (ai.institution_uid in (?2) or ai.hospital_uid in (?2) or ai.site_uid in (?2))  and ai.clinical_dept_uid=?3      and eei.uid is null limit 5000 ",nativeQuery = true)
    List<AssetInfo> getAssetInfoIsNotEfficiencyEquipment(String tenantUID, List<String> siteOrgUids, String clinicalDeptUid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asset_info SET clinical_dept_id = ?2, clinical_dept_uid = ?3, clinical_dept_name = ?4 WHERE uid IN ?1",nativeQuery = true)
    void outStocked(List<String> assetIds, Integer clinicalDeptId, String clinicalDeptUid, String clinicalDeptName);

    @Query(nativeQuery = true, value = "SELECT uid FROM asset_info WHERE tenant_uid = ?1 AND asset_type IN (?3) AND is_checked = TRUE AND is_deleted = FALSE AND is_valid = TRUE AND clinical_dept_uid IN (?2) AND (tenant_uid != 'dccf4542d20648bdb8122e1628d84e82' OR (depart_num !~* '^XL' AND depart_num !~* '^L' AND depart_num !~* '^X\\d*$' AND depart_num !~* '^\\d+$'))")
    List<String> findAssetTypeToInventory(String tenantUid, List<String> clinicalDeptUIDs, Collection<String> assetTypes);

}