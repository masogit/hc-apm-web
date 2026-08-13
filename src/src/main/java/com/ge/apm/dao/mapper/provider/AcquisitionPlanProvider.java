package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AcquisitionPlanProvider {


    public String queryAcquisitionPlanListForPage(Map<String, Object> param){
        StringBuilder sb = new StringBuilder();
        sb.append("select application_hospital_name as applicationHospitalName, application_hospital_uid as applicationHospitalUid, application_year as applicationYear, sum(application_plan_count) as applicationPlanCount , sum(budget_total_amount) as budgetTotalAmount  from acquisition_plan where 1=1 " );
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        sb.append(" group by application_hospital_name,applicationHospitalUid, application_year ");
        if (param.get("limit") != null) {
            sb.append(" limit ").append(param.get("limit"));
        }
        if (param.get("offset") != null) {
            sb.append(" offset ").append(param.get("offset"));
        }
        return sb.toString();
    }

    public String queryAcquisitionPlanListForPageCount(Map<String, Object> param){
        StringBuilder sb = new StringBuilder();
        sb.append("select count(1) from acquisition_plan where 1=1 " );
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }


    public String buildFilter(List<SearchFilter> searchFilterList) {
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            switch (tempColumn) {
                case "tenantUID":
                    sb.append(" and tenant_uid = '").append(value).append("'");
                    break;
                case "drugCenterUid":
                    sb.append(" and drug_center_uid = '").append(value).append("'");
                    break;
                case "workGroupUid":
                    sb.append(" and work_group_uid = '").append(value).append("'");
                    break;
                case "bureauPartyUid":
                    sb.append(" and bureau_party_uid = '").append(value).append("'");
                    break;
                case "applicationDeptUid":
                    sb.append(" and application_dept_uid = '").append(value).append("'");
                    break;
                case "applicationHospitalUid":
                    sb.append(" and application_hospital_uid = '").append(value).append("'");
                    break;
                case "applicationYear":
                    sb.append(" and application_year like '%").append(value).append("%'");
                    break;
                case "applicationNo": {
                    sb.append(" and application_no like '%").append(value).append("%'");
                    break;
                }
                case "hospitalName":
                    sb.append(" and application_hospital_uid = '").append(value).append("'");
                    break;
                case "assetName":
                    sb.append(" and asset_name like '%").append(value).append("%'");
                    break;
                case "applicationType": {
                    sb.append(" and application_type = '").append(value).append("'");
                    break;
                }
                case "applicationPlanCount":
                    try{
                        Integer c = Integer.parseInt(value.toString());
                        sb.append(" and application_plan_count > ").append(c);
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                    break;
                case "budgetUnitPrice":
                    BigDecimal c = parseBigDecimal(value.toString());
                    sb.append(" and budget_unit_price > ").append(c);
                    break;
                case "budgetTotalAmount":
                    BigDecimal budgetTotalAmount = parseBigDecimal(value.toString());
                    if (op.equals(SearchFilter.Operator.GTE)) {
                        sb.append(" and budget_total_amount >= ").append(budgetTotalAmount);
                    } else if (op.equals(SearchFilter.Operator.LT)) {
                        sb.append(" and budget_total_amount < ").append(budgetTotalAmount);
                    }
                    break;
                case "estimatedServiceYears":
                    try {
                        Integer estimatedServiceYears = Integer.parseInt(value.toString());
                        sb.append(" and estimated_service_years > ").append(estimatedServiceYears);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case "applicationStatus":
                    if (op.equals(SearchFilter.Operator.IN)) {
                        sb.append(" and application_status in (");
                        for (String status : (List<String>) value) {
                            sb.append("'").append(status).append("',");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(")");
                    }else if (op.equals(SearchFilter.Operator.EQ)){
                        sb.append(" and application_status = '").append(value).append("'");
                    }
                    break;
                case "applicationUserName" :
                    sb.append(" and application_user_name like '%").append(value).append("%'");
                    break;
                case "applicationDate":
                    if (op.equals(SearchFilter.Operator.GTE)) {
                        sb.append(" and application_date >='").append(value).append("' ");
                    } else if (op.equals(SearchFilter.Operator.LT)) {
                        sb.append(" and application_date <'").append(value).append("' ");
                    }
                    break;
                case "applicationHospitalName":
                    sb.append(" and application_hospital_name like '%").append(value).append("%'");
                    break;


                default:
                    break;
            }
        }

        return sb.toString();

    }


    public BigDecimal parseBigDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return BigDecimal.ZERO; // 或者 return null;
        }
        try {
            return new BigDecimal(str.trim());
        } catch (NumberFormatException e) {
            // 处理格式异常
            return BigDecimal.ZERO;
        }
    }
}
