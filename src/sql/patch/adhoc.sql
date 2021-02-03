--2021-01-27 消息订阅 修改部分角色配置 start
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='6' and role_position='SalesManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='1' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='7' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='8' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='11' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='12' and role_position='FE';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='1' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='7' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='8' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='11' and role_position='FeManager';
update user_subscription_push set sub_status=true,default_send_status=false,send_status=true where menu_type='12' and role_position='FeManager';
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '1', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=1 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '2', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=2 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '3', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=3 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '4', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=4 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '5', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=5 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '6', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=6 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '7', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=7 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '8', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=8 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '9', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=9 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '10', false, true, true, true, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=10 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '11', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=11 and tag='Super');
INSERT INTO user_subscription_push (menu_type,is_deleted,send_status,sub_status,default_send_status,tag)
select '12', false, true, true, false, 'Super'
where not exists (select 1 from user_subscription_push where menu_type=12 and tag='Super');
--2020-01-27 end
