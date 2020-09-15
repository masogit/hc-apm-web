package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.OrderHistoryProvider;
import com.ge.apm.domain.ZkbOrderHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * Created by 503202755 on 2020/9/2
 */
public interface ZkbOrderHistoryMapper {



    @SelectProvider(type = OrderHistoryProvider.class,method="loadZkbOrderHistory")
    List<ZkbOrderHistory> loadZkbOrderHistory(@Param("filters") List<SearchFilter> filters);
}
