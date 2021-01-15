delete from v2_blob_object where object_storage_id in (select storage_id from ib_manual_mapping) and bo_sub_type='101';

--更新智康保配置
update zkb_part_order_user_config set asset_type = 'US' where asset_type = 'CS';
