package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainCourseQuotaDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainCourseQuotaDetailRepository extends GenericRepositoryUUID<ZkbTrainCourseQuotaDetail> {

    List<ZkbTrainCourseQuotaDetail> findByQuotaId(String quotaId);
}
