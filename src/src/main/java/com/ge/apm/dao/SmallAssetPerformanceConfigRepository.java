package com.ge.apm.dao;

import com.ge.apm.domain.SmallAssetPerformanceConfig;
import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface SmallAssetPerformanceConfigRepository extends GenericRepositoryUUID<SmallAssetPerformanceConfig> {

	@Query("select o from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.siteUID = ?2")
	public List<SmallAssetPerformanceConfig> findConfigByProjectNameAndSiteUID(String projectName, String siteUID);
	
	@Query("select o from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.assetName = ?2 and o.siteUID = ?3")
	public SmallAssetPerformanceConfig findByProjectNameAndAssetName(String projectName, String assetName,String siteUID);

	@Query("select 1 from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.assetName = ?2 and o.siteUID = ?3")
	public Integer findExistsByProjectNameAndAssetName(String projectName, String assetName,String siteUID);

}
