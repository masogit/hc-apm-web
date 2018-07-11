package com.ge.apm.dao;

import com.ge.apm.domain.UserOrgInfo;
import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;

public interface UserOrgInfoRepository extends GenericRepositoryUUID<UserOrgInfo> {

    List<UserOrgInfo> findByUserUid(String userUid);

    UserOrgInfo findFirstByUserUidAndOrgUid(String userUid, String orgUid);

}
