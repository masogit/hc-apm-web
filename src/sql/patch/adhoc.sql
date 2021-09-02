-- YHAAO-12853 刷cms_psi_mapdata字段数据
update cms_psi_mapdata pm set modality=t.modality  from (select modality,map_data from cms_document_product group by modality,map_data) as t where pm.map_data=t.map_data;

-- YHAAO-12742
update asset_alert_config set check_type = 7,valid_values = 'Shutdown;关闭;Paused;暂停;Failed' where config_type = 204;

update asset_alert_config set check_type = 7,valid_values = 'Shutdown;关闭;Paused;暂停;Failed' where config_type = 504;

-- 刷新asset_service_member表，定时任务加的类型从DealerFEAndSalesExcel改成GEIBTableExcel
update asset_service_member set import_file_type = 'GEIBTableExcel' where import_file_type='DealerFEAndSalesExcel' and ge_user_type='9'
--   USUpgradeRecommendExcel
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','USUpgradeRecommendExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
