-- YHAAO-24418 添加配置项控制合同新增字段
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '合同是否显示维保字段', 'displayMaintenanceField', '合同是否显示维保字段', 'f','t',3 where not exists(select * from config_item where key = 'displayMaintenanceField');

-- YHAAO-24782 配置项增加维修脚本
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, '', '测试租户脚本维修文件', 'workOrderScript', '测试租户脚本维修文件', 't','f',1 where not exists(select * from config_item where key = 'workOrderScript');