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


    
    @Query(value="select w from WorkflowConfig w where w.siteUID in ?1")
    public List<WorkflowConfig>  getBySiteUIDList(List<String> uidList);

    @Query(value=" select w from WorkflowConfig w where w.siteUID in ?1 or " +
            " exists(select 1 from OrgInfo orginfo0_ where orginfo0_.orgType<>4 and orginfo0_.uid = w.siteUID and " +
            " (exists(select 1 from OrgInfo orginfo0_1,UserOrgInfo userorginf1_1 where orginfo0_1.uid=userorginf1_1.orgUid and orginfo0_1.orgType <> 4 " +
            " and userorginf1_1.userUid = ?2 and (orginfo0_1.uid = orginfo0_.siteUID or orginfo0_1.uid = orginfo0_.institutionUID or orginfo0_1.uid = orginfo0_.tenantUID or orginfo0_1.uid = orginfo0_.hospitalUID)) " +
            " or exists( select 1 from OrgInfo orginfo0_1,UserOrgInfo userorginf1_1 where orginfo0_1.uid=userorginf1_1.orgUid and orginfo0_1.orgType = 4 and userorginf1_1.userUid = ?2 and orginfo0_1.siteUID = orginfo0_.uid ))) ")
    public List<WorkflowConfig>  getBySiteUIDList1(List<String> uidList,String uid);
    
    @Query(value="select * from workflow_config w where w.site_UID=:uid order by w.id desc limit 1",nativeQuery = true)
    public WorkflowConfig getBySiteUID(@Param("uid")String site_uid);
    
	public List<WorkflowConfig> findBySiteUID(String institutionUID);
    public List<WorkflowConfig> findByHospitalUID(String hospitalId);
    public List<WorkflowConfig> findByInstitutionUID(String institutionId);

    public WorkflowConfig findFirstBySiteUID(String siteUid);

	 @Query(value="delete from WorkflowConfig wc where wc.siteUID = ?1")
	 @Modifying
	 @Transactional
	public void deleteBySiteUID(String siteUID);

    public List<WorkflowConfig> findByDeptForSharedAsset(String orgUid);

    @Query(value="select distinct dept_for_shared_asset from workflow_config WHERE site_uid = ?1 AND dept_for_shared_asset IS NOT NULL",nativeQuery = true)
    public List<String> findDeptForSharedAsset(String siteUID);

    @Query("select w from WorkflowConfig w where w.siteUID = :hostId and dept_for_shared_asset is not null ")
    public List<WorkflowConfig> findHospitalLevel(@Param("hostId") String hostId);

    List<WorkflowConfig> findByTenantUID(String tenantUID);

    @Query(value="select * from workflow_config where generate_method = 1 and prefix is not null and tenant_uid = ?1",nativeQuery = true)
    List<WorkflowConfig> findAllPrefixByTenantUID(String tenantUID);
}
