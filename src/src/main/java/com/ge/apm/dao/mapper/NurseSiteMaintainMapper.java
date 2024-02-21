package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.domain.NurseSiteMaintain;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NurseSiteMaintainMapper extends BaseMapper<NurseSiteMaintain> {
    List<NurseSiteMaintain> findJoinAssetList(@Param("ew") Wrapper<NurseSiteMaintain> wrapper);

    List<NurseSiteMaintain> findJoinAssetList1(@Param("ew") Wrapper<NurseSiteMaintain> wrapper, @Param("assetTypes") Collection<String> assetTypes);

    List<NurseSiteMaintain> searchMaintain(Map<String, Object> param);
}
