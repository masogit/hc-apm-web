package com.ge.apm.service.zkb;


import com.ge.apm.dao.ZkbCourseScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 课程计划表service
 */
@Component
public class ZkbCourseScheduleService {

    @Autowired
    private ZkbCourseScheduleRepository zkbCourseScheduleRepository;

}
