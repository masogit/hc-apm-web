package com.ge.apm.dao;

import com.ge.apm.domain.ZkbCourseTrain;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface ZkbCourseTrainRepository extends GenericRepositoryUUID<ZkbCourseTrain> {

    @Query("select max(p.trainNoIndex) from ZkbCourseTrain p where p.trainNo like ?1 ")
    Integer getMaxIndex(String dateCondition);


    /**
     * 根据租户UID和course shcedule id 查询
     * @param tenantUid 租户tenant uid
     * @param courseScheduleId 课程表ID
     * @return
     */
    @Query(value = "SELECT * FROM zkb_course_train WHERE tenant_uid = ?1 and course_schedule_id = ?2 ORDER BY created_date desc limit 1 ", nativeQuery = true)
    ZkbCourseTrain findByUidAndSheduleId(String tenantUid, String courseScheduleId);


}
