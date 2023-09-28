package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import com.ge.apm.domain.UserAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.UserOrgInfo;

import webapp.framework.dao.GenericRepositoryUUID;

public interface UserOrgInfoRepository extends GenericRepositoryUUID<UserOrgInfo> {

    List<UserOrgInfo> findByUserUid(String userUid);
    
    @Query(value = "select o.user_uid from user_org o where o.org_uid in ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUids(List<String> listUid);

    @Query(value = "select o.user_uid from user_org o where o.org_uid = ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUid(String orgUid);
    
    @Query(value = "select o.* from user_org o where o.user_uid = ?1",nativeQuery = true)
    public List<UserOrgInfo> findOrgUidListByUserUid(String userUid);

    @Query(value = "select oi.uid from user_org o, org_info oi where o.user_uid =?1 " +
            "and o.org_uid in (oi.tenant_uid,oi.institution_uid,oi.hospital_uid,oi.site_uid,oi.uid) and oi.org_type =4 ;",nativeQuery = true)
    public List<String> findAccessUidByUserUid(String userUid);

    @Query(value = "select o.* from user_org o where o.user_uid in ?1",nativeQuery = true)
    public List<UserOrgInfo> findOrgUidListByUserUid(Set<String> userUid);

    UserOrgInfo findFirstByUserUidAndOrgUid(String userUid, String orgUid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where user_uid = ?1", nativeQuery = true)
    void deleteByUserUid(String userUid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where org_uid = ?1", nativeQuery = true)
    void deleteByOrgUId(String OrgUid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where user_uid= ?1 and org_uid in (\n" +
            "with recursive cte_org as (\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    where oi.institution_uid in (?2)\n" +
            "    union\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            ") select * from cte_org)", nativeQuery = true)
    void deleteByUserUid46(String userUid,List<String> orgUids);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where user_uid= ?1 and org_uid in (\n" +
            "with recursive cte_org as (\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    where oi.uid in (?2)\n" +
            "    union\n" +
            "    select oi.uid\n" +
            "    from org_info oi\n" +
            "    join cte_org cte on oi.parent_uid = cte.uid\n" +
            ") select * from cte_org)", nativeQuery = true)
    void deleteByUserUids46(String userUid,List<String> orgUids);

}
