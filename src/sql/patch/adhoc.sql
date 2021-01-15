delete from v2_blob_object where object_storage_id in (select storage_id from ib_manual_mapping) and bo_sub_type='101';

--更新智康保配置
update zkb_part_order_user_config set asset_type = 'US' where asset_type = 'CS';

 -- 2021-01-15  导入ZoneMgrExcel配置权限  start
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ZoneManagerInfoExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end
