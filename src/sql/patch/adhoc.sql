--2021/05/21 start 修改自订阅消息中代理商FE、代理商Salses配置
update user_subscription_push set sub_status=true,send_status=true,default_send_status=true where role_position in('DealerFe','DealerSales') and menu_type in(5,6);
--2021/05/21 end

-- 删除开关机登记的外键约束
ALTER TABLE asset_opt_record DROP CONSTRAINT "fk3goow0twmmkvcf8rem5juby3j";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkhmd105dn8hl6gywkb5wffemxw";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkfxunn74gmcytw84gwilo20apk";


---初始化GE FE流程列表 start
update zkb_fe_part_order set function_type= (select function_type from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set machine_status= (select machine_status from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set urgency_status= (select urgency_status from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set subtract_total_status= (select subtract_total_status from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set hospital_name= (select hospital_name from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set hospital_client_name= (select hospital_client_name from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set hospital_client_phone= (select hospital_client_phone from zkb_fe_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'FE%';
update zkb_fe_part_order set function_type= (select product_number from zkb_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
update zkb_fe_part_order set hospital_name= (select hospital_name from zkb_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
update zkb_fe_part_order set hospital_client_name= (select hospital_repairer from zkb_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
update zkb_fe_part_order set hospital_client_phone= (select hospital_repairer_phone from zkb_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
update zkb_fe_part_order set urgency_status= (select urgent_order from zkb_part_order  u where u.part_order_no=zkb_fe_part_order.father_part_order_no) where order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
UPDATE zkb_fe_part_order set urgency_status='1' where urgency_status='t' and order_type in ('SpareParts','Probe') and father_part_order_no like 'PO%';
---初始化GE FE流程列表 end

--2021/5/31 删除错误手册数据，和错误频道
delete from cms_channel where name='XF' or name='XR';
delete from cms_content where tags like '%name":"XF"%' or tags like '%name":"XR"%';
delete from cms_document_product where modality='XF' or modality='XR';

-- 2021/6/2 更新asset_dept_id=0的情况  start
update asset_info set asset_dept_id = (select id from org_info where uid = asset_dept_uid )
WHERE asset_dept_id = 0 AND asset_dept_uid is not null
-- 2021/6/2 更新asset_dept_id=0的情况  end

update asset_lifecycle_event set description = CONCAT('{"title":"', event_uid,'","messages":["状态:关单"],"status":"Closed"}'), status = 2 where event_uid in ('1-8028069329', '1-7706967725');
update asset_lifecycle_event set description = CONCAT('{"title":"', event_uid,'","messages":["状态:取消"],"status":"Cancelled"}'), status = 3 where event_uid in ('1-8207235425', '1-9013277409');

-- 2021/6/2 YHAAO-12224 邵逸夫维修工单时间变更
update v2_service_request set
created_date = created_date - interval '100 day',
last_modified_date = last_modified_date - interval '100 day',
request_time = request_time - interval '100 day',
estimated_close_time = estimated_close_time - interval '100 day',
close_time = close_time - interval '100 day'
where sr_num in ('2105266054','2105266053','2105266052','2105266051','2105266048','2105266047','2105266046');

update v2_work_order set
created_date = created_date - interval '100 day',
last_modified_date = last_modified_date - interval '100 day',
accept_step_start_time = accept_step_start_time - interval '100 day',
close_step_end_time = close_step_end_time - interval '100 day',
evaluate_step_end_time = evaluate_step_end_time - interval '100 day',
send_step_start_time = send_step_start_time - interval '100 day',
work_step_end_time = work_step_end_time - interval '100 day',
close_time = close_time - interval '100 day'
where exists(select * from v2_service_request sr where sr.id = sr_id
and sr.sr_num in ('2105266054','2105266053','2105266052','2105266051','2105266048','2105266047','2105266046'));

update v2_work_order_step set
created_date = created_date - interval '100 day',
last_modified_date = last_modified_date - interval '100 day',
start_time = start_time - interval '100 day',
end_time = end_time - interval '100 day'
where exists (select * from v2_work_order wo where wo.id = wo_id
and exists (select * from v2_service_request sr where sr.id = wo.sr_id and sr.sr_num in ('2105266054','2105266053','2105266052','2105266051','2105266048','2105266047','2105266046')));
