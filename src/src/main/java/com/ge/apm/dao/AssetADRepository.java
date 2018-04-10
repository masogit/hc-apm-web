package com.ge.apm.dao;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.AssetAD;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface AssetADRepository extends GenericRepositoryUUID<AssetAD> {

	@Query("select o from AssetAD o where o.systemId=?1")
	public AssetAD findBySystemId(String systemId);
}
