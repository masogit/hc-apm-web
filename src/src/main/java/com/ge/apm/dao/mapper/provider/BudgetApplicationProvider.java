package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BudgetApplicationProvider {

    public String loadDataList(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        StringBuilder sb = new StringBuilder();

        sb.append(" SELECT t.id," +
                "  t.application_num, " +
                "  baa.application_asset_name," +
                "  baa.application_amount," +
                "  baa.application_price," +
                "  baa.application_quantity," +
                "  ua.name as applicantName," +
                "  t.application_date," +
                "  t.application_status," +
                "  t.application_step," +
                "  uap.name as approveName," +
                "  t.approval_date" +
                "  from budget_application t" +
                "  inner join budget_application_asset baa on t.id = baa.application_id" +
                "  left join user_account ua on t.applicant_id = ua.id" +
                "  left join user_account uap on t.approver_id = uap.id " +
                "  where 1=1 ");
        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }

    public String buildFilter(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if("orderType".equals(tempColumn)) {
                sb.append(" and t.order_type = '"+value.toString()+"'");
            }
            if("isDeleted".equals(tempColumn)) {
                sb.append(" and t.is_deleted = "+value+"");
            }
            if("applicationStatus".equals(tempColumn)) {
                sb.append(" and t.application_status in "+getSetValue(value)+"");
            }
            if("applicationStep".equals(tempColumn)) {
                sb.append(" and t.application_step = '"+value.toString()+"'");
            }

            if("applicationDeptId".equals(tempColumn)) {
                sb.append(" and t.applicant_dept_id = "+value+"");
            }
            if("applicationDate".equals(tempColumn)) {
                if(SearchFilter.Operator.GTE.toString().equals(searchFilter.operator.toString())){
                    sb.append(" and t.application_date >= to_date('"+value.toString()+"','yyyyMMdd')");
                }
                if(SearchFilter.Operator.LT.toString().equals(searchFilter.operator.toString())){
                    sb.append(" and t.application_date <= to_date('"+value.toString()+"','yyyyMMdd')");
                }
                if(SearchFilter.Operator.EQ.toString().equals(searchFilter.operator.toString())){
                    sb.append(" and to_char(t.application_date,'yyyy') = to_char(to_date('"+value.toString()+"','yyyyMMdd'),'yyyy')");
                }
            }
            if("applicantName".equals(tempColumn)) {
                sb.append(" and ua.name = '"+value.toString()+"'");
            }

            if("applicationAssetName".equals(tempColumn)) {
                sb.append(" and baa.application_asset_name like '%"+value.toString()+"%'");
            }
            if("applicationNum".equals(tempColumn)) {
                sb.append(" and t.application_num = '"+value.toString()+"'");
            }
        }
        sb.append(" order by t.created_date desc");
        return sb.toString();
    }

    private String getSetValue(Object value){
        StringBuilder sb=new StringBuilder();
        Collection<Integer> set=(Collection<Integer>)value;
        if(set!=null){
            Iterator<Integer> it=set.iterator();
            sb.append("(");
            while(it.hasNext()){
                sb.append(it.next());
                sb.append(",");
            }
            sb.append(999);
            sb.append(")");
        }
        return sb.toString();
    }
}
