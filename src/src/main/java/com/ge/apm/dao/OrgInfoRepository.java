package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.OrgInfo;

import webapp.framework.dao.GenericRepository;

public interface OrgInfoRepository extends GenericRepository<OrgInfo> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    public List<OrgInfo> getByHospitalId(int hospitalId);

   // @Query(value = "select t.uid from org_info t where site_uid in (?1) and name LIKE concat('%',?2,'%')",nativeQuery = true)
    @Query(value = "select t.uid from org_info t where site_uid in (?1) and name LIKE concat('%',?2,'%')",nativeQuery = true)
    public List<String> getOrgBySite(List<String> orgList,String filterName);

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
    
    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=4 and t.parentUID=?2")
    public OrgInfo getDepartmentByNameParent(String name,String parentUID);
    
    @Query("select t from OrgInfo t where t.name in ?1 and t.orgType=4 and t.siteUID=?2")
    public List<OrgInfo> getDepartmentByName(Set<String> name,String siteUID);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select t from OrgInfo t where t.siteId=?1 order by t.orgLevel, t.parentOrg")
    public List<OrgInfo> getFullOrgListBySiteId(int siteId);
    
    public Page<OrgInfo> getByHospitalId(Pageable pageRequest, int hospitalId);
    public Page<OrgInfo> getBySiteId(Pageable pageRequest, int siteId);
    
    @Query("select o from OrgInfo o where institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1 order by name")
    public List<OrgInfo> getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(String siteId);

    @Query("select o from OrgInfo o where orgType<>4 and orgType<>5 and (institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1)")
    public List<OrgInfo> getOrgInfosByParentId(String siteId);

   @Query("select o from OrgInfo o where orgType = 3 and (institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1)")
   public List<OrgInfo> getOrgInfosByParentIdExcludeClinical(String siteId);
    
    @Query("select o from OrgInfo o,UserOrgInfo u where o.uid=u.orgUid and u.userUid=?1")
    public List<OrgInfo> getByUser(String userUid);
    @Query(value = "select name from org_info where id=?1",nativeQuery = true)
    public String getDepName(int id);
    
    @Query(value = "select o.* from org_info o where o.org_type=4 and (o.uid in (?1) or o.site_uid in (?1) or o.hospital_uid in (?1) or o.institution_uid in (?1) or o.tenant_uid in (?1) ) order by o.name_py_code asc",nativeQuery = true)
    public List<OrgInfo> findClinicalByUids(List<String> listUid);
    
    @Query(value = "select o.* from org_info o where o.org_type=4 and (o.uid in (?1) or o.site_uid in (?1) or o.hospital_uid in (?1) or o.institution_uid in (?1) or o.tenant_uid in (?1) ) order by o.name_py_code asc",nativeQuery = true)
    public List<OrgInfo> findClinicalByUids(Set<String> listUid);
    
    @Query(value = "select o.* from org_info o where o.org_type=4 and (o.uid in (?1) or o.site_uid in (?1) or o.hospital_uid in (?1) or o.institution_uid in (?1) or o.tenant_uid in (?1) ) and o.parent_uid=?2 order by o.name_py_code asc",nativeQuery = true)
    public List<OrgInfo> findClinicalByUidsAndParent(Set<String> listUid,String parentUid);
    
    @Query(value = "select o.* from org_info o where o.uid in (select c.parent_uid from org_info c where c.org_type=4 and (c.uid in (?1) or c.site_uid in (?1) or c.hospital_uid in (?1) or c.institution_uid in (?1) or c.tenant_uid in (?1) )) order by o.name_py_code asc",nativeQuery = true)
    public List<OrgInfo> findParentSiteByUids(Set<String> listUid);
    
    @Query(value = "select o.* from org_info o where o.org_type=3 and (o.uid in (?1) or o.site_uid in (?1) or o.hospital_uid in (?1) or o.institution_uid in (?1) or o.tenant_uid in (?1) ) order by o.name_py_code asc",nativeQuery = true)
    public List<OrgInfo> findSiteByUids(Set<String> listUid);
    
    public OrgInfo getByUid(String uid);

    @Query(value = "select o from OrgInfo o where o.uid in ?1")
    public List<OrgInfo> findByUids(Set<String> idc);
    
    public List<OrgInfo> getByTenantUID(String tenantUID);

    @Query("select o from OrgInfo o where orgType<>4 and tenantUID=?1")
    public List<OrgInfo> getSitesByTenantUID(String tenantUID);

    public List<OrgInfo> findByTenantUIDAndOrgType(String tenantUID, Integer orgType);

    public List<OrgInfo> findByParentUIDAndOrgTypeOrderByNameAsc(String parentUID, Integer orgType);

    public List<OrgInfo> findByParentUID(String parentUID);

    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc(String siteUID, Integer orgType);

    public List<OrgInfo> findBySiteUIDAndOrgTypeIn(String siteUID, Integer[] orgTypes);

    public OrgInfo findByTenantUIDAndName(String tenantUID, String name);

    OrgInfo findByUid(String uid);

    public List<OrgInfo> findByParentUIDAndOrgType(String parentUID, Integer orgType);

    public List<OrgInfo> findBySiteUIDAndOrgType(String siteUID,Integer orgType);
    
    public OrgInfo findFirstByTenantUIDAndOrgTypeAndName(String tenantUID, int orgType, String name);

    public OrgInfo findFirstByParentUIDAndName(String parentUID, String name);

    public OrgInfo findFirstByParentUIDAndShortName(String parentUID, String name);
    
    @Query(value = "select o.uid from org_info o where (o.tenant_uid = ?1 or o.institution_uid = ?1 or o.hospital_uid = ?1 or o.site_uid = ?1) and o.org_type=4",nativeQuery = true)
    public Set<String> findClinicalDeptOrgUidListByOrgUid(String orgUid);
    
    @Query(value = "select o.uid from org_info o where (o.tenant_uid in (?1) or o.institution_uid in (?1) or o.hospital_uid in (?1) or o.site_uid in (?1)) and o.org_type=4",nativeQuery = true)
    public Set<String> findClinicalDeptOrgUidListByOrgUid(Set<String> orgUid);

    public OrgInfo findFirstByParentUIDAndNameAndUidIsNot(String parentUid, String name, String uid);

    public int countByParentUIDAndUidIsNot(String parentUid, String uid);

    public int countByParentUIDAndOrgType(String parentUid, int orgType);



}
