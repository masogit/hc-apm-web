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
ALTER TABLE qr_code_lib ADD COLUMN IF NOT EXISTS asset_dept_id INTEGER;



-- 采购医疗设备分类数据导入（共73条）
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('04b31e85b22a42f48997ff88a8415c10', '设备', '大型放射影像设备', 'CT≤64排', '中低端X线断层成像设备', '64排及以下CT');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('613375c05a544ecbaa2a034a43483bce', '设备', '大型放射影像设备', 'CT128排', '中高价值X线断层成像设备', '128排CT');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('b6981ad2639c459a9c0f85a1be953bea', '设备', '大型放射影像设备', 'CT≥256排或高端平台', '高价值X线断层成像设备', '256排及以上CT，能谱CT、双源CT、光子计数CT');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('cbe664ed6b41441585d9719fc31fa6e1', '设备', '大型放射影像设备', 'MR1.5T及以下', '中高价值磁共振成像设备', '1.5T 及以下MRI');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('a118b1e0e1d34d018d872bdf336bfcf1', '设备', '大型放射影像设备', 'MR3.0T及以上', '高价值磁共振成像设备', '3.0T 及以上MRI');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('7ad1d2795f32490281e9b505dcad7834', '设备', '大型放射影像设备', 'MR专科/开放式', '超高价值磁共振成像设备', '专科MRI');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('92d6860274cd49bfa7b2aadc79bf0a45', '设备', '大型放射影像设备', 'DSA单板', '血管造影及介入诊疗核心设备，单板价格相对较低', '单板DSA');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('5c85c2d455c242fba7ac20883ef5e736', '设备', '大型放射影像设备', 'DSA双板/复合手术室', '血管造影及介入诊疗核心设备，双板或复合手术室价格较高', '双板DSA、复合手术室DSA');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('fd16a178322d4cd5baf2b7052f758dd6', '设备', '大型放射影像设备', '高端C臂', '价值较高的C臂', '大型C臂、三维C臂、O臂');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('433a9a6901154f19beca275352ec492b', '设备', '大型放射影像设备', '高端X光成像', '除CT/MRI/DSA外，价格较高且配置差异较大的X线成像系统', '数字胃肠机、乳腺X线机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('6a42fbd85214437d92d7610a43bc4a06', '设备', '普通放射影像设备', 'DR/移动DR', '常规放射摄影设备，采购频次较高，适合按同类历史价格评价', '固定DR、移动DR、悬吊DR');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('85bfe3b99b104923b88516b9a1bf226e', '设备', '普通放射影像设备', '普通X线及骨密度', '价值相对低于大型影像，技术形态较稳定的常规影像设备', '普通X线机、骨密度仪、牙片机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('2a7f78c7599045beb1575cf6f02c4f1d', '设备', '普通放射影像设备', '普通C臂', '价值中等的C臂', '普通C臂、移动C臂');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('47c18f904b944099b7adbbf702b623a5', '设备', '普通放射影像设备', '放射影像配套设备', '不是整机，作为配件用于放射影像系统', 'X射线管、平板探测器、高压发生器');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('45d2477dded0474780c4a054c216a472', '设备', '超声设备', '台式彩超', '常规科室使用的台式超声设备，价格跨度较大但认知清晰', '全身彩超、腹部彩超、浅表彩超');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('55965838ad6a4bd4ab24fc65c4c69e7c', '设备', '超声设备', '高端/专科彩超', '用于心脏、妇产、介入等专科场景，配置和价格明显高于常规彩超', '心脏彩超、四维妇产彩超、介入超声、肌骨超声');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('6a366b4aefec4ff2a8f4739e59535ca1', '设备', '超声设备', '便携/床旁超声', '移动或床旁应用设备，单价和配置逻辑与台式设备不同', '便携彩超、掌上超声、床旁超声');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('f5259e9425944292a323361a3ce50291', '设备', '超声设备', '其他超声设备', '非常规成像彩超，或超声测量、治疗、辅助类设备', '内镜超声主机、经颅多普勒、超声骨密度');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('bd64edb336284b91995aff29597f092d', '设备', '内镜设备', '电子内镜系统', '以软式电子内镜为主，主机、镜体、图文系统常成套采购', '胃肠镜系统、支气管镜系统、鼻咽喉镜系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('a9edd59608834b03bd112ad432744c76', '设备', '内镜设备', '腔镜系统', '以硬镜和腔镜平台为主，常用于手术室或专科手术', '腹腔镜系统、胸腔镜系统、关节镜系统、宫腔镜系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('b9fefb86f54c4d19894792c1b5cb947d', '设备', '内镜设备', '高端内镜/腔镜平台', '高价值影像平台，价格受4K、3D、荧光、机器人等功能影响明显', '4K腔镜、3D腔镜、荧光腔镜、高清电子内镜平台');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('234790ecf2de4502840c2dee53ecad2e', '设备', '核医学设备', 'PET', '核医学高价值分子影像设备，建设条件和采购金额均需重点评审', 'PET/CT、PET/MR');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('4bbbc41c1ca4457a9d32daacc5e097f8', '设备', '核医学设备', 'SPECT', '常规核医学断层或平面显像设备', 'SPECT、SPECT/CT、伽马相机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('f61ea7e9b9044342b0541d8769faa96e', '设备', '核医学设备', '核医学辅助设备', '配套核医学诊疗流程的制备、检测、防护和质控设备', '放射性药物制备设备、活度计、甲功仪、核医学质控设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('dd2183253dc44915a24f1bc5a969ad46', '设备', '放射治疗设备', '放疗主机', '放疗核心治疗设备，金额高、建设和维保影响大', '直线加速器、质子治疗设备、伽马刀、后装治疗机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('af049721d2764130bf1fa6d026164cd7', '设备', '放射治疗设备', '放疗定位与计划', '用于放疗模拟、定位、计划、图像引导和治疗验证', '模拟定位机、放疗计划系统、图像引导系统、呼吸门控系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('6cf4661872554f0b970f28d5f4192805', '设备', '放射治疗设备', '放疗质控与配套', '用于放疗质量控制、剂量验证、摆位固定等配套场景', '水箱、剂量仪、矩阵验证系统、激光定位系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('e85b48ae73cf4b6f8efcbca3a5180a3d', '设备', '检验和病理设备', '生化免疫设备', '检验科高频核心分析设备，可按通量、模块和自动化程度评价', '生化分析仪、免疫分析仪、生化免疫分析系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('4682d99098904c60802b3e322a632cb7', '设备', '检验和病理设备', '检验流水线', '价格和建设复杂度明显高于单机，需单独比价和论证', '生化免疫流水线、血液分析流水线、尿液流水线、TLA');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('d29e977ec4434082abdcafccff672f25', '设备', '检验和病理设备', '血液/凝血/尿液设备', '常规检验分析设备，适合按科室需求和测试量评估', '血细胞分析仪、凝血分析仪、尿液分析仪、血库系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('3016be5d788340a283875fa63c8a091c', '设备', '检验和病理设备', '微生物设备', '微生物培养、鉴定、药敏等设备，价格受自动化程度影响', '血培养仪、微生物鉴定药敏系统、微生物质谱');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('3afb8d9c7dfd43829db260cb7e29d50f', '设备', '检验和病理设备', '分子诊断设备', '分子检测平台，价格受检测通量和技术路线影响', 'PCR仪、全自动核酸提取仪、分子诊断平台');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('5457b41544284cd7b8238239ff3e55ef', '设备', '检验和病理设备', '高端检验平台', '技术平台属性强、单价高、历史比价需单独归集', '基因测序仪、质谱仪、流式细胞仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('75b2fdf5d78d4ba5aac8c2d59d203538', '设备', '检验和病理设备', 'POCT/血气设备', '床旁或快速检测设备，单价和使用模式不同于大型检验设备', '血气分析仪、POCT分析仪、糖化血红蛋白分析仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('13b1a969b39240e38859247fd759cf12', '设备', '检验和病理设备', '专科检验设备', '其他专科检验设备', '精子分析仪、生殖道分泌物分析仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('18d676ce29e24e658dc087ae3a0767eb', '设备', '检验和病理设备', '检验辅助设备', '支撑样本处理、保存、培养和实验室环境的设备', '离心机、培养箱、医用冰箱、生物安全柜、洁净工作台');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('094efee6539e4d558ea3f331117e4929', '设备', '检验和病理设备', '病理常规设备', '病理取材、制片、染色等常规设备', '脱水机、包埋机、切片机、染色机、封片机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('5f7c36edd4a242b4a16dc429f14f3eaf', '设备', '检验和病理设备', '数字病理设备', '数字化程度高、价格差异大，适合单独归类', '数字病理切片扫描仪、病理图像分析系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('0bd81e49080c4d6dbd2af9f5d450dcfd', '设备', '手术设备', '手术机器人/导航', '高价值智能化手术平台，采购论证和价格评审复杂', '手术机器人、骨科导航、神经外科导航');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('51effceb2e564104b62c0f1851e70284', '设备', '手术设备', '手术显微镜', '高价值精密手术设备', '神外显微镜、眼科显微镜、耳鼻喉显微镜');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('974a9cdaa5f949ae9fcef69a7be4c93d', '设备', '手术设备', '能量与动力设备', '手术室常用有源设备', '电外科工作站、超声刀、射频消融、手术动力系统、体外冲击波碎石机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('c5223febb8334b24bd9c71c1e56ede2b', '设备', '手术设备', '医用激光设备', '手术室常用激光设备', '医用激光治疗机、铥激光');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('99875d6bf45c4ac7b27daedf0a4bc631', '设备', '手术设备', '其他有源手术设备', '不能归入以上的有源手术设备', '水刀、植皮机、电动吻合器');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('ae95a0471eb5440dba71b88f134ca5ee', '设备', '手术设备', '灯塔床基础装备', '手术室基础配置，常成套或批量采购', '手术床、无影灯、吊塔、吊桥');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('c1de61dd0f2a4add9f41ec9f840f2b23', '设备', '麻醉设备', NULL, '麻醉相关设备，通常按设备名称和配置字段即可完成评审', '麻醉机、麻醉工作站、麻醉深度监测、肌松监测');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('72f6c8e6571a4e2aa3472e3622b2620f', '设备', '急救与生命支持设备', '呼吸支持设备', '急诊、ICU、病区使用的呼吸治疗支持设备', '有创呼吸机、无创呼吸机、转运呼吸机、高流量氧疗、医用制氧系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('86e8c691c2e64f4aa960ab7820301131', '设备', '急救与生命支持设备', '高级生命支持设备', '单价高、临床风险高，需重点评审适应症和配置', 'ECMO、IABP、人工肝、心肺复苏机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('16c2903b907142ee895ec37a060f07fd', '设备', '急救与生命支持设备', '急救基础设备', '急救场景常规设备，采购频次较高', '除颤仪、洗胃机、急救转运设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('9b542ae18a66463fa19492f0a84c5c9a', '设备', '监护、诊察与电生理设备', '监护系统', '监护类设备，价格受模块、联网和床位规模影响', '监护仪、中央监护系统、遥测监护系统');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('e88ab15efe5a492d88fc515d40b2e5f5', '设备', '监护、诊察与电生理设备', '心电与电生理诊断', '心电、脑电、肌电等生理信号检查设备', '心电图机、动态心电、脑电图机、肌电图机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('96ace4c71dc841aeb4dccb192edf56ef', '设备', '监护、诊察与电生理设备', '功能检查设备', '各类功能评估和生理参数检测设备', '肺功能仪、睡眠监测、人体成分分析仪、尿动力学分析仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('43607f68d0674444b206786998d32765', '设备', '血液净化与输血设备', '血液透析设备', '透析治疗核心设备，批量采购和维保成本均需关注', '血液透析机、血液透析滤过机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('48f009aee73b4a208dfe3aaf75b0d4e8', '设备', '血液净化与输血设备', '连续血液净化设备', '重症连续血液净化相关设备，单价和耗材联动明显', 'CRRT、连续血液滤过设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('bea562be1ff3417ba3190ce3729ef4d2', '设备', '血液净化与输血设备', '透析配套设备', '透析中心建设及运行配套设备', '透析水处理系统、透析椅、透析器复用机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('a383f129eb9f4537bc88d4c7c7fa85a1', '设备', '血液净化与输血设备', '输血与血液处理设备', '输血科、手术室相关血液处理设备', '血液成分分离机、自体血回收机、血液辐照仪、血浆融化箱');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('edb3a7423bc7469796c46560c824a9ba', '设备', '消毒供应与院感设备', NULL, '消毒、灭菌、清洗、院感控制相关设备', '内镜清洗消毒机、灭菌器、低温等离子、空气消毒机、纯水设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('785522a45fb74e588d4f44959acd2ee2', '设备', '康复与理疗设备', '康复训练设备', '主动训练、功能恢复和康复评估设备', '康复机器人、步态训练系统、上下肢训练设备、经颅磁刺激仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('dec8a4d8d3484d508bc4698701f5a396', '设备', '康复与理疗设备', '物理治疗设备', '物理因子治疗和疼痛治疗设备', '冲击波治疗仪、磁刺激仪、中频治疗仪、牵引设备、医用氧舱、臭氧治疗仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('a9b8b163dde14cd89b473f4797663f23', '设备', '中医诊疗设备', '中医诊疗设备', '中医特色诊疗、理疗和辅助诊断设备', '熏蒸设备、艾灸设备、中医定向透药、脉诊仪、舌诊仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('6278d41a84554875828ed6a275ba6ddc', '设备', '专科诊疗设备', '眼科设备', '眼科诊断和治疗设备，部分高值设备需重点评审', 'OCT、眼底相机、裂隙灯、超乳玻切、眼科激光');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('03e0fd759fde472f99624793ab9253ca', '设备', '专科诊疗设备', '口腔设备', '口腔诊疗、影像、技工相关设备', '牙椅、口腔CBCT、洁牙机、种植机、技工设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('4681bf0dd44f49a2948170a2ec9c47cc', '设备', '专科诊疗设备', '妇产与生殖设备', '妇产、辅助生殖及相关专科设备', '阴道镜、宫腔镜、胎监、胚胎培养箱、生殖实验室设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('a82985be9e484c4a8bad24ea026c7e9f', '设备', '专科诊疗设备', '新生儿设备', '新生儿专用诊疗、保温和治疗设备', '新生儿培养箱、辐射保暖台、蓝光治疗仪');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('cdc446e594a04fd89ef659d203ac39b4', '设备', '专科诊疗设备', '其他专科设备', '不能归入以上专科的设备', '耳鼻喉科、皮肤科等专科设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('298980d739d049f2b6814118b279e986', '设备', '注输、护理与承载设备', '注输泵类设备', '病区、ICU、麻醉等场景常见注输设备', '输液泵、注射泵、营养泵、输注工作站');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('d9a57e47ff434c578f1def33dda766b3', '设备', '注输、护理与承载设备', '患者运载设备', '患者转运、承载、护理相关设备', '病床、护理床、转运床、抢救床、轮椅、诊疗床、检查床');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('61f0b4ee3257444fa7ab8c6d7dfcb3c7', '设备', '注输、护理与承载设备', '护理辅助设备', '病区护理、防护和辅助治疗设备', '排痰机、防压疮设备、负压吸引器、护理防护设备');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('3349376b14ae46359e5ddb8c0b4228b7', '设备', '医用辅助与工程设备', NULL, '服务医疗流程或设备运行的辅助、工程、药事、环境类设备', '医用气体设备、医用UPS、物流传输、自动发药机、静配设备、医用影像打印机');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('7f588d36cf8f4f0cb240326ac21ff03b', '软件', '医用软件和信息化系统', NULL, '独立医疗软件或与设备配套的软件系统，按软件用途和授权方式另行填写', '影像处理软件、放疗计划系统、AI辅助诊断、监护软件');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('9e17605cc7364368a80c395717b5ad56', '器械', '植入、介入与高值器械', NULL, '植入物、介入器械及高值治疗器械，评审重点在适应症、耗用量和价格依据', '支架、人工关节、起搏器、介入导管、骨科内固定材料');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('61093e1670864350a06695b3551a3ba4', '器械', '常规手术器械与低值器械', NULL, '单价较低或标准化程度较高的常规器械，通常不需要细分二级', '手术刀剪钳镊、基础器械包、骨科工具、无源器械');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('2e2cbd2babb6445f8e6c4592d1c96fdb', '器械', '其他医疗设备器械', NULL, '无法归入以上类别的医疗设备或器械，原则上应控制使用', '其他专用设备、少见设备、临时新增品类');
INSERT INTO acquisition_specification (id, major_category, first_level_category, second_level_category, brief_description, typical_examples) VALUES ('548d76adb688499f87ff363638a11fa5', '器械', '非医疗设备器械', NULL, '不作为医疗设备器械使用', '其他不能归入医疗设备器械者');
