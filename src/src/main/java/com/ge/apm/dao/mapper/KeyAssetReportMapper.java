package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import com.ge.apm.domain.KeyAssetReport;

import java.util.List;
import java.util.Map;

public interface KeyAssetReportMapper extends BaseMapper<KeyAssetReport> {
    List<KeyAssetReport> findList(Map<String, Object> param);

    Long findcount(Map<String, Object> param);

}
