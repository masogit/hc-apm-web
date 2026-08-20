package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetAnnualInspectionReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetAnnualInspectionReportMapper extends BaseMapper<AssetAnnualInspectionReport> {

    List<AssetAnnualInspectionReport> selectAnnualInspectionReportList(Pagination pagination, @Param("ew") Wrapper<AssetAnnualInspectionReport> wrapper);

    List<AssetAnnualInspectionReport> selectAnnualInspectionReportListByTenantUid(String tenantUid);
}
