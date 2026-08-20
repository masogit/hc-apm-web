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


INSERT INTO public.config_item (uid, name, category, key, value_type, description, super_admin_edit, default_value, user_parent_config)SELECT replace(uuid_generate_v4()::text,'-',''),'计量周期',7, 'displayMeasureCycle',3,'计量是否显示检定周期',false,'false',false WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'displayMeasureCycle');


insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 24, 'xls|xlsx|doc|docx|pdf|jpeg|jpg|png|txt|ps|jfif|XLS|XLSX|DOC|DOCX|PDF|JPEG|JPG|PNG|TXT|PS|JFIF|PPTX|PPT|ppt|pptx|mp4|MP4', '资产附件上传文件格式', 'assetUploadFileFormats', '资产附件上传文件格式', 'f', 't', 1 where not exists(select * from config_item where key = 'assetUploadFileFormats');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 24, 'jpg,jpeg,png,pdf,doc,docx,xls,xlsx', '合同附件上传文件格式', 'contractUploadFileFormats', '合同附件上传文件格式', 'f', 't', 1 where not exists(select * from config_item where key = 'contractUploadFileFormats');

-- YHAAO-29249
CREATE INDEX CONCURRENTLY idx_sys_doc_file_hash ON sys_doc (file_hash);

-- YHAAO-29169 为所有医工/主任添加转科管理员权限
INSERT INTO user_role (user_id, role_id, created_by, created_date, last_modified_by, last_modified_date, role_type)
SELECT DISTINCT ur.user_id,
                (SELECT id FROM sys_role WHERE name = 'DepartmentConvertManager'),
                'adhoc', NOW(), 'adhoc', NOW(), 0
FROM user_role ur
         INNER JOIN sys_role sr ON ur.role_id = sr.id AND ur.role_type = 0
         INNER JOIN user_account ua ON ur.user_id = ua.id AND ua.is_active = true
         INNER JOIN tenant_info ti ON ua.tenant_uid = ti.uid AND ti.is_enabled = true
WHERE sr.name IN ('AssetHead', 'AssetStaff')
  AND NOT EXISTS (
    SELECT 1 FROM user_role existing
    WHERE existing.user_id = ur.user_id
      AND existing.role_id = (SELECT id FROM sys_role WHERE name = 'DepartmentConvertManager')
      AND existing.role_type = 0
);