package com.ge.apm.dao.mapper.provider;

import webapp.framework.dao.SearchFilter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AssetOperationRecordProvider {
	private static final String selectStr = "select a.id as id,a.asset_info_name as assetInfoName,a.asset_info_function_type as assetInfoFunctionType,a.asset_info_depart_num as assetInfoDepartNum,a.serial_num as serialNum,a.asset_info_clinical_dept_name as assetInfoClinicalDeptName,a.hospital_number as hospitalNumber,a.operation_name as operationName,a.operation_by_name as operationByName,a.create_date as createDate";
	private static final String tableStr = " from (((asset_operation_record a join asset_info b on a.asset_info_uid= b.uid and b.is_deleted='f' and b.is_valid='t' and b.status!=4) join org_info c on b.site_uid=c.uid) join user_account d on a.operation_by=d.uid) where 1=1";
	public String getAll(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Set<SearchFilter> columns = (Set<SearchFilter>) param.get("filters");
		String sortField = (String) param.get("sortField");
		boolean isAscending = (boolean) param.get("isAscending");
		int limit = (int) param.get("limit");
		int offset = (int) param.get("offset");
		sb.append(selectStr + tableStr);
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				addDefaultFilter(sb, searchFilter);
				// addFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				// addDefaultFilter(sb, searchFilter);
				addFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		addOrder(sb, sortField, isAscending);
		addLimit(sb, limit, offset);
		String result = sb.toString();
		return result;
	}

	public String getAllCount(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Set<SearchFilter> columns = (Set<SearchFilter>) param.get("filters");
		sb.append("select count(b.id)" + tableStr);
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				addDefaultFilter(sb, searchFilter);
				// addFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				// addDefaultFilter(sb, searchFilter);
				addFilter(sb, searchFilter);
			}
			sb.append(" 1=1 ");
			sb.append(")");
		}
		String result = sb.toString();
		return result;
	}

	private void addOrder(StringBuilder sb, String sortField, boolean isAscending) {
		if (isAscending) {
			sb.append(" order by " + sortField + " asc");
		} else {
			sb.append(" order by " + sortField + " desc");
		}
	}

	private void addLimit(StringBuilder sb, int limit, int offset) {
		sb.append(" limit " + limit);
		sb.append(" offset " + offset);
	}

	private void addDefaultFilter(StringBuilder sb, SearchFilter searchFilter) {
		String tempColumn = searchFilter.fieldName;
		Object value = searchFilter.value;
		SearchFilter.Operator op = searchFilter.operator;
		if (op.equals(SearchFilter.Operator.IN)) {
			String strValue = getSetValue(value);
			if (tempColumn.equals("assetInfo.clinicalDeptUID")) {
				sb.append("b.clinical_dept_uid in " + strValue + " and ");
			}
		} else {
			if (tempColumn.equals("assetInfo.siteUID")) {
				sb.append("b.site_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.hospitalUID")) {
				sb.append("b.hospital_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.institutionUID")) {
				sb.append("b.institution_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.tenantUID")) {
				sb.append("b.tenant_uid='" + value + "' and ");
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
		if (tempColumn.equals("createDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.create_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.create_date<='" + value + "' and ");
			}
		}
		// userAccount
		else if (tempColumn.equals("operationByName")) {
			sb.append("(d.name like '%" + value + "%' or ");
			sb.append("d.name_py_code like '%" + value + "%') and ");
		}
		else if (tempColumn.equals("operationName")) {
			sb.append("a.operation_name like '%" + value + "%' and ");
		}

		else if (tempColumn.equals("hospitalNumber")) {
            sb.append("a.hospital_number like '%" + value + "%' and ");
        }
		
		// assetInfo
		else if (tempColumn.equals("assetInfoName")) {
			sb.append("(b.name like '%" + value + "%' or ");
			sb.append("b.name_py_code like '%" + value + "%') and ");
		} else if (tempColumn.equals("assetInfodepartNum")) {
			sb.append("b.depart_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfofunctionType")) {
			sb.append("b.function_type like '%" + value + "%' and ");
		} else if (tempColumn.equals("serialNum")) {
			sb.append("(b.serial_num like '%" + value + "%' and ");
		}else if (tempColumn.equals("assetInfoclinicalDeptName")) {
			sb.append("(b.clinical_dept_name like '%" + value + "%' or ");
			sb.append("b.clinical_dept_name_py_code like '%" + value + "%') and ");
		}

	}
}
