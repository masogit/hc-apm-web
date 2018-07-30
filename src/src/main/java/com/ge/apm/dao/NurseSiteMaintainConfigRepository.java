package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainConfig;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainConfigRepository extends GenericRepositoryUUID<NurseSiteMaintainConfig> {
	@Query(value="select distinct o.* from nurse_site_maintain_config o where o.clinical_dept_uid in (?1) order by o.modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByOrgUids(Set<String> orgUids);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfig findById(String id);
	
	
}
