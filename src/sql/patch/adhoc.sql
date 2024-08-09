--报修签到增加【签到时间需扫二维码获取】配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '签到时间需扫二维码获取', 'enableSignNeedScanQrCode', '签到时间需扫二维码获取', 'f','t',3 where not exists(select * from config_item where key = 'enableSignNeedScanQrCode');

--YHAAO-22413 工单维修判断是否需要维保合同
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '工单维修是否需要判断维保合同', 'enableMaintenanceContract', '工单维修是否需要判断维保合同', 'f','t',3 where not exists(select * from config_item where key = 'enableMaintenanceContract');
