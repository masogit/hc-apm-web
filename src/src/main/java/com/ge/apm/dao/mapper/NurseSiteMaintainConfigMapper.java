package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NurseSiteMaintainConfigMapper extends BaseMapper<NurseSiteMaintainConfig> {
    List<NurseSiteMaintainConfig> findJoinAssetList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainConfig> wrapper);

    List<NurseSiteMaintainConfig> findList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainConfig> wrapper);

    List<NurseSiteMaintainConfig> searchMaintainConfig(Map<String, Object> param);


    List<NurseSiteMaintainConfig> selectByAssetUid(@Param("assetUid") String assetUid);
}
