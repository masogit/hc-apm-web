-- 2020.05.28 YHAAO-6501 pin date or phantom date < 90 days
insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet62f843e9b008629', 506, now(), null, 0, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源已经逾期，请您加速校正源购买和更换进程，逾期使用校正源或不进行Daily QA质控都将会严重影响PET图像质量。谢谢！', true, false, true);

insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet63f843e9b008629', 507, now(), 0, 60, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源马上到期，由于校正源购买周期很长，请您尽快准备校正源更换事宜，逾期使用校正源将会严重影响PET图像质量。谢谢！', true, false, true);

insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet64f843e9b008629', 508, now(), 60, 90, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源即将到期，由于校正源购买周期很长，请您尽早开始准备校正源更换事宜，谢谢！', true, false, true);