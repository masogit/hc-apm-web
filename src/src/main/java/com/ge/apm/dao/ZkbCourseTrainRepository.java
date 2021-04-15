package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourseTrain;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ZkbCourseTrainRepository extends GenericRepositoryUUID<ZkbCourseTrain> {

    @Query("select max(p.trainNoIndex) from ZkbCourseTrain p where p.trainNo like ?1 ")
    Integer getMaxIndex(String dateCondition);


}
