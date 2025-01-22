
-- his绩效设备配置修改字段长度
ALTER TABLE efficiency_equipment_info ALTER COLUMN report_config TYPE TEXT;

-- YHAAO-23014  新增资产出入库管理员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'DepartmentConvertManager', '转科管理员',  'Department Convert Manager'
    where not exists (select 1 from sys_role where name='DepartmentConvertManager');