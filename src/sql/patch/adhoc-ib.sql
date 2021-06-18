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
