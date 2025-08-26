package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

public class ZkbTrainPublishRegisterProvider {

    public String loadUnRegisterDataList(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String tenantUid = (String) param.get("tenantUid");
        StringBuilder sb = new StringBuilder();
        sb.append(" select p.* from zkb_train_publish_info p inner join zkb_train_course_quota q on p.course_id = q.course_id  " +
                " where q.tenant_uid = '" + tenantUid +"' and  p.id not in  (select pid from zkb_train_publish_register r where tenant_uid = '" + tenantUid +"') ");
        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }

    public String buildFilter(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;

            if("tenantUid".equals(tempColumn)) {
                continue;
            }
        }

        return sb.toString();
    }



}
