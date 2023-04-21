-- YHAAO-18200
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '无人值守是否开启审批', 'enableUnattendedApproval', '无人值守是否开启审批', 'f', 't', 3 where not exists (select * from config_item where key = 'enableUnattendedApproval');
-- YHAAO-18438
update mvs_report_info set asset_code68 = (select msg_key from i18n_message where msg_type = function_group and value_zh = asset_code68 and site_id = -1) where asset_code68 is not null;