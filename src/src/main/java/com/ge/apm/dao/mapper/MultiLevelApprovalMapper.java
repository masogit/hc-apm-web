package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.MultiLevelApproval;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MultiLevelApprovalMapper {
    @Select("select ma.approva_number approvalNumber, ma.* from multi_level_approval ma left join asset_scrap  scrap on ma.business_number=scrap.business_number " +
            "left join asset_department_convert adc   on ma.business_number=adc.business_number " +
            "left join v2_service_request sr on ma.business_number=sr.sr_num " +
            "left join v2_pm_order po on ma.business_uid=po.id " +
            "where 1=1 ${ew.sqlSegment}")
    List<MultiLevelApproval> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);


    @Select("WITH ApprovalAssets AS (\n" +
            "    SELECT \n" +
            "        la.id AS laId,\n" +
            "        la.tenant_uid,\n" +
            "        CASE \n" +
            "            WHEN la.business_type = 7 THEN wo.asset_uid\n" +
            "            WHEN la.business_type = 8 THEN po.asset_uid\n" +
            "            WHEN la.business_type = 15 THEN ac.asset_uid\n" +
            "            WHEN la.business_type = 16 THEN dc.asset_uid\n" +
            "        END AS asset_uid\n" +
            "    FROM multi_level_approval la\n" +
            "    LEFT JOIN v2_work_order wo ON wo.id = la.business_uid AND la.business_type = 7\n" +
            "    LEFT JOIN v2_pm_order po ON po.id = la.business_uid AND la.business_type = 8\n" +
            "    LEFT JOIN asset_scrap ac ON ac.id = la.business_uid AND la.business_type = 15\n" +
            "    LEFT JOIN asset_department_convert dc ON dc.id = la.business_uid AND la.business_type = 16\n" +
            "    WHERE la.tenant_uid = #{tenantUid}\n" +
            ")\n" +
            "SELECT la.approva_number approvalNumber, a.name as assetName, a.function_type as functionType, a.make_short_name as makerShortName,applyUser.name as applyUserName,  la.*\n" +
            "FROM multi_level_approval la\n" +
            "left join ApprovalAssets ai ON ai.laId = la.id\n" +
            "left join asset_info a on a.uid= ai.asset_uid   " +
            "left join user_account applyUser on applyUser.uid = la.application_uid   " +
            "where 1=1 ${ew.sqlSegment}")
    List<MultiLevelApproval> loadData(Pagination pagination, @Param("ew") Wrapper wrapper,  @Param("tenantUid") String tenantUid);
}
