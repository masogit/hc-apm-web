package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbPartOrderProvider;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.domain.ZkbPoHistoryQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * @author : 503189362
 * @date : 2020/11/16
 * @description :
 */
public interface ZkbPartOrderMapper {


    @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryZkbHistory")
    List<ZkbPoHistoryQueryVo> queryZkbHistory(@Param("filters") List<SearchFilter> filters, @Param("tenantUID") String tenantUID);

     @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryGeHistory")
    List<ZkbPoHistoryQueryVo> queryGeHistory(@Param("filters") List<SearchFilter> filters, @Param("tenantUID") String tenantUID);


    /**
     * 获取FE 人工订单
     * @param systemId 设备system id
     * @param sr sr
     * @param partOrderNo 人工单号
     * @param assetGroup 类型
     * @return FE人工申请订单
     */
    @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryBySystemIdAndSr")
    List<ZkbPartOrder> queryBySystemIdAndSr(@Param("systemId") String systemId,@Param("sr") String sr,@Param("partOrderNo") String partOrderNo,@Param("assetGroup") Integer assetGroup);

    /**
     * 查询所有单子
     * @return
     */
    @SelectProvider(type = ZkbPartOrderProvider.class, method = "batchExport")
    List<ZkbPartOrder> batchExport(@Param("testLogin")String testLogin, @Param("tenantUid")String tenantUid);

    @SelectProvider(type = ZkbPartOrderProvider.class, method = "queryPendCountByUserConfig")
    Integer queryPendCountByUserConfig(Map<String, Object> params);
}
