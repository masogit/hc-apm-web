-- https://ge-hc.atlassian.net/browse/YHAAO-20766
insert into i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) select 'label', 'EquipmentSwap', '设备调配中心', 'Equipment Swap', NULL, 808 where not exists(select * from i18n_message where msg_type = 'label' and site_id = 808 and msg_key = 'EquipmentSwap');
insert into i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) select 'label', 'BatchEquipmentSwap', '设备批量调配', 'Equipment Swap', NULL, 808 where not exists(select * from i18n_message where msg_type = 'label' and site_id = 808 and msg_key = 'BatchEquipmentSwap');
insert into i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) select 'label', 'SwappedEquipmentConfiguration', '调配设备配置', 'Equipment Swap', NULL, 808 where not exists(select * from i18n_message where msg_type = 'label' and site_id = 808 and msg_key = 'SwappedEquipmentConfiguration');
insert into i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) select 'label', 'SwappedEquipmentNameSettings', '调配名称设置', 'Equipment Swap', NULL, 808 where not exists(select * from i18n_message where msg_type = 'label' and site_id = 808 and msg_key = 'SwappedEquipmentNameSettings');
insert into i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) select 'label', 'AdjustmentEquipmentName', '调配设备名称', 'Equipment Swap', NULL, 808 where not exists(select * from i18n_message where msg_type = 'label' and site_id = 808 and msg_key = 'AdjustmentEquipmentName');

--https://ge-hc.atlassian.net/browse/YHAAO-20653 维修工单增加asset_uid字段刷历史数据
update v2_service_request sr set asset_uid =(select ai.uid from asset_info  ai where ai.id=sr.asset_id);
update v2_work_order wo set asset_uid =(select uid from asset_info ai where wo.asset_id =ai.id  );
--https://ge-hc.atlassian.net/browse/YHAAO-20655 资产表增加parent_asset_uid字段刷历史数据
update asset_info ai set parent_asset_uid =(select uid from asset_info ai2 where ai.parent_asset_id=ai2.id) where ai.parent_asset_id is not null;

--广元his数据上传，新增账号
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date)  select replace(uuid_generate_v4()::text,'-',''), '1dee1cec8d7f440dbf26a2d1f21a8580', '1dee1cec8d7f440dbf26a2d1f21a8580', '广元市中心医院本院', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, null where not exists(select 1 from system_client_info where bind_site_uid='1dee1cec8d7f440dbf26a2d1f21a8580');
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date) select replace(uuid_generate_v4()::text,'-',''), '86c1f747f1904e2190f240016412f179', '86c1f747f1904e2190f240016412f179', '广元市中心医院妇儿院区', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, null where not exists(select 1 from system_client_info where bind_site_uid='86c1f747f1904e2190f240016412f179')