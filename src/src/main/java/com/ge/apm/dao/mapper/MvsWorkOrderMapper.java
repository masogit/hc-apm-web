package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.V2_WorkOrder;

import java.util.List;
import java.util.Map;

public interface MvsWorkOrderMapper extends BaseMapper<V2_WorkOrder> {
    List<V2_WorkOrder> loadData(Map<String, Object> params);
}
