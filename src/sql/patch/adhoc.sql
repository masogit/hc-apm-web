-- YHAAO-12853 刷cms_psi_mapdata字段数据
update cms_psi_mapdata pm set modality=t.modality  from (select modality,map_data from cms_document_product group by modality,map_data) as t where pm.map_data=t.map_data;

-- YHAAO-12742
update asset_alert_config set check_type = 7,valid_values = 'Shutdown;关闭;Paused;暂停;Failed' where config_type = 204;
