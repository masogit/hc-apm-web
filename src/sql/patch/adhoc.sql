--YHAAO-27341 新增盘点是否显示维修责任人
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 12, false, '盘点是否显示维修责任人', 'InventoryDisplayRepairOwner', '盘点是否显示维修责任人', 'f', 't', 1 where not exists(select * from config_item where key = 'InventoryDisplayRepairOwner');

-- 增加配置项 取消保养工单取消原因是否必填 YHAAO-27380
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '保养工单取消原因是否必填', 'enableCheckCancelReason', '保养工单取消原因是否必填', 'f', 't', 3 where not exists (select * from config_item where key = 'enableCheckCancelReason');

--his 源数据和收费代码和分组匹配  job 创建索引
CREATE INDEX CONCURRENTLY IF NOT exists idx_a_perfect_lateral ON his_equipment_data_source (org_code, exec_dept_code, device_dept_code, exec_time DESC) WHERE is_matched IS NOT TRUE;