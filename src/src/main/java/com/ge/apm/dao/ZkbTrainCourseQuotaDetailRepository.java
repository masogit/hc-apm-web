package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainCourseQuotaDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainCourseQuotaDetailRepository extends GenericRepositoryUUID<ZkbTrainCourseQuotaDetail> {

    List<ZkbTrainCourseQuotaDetail> findByQuotaIdOrderByUniqueIDDesc(String quotaId);

    List<ZkbTrainCourseQuotaDetail> findByUniqueID(String uniqueId);
}
