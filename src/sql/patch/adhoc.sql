-- YHAAO-28510增加工单上面显示球管信息
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否显示球管信息', 'workOrderShowTubeInstall', '维修工单是否显示球管信息', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowTubeInstall');

-- 新增日常检查是否可忽略配置项
INSERT INTO public.config_item (uid, name, category, key, value_type, description, super_admin_edit, default_value, user_parent_config)SELECT replace(uuid_generate_v4()::text,'-',''),'日常检查是否可忽略',8, 'dailyCheckIgnorable',3,'日常检查是否可忽略',false,'false',true WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'dailyCheckIgnorable');

