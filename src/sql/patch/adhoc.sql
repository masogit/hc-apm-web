-- YHAAO-29649使用登记操作时间是否允许大于当前时间
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 8, 'false', '当勾选为是时，在修改操作时间时，可以修改为大于当前时间', 'usageRegistrationTimeLimit', '使用登记操作时间是否允许大于当前时间', 'f', 't', 3
    WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'usageRegistrationTimeLimit');

INSERT INTO PUBLIC.config_item (uid, category, default_value, description, KEY, name, super_admin_edit, user_parent_config, value_type)
SELECT REPLACE(uuid_generate_v4 ()::TEXT, '-', ''), 13, 'true', '报废申请是否校验工单', 'checkOrderWhenScrap', '报废申请是否校验工单', 'f', 't', 3
WHERE NOT EXISTS (SELECT 1 FROM PUBLIC.config_item WHERE KEY = 'checkOrderWhenScrap');

-- YHAAO-29812 扩大诊断过程字段
ALTER TABLE v2_work_order ALTER COLUMN diagnostic_process TYPE VARCHAR(500);

-- YHAAO-29788
CREATE OR REPLACE VIEW public.v_i18n_tenant_msg
AS SELECT oi.uid AS tenant_uid,
          vmsg.msg_type,
          vmsg.site_id,
          vmsg.msg_json
   FROM org_info oi
            JOIN i18n_type_manage intm ON oi.org_type = 0
            LEFT JOIN i18n_type_manage_tenant intmt ON intm.id::text = intmt.type_manage_id::text AND oi.uid::text = intmt.tenant_uid::text
     JOIN ( SELECT inm.site_id,
            inm.msg_type,
            jsonb_object_agg(inm.msg_key, inm.value_zh) AS msg_json
           FROM i18n_type_manage intm_1
             JOIN i18n_message inm ON intm_1.msg_type::text = inm.msg_type::text
          GROUP BY inm.site_id, inm.msg_type) vmsg ON vmsg.msg_type::text = intm.msg_type::text AND vmsg.site_id = COALESCE(intmt.tenant_id, '-1'::integer);
--YHAAO-29714 增加配置项医院的信息
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13,  null, '医院信息配置，包含医院等级、城市编码、省编码', 'hospitalInfoConfig', '医院信息配置', 'f', 't', 1 where not exists(select * from config_item where key = 'hospitalInfoConfig');

-- Step 2:  YHAAO-29471  一脉阳光-维修派单人选择逻辑优化  历史数据迁移（支持重复执行）
DO $$
DECLARE
v_tenant_uid CHAR(32);
    v_role_id INT;
    v_existing_role_id INT;
    tenant_rec RECORD;
BEGIN
FOR tenant_rec IN
SELECT DISTINCT tenant_uid
FROM workflow_config
WHERE (dispatch_user_id IS NOT NULL OR dispatch_user_id2 IS NOT NULL)
  AND tenant_uid IS NOT NULL
    LOOP
        v_tenant_uid := tenant_rec.tenant_uid;

-- 检查该租户是否已存在同名迁移角色（幂等保护）
SELECT id INTO v_existing_role_id
FROM custom_role
WHERE name = '默认维修派工角色'
  AND tenant_uid = v_tenant_uid
  AND function_list LIKE '%REPAIR_ORDER_DISPATCH%'
    LIMIT 1;

IF v_existing_role_id IS NOT NULL THEN
            -- 已存在则复用
            v_role_id := v_existing_role_id;
ELSE
            -- 为该租户创建默认维修派工角色
            INSERT INTO custom_role (name, role_desc, function_list, tenant_uid, institution_uid, hospital_uid, site_uid,
                                     created_by, created_date, last_modified_date)
            VALUES ('默认维修派工角色', '系统迁移自动创建的维修派工角色',
                    '["REPAIR_ORDER_VIEW","REPAIR_ORDER_DISPATCH"]',
                    v_tenant_uid, v_tenant_uid, v_tenant_uid, v_tenant_uid,
                    'system', NOW(), NOW())
            RETURNING id INTO v_role_id;
END IF;

        -- 更新该租户下 dispatch_role_id 为空的 workflow_config 记录
UPDATE workflow_config
SET dispatch_role_id   = v_role_id,
    dispatch_role_name = '默认维修派工角色'
WHERE tenant_uid = v_tenant_uid
  AND dispatch_role_id IS NULL
  AND (dispatch_user_id IS NOT NULL OR dispatch_user_id2 IS NOT NULL);

-- 将 dispatch_user_id 对应的用户关联到新角色（去重，跳过已不存在的用户）
INSERT INTO user_role (user_id, role_id, role_type)
SELECT DISTINCT wc.dispatch_user_id, v_role_id, 1
FROM workflow_config wc
WHERE wc.tenant_uid = v_tenant_uid
  AND wc.dispatch_user_id IS NOT NULL
  AND EXISTS (SELECT 1 FROM user_account ua WHERE ua.id = wc.dispatch_user_id)
  AND NOT EXISTS (
    SELECT 1 FROM user_role ur
    WHERE ur.user_id = wc.dispatch_user_id AND ur.role_id = v_role_id AND ur.role_type = 1
);

-- 将 dispatch_user_id2 对应的用户关联到新角色（去重，跳过已不存在的用户）
INSERT INTO user_role (user_id, role_id, role_type)
SELECT DISTINCT wc.dispatch_user_id2, v_role_id, 1
FROM workflow_config wc
WHERE wc.tenant_uid = v_tenant_uid
  AND wc.dispatch_user_id2 IS NOT NULL
  AND EXISTS (SELECT 1 FROM user_account ua WHERE ua.id = wc.dispatch_user_id2)
  AND NOT EXISTS (
    SELECT 1 FROM user_role ur
    WHERE ur.user_id = wc.dispatch_user_id2 AND ur.role_id = v_role_id AND ur.role_type = 1
);

END LOOP;
END $$;


CREATE UNIQUE INDEX idx_udid_registry_num_info_registry_num ON udid_registry_num_info (registry_num);

INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0110'),
       '011006',
       '01-01-10-06',
       '乳腺旋切活检系统及附件'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '011006');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0313'),
       '031327',
       '01-03-13-27',
       '脑血栓取出装置'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '031327');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0313'),
       '031328',
       '01-03-13-28',
       '房间隔穿刺鞘'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '031328');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1007'),
       '100702',
       '01-10-07-02',
       '离体脏器机械灌注转运设备'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '100702');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1307'),
       '130709',
       '01-13-07-09',
       '心耳夹'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '130709');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1309'),
       '130911',
       '01-13-09-11',
       '整形用植入线材'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '130911');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1311'),
       '131104',
       '01-13-11-04',
       '组织诱导性植入器械'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '131104');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1311'),
       '131105',
       '01-13-11-05',
       '含几丁糖关节腔内液体'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '131105');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1607'),
       '160717',
       '01-16-07-17',
       '人工角膜'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '160717');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1706'),
       '170607',
       '01-17-06-07',
       '增材制造用金属、陶瓷义齿制作材料'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '170607');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2212'),
       '221204',
       '01-22-12-04',
       '微生物样本前处理仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221204');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2212'),
       '221205',
       '01-22-12-05',
       '粪便分析前处理仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221205');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2213'),
       '221303',
       '01-22-13-03',
       '细胞过滤分选仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221303');
