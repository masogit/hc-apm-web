--2021-01-27 消息订阅 修改部分角色配置 start
update user_subscription_push set sub_status=true,default_send_status=true, send_status=true where menu_type='6' and role_position='SalesManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='1' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='7' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='8' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='11' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='12' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='1' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='7' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='8' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='11' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='12' and role_position='FeManager';
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '1', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=1 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '2', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=2 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '3', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=3 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '4', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=4 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '5', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=5 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '6', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=6 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '7', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=7 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '8', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=8 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '9', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=9 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '10', false, true, true, true, 'super'
where not exists (select 1 from user_subscription_push where menu_type=10 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '11', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=11 and tag='super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '12', false, true, true, false, 'super'
where not exists (select 1 from user_subscription_push where menu_type=12 and tag='super');
--2020-01-27 end

--2020-02-04 update sr name
update service_request_report service set file_name = (case when trans.transfer_name is not null then trans.transfer_name else service.file_name end) from (select file.id, replace(file.file_name, file.sub_file, transfer.mapped_name) transfer_name  from (select sr.id, event.event_type, sr.file_name, substring(sr.file_name, position('_' in sr.file_name)+1) sub_file from service_request_report sr, asset_lifecycle_event event where sr.alce_id = event.id and position('_' in sr.file_name) > 0) file, sr_name_transfer_mapping transfer where file.event_type = transfer.sr_type and upper(file.sub_file) = upper(transfer.file_name)) trans where service.id = trans.id;

--2020-02-04 update ct and petct alert config
update asset_alert_config set label = '图像硬盘空间', pad_message = replace(pad_message, '磁盘空间', '图像硬盘空间'), wechat_message = replace(wechat_message, '磁盘空间', '图像硬盘空间') where config_type in (201, 501);

--2020-02-04 game plan添加index
create index asset_service_member_asset_uid on asset_service_member (asset_uid);
create index asset_service_member_user_uid on asset_service_member (user_uid);
create index owoc_report_data_asset_uid on owoc_report_data (asset_uid);
create index iwic_monthly_report_asset_uid on iwic_monthly_report (asset_uid);
create index us_report_dept_data_asset_uid on us_report_dept_data (asset_uid);
create index us_report_data_asset_uid on us_report_data (asset_uid);
create index wechat_message_history_user_id on wechat_message_history (user_id);
create index wechat_message_history_system_id on wechat_message_history (system_id);
create index wechat_message_history_request_uid on wechat_message_history (request_uid);
create index ole_operate_log_system_id on ole_operate_log (system_id);
create index ole_operate_log_user_id on ole_operate_log (user_id);
create index ole_operate_log_request_uid on ole_operate_log (request_uid);
create index user_activity_log_time on user_activity_log (time);
create index user_activity_log_login_name on user_activity_log (login_name);

--2020-02-19 update pad and wechat message
update asset_alert_config set pad_message = '磁体压力异常，磁体压力过低，有磁体结冰风险；磁体压力过高，会影响图像质量。请关注设备是否有其他异常，若有进一步问题请与GE400热线联系。', wechat_message = '磁体压力异常，磁体压力过低，有磁体结冰风险；磁体压力过高，会影响图像质量。请关注设备是否有其他异常，若有进一步问题请与GE400热线联系。'  where config_type = 104;

