-- YHAAO-19724 供应商管理-增加批量导入功能
ALTER TABLE supplier ALTER COLUMN tel TYPE varchar(500);


-- 增加配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '设备报废流程', 'assetScrap', '设备报废流程', 'f', 't', 3 where not exists (select * from config_item where key = 'assetScrap');
