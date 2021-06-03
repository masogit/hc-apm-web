 -- 2021-05-21 导入GEDictionaryExcel配置权限  start
 delete from role_msg_rel where msg_key = 'GEDictionaryExcel';
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('GEDictionaryExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end