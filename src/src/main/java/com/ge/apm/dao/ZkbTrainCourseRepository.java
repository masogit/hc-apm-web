package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainCourse;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbTrainCourseRepository  extends GenericRepositoryUUID<ZkbTrainCourse> {
    List<ZkbTrainCourse> findByName(String name);
    List<ZkbTrainCourse> findByNameAndIdNot(String name, String id);

    List<ZkbTrainCourse> findByCourseTypeOrderByCreatedDateDesc(String courseType);
}
