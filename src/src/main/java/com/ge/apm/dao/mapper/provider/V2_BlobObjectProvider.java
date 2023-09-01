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
        sb.append(" SELECT * from ( SELECT" +
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
                " WHERE exists(select main_directory, second_directory" +
                " FROM ( (SELECT main_directory, second_directory" +
                " FROM asset_info WHERE tenant_uid = '"+tenantUid+"'" +
                " AND main_directory IS NOT NULL AND second_directory IS NOT NULL" +
                " GROUP BY main_directory, second_directory)" +
                " union all" +
                " (SELECT 'AW' as main_directory, 'AW' as second_directory from asset_info" +
                " where asset_group = '1' and tenant_uid = '"+tenantUid+"' limit 1)) directory" +
                " where directory.main_directory = bo.main_directory " +
                " and directory.second_directory = bo.second_directory)" +
                " and bo.bo_type ='"+boType+"'"+
                " UNION" +
                        " SELECT" +
                        " bo. ID," +
                        " bo.bo_id," +
                        " bo.bo_type," +
                        " bo.bo_uuid, " +
                        " bo.object_name, " +
                        " bo.object_size, " +
                        " bo.object_storage_id, " +
                        " bo.object_type, " +
                        " bo.created_by, " +
                        " bo.created_date, " +
                        " bo.last_modified_by, " +
                        " bo.last_modified_date, " +
                        " bo.bo_sub_type, " +
                        " bo.ext_link, " +
                        " bo.operation_type, " +
                        " bo.main_directory, " +
                        " bo.second_directory " +
                        "FROM" +
                        " v2_blob_object bo WHERE bo.bo_type = '"+boType+"' AND (bo.main_directory='ALL' AND bo.second_directory='ALL')) as bo where 1=1");
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
//            if("tenantUid".equals(tempColumn))
//            {
//                sb.append(" and ai.tenant_uid = '"+value.toString()+"'");
//            }
        }
        return sb.toString();
    }
}
