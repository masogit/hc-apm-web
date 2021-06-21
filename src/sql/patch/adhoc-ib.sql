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
create view zkb_ib_cross_user_dblink as  SELECT *
FROM
	dblink ('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d' ,
	'select id,
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
	from zkb_ib_cross_user'  ) T (
	id  char( 32 ),
	account_creation_type int4,
	admin_mode bool,
	app_alias varchar( 40 ),
	default_env int4,
	email varchar( 32 ),
	first_visit_date timestamp(6),
	ge_sso varchar( 255 ),
	ge_user_type int4,
	hospital_id int4,
	hospital_uid varchar( 255 ),
	institution_uid varchar( 255 ),
	is_active bool,
	is_audit_admin bool,
	is_deleted bool,
	is_ge_op bool,
	is_ge_wechat bool,
	is_local_admin bool,
	is_locked bool,
	is_online bool,
	is_site_admin bool,
	is_super_admin bool,
	last_login_time timestamp(6),
	leader_user_id int4,
	login_admin varchar( 255 ),
	login_name varchar( 255 ),
	mvs_role_type int4,
	name varchar( 255 ),
	name_py_code varchar( 255 ),
	org_id int4,
	org_level int4,
	org_uid varchar( 255 ),
	parent_uid varchar( 255 ),
	password varchar( 255 ),
	password_error_count int4,
	password_update_date timestamp(6),
	position varchar( 255 ),
	pwd_salt varchar(64),
	region int4,
	site_id int4,
	site_uid varchar( 255 ),
	telephone varchar( 16 ),
	tenant_uid varchar( 255 ),
	uid varchar( 255 ),
	user_id int4,
	user_type varchar( 255 ),
	wechat_id varchar( 255 ),
	wechat_nickname varchar(255),
	ma_ib_open_id varchar(255),
	wx_union_id varchar( 255 )
	);

DROP VIEW IF EXISTS zkb_ib_cross_relation_ship_dblink;
create view zkb_ib_cross_relation_ship_dblink as  SELECT t.id,
	t.login_name,
	t.qr_code,
	t.syetem_id
FROM
	dblink ('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d' :: TEXT, 'select
	id,
	login_name,
	qr_code,
	system_id
	from zkb_ib_cross_relation_ship' :: TEXT ) T (
	ID CHARACTER VARYING ( 32 ),
	login_name CHARACTER VARYING ( 255 ),
	qr_code CHARACTER VARYING ( 255 ),
	syetem_id CHARACTER VARYING ( 255 )
	);