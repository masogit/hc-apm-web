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

--智康宝 zone刷新ZKB FE数据
update zkb_part_order p set zone = (select a.zone from zkb_contract_asset a where a.contract_id = p.contract_id and a.asset_id = p.asset_id)  where zone is null and current_step in ('TechnicalPending','ProductPending','ManagePending');

-- YHAAO-23014  新增资产出入库管理员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'StockedManager', '出入库管理员',  'Stocked Manager' where not exists (select 1 from sys_role where name='StockedManager');
--删除维修和保养工单中 asset_id外键约束，因为云版mvs租户可能不存在相关设备
ALTER TABLE v2_pm_order DROP CONSTRAINT IF EXISTS fk_p6lxi1rkh49qtix91am8excms;
ALTER TABLE v2_work_order DROP CONSTRAINT IF EXISTS fk_fg05gp9eenc2sqth59kv2jk3t;

-- YHAAO-23300 添加网页版供应商登录地址
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'https://hcapm.gehealthcloud.cn/reports/vender/index', '网页版供应商链接', 'vendorLoginUrl', '网页版供应商链接', 'f', 't', 1 where not exists (select * from config_item where key = 'vendorLoginUrl');
