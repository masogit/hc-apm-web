--报修签到增加【签到时间需扫二维码获取】配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '签到时间需扫二维码获取', 'enableSignNeedScanQrCode', '签到时间需扫二维码获取', 'f','t',3 where not exists(select * from config_item where key = 'enableSignNeedScanQrCode');
