package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.ZkbKeyBehaviorIndexProvider;
import com.ge.apm.domain.ZkbKeyBehaviorIndex;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * Created by 503202755 on 2020/9/23
 */
public interface ZkbKeyBehaviorIndexMapper {



    @SelectProvider(type = ZkbKeyBehaviorIndexProvider.class,method="loadZkbKeyBehaviorIndex")
    List<ZkbKeyBehaviorIndex> loadZkbKeyBehaviorIndex(@Param("filters") List<SearchFilter> filters);

    @SelectProvider(type = ZkbKeyBehaviorIndexProvider.class,method="loadAverageZkbKeyBehaviorIndex")
    List<ZkbKeyBehaviorIndex> loadAverageZkbKeyBehaviorIndex(@Param("filters") List<SearchFilter> filters);

    /**
     * 初始化供应商排名数据
     * @param kpiDate
     * @param dateFormat
     * @return
     */
    @SelectProvider(type = ZkbKeyBehaviorIndexProvider.class,method="initRanking")
    List<ZkbKeyBehaviorIndex> initRanking(@Param("kpiDate") String kpiDate,@Param("dateFormat") String dateFormat);


    /**
     * 批量更新KPI剩下字段
     * @param kpiDate  KPI时间
     * @param dateFormat 日期格式
     * @param tenantUID 租户UID
     * @return
     */
    @UpdateProvider(type = ZkbKeyBehaviorIndexProvider.class,method = "batchUpdate")
    Integer batchUpdate(@Param("kpiDate") String kpiDate,@Param("dateFormat") String dateFormat,@Param("tenantUID") String tenantUID);


    /**
     * 批量获取KPI
     * @param kpiDate  KPI时间
     * @param dateFormat 日期格式
     * @param tenantUID 租户UID
     * @return
     */
    @SelectProvider(type = ZkbKeyBehaviorIndexProvider.class,method = "findByDateAndTenantUID")
    List<ZkbKeyBehaviorIndex>  findByDateAndTenantUID(@Param("kpiDate") String kpiDate,@Param("dateFormat") String dateFormat,@Param("tenantUID") String tenantUID);
    /**
     * 批量获取KPI
     * @param kpiDate  KPI时间
     * @param dateFormat 日期格式
     * @param tenantUID 租户UID
     * @return
     */
    @SelectProvider(type = ZkbKeyBehaviorIndexProvider.class,method = "findByDateAndTenantUIDOfCCSB")
    List<ZkbKeyBehaviorIndex>  findByDateAndTenantUIDOfCCSB(@Param("kpiDate") String kpiDate,@Param("dateFormat") String dateFormat,@Param("tenantUID") String tenantUID);

}
