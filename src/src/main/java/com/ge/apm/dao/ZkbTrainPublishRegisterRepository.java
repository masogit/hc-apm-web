package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainPublishInfo;
import com.ge.apm.domain.ZkbTrainPublishRegister;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainPublishRegisterRepository extends GenericRepositoryUUID<ZkbTrainPublishRegister> {

    List<ZkbTrainPublishRegister> findByPid(String pid);
}
