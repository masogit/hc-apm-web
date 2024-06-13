package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.MvsIbMapping;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MvsIbMappingMapper extends BaseMapper<MvsIbMapping> {

    @Select("select distinct mvs.mvs_ib_id from mvs_ib_mapping mvs where mvs.ib_id = #{assetId} ")
    List<Integer> findMvsAssetIds(@Param("assetId") Integer assetId);
}
