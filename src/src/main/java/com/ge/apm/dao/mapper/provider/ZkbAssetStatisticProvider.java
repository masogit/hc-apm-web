package com.ge.apm.dao.mapper.provider;

import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/10/13
 */
public class ZkbAssetStatisticProvider {

    public static final String dateFormat = "yyyyMMdd";

    /**
     * 查出所有设备信息
     * @param param filter 过滤条件
     * @return
     */
    public String loadAllAsset(Map<String,Object> param){
        List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
        String currentDate = this.getDateStr();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT " +
                " ai.id,"+
                " ai.clinical_dept_name as hospital_name, "+
                " ai.function_type, "+
                " ai.system_id," +
                " ti.uid AS tenant_uid," +
                " ti. NAME," +
                " ai.uid AS asset_uid," +
                " ac.contract_no,"+
                " qr_code_info.reason AS reason," +
                " qr_code_info.can_not_past_qr_code AS can_not_past_qr_code,"+
                " ib_qr_code.is_qr_code"+
                " FROM" +
                " zkb_contract ac" +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id" +
                " INNER JOIN tenant_info ti ON ti.uid = ac.tenant_uid" +
                " INNER JOIN asset_info ai ON ai.uid= zca.asset_id" +
                " LEFT JOIN zkb_asset_qr_code_info qr_code_info ON qr_code_info.system_id= ai.system_id and ti.uid = qr_code_info.tenant_uid " +
                " LEFT JOIN zkb_asset_qr_code_from_ib ib_qr_code on ib_qr_code.asset_uid = ai.uid " +
                " where ac.contract_no is not null and ai.system_id is not null"+
                        " and to_date('"+currentDate+"','"+dateFormat+"')>=ac.start_date and to_date('"+currentDate+"','"+dateFormat+"')<=ac.end_date"
                );
        String filter = buildFilter_loadAllAsset(searchFilterList);
        sb.append(filter);
        return sb.toString();
    }
    private String getDateStr()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(new Date());
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
            if("contractNo".equals(tempColumn))
            {
                sb.append(" and ac.contract_no like '%"+value.toString()+"%'");
            }
            if("hospitalName".equals(tempColumn))
            {
                sb.append(" and ai.clinical_dept_name like'%"+value.toString()+"%'");
            }
            if("functionType".equals(tempColumn))
            {
                sb.append(" and ai.function_type like'%"+value.toString()+"%'");
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
                " bo_sub_type = '1' and bo_uuid in (");
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

    /**
     * 查出所有合同相关联设备需要进行二维码贴码的信息
     * @return
     */
    public String getZkbAssetQrCodeInfoList(Map<String,Object> param){
        String currentDate = getDateStr();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT qr_code_info.id,"+
                " ai.clinical_dept_name as hospital_name, "+
                " ai.clinical_dept_uid,"+
                " ai.function_type, "+
                " ai.system_id," +
                " ti.uid AS tenant_uid," +
                " ti. NAME AS tenant_name," +
                " ai.uid AS asset_uid," +
                " ac.contract_no,"+
                " qr_code_info.reason AS reason," +
                " qr_code_info.can_not_past_qr_code," +
                " qr_code_info.reason_date,"+
                " qr_code_info.qr_code_count_state,qr_code_info.ib_qr_code_state,qr_code_info.qr_code_state,qr_code_info.created_date,qr_code_info.qr_code_date,ib_qr_code.is_qr_code AS ibQrCodeTemporary"+
                " FROM" +
                " zkb_contract ac" +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id" +
                " INNER JOIN tenant_info ti ON ti.uid = ac.tenant_uid" +
                " INNER JOIN asset_info ai ON ai.uid= zca.asset_id AND ai.is_deleted=false AND ai.is_valid=true" +
                " LEFT JOIN zkb_asset_qr_code_info qr_code_info ON qr_code_info.system_id= ai.system_id and ti.uid = qr_code_info.tenant_uid " +
                " LEFT JOIN zkb_asset_qr_code_from_ib ib_qr_code on ib_qr_code.asset_uid = ai.uid " +
                " WHERE" +
                " ac.contract_no is not null AND ai.system_id is not null" +
                " AND to_date('"+currentDate+"','"+dateFormat+"')>=ac.start_date and to_date('"+currentDate+"','"+dateFormat+"')<=ac.end_date" +
                " AND (qr_code_info.qr_code_count_state is null OR qr_code_info.qr_code_count_state='N') " +
                " AND (qr_code_info.can_not_past_qr_code is null OR qr_code_info.can_not_past_qr_code='N')"
        );
        if(param != null){
            List<SearchFilter> searchFilterList = (List<SearchFilter>) param.get("filters");
            String filter = buildFilter_loadAllAsset(searchFilterList);
            sb.append(filter);
        }

        return sb.toString();
    }

    public String getZkbAssetQrCode(){
        StringBuilder sb = new StringBuilder();
        String currentDate = getDateStr();
        sb.append("SELECT DISTINCT qr_code_info.id,"+
                        " ai.clinical_dept_name as hospital_name, "+
                        " ai.clinical_dept_uid,"+
                        " ai.function_type, "+
                        " ai.system_id," +
                        " ti.uid AS tenant_uid," +
                        " ti. NAME AS tenant_name," +
                        " ai.uid AS asset_uid," +
                        " (select ac.contract_no from zkb_contract ac,zkb_contract_asset zca where zca.contract_id=ac.ID and ai.uid=zca.asset_id and to_date('"+currentDate+"','yyyyMMdd')>=ac.start_date and to_date('"+currentDate+"','yyyyMMdd')<=ac.end_date) as contract_no,"+
                        " qr_code_info.reason AS reason," +
                        " qr_code_info.can_not_past_qr_code," +
                        " qr_code_info.reason_date,"+
                        " qr_code_info.qr_code_count_state,qr_code_info.ib_qr_code_state,qr_code_info.qr_code_state,qr_code_info.created_date,qr_code_info.qr_code_date" +
                " FROM" +
                " zkb_asset_qr_code_info qr_code_info" +
                " INNER JOIN tenant_info ti ON ti.uid=qr_code_info.tenant_uid" +
                " INNER JOIN asset_info ai ON ai.system_id=qr_code_info.system_id AND ai.tenant_uid=qr_code_info.tenant_uid"+
                " WHERE qr_code_info.tenant_name isnull"
        );
        return sb.toString();
    }
}
