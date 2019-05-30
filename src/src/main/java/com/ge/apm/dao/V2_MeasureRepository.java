package com.ge.apm.dao;


import com.ge.apm.domain. MeasuringOrder;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface V2_MeasureRepository extends GenericRepositoryUUID< MeasuringOrder> {
    @Query(value="select id from Asset_Info a where (a.tenant_UID in (?1) or a.institution_UID in (?1) or a.hospital_UID in (?1) or a.site_UID in (?1) or a.clinical_Dept_UID in (?1)) and id not in (select tr.asset_Id from Asset_Tag_Rule tr, Asset_Tag_Biomed_Group tg, Biomed_Group_User tu where tr.tag_Id=tg.tag_Id and tg.biomed_Group_Id=tu.group_Id)",nativeQuery = true)
    public List<Integer> getAssetsByNoTagGroup(List<String> c);

    @Query(value = "select * from v2_measure  where asset_uid =?1  order by created_date desc limit 4", nativeQuery = true)
    public List< MeasuringOrder> getPmOrdersForAsset(String assetUid);

    @Query("select distinct tr.assetId from BiomedGroupUser bgu,AssetTagBiomedGroup tbg, AssetTagRule tr where bgu.groupId = tbg.biomedGroupId and tbg.tagId = tr.tagId and bgu.userId = ?1")
    public List<Integer> getAssetsByTagGroup(Integer currentPersonId);

    @Query(value = "select exists(select 1 from v2_measure where asset_id= ?1 and is_finished = false and rule_level = ?2) ",nativeQuery = true)
    public Boolean findOpenPm(int assetId, Integer level);

    @Query(value = "select o.* from v2_measure o where o.asset_uid= ?1 and o.rule_level=3 and o.is_finished='false' order by o.plan_time desc limit 1",nativeQuery = true)
    public  MeasuringOrder findByAssetUid(String asset_uid);
    @Query(value = "select o.* from v2_measure o where o.asset_uid in ?1 and o.rule_level=3 and o.is_finished='false' order by o.plan_time desc limit 1",nativeQuery = true)
    public Set< MeasuringOrder> findByAssetUids(Set<String> setAssetUids);

    @Query(value = "select o.* from v2_measure o where o.asset_uid in ?1 and o.rule_level=3 and o.plan_time>= ?2 and o.plan_time< ?3 order by o.plan_time desc",nativeQuery = true)
    public List< MeasuringOrder> findMultiByAssetUids(Set<String> setAssetUids, Date startDate, Date endDate);

    @Query(value = "select exists(select 1 from v2_measure where asset_id= ?1 and is_finished = false and rule_level = ?2 and pm_type = ?3) ",nativeQuery = true)
    public Boolean findOpenPm(int assetId, Integer level, Integer pmType);

    @Query(value="select measure.* from v2_measure measure where id in ?1 and measure.simplify =true",nativeQuery = true)
    public  List< MeasuringOrder> checkSimplify(List<String> idList);

    @Query(value="select measure.* from v2_measure measure where id in ?1",nativeQuery = true)
    public  List< MeasuringOrder> getBatchPmOrderList(List<String> idList);

    public  MeasuringOrder findById(String Id);

    public List< MeasuringOrder> getByAssetUid(String asset_uid);

    @Query(value="select asset_uid from v2_measure where id in ?1",nativeQuery = true)
    public Set<String> getAssetUid(List<String> uidList);

}

