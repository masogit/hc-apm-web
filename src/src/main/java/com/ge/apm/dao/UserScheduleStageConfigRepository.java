package com.ge.apm.dao;

import com.ge.apm.domain.UserScheduleStageConfig;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 212605082 on 2018/5/18.
 */
public interface UserScheduleStageConfigRepository extends GenericRepositoryUUID<UserScheduleStageConfig> {

    public List<UserScheduleStageConfig> findByScheduleDateStageAndSiteUID(String scheduleDateStage, String siteUID);

    public List<UserScheduleStageConfig> findBySiteUID(String siteUID);

}
