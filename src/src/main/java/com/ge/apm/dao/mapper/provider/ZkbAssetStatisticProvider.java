package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/10/13
 */
public class ZkbAssetStatisticProvider {

    /**
     * 查出所有设备信息
     * @param param filter 过滤条件
     * @return
     */
    public String loadAllAsset(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT " +
                " ai.system_id," +
                " ti.uid AS tenant_uid," +
                " ti. NAME," +
                " ai.uid AS asset_uid" +
                " FROM" +
                " zkb_contract ac" +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id" +
                " INNER JOIN tenant_info ti ON ti.uid = ac.tenant_uid" +
                " INNER JOIN asset_info ai ON ai.uid= zca.asset_id where 1=1 ");
        String filter = buildFilter_loadAllAsset(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }

    /**
     * 根据过滤条件组装sql
     * @param searchFilterList
     * @return
     */
    public String buildFilter_loadAllAsset(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("tenantUid".equals(tempColumn))
            {
                sb.append(" and ac.tenant_uid='"+value.toString()+"'");
            }
            if("systemId".equals(tempColumn))
            {
                sb.append(" and ai.system_id like'%"+value.toString()+"%'");
            }
        }
        return sb.toString();
    }

    /**
     * 根据资产ID获取所有照片数
     * @param param 资产IDlist
     * @return
     */
    public String loadQrCount(Map<String,Object> param){
        List<String> list = (List<String>) param.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT" +
                " COUNT (bo_uuid) AS qr_code_count," +
                " bo_uuid" +
                " FROM" +
                " v2_blob_object" +
                " WHERE" +
                " 1=1 and bo_uuid in (");
        String filter = buildFilter_loadQrCount(list);
        sb.append(filter);
        sb.append(") GROUP BY bo_uuid");
        return sb.toString();
    }

    public String buildFilter_loadQrCount(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        for (String boUuid: list) {
            sb.append("'"+boUuid+"',");
        }
        String filter=sb.toString();
        filter = filter.substring(0,filter.length()-1);
        return filter;
    }
}
