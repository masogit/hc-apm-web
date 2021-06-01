package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourse;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbCourseRepository extends GenericRepositoryUUID<ZkbCourse> {


    ZkbCourse findFirstByCid(String cid);

    ZkbCourse findFirstByCidAndCname(String cid,String cname);

    List<ZkbCourse> findByCname(String cname);

    @Query(value = "SELECT * FROM zkb_course order by created_date desc", nativeQuery = true)
    List<ZkbCourse> fetchAllSortByCreateDate();


}
