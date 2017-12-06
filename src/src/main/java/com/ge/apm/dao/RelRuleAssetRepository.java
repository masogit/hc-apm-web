package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.ge.apm.domain.RelRuleAsset;
import webapp.framework.dao.GenericRepositoryUUID;

public interface RelRuleAssetRepository extends GenericRepositoryUUID<RelRuleAsset> {

	@Transactional
	public void deleteByAssetUIDIn(List<String> deleteIds);

	@	Query("select r.assetUID from RelRuleAsset r where r.tenantUID = ?1")
	public List<String> findAssetUidsByTenantUID(String tenantUID);
	
}
