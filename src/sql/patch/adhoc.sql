
-- YHAAO-25583 更新历史数据院区
update asset_department_convert adc set out_site_uid=o2.uid ,out_site_name =o2.name,in_site_uid = o4.uid, in_site_name= o4.name from asset_department_convert a1 left join org_info o1 on a1.out_dept_uid = o1.uid left join org_info o2 on o1.site_uid = o2.uid left join  org_info o3 on o3.uid = a1.in_dept_uid left join org_info o4 on o3.site_uid = o4.uid where a1.id = adc.id and  ((adc.out_site_uid is null or adc.out_site_uid = '') or (adc.out_site_name is null or adc.out_site_name ='' ) or (adc.in_site_uid is null or adc.in_site_uid = '') or (adc.in_site_name is null or adc.in_site_name = ''));
-- YHAAO-25580 多级审批-设备维修：备件审批状态取最新工单的审批状态
update v2_work_order_detail vod  set approved=null where approved=false and exists(select 1 from multi_level_approval mla where mla.business_uid=vod.wo_id and mla.approval_status=0 or mla.approval_status=1 )
-- YHAAO-25659 是否显示盘点方式
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '是否显示盘点方式', 'enableInventoryMethod', '是否显示盘点方式', 'f','t',3 where not exists(select * from config_item where key = 'enableInventoryMethod');

