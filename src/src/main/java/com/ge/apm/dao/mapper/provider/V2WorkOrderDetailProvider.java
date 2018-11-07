package com.ge.apm.dao.mapper.provider;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import webapp.framework.dao.SearchFilter;

public class V2WorkOrderDetailProvider {
	private String selectMVSStr="select a.id as id,a.wo_id as woId,a.parts as parts,a.parts_quantity as partsQuantity,a.parts_price as partsPrice,a.tenant_uid as tenantUID,a.institution_uid as institutionUID,a.hospital_uid as hospitalUID,a.site_uid as siteUID,a.created_date as createdDate,a.serial_num as serialNum,a.warranty_date as warrantyDate,a.apply_time as applyTime,a.estimated_arrive_time as estimatedArriveTime,a.arrived_time as arrivedTime,a.closed_price as closedPrice,a.arrived as arrived,a.parts_type as partsType,a.approved as approved,a.invoice_num as invoiceNum,a.closed as closed,a.closed_time as closedTime,a.supplier as supplier,a.comment as comment,a.contract_num as contractNum,a.invoice_time as invoiceTime,a.arrived_good as arrivedGood,a.arrived_good_return as arrivedGoodReturn,a.purchase_time as purchaseTime,a.deliver_time as deliverTime,a.priority as priority,a.mvs_price as mvsPrice, b.request_time as v2WorkOrderServiceRequestRequestTime, c.mvs_type as v2WorkOrderMvsType, d.name as v2WorkOrderFEOwnerName,d.name_py_code as v2WorkOrderFEOwnerNamePyCode, e.depart_num as v2WorkOrderAssetInfoDepartNum, e.name as v2WorkOrderAssetInfoName,e.name_py_code as v2WorkOrderAssetInfoNamePyCode,e.mvs_equip as v2WorkOrderAssetInfoMvsEquip, e.site_uid as v2WorkOrderAssetInfoSiteUID,e.hospital_uid as v2WorkOrderAssetInfoHospitalUID,e.institution_uid as v2WorkOrderAssetInfoInstitutionUID,e.tenant_uid as v2WorkOrderAssetInfoTenantUID, f.name as hospitalOrgName, f.uid as hospitalOrgUid, g.name as siteOrgName, g.uid as siteOrgUid, h.uid as tenantOrgUid, i.uid as institutionOrgUid";
	private String tableMVSStr=" from v2_work_order_detail a,v2_service_request b, v2_work_order c, user_account d, asset_info e, org_info f, org_info g, org_info h, org_info i where a.wo_id=c.id and c.sr_id=b.id and c.current_person_id=d.id and c.asset_id=e.id and e.hospital_uid=f.uid and e.site_uid=g.uid and e.tenant_uid=h.uid and e.institution_uid=i.uid";
	public String getAll(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		@SuppressWarnings("unchecked")
		Set<SearchFilter> columns = (Set<SearchFilter>) param.get("filters");
		String sortField = (String) param.get("sortField");
		boolean isAscending = (boolean) param.get("isAscending");
		int limit = (int) param.get("limit");
		int offset = (int) param.get("offset");
		// hospital f,site g,tenant h,institution i
		// v2_work_order_detail a,v2_service_request b, v2_work_order c,
		// user_account d, asset_info e, org_info f, org_info g, org_info h,
		// org_info i
		// [deliverTime LTE Thu Nov 15 00:00:00 CST 2018 null, deliverTime GTE
		// Tue Nov 06 00:00:00 CST 2018 null]
		sb.append(selectMVSStr+tableMVSStr);
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				addDefaultFilter(sb, searchFilter);
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
		sb.append("select count(a.id)"+tableMVSStr);
		if (columns != null && columns.size() > 0) {
			sb.append(" and (");
			Iterator<SearchFilter> it = columns.iterator();
			while (it.hasNext()) {
				SearchFilter searchFilter = it.next();
				addDefaultFilter(sb, searchFilter);
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
		if (tempColumn.equals("v2WorkOrder.assetInfo.mvsEquip")) {
			sb.append("e.mvs_equip='" + value + "' and ");
		} else if (tempColumn.equals("v2WorkOrder.assetInfo.siteUID")) {
			sb.append("e.site_uid='" + value + "' and ");
		} else if (tempColumn.equals("v2WorkOrder.assetInfo.hospitalUID")) {
			sb.append("e.hospital_uid='" + value + "' and ");
		} else if (tempColumn.equals("v2WorkOrder.assetInfo.institutionUID")) {
			sb.append("e.institution_uid='" + value + "' and ");
		} else if (tempColumn.equals("v2WorkOrder.assetInfo.tenantUID")) {
			sb.append("e.tenant_uid='" + value + "' and ");
		}
	}

	private void addFilter(StringBuilder sb, SearchFilter searchFilter) {
		String tempColumn = searchFilter.fieldName;
		Object value = searchFilter.value;
		SearchFilter.Operator op = searchFilter.operator;
		if (tempColumn.equals("priority")) {
			sb.append("a.priority='" + value + "' and ");
		} else if (tempColumn.equals("parts")) {
			sb.append("a.parts like'%" + value + "%' and ");
		} else if (tempColumn.equals("partsQuantity")) {
			sb.append("a.parts_quantity='" + value + "' and ");
		} else if (tempColumn.equals("mvsPrice")) {
			sb.append("a.mvs_price='" + value + "' and ");
		} else if (tempColumn.equals("serialNum")) {
			sb.append("a.serial_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("invoiceNum")) {
			sb.append("a.invoice_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("warrantyDate")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.warranty_date>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.warranty_date<='" + value + "' and ");
			}
		} else if (tempColumn.equals("purchaseTime")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.purchase_time>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.purchase_time<='" + value + "' and ");
			}
		} else if (tempColumn.equals("deliverTime")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.deliver_time>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.deliver_time<='" + value + "' and ");
			}
		} else if (tempColumn.equals("arrivedTime")) {
			if (op.equals(SearchFilter.Operator.GTE)) {
				sb.append("a.arrived_time>='" + value + "' and ");
			} else if (op.equals(SearchFilter.Operator.LTE)) {
				sb.append("a.arrived_time<='" + value + "' and ");
			}
		} else if (tempColumn.equals("arrivedGood")) {
			sb.append("a.arrived_good=" + value + " and ");
		} else if (tempColumn.equals("arrivedGoodReturn")) {
			sb.append("a.arrived_good_return=" + value + " and ");
		} else if (tempColumn.equals("v2WorkOrderMvsType")) {
			sb.append("c.mvs_type='" + value + "' and ");
		} else if (tempColumn.equals("v2WorkOrderAssetInfoDepartNum")) {
			sb.append("e.depart_num like '%" + value + "%' and ");
		} else if (tempColumn.equals("v2WorkOrderAssetInfoNamePyCode")) {
			sb.append("e.name_py_code like '%" + value + "%' and ");
		} else if (tempColumn.equals("v2WorkOrderFEOwnerNamePyCode")) {
			sb.append("d.name_py_code like '%" + value + "%' and ");
		}
	}
}
