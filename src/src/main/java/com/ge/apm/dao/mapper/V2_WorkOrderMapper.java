package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.V2_WorkOrder;

import java.util.List;
import java.util.Map;

/**
 * @author 503310146
 */
public interface V2_WorkOrderMapper extends BaseMapper<V2_WorkOrder> {

    List<V2_WorkOrder> queryWorkOrderList(Map<String, Object> params);


    long queryWorkOrderCount(Map<String, Object> params);

}
