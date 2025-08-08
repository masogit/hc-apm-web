
-- YHAAO-25583 更新历史数据院区
update asset_department_convert adc set out_site_uid=o2.uid ,out_site_name =o2.name,in_site_uid = o4.uid, in_site_name= o4.name from asset_department_convert a1 left join org_info o1 on a1.out_dept_uid = o1.uid left join org_info o2 on o1.site_uid = o2.uid left join  org_info o3 on o3.uid = a1.in_dept_uid left join org_info o4 on o3.site_uid = o4.uid where a1.id = adc.id and  ((adc.out_site_uid is null or adc.out_site_uid = '') or (adc.out_site_name is null or adc.out_site_name ='' ) or (adc.in_site_uid is null or adc.in_site_uid = '') or (adc.in_site_name is null or adc.in_site_name = ''));
-- YHAAO-25580 多级审批-设备维修：备件审批状态取最新工单的审批状态
update v2_work_order_detail vod  set approved=null where approved=false and exists(select 1 from v2_work_order wo  where wo.id=vod.wo_id and (wo.approval_status='1' or wo.approval_status='5') );
-- YHAAO-25659 是否显示盘点方式
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 12, 'false', '是否显示盘点方式', 'enableInventoryMethod', '是否显示盘点方式', 'f','t',3 where not exists(select * from config_item where key = 'enableInventoryMethod');
-- 批量更新历史数据
update inventory_order_detail set inventory_method = 'scanCode' where status = 1;
update inventory_issue set inventory_method = 'scanCode';
--宝石花医院his  添加systemclien信息
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time,
                                last_access_date)
VALUES (REPLACE(uuid_generate_v4()::text, '-', ''), '7778d7b1e4bd4137bfc638e19794a2f8',
        '1dee1cec8d7f440dbf26a2d1f21a8580', '四川宝石花',
        '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, clock_timestamp());

-- 删除到货清单外键约束
ALTER TABLE arrival_list DROP CONSTRAINT "fklkb6140aruelepc2hfb1w39km";
ALTER TABLE arrival_list DROP CONSTRAINT "fk_hxgxok12yhhye3h9rkkmlsnak";




---工程师扫码默认打开页面设置
insert into public.config_item (uid, category,default_value,  description, key, name, super_admin_edit, user_parent_config, value_type,validation)
select replace(uuid_generate_v4()::text,'-',''), 1, 'asset', '工程师扫码默认打开页面设置', 'setEngineerScansToPage', '工程师扫码默认打开页面', 'f', 't', 7,'[{"key":"事件记录","value":"record"},{"key":"设备台账","value":"asset"},{"key":"报修页面","value":"repair"}]' where not exists (select * from config_item where key = 'setEngineerScansToPage');
