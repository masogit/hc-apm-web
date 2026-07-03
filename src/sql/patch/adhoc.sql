-- YHAAO-28510增加工单上面显示球管信息
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否显示球管信息', 'workOrderShowTubeInstall', '维修工单是否显示球管信息', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowTubeInstall');

-- 新增日常检查是否可忽略配置项
INSERT INTO public.config_item (uid, name, category, key, value_type, description, super_admin_edit, default_value, user_parent_config)SELECT replace(uuid_generate_v4()::text,'-',''),'日常检查是否可忽略',8, 'dailyCheckIgnorable',3,'日常检查是否可忽略',false,'false',true WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'dailyCheckIgnorable');

--YHAAO-28486
update config_item  set  default_value='[{"label":"设备名称","field":"assetName","required":true,"rendered":true},{"label":"制造商序列号","field":"manufacturerSerialNumber","required":true,"rendered":true},{"label":"设备编号（院方）","field":"departNum","required":true,"rendered":true},{"label":"设备型号","field":"functionType","required":false,"rendered":true},{"label":"所属院区","field":"hospitalId","required":true,"rendered":true},{"label":"所属科室","field":"clinicalDeptName","required":false,"rendered":true},{"label":"使用科室","field":"fromDeptName","required":false,"rendered":false},{"label":"报修单号","field":"srNum","required":true,"rendered":true},{"label":"安装位置","field":"locationName","required":false,"rendered":true},{"label":"报修人","field":"requestorName","required":false,"rendered":true},{"label":"维修人","field":"requireName","required":false,"rendered":true},{"label":"报修时间","field":"requestTime","required":true,"rendered":true},{"label":"当前步骤","field":"currentStep","required":true,"rendered":true},{"label":"工单类型","field":"intExtType","required":false,"rendered":true},{"label":"故障现象","field":"requestReason","required":false,"rendered":true},{"label":"主子工单","field":"mainSub","required":false,"rendered":true},{"label":"资产分类","field":"assetType","required":false,"rendered":true},{"label":"备件状态","field":"approvalStatus","required":false,"rendered":false},{"label":"维修工单来源","field":"srRepairSource","required":false,"rendered":false}]'  where   key like  'WorkOrderListColumn%';


-- YHAAO-28589: 黔东南-配置显示使用科室 - 使用科室筛选配置项
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '设备档案-资产转科管理', 'assetDepartmentConvertDeptFilter', '设备档案-资产转科管理', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'assetDepartmentConvertDeptFilter');

-- YHAAO-28589: 各页面使用科室筛选配置项
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '设备风险评估-是否显示使用科室筛选', 'assetRiskAssessmentDeptFilter', '设备风险评估-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'assetRiskAssessmentDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '特种设备台账管理-是否显示使用科室筛选', 'specialAssetDeptFilter', '特种设备台账管理-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'specialAssetDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '保养工作台-是否显示使用科室筛选', 'pmOrderDeptFilter', '保养工作台-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'pmOrderDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '设备综合效益分析-是否显示使用科室筛选', 'efficiencyEquipmentDeptFilter', '设备综合效益分析-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'efficiencyEquipmentDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '计量结果-是否显示使用科室筛选', 'measuringAssetDeptFilter', '计量结果-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'measuringAssetDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '开关机登记-是否显示使用科室筛选', 'assetOptRecordDeptFilter', '开关机登记-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'assetOptRecordDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '未关机设备列表-是否显示使用科室筛选', 'assetOptUnClosedRecordDeptFilter', '未关机设备列表-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'assetOptUnClosedRecordDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '资产规则管理-是否显示使用科室筛选', 'relRuleAssetDeptFilter', '资产规则管理-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'relRuleAssetDeptFilter');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '临床助手-是否显示使用科室筛选', 'dailyCheckDeptFilter', '临床助手-是否显示使用科室筛选', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'dailyCheckDeptFilter');

update config_item set default_value = '[{"label":"设备名称","field":"assetName","required":true,"rendered":true},{"label":"展示名称","field":"displayName","required":false,"rendered":true},{"label":"设备型号","field":"functionType","required":true,"rendered":true},{"label":"资产编号","field":"financingNum","required":true,"rendered":true},{"label":"品牌/制造商简称","field":"makerShortName","required":true,"rendered":true},{"label":"制造商序列号","field":"serialNum","required":true,"rendered":true},{"label":"二维码","field":"qrCode","required":false,"rendered":true},{"label":"所属科室","field":"clinicalDeptName","required":true,"rendered":true},{"label":"使用科室","field":"assetDeptName","required":false,"rendered":true},{"label":"设备分类","field":"assetGroup","required":false,"rendered":true},{"label":"采购金额","field":"purchasePrice","required":true,"rendered":true},{"label":"采购日期","field":"purchaseDate","required":true,"rendered":true},{"label":"收入(元)","field":"income","required":false,"rendered":true},{"label":"成本(元)","field":"cost","required":false,"rendered":true},{"label":"利润(元)","field":"profits","required":false,"rendered":true},{"label":"利润率(%)","field":"profitsRate","required":false,"rendered":true},{"label":"阳性率(%)","field":"positiveRate","required":false,"rendered":true}]' where key = 'EfficiencyEquipmentColumn';

-- YHAAO-28518
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 2, 'false', '开启设备库存显示', 'enableStockShow', '开启设备库存显示', 'f','t',3 where not exists(select * from config_item where key = 'enableStockShow');


-- YHAAO-28723 新增资产建档-是否显示使用科室筛选
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 22, 'false', '新增资产建档-是否显示使用科室筛选', 'assetCreateDeptFilter', '新增资产建档-是否显示使用科室筛选', 'f','t',3
WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'assetCreateDeptFilter');

-- YHAAO-28723 新增资产建档表添加使用科室字段
ALTER TABLE qr_code_lib ADD COLUMN IF NOT EXISTS asset_dept_uid CHAR(32);
