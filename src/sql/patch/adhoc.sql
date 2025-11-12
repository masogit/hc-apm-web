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


insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, '', '无台账报修工单没有所属科室时，读取配置项“无台账报修派工人1”、“无台账报修派工人2” 和医学工程处主任派工', 'NoAssetDispatchWorkers', '无台账报修派工人', 'f', 't', 1 where not exists(select * from config_item where key = 'NoAssetDispatchWorkers');

-- YHAAO-26026 添加设备维保截止日期工单提醒配置项
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '工单维修是否需要判断维保截止日期', 'enableMaintenanceDate', '工单维修是否需要判断维保截止日期', 'f','t',3,'' where not exists(select * from config_item where key = 'enableMaintenanceDate');

-- YHAAO-26075 创建盘点工单优化，删除盘点工单详情表外键
ALTER TABLE inventory_order_detail DROP CONSTRAINT IF EXISTS fk_ft7t8o1hurpmy0ra6xp256gp2;
ALTER TABLE inventory_order_detail DROP CONSTRAINT IF EXISTS fk_kbu315ol3cbxgu6a0b6kp4ob;
ALTER TABLE inventory_order_detail DROP CONSTRAINT IF EXISTS fkhkghkgwklvpjs04m0nt3e2py3;

-- YHAAO-26051
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation)select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '科室借调是否可调整归还时间', 'enableDeptSecondmentAdjustReturnTime', '科室借调是否可调整归还时间', 'f','t',3,'' where not exists(select * from config_item where key = 'enableDeptSecondmentAdjustReturnTime');

-- YHAAO-26195
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 2, '', '借出门锁密码', 'outDoorPassword', '借出门锁密码', 'f','t',1,'' where not exists(select * from config_item where key = 'outDoorPassword');
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 2, '', '归还门锁密码', 'inDoorPassword', '归还门锁密码', 'f','t',1,'' where not exists(select * from config_item where key = 'inDoorPassword');

-- YHAAO-26222 批量更新用户修改记录user_uid
update user_account_history set user_uid = user_account.uid from user_account where user_account_history.login_name = user_account.login_name and user_account_history.user_uid is null;

-- YHAAO-26164 查询安徽臻徽医疗器械有限公司KPI得分显示的问题
update zkb_part_order_return set data_satus = 'Accept' where part_order_id = '2c9288957edde390017f01c4c4a63f6d';
update zkb_part_order_return set qty = 2 where part_order_id = '2c9b8584944a68ac01944df10a6e040c';
delete from zkb_key_behavior_index where tenant_uid = 'e1f36960972d4251bb92fd61d7afe1af' and kbi_date > '2025-01-01';

-- YHAAO-26186 修和保养工时填写方式进一步优化
update  config_item  set validation='[{"key":"工时段","value":"1"},{"key":"总工时","value":"0"},{"key":"仅总工时","value":"2"},{"key":"仅工时段","value":"3"}]' where key='repairEntryTypeSet';

-- YHAAO-26342
insert into i18n_type_manage (id,msg_type, name) select replace(uuid_generate_v4()::text,'-',''), 'spare03','备用字段03' where not exists(select * from i18n_type_manage where msg_type = 'spare03');
insert into i18n_type_manage (id,msg_type, name) select replace(uuid_generate_v4()::text,'-',''), 'spare04','备用字段04' where not exists(select * from i18n_type_manage where msg_type = 'spare04');
insert into i18n_type_manage (id,msg_type, name) select replace(uuid_generate_v4()::text,'-',''), 'spare05','备用字段05' where not exists(select * from i18n_type_manage where msg_type = 'spare05');