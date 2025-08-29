package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainPublishInfo;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainPublishRepository  extends GenericRepositoryUUID<ZkbTrainPublishInfo> {


    List<ZkbTrainPublishInfo> findByCourseId(String courseId);

}
