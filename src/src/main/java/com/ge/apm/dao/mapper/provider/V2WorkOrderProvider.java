package com.ge.apm.dao.mapper.provider;

import java.util.Map;

public class V2WorkOrderProvider {


    /**
     * 根据时间统计订单
     * @param params
     * @return
     */
    public String statisticsOrderCount(Map<String, Object> params){
        String date = (String) params.get("date"); //时间
        String dateFormat = (String) params.get("dateFormat"); //时间格式
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(id) as order_count,tenant_uid from v2_work_order where to_char(created_date, '"+dateFormat+"') = '"+date+"' GROUP BY tenant_uid;");
        return sb.toString();
    }
}
