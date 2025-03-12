package com.ge.apm.dao;

import com.ge.apm.domain.TrainInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface TrainInfoRepository extends GenericRepositoryUUID<TrainInfo> {

    @Query("select max(p.courseNo) from TrainInfo p where p.courseNo like ?1")
    Object queryMaxCourseNo(String preCourseNo);
}
