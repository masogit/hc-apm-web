package com.ge.apm.dao;

import com.ge.apm.domain.V2_PmOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Collection;
import java.util.List;

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
	
	@Query(value = "select * from v2_pm_order where asset_uid= ?1 and is_finished='false' order by created_date desc limit 1",nativeQuery = true)
	public String findByAssetUid(String asset_uid);


}
