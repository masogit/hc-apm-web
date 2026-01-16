package com.ge.apm.dao.mapper;

import com.ge.apm.domain.dto.HealthPendingStatistics;

import java.util.Map;

public interface HealthMapper {
    Integer countAssetsToMeter(Map<String, Object> param);
    Integer countOpenIssues(Map<String, Object> param);
    Integer countOutOfWarranty(Map<String, Object> param);
    Integer countPmLv2(Map<String, Object> param);
    Integer countPmLv3(Map<String, Object> param);
    HealthPendingStatistics deviceStatusSummary(Map<String, Object> param);
}
