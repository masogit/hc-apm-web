DROP VIEW IF EXISTS acc_tenant_info_dblink;
CREATE VIEW acc_tenant_info_dblink AS SELECT
	*
FROM
	dblink (
'hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d',
'select
id,
name,
name_en,
alias_name,
site_description,
contact_person,
contact_phone,
contact_email,
location,
location_en,
time_zone,
default_lang,
is_enabled,
wf_auto_step2,
wf_auto_step3,
wf_auto_step4,
wf_auto_step5,
wf_auto_step6,
manhour_price,
created_by,
created_date,
last_modified_by,
last_modified_date,
password_lifetime,
uid,
tenant_config,
type,
active_time,
is_insulate,
active_type,
active_user_id,
initial_password,
initial_password_salt,
contract_no,
test_status,
currency,
is_deleted,
international_version,
have_ib_device,
two_factor_valid,
qrcode_editable
from tenant_info'
	) AS T (
id int4,
name varchar(64),
name_en varchar(64),
alias_name varchar(64),
site_description varchar(256),
contact_person varchar(64),
contact_phone varchar(64),
contact_email varchar(64),
location varchar(64),
location_en varchar(256),
time_zone int4,
default_lang varchar(16),
is_enabled bool,
wf_auto_step2 bool,
wf_auto_step3 bool,
wf_auto_step4 bool,
wf_auto_step5 bool,
wf_auto_step6 bool,
manhour_price float8,
created_by varchar(50),
created_date date,
last_modified_by varchar(50),
last_modified_date date,
password_lifetime int4,
uid	varchar(32),
tenant_config text,
type int4,
active_time	date,
is_insulate	bool,
active_type int4,
active_user_id int4,
initial_password varchar(255),
initial_password_salt varchar(64),
contract_no varchar(255),
test_status int4,
currency int4,
is_deleted bool,
international_version int4,
have_ib_device bool,
two_factor_valid bool,
qrcode_editable bool);

DROP VIEW IF EXISTS acc_userasset_info_dblink;
create view acc_userasset_info_dblink as select * from dblink('hostaddr=172.18.9.27 port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d','select t1.system_id,t2.wechat_id from asset_info t1,user_account t2,user_org t3 where t2.uid = t3.user_uid and t1.is_deleted = FALSE and t1.is_valid = TRUE
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
