--2020-10-14 start
-- FE/SALSE 全量导入支持批次识别
alter table asset_service_member add column import_file_id varchar(255);
--2020-10-14 end