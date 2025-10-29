-- YHAAO-26029 添加默认维修验收人、保养验收人
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'default', '默认维修验收人', 'defaultRepairPerson', '默认维修验收人', 'f','t',7,'[{"key":"报修人","value":"default"},{"key":"维修人","value":"4"}]' where not exists(select * from config_item where key = 'defaultRepairPerson');
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 5, 'default', '默认保养验收人', 'defaultMaintainPerson', '默认保养验收人', 'f','t',7,'[{"key":"创建人","value":"default"},{"key":"保养人","value":"Maintain"}]' where not exists(select * from config_item where key = 'defaultMaintainPerson');


-- YHAAO-26102
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '主页是否显示平均响应时间', 'showHomepageAverageResponseTime', '主页是否显示平均响应时间', 'f','t',3,'' where not exists(select * from config_item where key = 'showHomepageAverageResponseTime');
-- YHAAO-25721
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 13, '0', '主页平均响应时间最大天数', 'homepageAverageResponseTimeMaxDays', '主页平均响应时间最大天数', 'f','t',2,'^(?:1[0-3](?:\.\d{1,2})?|14(?:\.0{1,2})?|[1-9](?:\.\d{1,2})?|0(?:\.\d{1,2})?)$' where not exists(select * from config_item where key = 'homepageAverageResponseTimeMaxDays');
-- YHAAO-24901
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '医工是否可填写外修人信息', 'enableEngineersWriteRepairOutside', '医工是否可填写外修人信息', 'f','t',3,'' where not exists(select * from config_item where key = 'enableEngineersWriteRepairOutside');

-- YHAAO-26115 批量添加协作者工时类型
update v2_pm_order set coworker_entry_type = '0' where coworker_entry_type is null;

--  YHAAO-26159  无台账报修派工人
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, '', '无台账报修派工人1', 'NoAssetDispatchWorkers1', '无台账报修派工人1', 'f', 't', 1 where not exists(select * from config_item where key = 'NoAssetDispatchWorkers1');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, '', '无台账报修派工人2', 'NoAssetDispatchWorkers2', '无台账报修派工人2', 'f', 't', 1 where not exists(select * from config_item where key = 'NoAssetDispatchWorkers2');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, '', '医学工程处主任', 'AssetHead', '医学工程处主任', 'f', 't', 1 where not exists(select * from config_item where key = 'AssetHead');

-- YHAAO-26026 添加设备维保截止日期工单提醒配置项
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '工单维修是否需要判断维保截止日期', 'enableMaintenanceDate', '工单维修是否需要判断维保截止日期', 'f','t',3,'' where not exists(select * from config_item where key = 'enableMaintenanceDate');
