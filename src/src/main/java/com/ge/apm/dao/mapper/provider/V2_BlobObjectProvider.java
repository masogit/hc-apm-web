package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/8
 */
public class V2_BlobObjectProvider {

    public String loadV2_BlobObject(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String tenantUid = (String) param.get("tenantUid");
        String boType = (String) param.get("boType");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT" +
                " bo.id," +
                " bo.bo_id," +
                " bo.bo_type," +
                " bo.bo_uuid," +
                " bo.object_name," +
                " bo.object_size," +
                " bo.object_storage_id," +
                " bo.object_type," +
                " bo.created_by," +
                " bo.created_date," +
                " bo.last_modified_by," +
                " bo.last_modified_date," +
                " bo.bo_sub_type," +
                " bo.ext_link," +
                " bo.operation_type," +
                " bo.main_directory," +
                " bo.second_directory" +
                " FROM" +
                " v2_blob_object bo" +
                " INNER JOIN (SELECT" +
                " main_directory," +
                " second_directory" +
                " FROM" +
                " asset_info" +
                " WHERE" +
                " tenant_uid = '"+tenantUid+"'" +
                " AND main_directory IS NOT NULL" +
                " AND second_directory IS NOT NULL" +
                " GROUP BY" +
                " main_directory," +
                " second_directory) ai ON bo.main_directory = ai.main_directory" +
                " AND bo.second_directory = ai.second_directory" +
                " WHERE bo.bo_type ='"+boType+"'");
        String filter = buildFilter_V2_BlobObjectDetail(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }

    public String buildFilter_V2_BlobObjectDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("mainDirectory".equals(tempColumn))
            {
                sb.append(" and bo.main_directory like '%"+value.toString()+"%'");
            }
            if("secondDirectory".equals(tempColumn))
            {
                sb.append(" and bo.second_directory like '%"+value.toString()+"%'");
            }
            if("objectName".equals(tempColumn))
            {
                sb.append(" and bo.object_name like '%"+value.toString()+"%'");
            }
            if("boSubType".equals(tempColumn))
            {
                sb.append(" and bo.bo_sub_type = '"+value.toString()+"'");
            }
            if("operationType".equals(tempColumn))
            {
                sb.append(" and bo.operation_type = '"+value.toString()+"'");
            }
            if("tenantUid".equals(tempColumn))
            {
                sb.append(" and ai.tenant_uid = '"+value.toString()+"'");
            }
        }
        return sb.toString();
    }
}
