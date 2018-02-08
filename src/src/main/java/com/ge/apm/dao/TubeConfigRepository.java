package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.TubeConfig;

import webapp.framework.dao.GenericRepository;

public interface TubeConfigRepository extends GenericRepository<TubeConfig> {

	@Query("select tc from TubeConfig tc where tc.tenantUID = ?1 and tc.assetUid in ?2")
	List<TubeConfig> findbyTenantUidAndAssetUidIn(String tenantUid, List<String> uids);


}
