package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/12/14.
 */
public class WorkOrderReportProvider {

    public String statisticsByDepart(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by ai.clinical_dept_id, oi.name");
        sb.append(" order by closeCount desc");

        return sb.toString();
    }

    public String statisticsByAssetGroup(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select ai.asset_group as assetGroup,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai where wo.asset_id=ai.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by ai.asset_group");
        sb.append(" order by closeCount desc");

        return sb.toString();
    }

    public String statisticsByDepartAndAssetGroup(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,ai.asset_group as assetGroup,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by ai.clinical_dept_id, oi.name, ai.asset_group");
        sb.append(" order by closeCount desc");

        return sb.toString();
    }

    public String statisticsByAssetName(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select ai.name as assetName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_info ai where wo.asset_id=ai.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by ai.name");
        sb.append(" order by closeCount desc");

        return sb.toString();
    }

    public String statisticsByCaseType(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select wo.case_type as caseType,aft.fault_name as caseTypeName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount from v2_work_order wo,asset_fault_type aft where wo.case_type=aft.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by wo.case_type, aft.fault_name");
        sb.append(" order by closeCount desc");

        return sb.toString();
    }

    public String statisticsByStaff(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();
        sb.append("select wo.current_person_id as staffId,ua.name as staffName,count(wo.id) as totalCount,SUM(CASE WHEN wo.status = 1 THEN 1 ELSE 0 END) as openCount,SUM(CASE WHEN wo.status = 2 THEN 1 ELSE 0 END) as closeCount,SUM(CASE WHEN wo.status = 3 THEN 1 ELSE 0 END) as cleanCount,sum(wo.total_man_hour) as totalManHour,SUM(CASE WHEN wo.status = 2 THEN date(wo.close_time)-date(wo.created_date) ELSE 0 END) as totalSpendTime from v2_work_order wo,user_account ua where wo.current_person_id=ua.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by wo.current_person_id, ua.name");
        sb.append(" order by closeCount desc");

        return sb.toString();

    }

    public String statisticsCaseloadByDepartment(Map<String,String> param){

        String tenantUID = param.get("tenantUID");
        String institutionUID = param.get("institutionUID");
        String hospitalUID = param.get("hospitalUID");
        String siteUID = param.get("siteUID");
        String startDate = param.get("startDate");
        String endDate = param.get("endDate");

        StringBuilder sb = new StringBuilder();

        sb.append("select ai.clinical_dept_id as clinicalDeptId,oi.name as clinicalDeptName,sum(wo.total_man_hour) as totalManHour from v2_work_order wo,asset_info ai,org_info oi where wo.asset_id=ai.id and ai.clinical_dept_id=oi.id");

        if(tenantUID != null){
            sb.append(" and wo.tenant_uid='"+tenantUID+"'");
        }
        if(institutionUID != null){
            sb.append(" and wo.institution_uid='"+institutionUID+"'");
        }
        if(hospitalUID != null){
            sb.append(" and wo.hospital_uid='"+hospitalUID+"'");
        }
        if(siteUID != null){
            sb.append(" and wo.site_uid='"+siteUID+"'");
        }
        if(startDate != null){
            sb.append(" and wo.created_date>='"+startDate+"'");
        }
        if(endDate != null){
            sb.append(" and wo.created_date<='"+endDate+"'");
        }

        sb.append(" group by ai.clinical_dept_id, oi.name");
        sb.append(" order by totalManHour desc");

        return sb.toString();
    }

    public String workOrderExport(Map<String,Object> param){

        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");

        StringBuilder sb = new StringBuilder();

        sb.append("select wo.id as id,sr.sr_num as srNum,ai.depart_num as assetDepartNum,ai.financing_num as assetFinancingNum,ai.serial_num as assetSerialNum,ai.name as assetName, " +
            "(select im.value_zh from i18n_message im where concat(wo.status,'') = im.msg_key and im.msg_type='status') as statusName, " +
            "sr.hospital_name as hospitalName,ai.manufacture as assetManufacture, " +
            "ai.make_short_name as assetMakerShortName,ai.qr_code as assetInfoQrCode,sr.asset_location as locationName, " +
            "(select oi.short_name from org_info oi where ai.clinical_dept_uid = oi.uid) as assetClinicalDeptName, " +
            "(select im.value_zh from i18n_message im where concat(ai.asset_group,'') = im.msg_key and im.msg_type='assetGroup' and im.site_id in (select max(im1.site_id) from i18n_message im1 where im1.msg_type='assetGroup' and (im1.site_id=-1 or im1.site_id=ai.site_id))) as assetGroupName, " +
            "(select im.value_zh from i18n_message im where concat(ai.function_group,'') = im.msg_key and im.msg_type='assetFunctionType') as assetFunctionGroupName, " +
            "(select im.value_zh from i18n_message im where concat(ai.asset_code68,'') = im.msg_key and im.msg_type=(select im.msg_key from i18n_message im where concat(ai.function_group,'') = im.msg_key and im.msg_type='assetFunctionType')) as assetCode68Name, " +
            "ai.function_type as assetFunctionType,wo.current_person_name as currentPersonName, " +
            "(select im.value_zh from i18n_message im where concat(wo.current_step_id,'') = im.msg_key and im.msg_type='woSteps') as currentStepName, " +
            "sr.requestor_name as requestorName,sr.request_time as requestTime,sr.request_reason as requestReason, " +
            "(select wos.owner_name from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 2 order by wos.start_time desc limit 1) as sendStepOwnerName, " +
            "(select wos.end_time from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 2 order by wos.start_time desc limit 1) as sendStepStartTime, " +
            "(select im.value_zh from i18n_message im where concat(wo.int_ext_type,'') = im.msg_key and im.msg_type='intExtType') as intExtTypeName, " +
            "(select im.value_zh from i18n_message im where concat(wo.repair_type,'') = im.msg_key and im.msg_type='repairType') as repairTypeName, " +
            "(select wos.owner_name from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 4 order by wos.start_time asc limit 1) as acceptStepOwnerName, " +
            "(select wos.start_time from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 4 order by wos.start_time asc limit 1) as acceptStepStartTime, " +
            "(select wos.end_time from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 4 order by wos.start_time desc limit 1) as workStepEndTime, " +
            "wo.checkin_time as checkinTime,wo.man_hours as manHours,wo.total_man_hour as totalManHour, " +
            "(select string_agg(concat(wod.parts,concat(',数量:',concat(wod.parts_quantity,concat(',合同价格:',concat(wod.closed_price, ';'))))),'') from v2_work_order_detail wod where wod.wo_id=wo.id GROUP BY wod.wo_id) as parts, " +
            "(select sum(wod.parts_quantity * wod.closed_price) from v2_work_order_detail wod where wod.wo_id=wo.id GROUP BY wod.wo_id) as partsTotalPrice, " +
            "wo.other_expense as otherExpense,wo.external_expense as externalExpense,wo.total_price as totalPrice, " +
            "(select wos.owner_name from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 5 order by wos.start_time desc limit 1) as evaluateStepOwnerName, " +
            "(select wos.end_time from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 5 order by wos.start_time desc limit 1) as evaluateStepEndTime, " +
            "(select wos.owner_name from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 6 order by wos.start_time desc limit 1) as closeStepOwnerName, " +
            "(select wos.end_time from v2_work_order_step wos where wos.wo_id = wo.id and wos.step_id = 6 order by wos.start_time desc limit 1) as closeStepEndTime, " +
            "(select string_agg(woc.cowoker_user_name,',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworker, " +
            "(select string_agg(concat(woc.man_hours),',') from v2_work_order_coworker woc where woc.wo_id=wo.id GROUP BY woc.wo_id) as coworkManHour, " +
            "sr.nearest_sr_days as nearestWorkDay,sr.confirmed_down_time as confirmedDownTime,sr.confirmed_up_time as confirmedUpTime,wo.feedback_rating as feedbackRating, " +
            "(select ua.telephone from user_account ua where ua.id = sr.requestor_id) as telephone," +
            "(select aft.fault_name from asset_fault_type aft where aft.id=wo.case_type) as caseTypeName, " +
            "(select im.value_zh from i18n_message im where concat(ai.status,'') = im.msg_key and im.msg_type='assetStatus') as assetStatusName," +
            "wo.signature as signature,wo.ack_signature as ackSignature,wo.pat_problems as patProblems,wo.pat_actions as patActions,wo.pat_tests as patTests,wo.comment as comment " +
            "from v2_work_order wo, v2_service_request sr, asset_info ai " +
            "where wo.sr_id = sr.id and ai.id = wo.asset_id "
        );

        String filter = buildFilter(searchFilterList);
        sb.append(filter);
        sb.append(" order by wo.created_date desc ");

        return sb.toString();

    }

    public String buildFilter(List<SearchFilter> searchFilterList){

        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            if(tempColumn.equals("orgUID")){
                sb.append(" and (wo.tenant_uid='" + value + "' or wo.institution_uid='" + value + "' or wo.hospital_uid='" + value + "' or wo.site_uid='" + value + "') ");
            }else if(tempColumn.equals("assetInfo.clinicalDeptUID")){
                String str = "";
                for (Object obj : (List<Object>)value) {
                    str += "'" + obj + "',";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.clinical_dept_uid in (" + str + ") ");
            }else if(tempColumn.equals("currentPersonId")){
                sb.append(" and wo.current_person_id=" + value);
            }else if(tempColumn.equals("createdDate")){
                if(op.equals(SearchFilter.Operator.GTE)){
                    sb.append(" and wo.created_date>='" + value + "' ");
                }else if (op.equals(SearchFilter.Operator.LT)) {
                    sb.append(" and wo.created_date<'" + value + "' ");
                }
            }else if(tempColumn.equals("closeTime")){
                if(op.equals(SearchFilter.Operator.GTE)){
                    sb.append(" and wo.close_time>='" + value + "' ");
                }else if (op.equals(SearchFilter.Operator.LT)){
                    sb.append(" and wo.close_time<'" + value + "' ");
                }
            }else if(tempColumn.equals("status")){
                String str = "";
                for (Object obj : (List<Object>)value) {
                    str += obj + ",";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and wo.status in (" + str + ") ");
            }

        }

        return sb.toString();

    }

    public String statisticsAssetWorkOrder(Map<String,Object> param){

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

    public String buildFilter_AssetWorkOrder(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            SearchFilter.Operator op = searchFilter.operator;

            if(tempColumn.equals("orgUID")){
                sb.append(" and (ai.tenant_uid='" + value + "' or ai.institution_uid='" + value + "' or ai.hospital_uid='" + value + "' or ai.site_uid='" + value + "') ");
            }else if(tempColumn.equals("clinicalDeptUID")){
                String str = "";
                for (Object obj : (List<Object>)value) {
                    str += "'" + obj + "',";
                }
                str = str.substring(0, str.length() - 1);
                sb.append(" and ai.clinical_dept_uid in (" + str + ") ");
            }else if(tempColumn.equals("assetGroup")){
                sb.append(" and ai.asset_group = " + value);
            }else if(tempColumn.equals("searchStr")){
                if(value != null){
                    String tempValue = value.toString().toLowerCase();
                    sb.append(" and (lower(ai.name) like '%" + tempValue + "%' or lower(ai.function_type) like '%" + tempValue + "%' or lower(ai.serial_num) like '%" + tempValue + "%' or lower(ai.financing_num) like '%" + tempValue + "%' or lower(ai.function_type) like '%" + tempValue + "%' or ai.qr_code like '%" + tempValue + "%') ");
                }
            }else if(tempColumn.equals("createdDateStart")){
                sb.append(" and wo.created_date>='" + value + "' ");
            }else if(tempColumn.equals("createdDateEnd")){
                sb.append(" and wo.created_date<'" + value + "' ");
            }
        }

        return sb.toString();
    }
}
