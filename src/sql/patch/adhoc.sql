
CREATE index IF NOT exists asset_operation_record_asset_info_uid_idx ON public.asset_operation_record (asset_info_uid);
CREATE INDEX IF NOT exists asset_operation_record_create_date_idx ON public.asset_operation_record (create_date);
CREATE INDEX IF NOT exists asset_operation_record_tenant_uid_idx ON public.asset_operation_record (tenant_uid);


-- YHAAO-21097 积水潭名称判断改为配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '启用审核（核心）组配置', 'enableAuditGroupConfiguration', '审核（核心）组配置', 'f', 't', 3 where not exists (select * from config_item where key = 'enableAuditGroupConfiguration');
-- 同步北京积水潭医院租户下的配置 enableAuditGroupConfiguration
insert into config_item_value (uid,key,value,tenant_uid,institution_uid,hospital_uid,site_uid)select  replace(uuid_generate_v4()::text,'-',''),'enableAuditGroupConfiguration',true,tenant_uid,institution_uid,hospital_uid,site_uid from  org_info oi where site_id=(select  id  from  tenant_info where name='北京积水潭医院') and org_type < 4 and  not exists(select * from config_item_value where key='enableAuditGroupConfiguration' and oi.site_uid=site_uid);

ALTER TABLE approve_schedule ADD COLUMN IF NOT EXISTS tenant_uid char(32);
ALTER TABLE approve_schedule ADD COLUMN IF NOT EXISTS institution_uid char(32);
ALTER TABLE approve_schedule ADD COLUMN IF NOT EXISTS hospital_uid char(32);
ALTER TABLE approve_schedule ADD COLUMN IF NOT EXISTS site_uid char(32);

update approve_schedule apc set tenant_uid=wo.tenant_uid,institution_uid=wo.institution_uid,hospital_uid=wo.hospital_uid,site_uid=wo.site_uid  from v2_work_order wo   where wo.id = apc.business_uid;
-- 报修人电话同步历史数据
update v2_service_request sr set requestor_phone = ua.telephone from user_account ua where sr.requestor_id = ua.id and sr.requestor_phone is null and ua.telephone is not null;





insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '验证工单是否执行复检流程', 'verifyPmOrderReExaminationMode', '验证工单是否执行复检流程', 'f','t',3 where not exists(select * from config_item where key = 'verifyPmOrderReExaminationMode');
