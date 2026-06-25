-- YHAAO-28510增加工单上面显示球管信息
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否显示球管信息', 'workOrderShowTubeInstall', '维修工单是否显示球管信息', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowTubeInstall');
