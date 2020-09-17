package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/3
 */
public class OrderHistoryProvider {

    public String loadZkbOrderHistory(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        StringBuilder sb = new StringBuilder();
        sb.append("select * from zkb_order_history where 1 = 1 ");
        String filter = buildFilter_zkbOrderHistoryDetail(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }

    public String buildFilter_zkbOrderHistoryDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("dataType".equals(tempColumn))
            {
                sb.append("and data_type='"+value.toString()+"'");
            }
            if("contractBeginningTime".equals(tempColumn))
            {
                sb.append(" and contract_beginning_time>='"+value+"'");
            }
            if("contractEndingTime".equals(tempColumn))
            {
                sb.append(" and contract_ending_time<='"+value+"'");
            }
            if("region".equals(tempColumn))
            {
                sb.append(" and region like '%"+value.toString()+"%'");
            }
            if("agent".equals(tempColumn))
            {
                sb.append(" and agent like '%"+value.toString()+"%'");
            }
            if("contractNo".equals(tempColumn))
            {
                sb.append(" and contract_no like '%"+value.toString()+"%'");
            }
            if("contractType".equals(tempColumn))
            {
                sb.append(" and contract_type like '%"+value.toString()+"%'");
            }
            if("searchStrFilter".equals(tempColumn))
            {
                sb.append(" and (region like '%"+value+"%'");
                sb.append(" or  agent like '%"+value+"%'");
                sb.append(" or  contract_no like '%"+value+"%'");
                sb.append(" or  contract_type like '%"+value+"%')");
            }
        }
        return sb.toString();
    }
}
