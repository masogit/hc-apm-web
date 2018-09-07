package com.ge.apm.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserInfo;

import webapp.framework.dao.GenericRepository;

public interface UserAccountRepository extends GenericRepository<UserAccount> {
    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getByLoginName(String loginName);

    //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    UserAccount getById(int userId);
    
    UserAccount getByUid(String userUid);

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
    public List<UserAccount> getSimpleByUids(Set<String> setUids);

    //当前租户下的临时报修人
    //@Query(value= "select * from user_account where id > 0 and tenant_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and  r.role_id=3 and tenant_uid=?1",nativeQuery=true)
    public List<UserAccount> getByTenantUIDStaff(String tenantUID);

    //获取关注人--当前租户下除了Guest ClincialStaff Activation..角色以外的其它人 都可以成为关注人
    @Query(value= "select * from user_account u join user_role r on u.id=r.user_id  left join sys_role sr on sr.id = r.role_Id  where u.id > 0 and  (sr.name !='Guest' or sr.name !='ClinicalStaff' or sr.name !='ActivationPerson') and tenant_uid=?1",nativeQuery=true)
    public List<UserAccount> getByTenantUID(String tenantUID);
    
    public List<UserAccount> getByOrgInfoUID(String orgUID);

    public Page<UserAccount> getByTenantUID(Pageable pageRequest,String tenantUID);
    public List<UserAccount> findBySiteUID(String id);


    @Query(value="select distinct u from UserAccount u, UserOrgInfo o, UserRole ur where u.uid=o.userUid and u.id=ur.userId and ur.roleId=3 and o.orgUid in ?1")
    public List<UserAccount> findByAssetAccess(List<String> uid);

    @Query("select u from UserAccount u where u.orgInfoUID=?1 and u.id not in (select r.userId from UserRole r where r.roleId=9)")
    List<UserAccount> findUsersByOrgIdExceptTemporaryAccount(String orgInfoUID);

    //subscirber with tag=-999 under  this assetInfo.site_UID
    @Query(value="select ua.* from asset_tag at,asset_tag_biomed_group atbg,biomed_group bg,biomed_group_user bgu,user_account ua where at.id=-999  and at.id=atbg.tag_id and atbg.biomed_group_id=bg.id and bgu.group_id=bg.id and bgu.user_id=ua.id and ua.site_uid=?1",nativeQuery=true)
    List<UserAccount> getResponserwithTag999(String siteUID);

    @Query("select u from UserAccount u, UserRole r where u.tenantUID=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> findByTenantUIDAndStaffRole(String tenantUID);

    @Query("select u from UserAccount u, UserRole r where u.institutionUID=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> findByInstitutionUIDAndStaffRole(String institutionUID);

    @Query("select u from UserAccount u, UserRole r where u.hospitalUID=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> findByHospitalUIDAndStaffRole(String hospitalUID);

    @Query("select u from UserAccount u, UserRole r where u.siteUID=?1 and u.id=r.userId and r.roleId=3")
    List<UserAccount> findBySiteUIDAndStaffRole(String siteUID);
    
    @Query(value = "select distinct u from UserAccount u where u.tenantUID = ?1 and u.id in (select r.userId from UserRole r where r.roleId=2)")
    List<UserAccount> getAssetHeadList(String tenantUID);
    
    @Query(value = "select u from UserAccount u, UserOrgInfo o where  u.uid=o.userUid and o.orgUid in ?1")
    List<UserAccount> getByClinicalDept(Collection<String> orgList);
    
    List<UserAccount> findByOrgInfoUID(String orgUid);

    UserAccount findFirstByTenantUIDAndName(String tenantUid, String name);

    List<UserAccount> findByTenantUIDAndName(String tenantUid, String name);

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
    @Query(value= "update user_account set active_time= now(),is_active=true where id = ?1",nativeQuery=true)
    public void activeSingleUser(Integer userId);


    @Transactional
    @Modifying
    @Query(value= "update user_account set active_time= null,is_active=false where id = ?1",nativeQuery=true)
    public void deactiveTemporaryUser(Integer userId);


    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and site_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffBySiteUID(String siteUID);

    //    @Query(value= "select * from user_account where id > 0 and hospital_uid=?1 and id not in(select user_id from user_role where role_id=9)",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and hospital_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByHospitalUID(String hospitalUID);

    //@Query(value= "select * from user_account where id > 0 and institution_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and (r.role_id=3 or r.role_id=2) and institution_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByInstitutionUID(String InsitiUID);

    //当前租户下的肥临时报修人
    //@Query(value= "select * from user_account where id > 0 and tenant_uid=?1 and id not in(select user_id from user_role where role_id=9) ",nativeQuery=true)
    @Query(value= "select DISTINCT u.* from user_account u join user_role r on u.id=r.user_id where u.id > 0 and  (r.role_id=3 or r.role_id=2) and tenant_uid=?1 ",nativeQuery=true)
    public List<UserAccount> getAssetHeadAndStaffByTenantUID(String tenantUID);
    
    @Query(value = "select o.uid from user_account o where o.org_uid in ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUids(List<String> listUid);

    @Query("select u from UserAccount u, UserRole r, SysRole s where u.tenantUID=?1 and u.id=r.userId and r.roleId=s.id and s.name=?2")
    List<UserAccount> findByTenantUIDAndRoleName(String tenantUID, String roleName);

    @Query(value= "select exists(select 1 from user_account where tenant_uid = ?1)",nativeQuery = true)
    boolean findRecordByTenantUid(String uid);

}
