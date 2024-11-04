package com.ge.apm.dao.mapper.provider;

import com.ge.apm.domain.PurchaseInfo;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.service.zkb.CopyByInvok;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
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
        sb.append("select p.id, p.part_order_no partOrderNo,a.system_id as systemId,p.sr_num srNum, " );
        sb.append("i.part_no partNo,i.part_name partName,i.part_qty partQty,i.remark partRemark,p.status,p.current_step currentStep, " );
        sb.append(" case when u.id is not null then to_char(u.created_date,'yyyy-MM-dd') || '判定不真实' else '' end as unrealStatus, u.reason ");
        sb.append("from zkb_part_order p inner join asset_info a on a.uid = p.asset_id inner join zkb_part_order_item i on i.part_order_id = p.id ");
        sb.append(" left join zkb_unreal u on u.part_order_id = p.id and u.item_no = i.part_no ");
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

    /**
     * 根据systemId 和 sr查询FE人工订单
     * @param params
     * @return
     */
    public String queryBySystemIdAndSr(Map<String, Object> params){
        String systemId = (String) params.get("systemId");
        String sr = (String) params.get("sr");
        String partOrderNo = (String) params.get("partOrderNo");
        Integer assetGroup = (Integer) params.get("assetGroup");
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT o.*,a.system_id from zkb_part_order o " +
                " INNER JOIN asset_info a on o.asset_id = a.uid " +
                " where o.status = '"+ ZkbPartOrder.PartOrderStatus.Ordered.toString()+"' " +
                " and o.order_type='"+ZkbPartOrder.OrderType.Artificial.toString()+"' "+
                " and o.current_step='Complete' ").append(this.buildFilter_queryBySystemIdAndSr(systemId,sr,partOrderNo,assetGroup));
        return sb.toString();
    }
    public String buildFilter_queryBySystemIdAndSr(String systemId,String sr,String partOrderNo,Integer assetGroup){
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotBlank(systemId))
        {
            sb.append(" and a.system_id = '"+systemId+"'");
        }
        if(StringUtils.isNotBlank(sr))
        {
            sb.append(" and o.sr_no like '%"+sr+"%'");
        }
        if(StringUtils.isNotBlank(partOrderNo))
        {
            sb.append(" and o.part_order_no = '"+partOrderNo+"'");
        }
        if(assetGroup!=null) {
            sb.append(" and a.asset_group ="+assetGroup.intValue());
        }
        return sb.toString();
    }

    public String batchExport(Map<String,String> param){
        String testLogin = param.get("testLogin");//是否测试账号登录
        String tenantUid = param.get("tenantUid");
        StringBuffer sql = new StringBuffer("SELECT * from zkb_part_order where 1=1");
        if(StringUtils.isNotBlank(tenantUid))
            sql.append(" and tenant_uid = '"+tenantUid+"' ");
        if("f".equals(testLogin))
            sql.append(" and data_type is null ");
        sql.append(" order by request_date desc");
        return sql.toString();
    }

    public String queryPendCountByUserConfig(Map<String,Object> param){
        //Map<String,Object> param = (Map<String,Object>)params.get("params");
        String tenantUid = param.get("tenantUid") == null ? "" : (String)param.get("tenantUid");
        String testLogin = param.get("testLogin") == null ? "" : (String)param.get("testLogin");
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from zkb_part_order where 1=1 ");
        if(StringUtils.isNotBlank(tenantUid)) {
            sql.append(" and tenant_uid = '" + tenantUid + "' ");
        }
        if(CopyByInvok.T.equals(testLogin)){
            sql.append(" and data_type = 'test.'" );
        }else{
            sql.append(" and data_type is null ");
        }
        String currentStep = param.get("currentStep") == null ? "" : (String)param.get("currentStep");

        String onlineEngineer = param.get("onlineEngineer") == null ? "" : (String)param.get("onlineEngineer");
        if(StrUtil.isNotEmpty(onlineEngineer)){
            sql.append(" and online_engineer = '" + onlineEngineer + "'");
        }
        String assetTypeDi = param.get("assetTypeDi") == null ? "" : (String)param.get("assetTypeDi");
        if(StrUtil.isNotEmpty(assetTypeDi)){
            sql.append(" and asset_type_di = '" + assetTypeDi + "'");
        }
        String zone = param.get("zone") == null ? "" : (String)param.get("zone");
        if(StrUtil.isNotEmpty(zone)){
            sql.append(" and zone = '" + zone + "'");
        }
        String orderType = param.get("orderType") == null ? "" : (String)param.get("orderType");
        if(StrUtil.isNotEmpty(orderType)){
            sql.append(" and order_Type = '" + orderType + "'");
        }
        String orderTypeNe = param.get("orderTypeNe") == null ? "" : (String)param.get("orderTypeNe");
        if(StrUtil.isNotEmpty(orderTypeNe)){
            sql.append(" and order_Type != '" + orderTypeNe + "'");
        }
        String status = param.get("status") == null ? "" : (String)param.get("status");
        if(StrUtil.isNotEmpty(status)){
            sql.append(" and (status = '" + status + "' or current_step = '" + currentStep + "')");
        }else{
            if(StrUtil.isNotEmpty(currentStep)){
                sql.append(" and current_step = '" + currentStep + "'");
            }
        }
        List<String> currentSteps = param.get("currentSteps") == null ? new ArrayList<>() : (List<String>)param.get("currentSteps");
        if(currentSteps != null && currentSteps.size() > 0){
            sql.append(" and (");
            int i= 0;
            for(String c : currentSteps){
                if(i == (currentSteps.size() - 1)){
                    sql.append(" current_step = '"+ c + "'");
                }else{
                    sql.append(" current_step = '"+ c + "' or ");
                }
                i++;
            }
            sql.append(") ");
        }
        String ordersRole = param.get("ordersRole") == null ? "" : (String)param.get("ordersRole");
        if(StrUtil.isNotEmpty(ordersRole)){
            sql.append(" and (current_step = 'OrderExtraPending' or (current_step = 'OrderPending' and order_type != 'Probe')) ");
        }
        return sql.toString();
    }
}
