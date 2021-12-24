package com.ge.apm.dao;

import com.ge.apm.domain.NurseSiteMaintainConfigJN;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 * Created by 503248590 on 2021/12/21.
 */
public interface NurseSiteMaintainConfigJNRepository extends GenericRepositoryUUID<NurseSiteMaintainConfigJN> {
	@Query(value="select distinct o.* from nurse_site_maintain_config_jn o where o.clinical_dept_uid in (?1) order by o.modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfigJN> findByOrgUids(List<String> orgUids);
	
	@Query(value="select o.* from nurse_site_maintain_config_jn o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfigJN findById(String id);
	
	@Query(value="select o.* from nurse_site_maintain_config_jn o where o.id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJN> fetchListByIds(Set<String> ids);
	
	@Query(value="select o.* from nurse_site_maintain_config_jn o where o.site_uid = ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJN> findBySiteUid(String siteUID);
	
	@Query(value="select o.* from nurse_site_maintain_config_jn o order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainConfigJN> findByLimit(int limit,int offset);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_config_jn where id in ?1", nativeQuery = true)
	public void deleteMaintainConfigData(Set<String> ids);
}
