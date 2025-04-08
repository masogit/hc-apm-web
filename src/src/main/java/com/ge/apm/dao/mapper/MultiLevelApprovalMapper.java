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
}
