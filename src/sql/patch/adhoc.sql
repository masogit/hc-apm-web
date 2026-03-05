--YHAAO-27341 新增盘点是否显示维修责任人
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 12, false, '盘点是否显示维修责任人', 'InventoryDisplayRepairOwner', '盘点是否显示维修责任人', 'f', 't', 1 where not exists(select * from config_item where key = 'InventoryDisplayRepairOwner');
