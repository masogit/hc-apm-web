package com.ge.apm.dao;

import java.util.List;

import com.ge.apm.domain.WorkflowConfig;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import webapp.framework.dao.GenericRepository;
import java.util.List;

public interface WorkflowConfigRepository extends GenericRepository<WorkflowConfig> {
    @Deprecated
    public WorkflowConfig getBySiteIdAndHospitalId(Integer siteId,Integer hospitalId);
    public WorkflowConfig getByHospitalId(int hospitalId);
    
    @Query(value="select w from WorkflowConfig w where w.siteUID in ?1")
    public List<WorkflowConfig>  getBySiteUIDList(List<String> uidList);
    
    @Query(value="select * from workflow_config w where w.site_UID=:uid order by w.id desc limit 1",nativeQuery = true)
    public WorkflowConfig getBySiteUID(@Param("uid")String site_uid);
    
	public List<WorkflowConfig> findBySiteUID(String institutionUID);

    public WorkflowConfig findFirstBySiteUID(String siteUid);
	
	 @Query(value="delete from WorkflowConfig wc where wc.siteUID = ?1")
	 @Modifying
	 @Transactional
	public void deleteBySiteUID(String siteUID);

    public List<WorkflowConfig> findByDeptForSharedAsset(String orgUid);

    @Query(value="select distinct dept_for_shared_asset from workflow_config WHERE site_uid = ?1 AND dept_for_shared_asset IS NOT NULL",nativeQuery = true)
    public List<String> findDeptForSharedAsset(String siteUID);

    @Query("select w from WorkflowConfig w where w.siteUID = :hostId and w.hospitalUID =:hostId and dept_for_shared_asset is not null ")
    public List<WorkflowConfig> findHospitalLevel(@Param("hostId") String hostId);

    List<WorkflowConfig> findByTenantUID(String tenantUID);

    @Query(value="select * from workflow_config where generate_method = 1 and prefix is not null and tenant_uid = ?1",nativeQuery = true)
    List<WorkflowConfig> findAllPrefixByTenantUID(String tenantUID);
}
