-- 批量更新设备使用年限截止日期字段
update asset_info set asset_lifecycle_deadline = date_trunc('month', manufact_date + INTERVAL '1 month' * lifecycle) where manufact_date is not null and lifecycle is not null;
-- YHAAO-21538 调剂中心借用确认后，设备使用责任人修改
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 2, 'false', '是否修改设备责任人', 'enableModifyingTheDeviceOwner', '是否修改设备责任人', 'f', 't', 3 where not exists (select * from config_item where key = 'enableModifyingTheDeviceOwner');
-- YHAAO-21497 需求-科室间借调，跨院区借调做成可配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '启用医院间科室借调', 'enableSecondmentDepartmentsBetweenHospitals', '启用医院间科室借调', 'f', 't', 3 where not exists (select * from config_item where key = 'enableSecondmentDepartmentsBetweenHospitals');


-- HIS 创建索引
CREATE INDEX his_equipment_data_source_exec_time_idx ON public.his_equipment_data_source (exec_time);
CREATE INDEX his_equipment_data_source_org_code_idx ON public.his_equipment_data_source (org_code);
CREATE INDEX his_equipment_data_source_site_uid_idx ON public.his_equipment_data_source (site_uid);
CREATE INDEX his_equipment_data_source_split_exec_time_idx ON public.his_equipment_data_source_split (exec_time);
CREATE INDEX his_equipment_data_source_split_asset_uid_idx ON public.his_equipment_data_source_split (asset_uid);
CREATE INDEX his_equipment_data_result_exec_time_idx ON public.his_equipment_data_result (exec_time);
CREATE INDEX his_equipment_data_result_asset_uid_idx ON public.his_equipment_data_result (asset_uid);
CREATE INDEX his_equipment_data_result_org_code_idx ON public.his_equipment_data_result (org_code);
CREATE INDEX his_equipment_data_result_tenant_uid_idx ON public.his_equipment_data_result (tenant_uid);