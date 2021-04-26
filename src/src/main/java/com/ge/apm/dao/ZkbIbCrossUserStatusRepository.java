package com.ge.apm.dao;

import com.ge.apm.domain.ZkbIbCrossUserStatus;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


/**
 *
 */
public interface ZkbIbCrossUserStatusRepository extends GenericRepositoryUUID<ZkbIbCrossUserStatus> {

    List<ZkbIbCrossUserStatus> findByLoginName(String loginName);

    void deleteByLoginName(String loginName);
}
