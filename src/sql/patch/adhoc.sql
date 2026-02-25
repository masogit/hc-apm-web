CREATE INDEX IF NOT exists  idx_asm_asset_sfxm ON his_asset_sfxm_match (asset_uid, sfxm_id);


--  YHAAO-27354 质控增加配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 11, '1', '质控筛选项', 'QualityControlScreeningOptions', '质控筛选项', 'f', 't', 7 , '[{"key":"无","value":"1"},{"key":"医院组织","value":"2"},{"key":"医院类型","value":"3"}]' where not exists (select * from config_item where key = 'QualityControlScreeningOptions');


-- YHAAO-27330 增加配置项，“质控管理筛选条件”【通用版/区域版】
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 11, '', '质控管理筛选条件', 'QualityControlManagementScreeningConditions', '质控管理筛选条件', 'f', 't', 7 , '[{"key":"通用版","value":"1"},{"key":"区域版","value":"2"}]' where not exists (select * from config_item where key = 'QualityControlManagementScreeningConditions');