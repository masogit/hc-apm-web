-- YHAAO-12909
update asset_alert_config set wechat_message = '冷却水流量异常，流量过高会导致氦压缩机制冷效率下降。',
 pad_message = '冷却水流量异常，流量过高会导致氦压缩机制冷效率下降。',
 lower_limit = null
 where config_type = 101;
insert into asset_alert_config (
id, asset_uid, config_type, create_by, create_date, lower_limit, upper_limit, is_default, is_enabled, check_type, device_type, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_notify_fe, is_notify_ole, is_notify_sales, is_notify_customer,ole_config,message_delay) values (
'8370b8vmr1f9431zyj72ff9va06b9fy6', null, 111, null, now(), 4, null, true, true, 1, 1, '冷却水流量', 1440, '冷却水流量异常，流量过低会导致氦压缩机停机，请立刻报修。', 'LPM', null, '冷却水流量异常，流量过低会导致氦压缩机停机，请立刻报修。',true, false, true, true, false,true);

