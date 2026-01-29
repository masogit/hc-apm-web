-- YHAAO-27044 维修工作台增加“资产分类”字段
update config_item set default_value = '[{"label":"设备名称","field":"assetName","required":true,"rendered":true},{"label":"制造商序列号","field":"manufacturerSerialNumber","required":true,"rendered":true},{"label":"设备编号（院方）","field":"departNum","required":true,"rendered":true},{"label":"设备型号","field":"functionType","required":false,"rendered":true},{"label":"所属院区","field":"hospitalId","required":true,"rendered":true},{"label":"所属科室","field":"clinicalDeptName","required":false,"rendered":true},{"label":"报修单号","field":"srNum","required":true,"rendered":true},{"label":"安装位置","field":"locationName","required":false,"rendered":true},{"label":"报修人","field":"requestorName","required":false,"rendered":true},{"label":"维修人","field":"requireName","required":false,"rendered":true},{"label":"报修时间","field":"requestTime","required":true,"rendered":true},{"label":"当前步骤","field":"currentStep","required":true,"rendered":true},{"label":"工单类型","field":"intExtType","required":false,"rendered":true},{"label":"故障现象","field":"requestReason","required":false,"rendered":true},{"label":"主子工单","field":"mainSub","required":false,"rendered":true},{"label":"资产分类","field":"assetType","required":false,"rendered":true}]' where key like 'WorkOrderListColumn_%';
-- YHAAO-26840 删除未开启效期管理的资产到期提醒消息
DELETE FROM reminder_rule WHERE tenant_uid IN (SELECT uid FROM tenant_info WHERE feature_list not like '%ASSET_DEVICE_OVERDUE%');



--YHAAO-27049
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否显示诊断过程', 'showDiagnosticProcess', '维修工单是否显示诊断过程', 'f','t',3 where not exists(select * from config_item where key = 'showDiagnosticProcess');


-- YHAAO-27053
insert into public.config_item(uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text, '-', ''), 4, '[{"label": "工单类型", "field":"orderType"},{"label": "院区名称", "field":"siteName"},{"label": "设备名称", "field":"assetName"},,{"label": "工单编号", "field":"srNum"}]', '维修报告单名称配置', 'WorkOrderReportNameConfiguration', '维修报告单名称配置', 'f', 't', 10 where not exists ( select * from config_item where key = 'WorkOrderReportNameConfiguration');

insert into public.config_item(uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text, '-', ''), 5, '[{"label": "工单类型", "field":"orderType"},{"label": "院区名称", "field":"siteName"},{"label": "设备名称", "field":"assetName"},,{"label": "工单编号", "field":"srNum"}]', '保养报告单名称配置', 'PmOrderReportNameConfiguration', '保养报告单名称配置', 'f', 't', 10
    where not exists ( select * from config_item where key = 'PmOrderReportNameConfiguration');

-- YHAAO-27155 是否显示主子设备按钮
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '是否显示主子设备按钮', 'showMasterEquipment', '是否显示主子设备按钮', 'f','t',3 where not exists(select * from config_item where key = 'showMasterEquipment');

-- 供应商字段长度设置128
ALTER TABLE asset_info ALTER COLUMN vendor TYPE VARCHAR(128);
ALTER TABLE supplier ALTER COLUMN name TYPE VARCHAR(128);


-- YHAAO-27110
insert into public.config_item(uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
select replace(uuid_generate_v4()::text, '-', ''), 18, '[{"label":"设备名称","field":"assetName","required":true,"rendered":true},{"label":"展示名称","field":"displayName","required":false,"rendered":true},{"label":"设备型号","field":"functionType","required":false,"rendered":true},{"label":"资产编号","field":"financingNum","required":false,"rendered":true},{"label":"品牌/制造商简称","field":"makerShortName","required":false,"rendered":true},{"label":"制造商序列号","field":"serialNum","required":false,"rendered":true},{"label":"二维码","field":"qrCode","required":false,"rendered":true},{"label":"所属科室","field":"clinicalDeptName","required":false,"rendered":true},{"label":"设备分类","field":"assetGroup","required":false,"rendered":true},{"label":"采购金额","field":"purchasePrice","required":false,"rendered":true},{"label":"采购日期","field":"purchaseDate","required":false,"rendered":true},{"label":"收入(元)","field":"income","required":false,"rendered":true},{"label":"成本(元)","field":"cost","required":false,"rendered":true},{"label":"利润(元)","field":"profits","required":false,"rendered":true},{"label":"利润率(%)","field":"profitsRate","required":false,"rendered":true},{"label":"阳性率(%)","field":"positiveRate","required":false,"rendered":true}]', '设备综合效益分析动态列', 'EfficiencyEquipmentColumn', '设备综合效益分析动态列', 'f', 't', 10
    where not exists ( select * from config_item where key = 'EfficiencyEquipmentColumn');


-- his his_equipment_data_source_split 加联合索引 跑result表
CREATE INDEX IF NOT exists his_equipment_data_source_split_create_date_is_sum_idx ON his_equipment_data_source_split (create_date asc, is_sum);
