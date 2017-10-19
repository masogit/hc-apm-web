package com.ge.apm.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.AssetSeriaNumConfig;
import webapp.framework.dao.GenericRepository;

public interface AssetSeriaNumConfigRepository extends GenericRepository<AssetSeriaNumConfig> {

	 @Query(value="delete from AssetSeriaNumConfig wc where wc.siteUID = ?1")
	 @Modifying
	 @Transactional
	public void deleteBySiteUID(String siteUID);

	public AssetSeriaNumConfig findBySiteUID(String siteUID);

	public AssetSeriaNumConfig findByHospitalUID(String hospitalUID);

	public AssetSeriaNumConfig findBySiteUIDAndLevel(String siteUID, int level);

}
