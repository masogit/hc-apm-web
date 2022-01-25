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
                " ai.clinical_dept_name AS hospital_name, " +
                " zca.asset_type AS asset_type," +
                " ac.end_date " +
                " FROM " +
                " zkb_contract ac " +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id " +
                " INNER JOIN asset_info ai ON ai.uid = zca.asset_id " +
                " WHERE " +
                " ai.system_id = '"+systemId+"' " +
                " ORDER BY ac.end_date desc " +
                " limit 1");
        return sb.toString();
    }

    /**
     * 查出设备信息
     * @param param 过滤条件
     * @return
     */
    public String loadAssetInfoBuildByUid(Map<String,String> param){
        String uid = param.get("uid");//设备system id
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  ai.system_id, " +
                " ai.function_type, " +
                " ac.ID AS contract_id, " +
                " ai.tenant_uid, "+
                " ai.clinical_dept_name AS hospital_name, " +
                " zca.asset_type AS asset_type," +
                " ac.end_date " +
                " FROM " +
                " zkb_contract ac " +
                " INNER JOIN zkb_contract_asset zca ON CAST (ac. ID AS VARCHAR) = zca.contract_id " +
                " INNER JOIN asset_info ai ON ai.uid = zca.asset_id " +
                " WHERE " +
                " ai.uid = '"+uid+"' " +
                " AND now() between ac.start_date and ac.end_date");
        return sb.toString();
    }




    /**
     * 查出设备信息
     * @param param 过滤条件
     * @return
     */
    public String loadAssetInfoBySysId(Map<String,String> param){
        String systemId = param.get("systemId");//设备system id
        String currentDate = this.getDateStr();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT TT.name as comments,\n" +
                "       t.qr_code,\n" +
                "       t.uid,\n" +
                "       t.name,\n" +
                "       t.depart_num,\n" +
                "       t.clinical_dept_name,\n" +
                "       t.function_type,\n" +
                "       t.system_id,\n" +
                "       t.asset_group,\n" +
                "       t.repair_form\n" +
                "FROM asset_info T\n" +
                "         LEFT JOIN tenant_info TT ON TT.uid = T.tenant_uid\n" +
                "WHERE T.system_id = '"+systemId+"'\n" +
                "  AND T.is_valid = TRUE\n" +
                "  AND T.is_deleted = FALSE\n" +
                "  and exists(select 1 from tenant_info ttt WHERE ttT.type = '3' AND ttt.uid = t.tenant_uid)");
        return sb.toString();
    }


    /**
     * 查出设备信息
     * @param param 过滤条件
     * @return
     */
    public String loadAssetInfoByUid(Map<String,String> param){
        String uid = param.get("uid");//设备system id
        String currentDate = this.getDateStr();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT TT.name as comments,\n" +
                "       t.qr_code,\n" +
                "       t.uid,\n" +
                "       t.name,\n" +
                "       t.depart_num,\n" +
                "       t.clinical_dept_name,\n" +
                "       t.function_type,\n" +
                "       t.system_id,\n" +
                "       t.asset_group,\n" +
                "       t.repair_form\n" +
                "FROM asset_info T\n" +
                "         LEFT JOIN tenant_info TT ON TT.uid = T.tenant_uid\n" +
                "WHERE T.uid = '"+uid+"'\n" +
                "  AND T.is_valid = TRUE\n" +
                "  AND T.is_deleted = FALSE\n" +
                "  and exists(select 1 from tenant_info ttt WHERE ttT.type = '3' AND ttt.uid = t.tenant_uid)");
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
        sb=sb.append(" SELECT distinct fe.* from zkb_fe_part_order fe " +
                " INNER JOIN asset_info ass on fe.system_id = ass.system_id and  ass.is_deleted = 'false'" +
                " INNER JOIN tenant_info tenant ON tenant.uid = ass.tenant_uid and tenant.TYPE = 3 " +
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

    /**
     * 查询等待归还的配件
     * @return
     */
    public String buildFilter_queryNeedReturnSparePart(Map<String,String> param)
    {
        String testLogin = param.get("testLogin");//是否测试账号登录
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT " +
                " zkb_fe_part.id, " +
                " tenant.name as tenant_name, " +
                " zkb_con.contract_no, " +
                " zkb_con.region, " +
                " zkb_con.contract_type, " +
                " zkb_fe_part.system_id, " +
                " zkb_fe_part.part_order_no, " +
                " zkb_fe_asset.part_order_no as asset_no, " +
                " zkb_fe_asset.asset_description, " +
                " zkb_fe_asset.asset_qty as cat_num, " +
                " zkb_fe_asset.order_no, " +
                " zkb_fe_asset.price, " +
                " zkb_fe_asset.comment " +
                " from zkb_fe_part_order zkb_fe_part " +
                " INNER JOIN zkb_contract zkb_con on zkb_con.id = zkb_fe_part.contract_id " +
                " INNER JOIN tenant_info tenant on zkb_fe_part.tenant_uid = tenant.uid " +
                " INNER JOIN zkb_fe_part_order_asset_info zkb_fe_asset on zkb_fe_asset.fe_part_order_id = zkb_fe_part.id " +
                " where zkb_fe_part.current_step = 'WaitingGEAdmin' and  zkb_fe_part.order_type = 'SpareParts' and  zkb_fe_asset.asset_qty>0 ");
        if("f".equals(testLogin))
            sb.append(" and zkb_fe_part.test_status = 'f' ");
        sb.append(" order by zkb_fe_part.report_date desc");
        return sb.toString();
    }

    public String buildFilter_queryNeedReturnProbe(Map<String,String> param)
    {
        String testLogin = param.get("testLogin");//是否测试账号登录
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT " +
                " tenant.name as tenant_name, " +
                " zkb_con.contract_no, " +
                " zkb_con.region, " +
                " zkb_con.contract_type," +
                " zkb_fe_part.system_id, " +
                " zkb_fe_part.part_order_no, " +
                " zkb_fe_part.cat_no, " +
                " cat.value_type, " +
                " cat.cat_model,"+
                " zkb_fe_part.old_cat_sn, "+
                " zkb_fe_part.order_no, "+
                " zkb_fe_part.price"+
                " from zkb_fe_part_order zkb_fe_part " +
                " INNER JOIN tenant_info tenant on zkb_fe_part.tenant_uid = tenant.uid " +
                " INNER JOIN zkb_contract zkb_con on zkb_con.id = zkb_fe_part.contract_id " +
                " INNER JOIN zkb_part_order_cat cat on cat.id = zkb_fe_part.cat_id " +
                " where zkb_fe_part.current_step = 'WaitingGEAdmin' and  zkb_fe_part.order_type = 'Probe' ");
                if("f".equals(testLogin))
                    sb.append(" and zkb_fe_part.test_status = 'f' ");
        sb.append(" order by zkb_fe_part.report_date desc ");
        return sb.toString();
    }

    public String batchExport(Map<String,String> param){
        String testLogin = param.get("testLogin");//是否测试账号登录
        String tenantUid = param.get("tenantUid");
        StringBuffer sql = new StringBuffer("SELECT * from zkb_fe_part_order where 1=1");
        if(StringUtils.isNotBlank(tenantUid))
            sql.append(" and tenant_uid = '"+tenantUid+"' ");
        if("f".equals(testLogin))
            sql.append(" and test_status = 'f' ");
        sql.append(" order by report_date desc");
        return sql.toString();
    }

}
