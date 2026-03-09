package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.KeyAssetReport;
import com.ge.apm.domain.MeasuringCertificateDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeasureAssetMapper extends BaseMapper<KeyAssetReport> {

    List<MeasuringCertificateDetail> selectMeasureUnpairedResult( @Param("ew") Wrapper<AssetInfo> wrapper);

}
