package com.ge.apm.dao;

import com.ge.apm.domain.UserScheduleStageConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2018/5/18.
 */
public interface UserScheduleStageConfigRepository extends GenericRepositoryUUID<UserScheduleStageConfig> {

    public List<UserScheduleStageConfig> findByScheduleDateStageAndSiteUID(String scheduleDateStage, String siteUID);

    public List<UserScheduleStageConfig> findBySiteUID(String siteUID);
    @Query(value = "select  *  from  user_schedule_stage_config u where u.site_uid=?1 and (u.type=?2 or u.type is null)",nativeQuery = true)
    public List<UserScheduleStageConfig> findBySiteUIDAndTypeW(String siteUID,String typeName);

    @Query(value = "select  *  from  user_schedule_stage_config u where u.site_uid=?1 and u.type=?2",nativeQuery = true)
    public List<UserScheduleStageConfig> findBySiteUIDAndTypeA(String siteUID,String typeName);

}
