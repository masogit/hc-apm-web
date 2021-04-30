package com.ge.apm.dao;

import com.ge.apm.domain.ZkbContractCourseSchedule;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * 课程计划表repository
 */
public interface ZkbCourseScheduleRepository extends GenericRepositoryUUID<ZkbContractCourseSchedule> {


//    /**
//     * 获取供应商课程计划
//     * @param tenantUID
//     * @returnZ
//     */
//    @Query(value = "SELECT DISTINCT zkb_sch.*,zkbcou.id as zkb_contract_course_id,zkbcou.id as zkbContractCourseId from zkb_contract zkbcon LEFT JOIN zkb_contract_course zkbcou on zkbcon.id = zkbcou.contract_id " +
//            "LEFT JOIN Zkb_contract_course_schedule zkb_sch on zkb_sch.zkb_course_id = zkbcou.course_id " +
//            "where zkbcon.tenant_uid =  ?1 and " +
//            "zkbcon.start_date<= current_date and zkbcon.end_date>=current_date " +
//            "and zkb_sch.valid = 't' and zkb_sch.start_date<= current_date and zkb_sch.end_date>=current_date;", nativeQuery = true)
//    List<ZkbContractCourseSchedule> fetchByTenantUid(String tenantUID);

}
