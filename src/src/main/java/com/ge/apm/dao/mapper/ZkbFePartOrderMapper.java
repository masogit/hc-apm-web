package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbFePartOrderProvider;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ZkbFePartOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * Created by 503202755 on 2020/12/9
 */
public interface ZkbFePartOrderMapper {


    /**
     * 根据systemId和用户tenantUID获取设备信息
     * @param systemId 设备systemId
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class,method="loadAssetInfo")
    List<ZkbFePartOrder> loadAssetInfo(@Param("systemId") String systemId);

    /**
     * 根据systemId和用户tenantUID获取设备信息
     * @param uid uid
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class,method="loadAssetInfoByUid")
    List<AssetInfo> loadAssetInfoByUid(@Param("uid") String uid);
    /**
     * 根据SYSTEM ID 和 contract_id tenant_uid 获取设备信息
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class,method="loadAssetInfoForHisData")
    List<AssetInfo> loadAssetInfoForHisData(@Param("systemId") String systemId,@Param("contractId") String contractId,@Param("tenantUid") String tenantUid);



    /**
     * 根据systemId和用户tenantUID获取设备信息
     * @param uid uid
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class,method="loadAssetInfoBuildByUid")
    List<ZkbFePartOrder> loadAssetInfoBuildByUid(@Param("uid") String uid);




    /**
     * 根据systemId和用户tenantUID获取设备信息
     * @param systemId 设备systemId
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class,method="loadAssetInfoBySysId")
    List<AssetInfo> loadAssetInfoBySysId(@Param("systemId") String systemId);

    @SelectProvider(type = ZkbFePartOrderProvider.class, method = "queryZkbFePart")
    List<ZkbFePartOrder> queryZkbFePart(@Param("filters") List<SearchFilter> filters);

    /**
     * 查询等待归还的配件
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class, method = "buildFilter_queryNeedReturnSparePart")
    List<ZkbFePartOrder> buildFilter_queryNeedReturnSparePart(@Param("testLogin")String testLogin);

    /**
     * 查询等待归还的探头
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class, method = "buildFilter_queryNeedReturnProbe")
    List<ZkbFePartOrder> buildFilter_queryNeedReturnProbe(@Param("testLogin")String testLogin);


    /**
     * 查询所有单子
     * @return
     */
    @SelectProvider(type = ZkbFePartOrderProvider.class, method = "batchExport")
    List<ZkbFePartOrder> batchExport(@Param("testLogin")String testLogin,@Param("tenantUid")String tenantUid);
}
