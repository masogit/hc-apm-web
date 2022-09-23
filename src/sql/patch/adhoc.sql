-- YHAAO-16622 云阜需求-手机端，设备调剂-各个页面的设备信息显示调整
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, validation, value_type) select '3f6953a582a5488c91b41ce53ad34588', 2, '1', '调剂名称信息自定义配置', 'assetAllocate', '调剂设备名称配置', 'f', 'f', '[{"key":"设备编号","value":"1"},{"key":"二维码","value":"2"}]', 7 where not exists (select * from config_item where key = 'assetAllocate');
-- YHAAO-16616 到货清单提交后，展开的信息要根据上次记录带入
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select '8f4f32053d8443f284f2a468ce09b101', 3, 'false', '到货清单同类设备是否自动填充', 'arrivalListAutoFill', '到货清单同类设备是否自动填充', 'f', 't', 3 where not exists (select * from config_item where key = 'arrivalListAutoFill');
-- YHAAO-15835 计量信息-批量上传计量报告附件
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select '79fb9c1ed56943bc8df24ee29fef6471', 7, 'false', '批量上传计量报告附件', 'batch_upload', '批量上传计量报告附件', 'f', 'f', 3 where not exists (select * from config_item where key = 'batch_upload');
