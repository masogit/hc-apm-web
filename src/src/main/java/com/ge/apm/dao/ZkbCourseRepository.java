package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourse;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;

public interface ZkbCourseRepository extends GenericRepositoryUUID<ZkbCourse> {


    ZkbCourse findFirstByCid(String cid);

    ZkbCourse findFirstByCidAndCname(String cid,String cname);

    List<ZkbCourse> findByCname(String cname);

    /*@Query("select count(id) from ZkbContactCourse c where c.cid = ?1")
    Integer findContractByCourseId(String cid);*/
}
