-- YHAAO-29649使用登记操作时间是否允许大于当前时间
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type)
SELECT replace(uuid_generate_v4()::text,'-',''), 8, 'false', '当勾选为是时，在修改操作时间时，可以修改为大于当前时间', 'usageRegistrationTimeLimit', '使用登记操作时间是否允许大于当前时间', 'f', 't', 3
    WHERE NOT EXISTS (SELECT 1 FROM public.config_item WHERE key = 'usageRegistrationTimeLimit');