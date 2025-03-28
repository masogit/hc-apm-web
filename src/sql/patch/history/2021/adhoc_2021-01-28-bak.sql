delete from v2_blob_object where object_storage_id in (select storage_id from ib_manual_mapping) and bo_sub_type='101';

--更新智康保配置
update zkb_part_order_user_config set asset_type = 'US' where asset_type = 'CS';

 -- 2021-01-15  导入ZoneMgrExcel配置权限  start
 delete from role_msg_rel where msg_key = 'ZoneManagerInfoExcel';
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ZoneManagerInfoExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end

-- game plan添加索引
create index ix_msa_recommend_system_id on msa_recommend (system_id);
create index ix_promotion_activity_system_id on promotion_activity (system_id);
create index ix_care_package_system_id on care_package (system_id);
create index ix_training_quota_system_id on training_quota (system_id);
create index ix_coil_opportunity_system_id on coil_opportunity (system_id);


--2021-01-25 ignore sr report
INSERT INTO sr_name_transfer_mapping (file_name,mapped_name,sr_type, is_upload) VALUES ('Demo Checklist.pdf','球管失效报告.pdf',3, 'N'), ('Demo Checklist.pdf','球管失效报告.pdf',6, 'N');

update sr_name_transfer_mapping set is_upload = 'N' where id in (213, 214, 183, 184, 314, 315, 306, 307, 256, 257, 222, 223, 243, 244, 323, 324);

INSERT INTO zkb_fe_part_order_user_config (
    ID,
    config_type,
    email,
    login_name,
    NAME,
    phone,
    remark,
    sso,
    tenant_uid
) SELECT
    ID,
    'Supplier',
    email,
    login_name,
    '',
    telephone,
    '',
    '',
    tenant_uid
FROM
    user_account
WHERE
    tenant_uid IN (
        SELECT
            uid
        FROM
            tenant_info
        WHERE
            TYPE = 3
        AND uid != 'b0b889c38c6e4da48f91de9c8d50cc6b' and uid!='dc1fd473d1e14eb6b5e3644fc73b3690' and uid!='9d5d09bbc3184b73af45d07e26640fd4'
    );


-- 智康保归还数据
update zkb_part_order_return set modify_flag = 'Y' where data_satus = 'New';
update zkb_part_order_return set modify_flag = 'N' where data_satus = 'Old';
update zkb_part_order_return set modify_flag = 'N' where data_satus is null;

update zkb_part_order_return r set data_satus = 'Accept' where part_order_id in (select id from zkb_part_order where status ='Closed') and r.data_satus != 'Accept' and r.data_satus != 'Pending' and r.data_satus != 'Reject';
update zkb_part_order_return r set data_satus = 'Accept' where part_order_id in (select id from zkb_part_order where status ='Closed') and r.data_satus is null;
update zkb_part_order_return r set data_satus = 'Pending' where part_order_id in (select id from zkb_part_order where status !='Closed') and r.data_satus != 'Accept' and r.data_satus != 'Pending' and r.data_satus != 'Reject';
update zkb_part_order_return r set data_satus = 'Pending' where part_order_id in (select id from zkb_part_order where status !='Closed') and r.data_satus is null;