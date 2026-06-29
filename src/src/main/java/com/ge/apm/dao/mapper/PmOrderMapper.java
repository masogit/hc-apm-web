package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.V2_PmOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface PmOrderMapper {
    @Select("select * from v2_pm_order where 1=1 ${ew.sqlSegment}")
    List<V2_PmOrder> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);


    List<V2_PmOrder> queryPmOrderList(Map<String, Object> params);

    long queryPmOrderCount(Map<String, Object> params);
}
