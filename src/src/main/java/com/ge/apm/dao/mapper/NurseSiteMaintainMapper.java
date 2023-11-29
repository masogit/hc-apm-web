package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.domain.NurseSiteMaintain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NurseSiteMaintainMapper extends BaseMapper<NurseSiteMaintain> {
    List<NurseSiteMaintain> findJoinAssetList(@Param("ew") Wrapper<NurseSiteMaintain> wrapper);
}
