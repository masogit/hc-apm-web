package com.ge.apm.dao;

import com.ge.apm.domain.ZkbTrainPublishInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

public interface ZkbTrainPublishRepository  extends GenericRepositoryUUID<ZkbTrainPublishInfo> {


    List<ZkbTrainPublishInfo> findByCourseId(String courseId);

    @Query(value = "SELECT z.* FROM zkb_train_publish_info z where (to_date(to_char(z.train_start_date, 'yyyy-mm-dd'), 'yyyy-mm-dd') - current_date) = 15", nativeQuery = true)
    List<ZkbTrainPublishInfo> findByCourseStartDate();

    @Query(value = "SELECT z.* FROM zkb_train_publish_info z where z.course_id in (select id from zkb_train_course where name = ?1) and z.train_start_date = ?2 and z.train_end_date = ?3  limit 1", nativeQuery = true)
    ZkbTrainPublishInfo findBySelectedInfo(String courseName, Date trainStartDate, Date trainEndDate);
}
