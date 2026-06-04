-- YHAAO-28074
INSERT INTO public.config_item (uid, name, category, key, value_type, description, super_admin_edit, default_value, user_parent_config)SELECT replace(uuid_generate_v4()::text,'-',''),'计量结果日期预警',7, 'measuringDateWarning',3,'计量结果页是否显示日期校验预警（计量日期、有效期至为空或日期不合法时标黄提示）',false,'false',false WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'measuringDateWarning');

-- YHAAO-28258
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 21, '7', '院区调剂退货时限（天）', 'hospitalAdjustmentReturnTime', '院区调剂退货时限（天）', 'f','t',2 where not exists(select * from config_item where key = 'hospitalAdjustmentReturnTime');