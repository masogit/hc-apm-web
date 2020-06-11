package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbContractCourse;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbContractCourseRepository extends GenericRepositoryUUID<ZkbContractCourse> {

    ZkbContractCourse findFirstByContractIdAndCourseId(String contractId, String courseId);
}
