package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainConfigItemRepository extends GenericRepositoryUUID<NurseSiteMaintainConfigItem> {
	
	@Query(value="select o.* from nurse_site_maintain_config_item o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfigItem findById(String id);
	
	@Query(value="select o.* from nurse_site_maintain_config_item o where o.parent_id=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByParentId(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_config_item o where o.asset_uid=?1)",nativeQuery=true)
	public Boolean findExistsAssetUid(String assetUid);
	
	@Query(value="select o.* from nurse_site_maintain_config_item o order by o.create_date limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByLimit(int limit,int offset);
}
