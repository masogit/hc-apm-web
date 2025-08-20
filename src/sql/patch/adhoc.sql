insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 5, '{"value": 1,"hours":0.1}', '保养工时设置', 'pmEntryTypeSet', '保养工时设置', 'f','t',9 where not exists(select * from config_item where key = 'pmEntryTypeSet');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 4, 0, '维修工时类型设置', 'repairEntryTypeSet', '维修工时类型设置', 'f', 't', 7,'[{"key":"工时段","value":"1"},{"key":"总工时","value":"PDF"}]' where not exists (select * from config_item where key = 'repairEntryTypeSet');



