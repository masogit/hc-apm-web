package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.simpleflatmapper.tuple.Tuple4;
import org.simpleflatmapper.tuple.Tuple5;
import org.simpleflatmapper.tuple.Tuple6;
import org.simpleflatmapper.tuple.Tuple7;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class HealthService {

	private Database db;

	@Resource(name = "connectionProvider")
	private ConnectionProvider connectionProvider;

	@PostConstruct
	public void init() {
		db = Database.from(connectionProvider);
	}

	/*private static final String asset_maintain7
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id,v2_work_order.id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND v2_work_order.site_id=:site_id AND v2_work_order.hospital_id=:hospital_id ";*/
	private static final String asset_maintain7
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id,v2_work_order.id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND (v2_work_order.tenant_uid IN (:site_uid) OR v2_work_order.institution_uid IN (:site_uid) OR v2_work_order.hospital_uid IN (:site_uid) OR v2_work_order.site_uid IN (:site_uid)) ";


    /*private static final String asset_maintain
    	= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id "
    	+ " FROM v2_work_order "
    	+ " LEFT JOIN asset_info "
    	+ " ON v2_work_order.asset_id = asset_info.id "
    	+ " WHERE v2_work_order.status = 1 AND v2_work_order.site_id=:site_id AND v2_work_order.hospital_id=:hospital_id ";*/
	private static final String asset_maintain
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND (v2_work_order.tenant_uid IN (:site_uid) OR v2_work_order.institution_uid IN (:site_uid) OR v2_work_order.hospital_uid IN (:site_uid) OR v2_work_order.site_uid IN (:site_uid)) ";

	/*private static final String asset_maintain7_all
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id,v2_work_order.id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND v2_work_order.site_id=:site_id ";*/
	private static final String asset_maintain7_all
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id,v2_work_order.id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND v2_work_order.tenant_uid=:tenant_uid ";


	/*private static final String asset_maintain_all
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND v2_work_order.site_id=:site_id ";*/
	private static final String asset_maintain_all
			= " SELECT v2_work_order.asset_id, asset_info.name, current_step_id, current_step_name, current_person_name, asset_info.clinical_dept_id "
			+ " FROM v2_work_order "
			+ " LEFT JOIN asset_info "
			+ " ON v2_work_order.asset_id = asset_info.id "
			+ " WHERE v2_work_order.status = 1 AND v2_work_order.tenant_uid=:tenant_uid ";

	/*public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(Integer site_id, Integer hospital_id) {

		if(hospital_id != null){
			return db
					.select(asset_maintain)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(asset_maintain_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					;
		}

	}*/
	public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_maintain)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					;
		}else{
			return db
					.select(asset_maintain_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					;
		}

	}

	/*public Observable<Tuple7<Integer, String, Integer, String, String, Integer,String>> queryForMaintain7(Integer site_id, Integer hospital_id,Integer dept) {

		if(hospital_id != null){
			return db
					.select(asset_maintain7)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) ).filter(t6 -> t6.getElement5()==dept)
					;
			//.cache();
		}else {
			return db
					.select(asset_maintain7_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) ).filter(t6 -> t6.getElement5()==dept)
					;
		}
	}*/
	public Observable<Tuple7<Integer, String, Integer, String, String, Integer,String>> queryForMaintain7(String tenantUID, String siteUID,Integer dept) {

		if(siteUID != null){
			return db
					.select(asset_maintain7)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) ).filter(t6 -> t6.getElement5()==dept)
					;
			//.cache();
		}else {
			return db
					.select(asset_maintain7_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) ).filter(t6 -> t6.getElement5()==dept)
					;
		}
	}

	/*public Observable<Tuple7<Integer, String, Integer, String, String, Integer,String>> queryForMaintain7NoDept(Integer site_id, Integer hospital_id) {

		if(hospital_id != null){
			return db
					.select(asset_maintain7)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) );//.cache();
		}else{
			return db
					.select(asset_maintain7_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) );
		}


	}*/
	public Observable<Tuple7<Integer, String, Integer, String, String, Integer,String>> queryForMaintain7NoDept(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_maintain7)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) );
		}else{
			return db
					.select(asset_maintain7_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple7<Integer, String, Integer, String, String, Integer,String>(
									rs.getInt("asset_id"), rs.getString("name"),
									rs.getInt("current_step_id"), rs.getString("current_step_name"),
									rs.getString("current_person_name"), rs.getInt("clinical_dept_id")
									,rs.getString("id")) );
		}


	}

	/*public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(Integer site_id, Integer hospital_id, Integer dept) {

		if(hospital_id != null){
			return db
					.select(asset_maintain)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					.filter(t6 -> t6.getElement5()==dept)
					;
			//.cache();
		}else{
			return db
					.select(asset_maintain)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					.filter(t6 -> t6.getElement5()==dept)
					;
		}
	}*/
	public Observable<Tuple6<Integer, String, Integer, String, String, Integer>> queryForMaintain(String tenantUID, String siteUID, Integer dept) {

		if(siteUID != null){
			return db
					.select(asset_maintain)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					.filter(t6 -> t6.getElement5()==dept)
					;
			//.cache();
		}else{
			return db
					.select(asset_maintain)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple6<Integer, String, Integer, String, String, Integer> (
									rs.getInt("asset_id"), rs.getString("name"), rs.getInt("current_step_id"), rs.getString("current_step_name"), rs.getString("current_person_name"), rs.getInt("clinical_dept_id") ) )
					.filter(t6 -> t6.getElement5()==dept)
					;
		}
	}


    /*private static final String asset_outage
    	= " SELECT asset_info.id, asset_info.name, to_char(inner_2.created_date, 'YYYY-MM-DD'), inner_2.int_ext_type as case_type, asset_info.clinical_dept_id "
    	+ " FROM "
    		+ " asset_info "
    		+ " LEFT JOIN "
    		+ " (SELECT inner_1.asset_id, inner_1.created_date, v2_work_order.int_ext_type "
    		+ " FROM "
    			+ " ( SELECT asset_id, MAX(created_date) AS created_date FROM v2_work_order GROUP BY asset_id) AS inner_1 "
    			+ " INNER JOIN v2_work_order "
    			+ " ON inner_1.asset_id = v2_work_order.asset_id "
    			+ " WHERE inner_1.created_date = v2_work_order.created_date ) AS inner_2 "
    		+ " ON asset_info.id = inner_2.asset_id "
    		+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND asset_info.site_id = :site_id AND asset_info.hospital_id = :hospital_id "
    		+ " ORDER BY to_char, id, int_ext_type ";*/

	private static final String asset_outage
			= " SELECT asset_info.id, asset_info.name, to_char(inner_2.created_date, 'YYYY-MM-DD'), inner_2.int_ext_type as case_type, asset_info.clinical_dept_id "
			+ " FROM "
			+ " asset_info "
			+ " LEFT JOIN "
			+ " (SELECT inner_1.asset_id, inner_1.created_date, v2_work_order.int_ext_type "
			+ " FROM "
			+ " ( SELECT asset_id, MAX(created_date) AS created_date FROM v2_work_order GROUP BY asset_id) AS inner_1 "
			+ " INNER JOIN v2_work_order "
			+ " ON inner_1.asset_id = v2_work_order.asset_id "
			+ " WHERE inner_1.created_date = v2_work_order.created_date ) AS inner_2 "
			+ " ON asset_info.id = inner_2.asset_id "
			+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND (asset_info.tenant_uid IN (:site_uid) OR asset_info.institution_uid IN (:site_uid) OR asset_info.hospital_uid IN (:site_uid) OR asset_info.site_uid IN (:site_uid)) "
			+ " ORDER BY to_char, id, int_ext_type ";

	/*private static final String asset_outage_all
			= " SELECT asset_info.id, asset_info.name, to_char(inner_2.created_date, 'YYYY-MM-DD'), inner_2.int_ext_type as case_type, asset_info.clinical_dept_id "
			+ " FROM "
			+ " asset_info "
			+ " LEFT JOIN "
			+ " (SELECT inner_1.asset_id, inner_1.created_date, v2_work_order.int_ext_type "
			+ " FROM "
			+ " ( SELECT asset_id, MAX(created_date) AS created_date FROM v2_work_order GROUP BY asset_id) AS inner_1 "
			+ " INNER JOIN v2_work_order "
			+ " ON inner_1.asset_id = v2_work_order.asset_id "
			+ " WHERE inner_1.created_date = v2_work_order.created_date ) AS inner_2 "
			+ " ON asset_info.id = inner_2.asset_id "
			+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND asset_info.site_id = :site_id "
			+ " ORDER BY to_char, id, int_ext_type ";*/
	private static final String asset_outage_all
			= " SELECT asset_info.id, asset_info.name, to_char(inner_2.created_date, 'YYYY-MM-DD'), inner_2.int_ext_type as case_type, asset_info.clinical_dept_id "
			+ " FROM "
			+ " asset_info "
			+ " LEFT JOIN "
			+ " (SELECT inner_1.asset_id, inner_1.created_date, v2_work_order.int_ext_type "
			+ " FROM "
			+ " ( SELECT asset_id, MAX(created_date) AS created_date FROM v2_work_order GROUP BY asset_id) AS inner_1 "
			+ " INNER JOIN v2_work_order "
			+ " ON inner_1.asset_id = v2_work_order.asset_id "
			+ " WHERE inner_1.created_date = v2_work_order.created_date ) AS inner_2 "
			+ " ON asset_info.id = inner_2.asset_id "
			+ " WHERE asset_info.is_valid = true AND asset_info.status = 2 AND asset_info.tenant_uid = :tenant_uid "
			+ " ORDER BY to_char, id, int_ext_type ";

	/*public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(Integer site_id, Integer hospital_id) {

		if(hospital_id != null){
			return db
					.select(asset_outage)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(asset_outage_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}*/
	public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_outage)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(asset_outage_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}

	/*public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(Integer site_id, Integer hospital_id, Integer dept) {

		if(hospital_id != null){
			return db
					.select(asset_outage)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					.filter(t5 -> t5.getElement4()==dept)
					;
			//.cache();
		}else{
			return db
					.select(asset_outage_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					.filter(t5 -> t5.getElement4()==dept)
					;
		}
	}*/
	public Observable<Tuple5<Integer, String, String, Integer, Integer>> queryForOutage(String tenantUID, String siteUID, Integer dept) {

		if(siteUID != null){
			return db
					.select(asset_outage)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					.filter(t5 -> t5.getElement4()==dept)
					;
			//.cache();
		}else{
			return db
					.select(asset_outage_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple5<Integer, String, String, Integer, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("case_type"), rs.getInt("clinical_dept_id") ) )
					.filter(t5 -> t5.getElement4()==dept)
					;
		}
	}

    /*private static final String asset_warranty
    	= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD'), clinical_dept_id "
    	+ " FROM asset_info "
    	+ " WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') AND site_id = :site_id AND hospital_id = :hospital_id "
    	+ " ORDER BY warranty_date ";*/
	private static final String asset_warranty
			= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD'), clinical_dept_id "
			+ " FROM asset_info "
			+ " WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') AND (tenant_uid IN (:site_uid) OR institution_uid IN (:site_uid) OR hospital_uid IN (:site_uid) OR site_uid IN (:site_uid)) "
			+ " ORDER BY warranty_date ";

	/*private static final String asset_warranty_all
			= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD'), clinical_dept_id "
			+ " FROM asset_info "
			+ " WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') AND site_id = :site_id "
			+ " ORDER BY warranty_date ";*/
	private static final String asset_warranty_all
			= " SELECT id, name, to_char(warranty_date, 'YYYY-MM-DD'), clinical_dept_id "
			+ " FROM asset_info "
			+ " WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') AND tenant_uid = :tenant_uid "
			+ " ORDER BY warranty_date ";

	/*public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(Integer site_id, Integer hospital_id) {

		if(hospital_id != null){
			return db
					.select(asset_warranty)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(asset_warranty_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}*/
	public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_warranty)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}else{
			return db
					.select(asset_warranty_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}

	/*public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(Integer site_id, Integer hospital_id, Integer dept) {

		if(hospital_id != null){
			return db
					.select(asset_warranty)
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept)
					;//.cache();
		}else{
			return db
					.select(asset_warranty_all)
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept)
					;
		}
	}*/
	public Observable<Tuple4<Integer, String, String, Integer>> queryForWarranty(String tenantUID, String siteUID, Integer dept) {

		if(siteUID != null){
			return db
					.select(asset_warranty)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept);
		}else{
			return db
					.select(asset_warranty_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("id"), rs.getString("name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept)
					;
		}
	}

    /*private static final String asset_pm
			= " SELECT po.id as pm_order_id, asset_name, to_char(plan_time, 'YYYY-MM-DD'), ai.clinical_dept_id "
			+ " FROM v2_pm_order po "
			+ " LEFT JOIN asset_info ai "
			+ " ON po.asset_uid = ai.uid "
			+ " WHERE po.tenant_uid=:tenantUID AND po.site_uid=:siteUID AND po.is_finished=false AND po.plan_time<(NOW() + interval '1 weeks') "
			+ " ORDER BY plan_time ";*/
	private static final String asset_pm
			= " SELECT po.id as pm_order_id, asset_name, to_char(plan_time, 'YYYY-MM-DD'), ai.clinical_dept_id "
			+ " FROM v2_pm_order po "
			+ " LEFT JOIN asset_info ai "
			+ " ON po.asset_uid = ai.uid "
			+ " WHERE po.is_finished=false AND po.plan_time<(NOW() + interval '1 weeks') AND (po.tenant_uid IN (:site_uid) OR po.institution_uid IN (:site_uid) OR po.hospital_uid IN (:site_uid) OR po.site_uid IN (:site_uid)) "
			+ " ORDER BY plan_time ";

	/*private static final String asset_pm_all
			= " SELECT po.id as pm_order_id, asset_name, to_char(plan_time, 'YYYY-MM-DD'), ai.clinical_dept_id "
			+ " FROM v2_pm_order po "
			+ " LEFT JOIN asset_info ai "
			+ " ON po.asset_uid = ai.uid "
			+ " WHERE po.tenant_uid=:tenantUID AND po.is_finished=false AND po.plan_time<(NOW() + interval '1 weeks') "
			+ " ORDER BY plan_time ";*/
	private static final String asset_pm_all
			= " SELECT po.id as pm_order_id, asset_name, to_char(plan_time, 'YYYY-MM-DD'), ai.clinical_dept_id "
			+ " FROM v2_pm_order po "
			+ " LEFT JOIN asset_info ai "
			+ " ON po.asset_uid = ai.uid "
			+ " WHERE po.tenant_uid=:tenant_uid AND po.is_finished=false AND po.plan_time<(NOW() + interval '1 weeks') "
			+ " ORDER BY plan_time ";

	/*public Observable<Tuple4<String, String, String, Integer>> queryForPm(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_pm)
					.parameter("tenantUID", tenantUID)
					.parameter("siteUID", siteUID)
					.get(rs ->
							new Tuple4<String, String, String, Integer> (
									rs.getString("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					//rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(asset_pm)
					.parameter("tenantUID", tenantUID)
					.get(rs ->
							new Tuple4<String, String, String, Integer> (
									rs.getString("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}*/
	public Observable<Tuple4<String, String, String, Integer>> queryForPm(String tenantUID, String siteUID) {

		if(siteUID != null){
			return db
					.select(asset_pm)
					.parameter("site_uid", siteUID)
					.get(rs ->
							new Tuple4<String, String, String, Integer> (
									rs.getString("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}else{
			return db
					.select(asset_pm_all)
					.parameter("tenant_uid", tenantUID)
					.get(rs ->
							new Tuple4<String, String, String, Integer> (
									rs.getString("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}
	}

	/*public Observable<Tuple4<Integer, String, String, Integer>> queryForPm(Integer site_id, Integer hospital_id, Integer dept) {

		return db
			.select(asset_pm)
			.parameter("site_id", site_id)
			.parameter("hospital_id", hospital_id)
			.get(rs ->
				new Tuple4<Integer, String, String, Integer> (
						rs.getInt("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					//rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
			.filter(t4 -> t4.getElement3()==dept)
		;
		//.cache();

	}*/
	public Observable<Tuple4<Integer, String, String, Integer>> queryForPm(String tenantUID, String siteUID, Integer dept) {

		return db
				.select(asset_pm)
				.parameter("site_uid", siteUID)
				.get(rs ->
						new Tuple4<Integer, String, String, Integer> (
								rs.getInt("pm_order_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
				.filter(t4 -> t4.getElement3()==dept)
				;
	}

    private static final String assets_meterqa
			= " SELECT left_table.oid, left_table.asset_name, left_table.to_char, asset_info.clinical_dept_id "
			+ " FROM ( SELECT DISTINCT inspection_order.id as oid, asset_id, asset_name, to_char(plan_time, 'YYYY-MM-DD') "
			+ " FROM inspection_order "
			+ " INNER JOIN inspection_order_detail "
			+ " ON inspection_order.id = inspection_order_detail.order_id "
			+ " WHERE inspection_order.site_id = :site_id AND inspection_order.hospital_id=:hospital_id AND plan_time<=(NOW() + interval '2 months') AND is_finished=false AND order_type=%s "
			+ " ORDER BY to_char ) as left_table "
			+ " LEFT JOIN asset_info "
			+ " ON left_table.asset_id = asset_info.id "
			+ " ORDER BY asset_id ";

	private static final String assets_meterqa_all
			= " SELECT left_table.oid, left_table.asset_name, left_table.to_char, asset_info.clinical_dept_id "
			+ " FROM ( SELECT DISTINCT inspection_order.id as oid, asset_id, asset_name, to_char(plan_time, 'YYYY-MM-DD') "
			+ " FROM inspection_order "
			+ " INNER JOIN inspection_order_detail "
			+ " ON inspection_order.id = inspection_order_detail.order_id "
			+ " WHERE inspection_order.site_id = :site_id AND plan_time<=(NOW() + interval '2 months') AND is_finished=false AND order_type=%s "
			+ " ORDER BY to_char ) as left_table "
			+ " LEFT JOIN asset_info "
			+ " ON left_table.asset_id = asset_info.id "
			+ " ORDER BY asset_id ";

	public Observable<Tuple4<Integer, String, String, Integer>> queryForMeterqa(Integer site_id, Integer hospital_id, Integer meterqa) {

		if(hospital_id != null){
			return db
					.select(String.format(assets_meterqa, meterqa))
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("oid"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					//rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
			//.cache();
		}else{
			return db
					.select(String.format(assets_meterqa_all, meterqa))
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("oid"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					;
		}

	}

	public Observable<Tuple4<Integer, String, String, Integer>> queryForMeterqa(Integer site_id, Integer hospital_id, Integer meterqa, Integer dept) {

		if(hospital_id != null){
			return db
					.select(String.format(assets_meterqa, meterqa))
					.parameter("site_id", site_id)
					.parameter("hospital_id", hospital_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("oid"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					//rs.getInt("asset_id"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept)
					;
			//.cache();
		}else{
			return db
					.select(String.format(assets_meterqa_all, meterqa))
					.parameter("site_id", site_id)
					.get(rs ->
							new Tuple4<Integer, String, String, Integer> (
									rs.getInt("oid"), rs.getString("asset_name"), rs.getString("to_char"), rs.getInt("clinical_dept_id") ) )
					.filter(t4 -> t4.getElement3()==dept)
					;
		}
	}
}
