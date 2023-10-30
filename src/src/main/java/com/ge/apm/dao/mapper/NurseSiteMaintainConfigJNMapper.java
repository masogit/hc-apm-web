package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.NurseSiteMaintainConfigJN;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NurseSiteMaintainConfigJNMapper extends BaseMapper<NurseSiteMaintainConfigJN> {
    List<NurseSiteMaintainConfigJN> findJoinAssetList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainConfigJN> wrapper);
}
