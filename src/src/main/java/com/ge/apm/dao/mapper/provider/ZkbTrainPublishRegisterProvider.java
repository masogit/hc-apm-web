package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ZkbTrainPublishRegisterProvider {

    public String loadUnRegisterDataList(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String tenantUid = (String) param.get("tenantUid");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p.* FROM zkb_train_publish_info p " +
                " WHERE EXISTS (SELECT 1 FROM zkb_train_course_quota q WHERE q.course_id = p.course_id AND q.tenant_uid = '" + tenantUid + "') " +
                "   AND NOT EXISTS (SELECT 1 FROM zkb_train_publish_register r WHERE r.pid = p.id AND r.tenant_uid = '" + tenantUid + "') ");
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
            if("zkbTrainCourse.courseType".equals(tempColumn)) {
                sb.append(" and p.course_id  in (select id from zkb_train_course where course_type = '" + value + "') ");
            }
            if ("signDeadline".equals(tempColumn)) {
                if (value instanceof Date) {
                    String formatted = new SimpleDateFormat("yyyy-MM-dd").format(value);
                    sb.append(" AND p.sign_deadline >= '").append(formatted).append("'");
                } else {
                    sb.append(" AND p.sign_deadline >= '").append(value.toString()).append("'");
                }
            }
        }

        return sb.toString();
    }



}
