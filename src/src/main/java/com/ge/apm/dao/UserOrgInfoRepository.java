package com.ge.apm.dao;

import com.ge.apm.domain.UserOrgInfo;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

public interface UserOrgInfoRepository extends GenericRepositoryUUID<UserOrgInfo> {

    List<UserOrgInfo> findByUserUid(String userUid);

    UserOrgInfo findFirstByUserUidAndOrgUid(String userUid, String orgUid);

    @Transactional
    @Modifying
    @Query(value = "delete from user_org where user_uid = ?1", nativeQuery = true)
    void deleteByUserUid(String userUid);

}
