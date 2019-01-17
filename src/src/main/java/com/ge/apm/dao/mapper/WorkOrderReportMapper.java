package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.WorkOrderReportProvider;
import com.ge.apm.domain.pure.AssetWorkOrder;
import com.ge.apm.domain.pure.WorkOrderExportPojo;
import com.ge.apm.domain.pure.WorkOrderReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Set;

/**
 * Created by 212605082 on 2017/12/14.
 */
public interface WorkOrderReportMapper {

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByDepart")
    public List<WorkOrderReport> statisticsByDepart(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByAssetGroup")
    public List<WorkOrderReport> statisticsByAssetGroup(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByDepartAndAssetGroup")
    public List<WorkOrderReport> statisticsByDepartAndAssetGroup(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByAssetName")
    public List<WorkOrderReport> statisticsByAssetName(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByCaseType")
    public List<WorkOrderReport> statisticsByCaseType(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsByStaff")
    public List<WorkOrderReport> statisticsByStaff(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsCaseloadByDepartment")
    public List<WorkOrderReport> statisticsCaseloadByDepartment(@Param("tenantUID") String tenantUID, @Param("institutionUID") String institutionUID, @Param("hospitalUID") String hospitalUID, @Param("siteUID") String siteUID, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = WorkOrderReportProvider.class,method="workOrderExport")
    public List<WorkOrderExportPojo> workOrderExport(@Param("filters") List<SearchFilter> filters);

    @SelectProvider(type = WorkOrderReportProvider.class,method="statisticsAssetWorkOrder")
    public List<AssetWorkOrder> statisticsAssetWorkOrder(@Param("filters") List<SearchFilter> filters);
}
