package com.ge.apm.dao.mapper.provider;

import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/23
 */
public class ZkbKeyBehaviorIndexProvider {

    public String loadZkbKeyBehaviorIndex(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        StringBuilder sb = new StringBuilder();
        sb.append(" select RANK () OVER (PARTITION BY kbi_date ORDER BY total_score DESC ) rank, * from " +
                "( SELECT DISTINCT (apm_billing_rate_score + compliance_score + engineer_score + part_return_score + paste_quick_response_rate_score + satisfactory_degree_score ) AS total_score," +
                " T .*, t1.NAME AS tenant_name, dit.di_rank,ccst.ccs_rank,pcbt.pcb_rank FROM zkb_key_behavior_index T " +
                " INNER JOIN tenant_info t1 ON T.tenant_uid = t1.uid " +
                " left join (SELECT RANK ( ) OVER ( PARTITION by dt ORDER BY total_score DESC ) di_rank, * FROM " +
                "   ( SELECT DISTINCT i.tenant_uid, ( apm_billing_rate_score + compliance_score + engineer_score + part_return_score + paste_quick_response_rate_score + satisfactory_degree_score ) AS total_score , TO_CHAR( kbi_date, 'yyyy-MM' ) dt FROM zkb_key_behavior_index i " +
                "    inner join zkb_contract c on i.tenant_uid = c.tenant_uid and contract_type = 'DI' and start_date <= ( kbi_date+ INTERVAL '1 month - 1 day') and end_date >= kbi_date" +
                "   ) T order by dt ) dit on T.tenant_uid = dit.tenant_uid and TO_CHAR(T.kbi_date, 'yyyy-MM' ) = dit.dt " +
                " left join (SELECT RANK ( ) OVER ( PARTITION by dt ORDER BY total_score DESC ) ccs_rank, * FROM " +
                "   ( SELECT DISTINCT i.tenant_uid, ( apm_billing_rate_score + compliance_score + engineer_score + part_return_score + paste_quick_response_rate_score + satisfactory_degree_score ) AS total_score , TO_CHAR( kbi_date, 'yyyy-MM' ) dt FROM zkb_key_behavior_index i " +
                "    inner join zkb_contract c on i.tenant_uid = c.tenant_uid and contract_type in ('CCS','Biomed','CCSB' ) and start_date <= ( kbi_date+ INTERVAL '1 month - 1 day') and end_date >= kbi_date" +
                "   ) T order by dt ) ccst on T.tenant_uid = ccst.tenant_uid and TO_CHAR(T.kbi_date, 'yyyy-MM' ) = ccst.dt " +
                " left join (SELECT RANK ( ) OVER ( PARTITION by dt ORDER BY total_score DESC ) pcb_rank, * FROM " +
                "   ( SELECT DISTINCT i.tenant_uid, ( apm_billing_rate_score + compliance_score + engineer_score + part_return_score + paste_quick_response_rate_score + satisfactory_degree_score ) AS total_score , TO_CHAR( kbi_date, 'yyyy-MM' ) dt FROM zkb_key_behavior_index i " +
                "    inner join zkb_contract c on i.tenant_uid = c.tenant_uid and contract_type in ('PCB','PCB MVS') and start_date <= ( kbi_date+ INTERVAL '1 month - 1 day') and end_date >= kbi_date" +
                "   ) T order by dt ) pcbt on T.tenant_uid = pcbt.tenant_uid and TO_CHAR(T.kbi_date, 'yyyy-MM' ) = pcbt.dt " +
                " WHERE 1 = 1   ");
        String filter = buildFilter_zkbOrderHistoryDetail(searchFilterList);
        sb.append(filter);
        sb.append(" ) T ORDER BY kbi_date DESC,tenant_name ASC;");
        return sb.toString();
    }

    public String buildFilter_zkbOrderHistoryDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("tenantUID".equals(tempColumn))
            {
                sb.append("and T.tenant_uid='"+value.toString()+"'");
            }
            if("beginningTime".equals(tempColumn))
            {
                sb.append(" and T.kbi_date>='"+value+"'");
            }
            if("endingTime".equals(tempColumn))
            {
                sb.append(" and T.kbi_date<='"+value+"'");
            }
            if("effect".equals(tempColumn))
            {
                sb.append(" and T.effect = '"+value+"'");
            }
        }
        return sb.toString();
    }

    public String loadAverageZkbKeyBehaviorIndex(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT AVG(apm_billing_rate_score) as apm_billing_rate_score,"+
                        "AVG(compliance_score) as compliance_score,"+
                        "AVG(engineer_score) as engineer_score,"+
                        "AVG(compliance_score) as compliance_score,"+
                        "AVG(part_return_score) as part_return_score,"+
                        "AVG(paste_quick_response_rate_score) as paste_quick_response_rate_score,"+
                        "AVG(satisfactory_degree_score) as satisfactory_degree_score,kbi_date "+
                        "from zkb_key_behavior_index T where 1=1 ");
        String filter = buildFilter_zkbOrderHistoryDetail(searchFilterList);
        sb.append(filter);
        sb.append("GROUP BY kbi_date ORDER BY kbi_date ASC");
        return sb.toString();
    }

    /**
     * 初始化排名信息
     * @param param
     * @return
     */
    public String initRanking(Map<String,Object> param)
    {
        String kpiDate = param.get("kpiDate").toString();//kpi时间
        String dateFormat = param.get("dateFormat").toString();//日期格式
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT " +
                " RANK () OVER (ORDER BY total_score DESC), " +
                " * " +
                "FROM " +
                " ( " +
                " SELECT " +
                " tenant_uid, ( " +
                " apm_billing_rate_score + compliance_score + engineer_score + part_return_score + paste_quick_response_rate_score + satisfactory_degree_score\n" +
                " ) AS total_score " +
                " FROM " +
                " zkb_key_behavior_index " +
                " WHERE " +
                " '"+kpiDate+"' = TO_CHAR(kbi_date, '"+dateFormat+"') " +
                " ) T");
        return sb.toString();
    }

    /**
     * 组装批量生效sql语句
     * @param param
     * @return
     */
    public String batchUpdate(Map<String,Object> param)
    {
        String kpiDate = param.get("kpiDate").toString();//kpi时间
        String dateFormat = param.get("dateFormat").toString();//日期格式
        String tenantUID = "";
        if(param.get("tenantUID")!=null)
        {
            tenantUID=param.get("tenantUID").toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE zkb_key_behavior_index set effect ='Y' where '"+kpiDate+"' = TO_CHAR(kbi_date, '"+dateFormat+"') ");
        sb.append(buildFilter_batchUpdate(tenantUID));
        return sb.toString();
    }

    /**
     * 添加租户UID条件
     * @param tenantUID 租户UID
     * @return
     */
    public String buildFilter_batchUpdate(String tenantUID){
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(tenantUID))
        {
            sb.append(" and tenant_uid = '"+tenantUID+"'");
        }
        return sb.toString();
    }
    /**
     * 添加租户UID条件
     * @param tenantUID 租户UID
     * @return
     */
    public String buildFilter_batchUpdateOfCCSB(String tenantUID){
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(tenantUID))
        {
            sb.append(" and zkbi.tenant_uid = '"+tenantUID+"'");
        }
        return sb.toString();
    }

    /**
     * 组装批量生效sql语句
     * @param param
     * @return
     */
    public String findByDateAndTenantUID(Map<String,Object> param)
    {
        String kpiDate = param.get("kpiDate").toString();//kpi时间
        String dateFormat = param.get("dateFormat").toString();//日期格式
        String tenantUID = "";
        if(param.get("tenantUID")!=null)
        {
            tenantUID=param.get("tenantUID").toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select * from zkb_key_behavior_index where '"+kpiDate+"' = TO_CHAR(kbi_date, '"+dateFormat+"') ");
        sb.append(buildFilter_batchUpdate(tenantUID));
        return sb.toString();
    }

    /**
     * 组装批量生效sql语句
     * @param param
     * @return
     */
    public String findByDateAndTenantUIDOfCCSB(Map<String,Object> param)
    {
        String kpiDate = param.get("kpiDate").toString();//kpi时间
        String dateFormat = param.get("dateFormat").toString();//日期格式
        String tenantUID = "";
        if(param.get("tenantUID")!=null)
        {
            tenantUID=param.get("tenantUID").toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select zkbi.*,recalculatetable.recalculate from zkb_key_behavior_index zkbi left join (select t.tenant_uid,true as recalculate from zkb_contract t where t.contract_type='CCSB' group by t.tenant_uid having count(t.contract_no)>0) recalculatetable on recalculatetable.tenant_uid =zkbi.tenant_uid  where '"+kpiDate+"' = TO_CHAR(zkbi.kbi_date, '"+dateFormat+"') ");
        sb.append(buildFilter_batchUpdateOfCCSB(tenantUID));
        return sb.toString();
    }
}
