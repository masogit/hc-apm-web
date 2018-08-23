package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.NurseSiteMaintainData;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainDataRepository extends GenericRepositoryUUID<NurseSiteMaintainData> {
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in (select b.id from nurse_site_maintain_item where b.parent_id in (select c.id from nurse_site_maintain where c.id=?1))",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String maintainId);
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in ?1",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByParentIds(Set<String> parentIds);
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainData> findListMaintainDataByParentIds(Set<String> parentIds);
	
	@Query(value="select o.id from nurse_site_maintain_data o where o.parent_id in ?1",nativeQuery=true)
	public Set<String> findIdsByParentIds(Set<String> parentIds);
	
	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_data set check_value=true where id in ?1", nativeQuery = true)
	void completeMaintainData(Set<String> ids);
	
	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_data where id in ?1", nativeQuery = true)
	void deleteMaintainData(Set<String> ids);
}
