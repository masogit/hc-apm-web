package com.ge.apm.dao.mapper.provider;

import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/23
 */
public class ZkbContractProvider {

    public String getContractByTenantUIDList(Map<String,Object> param){
        List<String> tenantUIDList = (List<String>) param.get("tenantUIDList");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT " +
                " zkbc.*, tenant. NAME AS tenant_name " +
                " FROM " +
                " zkb_contract zkbc " +
                " INNER JOIN tenant_info tenant ON tenant.uid = zkbc.tenant_uid ");
        sb.append(" where ");
        sb.append(buildFilterByTenantUIDList(tenantUIDList));
        return sb.toString();
    }

    public String buildFilterByTenantUIDList(List<String> tenantUIDList){
        StringBuilder sb = new StringBuilder();
        sb.append(" uid in (");
        for (String tenantUID : tenantUIDList) {
            sb.append("'");
            sb.append(tenantUID);
            sb.append("'");
            sb.append(",");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        sb.append(")");
        return sb.toString();
    }

}
