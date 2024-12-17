--增配置项【不良事件上报是否填入默认场所】
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 14, 'false', '不良事件上报是否填入默认场所', 'enableInsertUsePlace', '不良事件上报是否填入默认场所', 'f','t',3 where not exists(select * from config_item where key = 'enableInsertUsePlace');

-- YHAAO-23399
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示外部工单', 'displayExternalWorkOrder', '是否显示外部工单 (维修、保养)', 'f','t',3 where not exists(select * from config_item where key = 'displayExternalWorkOrder');

insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 13, 'true', '是否显示远程工单', 'displayRemoteWorkOrder', '是否显示远程工单 (维修、保养)', 'f','t',3 where not exists(select * from config_item where key = 'displayRemoteWorkOrder');

-- YHAAO-23419 mvs 工单显示控制
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '启用MVS工单同步', 'synchronizeWorkOrder', '启用MVS工单同步', 'f','t',3 where not exists(select * from config_item where key = 'synchronizeWorkOrder');
insert into public.config_item_value (uid,key,value,tenant_uid,institution_uid,hospital_uid,site_uid) select replace(uuid_generate_v4()::text,'-',''),'synchronizeWorkOrder','true',top.tenant_uid,top.tenant_uid,top.tenant_uid,top.tenant_uid  from tenant_option_config top left join config_item_value civ on civ.site_uid =top.tenant_uid where top.synchronize_work_order = true and civ.uid is null;

-- YHAAO-23542
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '是否允许外修关单', 'externalRepairOrderClose', '是否允许外修关单', 'f','t',3 where not exists(select * from config_item where key = 'externalRepairOrderClose');

-- YHAAO-23597
ALTER TABLE asset_lifecycle_event ALTER COLUMN description TYPE VARCHAR(2047);
ALTER TABLE sieble_sr_data ALTER COLUMN sr_description TYPE VARCHAR(2047);
ALTER TABLE sieble_sr_data ALTER COLUMN origin_data TYPE VARCHAR(6143);


-- YHAAO-23657 重点医疗设备，清数据
delete from key_asset_report;
delete from key_asset_report_org;
