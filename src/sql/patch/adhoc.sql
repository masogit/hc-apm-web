--报修签到增加【签到时间需扫二维码获取】配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '签到时间需扫二维码获取', 'enableSignNeedScanQrCode', '签到时间需扫二维码获取', 'f','t',3 where not exists(select * from config_item where key = 'enableSignNeedScanQrCode');

--YHAAO-22413 工单维修判断是否需要维保合同
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '工单维修是否需要判断维保合同', 'enableMaintenanceContract', '工单维修是否需要判断维保合同', 'f','t',3 where not exists(select * from config_item where key = 'enableMaintenanceContract');

--YHAAO-22200 资产出库科室设置
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 3, '', '资产出库科室设置，请输入科室uid', 'setAssetOutStockDept', '资产出库科室设置', 'f', 't', 1 where not exists(select * from config_item where key = 'setAssetOutStockDept');
-- YHAAO-22426 日常检查--有问题数据显示时间调整
delete from config_item where key = 'setQueryNumberDays';
delete from config_item_value where key = 'setQueryNumberDays';
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 8, '30', '仅对移动端有问题卡片生效', 'setQueryNumberDays', '查询天数', 'f', 't', 2,'^([12][0-9]|30|[1-9])$' where not exists(select * from config_item where key = 'setQueryNumberDays');

-- 多级审批给申请人uid赋值
UPDATE multi_level_approval mla SET application_uid = ( SELECT mla2.initiate_approver_uid FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL LIMIT 1 ) WHERE EXISTS ( SELECT 1 FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL );

--YHAAO-22490不良事件产品类型设置
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type,validation)
select replace(uuid_generate_v4()::text,'-',''), 14, '', '不良事件产品类型设置', 'setAdverseEventProductType', '不良事件产品类型设置', 'f', 't', 7, '[{"key":"全部","value":"1"},{"key":"仅设备","value":"2"},{"key":"仅耗材","value":"3"}]' where not exists(select * from config_item where key = 'setAdverseEventProductType');

-- YHAAO-20948  计量增加微信端输入界面
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 7, '[{"children":[{"value":"镜片箱","key":"镜片箱"},{"value":"医用激光源","key":"医用激光源"}],"value":"光学","key":"光学"},{"children":[{"value":"血压计","key":"血压计"},{"value":"电子血压计","key":"电子血压计"},{"value":"气体流量计","key":"气体流量计"},{"value":"压力表","key":"压力表"},{"value":"台秤","key":"台秤"},{"value":"天平","key":"天平"},{"value":"砝码","key":"砝码"},{"value":"转速表","key":"转速表"},{"value":"移液器","key":"移液器"},{"value":"工作玻璃浮计","key":"工作玻璃浮计"},{"value":"血压模拟仪","key":"血压模拟仪"},{"value":"眼压计","key":"眼压计"},{"value":"微泵测试仪","key":"微泵测试仪"}],"value":"力学","key":"力学"},{"children":[{"value":"温度计","key":"温度计"},{"value":"温湿度计","key":"温湿度计"},{"value":"婴儿培养箱","key":"婴儿培养箱"}],"value":"热学","key":"热学"},{"children":[{"value":"医用超声源","key":"医用超声源"},{"value":"声级计","key":"声级计"}],"value":"声学","key":"声学"},{"children":[{"value":"电导仪","key":"电导仪"},{"value":"酸度计","key":"酸度计"},{"value":"分光光度计","key":"分光光度计"},{"value":"光度计","key":"光度计"},{"value":"旋光仪","key":"旋光仪"},{"value":"色谱仪","key":"色谱仪"},{"value":"电泳仪","key":"电泳仪"},{"value":"气体分析仪","key":"气体分析仪"},{"value":"酶标分析仪","key":"酶标分析仪"},{"value":"氧饱和度模拟仪","key":"氧饱和度模拟仪"}],"value":"物理化学","key":"物理化学"},{"children":[{"value":"磁共振","key":"磁共振"}],"value":"电磁学","key":"电磁学"},{"children":[{"value":"医用辐射源","key":"医用辐射源"},{"value":"活度计","key":"活度计"},{"value":"剂量计","key":"剂量计"},{"value":"比释动能测量仪","key":"比释动能测量仪"},{"value":"剂量当量率仪","key":"剂量当量率仪"}],"value":"电离辐射","key":"电离辐射"},{"children":[{"value":"心电图机","key":"心电图机"},{"value":"心电模拟器","key":"心电模拟器"}],"value":"无线电","key":"无线电"},{"children":[{"value":"除颤分析仪","key":"除颤分析仪"},{"value":"电刀分析仪","key":"电刀分析仪"},{"value":"电气安全分析仪","key":"电气安全分析仪"}],"value":"专用设备","key":"专用设备"}]
', '计量项别、计量种别', 'measurementCategory', '计量项别、计量种别', 'f', 't', 1 where not exists(select * from config_item where key = 'measurementCategory');

-- YHAAO-22568 mvsIbMapping 设置uid
UPDATE mvs_ib_mapping SET ib_uid = ai.uid FROM asset_info ai WHERE mvs_ib_mapping.ib_id = ai.id;
UPDATE mvs_ib_mapping SET mvs_ib_uid = ai.uid FROM asset_info ai WHERE mvs_ib_mapping.mvs_ib_id = ai.id;