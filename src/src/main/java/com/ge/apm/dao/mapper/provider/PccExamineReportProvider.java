package com.ge.apm.dao.mapper.provider;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import webapp.framework.dao.SearchFilter;

public class PccExamineReportProvider {
	private static final String selectStr = "select a.id as id,a.create_date as createDate,a.modify_date as modifyDate,a.report_date as reportDate,a.check_date as checkDate,a.tenant_uid as tenantUID,a.institution_uid as institutionUID,a.hospital_uid as hospitalUID,a.site_uid as siteUID,a.create_by as createByUid,a.modify_by as modifyByUid,a.name as name,a.report_storage_id as reportStorageId,a.status as status,b.uid as assetUid,a.line_status as lineStatus,a.mirror_status as mirrorStatus,a.shell_status as shellStatus,a.oil_status as oilStatus,a.linker_status as linkerStatus,a.tip_status as tipStatus,a.electric_leakage_status as electricLeakageStatus,a.first_seen_status as firstSeenStatus,a.first_tip_status as firstTipStatus,a.result_status as resultStatus,a.recommand_status as recommandStatus,a.comment as comment,b.name as assetInfoname,b.name_py_code as assetInfonamePyCode,b.function_group as assetInfofunctionGroup,b.function_type as assetInfofunctionType,b.serial_num as assetInfoserialNum,b.depart_num as assetInfodepartNum,b.financing_num as assetInfofinancingNum,b.clinical_dept_uid as assetInfoclinicalDeptUID,b.clinical_dept_name as assetInfoclinicalDeptName,b.manufact_date as assetInfomanufactDate,b.system_id as assetInfosystemId,b.arrive_date as assetInfoarriveDate,b.install_date as assetInfoinstallDate,b.warranty_date as assetInfowarrantyDate,c.name as createByName, d.name as assetInfohospitalName";
	private static final String tableStr = " from ((asset_info b left join pcc_examine_report a on a.asset_uid=b.uid) left join user_account c on c.uid=a.create_by left join org_info d on d.id=b.hospital_id) where b.is_deleted='f' and b.is_valid='t' and b.status!=4";

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
//				addFilter(sb, searchFilter);
			}
			sb.append(" 1=2 ");
			sb.append(")");
		}
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
//				addDefaultFilter(sb, searchFilter);
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
//				addFilter(sb, searchFilter);
			}
			sb.append(" 1=2 ");
			sb.append(")");
		}
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
//				addDefaultFilter(sb, searchFilter);
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
		if(op.equals(SearchFilter.Operator.IN)){
			String strValue=getSetValue(value);
			if (tempColumn.equals("assetInfo.siteUID")) {
				sb.append("b.site_uid in " + strValue + " and ");
			} else if (tempColumn.equals("assetInfo.hospitalUID")) {
				sb.append("b.hospital_uid in " + strValue + " and ");
			} else if (tempColumn.equals("assetInfo.institutionUID")) {
				sb.append("b.institution_uid in " + strValue + " and ");
			} else if (tempColumn.equals("assetInfo.tenantUID")) {
				sb.append("b.tenant_uid in " + strValue + " and ");
			} else if (tempColumn.equals("assetInfo.clinicalDeptUID")) {
				sb.append("b.clinical_dept_uid in " + strValue + " and ");
			}
		}
		else{
			if (tempColumn.equals("assetInfo.siteUID")) {
				sb.append("b.site_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.hospitalUID")) {
				sb.append("b.hospital_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.institutionUID")) {
				sb.append("b.institution_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.tenantUID")) {
				sb.append("b.tenant_uid='" + value + "' and ");
			} else if (tempColumn.equals("assetInfo.clinicalDeptUID")) {
				sb.append("b.clinical_dept_uid='" + value + "' and ");
			}
		}
	}
	
	private String getSetValue(Object value){
		StringBuilder sb=new StringBuilder();
		Set<String> set=(Set<String>)value;
		if(set!=null){
			Iterator<String> it=set.iterator();
			sb.append("(");
			while(it.hasNext()){
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
		if (tempColumn.equals("reportDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.report_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.report_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("checkDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.check_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.check_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("status")) {
			sb.append("a.status='" + value + "' and ");
		} else if (tempColumn.equals("recommandStatus")) {
			sb.append("a.recommand_status='" + value + "' and ");
		} else if (tempColumn.equals("assetInfoname")) {
			sb.append("(b.name like '%" + value + "%' or ");
			sb.append("b.name_py_code like '%" + value + "%') and ");
		} else if (tempColumn.equals("assetInfonamePyCode")) {
			sb.append("(b.name like '%" + value + "%' or ");
			sb.append("b.name_py_code like '%" + value + "%') and ");
		} else if (tempColumn.equals("assetInfofunctionGroup")) {
			sb.append("b.function_group='" + value + "' and ");
		} else if (tempColumn.equals("assetInfofunctionType")) {
			sb.append("b.function_type like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfosystemId")) {
			sb.append("b.system_id like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfoserialNum")) {
			sb.append("b.serial_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfodepartNum")) {
			sb.append("b.depart_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfofinancingNum")) {
			sb.append("b.financing_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfoclinicalDeptName")) {
			sb.append("(b.clinical_dept_name like '%" + value + "%' or ");
			sb.append("b.clinical_dept_name_py_code like '%" + value + "%') and ");
		} else if (tempColumn.equals("assetInfoassetGroup")) {
			sb.append("b.asset_group='" + value + "' and ");
		} else if (tempColumn.equals("assetInfoassetType")) {
			sb.append("b.asset_type like '%" + value + "%' and ");
		} else if (tempColumn.equals("assetInfomanufactDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("b.manufact_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("b.manufact_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("assetInfoarriveDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("b.arrive_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("b.arrive_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("assetInfoinstallDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("b.install_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("b.install_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("assetInfowarrantyDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("b.warranty_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("b.warranty_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("createByName")) {
			sb.append("(c.name like '%" + value + "%' or ");
			sb.append("c.name_py_code like '%" + value + "%') and ");
		} else if (tempColumn.equals("assetInfohospitalName")) {
			sb.append("(d.name like '%" + value + "%' or ");
			sb.append("d.name_py_code like '%" + value + "%') and ");
		}
	}
}
