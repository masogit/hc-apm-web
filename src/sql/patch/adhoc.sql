-- 增加配置项：MVS保养报告仅下载保养项目，默认否，归属预防维护tag(category=5)
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', 'MVS保养报告仅下载保养项目', 'mvsPmReportOnlyDownloadPmItems', 'MVS保养报告仅下载保养项目', 'f', 't', 3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'mvsPmReportOnlyDownloadPmItems');

-- 增加配置项：维修是否显示自动验收，默认否，归属维修流程tag(category=4)
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修是否显示自动验收', 'repairShowAutoAcceptance', '维修是否显示自动验收', 'f', 't', 3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'repairShowAutoAcceptance');


-- YHAAO-28922 巡检保养工单增加巡检图片上传,其他保养工单增加其他图片上传
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', '巡检保养巡检图片是否必填', 'requireInspectionPhotos', '巡检保养巡检图片必填', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'requireInspectionPhotos');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', '二级保养其他图片是否必填', 'requireRuleLevelSecondOtherPhotos', '二级保养其他图片必填', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'requireRuleLevelSecondOtherPhotos');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', '三级/质控保养其他图片是否必填', 'requireRuleLevelThreeOtherPhotos', '三级/质控保养其他图片必填', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'requireRuleLevelThreeOtherPhotos');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', '二级保养前后图片是否必填', 'requireRuleLevelSecondMaintenancePhotos', '二级保养前后图片必填', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'requireRuleLevelSecondMaintenancePhotos');

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', '三级/质控保养前后图片是否必填', 'requireRuleLevelThreeMaintenancePhotos', '三级/质控保养前后图片必填', 'f','t',3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'requireRuleLevelThreeMaintenancePhotos');
