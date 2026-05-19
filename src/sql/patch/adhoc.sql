-- YHAAO-28014 web端时间记录配置
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示维修记录', 'showRepairRecordTable', '是否显示维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示内部保养记录', 'showMaintainanceRecordTable', '是否显示内部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMaintainanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示盘点记录', 'showInventoryRecordTable', '是否显示盘点记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showInventoryRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示外部保养记录', 'showExternalMaintenanceRecordTable', '是否显示外部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showExternalMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示外部维修记录', 'showExternalRepairRecordTable', '是否显示外部维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showExternalRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示现场保养记录', 'showOnSiteMaintenanceRecordTable', '是否显示现场保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showOnSiteMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示日常检查记录', 'showDailyInspectionRecordTable', '是否显示日常检查记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showDailyInspectionRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示开关机记录', 'showOperationRecordTable', '是否显示开关机记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showOperationRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示计量监控记录', 'showMetrologyMonitorTable', '是否显示计量监控记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMetrologyMonitorTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示不良事件记录', 'showAdverseEventRecordTable', '是否显示不良事件记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showAdverseEventRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示设备流程记录', 'showApprovalWorkflowTable', '是否显示设备流程记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showApprovalWorkflowTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示设备修改记录', 'showModifyRecordTable', '是否显示设备修改记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showModifyRecordTable');