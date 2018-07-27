package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.SmallAssetPerformanceConfig;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainConfigRepository extends GenericRepositoryUUID<NurseSiteMaintainConfig> {
	@Query(value="select distinct o.* from nurse_site_maintain_config o where o.clinical_dept_uid in (?1) order by o.modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByOrgUids(List<String> orgUids);
	
	@Query(value="select distinct o.* from nurse_site_maintain_config o where o.site_uid = ?1 or o.hospital_uid = ?1 or o.institution_uid = ?1 or o.tenant_uid = ?1 order by o.modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByOrgUid(String orgUid);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfig findById(String id);
	
	
}
