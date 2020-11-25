package com.ge.apm.dao.mapper.provider;

import org.apache.commons.lang.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Map;

/**
 * @author : 503189362
 * @date : 2020/11/16
 * @description :
 */
public class ZkbPartOrderProvider {

    public String queryZkbHistory(Map<String, Object> params){
        List<SearchFilter> filters = (List<SearchFilter>) params.get("filters");
        String tenantUID = (String) params.get("tenantUID");
        StringBuilder sb = new StringBuilder();
        sb.append("select p.id, p.part_order_no partOrderNo,a.system_id as systemId,p.sr_num srNum, i.part_no partNo,i.part_name partName,i.part_qty partQty,i.remark partRemark,p.status,p.current_step currentStep from zkb_part_order p inner join asset_info a on a.uid = p.asset_id inner join zkb_part_order_item i on i.part_order_id = p.id ");
        sb.append(" where p.tenant_uid = '"+tenantUID+"' and p.order_type = 'Parts' ");
        String filter = buildFilter_zkbOrderHistoryDetail(filters);
        sb.append(filter);
        sb.append(" order by p.created_date desc ");
        return sb.toString();
    }

    public String buildFilter_zkbOrderHistoryDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if(value == null || StringUtils.isEmpty(value.toString())){
                continue;
            }
            if("sysId".equals(tempColumn)) {
                sb.append("and system_id like'%"+value+"%'");
            }
            if("contractNo".equals(tempColumn)) {
                sb.append(" and contract_no like '%"+value+"%'");
            }
            if("srNum".equals(tempColumn)) {
                sb.append(" and sr_num like '%"+value+"%'");
            }
        }
        return sb.toString();
    }

    public String queryGeHistory(Map<String, Object> params){
        List<SearchFilter> filters = (List<SearchFilter>) params.get("filters");
        String tenantUID = (String) params.get("tenantUID");
        StringBuilder sb = new StringBuilder();
        sb.append("select p.id, p.system_no as systemId,p.sr srNum, spare_parts_no partNo,p.quantity partQty,p.description partRemark,p.status from zkb_order_history p  ");
        sb.append(" where p.tenant_uid = '"+tenantUID+"' ");
        String filter = buildFilter_geOrderHistoryDetail(filters);
        sb.append(filter);
        sb.append(" order by p.sr desc ");
        return sb.toString();
    }

    public String buildFilter_geOrderHistoryDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if(value == null || StringUtils.isEmpty(value.toString())){
                continue;
            }
            if("sysId".equals(tempColumn)) {
                sb.append("and system_no like'%"+value+"%'");
            }
            if("contractNo".equals(tempColumn)) {
                sb.append(" and contract_no like '%"+value+"%'");
            }
            if("srNum".equals(tempColumn)) {
                sb.append(" and sr like '%"+value+"%'");
            }
        }
        return sb.toString();
    }
}
