-- YHAAO-23495
ALTER TABLE pm_order_rule ADD COLUMN  if not exists name_py_code VARCHAR(255);


--YHAAO-23423 安装位置配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '是否显示安装位置', 'showLocationNameFlag', '是否显示安装位置', 'f','t',3 where not exists(select * from config_item where key = 'showLocationNameFlag');
-- YHAAO-23769
create index if not exists v2_blob_object_object_storage_id_idx on public.v2_blob_object (object_storage_id);


-- his 添加索引
CREATE INDEX IF NOT exists his_equipment_data_source_device_code_idx ON public.his_equipment_data_source (device_code);
CREATE INDEX IF NOT exists his_equipment_match_eq_id_idx ON public.his_equipment_match (eq_id);
CREATE INDEX IF NOT exists his_equipment_match_eq_hosid_idx ON public.his_equipment_match (eq_hosid);
CREATE INDEX IF NOT exists his_equipment_match_site_uid_idx ON public.his_equipment_match (site_uid);