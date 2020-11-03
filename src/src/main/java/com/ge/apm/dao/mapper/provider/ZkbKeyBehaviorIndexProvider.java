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
}
