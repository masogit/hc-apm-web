package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainConfigItem;

import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 503035627
 */
public interface NurseSiteMaintainConfigItemRepository extends GenericRepositoryUUID<NurseSiteMaintainConfigItem> {
	
	@Query(value="select o.* from nurse_site_maintain_config_item o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfigItem findById(String id);
	
	@Query(value="select o.* from nurse_site_maintain_config_item o where o.parent_id=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByParentId(String parentId);
	
	@Query(value="select o.* from nurse_site_maintain_config_item o where o.asset_uid=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findExistsAssetUid(String assetUid);
	
	@Query(value="select o.* from nurse_site_maintain_config_item o order by o.create_date limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByLimit(int limit,int offset);

	@Query(value="select o.* from nurse_site_maintain_config_item o where o.asset_uid in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByAssetUid(List<String> uids);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_config_item where id in ?1", nativeQuery = true)
	public void deleteMaintainData(Set<String> ids);

	@Query(value="select o.* from nurse_site_maintain_config_item o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByParentIds(Set<String> parentIds);

	@Transactional
	@Modifying
	@Query(value="delete from nurse_site_maintain_config_item o where o.asset_uid=?1",nativeQuery=true)
	public void deleteByAssetUid(String assetUid);

	@Query(value="select o.* from nurse_site_maintain_config_item o where o.asset_uid=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigItem> findByAssetUid(String assetUid);
}
