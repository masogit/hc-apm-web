package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourseTrainDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbCourseTrainDetailRepository extends GenericRepositoryUUID<ZkbCourseTrainDetail> {

    List<ZkbCourseTrainDetail> findByTrainId(String trainId);

    ZkbCourseTrainDetail findFirstByTrainIdAndEid(String trainId,String eid);
}
