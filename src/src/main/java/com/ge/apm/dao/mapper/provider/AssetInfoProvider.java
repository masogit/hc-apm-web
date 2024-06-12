package com.ge.apm.dao.mapper.provider;

import com.ge.apm.service.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AssetInfoProvider {
	Logger logger = LoggerFactory.getLogger(getClass());
	public String fetchAssetInfo(){
		StringBuilder sb = new StringBuilder();
		sb.append("select asset_id from asset_summit where created = date(now())");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetInfo sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchAssetCostStatisticsByAssetId(Map<String,Object> param){
		Integer assetId = (Integer) param.get("assetId");
		Date day = (Date) param.get("day");
		StringBuilder sb = new StringBuilder();
		sb.append("select ai. id asset_id,ai.site_id,ai.hospital_id,ai.asset_group,ai.clinical_dept_id dept_id,ai.status,ai.supplier_id, ");
		sb.append("wo.request_time,wo.confirmed_down_time,wo.status wo_status,wo.id wo_id ");
		sb.append("from asset_info ai left join work_order wo ");
		sb.append("on ai. id = wo.asset_id and ai. id = "+assetId );
		if(day == null){
			sb.append(" and date(wo.request_time) = date(now()) ");
		}else{
			sb.append(" and date(wo.request_time) = '"+TimeUtils.getStrDate(day,null)+"' ");
		}
		sb.append("order by wo.confirmed_down_time asc,wo.request_time asc limit 1");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetCostStatisticsByAssetId sql is {}",sb.toString());
		}
		return sb.toString();
	}
	
	public String fetchAssetInfoByDay(Date day){
		StringBuilder sb = new StringBuilder();
		sb.append("select asset_id from asset_summit where created = '"+TimeUtils.getStrDate(day,null)+"' ");
		if(logger.isInfoEnabled()){
			logger.info("fetchAssetInfoByDay sql is {}",sb.toString());
		}
		return sb.toString();
	}

	public String fetchByAssetInfoBySystemId(Map<String,String> param){
		String systemId = param.get("systemId");//设备system id
		String contractId = param.get("contractId");//合同ID
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT 	ai.repair_form,ai.depart_num,ai.qr_code,ai.manufacture,ai.uid,ai.asset_group,ai.function_type,zca.asset_type from zkb_contract ac INNER JOIN zkb_contract_asset zca on ac.id = zca.contract_id");
		sb.append(" INNER JOIN asset_info ai on ai.uid = zca.asset_id and is_deleted = 'false' ");
		sb.append(" where ac.id = '"+contractId+"'");
		sb.append(" and ai.system_id = '"+systemId+"'");
		return sb.toString();
	}
	/**
	 * 未查找到使用痕迹
	 */
	public String getMvsWorkOrders(Map<String,Object> param){
		String orgUidList = getSetValue(param.get("orgList")) ;
		String tenantUid = (String) param.get("tenantUid");//
		StringBuilder sb = new StringBuilder();
		sb.append(" select wo.id from asset_info ai2,v2_work_order wo,asset_info ai where ai.is_deleted ='false' and ai.is_valid = 'true' ");
		sb.append(" and (ai.institution_uid in "+orgUidList+" or ai.hospital_uid in "+orgUidList+"or ai.site_uid in "+orgUidList);
		sb.append(" or ai.clinical_dept_uid in "+orgUidList+" or ai.asset_dept_uid in "+orgUidList+" or ai.tenant_uid in "+orgUidList+")");
		sb.append(" and wo.tenant_uid  = '"+tenantUid+"'");
		sb.append(" and ai2.id  = wo.asset_id ");
		sb.append(" (ai2.qr_code = SUBSTRING(ai.system_id,5) and ai.system_id like 'MVS-%'  or ai2.qr_code = SUBSTRING(ai.system_num1,5) and ai.system_num1 like 'MVS-%') ");
		return sb.toString();
	}



	public String getMvsPmOrders(Map<String,Object> param){
		String orgUidList = getSetValue(param.get("orgList")) ;
		String tenantUid = (String) param.get("tenantUid");//
		StringBuilder sb = new StringBuilder();
		sb.append(" select id from v2_pm_order where asset_id in "+orgUidList+"and tenant_uid = '"+tenantUid+"'");
		return sb.toString();
	}

	private String getSetValue(Object value) {
		StringBuilder sb = new StringBuilder();
		Collection<Integer> set = (Collection<Integer>) value;
		if (set != null) {
			Iterator<Integer> it = set.iterator();
			sb.append("(");
			while (it.hasNext()) {
//				sb.append("'");
				sb.append(it.next());
//				sb.append("'");
				sb.append(",");
			}
//			sb.append("'");
//			sb.append("'");
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
		}
		return sb.toString();
	}

	public String countAssetWithQRcode(Map<String,Object> param){
		String uidList = getSetValue1(param.get("uidList")) ;
		String tenantUid = (String) param.get("tenantUid");
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT (ID) FROM ( SELECT DISTINCT ai.* FROM asset_info ai LEFT JOIN zkb_asset_qr_code_from_ib ib_qr_code ON ai.uid = ib_qr_code.asset_uid LEFT JOIN " +
				"            (SELECT COUNT (ID) AS count1,bo_uuid FROM v2_blob_object WHERE bo_sub_type = '1' AND bo_uuid IN "+uidList+" GROUP BY bo_uuid) T " +
				"            ON T.bo_uuid = ai.uid WHERE (ib_qr_code.is_qr_code = true OR T.count1 = 3)" +
				"            AND ai.uid IN "+uidList+") T1");

		return sb.toString();
	}

	private String getSetValue1(Object value) {
		StringBuilder sb = new StringBuilder();
		Collection<String> set = (Collection<String>) value;
		if (set != null) {
			Iterator<String> it = set.iterator();
			sb.append("(");
			while (it.hasNext()) {
				sb.append("'");
				sb.append(it.next());
				sb.append("'");
				sb.append(",");
			}
			sb.deleteCharAt(sb.toString().length()-1);
			sb.append(")");
		}
		return sb.toString();
	}

}
