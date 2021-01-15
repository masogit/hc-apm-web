 -- 2021-01-15  导入ZoneMgrExcel配置权限  start
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ZoneManagerInfoExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end