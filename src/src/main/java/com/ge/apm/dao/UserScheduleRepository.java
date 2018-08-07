package com.ge.apm.dao;

import com.ge.apm.domain.UserSchedule;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * Created by 212605082 on 2017/11/13.
 */
public interface UserScheduleRepository extends GenericRepositoryUUID<UserSchedule> {

    public List<UserSchedule> findByTypeAndUserUID(String type, String userUID);

    public List<UserSchedule> findByTypeAndUserUIDAndScheduleDate(String type, String userUID, Date scheduleDate);

    public List<UserSchedule> findByTypeAndUserUIDAndScheduleDateAndAuditStatus(String type, String userUID, Date scheduleDate, String auditStatus);

    public List<UserSchedule> findByTypeAndUserUIDAndScheduleDateAndScheduleDateStage(String type, String userUID, Date scheduleDate, String scheduleDateStage);

    public List<UserSchedule> findByTypeAndScheduleDateAndTenantUID(String type, Date scheduleDate, String tenantUID);

    public List<UserSchedule> findByTypeAndScheduleDateAndSiteUID(String type, Date scheduleDate, String siteUID);

    public void deleteByTypeAndScheduleDateAndSiteUID(String type, Date scheduleDate, String siteUID);

}
