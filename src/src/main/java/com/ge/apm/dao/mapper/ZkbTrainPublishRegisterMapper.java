package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.BudgetApplicationProvider;
import com.ge.apm.dao.mapper.provider.ZkbTrainPublishRegisterProvider;
import com.ge.apm.domain.ZkbTrainPublishInfo;
import com.ge.apm.domain.ZkbTrainPublishRegister;
import com.ge.apm.domain.budget.BudgetApplication;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;


public interface ZkbTrainPublishRegisterMapper {



    @SelectProvider(type = ZkbTrainPublishRegisterProvider.class,method="loadUnRegisterDataList")
    List<ZkbTrainPublishInfo> loadUnRegisterDataList(@Param("filters") List<SearchFilter> filters, @Param("tenantUid")String tenantUid);
}
