delete from v2_blob_object where object_storage_id in (select storage_id from ib_manual_mapping) and bo_sub_type='101';

--更新智康保配置
update zkb_part_order_user_config set asset_type = 'US' where asset_type = 'CS';

 -- 2021-01-15  导入ZoneMgrExcel配置权限  start
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ZoneManagerInfoExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end

-- game plan添加索引
create index ix_msa_recommend_system_id on msa_recommend (system_id);
create index ix_promotion_activity_system_id on promotion_activity (system_id);
create index ix_care_package_system_id on care_package (system_id);
create index ix_training_quota_system_id on training_quota (system_id);
create index ix_coil_opportunity_system_id on coil_opportunity (system_id);
