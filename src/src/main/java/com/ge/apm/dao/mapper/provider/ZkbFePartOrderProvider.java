package com.ge.apm.dao.mapper.provider;

import org.apache.commons.lang.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/12/9
 */
public class ZkbFePartOrderProvider {

    private static final String dateFormat = "yyyyMMdd";

    /**
     * 查出设备信息
     * @param param 过滤条件
     * @return
     */
    public String loadAssetInfo(Map<String,String> param){
        String systemId = param.get("systemId");//设备system id
        String currentDate = this.getDateStr();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT " +
                " ai.system_id, " +
                " ai.function_type, " +
                " ac. ID AS contract_id, " +
                " ai.tenant_uid, "+
                " ai.clinical_dept_name AS hospital_name " +
                " FROM " +
                " zkb_contract ac " +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id " +
                " INNER JOIN asset_info ai ON ai.uid = zca.asset_id " +
                " WHERE " +
                " ai.system_id = '"+systemId+"' " +
                " and to_date('"+currentDate+"','"+dateFormat+"')>=ac.start_date and to_date('"+currentDate+"','"+dateFormat+"')<=ac.end_date" +
                " limit 1");
        return sb.toString();
    }

    private String getDateStr()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(new Date());
    }

    public String queryZkbFePart(Map<String,Object> params)
    {
        List<SearchFilter> filters = (List<SearchFilter>) params.get("filters");
        StringBuilder sb = new StringBuilder();
        sb=sb.append(" SELECT fe.* from zkb_fe_part_order fe " +
                " INNER JOIN asset_info ass on fe.system_id = ass.system_id " +
                " where 1=1");
        String filter = buildFilter_queryZkbFePartDetail(filters);
        sb=sb.append(filter);
        return sb.toString();
    }

    public String buildFilter_queryZkbFePartDetail(List<SearchFilter> searchFilterList){
        StringBuilder sb = new StringBuilder();
        for (SearchFilter searchFilter : searchFilterList) {
            String tempColumn = searchFilter.fieldName;
            Object value = searchFilter.value;
            if(value == null || StringUtils.isEmpty(value.toString())){
                continue;
            }
            if("orderType".equals(tempColumn)) {
                sb.append(" and fe.order_type ='"+value+"'");
            }
            if("status".equals(tempColumn)) {
                sb.append(" and fe.status = '"+value+"'");
            }
            if("systemId".equals(tempColumn)) {
                sb.append(" and fe.system_id = '"+value+"'");
            }
            if("sr".equals(tempColumn)) {
                sb.append(" and fe.sr like '%"+value+"%'");
            }
            if("partOrderNo".equals(tempColumn)) {
                sb.append(" and fe.part_order_no ='"+value+"'");
            }
            if("assetGroup".equals(tempColumn)) {
                sb.append(" and ass.asset_group ="+value);
            }
        }
        return sb.toString();
    }

}
