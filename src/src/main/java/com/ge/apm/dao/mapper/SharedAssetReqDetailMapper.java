package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.SharedAssetReqDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SharedAssetReqDetailMapper extends BaseMapper<SharedAssetReqDetail> {

    List<SharedAssetReqDetail> loadSharedAssets(Pagination page, @Param("ew") Wrapper<SharedAssetReqDetail> wrapper);
    int getTotalRowCount(@Param("ew") Wrapper<SharedAssetReqDetail> wrapper);
}
