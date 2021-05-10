package com.ge.apm.dao.mapper.provider;

import java.util.Map;

/**
 * Created by 503202755 on 2020/9/23
 */
public class ZkbContractCourseScheduleProvider {



    /**
     * 初始化排名信息
     * @param param
     * @return
     */
    public String fetchByTenantUid(Map<String,Object> param)
    {
        String tenantUID = param.get("tenantUID").toString();//租户tenantUID
        StringBuilder sb = new StringBuilder();
        sb.append("" +
                " SELECT DISTINCT zkb_sch.*," +
                " zkbcou.id as zkb_contract_course_id, " +
                " zkbcon.contract_no as contract_no, " +
                " zkbcon.id as contract_id " +
                " from zkb_contract zkbcon " +
                " INNER JOIN zkb_contract_course zkbcou on zkbcon.id = zkbcou.contract_id "+
                " INNER JOIN Zkb_contract_course_schedule zkb_sch on zkb_sch.zkb_course_id = zkbcou.course_id"+
                " where zkbcon.tenant_uid = '"+tenantUID+"' " +
                " and zkbcon.start_date<= current_date " +
                " and zkbcon.end_date>=current_date "  +
                " and zkb_sch.valid = 't' " +
                " and zkb_sch.start_date<= current_date " +
                " and zkb_sch.end_date>=current_date;");
        return sb.toString();
    }


}
