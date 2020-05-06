--2020.04.30
update asset_alert_config set lower_limit = null where config_type = '113';
insert into asset_alert_config (id, asset_uid, config_type, create_by, create_date, lower_limit, upper_limit, is_default, is_enabled, check_type, device_type, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_notify_fe, is_notify_ole, is_notify_sales, is_notify_customer,ole_config,message_delay) values ('8370b7vmr1f9431xpa72ff9vf06b9zw1', null, 113, null, now(), 60.0, 70.0, true, true, 5, 1, '液氦液面高度', 1440, '液氦液位<70%，请确保远程监控联通并提前计划液氦预定及添加。', '%', null, '检测到您的液位已经低于70%，请密切关注液位变化，做到每天记录，确保远程监控联通；考虑到液氦供货周期长，需提前计划液氦预定和添加。',true, false, true, true, false,true);
insert into asset_alert_config (id, asset_uid, config_type, create_by, create_date, lower_limit, upper_limit, is_default, is_enabled, check_type, device_type, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_notify_fe, is_notify_ole, is_notify_sales, is_notify_customer,ole_config,message_delay) values ('6143b7dpc1f9431vccry8a99f06bobn3', null, 101, null, now(), 4, null, true, true, 1, 1, '冷却水流量', 1440, '冷却水流量异常，流量过低会导致氦压缩机停机，请立刻报修。', 'LPM', null, '冷却水流量异常，流量过低会导致氦压缩机停机，请立刻报修。',true, false, true, true, false,true);

--磁体压力 删掉message中多余的>符号
update asset_alert_config set wechat_message='磁体压力异常，磁体压力过低，有磁体结冰风险；磁体压力过高，会影响图像质量。请关注设备是否有其他异常，若有进一步问题请与GE400热线联系。' where config_type = '104';

-- 2020.04.30 YHAAO-6543 GEOPS发送消息增加定时发送
ALTER TABLE ops_message ALTER COLUMN status SET DATA TYPE int4 USING status::int4, ALTER COLUMN status SET DEFAULT 0;
ALTER TABLE ops_message ALTER COLUMN message TYPE text;
ALTER TABLE ops_message ALTER COLUMN sys_id TYPE text;