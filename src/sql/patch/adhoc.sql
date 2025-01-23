
-- his绩效设备配置修改字段长度
ALTER TABLE efficiency_equipment_info ALTER COLUMN report_config TYPE TEXT;

-- YHAAO-23014  新增资产出入库管理员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'DepartmentConvertManager', '转科管理员',  'Department Convert Manager'
    where not exists (select 1 from sys_role where name='DepartmentConvertManager');

-- YHAAO-23861
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 8, 'false', '是否关机后终末消毒', 'disinfectedAfterShutdown', '是否关机后终末消毒', 'f','t',3 where not exists(select * from config_item where key = 'disinfectedAfterShutdown');