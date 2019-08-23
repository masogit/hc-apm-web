package com.ge.apm.dao;

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
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set check_value=true where id in ?1", nativeQuery = true)
	void completeMaintainData(Set<String> ids);
	
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
	@Query(value = "delete from nurse_site_maintain_data a where a.parent_id not in (select b.id from nurse_site_maintain_item b )", nativeQuery = true)
	void deleteInvalid();
}
