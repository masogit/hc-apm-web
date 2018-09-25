package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SharedAssetMapper extends BaseMapper<AssetInfo> {

    @Select("selectCountForSharedAssets")
    Integer getTotalRowCount(@Param("ew") Wrapper<AssetInfo> wrapper);

    @Select("loadSharedAssets")
    List<AssetInfo> loadSharedAssets(Pagination page, @Param("ew") Wrapper<AssetInfo> wrapper);

    @Select("SELECT ai.id,ai.uid,ai.depart_num,ai.name," +
        "sat.swap_name as swap_name,sat.unit_price as unit_price,sat.charging_type as charging_type " +
        "FROM asset_info ai INNER JOIN shared_asset_type sat ON ai.shared_asset_type=sat.id " +
        "WHERE 1 = 1 ${ew.sqlSegment}")
    @Results({
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "uid", property = "uid"),
        @Result(column = "name", property = "name"),
        @Result(column = "depart_num", property = "departNum"),
        @Result(column = "swap_name", property = "sharedAssetType.swapName"),
        @Result(column = "unit_price", property = "sharedAssetType.unitPrice"),
        @Result(column = "charging_type", property = "sharedAssetType.chargingType")
    })
    AssetInfo findSharedAssetByWrapper(@Param("ew") Wrapper<AssetInfo> wrapper);

}
