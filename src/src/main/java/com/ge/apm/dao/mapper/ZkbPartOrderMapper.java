package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbPartOrderProvider;
import com.ge.apm.domain.ZkbPoHistoryQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 503189362
 * @date : 2020/11/16
 * @description :
 */
public interface ZkbPartOrderMapper {


    @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryZkbHistory")
    List<ZkbPoHistoryQueryVo> queryZkbHistory(@Param("filters") List<SearchFilter> filters, @Param("tenantUID") String tenantUID);

     @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryGeHistory")
    List<ZkbPoHistoryQueryVo> queryGeHistory(@Param("filters") List<SearchFilter> filters, @Param("tenantUID") String tenantUID);


}
