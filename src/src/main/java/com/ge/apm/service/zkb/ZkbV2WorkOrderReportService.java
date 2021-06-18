package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.ZkbV2WorkOrderMapper;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.util.DateUtil;

import java.math.BigDecimal;
import java.util.*;


/**
 * 维修工作台报表统计
 */
@Component
public class ZkbV2WorkOrderReportService extends MicroServiceInvoker {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ZkbV2WorkOrderReportService.class);

    /**
     * 维修工作台统计
     */
    @Autowired
    private ZkbV2WorkOrderMapper zkbV2WorkOrderMapper;

    /**
     * 获取租户信息
     */
    @Autowired
    private TenantInfoRepository tenantInfoRepository;

    /**
     * 租户类型
     */
    private static final int TENANT_INFO_TYPE = 3;

    /**
     * 开始年份
     */
    private static final int BEGIN_YEAR = 2019;


    /**
     * @param yearMap 年的title
     * @param yearMonthMap 统计每年平均的title
     * @param monthMap 月的title
     * @return
     */
    public List<Map<String, Object>> statisticsOrderCount(Map<String,String> yearMap,
                                                          Map<String,String> yearMonthMap,
                                                          Map<String,String> monthMap)
    {
        Date date = new Date();
        String formatDateYear = DateUtil.getFormatStrByDate(date,"yyyy");
        String formatDateMonth = DateUtil.getFormatStrByDate(date,"MM");
        List<TenantInfo> tenantInfoList = this.tenantInfoRepository.findByType(TENANT_INFO_TYPE);
        List<Map<String, Object>> zkbV2WorkOrderReportList = this.initZkbV2WorkOrderReportList(tenantInfoList);
        this.statisticsCurrentYearMonthCount(zkbV2WorkOrderReportList,formatDateYear,formatDateMonth,monthMap);
        this.statisticsYearCount(zkbV2WorkOrderReportList,formatDateYear,formatDateMonth,yearMap,yearMonthMap);
        return zkbV2WorkOrderReportList;

    }

    /**
     * 根据租户信息初始化维修工单报表列表
     * @param tenantInfoList 租户信息列表
     * @return
     */
    private List<Map<String, Object>> initZkbV2WorkOrderReportList(List<TenantInfo> tenantInfoList)
    {
        List<Map<String, Object>> zkbV2WorkOrderReportList = new ArrayList<>();
        if(CollectionUtils.isEmpty(tenantInfoList))
            return zkbV2WorkOrderReportList;
        for(TenantInfo item:tenantInfoList)
        {
            Map<String,Object> reportMap = new HashMap<>();
            reportMap.put("id",item.getId());
            reportMap.put("uid",item.getUid());
            reportMap.put("tenantName",item.getName());
            zkbV2WorkOrderReportList.add(reportMap);
        }
        return zkbV2WorkOrderReportList;
    }


    /**
     * 统计当前年度每月订单数
     * @param zkbV2WorkOrderReportList 统计列表
     * @param formatDateYear 当前时间按年格式化
     * @param formatDateMonth 当前月份
     * @param monthTitleMap 当前月份
     * @return
     */
    private void statisticsCurrentYearMonthCount(List<Map<String,Object>> zkbV2WorkOrderReportList,
                                                 String formatDateYear,
                                                 String formatDateMonth,
                                                 Map<String,String> monthTitleMap)
    {
        if(CollectionUtils.isEmpty(zkbV2WorkOrderReportList))
        {
            return;
        }
        int intFormatDateMonth = Integer.parseInt(formatDateMonth);
        for(int i =1;i<=intFormatDateMonth;i++)
        {
            String month = i+"";
            if(month.length()==1){
                month="0"+month;
            }
            monthTitleMap.put("m_"+i,i+"月");
            List<V2_WorkOrder> v2_workOrderList = zkbV2WorkOrderMapper.statisticsOrderCount(formatDateYear+"-"+month,"yyyy-MM");
            Map<String,Integer> monthMap = new HashMap<>();
            for(V2_WorkOrder item:v2_workOrderList)
            {
                monthMap.put(item.getTenantUID(),item.getOrderCount());
            }
            for(Map<String, Object> reportMap:zkbV2WorkOrderReportList)
            {
                if(!monthMap.containsKey(reportMap.get("uid")))
                {
                    reportMap.put("m_"+i,0);
                    continue;
                }
                reportMap.put("m_"+i,monthMap.get(reportMap.get("uid")));
            }
        }
    }


    /**
     * 统计当前年度每月订单数
     * @param zkbV2WorkOrderReportList 统计列表
     * @param formatDateYear 当前时间按年格式化
     * @param formatDateMonth 当前月
     * @param yearMap 年的title
     * @param yearMonthMap 年月的title
     * @return
     */
    private void statisticsYearCount(List<Map<String, Object>> zkbV2WorkOrderReportList,
                                     String formatDateYear,
                                     String formatDateMonth,
                                     Map<String,String> yearMap,
                                     Map<String,String> yearMonthMap)

    {
        if(CollectionUtils.isEmpty(zkbV2WorkOrderReportList))
        {
            return;
        }
        int intFormatDateYear = Integer.parseInt(formatDateYear);
        int intFormatDateMonth = Integer.parseInt(formatDateMonth);
        for(int i =BEGIN_YEAR;i<=intFormatDateYear;i++)
        {
            yearMap.put("y_"+i,i+"年");
            yearMonthMap.put("y_m_"+i,i+"-monthly");
            List<V2_WorkOrder> v2_workOrderList = zkbV2WorkOrderMapper.statisticsOrderCount(i+"","yyyy");
            Map<String,Integer> monthMap = new HashMap<>();
            for(V2_WorkOrder item:v2_workOrderList)
            {
                monthMap.put(item.getTenantUID(),item.getOrderCount());
            }
            for(Map<String, Object> reportMap:zkbV2WorkOrderReportList)
            {
                if(!monthMap.containsKey(reportMap.get("uid")))
                {
                    reportMap.put("y_"+i,0);
                    reportMap.put("y_m_"+i,0);
                    continue;
                }
                reportMap.put("y_"+i,monthMap.get(reportMap.get("uid")));
                BigDecimal count = new BigDecimal(monthMap.get(reportMap.get("uid")));
                BigDecimal monthCount = null;
                if(i==intFormatDateYear)
                {
                    monthCount=new BigDecimal(intFormatDateMonth);
                }else
                    {
                        monthCount=new BigDecimal(12);
                    }
                    reportMap.put("y_m_"+i,count.divide(monthCount,1,BigDecimal.ROUND_DOWN));
            }
        }
    }
}
