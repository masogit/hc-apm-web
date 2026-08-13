-- 增加配置项：MVS保养报告仅下载保养项目，默认否，归属预防维护tag(category=5)
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 5, 'false', 'MVS保养报告仅下载保养项目', 'mvsPmReportOnlyDownloadPmItems', 'MVS保养报告仅下载保养项目', 'f', 't', 3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'mvsPmReportOnlyDownloadPmItems');

-- 增加配置项：维修是否显示自动验收，默认否，归属维修流程tag(category=4)
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修是否显示自动验收', 'repairShowAutoAcceptance', '维修是否显示自动验收', 'f', 't', 3
    WHERE NOT EXISTS(SELECT * FROM config_item WHERE key = 'repairShowAutoAcceptance');

