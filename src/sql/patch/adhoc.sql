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

-- 2021-06-09 给医工和医工主任 查看不良事件的权限 start
UPDATE sys_role SET authorities = ( CASE WHEN authorities IS NULL THEN 'repair.adverseEvents' WHEN authorities LIKE '%repair.adverseEvents%' THEN authorities ELSE authorities || ',repair.adverseEvents' END ), menus = ( CASE WHEN menus IS NULL THEN 'repair.adverseEvents' WHEN menus LIKE '%repair.adverseEvents%' THEN menus ELSE menus || ',repair.adverseEvents' END ), authorities_ch = ( CASE WHEN authorities_ch IS NULL THEN '不良事件' WHEN authorities_ch LIKE '%不良事件%' THEN authorities_ch ELSE authorities_ch || ',不良事件' END ) WHERE role_desc = '工程师' OR role_desc = '医学工程处主任';
-- end

-- 2021-06-17  新增多级审批模块,多级审批审核人角色 start
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'MultiLevelAuditor', '多级审批审核人',  'Multi-level approval reviewer'
where not exists (select 1 from sys_role where name='MultiLevelAuditor');
-- end

