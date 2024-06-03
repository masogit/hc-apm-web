-- YHAAO-21750 盘点查询优化
update inventory_order_detail set is_last = true from  (
select id, ROW_NUMBER() OVER (PARTITION BY asset_id ORDER BY last_modified_date DESC) AS rn from inventory_order_detail  ) t
where inventory_order_detail.id = t.id and t.rn = 1;

update inventory_issue set is_last = true from  (
select id, ROW_NUMBER() OVER (PARTITION BY asset_id ORDER BY last_modified_date DESC) AS rn from inventory_issue  ) t
where inventory_issue.id = t.id and t.rn = 1;


insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '启用不良事件哨兵模式', 'enableAdverseEventSentinelMode', '启用不良事件哨兵模式', 'f','t',3 where not exists(select * from config_item where key = 'enableAdverseEventSentinelMode');



insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报ClientId', 'adverseEventsReportClientId', '不良事件上报ClientId', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportClientId');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报userName', 'adverseEventsReportUserName', '不良事件上报userName', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportUserName');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报password', 'adverseEventsReportPassword', '不良事件上报password', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportPassword');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报appKey', 'adverseEventsReportAppKey', '不良事件上报appKey', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportAppKey');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报Account', 'adverseEventsReportAccount', '不良事件上报Account', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportAccount');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件上报SecretKey', 'adverseEventsReportSecretKey', '不良事件上报SecretKey', 'f','t',1 where not exists(select * from config_item where key = 'adverseEventsReportSecretKey');

ALTER TABLE public.delay_template_message ALTER COLUMN param TYPE text USING param::text;

--不良事件历史数据【不良工单类型】赋值
update adverse_events set adverse_event_type = '1'  where adverse_event_type is null;