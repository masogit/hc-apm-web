-- YHAAO-29649使用登记操作时间是否允许大于当前时间
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 8, 'false', '当勾选为是时，在修改操作时间时，可以修改为大于当前时间', 'usageRegistrationTimeLimit', '使用登记操作时间是否允许大于当前时间', 'f', 't', 3
    WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'usageRegistrationTimeLimit');

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