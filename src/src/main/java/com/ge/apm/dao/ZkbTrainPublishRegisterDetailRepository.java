package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainPublishRegisterDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainPublishRegisterDetailRepository extends GenericRepositoryUUID<ZkbTrainPublishRegisterDetail> {


    List<ZkbTrainPublishRegisterDetail>  findByRegisterId(String registerId);
}
