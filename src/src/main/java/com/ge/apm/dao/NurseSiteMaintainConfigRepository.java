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
	@Query(value="select o.* from nurse_site_maintain_config o where o.site_uid=?1 limit 1",nativeQuery=true)
	public NurseSiteMaintainConfig findBySiteUid(String siteUID);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfig findByUid(String id);
}
