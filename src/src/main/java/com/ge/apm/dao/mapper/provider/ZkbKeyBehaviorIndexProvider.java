package com.ge.apm.dao.mapper.provider;

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
        sb.append("SELECT " +
                " T .*, t1. NAME AS tenant_name " +
                "FROM " +
                " zkb_key_behavior_index T " +
                "INNER JOIN tenant_info t1 ON T .tenant_uid = t1.uid " +
                "WHERE " +
                " 1 = 1");
        String filter = buildFilter_zkbOrderHistoryDetail(searchFilterList);
        sb.append(filter);
        sb.append(" order by kbi_date asc , tenant_name asc");
        return sb.toString();
    }

    public String buildFilter_zkbOrderHistoryDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("tenantUID".equals(tempColumn))
            {
                sb.append("and tenant_uid='"+value.toString()+"'");
            }
            if("beginningTime".equals(tempColumn))
            {
                sb.append(" and kbi_date>='"+value+"'");
            }
            if("endingTime".equals(tempColumn))
            {
                sb.append(" and kbi_date<='"+value+"'");
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
//                        "AVG(intact_part_return_rate_score) as intact_part_return_rate_score,"+
                        "AVG(part_return_score) as part_return_score,"+
                        "AVG(paste_quick_response_rate_score) as paste_quick_response_rate_score,"+
                        "AVG(satisfactory_degree_score) as satisfactory_degree_score,kbi_date "+
                        "from zkb_key_behavior_index where 1=1 ");
        String filter = buildFilter_zkbOrderHistoryDetail(searchFilterList);
        sb.append(filter);
        sb.append("GROUP BY kbi_date ORDER BY kbi_date ASC");
        return sb.toString();
    }
}
