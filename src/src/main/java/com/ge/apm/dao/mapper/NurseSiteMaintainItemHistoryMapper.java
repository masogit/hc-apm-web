package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.NurseSiteMaintainItemHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NurseSiteMaintainItemHistoryMapper extends BaseMapper<NurseSiteMaintainItemHistory> {
    List<NurseSiteMaintainItemHistory> findJoinAssetList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainItemHistory> wrapper);
}
