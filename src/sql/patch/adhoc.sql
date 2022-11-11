-- YHAAO-17216 模板定义添加类型字段
UPDATE table_definition SET template_type = 'Maintain' where template_type is null;
-- YHAAO-15761 供应商小程序-增加配置项， 编辑照片说明
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select 'e2eccc5875994efab9742554d930f627', 3, '出厂编号、设备整体外观、医院编号标签', '供应商小程序到货清单照片说明配置', 'arrivalListPhotoCaption', '到货清单照片说明', 'f', 'f', 1 where not exists (select * from config_item where key = 'arrivalListPhotoCaption');

-- 刷新设备联网情况，是否使用登记 字段的历史数据
UPDATE asset_info SET networking_status = 0 WHERE networking_status IS NULL;
UPDATE asset_info SET is_use_registration = TRUE WHERE is_use_registration IS NULL;

-- YHAAO-15915 医工角色新增设备绩效记录页面菜单权限
UPDATE sys_role set menus = menus || ',asset.achievements' WHERE "name" = 'AssetStaff' ;