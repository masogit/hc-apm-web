package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.domain.NurseSiteMaintainHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NurseSiteMaintainHistoryMapper extends BaseMapper<NurseSiteMaintainHistory> {
    List<NurseSiteMaintainHistory> findJoinAssetList(@Param("ew") Wrapper<NurseSiteMaintainHistory> wrapper);

    List<NurseSiteMaintainHistory> searchMaintainHistory(Map<String, Object> param);
}
