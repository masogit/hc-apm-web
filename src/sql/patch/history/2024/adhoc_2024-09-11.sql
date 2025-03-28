-- YHAAO-22568 mvsIbMapping
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date)  select replace(uuid_generate_v4()::text,'-',''), '68fc4f19938546d7bdfaf2d405cd6e6d', '68fc4f19938546d7bdfaf2d405cd6e6d', '智汇E家', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, null where not exists(select 1 from system_client_info where bind_site_uid='68fc4f19938546d7bdfaf2d405cd6e6d');

-- 配置项管理
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 10, '[{"key":"维修费用","value":"MaintenanceFee"}]', '成本类型', 'costType', '成本类型', 'f', 't', 8 , '[{"key":"维修费用","value":"MaintenanceFee"},{"key":"维保费","value":"WarrantyFee"},{"key":"房屋折旧费","value":"DepreciationHouse"},{"key":"人工费","value":"LaborCosts"},{"key":"药物耗材费","value":"MedicalSupplies"},{"key":"水电费","value":"Utilities"}]' where not exists (select * from config_item where key = 'costType');
--配置当前工作时长
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 10, '8', '当前工作时长', 'currentWorkingHours', '当前工作时长', 'f', 't', 2 where not exists (select * from config_item where key = 'currentWorkingHours');
--YHAAO-22699 云南阜外历史盘点工单增加资产分类
update inventory_order set asset_type = '01' where tenant_uid = '97ab517f3ead4f9c9b85dec27a006e6f' and is_finished is true and asset_type is null;

-- YHAAO-22847 配置项重复添加
delete from config_item_value where uid in (select uid from (SELECT *,ROW_NUMBER() OVER (PARTITION BY key,site_uid ) as row_num FROM config_item_value) a where a.row_num>1 );
