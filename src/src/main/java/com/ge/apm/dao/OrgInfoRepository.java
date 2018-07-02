package com.ge.apm.dao;

import com.ge.apm.domain.OrgInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface OrgInfoRepository extends GenericRepository<OrgInfo> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public List<OrgInfo> getByHospitalId(int hospitalId);
    
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select t from OrgInfo t where t.siteId=?1 and t.parentOrg is null")
    public List<OrgInfo> getHospitalBySiteId(int siteId);
    
    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=1 and t.tenantUID=?2")
    public OrgInfo getInstitutionByName(String name,String tenantUID);
    
    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=2 and t.institutionUID=?2")
    public OrgInfo getHospitalByName(String name,String institutionUID);
    
    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=3 and t.hospitalUID=?2")
    public OrgInfo getSiteByName(String name,String hospitalUID);
    
    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=4 and t.siteUID=?2")
    public OrgInfo getDepartmentByName(String name,String siteUID);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select t from OrgInfo t where t.siteId=?1 order by t.orgLevel, t.parentOrg")
    public List<OrgInfo> getFullOrgListBySiteId(int siteId);
    
    public Page<OrgInfo> getByHospitalId(Pageable pageRequest, int hospitalId);
    public Page<OrgInfo> getBySiteId(Pageable pageRequest, int siteId);
    
    @Query("select o from OrgInfo o where institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1 order by name")
    public List<OrgInfo> getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(String siteId);

    @Query("select o from OrgInfo o where orgType<>4 and (institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1)")
    public List<OrgInfo> getOrgInfosByParentId(String siteId);
    
    @Query("select o from OrgInfo o,UserOrgInfo u where o.uid=u.orgUid and u.userUid=?1")
    public List<OrgInfo> getByUser(String userUid);
    @Query(value = "select name from org_info where id=?1",nativeQuery = true)
    public String getDepName(int id);
    
    public OrgInfo getByUid(String uid);
    
    public List<OrgInfo> getByTenantUID(String tenantUID);

    @Query("select o from OrgInfo o where orgType<>4 and tenantUID=?1")
    public List<OrgInfo> getSitesByTenantUID(String tenantUID);

    public List<OrgInfo> findByTenantUIDAndOrgType(String tenantUID, Integer orgType);

    public List<OrgInfo> findByParentUIDAndOrgTypeOrderByNameAsc(String parentUID, Integer orgType);

    public List<OrgInfo> findByParentUID(String parentUID);

    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc(String siteUID, Integer orgType);

    public OrgInfo findByTenantUIDAndName(String tenantUID, String name);

    OrgInfo findByUid(String uid);

    public List<OrgInfo> findByParentUIDAndOrgType(String parentUID, Integer orgType);

    public List<OrgInfo> findBySiteUIDAndOrgType(String siteUID,Integer orgType);

    public OrgInfo findFirstByTenantUIDAndOrgTypeAndName(String tenantUID, int orgType, String name);

}
