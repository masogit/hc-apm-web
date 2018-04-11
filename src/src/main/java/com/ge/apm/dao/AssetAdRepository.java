package com.ge.apm.dao;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.AssetAd;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface AssetAdRepository extends GenericRepositoryUUID<AssetAd> {

	@Query("select o from AssetAd o where o.systemId=?1")
	public AssetAd findBySystemId(String systemId);
	
	@Query("select o.content from AssetAd o where o.id=?1")
	public String getContentById(String id);
}
