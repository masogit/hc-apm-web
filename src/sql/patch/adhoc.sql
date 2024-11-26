--增配置项【不良事件上报是否填入默认场所】
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, 'false', '不良事件上报是否填入默认场所', 'enableInsertUsePlace', '不良事件上报是否填入默认场所', 'f','t',3 where not exists(select * from config_item where key = 'enableInsertUsePlace');

-- YHAAO-23399
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'true', '是否显示外部工单', 'displayExternalWorkOrder', '是否显示外部工单', 'f','t',3 where not exists(select * from config_item where key = 'displayExternalWorkOrder');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'true', '是否显示远程工单', 'displayRemoteWorkOrder', '是否显示远程工单', 'f','t',3 where not exists(select * from config_item where key = 'displayRemoteWorkOrder');


--YHAAO-23330重点医疗设备使用状况报表更新
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'annualRepairExpenses', '年维修支出', 'Annual repair expenses', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'annualMaintenanceExpenses', '年维保支出', 'Annual maintenance expenses', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'annualConsumablesExpenses', '年耗材支出', 'Annual consumables expenses', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
( msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'annualNetIncome', '年度净收入', 'Annual net income', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'annualIncome', '年收入', 'Annual income', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES( 'label', 'annualNumberOfCheck', '年检查数(项目)', 'Annual number of check(project)', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
( msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES( 'label', 'month', '月份', 'Month', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'netIncome', '净收入', 'Net income', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.i18n_message
(msg_type, msg_key, value_zh, value_en, value_tw, site_id, created_by, created_date, last_modified_by, last_modified_date, hospital_id, tenant_uid, institution_uid, hospital_uid, site_uid, order_by, msg_key_order)
VALUES('label', 'inCome', '收入', 'Income', NULL, -1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
