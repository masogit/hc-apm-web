-- YHAAO-12614
update asset_alert_config set wechat_message = '检测到您的液位已低于推荐阈值，请密切关注液位变化，做到每天记录，确保远程监控联通；考虑到液氦供货周期长，需提前计划液氦预定和添加。',
 pad_message = '液氦液位已低于推荐阈值，请确保远程监控联通并提前计划液氦预定及添加。'
 where label = '液氦液面高度' and lower_limit is not null;
-- YHAAO-12695
update asset_alert_config set upper_limit = 5.25 where config_type = 104 and is_default = true;
update asset_alert_config set upper_limit = 10 where config_type = 101 and is_default = true;
update asset_alert_config set wechat_message = '冷却水流量异常，流量过低会导致氦压缩机停机，流量过高会导致氦压缩机制冷效率下降，请立刻报修。' where config_type = 101;
update asset_alert_config set upper_limit = 309, lower_limit = 5 where config_type = 105 and is_default = true;
insert into asset_alert_config (id, asset_uid, config_type, create_by, create_date, lower_limit, upper_limit, is_default, is_enabled, check_type, device_type, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_notify_fe, is_notify_ole, is_notify_sales, is_notify_customer,ole_config,message_delay) values ('8370b8vmr1f9431xpa72ff9va06b9zw5', null, 115, null, now(), 3.8, null, true, true, 1, 1, '冷头温度', 4320, '冷头温度异常，冷头制冷效果差，磁体压力会持续升高，有液氦消耗风险。请关注设备是否有其他异常，若有进一步问题请与GE400热线联系。', 'K', null, '冷头温度异常，冷头制冷效果差，磁体压力会持续升高，有液氦消耗风险。请关注设备是否有其他异常，若有进一步问题请与GE400热线联系。',true, false, true, true, false,true);