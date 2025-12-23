-- YHAAO-26359 维修和保养时增加路途工时字段
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '是否显示路途工时', 'workOrderShowTravelTime', '是否显示路途工时', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowTravelTime');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否显示路途工时', 'pmOrderShowTravelTime', '是否显示路途工时', 'f','t',3 where not exists(select * from config_item where key = 'pmOrderShowTravelTime');
-- YHAAO-26551  手机端+web端：我的保养工单待接单页面，不允许进行批量接单
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否关闭批量接单', 'disableBatchAcceptOrder', '是否关闭批量接单', 'f','t',3 where not exists(select * from config_item where key = 'disableBatchAcceptOrder');

-- YHAAO-26581  漳州市医院--流程配置项派工模式逻辑增加逻辑
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '自动派工是否允许责任人抢单', 'autoDispatchAllowClaimByAssignee', '自动派工是否允许责任人抢单', 'f','t',3 where not exists(select * from config_item where key = 'autoDispatchAllowClaimByAssignee');

-- YHAAO-26046 云版贴码,转到本地版
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, '', 'apm本地版,但在云版盘点并贴码,二维码解析转发到对应本地版', 'cloudQrCodeToLocalApm', '云版二维码转发到本地版', 'f','t',1 where not exists(select * from config_item where key = 'cloudQrCodeToLocalApm');

-- asset_lifecycle_event 删除外键  asset_info  id
ALTER TABLE asset_lifecycle_event DROP CONSTRAINT IF EXISTS fk_avxmtljopeqkojo8r88kjdhvn;


--培训签到人
UPDATE train_detail
SET actual_sign_total = tds.sign_count
    FROM (
    SELECT detail_id, COUNT(*) as sign_count
    FROM train_detail_sign
    GROUP BY detail_id
) tds
WHERE train_detail.id = tds.detail_id
  AND train_detail.actual_sign_total IS NULL;