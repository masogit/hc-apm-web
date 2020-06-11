package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourse;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;

public interface ZkbCourseRepository extends GenericRepositoryUUID<ZkbCourse> {

    public ZkbCourse findFirstByCid(String cid);

    public ZkbCourse findFirstByCidAndCname(String cid,String cname);

    List<ZkbCourse> findByCname(String cname);
}
