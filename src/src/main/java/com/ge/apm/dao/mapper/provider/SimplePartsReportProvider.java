package com.ge.apm.dao.mapper.provider;

import org.apache.commons.lang3.StringUtils;


/**
 * @author by lyj 503277717
 * @date 2022/5/19.
 */
public class SimplePartsReportProvider {

    public String querySimplePartsReportList(String startDate,String endDate,String tenantUID ){
        StringBuilder sb = new StringBuilder();
        sb.append("select spr.maintenance_work_order_number as workOrderNo,vsr.status as workOrderStatus,t.total_prices as partsPrice,t.transaction_qty as partQuantity,t.name as partsName, ");
        sb.append(" spi.item_code as partsCode, t.parts_description as partsDescribe,t.created_date as outbounddate,spw.name as OutboundWarehouse, spwl.name as OutboundPosition ");
        sb.append(" from simple_parts_transaction t left join simple_parts_warehouse spw on t.parts_warehouse_id = spw.id ");
        sb.append(" left join simple_parts_warehouse_locator spwl on t.parts_locator_id = spwl.id ");
        sb.append(" left join simple_parts_item spi on t.parts_item_id = spi.id ");
        sb.append(" left join simple_parts_request spr on t.parts_request_id = spr.id ");
        sb.append(" left join v2_service_request vsr on vsr.sr_num = spr.maintenance_work_order_number where t.request_type ='1' "); //申请类型： 1.申领单 2.申购单
        if(StringUtils.isNotBlank(startDate)){
            sb.append(" and t.created_date>='"+startDate+"'");
        }
        if(StringUtils.isNotBlank(endDate)){
            sb.append(" and t.created_date<='"+endDate+"'");
        }
        if(StringUtils.isNotBlank(tenantUID)){
            sb.append(" and t.tenant_uid ='"+tenantUID+"'");
        }
        sb.append(" order by t.created_date desc");
        return sb.toString();
    }
}
