package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.UserOrgInfo;

import webapp.framework.dao.GenericRepositoryUUID;

public interface UserOrgInfoRepository extends GenericRepositoryUUID<UserOrgInfo> {

    List<UserOrgInfo> findByUserUid(String userUid);
    
    @Query(value = "select o.user_uid from user_org o where o.org_uid in ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUids(List<String> listUid);

    UserOrgInfo findFirstByUserUidAndOrgUid(String userUid, String orgUid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where user_uid = ?1", nativeQuery = true)
    void deleteByUserUid(String userUid);

}
