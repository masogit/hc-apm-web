-- YHAAO-22568 mvsIbMapping
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date)  select replace(uuid_generate_v4()::text,'-',''), '68fc4f19938546d7bdfaf2d405cd6e6d', '68fc4f19938546d7bdfaf2d405cd6e6d', '智汇E家', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, null where not exists(select 1 from system_client_info where bind_site_uid='68fc4f19938546d7bdfaf2d405cd6e6d');


update  his_asset_group set type='Income' where type is null;
-- 配置项管理
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 10, '[{"key":"维修费用","value":"MaintenanceFee"}]', '成本类型', 'costType', '成本类型', 'f', 't', 8 , '[{"key":"维修费用","value":"MaintenanceFee"},{"key":"维保费","value":"WarrantyFee"},{"key":"房屋折旧费","value":"DepreciationHouse"},{"key":"人工费","value":"LaborCosts"},{"key":"药物耗材费","value":"MedicalSupplies"},{"key":"水电费","value":"Utilities"}]' where not exists (select * from config_item where key = 'costType');