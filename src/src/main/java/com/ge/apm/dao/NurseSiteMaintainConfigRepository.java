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
	@Query(value="select distinct o.* from nurse_site_maintain_config o,user_org a where o.site_uid=?1 and o.create_by=a.user_uid and a.org_uid in (select b.org_uid from user_org b where b.user_uid=?2) order by modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findBySiteUid(String siteUID,String userUid);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfig findById(String id);
	
	
}
