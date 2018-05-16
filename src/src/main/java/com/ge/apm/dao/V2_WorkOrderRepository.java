package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Collection;
import java.util.List;

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
    @Query(value="select id from AssetInfo a where (a.tenantUID in ?1 or a.institutionUID in ?1 or a.hospitalUID in ?1 or a.siteUID in (?1) or a.clinicalDeptUID in (?1)) and "
            + " id not in (select tr.assetId from AssetTagRule tr, AssetTagBiomedGroup tg, BiomedGroupUser tu where tr.tagId=tg.tagId and tg.biomedGroupId=tu.groupId)")
    public List<Integer> getAssetsByNoTagGroup(Collection<String> c);
}
