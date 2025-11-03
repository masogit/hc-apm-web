package com.ge.apm.dao;

import com.ge.apm.domain.V2_PmOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface V2_PmOrderRepository extends GenericRepositoryUUID<V2_PmOrder> {

	@Query(value="select id from Asset_Info a where (a.tenant_UID in (?1) or a.institution_UID in (?1) or a.hospital_UID in (?1) or a.site_UID in (?1) or a.clinical_Dept_UID in (?1)) and id not in (select tr.asset_Id from Asset_Tag_Rule tr, Asset_Tag_Biomed_Group tg, Biomed_Group_User tu where tr.tag_Id=tg.tag_Id and tg.biomed_Group_Id=tu.group_Id)",nativeQuery = true)
	public List<Integer> getAssetsByNoTagGroup(List<String> c);

	@Query(value = "select * from v2_pm_order  where asset_uid =?1  order by created_date desc limit 4", nativeQuery = true)
	public List<V2_PmOrder> getPmOrdersForAsset(String assetUid);

	List<V2_PmOrder> findByOriginPmNumAndSiteUID(String originPmNum,String siteUid);

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

	@Query(value="select vpo.* from v2_pm_order vpo ,asset_info ai where ai.uid=vpo.asset_uid \n" +
			"\tand ai.is_deleted != true\n" +
			"\tand ai.is_valid = true\n" +
			"\tand (ai.tenant_uid in (?1)\n" +
			"\tor ai.institution_uid in (?1)\n" +
			"\tor ai.clinical_dept_uid in (?1)\n" +
			"\tor ai.asset_dept_uid in (?1)\n" +
			"\tor ai.hospital_uid in (?1)\n" +
			"\tor ai.site_uid in (?1)) order by vpo.plan_time asc nulls last,vpo.pm_num asc",nativeQuery = true)
	public  List<V2_PmOrder> getPmOrderListAll(List<String >OrgUid);

	public V2_PmOrder findById(String Id);

	public List<V2_PmOrder> getByAssetUid(String asset_uid);

	@Query(value="select asset_uid from v2_pm_order where id in ?1",nativeQuery = true)
	public Set<String> getAssetUid(List<String> uidList);

	@Query(value = "select o.* from v2_pm_order o where o.asset_uid in ?1 and o.is_finished='false' order by o.plan_time ",nativeQuery = true)
	public Set<V2_PmOrder> findV2PmOrderByAssetUids(Set<String> setAssetUids);

	@Query(value = "select o.asset_uid from v2_pm_order o where o.asset_uid in ?1 and o.is_finished='false' order by o.plan_time ",nativeQuery = true)
	public List<String> findV2PmOrderByAssetUidByBatchEdit(Set<String> setAssetUids);
	@Query(value="select\n" +
			"\t(\n" +
			"\tselect\n" +
			"\t\tname\n" +
			"\tfrom\n" +
			"\t\torg_info\n" +
			"\twhere\n" +
			"\t\tuid = vpo.site_uid) as siteName,\n" +
			"\tai.\"name\" as assetName,\n" +
			"\tai.serial_num as serialNum,\n" +
			"\tai.depart_num as departNum,\n" +
			"\tai.function_type as functionType,\n" +
			"\t (select value_zh from i18n_message where msg_type='ruleLevelFour' and msg_key = cast(vpo.rule_level as text)) as ruleLevel,\n" +
			"\t (select value_zh from i18n_message where msg_type='pmType' and msg_key = cast(vpo.pm_type as text)) as pmType,\n" +
			"\t(\n" +
			"\tselect\n" +
			"\t\tname\n" +
			"\tfrom\n" +
			"\t\torg_info\n" +
			"\twhere\n" +
			"\t\tuid = vpo.clinical_dept_id) as clinicalDeptName,\n" +
			"\tpor.\"name\" as pmRuleName,\n" +
			"\tpor.\"period\" as pmRulePeriod,\n" +
			"\tvpo.creator_name as creatorName,\n" +
			"\tvpo.owner_name as ownerName,\n" +
			"\tvpo.pm_num as pmNum,\n" +
			"\tvpo.current_step_name as currentStepName,\n" +
			"\t (select value_zh from i18n_message where msg_type='intExtType' and msg_key = cast(vpo.int_ext_type as text)) as intExtType,\n" +
			"\t to_char(vpo.plan_time,'MM/dd/yyyy') as planTime\n" +
			"from\n" +
			"  \tasset_info ai,\n" +
			"\tv2_pm_order vpo\n" +
			"full join rel_rule_asset rra on\n" +
			"\tvpo.asset_uid = rra.asset_uid\n" +
			"full join pm_order_rule por on\n" +
			"\tpor.\"level\" = vpo.rule_level\n" +
			"\tand por.uid in (rra.rule_uid,\n" +
			"\trra.rule_uid1,\n" +
			"\trra.rule_uid2)\n" +
			"where\n" +
			"\tai.id = vpo.asset_id\n" +
			"\tand ai.is_deleted != true\n" +
			"\tand ai.status != 4\n" +
			"\tand ai.status != 5\n" +
			"\tand ai.is_valid = true\n" +
			"\tand (ai.tenant_uid in (?1)\n" +
			"\tor ai.institution_uid in (?1)\n" +
			"\tor ai.clinical_dept_uid in (?1)\n" +
			"\tor ai.asset_dept_uid in (?1)\n" +
			"\tor ai.hospital_uid in (?1)\n" +
			"\tor ai.site_uid in (?1)) order by vpo.plan_time asc nulls last,vpo.pm_num asc",nativeQuery = true)
	List<Object> getAllPmOrder(List<String >OrgUid);


	/**
	 * 获取个数
	 * @param dateFormat 日期格式
	 * @param date 开单月份
	 * @param tenantUID 租户 tenantUID
	 * @return
	 */
	@Query(value = "SELECT count(id) from ( " +
			" SELECT " +
			" DISTINCT v2.id " +
			" FROM " +
			" v2_pm_order v2 " +
			" INNER JOIN asset_info a on v2.asset_id = a.id "+
			" INNER JOIN zkb_contract_asset zca ON zca.asset_id = a.uid "+
			" INNER JOIN zkb_contract zkbc ON zkbc.ID = zca.contract_id "+
			" where to_char(v2.created_date, ?1)=?2 " +
			" and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd') ) T", nativeQuery = true)
	Integer fetchCount(String dateFormat, String date, String tenantUID);

	/**
	 * 获取个数
	 * @param dateFormat 日期格式
	 * @param date 开单月份
	 * @param tenantUID 租户 tenantUID
	 * @return
	 */
	@Query(value = "SELECT coalesce(count(id),0) from ( " +
			" SELECT " +
			" DISTINCT v2.id " +
			" FROM " +
			" v2_pm_order v2 " +
			" INNER JOIN asset_info a on v2.asset_id = a.id "+
			" INNER JOIN zkb_contract_asset zca ON zca.asset_id = a.uid "+
			" INNER JOIN zkb_contract zkbc ON zkbc.ID = zca.contract_id "+
			" where zkbc.contract_type <> 'CCSB' and to_char(v2.created_date, ?1)=?2 " +
			" and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(v2.created_date, 'yyyy-MM-dd'),'yyyy-MM-dd') ) T", nativeQuery = true)
	Integer fetchCountOfCCSB(String dateFormat, String date, String tenantUID);

	@Query(value = "select count(asset_id) from v2_pm_order where asset_id= ?1 and  rule_level = ?2 and pm_type = ?3 ",nativeQuery = true)
	Long findCountByAssetId(Integer id, Integer level, Integer pmType);

	@Query(value = "select count(asset_id) from v2_pm_order where asset_id= ?1 and pm_type = ?2 ",nativeQuery = true)
	Long findCountByAssetIdAndPmType(Integer id,Integer pmType);
	@Modifying
	@Transactional
	@Query(value = "delete from v2_pm_order where id in ?1",nativeQuery = true)
	Integer deleteById(List<String> collect);

	@Query(value = "select o.* from v2_pm_order o where o.asset_uid in ?1 and o.is_finished='false' ",nativeQuery = true)
	List<V2_PmOrder> findByAssets(List<String> assetUids);

	@Query(value = "select * from v2_pm_order order by last_modified_date desc limit ?1 offset ?2",nativeQuery = true)
	List<V2_PmOrder> limit(Integer limit, Integer offset);

	@Query(nativeQuery = true,value = "select * from v2_pm_order where asset_uid=?1 and is_finished =?2")
	List<V2_PmOrder> findByAssetUidAndIsFinished(String assetUid,boolean status);

	@Query(nativeQuery = true,value = "select * from v2_pm_order where asset_uid in (?1) and is_finished =?2")
	List<V2_PmOrder> findByAssetUidInAndIsFinished(List<String> assetUid, boolean status);
}
