package com.ge.apm.dao;

import com.ge.apm.domain.V2_PmOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface V2_PmOrderRepository extends GenericRepositoryUUID<V2_PmOrder> {

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

	public V2_PmOrder findById(String Id);

	public List<V2_PmOrder> getByAssetUid(String asset_uid);

	@Query(value="select asset_uid from v2_pm_order where id in ?1",nativeQuery = true)
	public Set<String> getAssetUid(List<String> uidList);

	@Query(value="select\n" +
			"\t (select name from org_info where uid = vpo.site_uid) as siteName,\n" +
			"\tai.\"name\" as assetName,\n" +
			"\tai.serial_num as serialNum,\n" +
			"\tai.depart_num as departNum,\n" +
			"\tai.function_type as functionType,\n" +
			"\tvpo.rule_level as ruleLevel,\n" +
			"\tvpo.pm_type as pmType,\n" +
			"\t (select name from org_info where uid = vpo.clinical_dept_id) as clinicalDeptName,\n" +
			"\tpor.\"name\" as pmRuleName,\n" +
			"\tpor.\"period\" as pmRulePeriod,\n" +
			"\tvpo.creator_name as creatorName,\n" +
			"\tvpo.owner_name as ownerName,\n" +
			"\tvpo.pm_num as pmNum,\n" +
			"\tvpo.current_step_name as currentStepName,\n" +
			"\tvpo.int_ext_type as intExtType,\n" +
			"\tvpo.plan_time as planTime\n" +
			"from\n" +
			"\tv2_pm_order vpo,\n" +
			"\tasset_info ai,\n" +
			"\tpm_order_rule por,\n" +
			"\trel_rule_asset rra\n" +
			"where\n" +
			"\tai.id = vpo.asset_id\n" +
			"\tand rra.asset_uid = vpo.asset_uid\n" +
			"\tand por.uid in(rra.rule_uid,\n" +
			"\trra.rule_uid1,\n" +
			"\trra.rule_uid2)\n" +
			"\tand por.\"level\" = vpo.rule_level\n" +
			"\tand ai.is_deleted != true\n" +
			"\tand ai.status != 4\n" +
			"\tand ai.status != 5\n" +
			"\tand ai.is_valid = true\n" +
			"\tand (vpo.tenant_uid = :userOrgUid\n" +
			"\tor vpo.institution_uid = :userOrgUid\n" +
			"\tor vpo.hospital_uid = :userOrgUid\n" +
			"\tor vpo.site_uid = :userOrgUid)",nativeQuery = true)
	List<Object> getAllPmOrder(@Param("userOrgUid") String userOrgUid);

	@Query(value="select\n" +
			"\t (select name from org_info where uid = vpo.site_uid) as siteName,\n" +
			"\tai.\"name\" as assetName,\n" +
			"\tai.serial_num as serialNum,\n" +
			"\tai.depart_num as departNum,\n" +
			"\tai.function_type as functionType,\n" +
			"\tvpo.rule_level as ruleLevel,\n" +
			"\tvpo.pm_type as pmType,\n" +
			"\t (select name from org_info where uid = vpo.clinical_dept_id) as clinicalDeptName,\n" +
			"\tpor.\"name\" as pmRuleName,\n" +
			"\tpor.\"period\" as pmRulePeriod,\n" +
			"\tvpo.creator_name as creatorName,\n" +
			"\tvpo.owner_name as ownerName,\n" +
			"\tvpo.pm_num as pmNum,\n" +
			"\tvpo.current_step_name as currentStepName,\n" +
			"\tvpo.int_ext_type as intExtType,\n" +
			"\tvpo.plan_time as planTime\n" +
			"from\n" +
			"\tv2_pm_order vpo,\n" +
			"\tasset_info ai,\n" +
			"\tpm_order_rule por,\n" +
			"\trel_rule_asset rra\n" +
			"where\n" +
			"\tai.id = vpo.asset_id\n" +
			"\tand rra.asset_uid = vpo.asset_uid\n" +
			"\tand por.uid in(rra.rule_uid,\n" +
			"\trra.rule_uid1,\n" +
			"\trra.rule_uid2)\n" +
			"\tand por.\"level\" = vpo.rule_level\n"+
			"order by vpo.id limit 2000 offset 0"
			,nativeQuery = true)
	List<Object> getAllPmOrder1();
}
