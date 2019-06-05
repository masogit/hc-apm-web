package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.OpsSrReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OpsSrReportMapper extends BaseMapper {
    String SELECT_START = "select * from(";
    String SELECT_COLUMNS_FROM_SR = "select max(sr.id) as srId, max(sr.asset_name) as assetName, " +
            "sr.event_uid as srNumber, max(sr.create_time) as updateTime, max(asset.system_id) as systemId, " +
            "max(sieble.modality) as modality, max(sieble.sr_type) as srType, max(sieble.sr_open_time) as srOpenDate, " +
            "max(sieble.sr_scheduled_date) as srScheduleDate, max(sieble.sr_owner_sso) as feSso from asset_lifecycle_event sr ";
    String LEFT_JOIN_SR_REPORT = "left join service_request_report report on sr.id = report.alce_id ";
    String INNER_JOIN_ASSET = "inner join asset_info asset on asset.uid = sr.asset_uid ";
    String INNER_JOIN_SIEBLE = "inner join sieble_sr_data sieble on sr.event_uid = sieble.sr_number ";
    String WHERE_CONDITIONS = "where sr.event_type in (3, 4, 5) and sr.status = 2 and report.alce_id is null and asset.ib_status = 1 " +
            "and asset.ge_device_type is not null and asset.is_deleted = false and asset.is_valid = true and asset.status in (1,2,3) group by sr.event_uid";
    String SELECT_END = ") as resultgroup WHERE 1 = 1 ${ew.sqlSegment}";

    @Select(SELECT_START +
            SELECT_COLUMNS_FROM_SR +
            LEFT_JOIN_SR_REPORT +
            INNER_JOIN_ASSET +
            INNER_JOIN_SIEBLE +
            WHERE_CONDITIONS +
            SELECT_END)
    @Results({
            @Result(id = true, column = "srId", property = "srId"),
            @Result(column = "assetName", property = "assetName"),
            @Result(column = "srNumber", property = "srNumber"),
            @Result(column = "updateTime", property = "updateTime"),
            @Result(column = "systemId", property = "systemId"),
            @Result(column = "modality", property = "modality"),
            @Result(column = "srType", property = "srType"),
            @Result(column = "srOpenDate", property = "srOpenDate"),
            @Result(column = "srScheduleDate", property = "srScheduleDate"),
            @Result(column = "feSso", property = "feSso"),
    })
    List<OpsSrReport> findSRWithoutReports(@Param("page") Pagination pageRequest, @Param("ew") Wrapper<OpsSrReport> wrapper);


    @Select("select count(*) from (" +
            SELECT_START +
            SELECT_COLUMNS_FROM_SR +
            LEFT_JOIN_SR_REPORT +
            INNER_JOIN_ASSET +
            INNER_JOIN_SIEBLE +
            WHERE_CONDITIONS +
            SELECT_END +
            ") as the_count")
    int count(@Param("ew") Wrapper<OpsSrReport> wrapper);
}
