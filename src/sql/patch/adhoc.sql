-- YHAAO-27960 配置项-临床助手“查询天数”，改名为“有问题工单查询时间范围”
UPDATE config_item SET name = '有问题工单查询时间范围' WHERE key = 'setQueryNumberDays' AND name != '有问题工单查询时间范围';

-- YHAAO-27876 维修中列表-存在备件审核中的工单在列表中增加标识
update config_item  set  default_value='[{"label":"设备名称","field":"assetName","required":true,"rendered":true},{"label":"制造商序列号","field":"manufacturerSerialNumber","required":true,"rendered":true},{"label":"设备编号（院方）","field":"departNum","required":true,"rendered":true},{"label":"设备型号","field":"functionType","required":false,"rendered":true},{"label":"所属院区","field":"hospitalId","required":true,"rendered":true},{"label":"所属科室","field":"clinicalDeptName","required":false,"rendered":true},{"label":"报修单号","field":"srNum","required":true,"rendered":true},{"label":"安装位置","field":"locationName","required":false,"rendered":true},{"label":"报修人","field":"requestorName","required":false,"rendered":true},{"label":"维修人","field":"requireName","required":false,"rendered":true},{"label":"报修时间","field":"requestTime","required":true,"rendered":true},{"label":"当前步骤","field":"currentStep","required":true,"rendered":true},{"label":"工单类型","field":"intExtType","required":false,"rendered":true},{"label":"故障现象","field":"requestReason","required":false,"rendered":true},{"label":"主子工单","field":"mainSub","required":false,"rendered":true},{"label":"资产分类","field":"assetType","required":false,"rendered":true},{"label":"备件状态","field":"approvalStatus","required":false,"rendered":false}]'  where   key like  'WorkOrderListColumn%';

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '开启测试功能', 'EnableTestFunction', '开启测试功能', 'f','t',3 where not exists(select * from config_item where key = 'EnableTestFunction');

--YHAAO-27869
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '保养规则是否显示评分字段', 'pmRuleShowScoreFlag', '保养规则是否显示评分字段', 'f','t',3 where not exists(select * from config_item where key = 'pmRuleShowScoreFlag');

-- YHAAO-28014 web端时间记录配置
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示维修记录', 'showRepairRecordTable', '是否显示维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示内部保养记录', 'showMaintainanceRecordTable', '是否显示内部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMaintainanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示盘点记录', 'showInventoryRecordTable', '是否显示盘点记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showInventoryRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示外部保养记录', 'showExternalMaintenanceRecordTable', '是否显示外部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showExternalMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示外部维修记录', 'showExternalRepairRecordTable', '是否显示外部维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showExternalRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示现场保养记录', 'showOnSiteMaintenanceRecordTable', '是否显示现场保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showOnSiteMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示日常检查记录', 'showDailyInspectionRecordTable', '是否显示日常检查记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showDailyInspectionRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示开关机记录', 'showOperationRecordTable', '是否显示开关机记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showOperationRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示计量监控记录', 'showMetrologyMonitorTable', '是否显示计量监控记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMetrologyMonitorTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示不良事件记录', 'showAdverseEventRecordTable', '是否显示不良事件记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showAdverseEventRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示设备流程记录', 'showApprovalWorkflowTable', '是否显示设备流程记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showApprovalWorkflowTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示设备修改记录', 'showModifyRecordTable', '是否显示设备修改记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showModifyRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端维修记录', 'showMobileRepairRecordTable', '是否显示移动端维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端内部保养记录', 'showMobileMaintainanceRecordTable', '是否显示移动端内部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileMaintainanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端盘点记录', 'showMobileInventoryRecordTable', '是否显示移动端盘点记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileInventoryRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端外部保养记录', 'showMobileExternalMaintenanceRecordTable', '是否显示移动端外部保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileExternalMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端外部维修记录', 'showMobileExternalRepairRecordTable', '是否显示移动端外部维修记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileExternalRepairRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端现场保养记录', 'showMobileOnSiteMaintenanceRecordTable', '是否显示移动端现场保养记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileOnSiteMaintenanceRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端日常检查记录', 'showMobileDailyInspectionRecordTable', '是否显示移动端日常检查记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileDailyInspectionRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端开关机记录', 'showMobileOperationRecordTable', '是否显示移动端开关机记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileOperationRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端计量监控记录', 'showMobileMetrologyMonitorTable', '是否显示移动端计量监控记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileMetrologyMonitorTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端不良事件记录', 'showMobileAdverseEventRecordTable', '是否显示移动端不良事件记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileAdverseEventRecordTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端设备流程记录', 'showMobileApprovalWorkflowTable', '是否显示移动端设备流程记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileApprovalWorkflowTable');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 20, 'true', '是否显示移动端设备修改记录', 'showMobileModifyRecordTable', '是否显示移动端设备修改记录', 'f','t',3 where not exists(select 1 from config_item where key = 'showMobileModifyRecordTable');
