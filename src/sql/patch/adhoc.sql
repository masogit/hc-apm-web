-- YHAAO-26184 新增试用专员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'ProbationStaff', '试用专员',  'Probation Staff' where not exists (select 1 from sys_role where name='ProbationStaff');

-- YHAAO-26338
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'key', '维修紧急度显示方式', 'displayModeCasePriority', '维修紧急度显示方式', 'f','t',7,'[{"key":"key值","value":"key"},{"key":"文字","value":"name"}]' where not exists(select * from config_item where key = 'displayModeCasePriority');

-- YHAAO-26487 【ACCT Mobile】-多级审批，多级审批工单list申请人逻辑更新
UPDATE multi_level_approval mla SET application_name = ua.name FROM user_account ua WHERE ua.uid = mla.application_uid;

-- YHAAO-26448
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation) select replace(uuid_generate_v4()::text,'-',''), 7, 'local', 'OCR识别方式', 'identifyPatterns', 'OCR识别方式', 'f','t',7,'[{"key":"本地识别","value":"local"},{"key":"AI识别","value":"AI"}]' where not exists(select * from config_item where key = 'identifyPatterns');