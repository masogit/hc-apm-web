insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 5, '{"value": 1,"hours":0.1}', '保养工时设置', 'pmEntryTypeSet', '保养工时设置', 'f','t',9 where not exists(select * from config_item where key = 'pmEntryTypeSet');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 4, 0, '维修工时类型设置', 'repairEntryTypeSet', '维修工时类型设置', 'f', 't', 7,'[{"key":"工时段","value":"1"},{"key":"总工时","value":"PDF"}]' where not exists (select * from config_item where key = 'repairEntryTypeSet');

update v2_pm_order set cancel_time = end_time where current_step_name = '已取消';

--YHAAO-25896 报废申请人取用户名且需要更新历史数据
update asset_scrap scrap  set created_by=ua.name  from user_account ua  where scrap.request_user_uid=ua.uid;
update asset_scrap scrap  set approval_by=ua.name  from user_account ua  where scrap.approval_uid=ua.uid;
-- 质控日历优化 v2_pm_order添加asset_uid索引
CREATE INDEX if not exists v2_pm_order_asset_uid_idx ON public.v2_pm_order (asset_uid);

