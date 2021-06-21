 -- 2021-05-21 导入GEDictionaryExcel配置权限  start
 delete from role_msg_rel where msg_key = 'GEDictionaryExcel';
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('GEDictionaryExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end

-- 2021-06-04 消息订阅新增ole角色配置 start
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '1', true, true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '2', FALSE, '2', true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '2', FALSE, '3', true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '2', FALSE, '4', true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '5', true, true, true, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '2', FALSE, '6', true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '7', true, true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '8', true, true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '9', true, true, true, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '10', true, true, true, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '11', true, true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='OLE');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '2', FALSE, '12', true, true, false, 'OLE'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='OLE');
-- end

--2021/6/8 owoc, us update ib_status = 6
update asset_info set ib_status = 6 where uid in (select asset_uid from asset_tags where lower(tag) in ('us', 'owoc')) and ge_device_type in (1, 2, 21, 23, 24, 3) and ib_status in (1, 4);

--2021/6/8 delete us data in "iwic_monthly_report" and "di_quarterly_report"
delete from iwic_monthly_report where ge_device_type in (3, 31, 32, 33);
delete from di_quarterly_report where ge_device_type in (3, 31, 32, 33);



--apm-schedual新增模板導入 zkb代理商fe/sales     start 0610
delete from role_msg_rel where msg_key = 'ZKBDealerFEAndSalesExcel';
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ZKBDealerFEAndSalesExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);
--apm-schedual新增模板導入 zkb代理商fe/sales     end   0610


