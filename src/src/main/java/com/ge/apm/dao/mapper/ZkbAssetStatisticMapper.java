package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbAssetStatisticProvider;
import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import com.ge.apm.domain.ZkbAssetStatistic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * Created by 503202755 on 2020/10/13
 */
public interface ZkbAssetStatisticMapper {



    @SelectProvider(type = ZkbAssetStatisticProvider.class,method="loadAllAsset")
    List<ZkbAssetStatistic> loadAllAsset(@Param("filters") List<SearchFilter> filters);

    @SelectProvider(type = ZkbAssetStatisticProvider.class,method="loadQrCount")
    List<ZkbAssetStatistic> loadQrCount(@Param("list")List<String> list);

    @SelectProvider(type = ZkbAssetStatisticProvider.class,method="getZkbAssetQrCodeInfoList")
    List<ZkbAssetQrCodeInfo> getZkbAssetQrCodeInfoList(@Param("filters") List<SearchFilter> filters);
}
