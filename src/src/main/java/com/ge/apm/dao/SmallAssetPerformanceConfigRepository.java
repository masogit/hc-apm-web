package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.SmallAssetPerformanceConfig;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface SmallAssetPerformanceConfigRepository extends GenericRepositoryUUID<SmallAssetPerformanceConfig> {

	@Query("select o from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.siteUID = ?2")
	public List<SmallAssetPerformanceConfig> findConfigByProjectNameAndSiteUID(String projectName, String siteUID);
	
	@Query("select o from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.assetName = ?2 and o.department=?3 and o.siteUID = ?4")
	public SmallAssetPerformanceConfig findByProjectNameAndAssetName(String projectName, String assetName,String department,String siteUID);

	@Query("select 1 from SmallAssetPerformanceConfig o where o.projectName = ?1 and o.assetName = ?2 and o.department=?3 and o.siteUID = ?4")
	public Integer findExistsByProjectNameAndAssetName(String projectName, String assetName,String department,String siteUID);
	

}
