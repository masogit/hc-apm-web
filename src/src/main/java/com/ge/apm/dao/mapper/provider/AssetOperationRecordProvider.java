package com.ge.apm.dao.mapper.provider;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import webapp.framework.dao.SearchFilter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Liu
 */
public class AssetOperationRecordProvider {
	private static final String ROW_NUM_STR = " row_number() over ( PARTITION BY a.asset_info_uid ORDER BY a.create_date DESC ) AS rownum,";
	private static final String SELECT_STR = " a.id as id,a.asset_info_uid as assetInfoUid,a.asset_info_name as assetInfoName,a.asset_info_function_type as assetInfoFunctionType,a.asset_info_depart_num as assetInfoDepartNum,a.serial_num as serialNum,a.asset_info_clinical_dept_name as assetInfoClinicalDeptName,a.hospital_number as hospitalNumber,a.operation_name as operationName,a.operation_by_name as operationByName,a.create_date as createDate,a.description,b.qr_code as assetInfoqrCode";
	private static final String TABLE_STR = " from ((asset_operation_record a join asset_info b on a.asset_info_uid= b.uid and b.is_deleted='f'";

	private static final String TABLE_END = " ) ) where 1=1";
	public String getAll(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Set<SearchFilter> columns = (Set<SearchFilter>) param.get("filters");
		String sortField = (String) param.get("sortField");
		boolean isAscending = (boolean) param.get("isAscending");
		boolean assetFlag = (boolean) param.get("assetFlag");
		boolean scrapStatus = (boolean) param.get("scrapStatus");
		int limit = (int) param.get("limit");
		int offset = (int) param.get("offset");
		if (assetFlag) {
			sb.append("select t.* from ( select");
			sb.append(ROW_NUM_STR);
		} else {
			sb.append("select");
		}
		sb.append(SELECT_STR);
		sb.append(TABLE_STR);
		if (ObjectUtil.isNotEmpty(columns)) {
			for (SearchFilter searchFilter : columns) {
				addTenantFilter(sb, searchFilter);
			}
		}
		sb.append(TABLE_END);
		if (ObjectUtil.isNotEmpty(columns)) {
			sb.append(" and (");
			for (SearchFilter searchFilter : columns) {
				addDefaultFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		if (ObjectUtil.isNotEmpty(columns)) {
			sb.append(" and (");
			for (SearchFilter searchFilter : columns) {
				addFilter(sb, searchFilter);
			}
			if(scrapStatus){
//				sb.append(" b.is_valid='t' and b.status!=4   and");
			}else{
				sb.append(" b.is_valid='t'  and");
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		addOrder(sb, sortField, isAscending);
		sb.append(assetFlag ? " ) t where rownum = 1" : "");
		addLimit(sb, limit, offset);
		return sb.toString();
	}

	public String getAllCount(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Set<SearchFilter> columns = (Set<SearchFilter>) param.get("filters");
		boolean assetFlag = (boolean) param.get("assetFlag");
		boolean scrapStatus = (boolean) param.get("scrapStatus");

		if (assetFlag) {
			sb.append("select count(*) from ( select");
			sb.append(ROW_NUM_STR);
			sb.append(SELECT_STR);
		} else {
			sb.append("select count(*)");
		}
		sb.append(TABLE_STR);

		if (ObjectUtil.isNotEmpty(columns)) {
			for (SearchFilter searchFilter : columns) {
				addTenantFilter(sb, searchFilter);
			}
		}
		sb.append(TABLE_END);
		if (ObjectUtil.isNotEmpty(columns)) {
			sb.append(" and (");
			for (SearchFilter searchFilter : columns) {
				addDefaultFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		if (ObjectUtil.isNotEmpty(columns)) {
			sb.append(" and (");
			for (SearchFilter searchFilter : columns) {
				addFilter(sb, searchFilter);
			}
			if(scrapStatus){
//				sb.append(" b.is_valid='t' and b.status!=4   and");
			}else{
				sb.append(" b.is_valid='t'  and");
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		sb.append(assetFlag ? " ) t where rownum = 1" : "");
		return sb.toString();
	}

	private void addOrder(StringBuilder sb, String sortField, boolean isAscending) {
		if (isAscending) {
			sb.append(" order by ").append(sortField).append(" asc");
		} else {
			sb.append(" order by ").append(sortField).append(" desc");
		}
	}

	private void addLimit(StringBuilder sb, int limit, int offset) {
		sb.append(" limit ").append(limit);
		sb.append(" offset ").append(offset);
	}

	private void addDefaultFilter(StringBuilder sb, SearchFilter searchFilter) {
		String tempColumn = searchFilter.fieldName;
		Object value = searchFilter.value;
		SearchFilter.Operator op = searchFilter.operator;
		if (op.equals(SearchFilter.Operator.IN)) {
			String strValue = getSetValue(value);
			if ("assetInfo.assetType".equals(tempColumn)) {
				sb.append("b.asset_type in ").append(strValue).append(" and ");
			}
			if ("assetInfo.orgUidList".equals(tempColumn)) {
				sb.append("(");
				sb.append("b.tenant_uid in ").append(strValue);
				sb.append("or b.institution_uid in ").append(strValue);
				sb.append("or b.hospital_uid in ").append(strValue);
				sb.append("or b.site_uid in ").append(strValue);
				sb.append("or b.clinical_dept_uid in ").append(strValue);
				sb.append("or b.asset_dept_uid in ").append(strValue).append(")").append(" and ");
			}
		}
//		else {
//			switch (tempColumn) {
//				case "assetInfo.siteUID":
//					sb.append("b.site_uid='").append(value).append("' and ");
//					break;
//				case "assetInfo.hospitalUID":
//					sb.append("b.hospital_uid='").append(value).append("' and ");
//					break;
//				case "assetInfo.institutionUID":
//					sb.append("b.institution_uid='").append(value).append("' and ");
//					break;
//				case "assetInfo.tenantUID":
//					sb.append("b.tenant_uid='").append(value).append("' and ");
//					break;
//				default:
//					break;
//			}
//		}
	}


	private void addTenantFilter(StringBuilder sb, SearchFilter searchFilter) {
		String tempColumn = searchFilter.fieldName;
		Object value = searchFilter.value;
		SearchFilter.Operator op = searchFilter.operator;
		if (op.equals(SearchFilter.Operator.EQ))  {
			switch (tempColumn) {
				case "assetInfo.siteUID":
					sb.append(" and b.site_uid='").append(value).append("'");
					break;
				case "assetInfo.hospitalUID":
					sb.append(" and b.hospital_uid='").append(value).append("'");
					break;
				case "assetInfo.institutionUID":
					sb.append("and b.institution_uid='").append(value).append("'");
					break;
				case "assetInfo.tenantUID":
					sb.append("and b.tenant_uid='").append(value).append("'");
					break;
				default:
					break;
			}
		}
	}

	private String getSetValue(Object value) {
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
			sb.append("'");
			sb.append("'");
			sb.append(")");
		}
		return sb.toString();
	}

	private void addFilter(StringBuilder sb, SearchFilter searchFilter) {
		String tempColumn = searchFilter.fieldName;
		Object value = searchFilter.value;
		SearchFilter.Operator op = searchFilter.operator;
		// obj
		switch (tempColumn) {
			case "createDate":
				if (op.equals(SearchFilter.Operator.GTE)) {
					sb.append("a.create_date>='").append(value).append("' and ");
				} else if (op.equals(SearchFilter.Operator.LTE)) {
					sb.append("a.create_date<='").append(value).append("' and ");
				}
				break;
			// userAccount
			case "operationByName":
				sb.append("(d.name like '%").append(value).append("%' or ");
				sb.append("d.name_py_code like '%").append(value).append("%') and ");
				break;
			case "operationName":
				sb.append("a.operation_name like '%").append(value).append("%' and ");
				break;
			case "hospitalNumber":
				sb.append("a.hospital_number like '%").append(value).append("%' and ");
				break;
			case "assetInfoUid":
				sb.append("a.asset_info_uid = '").append(value).append("' and ");
				break;

			// assetInfo
			case "assetInfoName":
				sb.append("(b.name like '%").append(value).append("%' or ");
				sb.append("b.name_py_code like '%").append(value).append("%') and ");
				break;
			case "assetInfodepartNum":
				sb.append("b.depart_num like '%").append(value).append("%' and ");
				break;
			case "assetInfofunctionType":
				sb.append("b.function_type like '%").append(value).append("%' and ");
				break;
			case "serialNum":
				sb.append("b.serial_num like '%").append(value).append("%' and ");
				break;
			case "assetInfoclinicalDeptName":
				sb.append("(b.clinical_dept_name like '%").append(value).append("%' or ");
				sb.append("b.clinical_dept_name_py_code like '%").append(value).append("%') and ");
				break;
			default:
				break;
		}

	}
}
