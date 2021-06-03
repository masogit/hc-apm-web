package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCourseSchedule;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 课程计划表repository
 */
public interface ZkbCourseScheduleRepository extends GenericRepositoryUUID<ZkbContractCourseSchedule> {

        List<ZkbContractCourseSchedule> findByZkbCourseId(String courseId);

        @Query(value = "SELECT * FROM zkb_contract_course_schedule order by create_date desc", nativeQuery = true)
        List<ZkbContractCourseSchedule> fetchAllSortByCreateDate();

}
