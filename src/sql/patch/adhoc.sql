-- YHAAO-25105
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '科室借调流程简化', 'enableSimpleDepartmentSecondment', '开启科室借调流程简化', 'f','t',3 where not exists(select * from config_item where key = 'enableSimpleDepartmentSecondment');
insert into public.config_item_value(uid, hospital_uid, institution_uid, key, site_uid, tenant_uid, value) select replace(uuid_generate_v4()::text, '-', ''), t.hospital_uid, t.institution_uid, 'enableSimpleDepartmentSecondment', t.site_uid, t.tenant_uid, 'false' FROM workflow_config t WHERE t.enable_site_to_site_allocation = true AND NOT EXISTS ( SELECT 1 FROM config_item_value civ WHERE civ.key = 'enableSimpleDepartmentSecondment' AND civ.site_uid = t.site_uid);





insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否开启2级规则自动添加性能检测报告', 'enableSecondRuleReportAutoAdd', '保养单是否自动添加2级规则性能报告', 'f', 't', 3 where not exists (select * from config_item where key = 'enableSecondRuleReportAutoAdd');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否开启3级规则自动添加性能检测报告', 'enableThreeRuleReportAutoAdd', '保养单是否自动添加3级规则性能报告', 'f', 't', 3 where not exists (select * from config_item where key = 'enableThreeRuleReportAutoAdd');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否开启质量控制自动添加性能检测报告', 'enableQuantityControlReportAutoAdd', '保养单是否自动添加质量控制性能报告', 'f', 't', 3 where not exists (select * from config_item where key = 'enableQuantityControlReportAutoAdd');


--
update v2_wrok_order set coworker_entry_type = '0';

--增加是否展示报废单号配置项 https://ge-hc.atlassian.net/browse/YHAAO-25336
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,
                                user_parent_config, value_type)
select replace(uuid_generate_v4()::text, '-', ''),
       13,
       'false',
       '是否展示报废单号,需先启用设备报废流程',
       'showAssetScrapNo',
       '是否展示报废单号',
       'f',
       't',
       3
    where not exists (select * from config_item where key = 'showAssetScrapNo');