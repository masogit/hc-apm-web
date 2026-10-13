package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface V2_WorkOrderRepository extends GenericRepositoryUUID<V2_WorkOrder> {

    List<V2_WorkOrder> findBySrId(String srId);

    @Query(value = "select wo.* from V2_Work_Order wo where wo.asset_Id in (select distinct tr.asset_Id from Biomed_Group_User bgu, "
            + "Asset_Tag_Biomed_Group tbg, Asset_Tag_Rule tr where bgu.group_Id = tbg.biomed_Group_Id and tbg.tag_Id = tr.tag_Id and bgu.user_Id = ?1) and wo.current_Person_Id = '-1' "
            + "and wo.current_Step_Id = 3 and wo.status = 1 or wo.current_Person_Id = ?1 and wo.current_Step_Id = 3 and wo.status = 1 or wo.current_Step_Id = 3 and wo.current_Person_Id = '-1' "
            + "and wo.status = 1 and wo.asset_Id in (select distinct ai.id from Asset_Info ai, User_Account ua, Asset_Tag_Rule tr where case ua.org_Level when 1 then ai.institution_UID = ua.institution_UID "
            + "when 2 then ai.hospital_UID = ua.hospital_UID when 3 then ai.site_UID = ua.site_UID end and ua.id = ?1 and ai.is_Valid = true and tr.asset_Id <> ai.id) order by ?#{#pageable}",
            countQuery = "select count(1) from V2_Work_Order wo where wo.asset_Id in (select distinct tr.asset_Id from Biomed_Group_User bgu, "
            + "Asset_Tag_Biomed_Group tbg, Asset_Tag_Rule tr where bgu.group_Id = tbg.biomed_Group_Id and tbg.tag_Id = tr.tag_Id and bgu.user_Id = ?1) and wo.current_Person_Id = '-1' "
            + "and wo.current_Step_Id = 3 and wo.status = 1 or wo.current_Person_Id = ?1 and wo.current_Step_Id = 3 and wo.status = 1 or wo.current_Step_Id = 3 and wo.current_Person_Id = '-1' "
            + "and wo.status = 1 and wo.asset_Id in (select distinct ai.id from Asset_Info ai, User_Account ua, Asset_Tag_Rule tr where case ua.org_Level when 1 then ai.institution_UID = ua.institution_UID "
            + "when 2 then ai.hospital_UID = ua.hospital_UID when 3 then ai.site_UID = ua.site_UID end and ua.id = ?1 and ai.is_Valid = true and tr.asset_Id <> ai.id)",
            nativeQuery = true)
    Page<V2_WorkOrder> fetchAvailableWorkOrderByUser(Integer userId, Pageable pageable);

    /*@Query(value = "select  wo.* from v2_work_order wo where wo.asset_id in  (select distinct(tr.asset_id) from biomed_group_user bgu,asset_tag_biomed_group tbg,asset_tag_rule tr where tbg.biomed_group_Id=bgu.group_Id and tbg.tag_id =tr.tag_id and bgu.user_id=?1) and wo.current_person_id='-1' and wo.current_step_id=3 and wo.status=1 or wo.current_person_id=?1 and wo.current_step_id=3 and wo.status=1 order by ?#{#pageable}",
            countQuery = "select  wo.* from v2_work_order wo where wo.asset_id in  (select distinct(tr.asset_id) from biomed_group_user bgu,asset_tag_biomed_group tbg,asset_tag_rule tr where tbg.biomed_group_Id=bgu.group_Id and tbg.tag_id =tr.tag_id and bgu.user_id=?1) and wo.current_person_id='-1' and wo.current_step_id=3 and wo.status=1 or wo.current_person_id=?1 and wo.current_step_id=3 and wo.status=1",nativeQuery = true)
    Page<V2_WorkOrder> fetchAvailableWorkOrderByUser(Integer userId, Pageable pageable);*/

    Page<V2_WorkOrder> findByHospitalIdAndStatusAndCurrentStepIdAndCurrentPersonIdIn(int hospitalId, int status, int currentStepId, Collection<Integer> c, Pageable pageable);

    @Query("select distinct wo from V2_WorkOrder wo where wo.currentPersonId in(select distinct bgu2.userId from BiomedGroupUser bgu2 where bgu2.groupId in "
            + "(select distinct bgu2.groupId from bgu2 where bgu2.userId=?1)) and wo.currentStepId=3 and wo.status=1")
    Page<V2_WorkOrder> fetchAvailableSameGroupWorkOrderByUser(Integer userId, Pageable pageable);

    /*@Query("select distinct wo from V2_WorkOrder wo where wo.assetId in(select distinct(tr.assetId) from BiomedGroupUser bgu "
            + " join AssetTagBiomedGroup tbg on tbg.biomedGroupId=bgu.groupId join AssetTagRule tr on tbg.tagId=tr.tagId  where  bgu.userId=?1)"
            + " and wo.currentPersonId='-1' and wo.currentStepId=3 and wo.status=1 or wo.currentPersonId=?1 and wo.currentStepId=3 and wo.status=1")
    Page<V2_WorkOrder> fetchAvailableWorkOrderByUser(Integer userId,Pageable pageable);*/
    @Query(nativeQuery = true, value = "SELECT DISTINCT clinical_dept_name FROM asset_info ai WHERE hospital_id=?1 and site_id=?2 and EXISTS (SELECT * FROM v2_service_request sr WHERE sr.asset_id=ai.id and sr.status=1) ORDER BY clinical_dept_name")
    List<String> getClinicalDeptNameList(Integer hospitalId, Integer siteId);

    @Query("select distinct tr.assetId from BiomedGroupUser bgu,AssetTagBiomedGroup tbg, AssetTagRule tr where bgu.groupId = tbg.biomedGroupId and tbg.tagId = tr.tagId and bgu.userId = ?1")
    public List<Integer> getAssetsByTagGroup(Integer currentPersonId);
    
    
    //select asset_id from asset_tag_rule tr, asset_tag_biomed_group tg,biomed_group_user tu  where tr.tag_id=tg.tag_id and tg.biomed_group_id=tu.group_id and asset_id='20915'
    @Query(value="select id from AssetInfo a where (a.tenantUID in ?1 or a.institutionUID in ?1 or a.hospitalUID in ?1 or a.siteUID in (?1) or a.clinicalDeptUID in (?1) or a.assetDeptUID in (?1)) and "
            + " id not in (select tr.assetId from AssetTagRule tr, AssetTagBiomedGroup tg, BiomedGroupUser tu where tr.tagId=tg.tagId and tg.biomedGroupId=tu.groupId)")
    public List<Integer> getAssetsByNoTagGroup(Collection<String> c);

    public List<V2_WorkOrder> getByAssetIdAndStatusOrderByCloseTimeDesc(Integer assetId, Integer status);

    public List<V2_WorkOrder> getByAssetUidInAndStatusOrderByCloseTimeDesc(List<String> assetUid, Integer status);

    @Query(value="select distinct(o.*) from v2_work_order o where o.id in (?1)",nativeQuery=true)
    public List<V2_WorkOrder> getWorkOrderById(Set<String> ids);


    public List<V2_WorkOrder> findByAssetId(Integer asstId );

    @Query(nativeQuery = true, value = "SELECT  * FROM v2_work_order  WHERE asset_id=?1 and current_step_name in ?2")
    public List<V2_WorkOrder> findByAssetIdAndCurrentStepName(Integer asstId , Collection<String> c);


    /**
     * 设备ID
     * @param assetIdList 设备ID
     * @return
     */
    @Query(value = "SELECT count(id)  from v2_work_order  where asset_id in ?1 and created_date between ?2 and ?3", nativeQuery = true)
    Integer fetchCount(List<Integer> assetIdList,Date firstday, Date lastday);

    /**
     * 获取个数 个数
     * @param dateFormat 日期格式
     * @param date 开单月份
     * @param tenantUID 租户 tenantUID
     * @return
     */
    @Query(value = "SELECT count(id) from ( " +
            " SELECT " +
            " DISTINCT v2.id " +
            " FROM " +
            " v2_work_order v2 " +
            " INNER JOIN asset_info a on v2.asset_id = a.id "+
            " INNER JOIN zkb_contract_asset zca ON zca.asset_id = a.uid "+
            " INNER JOIN zkb_contract zkbc ON zkbc.ID = zca.contract_id "+
            " where to_char(v2.created_date, ?1)=?2 " +
            " and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd') ) T", nativeQuery = true)
    Integer fetchCount(String dateFormat,String date,String tenantUID);

    /**
     * 获取个数 个数
     * @param dateFormat 日期格式
     * @param date 开单月份
     * @param tenantUID 租户 tenantUID
     * @return
     */
    @Query(value = "SELECT count(id) from ( " +
            " SELECT " +
            " DISTINCT v2.id " +
            " FROM " +
            " v2_work_order v2 " +
            " INNER JOIN asset_info a on v2.asset_id = a.id "+
            " INNER JOIN zkb_contract_asset zca ON zca.asset_id = a.uid "+
            " INNER JOIN zkb_contract zkbc ON zkbc.ID = zca.contract_id "+
            " where zkbc.contract_type <> 'CCSB' and to_char(v2.created_date, ?1)=?2 " +
            " and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd') ) T", nativeQuery = true)
    Integer fetchCountOfCCSB(String dateFormat,String date,String tenantUID);
    @Modifying
    @Transactional
    @Query(value = "delete from v2_work_order where id in ?1",nativeQuery = true)
    Integer deleteById(List<String> woIds);

    /**县域工时实时更新**/
    @Modifying
    @Transactional
    @Query(value = "UPDATE v2_work_order SET man_hours=?1 WHERE id=?2", nativeQuery = true)
    public void updateManHours(Double manHours, String id);

    @Query(value = "select * from v2_work_order t where t.asset_uid in (?1) and t.status = 1 AND EXISTS (SELECT 1 FROM mvs_ib_mapping m1 WHERE t.asset_uid = m1.mvs_ib_uid OR t.asset_uid = m1.e_home_ib_uid)", nativeQuery = true)
    List<V2_WorkOrder> findUnFinishedMvsOrderByAsset(List<String> assetUidList);
}
