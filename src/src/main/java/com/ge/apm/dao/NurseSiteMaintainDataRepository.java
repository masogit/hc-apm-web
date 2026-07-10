package com.ge.apm.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.NurseSiteMaintainData;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 503035627
 */
public interface NurseSiteMaintainDataRepository extends GenericRepositoryUUID<NurseSiteMaintainData> {
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in (select b.id from nurse_site_maintain_item where b.parent_id in (select c.id from nurse_site_maintain where c.id=?1))",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String maintainId);
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in ?1",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByParentIds(Set<String> parentIds);
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in ?1 order by index",nativeQuery=true)
	public List<NurseSiteMaintainData> findListMaintainDataByParentIds(Set<String> parentIds);
	
	@Query(value="select o.id from nurse_site_maintain_data o where o.parent_id in ?1",nativeQuery=true)
	public Set<String> findIdsByParentIds(Set<String> parentIds);

    public List<NurseSiteMaintainData> findLByParentId(String parentId);
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set check_value=true where id in ?1", nativeQuery = true)
	void completeMaintainData(Set<String> ids);

	@Query(value = "SELECT count(parent_id) - count(check_value) FROM nurse_site_maintain_data WHERE parent_id = ?1",nativeQuery = true)
	int noCheckCount(String parentId);
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set check_value=true where id in ?1 and check_item in ?2", nativeQuery = true)
	void completeMaintainData(Set<String> ids,List<String> listCheckData);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_data where id in ?1", nativeQuery = true)
	void deleteMaintainData(Set<String> ids);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_data a where a.parent_id in (select b.id from nurse_site_maintain_item b where b.parent_id = ?1)", nativeQuery = true)
	void deleteMaintainDataByMaintainId(String parentId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_data a where not exists (select * from nurse_site_maintain_item b where b.id=a.parent_id)", nativeQuery = true)
	void deleteInvalid();

	@Transactional
	@Modifying
	//@Query(value = "update nurse_site_maintain_data set check_value='f' where parent_id in (select b.id from nurse_site_maintain_item b where b.status=0 and b.schedule_date>=?1 and b.schedule_date<?2 and b.asset_uid in(select a.uid from asset_info a where a.uid in (select d.asset_uid from nurse_site_maintain_item d where d.schedule_date>=?1 and d.schedule_date<?2) and (a.status in ?3 or a.is_valid=false or a.is_deleted=true) ) and b.config_id in (select c.id from nurse_site_maintain_config c where c.type=?4))", nativeQuery = true)
	@Query(value = "update nurse_site_maintain_data nsmd set check_value='f' where exists(select * from nurse_site_maintain_item nsmi where nsmi.schedule_date>=?1 and nsmi.schedule_date<?2 and nsmi.id=nsmd.parent_id and nsmi.status=0 and exists(select * from asset_info ai where ai.uid=nsmi.asset_uid and (ai.status in ?3 or ai.is_valid=false or ai.is_deleted=true) and not exists(select * from nurse_tenant_exception nte where nte.tenant_uid=ai.tenant_uid)) and exists(select * from nurse_site_maintain_config nsmc where nsmc.type=?4 and nsmc.id=nsmi.config_id))", nativeQuery = true)
	void updateDailyByStatus(Date startDate, Date endDate, Collection<Integer> statusArr, int configType);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set check_value=true where parent_id in ?1", nativeQuery = true)
	void completeMaintainDataByParent(Set<String> ids);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_data where parent_id in ?1", nativeQuery = true)
	void deleteMaintainDataByParentId(Collection<String> parentId);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set is_skip = null where is_skip = true " +
			"and parent_id in (select nsmi.id from nurse_site_maintain_item nsmi " +
			"join nurse_site_maintain nsm on nsmi.parent_id = nsm.id " +
			"where nsmi.is_finish = false and nsm.site_uid = ?1 " +
			"and not exists (select 1 from config_item_value civ where civ.key = ?2 and civ.site_uid = nsm.site_uid))", nativeQuery = true)
	void clearSkipForUnfinishedItemsBySite(String siteUid, String configKey);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set is_skip = null where is_skip = true " +
			"and parent_id in (select nsmi.id from nurse_site_maintain_item nsmi " +
			"join nurse_site_maintain nsm on nsmi.parent_id = nsm.id " +
			"where nsmi.is_finish = false and nsm.hospital_uid = ?1 " +
			"and not exists (select 1 from config_item_value civ where civ.key = ?2 and civ.site_uid = nsm.site_uid))", nativeQuery = true)
	void clearSkipForUnfinishedItemsByHospital(String hospitalUid, String configKey);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set is_skip = null where is_skip = true " +
			"and parent_id in (select nsmi.id from nurse_site_maintain_item nsmi " +
			"join nurse_site_maintain nsm on nsmi.parent_id = nsm.id " +
			"where nsmi.is_finish = false and nsm.institution_uid = ?1 " +
			"and not exists (select 1 from config_item_value civ where civ.key = ?2 and civ.site_uid = nsm.site_uid))", nativeQuery = true)
	void clearSkipForUnfinishedItemsByInstitution(String institutionUid, String configKey);

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set is_skip = null where is_skip = true " +
			"and parent_id in (select nsmi.id from nurse_site_maintain_item nsmi " +
			"join nurse_site_maintain nsm on nsmi.parent_id = nsm.id " +
			"where nsmi.is_finish = false and nsm.tenant_uid = ?1 " +
			"and not exists (select 1 from config_item_value civ where civ.key = ?2 and civ.site_uid = nsm.site_uid))", nativeQuery = true)
	void clearSkipForUnfinishedItemsByTenant(String tenantUid, String configKey);
}
