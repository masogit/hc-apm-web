package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainItem;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainItemRepository extends GenericRepositoryUUID<NurseSiteMaintainItem> {
	
	@Query(value="select o.* from nurse_site_maintain_item o where parent_id=?1",nativeQuery=true)
	public Set<NurseSiteMaintainItem> findMaintainItemByParentId(String parentId);
	
	@Query(value="select o.* from nurse_site_maintain_item o where parent_id in ?1",nativeQuery=true)
	public Set<NurseSiteMaintainItem> findMaintainItemByParentId(Set<String> parentIds);
}
