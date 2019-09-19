package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainCheckItem;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainCheckItemRepository extends GenericRepositoryUUID<NurseSiteMaintainCheckItem> {
	@Query(value="select o.* from nurse_site_maintain_check_item o where o.site_uid=?1",nativeQuery=true)
	public List<NurseSiteMaintainCheckItem> findBySiteUid(String siteUid);
	
	@Query(value="select exists(select 1 from nurse_site_maintain_check_item o where o.site_uid=?1 and o.name=?2)",nativeQuery=true)
	public boolean findExistsBySiteUid(String siteUid,String name);
}
