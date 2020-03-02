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
    String SELECT_COLUMNS_FROM_SR = "select  max(sr_sieble.id) as srId, max(sr_sieble.asset_name) as assetName, " +
            "sr_sieble.event_uid as srNumber, max(sr_sieble.create_time) as updateTime, max(sr_sieble.system_id) as systemId, " +
            "max(sr_sieble.modality) as modality, max(sr_sieble.sr_type) as srType, max(sr_sieble.sr_open_time) as srOpenDate, " +
            "max(sr_sieble.sr_scheduled_date) as srScheduleDate, max(sr_sieble.sr_owner_sso) as feSso ";
    String JOIN_ASSET = "from (select sr.id, sr.asset_name, sr.event_uid, sr.create_time, asset.system_id, sieble.modality, " +
            "sieble.sr_type, sieble.sr_open_time, sieble.sr_scheduled_date, sieble.sr_owner_sso from asset_lifecycle_event sr, asset_info asset, ";
    String GROUP_SIEBLE = "(select sr_number, max(modality) modality, max(sr_type) sr_type, max(sr_open_time) sr_open_time, " +
            "max(sr_scheduled_date) sr_scheduled_date, max(sr_owner_sso) sr_owner_sso from sieble_sr_data group by sr_number) sieble ";
    String WHERE_CONDITIONS = "where sr.event_type in (3, 4, 5, 9) and sr.status = 2  and asset.ib_status in (1,2,4) and asset.uid = sr.asset_uid " +
            "and asset.is_deleted = false and asset.is_valid = true and asset.status in (1,2,3) and sr.event_uid = sieble.sr_number) sr_sieble ";
    String LEFT_JOIN_SIEBLE = "left outer join service_request_report report on (sr_sieble.id = report.alce_id) where report.alce_id is null group by sr_sieble.event_uid ";
    String SELECT_END = ") as resultgroup WHERE 1 = 1 ${ew.sqlSegment}";

    @Select(SELECT_START +
            SELECT_COLUMNS_FROM_SR +
            JOIN_ASSET +
            GROUP_SIEBLE +
            WHERE_CONDITIONS +
            LEFT_JOIN_SIEBLE +
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
            JOIN_ASSET +
            GROUP_SIEBLE +
            WHERE_CONDITIONS +
            LEFT_JOIN_SIEBLE +
            SELECT_END +
            ") as the_count")
    int count(@Param("ew") Wrapper<OpsSrReport> wrapper);
}
