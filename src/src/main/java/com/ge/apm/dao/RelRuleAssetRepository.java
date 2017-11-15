package com.ge.apm.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.RelRuleAsset;
import webapp.framework.dao.GenericRepository;

public interface RelRuleAssetRepository extends GenericRepository<RelRuleAsset> {

	@Transactional
	public void deleteByAssetUIDIn(List<String> deleteIds);

}
