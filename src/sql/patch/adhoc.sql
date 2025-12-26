
--HAAO-26739配置项
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation)
select replace(uuid_generate_v4()::text,'-',''), 4, 'new', '维修工单详情查看方式', 'WorkOrderDetailViewType', '维修工单详情查看方式', 'f','t',7,'[{"key":"新网页","value":"new"},{"key":"当前网页","value":"current"}]' where not exists(select * from config_item where key = 'WorkOrderDetailViewType');
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation)
select replace(uuid_generate_v4()::text,'-',''), 5, 'new', '保养工单详情查看方式', 'PmOrderDetailViewType', '保养工单详情查看方式', 'f','t',7,'[{"key":"新网页","value":"new"},{"key":"当前网页","value":"current"}]' where not exists(select * from config_item where key = 'PmOrderDetailViewType');
