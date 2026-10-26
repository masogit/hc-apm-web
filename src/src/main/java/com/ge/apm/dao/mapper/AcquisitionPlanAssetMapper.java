package com.ge.apm.dao.mapper;

import com.ge.apm.domain.acquisition.AcquisitionPlanAsset;

import java.util.List;
import java.util.Map;

/**
 * 购置设备管理 MyBatis Mapper
 * 关联查询 acquisition_plan_asset + acquisition_plan + asset_info + org_info(hospital)
 */
public interface AcquisitionPlanAssetMapper {

    List<AcquisitionPlanAsset> selectList(Map<String, Object> param);

    Long selectListCount(Map<String, Object> param);
}
