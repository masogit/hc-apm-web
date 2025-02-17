package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.V2_PmOrder;

import java.util.List;
import java.util.Map;

public interface MvsPmOrderMapper extends BaseMapper<V2_PmOrder> {
    List<V2_PmOrder> loadData(Map<String, Object> params);
}
