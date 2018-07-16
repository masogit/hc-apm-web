package com.ge.apm.dao;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserOrgInfo;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import webapp.framework.dao.GenericRepositoryUUID;

public interface UserOrgInfoRepository extends GenericRepositoryUUID<UserOrgInfo> {

    List<UserOrgInfo> findByUserUid(String userUid);
    
    @Query(value = "select o.user_uid from user_org o where o.org_uid in ?1",nativeQuery = true)
    public List<String> findUserUidListByOrgUids(List<String> listUid);

}
