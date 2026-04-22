package com.ge.apm.dao;

import com.ge.apm.domain.OrgInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface OrgInfoRepository extends GenericRepository<OrgInfo> {
    // @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true")
    // })
    public List<OrgInfo> getByHospitalId(int hospitalId);
    @Query("select o from OrgInfo o where o.uid = ?1")
    OrgInfo getOrgInfoUIDByUid(String uid);
    /*
     * @Query(value =
     * "select t.uid from org_info t where site_uid in (?1) and name LIKE concat('%',?2,'%')"
     * ,nativeQuery = true)
     */
    @Query(value = "select t.uid from org_info t where site_uid in (?1) and name =?2", nativeQuery = true)
    public List<String> getOrgBySite(List<String> orgList, String filterName);

    // @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true")
    // })
    @Query("select t from OrgInfo t where t.siteId=?1 and t.parentOrg is null")
    public List<OrgInfo> getHospitalBySiteId(int siteId);

    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=1 and t.tenantUID=?2")
    public OrgInfo getInstitutionByName(String name, String tenantUID);

    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=2 and t.institutionUID=?2")
    public OrgInfo getHospitalByName(String name, String institutionUID);

    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=3 and t.hospitalUID=?2")
    public OrgInfo getSiteByName(String name, String hospitalUID);

    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=4 and t.siteUID=?2")
    public OrgInfo getDepartmentByName(String name, String siteUID);

    @Query("select t from OrgInfo t where t.name=?1 and t.orgType=4 and t.parentUID=?2")
    public OrgInfo getDepartmentByNameParent(String name, String parentUID);

    @Query("select t from OrgInfo t where t.name in ?1 and t.orgType=4 and t.siteUID=?2")
    public List<OrgInfo> getDepartmentByName(Set<String> name, String siteUID);

    @Query("select t from OrgInfo t where t.name in ?1 and t.orgType=3 and t.hospitalUID=?2")
    public List<OrgInfo> getSiteByName(Set<String> name, String hospitalUID);

    @Query("select t from OrgInfo t where t.name in ?1 and t.orgType=2 and t.institutionUID=?2")
    public List<OrgInfo> getHospitalByName(Set<String> name, String institutionUID);

    @Query("select t from OrgInfo t where t.name in ?1 and t.orgType=1 and t.tenantUID=?2")
    public List<OrgInfo> getInstitutionByName(Set<String> name, String tenantUID);

    @Query(value = "select o.* from org_info o where o.parent_uid=?1", nativeQuery = true)
    public List<OrgInfo> fetchOrgByParentUid(String parentUid);

    // @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true")
    // })
    @Query("select t from OrgInfo t where t.siteId=?1 order by t.orgLevel, t.parentOrg")
    public List<OrgInfo> getFullOrgListBySiteId(int siteId);

    public Page<OrgInfo> getByHospitalId(Pageable pageRequest, int hospitalId);

    public Page<OrgInfo> getBySiteId(Pageable pageRequest, int siteId);

    @Query("select o from OrgInfo o,UserOrgInfo u where o.uid=u.orgUid and u.userUid=?1")
    public List<OrgInfo> getByUser(String userUid);

    @Query("select o from OrgInfo o,ReminderRuleOrg r where o.uid=r.orgUid and r.ruleUid=?1")
    public List<OrgInfo> getByReminderRule(String userUid);

    @Query("select o from OrgInfo o,PermissionGroupOrg p where o.uid=p.orgUid and p.permissionGroupUid in ?1")
    public List<OrgInfo> getOrgListByPermissionGroup(List<String> permissionGroupUidList);

    @Query(value = "select name from org_info where id=?1", nativeQuery = true)
    public String getDepName(int id);

    @Query("select o from OrgInfo o where institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1 order by parentUID,orgIndex,name")
    public List<OrgInfo> getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(String siteId);

    @Query("select o from OrgInfo o where orgType<>4 and (institutionUID=?1 or hospitalUID=?1 or siteUID=?1 or tenantUID=?1)")
    public List<OrgInfo> getOrgInfosByParentId(String siteId);

    @Query(value = "select o.* from org_info o where o.uid in (select c.parent_uid from org_info c where c.org_type=4 and c.uid in (?1)) order by o.name_py_code asc", nativeQuery = true)
    public List<OrgInfo> findParentSiteByUids(List<String> listUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.site_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidBySiteUid(List<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.hospital_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByHospitalUid(List<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.hospital_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByHospitalUid(Set<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.institution_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByInstitutionUid(List<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.institution_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByInstitutionUid(Set<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.tenant_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByTenantUid(List<String> orgUid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.tenant_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidByTenantUid(Set<String> orgUid);

    @Query(value = "select o.* from org_info o where o.uid in ?1 and o.site_uid=?2", nativeQuery = true)
    public List<OrgInfo> findByUidAndSiteUid(List<String> orgUid, String siteUid);

    @Query(value = "select o.* from org_info o where o.uid in ?1 and o.parent_uid=?2", nativeQuery = true)
    public List<OrgInfo> findByUidAndParentUid(List<String> orgUid, String parentUid);

    @Query(value = "select o.uid from org_info o where o.uid in ?1 and o.site_uid=?2", nativeQuery = true)
    public List<String> findUidByUidAndSiteUid(Set<String> orgUid, String siteUid);

    public OrgInfo getByUid(String uid);

    @Query(value = "select o from OrgInfo o where o.uid in ?1")
    public List<OrgInfo> findByUids(Collection<String> idc);

    @Query(value = "select o.* from org_info o  where o.uid in ?1", nativeQuery = true)
    public List<OrgInfo> findInUIds(Set<String> ids);

    public List<OrgInfo> getByTenantUID(String tenantUID);

    @Query("select o from OrgInfo o where orgType<>4 and tenantUID=?1")
    public List<OrgInfo> getSitesByTenantUID(String tenantUID);

    public List<OrgInfo> findByTenantUIDAndOrgType(String tenantUID, Integer orgType);

    public List<OrgInfo> findByParentUIDAndOrgTypeOrderByNameAsc(String parentUID, Integer orgType);

    public List<OrgInfo> findByParentUID(String parentUID);

    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc(String siteUID, Integer orgType);

    @Query(value = "select o.* from org_info o where o.tenant_uid=?1 and o.org_type=4 ", nativeQuery = true)
    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc1(String siteUID);
    @Query(value = "select o.* from org_info o where o.institution_uid=?1 and o.org_type=4", nativeQuery = true)
    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc2(String siteUID);
    @Query(value = "select o.* from org_info o where o.hospital_uid=?1 and o.org_type=4", nativeQuery = true)
    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc3(String siteUID);
    @Query(value = "select o.* from org_info o where o.site_uid=?1 and o.org_type=4", nativeQuery = true)
    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc4(String siteUID);
    @Query(value = "select o.* from org_info o where o.uid=?1 and o.org_type=4", nativeQuery = true)
    public List<OrgInfo> findBySiteUIDAndOrgTypeOrderByNameAsc5(String siteUID);

    @Query(value = "select o.* from org_info o where o.tenant_uid=?1 and o.org_type !=?2 ", nativeQuery = true)
    public List<OrgInfo> getByTenantUIDAndOrgTypeNotEq(String tenantUID,int orgType);

    public List<OrgInfo> findBySiteUIDAndOrgTypeIn(String siteUID, Integer[] orgTypes);

    public OrgInfo findByTenantUIDAndName(String tenantUID, String name);

    OrgInfo findByUid(String uid);

    public List<OrgInfo> findByParentUIDAndOrgType(String parentUID, Integer orgType);

    public List<OrgInfo> findBySiteUIDAndOrgType(String siteUID, Integer orgType);

    public OrgInfo findFirstByTenantUIDAndOrgType(String tenantUid, Integer orgType);

    public OrgInfo findFirstBySiteUIDAndNameAndOrgType(String siteUID, String name, Integer orgType);

    public OrgInfo findFirstByTenantUIDAndOrgTypeAndName(String tenantUID, int orgType, String name);

    public OrgInfo findFirstByParentUIDAndName(String parentUID, String name);

    public OrgInfo findFirstByParentUIDAndShortName(String parentUID, String name);

    public OrgInfo findFirstByParentUIDAndNameAndUidIsNot(String parentUid, String name, String uid);

    public OrgInfo findFirstByTenantUIDAndHospitalUcmid(String tenantUID, String hospitalUcmid);

    public int countByParentUIDAndUidIsNot(String parentUid, String uid);

    public int countByParentUIDAndOrgType(String parentUid, int orgType);

    public OrgInfo findByHospitalUcmid(String ucmid);

    @Query(value = "select o.* from org_info o ", nativeQuery = true)
    public List<OrgInfo> findAllAsList();

    @Query(value = "select new com.ge.apm.domain.OrgInfo(id,uid,name,namePyCode,tenantUID,institutionUID,hospitalUID,siteUID,parentUID,orgLevel,orgType) from OrgInfo", nativeQuery = false)
    public Set<OrgInfo> findSimpleAllAsList();

    @Query(value = "select new com.ge.apm.domain.OrgInfo(id,uid,name,namePyCode,shortName,shortNamePyCode,tenantUID,institutionUID,hospitalUID,siteUID,parentUID,orgLevel,orgType) from OrgInfo where siteUID in ?1 ", nativeQuery = false)
    public Set<OrgInfo> findSimpleAllBySiteUid(Set<String> siteUid);

    @Query(value = "select new com.ge.apm.domain.OrgInfo(id,uid,name,namePyCode,shortName,shortNamePyCode,tenantUID,institutionUID,hospitalUID,siteUID,parentUID,orgLevel,orgType) from OrgInfo where uid = ?1 ", nativeQuery = false)
    public OrgInfo findSimpleByUid(String uid);

    @Query(value = "select o.* from org_info o where o.hospital_uid=?1 and o.org_type=3", nativeQuery = true)
    public List<OrgInfo> getSiteListByHospitalUid(String hospitalUid);

    @Query(value = "select o.* from org_info o where o.site_uid=?1 and o.org_type=4", nativeQuery = true)
    public List<OrgInfo> getDeptListBySiteUid(String siteUid);

    @Query(value = "with recursive cte_site as (\n" +
            "select o.id, o.uid, o.short_name, o.name, o.parent_id, o.parent_uid, o.org_level, o.org_type, o.site_uid, o.hospital_uid, o.institution_uid, o.tenant_uid, o.site_id, cast('' as varchar(1000)) as org_uri\n" +
            "from org_info o\n" +
            "join tenant_info t on o.tenant_uid = t.uid\n" +
            "where o.parent_uid is null and t.uid = ?1 \n" +
            "\n" +
            "union\n" +
            "\n" +
            "select o.id, o.uid, o.short_name, o.name, o.parent_id, o.parent_uid, o.org_level, o.org_type, o.site_uid, o.hospital_uid, o.institution_uid, o.tenant_uid, o.site_id, cast(cte.org_uri || '->' || o.short_name as varchar(1000)) as org_uri\n" +
            "from org_info o\n" +
            "join tenant_info t on o.tenant_uid = t.uid\n" +
            "inner join cte_site cte on o.parent_uid = cte.uid\n" +
            "where t.uid = ?1 \n" +
            ") select  * from cte_site;", nativeQuery = true)
    public List<Object[]> getOrgHierarchyForTenant(String tenantUid);

    @Query(value = "select site_uid from org_info where name=?1 and site_id=?2", nativeQuery = true)
    String getSiteUidByHospitalName(String hospitalName, Integer siteId);

    //only used by apm ib admin, limit result set to 500 institution items
    @Query(value = "select uid from org_info where name like concat('%',?1,'%') and org_type = 1 and site_id = ?2 limit 500", nativeQuery = true)
    List<String> findByInstitutionByNameLike(String name, Integer siteId);

    @Query(value = "with recursive cte_org as (\n" +
            "    select oi.*\n" +
            "    from org_info oi\n" +
            "    where oi.uid in (select uid from org_info where parent_uid= ?1 and name like concat('%',?2,'%'))\n" +
            "    union\n" +
            "    select oi.*\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            ") select * from cte_org union select * from org_info where uid= ?1", nativeQuery = true)
    List<OrgInfo> getInventoryOrgTree(String siteUid, String retrieval);

    //查询到集团限制500条之内的所有树
    @Query(value = "with recursive cte_org as (\n" +
            "    select oi.*\n" +
            "    from org_info oi\n" +
            "    where oi.uid in (select uid from org_info where parent_uid= ?1 and name like concat('%',?2,'%') limit 500)\n" +
            "    union\n" +
            "    select oi.*\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            ") select * from cte_org union select * from org_info where uid= ?1", nativeQuery = true)
    List<OrgInfo> getOrgTreeBySiteAndparm(String siteUid, String retrieval);

    //
    @Query(value = "with recursive cte_org as (\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    where oi.uid = ?1 \n" +
            "    union\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            ") select * from cte_org", nativeQuery = true)
    List<String> getOrgInfoUidsByparentUid(String uid);

    @Query(value = "with recursive cte_org_info as (select sag.* from org_info sag where tenant_uid = ?1 and sag.org_type = 0 union" +
            " select sag.* from org_info sag inner join cte_org_info cte on sag.parent_uid = cte.uid and sag.org_type !=4) select  * from cte_org_info", nativeQuery = true)
    List<OrgInfo> getOrgHierarchyExceptClinicalDeptForTenant(String tenantUid);

    @Query("select o.name from OrgInfo o where o.uid=?1")
    String findNameByUID(String uid);

    @Query(value = "with RECURSIVE cte as (select a.parent_uid,cast(a.name as varchar(500)) from org_info a where a.uid= ?1 " +
            "union all select k.parent_uid,cast(k.name||' '||c.name as varchar(500)) as name " +
            "from cte  c inner join org_info k on c.parent_uid = k.uid)select name from cte where  parent_uid is null", nativeQuery = true)
    String listOrgInfoNamePathInHierarchy(String uid);

    @Query(value = "with RECURSIVE cte as (select a.parent_uid,cast(a.name as varchar(500)) from org_info a where a.uid= ?1 " +
            "union all select k.parent_uid,cast(k.name||'/'||c.name as varchar(500)) as name " +
            "from cte  c inner join org_info k on c.parent_uid = k.uid)select name from cte where  parent_uid is null", nativeQuery = true)
    String listOrgInfoNamePathInHierarchyWithSlash(String uid);

    //根据tenant_uid找出所有的集团
    @Query(value = "select * from org_info where parent_uid=?1 ", nativeQuery = true)
    public List<OrgInfo> getHospitalByTenantUIDAndType(String tenantUID);

    @Query(value = "select * from org_info where uid=?1", nativeQuery = true)
    public OrgInfo getOrgInfoByUid(String tenantUID);

    //根据UID向下递归出所有的子集
    @Query(value = "with recursive cte_org as (" +
            "    select oi.*" +
            "    from org_info oi" +
            "    where oi.uid in (?1)" +
            "    union" +
            "    select oi.*" +
            "    from org_info oi" +
            "    join cte_org cte on oi.parent_uid = cte.uid" +
            ") select * from cte_org", nativeQuery = true)
    public List<OrgInfo> getHospitalChildrenByUids(List<String> uids);

    @Query(value = "select *from org_info o where o.hospital_uid=?1  and o.org_type = 2 order by random() LIMIT 1", nativeQuery = true)
    public OrgInfo findOrgCode(String hospitalUid);

    @Query(value = "with recursive cte_org_info as (\n" +
            "    select oi.*\n" +
            "    from org_info oi join user_org uo on uo.org_uid = oi.uid\n" +
            "    where uo.user_uid = :userUid \n" +
            "    union\n" +
            "    select oi.*\n" +
            "    from org_info oi\n" +
            "    inner join cte_org_info cte\n" +
            "    on oi.parent_uid = cte.uid\n" +
            "), cte_clinical_org as (\n" +
            "    select oi.*\n" +
            "    from org_info oi where oi.org_type = 4 and oi.site_uid = :siteUid \n" +
            ")\n" +
            "select * from cte_org_info where org_type = 4\n" +
            "intersect\n" +
            "select * from cte_clinical_org", nativeQuery = true)
    public List<OrgInfo> getUserAccessibleClinicalDeptsBySelectedSite(@Param("siteUid") String siteUid, @Param("userUid") String userUid);

    @Query(value = "select o.name from org_info o where o.uid in (select c.parent_uid from org_info c where  c.uid =  ?1)", nativeQuery = true)
    String findParentNameByUid(String uid);

    @Query(value = "select o.uid from org_info o where o.org_type=4 and o.site_uid in (?1) ", nativeQuery = true)
    public List<String> findClinicalUidBySiteUid(Set<String> orgUid);

//    @Query(value = "select * from org_info  where name= ?1 and site_uid =?2 ", nativeQuery = true)
     OrgInfo findByNameAndSiteUID(String Name, String siteUID);

    @Query(value = "select o.* from org_info o where o.tenant_uid = ?1 and o.uid = o.site_uid " +
            "and o.uid != o.hospital_uid and o.uid != o.institution_uid and o.uid != o.tenant_uid", nativeQuery = true)
    List<OrgInfo> findAllSiteByTenantUid(String tenantUid);

    List<OrgInfo> findByTenantUID(String tenantUID);

//    @Query(nativeQuery = true, value = "select o from org_info o where o.site_uid = ?1 and o.name_py_code= ?2")
    OrgInfo findBySiteUIDAndNamePyCode(String siteUID,String name);

    @Query(value = "with recursive cte_org as (\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    where oi.uid = ?1 \n" +
            "    union all\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            "    where oi.org_type < 4\n" +
            ") select * from cte_org", nativeQuery = true)
    List<String> getSiteOrgUidsByParentUid(String uid);

    @Query(value = "select o.uid from org_info o where (o.tenant_uid in (?1) or o.institution_uid in (?1) or o.hospital_uid in (?1) or o.site_uid in (?1)) and o.org_type=4",nativeQuery = true)
    Set<String> findClinicalDeptOrgUidListByOrgUid(Set<String> orgUid);

    @Query(value = "select o.id from org_info o where o.uid in (?1)",nativeQuery = true)
    Set<Integer> findIdListByUid(Set<String> orgUid);

    public OrgInfo findFirstByTenantUIDAndOrgCode(String tenantUID, String orgCode);


    @Query(nativeQuery = true, value = "select  distinct o.uid from org_info o , user_org uo " +
            " where uo.org_uid in (o.tenant_uid, o.institution_uid,o.hospital_uid,o.site_uid, o.uid) and uo.user_uid = ? and o.org_type = 4 ")
    List<String> getUserDeptScope(String userUID);

    @Query(nativeQuery = true, value = "select  distinct o.uid from org_info o, user_org uo, asset_info ai " +
            " where uo.user_uid = ?1 and uo.org_uid in (o.tenant_uid, o.institution_uid,o.hospital_uid,o.site_uid, o.uid) and ai.clinical_dept_uid in (o.tenant_uid, o.institution_uid,o.hospital_uid,o.site_uid, o.uid)" +
            " and o.org_type = 4 and ai.is_checked = true and ai.is_deleted = false and ai.is_valid = true and ai.asset_type in (?2) ")
    List<String> getUserDeptScopeByAssetType(String userUID, Collection<String> assetType);

    List<OrgInfo> findByIdIn(Collection<Integer> ids);


    @Query("select t from OrgInfo t where   t.institutionUID=?1  and t.orgType=4 ")
    public List<OrgInfo> getInstitutionUIDAndOrgType(String institutionUID);

}
