package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbContractCourseScheduleProvider;
import com.ge.apm.domain.ZkbContractCourseSchedule;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by 503202755 on 2021/4/30
 */
public interface ZknContractCourseScheduleMapper {



    /**
     * 批量获取KPI
     * @param tenantUID 租户UID
     * @return
     */
    @SelectProvider(type = ZkbContractCourseScheduleProvider.class,method = "fetchByTenantUid")
    List<ZkbContractCourseSchedule>  fetchByTenantUid(@Param("tenantUID") String tenantUID);

}
