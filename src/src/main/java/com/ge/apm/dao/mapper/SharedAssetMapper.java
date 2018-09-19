package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SharedAssetMapper extends BaseMapper<AssetInfo> {

    @Select("selectCountForSharedAssets")
    Integer getTotalRowCount(@Param("ew") Wrapper<AssetInfo> wrapper);

    @Select("loadSharedAssets")
    List<AssetInfo> loadSharedAssets(Pagination page, @Param("ew") Wrapper<AssetInfo> wrapper);

}
