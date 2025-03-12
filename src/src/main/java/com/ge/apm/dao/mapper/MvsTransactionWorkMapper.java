package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.EHomeTransactionWorkOrder;

import java.util.List;
import java.util.Map;

public interface MvsTransactionWorkMapper extends BaseMapper<EHomeTransactionWorkOrder> {
    List<EHomeTransactionWorkOrder> loadData(Map<String, Object> params);
}
