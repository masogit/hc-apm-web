package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NurseSiteMaintainConfigMapper extends BaseMapper<NurseSiteMaintainConfig> {
    List<NurseSiteMaintainConfig> findJoinAssetList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainConfig> wrapper);
}
