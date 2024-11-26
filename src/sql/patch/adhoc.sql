--增配置项【不良事件上报是否填入默认场所】
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, 'false', '不良事件上报是否填入默认场所', 'enableInsertUsePlace', '不良事件上报是否填入默认场所', 'f','t',3 where not exists(select * from config_item where key = 'enableInsertUsePlace');

-- YHAAO-23399
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'true', '是否显示外部工单', 'displayExternalWorkOrder', '是否显示外部工单', 'f','t',3 where not exists(select * from config_item where key = 'displayExternalWorkOrder');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'true', '是否显示远程工单', 'displayRemoteWorkOrder', '是否显示远程工单', 'f','t',3 where not exists(select * from config_item where key = 'displayRemoteWorkOrder');
