package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCourseSchedule;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 课程计划表repository
 */
public interface ZkbCourseScheduleRepository extends GenericRepositoryUUID<ZkbContractCourseSchedule> {

        List<ZkbContractCourseSchedule> findByZkbCourseId(String courseId);

}
