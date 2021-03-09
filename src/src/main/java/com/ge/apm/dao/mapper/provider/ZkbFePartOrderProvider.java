package com.ge.apm.dao.mapper.provider;

import java.util.Map;

/**
 * Created by 503202755 on 2020/12/9
 */
public class ZkbFePartOrderProvider {

    /**
     * 查出设备信息
     * @param param 过滤条件
     * @return
     */
    public String loadAssetInfo(Map<String,String> param){
        String systemId = param.get("systemId");//设备system id
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
                " limit 1");
        return sb.toString();
    }

}
