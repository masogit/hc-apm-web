package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.V2_PmOrder;

import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_PmOrderRepository extends GenericRepositoryUUID<V2_PmOrder> {

	/*@Query(value="select id from AssetInfo a where (a.tenantUID in ?1 or a.institutionUID in ?1 or a.hospitalUID in ?1 or a.siteUID in ?1 or a.clinicalDeptUID in ?1) and "
			+ " id not in (select tr.assetId from AssetTagRule tr, AssetTagBiomedGroup tg, BiomedGroupUser tu where tr.tagId=tg.tagId and tg.biomedGroupId=tu.groupId)")
	public List<Integer> getAssetsByNoTagGroup(Collection<String> c);*/
	/*@Query(value="select id from Asset_Info a where (a.tenant_UID in ?1 or a.institution_UID in ?1 or a.hospital_UID in ?1 or a.site_UID in ?1 or a.clinical_Dept_UID in ?1) and id not in (select tr.asset_Id from Asset_Tag_Rule tr, Asset_Tag_Biomed_Group tg, Biomed_Group_User tu where tr.tag_Id=tg.tag_Id and tg.biomed_Group_Id=tu.group_Id)",nativeQuery = true)
	public List<Integer> getAssetsByNoTagGroup(Collection<String> c);*/

	@Query(value="select id from Asset_Info a where (a.tenant_UID in (?1) or a.institution_UID in (?1) or a.hospital_UID in (?1) or a.site_UID in (?1) or a.clinical_Dept_UID in (?1)) and id not in (select tr.asset_Id from Asset_Tag_Rule tr, Asset_Tag_Biomed_Group tg, Biomed_Group_User tu where tr.tag_Id=tg.tag_Id and tg.biomed_Group_Id=tu.group_Id)",nativeQuery = true)
	public List<Integer> getAssetsByNoTagGroup(List<String> c);

	@Query(value = "select * from v2_pm_order  where asset_uid =?1  order by created_date desc limit 4", nativeQuery = true)
	public List<V2_PmOrder> getPmOrdersForAsset(String assetUid);

	@Query("select distinct tr.assetId from BiomedGroupUser bgu,AssetTagBiomedGroup tbg, AssetTagRule tr where bgu.groupId = tbg.biomedGroupId and tbg.tagId = tr.tagId and bgu.userId = ?1")
	public List<Integer> getAssetsByTagGroup(Integer currentPersonId);

	@Query(value = "select exists(select 1 from v2_pm_order where asset_id= ?1 and is_finished = false and rule_level = ?2) ",nativeQuery = true)
	public Boolean findOpenPm(int assetId, Integer level);
	
	@Query(value = "select o.* from v2_pm_order o where o.asset_uid= ?1 and o.rule_level=3 and o.is_finished='false' order by o.plan_time desc limit 1",nativeQuery = true)
	public V2_PmOrder findByAssetUid(String asset_uid);
	@Query(value = "select o.* from v2_pm_order o where o.asset_uid in ?1 and o.rule_level=3 and o.is_finished='false' order by o.plan_time desc limit 1",nativeQuery = true)
	public Set<V2_PmOrder> findByAssetUids(Set<String> setAssetUids);
	
	@Query(value = "select o.* from v2_pm_order o where o.asset_uid in ?1 and o.rule_level=3 and o.plan_time>= ?2 and o.plan_time< ?3 order by o.plan_time desc",nativeQuery = true)
	public List<V2_PmOrder> findMultiByAssetUids(Set<String> setAssetUids,Date startDate,Date endDate);

	@Query(value = "select exists(select 1 from v2_pm_order where asset_id= ?1 and is_finished = false and rule_level = ?2 and pm_type = ?3) ",nativeQuery = true)
	public Boolean findOpenPm(int assetId, Integer level,Integer pmType);

@Query(value="select pm.* from v2_pm_order pm where id in ?1 and pm.simplify =true",nativeQuery = true)
	public  List<V2_PmOrder> checkSimplify(List<String> idList);

	@Query(value="select pm.* from v2_pm_order pm where id in ?1",nativeQuery = true)
	public  List<V2_PmOrder> getBatchPmOrderList(List<String> idList);

}
