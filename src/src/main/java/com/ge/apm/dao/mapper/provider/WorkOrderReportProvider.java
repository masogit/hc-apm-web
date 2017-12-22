package com.ge.apm.dao.mapper.provider;

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
}
