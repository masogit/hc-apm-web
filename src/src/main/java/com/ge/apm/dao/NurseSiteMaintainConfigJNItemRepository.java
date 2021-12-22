package com.ge.apm.dao;

import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import com.ge.apm.domain.NurseSiteMaintainConfigJNItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 * Created by 503035627
 */
public interface NurseSiteMaintainConfigJNItemRepository extends GenericRepositoryUUID<NurseSiteMaintainConfigJNItem> {
	
	@Query(value="select o.* from nurse_site_maintain_config_item_jn o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfigJNItem findById(String id);
	
	@Query(value="select o.* from nurse_site_maintain_config_item_jn o where o.parent_id=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJNItem> findByParentId(String parentId);
	
	@Query(value="select exists (select 1 from nurse_site_maintain_config_item_jn o where o.asset_uid=?1)",nativeQuery=true)
	public Boolean findExistsAssetUid(String assetUid);
	
	@Query(value="select o.* from nurse_site_maintain_config_item_jn o order by o.create_date limit ?1 offset ?2*?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJNItem> findByLimit(int limit,int offset);

	@Query(value="select o.* from nurse_site_maintain_config_item_jn o where o.asset_uid in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJNItem> findByAssetUid(List<String> uids);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_config_item_jn where id in ?1", nativeQuery = true)
	public void deleteMaintainData(Set<String> ids);

	@Query(value="select o.* from nurse_site_maintain_config_item_jn o where o.parent_id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJNItem> findByParentIds(Set<String> parentIds);

	@Transactional
	@Modifying
	@Query(value="delete from nurse_site_maintain_config_item_jn o where o.asset_uid=?1",nativeQuery=true)
	public void deleteByAssetUid(String assetUid);

	@Query(value="select o.* from nurse_site_maintain_config_item_jn o where o.asset_uid=?1",nativeQuery=true)
	public List<NurseSiteMaintainConfigJNItem> findByAssetUid(String assetUid);
}
