package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainConfig;

import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainConfigRepository extends GenericRepositoryUUID<NurseSiteMaintainConfig> {
	@Query(value="select distinct o.* from nurse_site_maintain_config o where o.clinical_dept_uid in (?1) and o.deleted = false order by o.modify_date desc",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByOrgUids(List<String> orgUids);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id=?1",nativeQuery=true)
	public NurseSiteMaintainConfig findById(String id);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.id in ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfig> fetchListByIds(Set<String> ids);
	
	@Query(value="select o.* from nurse_site_maintain_config o where o.site_uid = ?1",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findBySiteUid(String siteUID);
	
	@Query(value="select o.* from nurse_site_maintain_config o order by o.create_date limit ?1 offset ?2",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByLimit(int limit,int offset);

	@Transactional
	@Modifying
	@Query(value = "delete from nurse_site_maintain_config where id in ?1", nativeQuery = true)
	public void deleteMaintainConfigData(Set<String> ids);

	@Query(value="select o.* from nurse_site_maintain_config o where o.deleted = true and o.delete_result = false",nativeQuery=true)
	public List<NurseSiteMaintainConfig> findByDeletedStatus();

	@Transactional
	@Modifying
	@Query(value = "update nurse_site_maintain_config set deleted=?1,delete_result=?2,modify_by=?3,modify_date=?4 where id=?5 ", nativeQuery = true)
	public void deleteToSoft(boolean deleted, boolean deleteResult, String modifyByUid, Date modifyDate,@Param("uid") String uid);
}
