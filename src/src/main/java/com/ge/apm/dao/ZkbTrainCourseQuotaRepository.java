package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainCourseQuota;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainCourseQuotaRepository extends GenericRepositoryUUID<ZkbTrainCourseQuota> {

    ZkbTrainCourseQuota findFirstByTenantUidAndContractNoAndCourseId(String tenantUid, String contractNo, String courseId);


    List<ZkbTrainCourseQuota> findByCourseId(String courseId);
    ZkbTrainCourseQuota findFirstByTenantUidAndCourseId(String tenantUid, String courseId);


}
