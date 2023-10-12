-- YHAAO-19547 设备超期页面新增达芬奇报表链接
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select 'e212e92a-b234-4a26-9658-c0d709fabd0f', 1, '51', '设备超期达芬奇dashboardId', 'deviceOverdue', '设备超期', 't', 't', 1 where not exists (select * from config_item where key = 'deviceOverdue');
