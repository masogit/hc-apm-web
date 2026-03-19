--YHAAO-27341 新增盘点是否显示维修责任人
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 12, false, '盘点是否显示维修责任人', 'InventoryDisplayRepairOwner', '盘点是否显示维修责任人', 'f', 't', 1 where not exists(select * from config_item where key = 'InventoryDisplayRepairOwner');

-- 增加配置项 取消保养工单取消原因是否必填 YHAAO-27380
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '保养工单取消原因是否必填', 'enableCheckCancelReason', '保养工单取消原因是否必填', 'f', 't', 3 where not exists (select * from config_item where key = 'enableCheckCancelReason');

--his 源数据和收费代码和分组匹配  job 创建索引
CREATE INDEX CONCURRENTLY IF NOT exists idx_a_perfect_lateral ON his_equipment_data_source (org_code, exec_dept_code, device_dept_code, exec_time DESC) WHERE is_matched IS NOT TRUE;

--YHAAO-26795 添加计量消息提醒配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 7, '', '计量提醒提前天数', 'measuringRemindDays', '计量提醒提前天数', 'f', 't', 2 where not exists(select * from config_item where key = 'measuringRemindDays');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 7, '', '计量提醒提前次数', 'measuringReminderTimes', '计量提醒提前次数', 'f', 't', 2 where not exists(select * from config_item where key = 'measuringReminderTimes');

-- 增加配置项 维修工单备件预估单价是否必填 YHAAO-27474
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单备件预估单价是否必填', 'enableCheckPartsPrice', '维修工单备件预估单价是否必填', 'f', 't', 3 where not exists (select * from config_item where key = 'enableCheckPartsPrice');



update config_item set value_type = 9, default_value = '{"rendered":false,"label":"维修工单是否显示诊断过程","required":"false","requiredMessage":"维修工单是否显示诊断过程为必输项, 不能为空！"}' where key = 'showDiagnosticProcess';

delete from config_item_value  where key = 'showDiagnosticProcess';

-- YHAAO-27471
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 7, '{}', '识别后置处理规则', 'identifyPostProcessorRule', '识别后置处理规则', 'f', 't', 1 where not exists(select * from config_item where key = 'identifyPostProcessorRule');
-- YHAAO-27381
update shared_asset_req_detail set charging_begin_time = '2026-02-14 15:37:55' where id='2c9b85829c28ab57019c89216fd269c8';


