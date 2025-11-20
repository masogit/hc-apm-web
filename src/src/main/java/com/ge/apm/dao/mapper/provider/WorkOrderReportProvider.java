package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 212605082 on 2017/12/14.
 */
public class WorkOrderReportProvider {

    public String statisticsByDepart(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by ai.clinical_dept_id, oi.name");
        sb.append(" order by closeCount desc");
        return sb.toString();
    }

    public String statisticsByAssetGroup(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ai.asset_group as assetGroup,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai where wo.asset_id=ai.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by ai.asset_group");
        sb.append(" order by closeCount desc");
        return sb.toString();
    }

    public String statisticsByDepartAndAssetGroup(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,ai.asset_group as assetGroup,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by ai.clinical_dept_id, oi.name, ai.asset_group");
        sb.append(" order by closeCount desc");
        return sb.toString();
    }

    public String statisticsByAssetName(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ai.name as assetName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai where wo.asset_id=ai.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by ai.name");
        sb.append(" order by closeCount desc");
        return sb.toString();
    }

    public String statisticsByCaseType(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select wo.case_type as caseType,aft.fault_name as caseTypeName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo, asset_fault_type aft, asset_info ai where wo.case_type = aft.id and wo.asset_id = ai.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by wo.case_type, aft.fault_name");
        sb.append(" order by closeCount desc");
        return sb.toString();
    }

    public String statisticsByStaff(Map<String, String> param) {
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select " +
                "wo.current_person_id as staffId" +
                ",ua.name as staffName,count(wo.id) as totalCount" +
                ",SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount" +
                ",SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount" +
                ",SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount" +
                ",SUM ( wo.man_hours) AS totalManHour" +
                ",SUM ( COALESCE(cow.man_hours, 0) ) AS collaboratedManHour" +
                ",COALESCE(AVG ( uawco.man_hours) , 0) AS collaborateManHour" +
                ",SUM(CASE WHEN wo.status = 2 THEN date(wo.close_time)-date(wo.created_date) ELSE 0 END) as totalSpendTime " +
                " from" +
                " v2_work_order wo" +
                " JOIN user_account ua ON wo.current_person_id = ua.ID " +
                " JOIN asset_info ai ON wo.asset_id = ai.id " +
                " LEFT JOIN ( SELECT wo_id, SUM(man_hours) AS man_hours FROM V2_Work_Order_Coworker GROUP BY wo_id ) cow ON wo.ID = cow.wo_id " +
                " LEFT JOIN ( SELECT sum(wc.man_hours) man_hours, wc.cowoker_user_id FROM V2_Work_Order_Coworker wc WHERE wc.wo_id in ( select wo.id from v2_work_order wo where " +
                " wo.site_uid = '" + siteUID + "' " +
                " AND wo.created_date >= '" + startDate + "' " +
                " AND wo.created_date <= '" + endDate + "' ) GROUP BY wc.cowoker_user_id  ) uawco ON wo.current_person_id = uawco.cowoker_user_id  " +
                " where wo.current_person_id=ua.id");

        buildStatisticsFilter(param, sb);
        sb.append(" group by wo.current_person_id, ua.name");
        sb.append(" order by closeCount desc");
        return sb.toString();

    }

    public String statisticsCaseloadByDepartment(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,sum(wo.total_man_hour) as totalManHour from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");
        buildStatisticsFilter(param, sb);
        sb.append(" group by ai.clinical_dept_id, oi.name");
        sb.append(" order by totalManHour desc");
        return sb.toString();
    }

    public void buildStatisticsFilter(Map<String, String> param, StringBuilder sb) {
        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");
        String assetTypeGroupEncodedStr = param.get("assetTypeGroupEncodedStr");

        if (tenantUID != null) {
            sb.append(" and wo.tenant_uid='").append(tenantUID).append("'");
        }
        if (institutionUID != null) {
            sb.append(" and wo.institution_uid='").append(institutionUID).append("'");
        }
        if (hospitalUID != null) {
            sb.append(" and wo.hospital_uid='").append(hospitalUID).append("'");
        }
        if (siteUID != null) {
            sb.append(" and wo.site_uid='").append(siteUID).append("'");
        }
        if (startDate != null) {
            sb.append(" and wo.created_date>='").append(startDate).append("'");
        }
        if (endDate != null) {
            sb.append(" and wo.created_date<='").append(endDate).append("'");
        }
        if (assetTypeGroupEncodedStr != null) {
            sb.append(" and ai.asset_type in ( '").append(assetTypeGroupEncodedStr).append("')");
        }
    }

    public String workOrderExport(Map<String, Object> param) {

        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");

        StringBuilder sb = new StringBuilder();

        sb.append("select wo.id as id,wo.sr_id as srId,sr.sr_num as srNum,ai.depart_num as assetDepartNum,ai.uid as assetUid," +
                "ai.financing_num as assetFinancingNum,ai.serial_num as assetSerialNum,ai.name as assetName, " +
                "wo.status as status, ai.id as assetId ,ai.risk_grade as riskGrade, " +
                "wo.sub_status as subStatus, " +
                "ai.registration_no as registrationNo, "+
                "ai.warranty_date as warrantyDate, "+
                "ai.factory_warranty_date as factoryWarrantyDate, "+
                "sr.hospital_name as hospitalName,ai.manufacture as assetManufacture, " +
                "ai.make_short_name as assetMakerShortName,ai.qr_code as assetInfoQrCode,sr.asset_location as locationName, " +
                "oi.short_name as assetClinicalDeptName, " +
                "ai.asset_group as assetGroup, " +
                "ai.function_group as assetFunctionGroup, " +
                "ai.asset_code68 as assetCode68, " +
                "ai.function_type as assetFunctionType,wo.current_person_name as currentPersonName, " +
                "wo.current_step_id as currentStepId, " +
                "sr.requestor_name as requestorName,sr.request_time as requestTime,sr.request_reason as requestReason, " +
                "wo.send_step_owner_name as sendStepOwnerName, " +
                "wo.send_step_start_time as sendStepStartTime, " +
                "wo.int_ext_type as intExtType, " +
                "wo.repair_result as repairResult, " +
                "wo.repair_type as repairType, " +
                "wo.accept_step_owner_name as acceptStepOwnerName, " +
                "wo.accept_step_start_time as acceptStepStartTime, " +
                "wo.work_step_end_time as workStepEndTime, " +
                "wo.checkin_time as checkinTime,wo.man_hours as manHours,wo.total_man_hour as totalManHour, " +
                "(select string_agg(concat(wod.parts,concat(',数量:',concat(wod.parts_quantity,concat(',合同价格:',concat(wod.closed_price, ';'))))),'') from v2_work_order_detail wod where wod.wo_id=wo.id GROUP BY wod.wo_id) as parts, " +
                "(select sum(wod.closed_price) from v2_work_order_detail wod where wod.wo_id=wo.id and (wod.approved = true or wod.approved is null) GROUP BY wod.wo_id) as partsTotalPrice, " +
                "wo.other_expense as otherExpense,wo.external_expense as externalExpense,wo.total_price as totalPrice, " +
                "wo.evaluate_step_owner_name as evaluateStepOwnerName, " +
                "wo.evaluate_step_end_time as evaluateStepEndTime," +
                "wo.close_step_owner_name as closeStepOwnerName, " +
                "wo.close_step_end_time as closeStepEndTime, " +
                "(select string_agg(woc.cowoker_user_name,',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworker, " +
                "(select string_agg(to_char(woc.man_hours,'9999990.99'),',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworkManHour, " +
                "sr.nearest_sr_days as nearestWorkDay,sr.confirmed_down_time as confirmedDownTime,sr.confirmed_up_time as confirmedUpTime,wo.feedback_rating as feedbackRating, wo.feedback_comment as feedbackComment,wo.close_time as closeTime, " +
                "(case when sr.requestor_phone is not null then sr.requestor_phone else ua.telephone end) as telephone," +
                "aft.fault_name as caseTypeName, " +
                "ai.status as assetStatus, " +
                "wo.external_quotation as externalQuotation," +
                "wo.signature as signature,wo.ack_signature as ackSignature,wo.maintenance_value as maintenanceValue,wo.pat_problems as patProblems,wo.pat_actions as patActions,wo.pat_tests as patTests,wo.comment as comment, wo.attributes as attributes ," +
                "wo.sieble_number as siebleNumber,wo.call_center_number as callCenterNumber, " +
                "(select string_agg(concat('开始时间:',woh.start_time,'结束时间:', woh.end_time, '工时:', cast(woh.hours as decimal(10,2)),'备注:', woh.remark, ';'),'') from v2_work_order_man_hours_detail woh where woh.wo_id=wo.id GROUP BY woh.wo_id) as workHourDetail, " +
                "wo.entry_type as entryType, " +
                " wo.coworker_entry_type as coworkerEntryType, "+
                " case when wo.coworker_entry_type = 0 THEN (SELECT string_agg(concat('协作者:',woc.cowoker_user_name,CAST(woc.man_hours AS DECIMAL(10,2)),'备注:',woc.remark,';'),'') FROM V2_Work_Order_Coworker woc WHERE woc.wo_id=wo.ID GROUP BY woc.wo_id) " +
                " when wo.coworker_entry_type = 1 THEN (SELECT string_agg(concat('协作者:',woc.cowoker_user_name,'开始时间:', woc.start_time, '结束时间:', woc.end_time,CAST(woc.man_hours AS DECIMAL(10,2)),'备注:',woc.remark,';'),'') FROM V2_Work_Order_Coworker woc WHERE woc.wo_id=wo.ID GROUP BY woc.wo_id) end as coworkerEntryDetail, " +
                "wo.work_order_type as workOrderType, " +
                "wo.main_sub as mainSub, " +
                "ai.spare01,ai.spare02,ai.spare03,ai.spare04,ai.spare05,ai.spare06,ai.spare07,ai.spare08,ai.spare09,ai.spare10 " +
                "from v2_work_order wo " +
                "INNER JOIN v2_service_request sr ON wo.sr_id = sr.id " +
                "INNER JOIN asset_info ai ON ai.id = wo.asset_id " +
                "LEFT JOIN user_account ua ON ua.id = sr.requestor_id " +
                "LEFT JOIN asset_fault_type aft ON aft.id=wo.case_type " +
                "LEFT JOIN org_info oi ON ai.clinical_dept_uid = oi.uid " +
                "WHERE 1=1 "
        );

        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        sb.append(" order by wo.created_date desc ");

        return sb.toString();

    }

    public String workOrderExportForMVS(Map<String, Object> param) {

        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");

        StringBuilder sb = new StringBuilder();

        sb.append("select wo.id as id,wo.sr_id as srId,sr.sr_num as srNum, ai.depart_num as assetDepartNum,ai.id as assetId,ai.financing_num as assetFinancingNum,ai.serial_num as assetSerialNum,ai.name as assetName, " +
                "wo.status as status, " +
                "wo.sub_status as subStatus, " +
                "sr.hospital_name as hospitalName,ai.manufacture as assetManufacture, " +
                "ai.make_short_name as assetMakerShortName,ai.qr_code as assetInfoQrCode,sr.asset_location as locationName, " +
                "oi.short_name as assetClinicalDeptName, " +
                "ai.asset_group as assetGroup, " +
                "ai.function_group as assetFunctionGroup, " +
                "ai.asset_code68 as assetCode68, " +
                "ai.function_type as assetFunctionType,wo.current_person_name as currentPersonName, " +
                "wo.current_step_id as currentStepId, " +
                "sr.requestor_name as requestorName,sr.request_time as requestTime,sr.request_reason as requestReason, " +
                "wo.send_step_owner_name as sendStepOwnerName, " +
                "wo.send_step_start_time as sendStepStartTime, " +
                "wo.int_ext_type as intExtType, " +
                "wo.repair_result as repairResult, " +
                "wo.repair_type as repairType, " +
                "wo.accept_step_owner_name as acceptStepOwnerName, " +
                "wo.accept_step_start_time as acceptStepStartTime, " +
                "wo.work_step_end_time as workStepEndTime, " +
                "wo.checkin_time as checkinTime,wo.man_hours as manHours,wo.total_man_hour as totalManHour, " +
                "(select string_agg(concat(wod.parts,concat(',数量:',concat(wod.parts_quantity,concat(',合同价格:',concat(wod.closed_price, ';'))))),'') from v2_work_order_detail wod where wod.wo_id=wo.id GROUP BY wod.wo_id) as parts, " +
                "(select sum(wod.closed_price) from v2_work_order_detail wod where wod.wo_id=wo.id and (wod.approved = true or wod.approved is null) GROUP BY wod.wo_id) as partsTotalPrice, " +
                "wo.other_expense as otherExpense,wo.external_expense as externalExpense,wo.total_price as totalPrice, " +
                "wo.evaluate_step_owner_name as evaluateStepOwnerName, " +
                "wo.evaluate_step_end_time as evaluateStepEndTime," +
                "wo.close_step_owner_name as closeStepOwnerName, " +
                "wo.close_step_end_time as closeStepEndTime, " +
                "(select string_agg(woc.cowoker_user_name,',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworker, " +
                "(select string_agg(to_char(woc.man_hours,'9999990.99'),',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworkManHour, " +
                "sr.nearest_sr_days as nearestWorkDay,sr.confirmed_down_time as confirmedDownTime,sr.confirmed_up_time as confirmedUpTime,wo.feedback_rating as feedbackRating, " +
                "ua.telephone as telephone," +
                "aft.fault_name as caseTypeName, " +
                "ai.status as assetStatus," +
                "wo.external_quotation as externalQuotation," +
                "wo.signature as signature,wo.ack_signature as ackSignature,wo.maintenance_value as maintenanceValue,wo.pat_problems as patProblems,wo.pat_actions as patActions,wo.pat_tests as patTests,wo.comment as comment, wo.attributes as attributes " +
                "from v2_work_order wo " +
                "INNER JOIN v2_service_request sr ON wo.sr_id = sr.id " +
                "INNER JOIN asset_info ai ON ai.id = wo.asset_id " +
                "LEFT JOIN user_account ua ON ua.id = sr.requestor_id " +
                "LEFT JOIN asset_fault_type aft ON aft.id=wo.case_type " +
                "LEFT JOIN org_info oi ON ai.clinical_dept_uid = oi.uid " +
                "WHERE 1=1 "
        );
        String filter = buildFilterForMVS(searchFilterList);
        sb.append(filter);
        sb.append(" order by wo.created_date desc ");

        return sb.toString();

    }

    private static final String FILTERPARTS_EXIST = "Exist";
    private static final String FILTERPARTS_UNEXIST = "UnExist";
    private static final String FILTERTOTALPRICE_EXIST = "Exist";
    private static final String FILTERTOTALPRICE_UNEXIST = "UnExist";

    public String buildFilter(List<SearchFilter> searchFilterList) {

        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            switch (tempColumn) {
                case "tenantUID":
                    sb.append(" and (wo.tenant_uid='").append(value).append("') ");
                    break;
                case "orgUID":
                    sb.append(" and (wo.tenant_uid='").append(value).append("' or wo.institution_uid='").append(value).append("' or wo.hospital_uid='").append(value).append("' or wo.site_uid='").append(value).append("') ");
                    break;
                case "assetInfo.clinicalDeptUID": {
                    StringBuilder str = new StringBuilder();
                    for (Object obj : (List<Object>) value) {
                        str.append("'").append(obj).append("',");
                    }
                    str = new StringBuilder(str.substring(0, str.length() - 1));
                    sb.append(" and ai.clinical_dept_uid in (").append(str).append(") ");
                    break;
                }
                case "currentPersonId":
                    sb.append(" and wo.current_person_id=").append(value);
                    break;
                case "createdDate":
                    if (op.equals(SearchFilter.Operator.GTE)) {
                        sb.append(" and wo.created_date>='").append(value).append("' ");
                    } else if (op.equals(SearchFilter.Operator.LT)) {
                        sb.append(" and wo.created_date<'").append(value).append("' ");
                    }
                    break;
                case "closeTime":
                    if (op.equals(SearchFilter.Operator.GTE)) {
                        sb.append(" and wo.close_time>='").append(value).append("' ");
                    } else if (op.equals(SearchFilter.Operator.LT)) {
                        sb.append(" and wo.close_time<'").append(value).append("' ");
                    }
                    break;
                case "status": {
                    StringBuilder str = new StringBuilder();
                    for (Object obj : (List<Object>) value) {
                        str.append(obj).append(",");
                    }
                    str = new StringBuilder(str.substring(0, str.length() - 1));
                    sb.append(" and wo.status in (").append(str).append(") ");
                    break;
                }
                case "filterParts":
                    if (FILTERPARTS_EXIST.equals(value)) {
                        sb.append(" and (select count(*) from v2_work_order_detail wod where wod.wo_id=wo.id) > 0 ");
                    } else if (FILTERPARTS_UNEXIST.equals(value)) {
                        sb.append(" and (select count(*) from v2_work_order_detail wod where wod.wo_id=wo.id) <= 0 ");
                    }
                    break;
                case "filterTotalPrice":
                    if (FILTERTOTALPRICE_EXIST.equals(value)) {
                        sb.append(" and wo.total_price > 0 ");
                    } else if (FILTERTOTALPRICE_UNEXIST.equals(value)) {
                        sb.append(" and wo.total_price <= 0 ");
                    }
                    break;
                case "assetInfoId":
                    sb.append(" and ai.id = ").append(value);
                    break;
                case "assetInfo.assetType":
                    StringBuilder str = new StringBuilder();
                    for (Object obj : (Set<Object>) value) {
                        str.append("'").append(obj).append("'").append(",");
                    }
                    str = new StringBuilder(str.substring(0, str.length() - 1));
                    sb.append(" and ai.asset_type in (").append(str).append(") ");
                    break;
                case "srNum":
                    sb.append(" and sr.sr_num = '").append(value).append("'");
                    break;
                case "requestTime":
                    if (op.equals(SearchFilter.Operator.GTE)) {
                        sb.append(" and sr.request_time>='").append(value).append("' ");
                    } else if (op.equals(SearchFilter.Operator.LT)) {
                        sb.append(" and sr.request_time<'").append(value).append("' ");
                    }
                    break;
                default:
                    break;
            }

        }

        return sb.toString();

    }

    public String buildFilterForMVS(List<SearchFilter> searchFilterList) {

        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter1 : searchFilterList) {
            String tempColumn1 = searchFilter1.fieldName;
            Object value1 = searchFilter1.value;
            if (tempColumn1.equals("assetIds")) {
                String str1 = "";
                List<Object> valueList = (List<Object>) value1;
                if (valueList.size() == 0) return null;
                for (Object obj : (List<Object>) value1) {
                    str1 += "'" + obj + "',";
                }
                str1 = str1.substring(0, str1.length() - 1);
                sb.append("and ai.id in (" + str1 + ") ");

                //页面原有的条件
                for (SearchFilter searchFilter : searchFilterList) {
                    String tempColumn = searchFilter.fieldName;
                    Object value = searchFilter.value;
                    SearchFilter.Operator op = searchFilter.operator;
                    /* if(tempColumn.equals("orgUID")){
                        sb.append(" and (ai.tenant_uid='" + value + "' or ai.institution_uid='" + value + "' or ai.hospital_uid='" + value + "' or ai.site_uid='" + value + "') ");
                    }else if(tempColumn.equals("assetInfo.clinicalDeptUID")){
                        String str = "";
                        for (Object obj : (List<Object>)value) {
                            str += "'" + obj + "',";
                        }
                        str = str.substring(0, str.length() - 1);
                        sb.append(" and ai.clinical_dept_uid in (" + str + ") ");
                    }else */
                    if (tempColumn.equals("currentPersonId")) {
                        sb.append(" and wo.current_person_id=" + value);
                    } else if (tempColumn.equals("createdDate")) {
                        if (op.equals(SearchFilter.Operator.GTE)) {
                            sb.append(" and wo.created_date>='" + value + "' ");
                        } else if (op.equals(SearchFilter.Operator.LT)) {
                            sb.append(" and wo.created_date<'" + value + "' ");
                        }
                    } else if (tempColumn.equals("closeTime")) {
                        if (op.equals(SearchFilter.Operator.GTE)) {
                            sb.append(" and wo.close_time>='" + value + "' ");
                        } else if (op.equals(SearchFilter.Operator.LT)) {
                            sb.append(" and wo.close_time<'" + value + "' ");
                        }
                    } else if (tempColumn.equals("status")) {
                        String str = "";
                        for (Object obj : (List<Object>) value) {
                            str += obj + ",";
                        }
                        str = str.substring(0, str.length() - 1);
                        sb.append(" and wo.status in (" + str + ") ");
                    } else if (tempColumn.equals("filterParts")) {
                        if (FILTERPARTS_EXIST.equals(value)) {
                            sb.append(" and (select count(*) from v2_work_order_detail wod where wod.wo_id=wo.id) > 0 ");
                        } else if (FILTERPARTS_UNEXIST.equals(value)) {
                            sb.append(" and (select count(*) from v2_work_order_detail wod where wod.wo_id=wo.id) <= 0 ");
                        }
                    } else if (tempColumn.equals("filterTotalPrice")) {
                        if (FILTERTOTALPRICE_EXIST.equals(value)) {
                            sb.append(" and wo.total_price > 0 ");
                        } else if (FILTERTOTALPRICE_UNEXIST.equals(value)) {
                            sb.append(" and wo.total_price <= 0 ");
                        }
                    }else if(tempColumn.equals("srNum")){
                        sb.append(" and sr.sr_num = '").append(value).append("'");
                    }else if(tempColumn.equals("requestTime")){
                            if (op.equals(SearchFilter.Operator.GTE)) {
                                sb.append(" and sr.request_time>='").append(value).append("' ");
                            } else if (op.equals(SearchFilter.Operator.LT)) {
                                sb.append(" and sr.request_time<'").append(value).append("' ");
                            }
                    }
                }
            }
        }
        return sb.toString();

    }

    public String statisticsAssetWorkOrder(Map<String, Object> param) {

        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ai.id as id,ai.uid as uid,ai.name as name,ai.serial_num as serialNum,ai.financing_num as financingNum, " +
                "ai.function_type as functionType,ai.clinical_dept_uid as clinicalDeptUID,ai.asset_group as assetGroup,ai.qr_code as qrCode, " +
                "count(wo.id) as workOrderCount, " +
                "(SELECT im.value_zh FROM i18n_message im WHERE concat(ai.asset_group,'') = im.msg_key and im.msg_type='assetGroup' and im.site_id in (select max(im1.site_id) from i18n_message im1 where im1.msg_type='assetGroup' and (im1.site_id=-1 or im1.site_id=ai.site_id))) as assetGroupName, " +
                "(SELECT oi.name FROM org_info oi WHERE ai.clinical_dept_uid = oi.uid) as clinicalDeptName " +
                "FROM asset_info ai, v2_work_order wo " +
                "WHERE ai.id=wo.asset_id ");

        sb.append(buildFilter_AssetWorkOrder(searchFilterList));
        sb.append(" GROUP BY ai.id ");
        sb.append(" HAVING count(wo.id) > 0 ");
        sb.append(" ORDER BY workOrderCount DESC ");

        return sb.toString();

    }

    public String buildFilter_AssetWorkOrder(List<SearchFilter> searchFilterList) {
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            if (tempColumn.equals("orgUID")) {
                sb.append(" and (ai.tenant_uid='" + value + "' or ai.institution_uid='" + value + "' or ai.hospital_uid='" + value + "' or ai.site_uid='" + value + "') ");
            } else if (tempColumn.equals("clinicalDeptUID")) {
                String str = "";
                for (Object obj : (List<Object>) value) {
                    str += "'" + obj + "',";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.clinical_dept_uid in (" + str + ") ");
            } else if (tempColumn.equals("assetGroup")) {
                sb.append(" and ai.asset_group = " + value);
            } else if (tempColumn.equals("searchStr")) {
                if (value != null) {
                    String tempValue = value.toString().toLowerCase();
                    sb.append(" and (lower(ai.name) like '%" + tempValue + "%' or lower(ai.function_type) like '%" + tempValue + "%' or lower(ai.serial_num) like '%" + tempValue + "%' or lower(ai.financing_num) like '%" + tempValue + "%' or lower(ai.function_type) like '%" + tempValue + "%' or ai.qr_code like '%" + tempValue + "%') ");
                }
            } else if (tempColumn.equals("createdDateStart")) {
                sb.append(" and wo.created_date>='" + value + "' ");
            } else if (tempColumn.equals("createdDateEnd")) {
                sb.append(" and wo.created_date<'" + value + "' ");
            } else if (tempColumn.equals("assetInfo.assetType")) {
                String str = "";
                for (Object obj : (Set<Object>) value) {
                    str += "'" + obj + "',";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.asset_type in (" + str + ") ");
            }
        }

        return sb.toString();
    }

    public String workOrderDetailExport(Map<String, Object> param) {

        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");

        StringBuilder sb = new StringBuilder();

        sb.append("select wod.id as id,ai.clinical_dept_name as assetClinicalDeptName, ai.name as assetName,ai.depart_num as assetDepartNum,wod.supplier as supplierName,wod.parts as partsName,wod.parts_quantity as partsQuantity, " +
                "wod.closed_price as closedPrice,wod.invoice_num as invoiceNum,wod.invoice_time as invoiceTime,wo.approver_name as approverName,wod.contract_num as contractNum,wod.comment as comment,wod.closed as statementStatus, wod.parts_type, wod.approved  " +
                "from v2_work_order_detail wod, v2_work_order wo, v2_service_request sr, asset_info ai where wod.wo_id = wo.id and wo.sr_id = sr.id and wo.asset_id = ai.id ");

        String filter = buildFilter_WorkOrderDetail(searchFilterList);
        sb.append(filter);
        sb.append(" order by wo.created_date desc ");

        return sb.toString();
    }

    public String buildFilter_WorkOrderDetail(List<SearchFilter> searchFilterList) {
        StringBuilder sb = new StringBuilder();

        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            if (tempColumn.equals("orgUID")) {
                sb.append(" and (wo.tenant_uid='" + value + "' or wo.institution_uid='" + value + "' or wo.hospital_uid='" + value + "' or wo.site_uid='" + value + "') ");
            } else if (tempColumn.equals("assetInfo.clinicalDeptUID")) {
                String str = "";
                for (Object obj : (List<Object>) value) {
                    str += "'" + obj + "',";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.clinical_dept_uid in (" + str + ") ");
            } else if (tempColumn.equals("currentPersonId")) {
                sb.append(" and wo.current_person_id=" + value);
            } else if (tempColumn.equals("createdDate")) {
                if (op.equals(SearchFilter.Operator.GTE)) {
                    sb.append(" and wo.created_date>='" + value + "' ");
                } else if (op.equals(SearchFilter.Operator.LT)) {
                    sb.append(" and wo.created_date<'" + value + "' ");
                }
            } else if (tempColumn.equals("totalPrice")) {
                if (op.equals(SearchFilter.Operator.GTE)) {
                    sb.append(" and (wod.closed_price)>=" + value);
                } else if (op.equals(SearchFilter.Operator.LTE)) {
                    sb.append(" and (wod.closed_price)<=" + value);
                }
            } else if (tempColumn.equals("statementStatus")) {
                if (Boolean.valueOf(value.toString())) {
                    sb.append(" and wod.closed=" + value);
                } else {
                    sb.append(" and (wod.closed=" + value + " or wod.closed is null) ");
                }

            } else if (tempColumn.equals("searchStr")) {
                if (value != null) {
                    String tempValue = value.toString().toLowerCase();
                    sb.append(" and (lower(ai.depart_num) like '%" + tempValue + "%' or lower(sr.sr_num) like '%" + tempValue + "%' or lower(wod.invoice_num) like '%" + tempValue + "%' or lower(wod.supplier) like '%" + tempValue + "%')");
                }
            } else if (tempColumn.equals("assetInfo.assetType")) {
                String str = "";
                for (Object obj : (Set<Object>) value) {
                    str += "'" + obj + "'" + ",";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.asset_type in (" + str + ") ");
            } else if (tempColumn.equals("approvedStatus")) {
                List<String> approvedStatus = (List<String>) value;
                if (!approvedStatus.isEmpty() && approvedStatus.size() < 3) {
                    sb.append("and (");
                    boolean flag = false;
                    if (approvedStatus.contains("1")) {
                        sb.append("wod.approved = true");
                        flag = true;
                    }
                    if (approvedStatus.contains("2")) {
                        sb.append(flag ? " or " : "");
                        sb.append("wod.approved = false");
                        flag = true;
                    }
                    if (approvedStatus.contains("3")) {
                        sb.append(flag ? " or " : "");
                        sb.append("wod.approved is null");
                    }
                    sb.append(")");
                }
            }
        }

        return sb.toString();
    }
}
