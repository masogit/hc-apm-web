package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbContractProvider;
import com.ge.apm.domain.ZkbContract;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by 503202755 on 2021/6/28
 */
public interface ZkbContractMapper {




    @SelectProvider(type = ZkbContractProvider.class,method="getContractByTenantUIDList")
    List<ZkbContract> getContractByTenantUIDList(@Param("tenantUIDList")List<String> tenantUIDList);

}
