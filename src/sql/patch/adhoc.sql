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


--2021/5/28 owoc, us update ib_status = 6
update asset_info set ib_status = 6 where uid in (select asset_uid from asset_tags where lower(tag) in ('us', 'owoc')) and ge_device_type in (1, 2, 21, 23, 24, 3) and ib_status in (1, 4);

--2021/5/31 删除错误手册数据，和错误频道
delete from cms_channel where name='XF' or name='XR';
delete from cms_content where tags like '%name":"XF"%' or tags like '%name":"XR"%';
delete from cms_document_product where modality='XF' or modality='XR';

