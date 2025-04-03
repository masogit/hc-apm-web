package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AdverseEvents;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdverseEventsMapper {
    @Select("select * from adverse_events where 1=1 ${ew.sqlSegment}")
    List<AdverseEvents> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);
}
