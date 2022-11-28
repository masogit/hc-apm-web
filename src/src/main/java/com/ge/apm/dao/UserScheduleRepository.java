package com.ge.apm.dao;

import com.ge.apm.domain.UserSchedule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
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
    @Modifying
    @Transactional
    @Query(value="delete from user_schedule where user_uid in ?1 and schedule_date between ?2 and ?3 and type=?4 and schedule_date_stage in ?5 and site_uid=?6",nativeQuery = true)
    void batchDeleteWorkSchedule(List<String> collect, Date startBatchDate, Date endBatchDate, String name,List<String> scheduleDateStage,String siteUid);
}
