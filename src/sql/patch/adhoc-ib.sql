DROP VIEW IF EXISTS acc_userasset_info_dblink;
create view acc_userasset_info_dblink as select * from dblink('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d','select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
and t1.tenant_uid = t3.org_uid and t3.org_type = 0 AND t1.system_id like ''0824%'' and t2.wechat_id is not NULL and (t2.is_active = TRUE or t2.is_active is null) and (t2.is_deleted = FALSE or t2.is_deleted is null)
UNION
select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
and t1.institution_uid = t3.org_uid and t3.org_type = 1 AND t1.system_id like ''0824%'' and t2.wechat_id is not NULL and (t2.is_active = TRUE or t2.is_active is null) and (t2.is_deleted = FALSE or t2.is_deleted is null)
UNION
select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
and t1.hospital_uid = t3.org_uid and t3.org_type = 2 AND t1.system_id like ''0824%'' and t2.wechat_id is not NULL and (t2.is_active = TRUE or t2.is_active is null) and (t2.is_deleted = FALSE or t2.is_deleted is null)
UNION
select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
and t1.site_uid = t3.org_uid and t3.org_type = 3 AND t1.system_id like ''0824%'' and t2.wechat_id is not NULL and (t2.is_active = TRUE or t2.is_active is null) and (t2.is_deleted = FALSE or t2.is_deleted is null)
UNION
select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
and t1.clinical_dept_uid = t3.org_uid and t3.org_type = 4 AND t1.system_id like ''0824%'' and t2.wechat_id is not NULL and (t2.is_active = TRUE or t2.is_active is null) and (t2.is_deleted = FALSE or t2.is_deleted is null)') AS t(system_id varchar(255),wechat_id varchar(255));


DROP VIEW IF EXISTS zkb_ib_cross_user_dblink;
create view zkb_ib_cross_user_dblink as  SELECT t.id,
    t.account_creation_type,
    t.admin_mode,
    t.app_alias,
    t.default_env,
    t.email,
    t.first_visit_date,
    t.ge_sso,
    t.ge_user_type,
    t.hospital_id,
    t.hospital_uid,
    t.institution_uid,
    t.is_active,
    t.is_audit_admin,
    t.is_deleted,
    t.is_ge_op,
    t.is_ge_wechat,
    t.is_local_admin,
    t.is_locked,
    t.is_online,
    t.is_site_admin,
    t.is_super_admin,
    t.last_login_time,
    t.leader_user_id,
    t.login_admin,
    t.login_name,
    t.mvs_role_type,
    t.name,
    t.name_py_code,
    t.org_id,
    t.org_level,
    t.org_uid,
    t.parent_uid,
    t.password,
    t.password_error_count,
    t.password_update_date,
    t."position",
    t.pwd_salt,
    t.region,
    t.site_id,
    t.site_uid,
    t.telephone,
    t.tenant_uid,
    t.uid,
    t.user_id,
    t.user_type,
    t.wechat_id,
    t.wechat_nickname,
    t.ma_ib_open_id,
    t.wx_union_id
   FROM dblink('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d'::text, 'select id,
	account_creation_type,
	admin_mode,
	app_alias,
	default_env,
	email,
	first_visit_date,
	ge_sso,
	ge_user_type,
	hospital_id,
	hospital_uid,
	institution_uid,
	is_active,
	is_audit_admin,
	is_deleted,
	is_ge_op,
	is_ge_wechat,
	is_local_admin,
  is_locked,
  is_online,
  is_site_admin,
  is_super_admin,
	last_login_time,
	leader_user_id,
	login_admin,
	login_name,
	mvs_role_type,
	name,
	name_py_code,
	org_id,
	org_level,
	org_uid,
	parent_uid,
	password,
	password_error_count,
	password_update_date,
	position,
	pwd_salt,
	region,
	site_id,
	site_uid,
	telephone,
	tenant_uid,
	uid,
	user_id,
	user_type,
	wechat_id,
	wechat_nickname,
  ma_ib_open_id,
	wx_union_id
	from zkb_ib_cross_user'::text) t(id character(32), account_creation_type integer, admin_mode boolean, app_alias character varying(40), default_env integer, email character varying(32), first_visit_date timestamp(6) without time zone, ge_sso character varying(255), ge_user_type integer, hospital_id integer, hospital_uid character varying(255), institution_uid character varying(255), is_active boolean, is_audit_admin boolean, is_deleted boolean, is_ge_op boolean, is_ge_wechat boolean, is_local_admin boolean, is_locked boolean, is_online boolean, is_site_admin boolean, is_super_admin boolean, last_login_time timestamp(6) without time zone, leader_user_id integer, login_admin character varying(255), login_name character varying(255), mvs_role_type integer, name character varying(255), name_py_code character varying(255), org_id integer, org_level integer, org_uid character varying(255), parent_uid character varying(255), password character varying(255), password_error_count integer, password_update_date timestamp(6) without time zone, "position" character varying(255), pwd_salt character varying(64), region integer, site_id integer, site_uid character varying(255), telephone character varying(16), tenant_uid character varying(255), uid character varying(255), user_id integer, user_type character varying(255), wechat_id character varying(255), wechat_nickname character varying(255), ma_ib_open_id character varying(255), wx_union_id character varying(255));

DROP VIEW IF EXISTS zkb_ib_cross_relation_ship_dblink;
create view zkb_ib_cross_relation_ship_dblink as SELECT t.id,
    t.login_name,
    t.qr_code,
    t.syetem_id
   FROM dblink('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d'::text, 'select
	id,
	login_name,
	qr_code,
	system_id
	from zkb_ib_cross_relation_ship'::text) t(id character varying(32), login_name character varying(255), qr_code character varying(255), syetem_id character varying(255))