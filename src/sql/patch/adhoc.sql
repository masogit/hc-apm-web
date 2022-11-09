-- YHAAO-17216 模板定义添加类型字段
UPDATE table_definition SET template_type = 'Maintain' where template_type is null;
-- YHAAO-15761 供应商小程序-增加配置项， 编辑照片说明
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select 'e2eccc5875994efab9742554d930f627', 3, '出厂编号、设备整体外观、医院编号标签', '供应商小程序到货清单照片说明配置', 'arrivalListPhotoCaption', '到货清单照片说明', 'f', 'f', 1 where not exists (select * from config_item where key = 'arrivalListPhotoCaption');

