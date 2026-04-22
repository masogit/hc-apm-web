insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 10, 'false', '维修成本是否纳入工单费用', 'maintenanceCostIncludedWorkOrderCost', '维修成本是否纳入工单费用', 'f','t',3 where not exists(select * from config_item where key = 'maintenanceCostIncludedWorkOrderCost');




--YHAAO-27143
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否提醒使用备件', 'workOrderShowPartFlag', '维修工单是否提醒使用备件', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowPartFlag');


--YHAAO-27142
update config_item set value_type = 9, default_value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime';
update config_item_value set value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime' and value = 'false';
update config_item_value set value = '{"rendered":true,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime' and value = 'true';

update config_item set value_type = 9, default_value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime';
update config_item_value set value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime' and value = 'false';
update config_item_value set value = '{"rendered":true,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime' and value = 'true';


