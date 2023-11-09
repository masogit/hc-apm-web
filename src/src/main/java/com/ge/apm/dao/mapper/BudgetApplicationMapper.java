package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.BudgetApplicationProvider;
import com.ge.apm.domain.budget.BudgetApplication;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;


public interface BudgetApplicationMapper {



    @SelectProvider(type = BudgetApplicationProvider.class,method="loadDataList")
    List<BudgetApplication> loadDataList(@Param("filters") List<SearchFilter> filters);
}
