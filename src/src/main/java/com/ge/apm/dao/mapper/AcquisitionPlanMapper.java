package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.AcquisitionPlanProvider;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

public interface AcquisitionPlanMapper {


    @SelectProvider(type = AcquisitionPlanProvider.class,method="queryAcquisitionPlanListForPage")
    public List<AcquisitionPlan> queryAcquisitionPlanListForPage(@Param("filters") List<SearchFilter> filters, @Param("offset") int offset, @Param("limit") int limit);

    @SelectProvider(type = AcquisitionPlanProvider.class,method="queryAcquisitionPlanListForPageCount")
    public String queryAcquisitionPlanListForPageCount(@Param("filters") List<SearchFilter> filters);

}
