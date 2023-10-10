-- YHAAO-19547 设备超期页面新增达芬奇报表链接
INSERT INTO public.config_item (category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select 1, 'https://apm-acct-stg.gehealthcloud.cn/davinci/externalSharePage?dashboardId=51', '设备超期页面达芬奇url', 'deviceOverdue', '设备超期', 't', 't', 1 where not exists (select * from config_item where key = 'deviceOverdue');
