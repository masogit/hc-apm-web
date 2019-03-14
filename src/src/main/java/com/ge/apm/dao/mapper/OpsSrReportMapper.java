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
    @Select("select " +
            "distinct sr.id as sr_id, sr.asset_name, sr.event_uid, sr.create_time, asset.system_id, sieble.modality, sieble.sr_type, " +
            "sieble.sr_open_time, sieble.sr_scheduled_date, sieble.sr_owner_sso from asset_lifecycle_event sr " +
            "left join service_request_report report on sr.id = report.alce_id " +
            "inner join asset_info asset on asset.uid = sr.asset_uid " +
            "inner join sieble_sr_data sieble on sr.event_uid = sieble.sr_number " +
            "where sr.event_type in (3, 4, 5) and sr.status = 2 and report.alce_id is null and asset.ib_status = 1 " +
            "and asset.ge_device_type is not null and asset.is_deleted = false and asset.status !=2 ${ew.sqlSegment}")
    @Results({
            @Result(id = true, column = "sr_id", property = "id"),
            @Result(column = "asset_name", property = "assetName"),
            @Result(column = "event_uid", property = "srNumber"),
            @Result(column = "create_time", property = "updateTime"),
            @Result(column = "system_id", property = "systemId"),
            @Result(column = "modality", property = "modality"),
            @Result(column = "sr_type", property = "srType"),
            @Result(column = "sr_open_time", property = "srOpenDate"),
            @Result(column = "sr_scheduled_date", property = "srScheduleDate"),
            @Result(column = "sr_owner_sso", property = "feSso"),
    })
    List<OpsSrReport> findSRWithoutReports(@Param("page") Pagination pageRequest, @Param("ew") Wrapper<OpsSrReport> wrapper);


    @Select("select count(*) from (select distinct " +
            "sr.id, sr.asset_name, sr.event_uid, sr.create_time, asset.system_id, sieble.modality, sieble.sr_type, " +
            "sieble.sr_open_time, sieble.sr_scheduled_date, sieble.sr_owner_sso from asset_lifecycle_event sr " +
            "left join service_request_report report on sr.id = report.alce_id " +
            "inner join asset_info asset on asset.uid = sr.asset_uid " +
            "inner join sieble_sr_data sieble on sr.event_uid = sieble.sr_number " +
            "where sr.event_type in (3, 4, 5) and sr.status = 2 and report.alce_id is null and asset.ib_status = 1 " +
            "and asset.ge_device_type is not null and asset.is_deleted = false and asset.status !=2 ${ew.sqlSegment}) as totalcount")
    int count(@Param("ew") Wrapper<OpsSrReport> wrapper);
}
