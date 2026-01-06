package com.ge.apm.dao;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserAccountRepository extends GenericRepository<UserAccount> {

    @Query(nativeQuery = true, value = "with recursive t(uid) as ((select uid from org_info where uid in (select org_uid from user_org uo where user_uid = ?1)) union all (select o.uid from org_info o join t on o.parent_uid = t.uid)) select uid from t")
    public List<String> getAccessScope(String userUID);
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getByLoginName(String loginName);
    List<UserAccount> getByLoginNameIn(Collection<String> loginNames);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getById(int userId);
    UserAccount findByTelephone(String telephone);

    UserAccount getByUid(String userUid);

    @Query(value = "select ua.* from user_account ua where ua.uid=?1 and (ua.is_disable =false or ua.is_disable is null )",nativeQuery = true)
    UserAccount getUsers(String userUid);
    @Query(value = "select ua.* from user_account ua where ua.uid in ?1 and (ua.is_disable =false or ua.is_disable is null ) and (ua.is_deleted =false or ua.is_deleted is null )  ",nativeQuery = true)
    List<UserAccount> getUserList(List<String> userUid);
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    List<UserAccount> getByHospitalId(int hospitalId);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    @Query("select u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> getUsersWithAssetStaffRole(int hospitalId);

    @Query("select u from UserAccount u, UserRole r where u.siteId=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> getUsersWithAssetStaffRoleBySiteId(int siteId);

    @Query("select distinct u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId in (2,3)")
    List<UserAccount> getUsersWithAssetHeadOrStaffRole(int hospitalId);

    @Query("SELECT distinct u FROM UserAccount u,UserRole ur where u.id=ur.userId and ((u.siteId=?1 ) or (u.hospitalId=?2 and ur.roleId=3 ))")
    List<UserAccount> getAssetOnwers(int siteId,int hospitalId);

    @Query("SELECT distinct u FROM UserAccount u,UserRole ur where u.id=ur.userId and ((ur.roleId='2' and u.siteId=?1 ) or (u.hospitalId=?2 and ur.roleId=2 ))")
    List<UserAccount> getAssetHead(int siteId,int hospitalId);
    
    @Query("select u from UserAccount u, UserRole r where u.hospitalId=?1 and u.id=r.userId and r.roleId=8")
    List<UserAccount> getUsersWithWorkOrderDispatcherRole(int hospitalId);

    List<UserAccount> getBySiteId(int siteId);

    Page<UserAccount> getBySiteId(Pageable pageRequest, int siteId);
    Page<UserAccount> getByOrgInfoId(Pageable pageRequest, int orgInfoId);
    @Query("select u from UserAccount u where u.tenantUID=?1 or u.institutionUID=?1 or u.hospitalUID=?1 or u.siteUID=?1 or u.orgInfoUID=?1")
    Page<UserAccount> getByOrgInfoUID(Pageable pageRequest, String getByOrgInfoUID);
    UserAccount getByWeChatId(String weChatId);
    List<UserAccount> getByOrgInfoId(int orgInfoId);

    @Query(value="select ua.* from user_account ua right join (select user_id from biomed_group_user where group_id in (select biomed_group_id from asset_tag_biomed_group where tag_id in (select tag_id from asset_tag_rule where asset_id = ?1)) ) tb1 on ua.id = tb1.user_id",nativeQuery = true)
    List<UserAccount> getResponserByAssetId(Integer assetId);

    /*select ua.* from asset_tag_rule atr,asset_tag ast,asset_tag_biomed_group atbg, biomed_group bg,biomed_group_user bgu, user_account ua where atr.asset_id=23 and atr.tag_id = ast.id and ast.id= atbg.tag_id and atbg.biomed_group_id =bg.id and bg.id=bgu.group_id and bgu.user_id = ua.id -- same as below*/
    @Query(value="select ua.* from user_account ua where id in (select asset_owner_id from asset_info where id = ?1 union select asset_owner_id2 from asset_info where id = ?1 union select asset_owner_id3 from asset_info where id = ?1)",nativeQuery = true)
    List<UserAccount> getResponserWithAsset(Integer assetId);

    //如果没有对某资产配置过对应的医院工，那么就通过标签中的siteId获取默认的医工
    @Query(value="select ua.* from user_account ua, asset_info ai, user_role r where ai.id = ?1 and ai.site_id = ua.site_id and ua.id = r.user_id and r.role_id = 3",nativeQuery=true)
    List<UserAccount> getDefaultUsers(int assetId);
	List<UserInfo> findByInstitutionUID(String institutionUID);

    /* sysrole.id=2*/
//    @Query(value= "select * from user_account where id > 0 and site_uid=?1 and id not in(select user_id from user_role where role_id=9)",nativeQuery=true)
	 @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and r.role_id=3 and site_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getBySiteUID(String siteUID);
    
//    @Query(value= "select * from user_account where id > 0 and hospital_uid=?1 and id not in(select user_id from user_role where role_id=9)",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and r.role_id=3 and hospital_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getByHospitalUID(String hospitalUID);
    
    //@Query(value= "select * from user_account where id > 0 and institution_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and r.role_id=3 and institution_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getByInstitutionUID(String InsitiUID);
    
    @Query(value= "select u.* from user_account u where u.uid in ?1 ",nativeQuery=true)
    public List<UserAccount> getByUids(Set<String> setUids);
    
    @Query(value= "select new com.ge.apm.domain.UserAccount(name,uid) from UserAccount where uid in ?1 ")
    public List<UserAccount> getSimpleByUids(Collection<String> setUids);
    
    @Query(value= "select new com.ge.apm.domain.UserAccount(name,uid) from UserAccount where uid = ?1 ")
    public UserAccount getSimpleByUids(String uid);
    
    @Query(value= "select o.* from user_account o where (o.is_disable =false or o.is_disable is null ) and o.id in ?1 ",nativeQuery=true)
    public List<UserAccount> getByIds(Set<Integer> setIds);

    //当前租户下的临时报修人
    //@Query(value= "select * from user_account where id > 0 and tenant_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and  r.role_id=3 and tenant_uid=?1",nativeQuery=true)
    public List<UserAccount> getByTenantUIDStaff(String tenantUID);

    //获取关注人--当前租户下除了Guest ClincialStaff Activation..角色以外的其它人 都可以成为关注人
    @Query(value= "select * from user_account u join user_role r on u.id=r.user_id  left join sys_role sr on sr.id = r.role_Id  where u.id > 0 and  (sr.name !='Guest' or sr.name !='ClinicalStaff' or sr.name !='ActivationPerson') and tenant_uid=?1 and (u.is_disable =false or u.is_disable is null )   and (u.is_deleted =false or u.is_deleted is null )   ",nativeQuery=true)
    public List<UserAccount> getByTenantUID(String tenantUID);
    
    public List<UserAccount> getByOrgInfoUID(String orgUID);

    @Query(value= "select * from user_account u join user_role r on u.id = r.user_id left join sys_role sr on sr.id = r.role_Id where u.org_uid = ?1 and sr.name = ?2 and (u.is_disable = false or u.is_disable is null) and (u.is_deleted = false or u.is_deleted is null )",nativeQuery=true)
    public List<UserAccount> getUsersByDeptUidAndRole(String orgUid, String roleName);

    @Query(value= "select * from user_account u join user_role r on u.id = r.user_id left join sys_role sr on sr.id = r.role_Id where u.site_uid = ?1 and sr.name = ?2 and (u.is_disable = false or u.is_disable is null) and (u.is_deleted = false or u.is_deleted is null )",nativeQuery=true)
    public List<UserAccount> getUsersBySiteUidAndRole(String orgUid, String roleName);

    public Page<UserAccount> getByTenantUID(Pageable pageRequest,String tenantUID);
    public List<UserAccount> findBySiteUID(String id);


    @Query(value="select distinct u from UserAccount u, UserOrgInfo o, UserRole ur where u.uid=o.userUid and u.id=ur.userId and ur.roleId=3 and o.orgUid in ?1")
    public List<UserAccount> findByAssetAccess(List<String> uid);

    @Query("select u from UserAccount u where u.orgInfoUID=?1  and (u.isDisable =false or u.isDisable is null ) and not exists (select r.userId from UserRole r where r.roleId=9 and r.userId = u.id)")
    List<UserAccount> findUsersByOrgIdExceptTemporaryAccount(String orgInfoUID);

    //subscirber with tag=-999 under  this assetInfo.site_UID
    @Query(value="select ua.* from asset_tag at,asset_tag_biomed_group atbg,biomed_group bg,biomed_group_user bgu,user_account ua where at.id=-999  and at.id=atbg.tag_id and atbg.biomed_group_id=bg.id and bgu.group_id=bg.id and bgu.user_id=ua.id and ua.site_uid=?1",nativeQuery=true)
    List<UserAccount> getResponserwithTag999(String siteUID);

    @Query("select u from UserAccount u, UserRole r where u.tenantUID=?1 and u.id=r.userId and r.roleId=3 and (u.isDisable =false or u.isDisable is null ) and (u.isDeleted =false or u.isDeleted is null )    ")
    List<UserAccount> findByTenantUIDAndStaffRole(String tenantUID);

    @Query("select u from UserAccount u, UserRole r where u.institutionUID=?1 and u.id=r.userId and r.roleId=3 and (u.isDisable =false or u.isDisable is null ) and (u.isDeleted =false or u.isDeleted is null )    ")
    List<UserAccount> findByInstitutionUIDAndStaffRole(String institutionUID);

    @Query("select u from UserAccount u, UserRole r where u.hospitalUID=?1 and u.id=r.userId and r.roleId=3 and (u.isDisable =false or u.isDisable is null ) and (u.isDeleted =false or u.isDeleted is null )   ")
    List<UserAccount> findByHospitalUIDAndStaffRole(String hospitalUID);

    @Query("select u from UserAccount u, UserRole r where u.siteUID=?1 and u.id=r.userId and r.roleId=3 and (u.isDisable =false or u.isDisable is null ) and (u.isDeleted =false or u.isDeleted is null ) ")
    List<UserAccount> findBySiteUIDAndStaffRole(String siteUID);
    
    @Query(value = "select distinct u from UserAccount u where u.tenantUID = ?1 and u.id in (select r.userId from UserRole r where r.roleId=2)")
    List<UserAccount> getAssetHeadList(String tenantUID);
    
    @Query(value = "select u from UserAccount u, UserOrgInfo o where  u.uid=o.userUid and o.orgUid in ?1")
    List<UserAccount> getByClinicalDept(Collection<String> orgList);

    @Query(value= "select o.* from user_account o where (o.is_disable =false or o.is_disable is null ) and o.org_uid in ?1 ",nativeQuery=true)
    List<UserAccount> findByOrgInfoUIDIn(Collection<String> orgUid);

    UserAccount findFirstByTenantUIDAndName(String tenantUid, String name);

    @Query(value = "select  u from UserAccount u where u.tenantUID = ?1 and u.name like concat('%',?2,'%') ")
    List<UserAccount> findByTenantUIDAndNameList(String tenantUid, String name);

    List<UserAccount> findByTenantUIDAndName(String tenantUid, String name);

    List<UserAccount> findByTenantUID(String tenantUid);

    @Transactional
    @Modifying
    @Query(value= "update user_account set active_time= now(),is_active=true where tenant_uid = ?1",nativeQuery=true)
    public void updateMembersStatus(String tenantUid);

    @Transactional
    @Modifying
    @Query(value= "update user_account set active_time = null,is_active = false where tenant_uid = ?1 and id not in(select user_id from user_role where role_id = 9)",nativeQuery=true)
    public void deactiveTenantMembersStatus(String tenantUid);


    @Transactional
    @Modifying
    @Query(value= "update user_account set is_deleted = true, wechat_id = null, wechat_nickname = null, wx_union_id = null, ma_ib_open_id = null where tenant_uid = ?1 ",nativeQuery=true)
    public void deleteTenantUser(String tenantUid);


    @Transactional
    @Modifying
    @Query(value= "update user_account set active_time= now(),is_active=true where id = ?1",nativeQuery=true)
    public void activeSingleUser(Integer userId);

    @Query(value="select * from user_account where  hospital_uid = ?2 and id in ?1 and (is_disable =false or is_disable is null )",nativeQuery = true)
    List<UserAccount> getUserAccounts(List<Integer> id,String hospitalUID );

    @Transactional
    @Modifying
    @Query(value= "update user_account set active_time= null,is_active=false where id = ?1",nativeQuery=true)
    public void deactiveTemporaryUser(Integer userId);


    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and site_uid=?1 and (u.is_disable =false or u.is_disable is null ) and (u.is_deleted =false or u.is_deleted is null )    ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffBySiteUID(String siteUID);

    //    @Query(value= "select * from user_account where id > 0 and hospital_uid=?1 and id not in(select user_id from user_role where role_id=9)",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and hospital_uid=?1 and (u.is_disable =false or u.is_disable is null ) and (u.is_deleted =false or u.is_deleted is null )   ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByHospitalUID(String hospitalUID);

    //@Query(value= "select * from user_account where id > 0 and institution_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and institution_uid=?1 and (u.is_disable =false or u.is_disable is null ) and (u.is_deleted =false or u.is_deleted is null )   ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByInstitutionUID(String InsitiUID);

    //当前租户下的肥临时报修人
    //@Query(value= "select * from user_account where id > 0 and tenant_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select DISTINCT u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and  (r.role_id=3 or r.role_id=2) and tenant_uid=?1 and (u.is_disable =false or u.is_disable is null ) and (u.is_deleted =false or u.is_deleted is null )  ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByTenantUID(String tenantUID);
    
    @Query(value = "select o.uid from user_account o where o.org_uid in ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUids(List<String> listUid);

    @Query("select u from UserAccount u, UserRole r, SysRole s where u.tenantUID=?1 and u.id=r.userId and r.roleId=s.id and s.name=?2")
    List<UserAccount> findByTenantUIDAndRoleName(String tenantUID, String roleName);

    @Query("select distinct u from UserAccount u, UserRole r, SysRole s where u.tenantUID=?1 and u.id=r.userId and r.roleId=s.id and s.name in ?2 and (u.isDisable =false or u.isDisable is null) and (u.isDeleted =false or u.isDeleted is null)")
    List<UserAccount> findByTenantUIDAndRoleNames(String tenantUID, List<String> roleName);

    @Query("select distinct u from UserAccount u, UserRole r where u.id=r.userId and r.roleId in ?1 and u.orgInfoUID in ?2 and (u.isDisable =false or u.isDisable is null) and (u.isDeleted =false or u.isDeleted is null)")
    List<UserAccount> findBySiteUidAndRoleNames(List<Integer> roleId, List<String> detpUid);

    @Query(value= "select exists(select 1 from user_account where tenant_uid = ?1)",nativeQuery = true)
    boolean findRecordByTenantUid(String uid);

    @Query("select u from UserAccount u, UserRole r where u.tenantUID=?1 and u.id=r.userId and r.roleId=9")
    List<UserAccount> findByTenantUIDAndTempStaffRole(String tenantUID);

    List<UserAccount> findByUidIn(Collection<String> userListUid);

    int deleteUserAccountsByTenantUID(String tenantUid);
    int countUserAccountsByTenantUID(String tenantUid);

    @Query("select u.name from UserAccount u where u.loginName = ?1")
    String getNameByLoginName(String loginName);

    /**
     * @param roleList
     * @return java.util.List<com.ge.apm.domain.UserAccount>
     * @date 2020/7/14 15:58
     * 获取角色列表
     */
    @Query(value = "select u from UserAccount u where u.id in (select uu.userId from UserRole uu where uu.roleId in (:roleList))")
    List<UserAccount> getApprovalPersonByRoleId(@Param(value = "roleList") List<Integer> roleList);


    /**
     * 根据租户tenant UID查询
     * @param tenantUid 租户tenantUID
     * @return
     */
    @Query(value = "SELECT ua.* from user_account ua INNER JOIN user_role u on ua.id= u.user_id " +
            "INNER JOIN tenant_info t1 on ua.tenant_uid=t1.uid " +
            "where ua.tenant_uid = ?1 " +
            "and u.role_id = 3 " +
            "and t1.type=3", nativeQuery = true)
    List<UserAccount> getTenantUidAndRoleId(String tenantUid);

    /**
     * @param userList
     * @return java.util.List<com.ge.apm.domain.UserAccount>
     * @date 2020/7/14 15:58
     * 获取角色列表
     */
    @Query(value = "select u from UserAccount u where u.uid in (:userList)")
    List<UserAccount> getApprovalPersonByUserId(@Param(value = "userList") List<String> userList);

    @Transactional
    @Modifying
    @Query(value = "delete from user_account where uid = ?1", nativeQuery = true)
    int deleteByUid(String uid);

    UserAccount findByLoginName(String loginName);

    @Query(value= "SELECT sys_role.NAME FROM user_role INNER JOIN user_account ON user_role.user_id = user_account.id INNER JOIN sys_role ON user_role.role_id = sys_role.id WHERE user_account.ID =?1",nativeQuery=true)
    List<String> findUsname(Integer id);

    @Query(value="select ua from UserAccount ua where ua.id in (select ur.userId from UserRole ur where " +
            "ur.roleType = 1 and ur.roleId in (select cr.id from CustomRole cr where cr.id in (?1)))")
    List<UserAccount> findCustomRoleUser(List<Integer> customRoleId);



    @Query(value = "WITH recursive t AS( ( SELECT * FROM org_info WHERE uid = ?1) UNION ALL ( SELECT org_info.* FROM org_info JOIN t ON t.parent_uid = org_info.uid ) ) SELECT ua.* FROM t LEFT JOIN user_org uo ON uo.org_uid = t.uid LEFT JOIN user_account ua ON uo.user_uid = ua.uid LEFT JOIN user_role ur ON ua.id = ur.user_id LEFT JOIN sys_role sr ON ur.role_id = sr.id WHERE uo.id IS NOT NULL AND sr.NAME =?2 group by ua.id ", nativeQuery = true)
    List<UserAccount> findUserByClinicalDeptUID(String clinicalDeptUID,String roleName);

    @Query(value="select ua.* from user_role ur inner join user_account ua on ur.user_id = ua.id where  ua.org_uid = ?1 and ur.role_id = ?2",nativeQuery=true)
    public List<UserAccount> findOrgUidByRoleId(String tenantUid ,int roleId);


    @Query(value = "WITH recursive t AS( ( SELECT * FROM org_info WHERE uid = ?1) UNION ALL ( SELECT org_info.* FROM org_info JOIN t ON t.parent_uid = org_info.uid ) ) SELECT ua.* FROM t LEFT JOIN user_org uo ON uo.org_uid = t.uid LEFT JOIN user_account ua ON uo.user_uid = ua.uid  where ua.id is not null ", nativeQuery = true)
    List<UserAccount> findByClinicalDeptUID(String clinicalDeptUID);

    UserAccount findByUid(String patrolInspectionOwnerUID);



    @Query(value = "select * from user_account where tenant_uid=?1 and name=?2 and (is_deleted = false or is_deleted is null) ", nativeQuery = true)
    List<UserAccount> findByTenantUIDAndNameAndValid(String tenantUID, String assetOwnerName);


    @Query(value="select distinct u from UserAccount u, UserOrgInfo o, UserRole ur where u.uid=o.userUid and u.id=ur.userId and ur.roleId=?2 and o.orgUid in ?1")
    public List<UserAccount> findByUserOrgAndRoleId(List<String> uid,int roleId);




//    @Query(value = "WITH RECURSIVE org_hierarchy AS ( SELECT * FROM org_info WHERE uid = ?1 UNION ALL SELECT o.* FROM org_info o JOIN org_hierarchy h ON o.uid = h.parent_uid ) SELECT DISTINCT ua.* FROM org_hierarchy oh JOIN user_org uo ON uo.org_uid = oh.uid JOIN user_account ua ON uo.user_uid = ua.uid WHERE ua.id IN ( SELECT ur.user_id FROM user_role ur JOIN sys_role sr ON ur.role_id = sr.id WHERE sr.NAME IN ?2 GROUP BY ur.user_id HAVING COUNT(DISTINCT sr.NAME) = ?3 );", nativeQuery = true)
//    List<UserAccount> findByClinicalDeptUidAndRoleNames(String clinicalDeptUID,List<String> roleName ,int size);




    @Query(value = "SELECT ua.uid FROM user_account ua WHERE ua.org_uid = ?1 AND EXISTS ( SELECT 1 FROM user_role ur1 JOIN sys_role sr1 ON ur1.role_id = sr1.id and ur1.role_type=0  WHERE ur1.user_id = ua.id AND sr1.name in (?2) ) AND EXISTS ( SELECT 1 FROM user_role ur2 JOIN sys_role sr2 ON ur2.role_id = sr2.id WHERE ur2.user_id = ua.id AND sr2.name = 'MultiLevelAuditor' )", nativeQuery = true)
    List<String> findByClinicalDeptUidAndRoleNames(String clinicalDeptUID,List<String> roleNames );
    @Query(value = "SELECT ua.uid FROM user_account ua WHERE ua.org_uid = ?1 AND EXISTS ( SELECT 1 FROM user_role ur1 JOIN custom_role sr1 ON   ur1.role_id = sr1.id  and ur1.role_type=1  WHERE ur1.user_id = ua.id AND sr1.name in (?2) ) AND EXISTS ( SELECT 1 FROM user_role ur2 JOIN sys_role sr2 ON ur2.role_id = sr2.id WHERE ur2.user_id = ua.id AND sr2.name = 'MultiLevelAuditor' )", nativeQuery = true)
    List<String> findByClinicalDeptUidAndCustomRoles(String clinicalDeptUID,List<String> roleNames );



    @Query("select u from UserAccount u, UserRole r, SysRole s where u.siteUID=?1 and u.id=r.userId and r.roleId=s.id and s.name=?2")
    List<UserAccount> findBySiteUidAndRoleName(String siteUid, String roleName);

    @Query(value = "select distinct ua.* from user_account ua " +
            "inner join zkb_train_course_quota zt on ua.tenant_uid  = zt.tenant_uid " +
            "where ua.id in(select user_id from user_role ur where ur.role_id = 5) " +
            "and zt.start_date <= now() and zt.end_date >= now() " +
            "and zt.course_id = ?1", nativeQuery = true)
    List<UserAccount> findByZkbCourseId(String courseId);

    List<UserAccount> findByNameContains(String name);
}
