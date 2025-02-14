
-- his绩效设备配置修改字段长度
ALTER TABLE efficiency_equipment_info ALTER COLUMN report_config TYPE TEXT;

-- YHAAO-23014  新增资产出入库管理员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'DepartmentConvertManager', '转科管理员',  'Department Convert Manager'
    where not exists (select 1 from sys_role where name='DepartmentConvertManager');

-- YHAAO-23861
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 8, 'false', '使用登记关机和终末消毒是否同时完成', 'disinfectedAfterShutdown', '使用登记关机和终末消毒是否同时完成', 'f','t',3 where not exists(select * from config_item where key = 'disinfectedAfterShutdown');



---zkb归还数据增加item_id
update zkb_part_order_return r set item_id = (select id from zkb_part_order_item where part_order_id = r.part_order_id and part_no = r.part_no limit 1) where item_id is null and part_order_id in (select id from zkb_part_order where current_step in ('ReturnPending', 'ClosePending'));


--清理zkb fe测试脏数据
update zkb_part_order set current_step = 'Complete', status = 'Closed' where tenant_uid = '5ef0ed1fff3a431782f21ac634bd8d8e' and current_step != 'Complete';

update zkb_part_order_cat set business_type = 'SpecialPart' where cat_type = 'specialPart' and business_type is null;
update zkb_part_order_cat set business_type = 'Probe' where cat_type != 'specialPart' and business_type is null;