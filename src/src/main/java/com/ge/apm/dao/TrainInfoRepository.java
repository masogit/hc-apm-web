package com.ge.apm.dao;

import com.ge.apm.domain.TrainInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface TrainInfoRepository extends GenericRepositoryUUID<TrainInfo> {

    @Query("select max(p.partOrderIndex) from ZkbPartOrder p where p.partOrderNo like ?1")
    Object queryMaxCourseNo(String preCourseNo);
}
